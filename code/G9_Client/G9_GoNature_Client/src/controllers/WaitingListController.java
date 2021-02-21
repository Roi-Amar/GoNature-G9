package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 * controller handles the options available to the user when entering the
 * waiting list
 *
 * @author Bar Katz
 */

public class WaitingListController implements Initializable {

	@FXML
	private Pane pnVerify;

	@FXML
	private Button btnContinue;

	@FXML
	private Button btnHere;

	@FXML
	private JFXDatePicker txtdate;

	@FXML
	private JFXComboBox<String> cbxArrivelTime;
	private static AlertController alert = new AlertController();
	private static ArrayList<String> Dates = new ArrayList<>();
	private static ArrayList<String> time = new ArrayList<>();
	private ArrayList<String> nonReleventDates = new ArrayList<>();
	private static ArrayList<Object> anotherDates = new ArrayList<>();
	private static int setDateFromWaitList = 0;

	public static int getSetDateFromWaitList() {
		return setDateFromWaitList;
	}

	public static void setSetDateFromWaitList(int setDateFromWaitList) {
		WaitingListController.setDateFromWaitList = setDateFromWaitList;
	}

	public static ArrayList<Object> getAnotherDates() {
		return anotherDates;
	}

	public static void setAnotherDates(ArrayList<Object> anotherDates) {
		WaitingListController.anotherDates = anotherDates;
	}

	public static ArrayList<String> getTime() {

		return Dates;
	}

	public static void setDates(ArrayList<String> date) {
		WaitingListController.Dates = date;
	}

	/**
	 * set dates into date picker
	 * 
	 * @param arr ArrayList of String nonReleventDates
	 */

	public void nonReleventDatesForCalender(ArrayList<String> arr) {

		final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
			@Override
			public DateCell call(final DatePicker datePicker) {
				return new DateCell() {
					@Override
					public void updateItem(LocalDate item, boolean empty) {
						for (String str : arr) {
							super.updateItem(item, empty);
							String[] arrDate = getDate(str, "Date");
							LocalDate thisDate = LocalDate.of(Integer.parseInt(arrDate[0]),
									Integer.parseInt(arrDate[1]), Integer.parseInt(arrDate[2]));
							LocalDate today = LocalDate.now();
							LocalDate nextYear = LocalDate.of(today.getYear() + 1, today.getMonth(),
									today.getDayOfMonth());

							if (item.compareTo(thisDate) == 0) {

								setDisable(true);
							} else if (item.compareTo(nextYear) > 0) {

								setDisable(true);
							} else if (item.compareTo(today) < 0) {

								setDisable(true);
							}
						}
					}
				};
			}
		};

