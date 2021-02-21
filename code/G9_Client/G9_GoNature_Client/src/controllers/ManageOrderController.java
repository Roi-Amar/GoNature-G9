package controllers;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import orderData.Order;

/**
 * controller handle all the manage orders options
 * @author Rinat Stoudenets
 *
 */

public class ManageOrderController implements Initializable {
	@FXML
	private Label lblPrice;
	@FXML
	private Label lblParkName;

	@FXML
	private Label lblDate;
	@FXML
	private Label lblOrderNum;
	@FXML
	private Label lblTime;
	@FXML
	private Label lblEditVisitors;
	@FXML
	private Label lblDiscount;

	@FXML
	private Label lblPayment;

	@FXML
	private Label lblYay;
	@FXML
	private Label lblTotal;
	@FXML
	private Label lblVisitors;
	@FXML
	private Label lblMail;
	@FXML
	private Hyperlink lnkSwitch;
	@FXML
	private Button btnBack;
	@FXML
	private Button btnUpdate;
	@FXML
	private Button btnCancel;
	@FXML
	private Button btnNoconfirme;
	@FXML
	private Label lblEdit;
	@FXML
	private Button btnconfirme;
	@FXML
	private JFXTextField txtVisitorsNumber;

	@FXML
	private JFXDatePicker txtdate;

	@FXML
	private JFXComboBox<String> cbxArriveTime;



	private AlertController alert = new AlertController();
	private static Order order = null;
	private static boolean updated = false;
	private static boolean canceled = false;
	private static int caseApproval = 0;

	public static int getCaseApproval() {
		return caseApproval;
	}

	public static void setCaseApproval(int caseApproval) {
		ManageOrderController.caseApproval = caseApproval;
	}

	public static Order getOrder() {
		return order;
	}

	public static void setOrder(Order order) {
		ManageOrderController.order = order;
	}

	/**
	 * received order object from server and present on screen: order details
	 * @param details Order
	 */
	
	void presentOrderdetails(Order details) {

		String DateAndTime = details.getArrivedTime();
		String[] splitDateAndTime = DateAndTime.split(" ");

		String date = splitDateAndTime[0];

		DateFormat iFormatter = new SimpleDateFormat("yyyy-MM-dd");

		DateFormat oFormatter = new SimpleDateFormat("dd-MM-yyyy");
		try {
			String strDateTime = oFormatter.format(iFormatter.parse(date));

			String time = (String) splitDateAndTime[1].subSequence(0, 5);

			lblOrderNum.setText(String.valueOf(details.getOrderNumber()));
			lblParkName.setText(details.getParkName());
			lblDate.setText(strDateTime);
			lblTime.setText(time);
			lblVisitors.setText(String.valueOf(details.getVisitorsNumber()));
			lblMail.setText(details.getOrderEmail());
			lblPrice.setText(details.getPrice() + "₪");
			this.lblTotal.setText(String.valueOf(details.getTotalPrice()) + "₪");
			double discValue = (1 - (details.getTotalPrice() / details.getPrice())) * 100;
			this.lblDiscount.setText(String.format("%.1f", discValue) + "%");
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	/**
	 *  presenting the details , initializing the fields with order details
	 */
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		setOrder(WelcomeController.getOrderDetails());
		
		
//		ArrayList<Object> msgPending = new ArrayList<>();
//		msgPending.add("checkIfPending");
//		msgPending.add(order.getOrderNumber());
//		ClientUI.sentToChatClient(msgPending);
	
		
		presentOrderdetails(order);
		if (!WelcomeController.getisIspending()) {
			updateDetailsFromOrder();

		} else {
			btnconfirme.setVisible(true);
			btnNoconfirme.setVisible(true);
			btnCancel.setVisible(false);
			cbxArriveTime.setVisible(false);
			lblEditVisitors.setVisible(false);
			lblEdit.setVisible(false);
		}
	}
 
	/**
	 * the function sets default dada in the  appropriate fields for edit as the data received from the DB
	 */
	
	public void updateDetailsFromOrder() {
		String DateAndTime = order.getArrivedTime();
		String[] splitDateAndTime = DateAndTime.split(" ");
		String [] timeString =splitDateAndTime[1].split(":");
		cbxArriveTime.setItems(FXCollections.observableArrayList("8:00-12:00", "12:00-16:00", "16:00-20:00"));
		txtVisitorsNumber.setText(String.valueOf(order.getVisitorsNumber()));
		switch(timeString[0])
		{
		case  "08" :
			cbxArriveTime.getSelectionModel().select(0);
			break;
		case "12" :
			cbxArriveTime.getSelectionModel().select(1);
			break;
		case "16" :
			cbxArriveTime.getSelectionModel().select(2);
			break;
		}
		txtdate.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				LocalDate nextYear = LocalDate.of(today.getYear() + 1, today.getMonth(), today.getDayOfMonth());
				setDisable(empty || (date.compareTo(nextYear) > 0 || date.compareTo(today) < 0));
			}
		});
		txtdate.setValue(LOCAL_DATE(splitDateAndTime[0]));
	}

	/**
	 * button back, return to previous screen 
	 * @param event ActionEvent
	 * @throws IOException
	 */
	
	@FXML
	void back(ActionEvent event) throws IOException {
		Stage stage = (Stage) btnBack.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("/gui/Welcome.fxml"));
		stage.setScene(new Scene(root));
	}

	/**
	 * send to server, Array list of objects, [0] string for server, editOrder , [1] updated order object, 
	 * [2] old order object
	 * @param event ActionEvent
	 * @throws IOException
	 */
	
	@FXML
	void update(ActionEvent event) throws IOException {
		Order sentOrder = new Order(order); 
		if (checkCurrentTime()) {
			String[] timeString = cbxArriveTime.getValue().toString().split("-");
			String clientDateTime = (txtdate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 0"
					+ timeString[0]) + ":00"; 									
			String orderDateTime = sentOrder.getArrivedTime();
			int clientVisitorsNumber = Integer.parseInt(txtVisitorsNumber.getText());
			int orderVisitorsNumber = sentOrder.getVisitorsNumber();
			if ((!clientDateTime.equals(orderDateTime)) || (clientVisitorsNumber != orderVisitorsNumber||!checkCurrentTime())) {
				sentOrder.setArrivedTime(clientDateTime);
				sentOrder.setVisitorsNumber(clientVisitorsNumber);
				ArrayList<Object> msgForServer = new ArrayList<>();
				msgForServer.add("editOrder");
				msgForServer.add(sentOrder);
				msgForServer.add(order);
				ClientUI.sentToChatClient(msgForServer);
				if (updated) {
					alert.successAlert("Update information", "Updated succesful");
					Stage stage = (Stage) btnBack.getScene().getWindow();
					Parent root = FXMLLoader.load(getClass().getResource("/gui/Welcome.fxml"));
					stage.setScene(new Scene(root));
				} else
					// alertFailed
					alert.setAlert("Updated failed");
			} else
				// notify user
				alert.setAlert("no chages made! Please try again");
		}
		updated = false;
	}

	public static void updatedOrderFromServer(Object received) {
		updated = (boolean) received;
	}

	@FXML
	void cancelOrder(ActionEvent event) throws IOException {
		ArrayList<Object> msgForServer = new ArrayList<>();
		msgForServer.add("cancelOrder");
		msgForServer.add(order);
		ClientUI.sentToChatClient(msgForServer);
		if (canceled) {

			alert.successAlert("Canceled succesful", "Canceled succesful");
			Stage stage = (Stage) btnBack.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("/gui/Welcome.fxml"));
			stage.setScene(new Scene(root));
		} else
			alert.setAlert("Failed to cancel Order");
	}

	/**
	 * Do not allow to book a trip for a time that already passed today
	 * @return T/F
	 */
	
	public boolean checkCurrentTime() {
		LocalDate date = txtdate.getValue();
		String[] arrSplit = cbxArriveTime.getValue().toString().split("-");
		String[] hour = arrSplit[0].split(":");
		LocalTime arrivalTime = LocalTime.of(Integer.parseInt(hour[0]), 00, 00);

		LocalTime now = LocalTime.now();
		if (date.compareTo(LocalDate.now()) == 0 && now.compareTo(arrivalTime) >= 0) {
			alert.setAlert("You're trying to book for a time that has already passed. Please select a future time\r\n");

			return false;
		}
		return true;
	}
	
	/**
	 * returned from server value true if cancelled , false if not cancelled  and an error ocured
	 * @param returned T/F
	 */

	public static void canceledOrderFromServer(boolean returned) {
		canceled = returned;
		order = null;
	}

	/**
	 * if obj contains only one cell the user pressed no present successful cancel message,
	 *  if true the user
	 * approved present edit window ,
	 * if false  something went wrong int caseApproval : 0 pressed no ,1 pressed yes, 2 false something wrong 
	 * 
	 * @param returned ArrayList of Object
	 */
	
	public static void receviedFromserverArrivalConfirmation(ArrayList<Object> returned) {

		if (returned.size() == 1)
			setCaseApproval(0);

		else {
			if ((boolean) returned.get(1))
				setCaseApproval(1);
			else
				setCaseApproval(2);
		}

	}

	
	/*
	 * NICE TO HAVE!!!!!!
	 */
	@FXML
	void switchToMember(ActionEvent event) throws IOException {
//		Stage stage = (Stage)lnkSwitch.getScene().getWindow();
//		Parent root = FXMLLoader.load(getClass().getResource("/gui/EditMemberOrder.fxml"));
//		stage.setScene(new Scene(root));
	}
	
	/**
	 * Upon receiving a message that there is free spot in the park or message to approve arrival on the next day
	 * Confirm or cancel order 
	 * @param event ActionEvent
	 * @throws IOException
	 */

	@FXML
	void confirmeArrival(ActionEvent event) throws IOException {
		ArrayList<Object> msg = new ArrayList<>();
		msg.add("waitlistReplay");
		if (event.getSource() == btnconfirme)
			msg.add("yes");
		else if (event.getSource() == btnNoconfirme)
			msg.add("no");
		msg.add(order);
		ClientUI.sentToChatClient(msg);

		switch (caseApproval) {
		case 0: {
			alert.successAlert("Cancelation", "Thank you ! Canceled successful !");
			Stage stage = (Stage) btnBack.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("/gui/Welcome.fxml"));
			stage.setScene(new Scene(root));
			break;
		}
		case 1: {

			alert.successAlert("Approval", "Thank you for your approval!");
			btnconfirme.setVisible(false);
			btnNoconfirme.setVisible(false);
			btnCancel.setVisible(true);
			cbxArriveTime.setVisible(true);
			lblEditVisitors.setVisible(true);
			lblEdit.setVisible(true);
			
			updateDetailsFromOrder();
			break;
		}
		case 2: {
			alert.setAlert("Something went wrong..");
			Stage stage1 = (Stage) btnBack.getScene().getWindow();
			Parent root1 = FXMLLoader.load(getClass().getResource("/gui/Welcome.fxml"));
			stage1.setScene(new Scene(root1));
			break;
		}
		default:
			break;
		}

	}
	
	/**
	 * convert from string to local date 
	 * @param dateString String, date as a string
	 * @return the local date value of the string received
	 */
	
	public static LocalDate LOCAL_DATE (String dateString){
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    LocalDate localDate = LocalDate.parse(dateString, formatter);
	    return localDate;
	}
}