		txtdate.setDayCellFactory(dayCellFactory);

	}

	/**
	 * for split the strings
	 * 
	 * @param date String
	 * @param what to know what string the user need
	 * @return String[]
	 */

	public String[] getDate(String date, String what) {
		String[] arrDateAndTime = date.split(" ");
		String[] arrDate = arrDateAndTime[0].split("-");
		if (what.equals("Date"))
			return arrDate;
		else
			return arrDateAndTime;
	}

	/**
	 * set list nonReleventDates that include dates that full from all capsules
	 */

	public void setArrForDatePicker() {
		ArrayList<String> newArr = new ArrayList<>();
		int count = 0, rounds = 0, flag = 0;
		String[] first = null;
		for (String str : Dates) {
			String[] TimesDates = getDate(str, "all");
			if (count == 0) {
				first = TimesDates;
				count++;
			} else {
				if (TimesDates[0].equals(first[0])) {
					count++;
				} else {
					first = TimesDates;
					count = 1;
					rounds = 1;
					flag = 1;
				}
			}
			if (flag != 1)
				rounds++;
			if (rounds == 3) {
				if (count == 3) {
					newArr.add(first[0]);
				}
				rounds = 0;
				count = 0;
			}
			flag = 0;
		}
		this.nonReleventDates = newArr;

	}

	/**
	 * set list of all available dates that are not filled in all capsules
	 */

	public void setArrForTime() {

		int length = Dates.size();
		int[] places = new int[length];
		for (int i = 0; i < places.length; i++) {
			places[i] = -1;
		}
		for (String str : nonReleventDates) {
			for (int i = 0; i < length; i++) {
				String[] TimesDates = getDate(Dates.get(i), "all");
				if (TimesDates[0].equals(str)) {
					places[i] = i;
				}
			}
		}
		for (int i = length - 1; i >= 0; i--) {
			if (places[i] != -1)
				Dates.remove(i);
		}
	}

	/**
	 * set the cbxArrivelTime array list of time for specific date that the client
	 * choose
	 * 
	 * @param date string
	 */

	public void setTimeWithSpecificDate(String date) {
		ArrayList<String> timeTmp = new ArrayList<>(time);

		for (String str : Dates) {
			String[] TimesDates = getDate(str, "all");
			if (TimesDates[0].equals(date)) {
				for (String start : time) {
					String[] arr = start.split("-");
					String[] specificArrTime = arr[0].split(":");
					String[] specificArrDate = TimesDates[1].split(":");
					if (specificArrDate[0].equals(specificArrTime[0])) {
						timeTmp.remove(start);
					}
				}
			}
		}

		cbxArrivelTime.setDisable(false);
		cbxArrivelTime.setItems(FXCollections.observableArrayList(timeTmp));
	}

	/**
	 * when press the date picker
	 * 
	 * @param ActionEvent event
	 */

	@FXML
	void chooseDate(ActionEvent event) {
		try {
			String date = txtdate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			if (!date.equals(null)) {
				setTimeWithSpecificDate(date);
			}
		} catch (NullPointerException e) {
			System.out.println("didnt choose date");
		}
	}

	/**
	 * button continue to choose another date
	 * 
	 * @param ActionEvent event
	 */

	@FXML
	void Continue(ActionEvent event) {
		if(checkToolate())
		{
			alert.setAlert("Invalid date");
			txtdate.setValue(LocalDate.now());		
		}
		else {
		WaitingListController.setSetDateFromWaitList(1);
		anotherDates.add(txtdate.getValue());
		anotherDates.add(cbxArrivelTime.getValue());
		OrderController ORC = Context.getInstance().getOrderC();
		ORC.getPnOrder().setDisable(false);
		ORC.initialize(ORC.getLocation(), ORC.getResources());
		Stage stage2 = (Stage) btnContinue.getScene().getWindow();
		stage2.close();
		}
	}

	/**
	 * button here to enter the wait list
	 * 
	 * @param ActionEvent event
	 * @throws IOException
	 */

	@FXML
	void here(ActionEvent event) throws IOException {
		WaitingListController.setSetDateFromWaitList(0);
		Stage stage = new Stage();
		Pane root = FXMLLoader.load(getClass().getResource("/gui/WaitingListConfirmation.fxml"));
		Scene scene = new Scene(root);
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {

				OrderController ORC = Context.getInstance().getOrderC();
				ORC.getPnOrder().setDisable(false);

			}
		});

		Stage stage2 = (Stage) btnHere.getScene().getWindow();
		stage2.close();
	}

	public static void getListDatesServer(ArrayList<String> arr) {
		setDates(arr);
	}

	// rinat : added for jubula testing
	private boolean checkToolate ()
	{
		LocalDate date = txtdate.getValue();
		
		LocalDate today = LocalDate.now();
		LocalDate nextYear = LocalDate.of(today.getYear() + 1, today.getMonth(), today.getDayOfMonth());
		if(date.compareTo(nextYear)>0)
			return true;
		return false;
	}

	
	/**
	 * initialize the screen
	 */

	@SuppressWarnings("static-access")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.time.clear();
		this.time.add("08:00-12:00");
		this.time.add("12:00-16:00");
		this.time.add("16:00-20:00");
		cbxArrivelTime.setDisable(true);

		/************ get from server the date and time that is full ***********/
		ArrayList<Object> sendServer = new ArrayList<>();
		sendServer.add("checkFullDays");
		sendServer.add(OrderController.getOrder());
		ClientUI.sentToChatClient(sendServer);

		setArrForDatePicker();
		if (nonReleventDates.isEmpty()) {
			/*********** set the date picker *************/
			txtdate.setDayCellFactory(picker -> new DateCell() {
				public void updateItem(LocalDate date, boolean empty) {
					super.updateItem(date, empty);
					LocalDate today = LocalDate.now();
					LocalDate nextYear = LocalDate.of(today.getYear() + 1, today.getMonth(), today.getDayOfMonth());
					setDisable(empty || (date.compareTo(nextYear) > 0 || date.compareTo(today) < 0));
				}
			});
		} else {
			nonReleventDatesForCalender(nonReleventDates);
		}
		setArrForTime();

	}

}
