package controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import client.ClientUI;
import dataLayer.Park;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DateCell;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import managerData.ManagerRequest;

/**
 * controller for all of the park manager : it is responsible for setting new
 * discounds and capacity of the parks. Viewing and creatong reports
 * 
 * @author Rinat Stoudenets
 *
 */

public class ParkManagerController implements Initializable {
	@FXML
	private Button btnDashboard;

	@FXML
	private Button btnVisits;

	@FXML
	private Button btnUsage;

	@FXML
	private Button btnMonthlyRevenue;

	@FXML
	private Button btnSettings;

	@FXML
	private Label lblTitle;

	@FXML
	private Button btnLogout;

	@FXML
	private Label lblFirstNameTitle;

	@FXML
	private Label lblParkName;

	@FXML
	private Pane pnSettings;

	@FXML
	private Pane pnMonthlyRev;

	@FXML
	private Label lblTitle1112;

	@FXML
	private Label lblTitle11111;

	@FXML
	private Button btnReportView11;

	@FXML
	private Pane pnUsage;

	@FXML
	private Label lblTitle111;

	@FXML
	private Label lblTitle1111;

	@FXML
	private Button btnReportView1;

	@FXML
	private Pane pnVisits;

	@FXML
	private Label lblTitle11;

	@FXML
	private Label lblTitle112;

	@FXML
	private TableView<?> tblHoursPerTypes;

	@FXML
	private Button btnReportView;

	@FXML
	private Pane pnVisits1;

	@FXML
	private JFXTextField lblSetMax;

	@FXML
	private JFXTextField txtManageDsic;

	@FXML
	private Button btnSubmitVisits;
	@FXML
	private Pane pnDashboard;
	@FXML
	private Label lblTitle1;

	@FXML
	private Button btnSubmitDisc;

	@FXML
	private JFXDatePicker txtDateFrom;

	@FXML
	private JFXDatePicker txtDateTo;
	@FXML
	private AnchorPane lblCurrentVisitorsInthePark;
	@FXML
	private Button btnSetDisc;

	@FXML
	private Button btnSetVisitors;
	@FXML
	private Button btnSetMaxByOrder;
	@FXML
	private Label lblPresentDisc;
	@FXML
	private Label lblPresentMaxVis;

	@FXML
	private Label lblPresentReservationCap;

	@FXML
	private JFXTextField txtMaxcapByorder;

	@FXML
	private Button btnSubmitCapacityByorder;

	@FXML
	private Button btnSetDisc1;
	@FXML
	private Label lblVisitorsnumber;

	@FXML
	private Label lblCurrentVisitors;

	// ------for visitors chart-----
	@FXML
	private BarChart<String, Double> bcVisitorsChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	@FXML
	private JFXDatePicker dpFrom;
	@FXML
	private JFXDatePicker dpTo;
	@FXML
	private Button btnExport;
	@FXML
	private Button btnShow;
	private static ArrayList<ArrayList<String>> visitorsReport = new ArrayList<>();
	private double[] sumByweekDays = new double[8];

	// ----for usage chart-----

	@FXML
	private BarChart<String, Double> bcUsageChart;

	@FXML
	private CategoryAxis xAxisU;

	@FXML
	private NumberAxis yAxisU;
	@FXML
	private JFXDatePicker dpFromU;

	@FXML
	private JFXDatePicker dpToU;

	@FXML
	private Button showUsage;
	private static ArrayList<ArrayList<String>> usageReport = new ArrayList<>();

	/*-----------for Revenue chart----------*/

	@FXML
	private BarChart<String, Double> bcRevenue;

	@FXML
	private CategoryAxis xAxisR;

	@FXML
	private NumberAxis yAxisR;
	@FXML
	private JFXComboBox<String> cbxMonth;

	@FXML
	private JFXComboBox<String> cbxYear;
	@FXML
	private Button btnShowReuvenue;

	private static ArrayList<ArrayList<String>> revReport = new ArrayList<>();

	private LocalDate fromDate;

	private LocalDate toDate;
//	@FXML
//	private JFXComboBox<String> cbxMounth;

	private static ManagerRequest Req = new ManagerRequest(0, "", 0, 0, "", "", "", "");
	private static String firstName;
	private static String parkName;
	private static String currentVisitors;

	private static AlertController alert = new AlertController();
	private static boolean requestAnswerFromServer;
	private static int empID = 0;
	private static Park park;
	private static boolean errorInchart = false;

	private static void seterrorInchart(boolean b) {

		errorInchart = b;
	}

	public static boolean getErrorInchart() {
		return errorInchart;
	}

	public static ArrayList<ArrayList<String>> getUsageReport() {
		return usageReport;
	}

	public static void setUsageReport(ArrayList<ArrayList<String>> usageReport) {
		ParkManagerController.usageReport = usageReport;
	}

	public static String getCurrentVisitors() {
		return currentVisitors;
	}

	public static void setCurrentVsitors(String curr) {
		ParkManagerController.currentVisitors = curr;
	}

	public static ArrayList<ArrayList<String>> getVisitorsReport() {
		return visitorsReport;
	}

	public static void setVisitorsReport(ArrayList<ArrayList<String>> visitorsReport) {
		ParkManagerController.visitorsReport = visitorsReport;
	}

	public static Park getPark() {
		return park;
	}

	public static void setPark(Park park) {
		ParkManagerController.park = park;
	}

	public static int getEmpID() {
		return empID;
	}

	public static void setEmpID(int empID) {
		ParkManagerController.empID = empID;
	}

	public static boolean getRequestAnswerFromServer() {
		return requestAnswerFromServer;
	}

	public static void setRequestAnswerFromServer(boolean discountAnswerFromServer) {
		ParkManagerController.requestAnswerFromServer = discountAnswerFromServer;
	}

	public static String getParkName() {
		return parkName;
	}

	public static void setParkName(String parkName) {
		ParkManagerController.parkName = parkName;
	}

	public static ArrayList<ArrayList<String>> getRevReport() {
		return revReport;
	}

	public static void setRevReport(ArrayList<ArrayList<String>> revReport) {
		ParkManagerController.revReport = revReport;
	}

	public static String getFirstName() {
		return firstName;
	}

	public static void setFirstName(String firstName) {
		ParkManagerController.firstName = firstName;
	}

	/**
	 * handles switching tabs in park manager controller
	 * 
	 * @param event ActionEvent
	 */

	@FXML
	void handleSideBarParkManager(ActionEvent event) {
		if (event.getSource() == btnDashboard) {
			lblTitle.setText("Dashboard");
			pnDashboard.toFront();
			setButtonPressed(btnDashboard);
			setButtonReleased(btnVisits, btnUsage, btnMonthlyRevenue);
		} else if (event.getSource() == btnVisits) {
			lblTitle.setText("Visits Report");
			pnVisits.toFront();
			setButtonPressed(btnVisits);
			setButtonReleased(btnDashboard, btnUsage, btnMonthlyRevenue);
			setDatePickerInitialValues();
		} else if (event.getSource() == btnUsage) {
			lblTitle.setText("Usage Report");
			pnUsage.toFront();
			setButtonPressed(btnUsage);
			setButtonReleased(btnDashboard, btnVisits, btnMonthlyRevenue);
		} else if (event.getSource() == btnMonthlyRevenue) {
			lblTitle.setText("Revenue report");
			pnMonthlyRev.toFront();
			setButtonPressed(btnMonthlyRevenue);
			setButtonReleased(btnVisits, btnDashboard, btnUsage);

		}
	}

	/**
	 * sets the style for pressed tabs
	 * 
	 * @param button Button
	 */

	public void setButtonPressed(Button button) {
		button.setStyle("-fx-background-color: transparent;" + "-fx-border-color: brown;"
				+ "-fx-border-width: 0px 0px 0px 3px;");
	}

	/**
	 * sets the style for not pressed tabs
	 * 
	 * @param button  Button
	 * @param button1 Button
	 * @param button2 Button
	 */
	public void setButtonReleased(Button button, Button button1, Button button2) {
		button.setStyle("-fx-background-color: transparent;");
		button1.setStyle("-fx-background-color: transparent;");
		button2.setStyle("-fx-background-color: transparent;");
	}

	/**
	 * 
	 * update current visitors number in the park.The number is updated every time
	 * when visitor enters or exits the park
	 * 
	 * @param visitNum String
	 */
	public void setUpdatedCurrentVisitors(String visitNum) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				lblCurrentVisitors.setText(visitNum);
			}
		});
	}

	/**
	 * Main initialize function for the screen. Initialize all the fields, labels
	 * and datepickers
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Context.getInstance().setPMC(this);

		setParkName(LoginController.getParkName());
		RequestForParkDetails();
		setFirstName(LoginController.getFirstName());
		lblFirstNameTitle.setText(getFirstName());
		lblParkName.setText(getParkName());
		lblCurrentVisitors.setText(String.valueOf(park.getCurrentAmount()));

		setDatePickerInitialValues();
		RequestForEmployeeID();
		presentParkDetails(park);

		/*** visitors reports ***/
		setDatePickerForVisitsReport();

		/*** usage reports ***/
		setDatePickerForUsageReport();

		/***** Revenue report *********/

		ArrayList<String> monthArr = new ArrayList<>();

		setDatesComboForRevenue(monthArr);

	}
	// *---------setting date pickers in the conroller --------------*

	/**
	 * set default values in combo box for elevant months and years . NOTICE :
	 * revenue report will be saved only forlast 3 years !
	 * 
	 * @param mounthArr ArrayList of String
	 */
	public void setDatesComboForRevenue(ArrayList<String> mounthArr) {
		for (int i = 1; i <= 12; i++) {
			mounthArr.add(String.valueOf(i));
		}
		cbxMonth.setItems(FXCollections.observableArrayList(mounthArr));
		cbxMonth.getSelectionModel().selectFirst();
		ArrayList<String> yearArr = new ArrayList<>();
		for (int i = 2021; i >= 2018; i--) {
			yearArr.add(String.valueOf(i));
		}
		cbxYear.setItems(FXCollections.observableArrayList(yearArr));
		cbxYear.getSelectionModel().selectFirst();
	}

	/**
	 * set default values for usage report. Default values will be the 1st of this
	 * month till 1st of next month
	 */
	public void setDatePickerForUsageReport() {
		dpFromU.setValue(LocalDate.now().withDayOfMonth(1));
		// listener for updating the date
		dpFromU.valueProperty().addListener((ov, oldValue, newValue) -> {
			dpFromU.setValue(newValue);
		});

		// plusMonths(1) to get the next month
		// withDayOfMonth(1) to get the first day
		dpToU.setValue(LocalDate.now());
		// listener for updating the date
		dpToU.valueProperty().addListener((ov, oldValue, newValue) -> {
			dpToU.setValue(newValue);
		});
		
		setDatePickerDisableFutureDate(dpFromU);
		setDatePickerDisableFutureDate(dpToU);
	}

	/**
	 * set default values for visitors report. Default values will be the 1st of
	 * this month till 1st of next month
	 */
	public void setDatePickerForVisitsReport() {
		dpFrom.setValue(LocalDate.now().withDayOfMonth(1));
		// listener for updating the date
		dpFrom.valueProperty().addListener((ov, oldValue, newValue) -> {
			dpFrom.setValue(newValue);
		});
		// plusMonths(1) to get the next month
		// withDayOfMonth(1) to get the first day
		dpTo.setValue(LocalDate.now());
		// listener for updating the date
		dpTo.valueProperty().addListener((ov, oldValue, newValue) -> {
			dpTo.setValue(newValue);
		});
		
		setDatePickerDisableFutureDate(dpFrom);
		setDatePickerDisableFutureDate(dpTo);
		
	}
	
	/**
	 * the futur date will be disable
	 * @param dp JFXDatePicker
	 */
	public void setDatePickerDisableFutureDate(JFXDatePicker dp) {
		dp.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty ||date.compareTo(today) >0);
			}
		});
	}

	/**
	 * datepicker initialize method. date that already passed will be disabled. as
	 * well as dated after one year from now
	 */
	public void setDatePickerInitialValues() {
		txtDateFrom.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				LocalDate nextYear = LocalDate.of(today.getYear() + 1, today.getMonth(), today.getDayOfMonth());
				setDisable(empty || (date.compareTo(nextYear) > 0 || date.compareTo(today) < 0));
			}
		});

		txtDateFrom.setValue(LocalDate.now());
		txtDateTo.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				LocalDate nextYear = LocalDate.of(today.getYear() + 1, today.getMonth(), today.getDayOfMonth());
				setDisable(empty || (date.compareTo(nextYear) > 0 || date.compareTo(today) < 0));
			}
		});

		txtDateTo.setValue(LocalDate.now());

	}

	/**
	 * /by clicking on logout buttonn the user will be logged out of the system and
	 * the infor will be saved in the DB . send to server data to update that the
	 * user was log out : arraylist of object [0] updateLoggedIn .[1] arraylist of
	 * string [0] user name ,[1] 0
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	void logout(ActionEvent event) throws IOException {
		// Data fields
		ArrayList<String> data = new ArrayList<String>();
		// Query
		ArrayList<Object> msg = new ArrayList<Object>();

		msg.add("updateLoggedIn");
		// update as loggedin as logged out
		data.add(LoginController.getUsername());
		data.add(String.valueOf(0));
		// Data fields
		msg.add(data);
		// set up all the order details and the payment method
		ClientUI.sentToChatClient(msg);

		Stage stage = (Stage) btnLogout.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
		stage.setScene(new Scene(root));
	}

	/**
	 * by clicking on set discount button - make the relevant text field and date
	 * pickers to be visible and the rest no if there is other request editing field
	 * vsisible - set them be not visible
	 * 
	 * @param event
	 */
	@FXML
	void setDiscount(ActionEvent event) {
		if (!btnSetMaxByOrder.isVisible()) {
			btnSetMaxByOrder.setVisible(true);
			txtMaxcapByorder.setVisible(false);
			btnSubmitCapacityByorder.setVisible(false);
		}
		if (!btnSetVisitors.isVisible()) {
			lblSetMax.setVisible(false);
			btnSubmitVisits.setVisible(false);
			btnSetVisitors.setVisible(true);
		}
		btnSetDisc.setVisible(false);
		txtManageDsic.setVisible(true);
		txtDateFrom.setVisible(true);
		txtDateTo.setVisible(true);
		btnSubmitDisc.setVisible(true);

		txtManageDsic.textProperty().addListener((obs, oldValue, newValue) -> {

			// \\d -> only digits
			// * -> escaped special characters
			if (!newValue.matches("\\d")) {
				// ^\\d -> everything that not a digit
				txtManageDsic.setText(newValue.replaceAll("[^\\d]", ""));

			}
		});
	}

	/**
	 * by clicking on set visitors capacity button - make the relevant text field
	 * and button to be visible and the rest no. If there is other request editing
	 * field vsisible - set them be not visible
	 * 
	 * @param event
	 */
	@FXML
	void setVisitorsCapacityByorder(ActionEvent event) {
		if (!btnSetDisc.isVisible()) {
			btnSetDisc.setVisible(true);
			txtManageDsic.setVisible(false);
			txtDateFrom.setVisible(false);
			txtDateTo.setVisible(false);
			btnSubmitDisc.setVisible(false);
		}
		if (!btnSetVisitors.isVisible()) {
			lblSetMax.setVisible(false);
			btnSubmitVisits.setVisible(false);
			btnSetVisitors.setVisible(true);
		}
		btnSetMaxByOrder.setVisible(false);
		txtMaxcapByorder.setVisible(true);
		btnSubmitCapacityByorder.setVisible(true);

		txtMaxcapByorder.textProperty().addListener((obs, oldValue, newValue) -> {

			// \\d -> only digits
			// * -> escaped special characters
			if (!newValue.matches("\\d")) {
				// ^\\d -> everything that not a digit
				txtMaxcapByorder.setText(newValue.replaceAll("[^\\d]", ""));

			}
		});
	}

	/**
	 * by clicking on set visitors button : make the relevant text field and date
	 * pickers to be visible and the rest no if there is other request editing field
	 * vsisible : set them be not visible
	 * 
	 * @param event
	 */
	@FXML
	void setMaxCapacity(ActionEvent event) {

		if (!btnSetDisc.isVisible()) {
			btnSetDisc.setVisible(true);
			txtManageDsic.setVisible(false);
			txtDateFrom.setVisible(false);
			txtDateTo.setVisible(false);
			btnSubmitDisc.setVisible(false);
		}
		if (!btnSetMaxByOrder.isVisible()) {
			btnSetMaxByOrder.setVisible(true);
			txtMaxcapByorder.setVisible(false);
			btnSubmitCapacityByorder.setVisible(false);
		}
		btnSetVisitors.setVisible(false);
		lblSetMax.setVisible(true);
		btnSubmitVisits.setVisible(true);

		lblSetMax.textProperty().addListener((obs, oldValue, newValue) -> {

			// \\d -> only digits
			// * -> escaped special characters
			if (!newValue.matches("\\d")) {
				// ^\\d -> everything that not a digit
				lblSetMax.setText(newValue.replaceAll("[^\\d]", ""));

			}
		});

	}

	/**
	 * send to server request : arraylist of object : [0] parkManagerRequest
	 * [1]Request object with the relevant fields for the specific request in this
	 * request : send the discount hight and the dates it will be valid in. All the
	 * rest fields that are not relevant will be empty
	 * 
	 * @param event
	 * @throws ParseException
	 */

	@FXML
	void submitPendingDiscount(ActionEvent event) throws ParseException {

		String discount = txtManageDsic.getText();

		if (!illegalValuesForSubmitingDiscount(discount))

		{
			int integerDisc = Integer.parseInt(discount);
			double discountInPrecents = integerDisc / 100.0;
			if (integerDisc > 100)
				alert.setAlert(
						"You are trying to set illegal discount!\nDiscount value should be in a range of 0%-100%");
			else {
				ArrayList<Object> msg = new ArrayList<>();
				msg.add("parkManagerRequest");
				Req.setDiscount(String.valueOf(1 - discountInPrecents));
				Req.setFromDate(txtDateFrom.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				Req.setToDate(txtDateTo.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				Req.setParkName(getParkName());
				Req.setEmployeeID(getEmpID());
				Req.setRequestType("discount");
				msg.add(Req);
				ClientUI.sentToChatClient(msg);

				if (requestAnswerFromServer) {
					alert.successAlert("Request Info",
							"Your request was sent and pending for deapartment manager approval.");
					btnSetDisc.setVisible(true);
					txtManageDsic.setVisible(false);
					txtDateFrom.setVisible(false);
					txtDateTo.setVisible(false);
					btnSubmitDisc.setVisible(false);

				} else {
					alert.setAlert(
							"You already reached maximum number of requests.\nIt is possible to have only one request of a type in a time.\nContact your department manager or try again later.");
					btnSetDisc.setVisible(true);
					txtManageDsic.setVisible(false);
					txtDateFrom.setVisible(false);
					txtDateTo.setVisible(false);
					btnSubmitDisc.setVisible(false);
				}

			}

		}
		Req.setDiscount("");
		Req.setFromDate("");
		Req.setToDate("");
		Req.setParkName("");
		Req.setEmployeeID(getEmpID());
		Req.setRequestType("");
		txtManageDsic.clear();
		setDatePickerInitialValues();
	}

	/**
	 * check the fields before submitting a discount. Check if the discount is not
	 * empty and that the date are correct
	 * 
	 * @param discount String
	 * @return T/F
	 * @throws ParseException ArrayList of String
	 */
	public boolean illegalValuesForSubmitingDiscount(String discount) throws ParseException {
		String datefrom = txtDateFrom.getValue().toString();
		String dateto = txtDateTo.getValue().toString();
		if (DatesNotCorresponding(datefrom, dateto)) {
			alert.setAlert("You are trying to set incorrect dates!\nPlease try again");
			return true;
		}

		if (discount.isEmpty()) {
			alert.setAlert("Cannot leave this field empty! \nPlease insert Valid discount.");
			return true;
		}
		return false;
	}

	/**
	 * send to server request : ArrayList of object : [0] parkManagerRequest
	 * [1]Request object with the relevant fields for the specific request. In this
	 * request : send the new value for maximum capacity for ordered visits. All
	 * the rest fields that are not relevant will be empty
	 * 
	 * @param event
	 */

	@FXML
	void submitVisitorsCapacityByorder(ActionEvent event) {
		String capacity = txtMaxcapByorder.getText();
		if (capacity.isEmpty())
			alert.setAlert("Cannot leave this field empty! \nPlease insert Valid capacity.");
		// check that capacity for orders is less than overall capacity
		if (Integer.parseInt(capacity) >= park.getMaximumCapacityInPark())
			alert.setAlert(
					"You are trying to determine illegal capacity !\nCapacity for ordered visits should be less than maximum capacity in park ");
		else {
			ArrayList<Object> msg = new ArrayList<>();
			msg.add("parkManagerRequest");
			Req.setParkName(getParkName());
			Req.setEmployeeID(getEmpID());
			Req.setRequestType("max_o");
			Req.setOrdersCapacity(Integer.parseInt(txtMaxcapByorder.getText()));
			msg.add(Req);
			ClientUI.sentToChatClient(msg);

			if (requestAnswerFromServer) {
				alert.successAlert("Request Info",
						"Your request was sent and pending for deapartment manager approval.");
				btnSetMaxByOrder.setVisible(true);
				txtMaxcapByorder.setVisible(false);
				btnSubmitCapacityByorder.setVisible(false);
			} else {
				alert.setAlert(
						"You already reached maximum number of requests.\nIt is possible to have only one request of a type in a time.\nContact your department manager or try again later.");
				btnSetMaxByOrder.setVisible(true);
				txtMaxcapByorder.setVisible(false);
				btnSubmitCapacityByorder.setVisible(false);
			}

		}
		Req.setDiscount("");
		Req.setFromDate("");
		Req.setToDate("");
		Req.setParkName("");
		Req.setEmployeeID(getEmpID());
		Req.setRequestType("");
		txtMaxcapByorder.clear();

	}

	/**
	 * send to server request : arraylist of object : [0] parkManagerRequest
	 * [1]Request object with the relevant fields for the specific request. In this
	 * request : ssend the new value for maximum capacity. All the rest fields that
	 * are not relevant will be empty
	 * 
	 * @param event
	 * 
	 */
	@FXML
	void submitVisitorsCapacity(ActionEvent event) {
		String orderCapacity = lblSetMax.getText();
		if (orderCapacity.isEmpty())
			alert.setAlert("Cannot leave this field empty! \nPlease insert Valid capacity.");
		else {
			ArrayList<Object> msg = new ArrayList<>();
			msg.add("parkManagerRequest");
			Req.setParkName(getParkName());
			Req.setEmployeeID(getEmpID());
			Req.setRequestType("max_c");
			Req.setMaxCapacity(Integer.parseInt(lblSetMax.getText()));
			msg.add(Req);
			ClientUI.sentToChatClient(msg);

			if (requestAnswerFromServer) {
				alert.successAlert("Request Info",
						"Your request was sent and pending for deapartment manager approval.");
				btnSetVisitors.setVisible(true);
				lblSetMax.setVisible(false);
				btnSubmitVisits.setVisible(false);
			} else {
				alert.setAlert(
						"You already reached maximum number of requests.\nIt is possible to have only one request of a type in a time.\nContact your department manager or try again later.");
				btnSetVisitors.setVisible(true);
				lblSetMax.setVisible(false);
				btnSubmitVisits.setVisible(false);
			}

		}
		Req.setDiscount("");
		Req.setFromDate("");
		Req.setToDate("");
		Req.setParkName("");
		Req.setEmployeeID(getEmpID());
		Req.setRequestType("");
		lblSetMax.clear();
	}

	/**
	 * present the current values of discount, maximum capacity and maximum allowed
	 * capacity by orders
	 * 
	 * @param parkDetails
	 */
	void presentParkDetails(Park parkDetails) {
		double disc = (1 - parkDetails.getMangerDiscount()) * 100;
		lblPresentDisc.setText(String.format("%.1f", disc) + "%");
		lblPresentMaxVis.setText(String.valueOf(parkDetails.getMaximumCapacityInPark()));
		lblPresentReservationCap.setText(String.valueOf(parkDetails.getMaxAmountOrders()));
	}

	/**
	 * 
	 * send to server in order to get employee id : arraylist of object : [0]
	 * requestForEmployeeID, [1]user name, [2] password
	 */
	public void RequestForEmployeeID() {

		ArrayList<Object> msg = new ArrayList<>();
		msg.add("requestForEmployeeID");
		msg.add(LoginController.getUsername());
		msg.add(LoginController.getPassword());
		ClientUI.sentToChatClient(msg);
	}

	/**
	 * 
	 * send to server in order to get parkDetails : arraylist of object : [0]
	 * requestForEmployeeID, [1]arraylist of string : [0] requestForParkDetails,
	 * [1]parkname
	 */
	public void RequestForParkDetails() {
		ArrayList<Object> msg = new ArrayList<>();
		ArrayList<String> data = new ArrayList<>();
		msg.add("requestForParkDetails");
		data.add(getParkName());
		msg.add(data);
		ClientUI.sentToChatClient(msg);
	}

	/**
	 * check if the date that is set to be the date "to" is not before ths date
	 * "from". return true if the dates are not corresponding , false if
	 * corresponding.
	 * 
	 * @param datefrom String
	 * @param dateto   String
	 * @return T/F
	 * @throws ParseException Signals that an error has been reached unexpectedly
	 *                        while parsing
	 */
	public boolean DatesNotCorresponding(String datefrom, String dateto) throws ParseException {
		LocalDate from;
		LocalDate to;

		String[] fromarrsplitStrings = datefrom.split(" ");
		String[] fromdatesplit = fromarrsplitStrings[0].split("-");
		String[] toarrsplitStrings = dateto.split(" ");
		String[] todatesplit = toarrsplitStrings[0].split("-");
		// [0]->year , [1]->month , [2]->day
		from = LocalDate.of(Integer.parseInt(fromdatesplit[0]), Integer.parseInt(fromdatesplit[1]),
				Integer.parseInt(fromdatesplit[2]));

		to = LocalDate.of(Integer.parseInt(todatesplit[0]), Integer.parseInt(todatesplit[1]),
				Integer.parseInt(todatesplit[2]));
		if (to.compareTo(from) < 0)
			return true;

		return false;
	}

	/** Reports **/

	/** visitors chart **/

	private static boolean error = false;

	public static boolean getError() {
		return error;
	}

	@FXML
	void export(ActionEvent event) throws ParseException {
		showChart(event);

		if (getErrorInchart()) {
			return;
		}

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String fromFormat = dateFormatter.format(dpFrom.getValue());
		String toFormat = dateFormatter.format(dpTo.getValue());
		// the date it was created
		String fileNameDate = fileNameFormatter.format(LocalDate.now());

		Font titleFont = FontFactory.getFont(FontFactory.COURIER, 18, Font.BOLD, new BaseColor(46, 139, 87));
		try {
			Document document = new Document();
			// creates a report with the name ==> CanceledReport 'yyyy-MM-dd'.pdf
			// the 'yyyy-MM-dd' is the date it was created
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream("Visits Report " + fileNameDate + ".pdf"));
			document.open();

			Paragraph title = new Paragraph(
					"Visitor numbers including segmented by types of visitors/" + getParkName() + "\n", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			Paragraph date = new Paragraph(new Date().toString() + "\n\n");
			date.setAlignment(Element.ALIGN_CENTER);
			document.add(date);

			PdfPTable table = new PdfPTable(3);
			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell titleCell = new PdfPCell(new Paragraph(fromFormat + " - " + toFormat));
			titleCell.setColspan(3);
			titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			titleCell.setBackgroundColor(BaseColor.GRAY);
			// the title of the table
			table.addCell(titleCell);

			PdfPCell visitor = new PdfPCell(new Paragraph("Visitor type"));
			visitor.setColspan(1);
			visitor.setHorizontalAlignment(Element.ALIGN_CENTER);
			visitor.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(visitor);

			PdfPCell Day = new PdfPCell(new Paragraph("Day in the week"));
			Day.setColspan(1);
			Day.setHorizontalAlignment(Element.ALIGN_CENTER);
			Day.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(Day);

			PdfPCell amount = new PdfPCell(new Paragraph("Amount"));
			amount.setColspan(1);
			amount.setHorizontalAlignment(Element.ALIGN_CENTER);
			amount.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(amount);

			for (int i = 0; i < visitorsReport.size(); i++) {
				table.addCell(visitorsReport.get(i).get(0));
				table.addCell(visitorsReport.get(i).get(1));
				table.addCell(visitorsReport.get(i).get(2));
			}
			document.add(table);

			alert.successAlert("Success", "The report was created successfully.");

			Desktop.getDesktop().open(new File("Visits Report " + fileNameDate + ".pdf"));

			document.close();
			writer.close();

		} catch (Exception e) {
			alert.failedAlert("Failed", "It looks like the file is already open, close it and try again.");
		}
	}

	/**
	 * create chart for visitors by membership kind : 1) member 2)regular not a
	 * member, single visitor 3)group with a guide
	 * 
	 * @throws ParseException Signals that an error has been reached
	 *                        unexpectedly while parsing
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void chartVisitors() throws ParseException {

		xAxis = new CategoryAxis();
		yAxis = new NumberAxis(0, 20, 2);
		bcVisitorsChart.getData().clear();
		bcVisitorsChart.setAnimated(false);
		bcVisitorsChart.setBarGap(1.0d);
		bcVisitorsChart.setCategoryGap(10.0);

		bcVisitorsChart.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		bcVisitorsChart.setPrefSize(613, 430);
		bcVisitorsChart.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

		bcVisitorsChart.setTitle("Visitors segmentation by type");
		xAxis.setCategories(FXCollections.<String>observableArrayList(
				Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")));

		Series<String, Double> regular = new Series<>();
		Series<String, Double> member = new Series<>();
		Series<String, Double> group = new Series<>();

		regular.setName("regular");
		checkWeekDays("regular");

		regular.getData().add(new XYChart.Data("Sunday", sumByweekDays[1]));
		regular.getData().add(new XYChart.Data("Monday", sumByweekDays[2]));
		regular.getData().add(new XYChart.Data("Tuesday", sumByweekDays[3]));
		regular.getData().add(new XYChart.Data("Wednesday", sumByweekDays[4]));
		regular.getData().add(new XYChart.Data("Thursday", sumByweekDays[5]));
		regular.getData().add(new XYChart.Data("Friday", sumByweekDays[6]));
		regular.getData().add(new XYChart.Data("Saturday", sumByweekDays[7]));

		member.setName("member");
		checkWeekDays("member");

		member.getData().add(new XYChart.Data("Sunday", sumByweekDays[1]));
		member.getData().add(new XYChart.Data("Monday", sumByweekDays[2]));
		member.getData().add(new XYChart.Data("Tuesday", sumByweekDays[3]));
		member.getData().add(new XYChart.Data("Wednesday", sumByweekDays[4]));
		member.getData().add(new XYChart.Data("Thursday", sumByweekDays[5]));
		member.getData().add(new XYChart.Data("Friday", sumByweekDays[6]));
		member.getData().add(new XYChart.Data("Saturday", sumByweekDays[7]));

		group.setName("group");
		checkWeekDays("group");

		group.getData().add(new XYChart.Data("Sunday", sumByweekDays[1]));
		group.getData().add(new XYChart.Data("Monday", sumByweekDays[2]));
		group.getData().add(new XYChart.Data("Tuesday", sumByweekDays[3]));
		group.getData().add(new XYChart.Data("Wednesday", sumByweekDays[4]));
		group.getData().add(new XYChart.Data("Thursday", sumByweekDays[5]));
		group.getData().add(new XYChart.Data("Friday", sumByweekDays[6]));
		group.getData().add(new XYChart.Data("Saturday", sumByweekDays[7]));

		bcVisitorsChart.getData().addAll(regular, member, group);
	}

	/**
	 * by clicking the show chart : check that current dates are corresponding call
	 * the send to server function
	 * 
	 * @param event
	 * @throws ParseException
	 */
	@FXML
	void showChart(ActionEvent event) throws ParseException {

		bcVisitorsChart.getData().clear();

		ArrayList<String> data = new ArrayList<>();
		if (DatesNotCorresponding(dpFrom.getValue().toString(), dpTo.getValue().toString()))
			alert.setAlert("You are trying to set incorrect dates!\nPlease try again");
		else {

			data.add(getParkName());
			data.add(dpFrom.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			data.add(dpTo.getValue().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			sendToServerArrayListForOverralVistiReports(data);

			if (!getErrorInchart())
				chartVisitors();
			else {			
				noDataTopresentInchartForDates();
				bcVisitorsChart.getData().clear();
				setDatePickerForVisitsReport();
			}
		}
	}

	/**
	 * Send to server array list of object [0] overallVisitorsReport, [1] data array
	 * list
	 * 
	 * @param data:aray list of string containing : [0] park name ,[1] date from,
	 *                  [2] date to
	 */

	public void sendToServerArrayListForOverralVistiReports(ArrayList<String> data) {
		ArrayList<Object> msg = new ArrayList<>();
		msg.add("overallVisitorsReport");
		msg.add(data);

		ClientUI.sentToChatClient(msg);
	}

	/**
	 * go over the report recevied from server acording to the appropriate day in
	 * the week add the amount of visitors to the total sum
	 * 
	 * @param type :membership type
	 * @throws ParseException
	 */
	void checkWeekDays(String type) throws ParseException {
		String someDate;
		Date date1;
		resetWeekDayCounter();
		for (ArrayList<String> arrayList : visitorsReport) {
			someDate = getDate(arrayList.get(2));
			date1 = new SimpleDateFormat("yyyy-MM-dd").parse(someDate);
			if (type.equals(arrayList.get(0))) {
				switch (getDayNumber(date1)) {
				// SUN
				case 1:
					sumByweekDays[1] += Double.parseDouble(arrayList.get(1));
					break;
				// MON
				case 2:
					sumByweekDays[2] += Double.parseDouble(arrayList.get(1));
					break;
				// TUE
				case 3:
					sumByweekDays[3] += Double.parseDouble(arrayList.get(1));
					break;
				// WED
				case 4:
					sumByweekDays[4] += Double.parseDouble(arrayList.get(1));
					break;
				// THU
				case 5:
					sumByweekDays[5] += Double.parseDouble(arrayList.get(1));
					break;
				// FRI
				case 6:
					sumByweekDays[6] += Double.parseDouble(arrayList.get(1));
					break;
				// SAT
				case 7:
					sumByweekDays[7] += Double.parseDouble(arrayList.get(1));
					break;
				default:
					break;
				}
			}
		}

	}

	public static int getDayNumber(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * reset the array
	 */

	public void resetWeekDayCounter() {

		for (int i = 0; i < this.sumByweekDays.length; i++) {
			sumByweekDays[i] = 0;
		}
	}

	/*-------end of visitors report section --------*/
	/**
	 * prints alert when there is no data in the data base for the dates entered
	 */
	static void noDataTopresentInchartForDates() {
		alert.setAlert("There is no data to present for selected dates.");
		System.out.println("no data to present");
	}

//get the date and split it 
	/**
	 * 
	 * @param date: string value of date
	 * @return String
	 */
	@SuppressWarnings("unused")
	public String getDate(String date) {
		String[] arrDateAndTime = date.split(" ");
		String[] arrDate = arrDateAndTime[0].split("-");
		return arrDateAndTime[0];
	}

	// usage report section *****
	/**
	 * send to server array list of object :[0]UsageReport, [1] array list of string
	 * [0] park name ,[1]from date .[2] to date.
	 * 
	 * @param event ActionEvent
	 * @throws ParseException Signals that an error has been reached
	 *                        unexpectedlywhile parsing
	 */

	@FXML
	void showUsage(ActionEvent event) throws ParseException {
		ArrayList<Object> msg = new ArrayList<>();
		ArrayList<String> data = new ArrayList<>();
		if (DatesNotCorresponding(dpFromU.getValue().toString(), dpToU.getValue().toString()))
			alert.setAlert("You are trying to set incorrect dates!\nPlease try again");
		else {

			msg.add("UsageReport");
			data.add(getParkName());
			data.add(dpFromU.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			data.add(dpToU.getValue().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			msg.add(data);
			ClientUI.sentToChatClient(msg);

			if (!getErrorInchart())
				chartUsage();
			else {
				noDataTopresentInchartForDates();
				bcUsageChart.getData().clear();
				setDatePickerForUsageReport();
			}
		}

	}

	/**
	 * create usage report
	 * 
	 * @throws ParseException Signals that an error has been reached unexpectedlywhile parsing
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void chartUsage() throws ParseException {
		boolean count = false;
		xAxisU = new CategoryAxis();
		yAxisU = new NumberAxis(0, 20, 2);
		bcUsageChart.getData().clear();
		bcUsageChart.setAnimated(false);
		bcUsageChart.setBarGap(1d);
		bcUsageChart.setCategoryGap(8.0);

		bcUsageChart.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		bcUsageChart.setPrefSize(613, 430);
		bcUsageChart.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

		Series<String, Double> visit8_12 = new Series<>();
		Series<String, Double> visit12_16 = new Series<>();
		Series<String, Double> viit16_20 = new Series<>();
		String someDate;
		ArrayList<String> firstArrDate;
		String firstDate;
		LocalDate checkDate = LocalDate.now();
		// for - that check every date if exist in usageReport and if it does it willl
		// put in on the chart and then will remove it from the usageReport;
		for (LocalDate date = dpFromU.getValue(); date
				.isBefore(dpToU.getValue().plusDays(1)); date = date.plusDays(1)) {

			if (!usageReport.isEmpty()) {
				firstArrDate = usageReport.get(0);
				firstDate = usageReport.get(0).get(0);// first array
				someDate = getDate(firstDate); // yyyy-mm-dd
				checkDate = LocalDate.parse(someDate);
			}
			if (date.equals(checkDate) && (!usageReport.isEmpty())) {
				String dateTime_8 = date.toString() + " " + "08:00:00";
				if (usageReport.get(0).get(0).equals(dateTime_8)) {
					visit8_12.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(),
							Double.parseDouble(usageReport.get(0).get(1))));
					usageReport.remove(0);
				} else
					visit8_12.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(), 0));

				String dateTime_12 = date.toString() + " " + "12:00:00";
				if ((!usageReport.isEmpty()) && usageReport.get(0).get(0).equals(dateTime_12)) {
					visit12_16.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(),
							Double.parseDouble(usageReport.get(0).get(1))));
					usageReport.remove(0);
				} else
					visit12_16.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(), 0));

				String dateTime_16 = date.toString() + " " + "16:00:00";
				if ((!usageReport.isEmpty()) && usageReport.get(0).get(0).equals(dateTime_16)) {
					viit16_20.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(),
							Double.parseDouble(usageReport.get(0).get(1))));
					usageReport.remove(0);
				} else
					viit16_20.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(), 0));
			} else {
				visit8_12.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(), 0));
				visit12_16.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(), 0));
				viit16_20.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(), 0));
			}

		}
		visit8_12.setName("8:00-12:00");
		visit12_16.setName("12:00-16:00");
		viit16_20.setName("16:00-20:00");
		bcUsageChart.getData().addAll(visit8_12, visit12_16, viit16_20);

	}

	/*-------end of usage report section --------*/

	/*-------Revenue report section --------*/
	/**
	 * create revenue report
	 * 
	 * @throws ParseException Signals that an error has been reached unexpectedlywhile parsing
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void chartRevenue() throws ParseException {
		xAxisR = new CategoryAxis();
		yAxisR = new NumberAxis(0, 20, 2);
		bcRevenue.getData().clear();
		bcRevenue.setAnimated(false);
		bcRevenue.setBarGap(1d);
		bcRevenue.setCategoryGap(8.0);

		bcRevenue.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		bcRevenue.setPrefSize(613, 430);
		bcRevenue.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

		Series<String, Double> DailyRevenue = new Series<>();

		String someDate;
		String firstDate;
		LocalDate checkDate = LocalDate.now();

		// for - that check every date if exist in usageReport and if it does it willl
		// put in on the chart and then will remove it from the usageReport;
		for (LocalDate date = fromDate; date.isBefore(toDate.plusDays(1)); date = date.plusDays(1)) {

			if (!revReport.isEmpty()) {

				firstDate = revReport.get(0).get(0);// first array
				someDate = getDate(firstDate); // yyyy-mm-dd
				checkDate = LocalDate.parse(someDate);
			}
			if (date.equals(checkDate) && (!revReport.isEmpty())) {
				DailyRevenue.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(),
						Double.parseDouble(revReport.get(0).get(1))));
				revReport.remove(0);
			} else
				DailyRevenue.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(), 0));
		}

		DailyRevenue.setName("Daily income");
		bcRevenue.getData().addAll(DailyRevenue);

	}

	/**
	 * on click on show - send to server the current month and the next month send
	 * to server array list of object :[0]revenueReport, [1] array list of string
	 * ,[0]from date .[1] to date,[2] park name
	 * 
	 * @param event
	 * @throws ParseException
	 */

	@FXML
	void ShowReuvenue(ActionEvent event) throws ParseException {
		ArrayList<String> monthArr = new ArrayList<>();
		ArrayList<Object> msg = new ArrayList<>();
		ArrayList<String> data = new ArrayList<>();
		msg.add("revenueReport");
		int nextyear = Integer.parseInt(cbxYear.getValue());
		int nextmonth = Integer.parseInt(cbxMonth.getValue());

		if (nextmonth + 1 < 10) {// both months less than 10
			nextmonth++;
			data.add(cbxYear.getValue().toString() + "-0" + cbxMonth.getValue().toString() + "-01");
			data.add(nextyear + "-0" + nextmonth + "-01");
			fromDate = LocalDate.parse(cbxYear.getValue().toString() + "-0" + cbxMonth.getValue().toString() + "-01");
			toDate = LocalDate.parse(nextyear + "-0" + nextmonth + "-01");
		} else if (nextmonth == 9) {// first month 9
			nextmonth++;
			data.add(cbxYear.getValue().toString() + "-0" + cbxMonth.getValue().toString() + "-01");
			data.add(nextyear + "-" + nextmonth + "-01");
			fromDate = LocalDate.parse(cbxYear.getValue().toString() + "-0" + cbxMonth.getValue().toString() + "-01");
			toDate = LocalDate.parse(nextyear + "-" + nextmonth + "-01");
		}

		else if (nextmonth == 12) {
			nextmonth = 1;
			nextyear++;
			data.add(cbxYear.getValue().toString() + "-" + cbxMonth.getValue().toString() + "-01");
			data.add(nextyear + "-0" + nextmonth + "-01");
			fromDate = LocalDate.parse(cbxYear.getValue().toString() + "-" + cbxMonth.getValue().toString() + "-01");
			toDate = LocalDate.parse(nextyear + "-0" + nextmonth + "-01");
		} else {
			nextmonth++;
			data.add(cbxYear.getValue().toString() + "-" + cbxMonth.getValue().toString() + "-01");
			data.add(nextyear + "-" + nextmonth + "-01");
			fromDate = LocalDate.parse(cbxYear.getValue().toString() + "-" + cbxMonth.getValue().toString() + "-01");
			toDate = LocalDate.parse(nextyear + "-" + nextmonth + "-01");
		}

		data.add(park.getName());
		msg.add(data);
		ClientUI.sentToChatClient(msg);
		if (!getErrorInchart())
			chartRevenue();
		else {
			noDataTopresentInchartForDates();
			bcRevenue.getData().clear();
			setDatesComboForRevenue(monthArr);
		}
	}
	// end of revenue report section ***

	// received from server section ***

	/**
	 * receive the answer if the request for park manager was successful - true if
	 * so , false if not
	 * 
	 * @param answer boolean
	 */
	public static void recivedFromserver(boolean answer) {
		setRequestAnswerFromServer(answer);

	}

	/**
	 * returned from server park details if in case of an error returned null
	 * 
	 * @param object Object
	 */
	public static void recivedFromserverParkDetails(Object object) {
		if (object instanceof Park)
			setPark((Park) object);
		else
			setPark(null);
	}

	/**
	 * returned from server employee id
	 * 
	 * @param answer String
	 */
	public static void recivedFromserverEmployeeID(String answer) {
		setEmpID(Integer.parseInt(answer));
	}

	/**
	 * visitors report - if the report is empty we will receive 'failed'
	 * 
	 * @param visitorsReportAnswer ArrayList of ArrayList of String
	 */
	public static void recivedFromserverVisitorsReport(ArrayList<ArrayList<String>> visitorsReportAnswer) {
		setVisitorsReport(null);
		if ((Object) visitorsReportAnswer.get(0) instanceof String)
			seterrorInchart(true);
		else {
			seterrorInchart(false);
			setVisitorsReport(visitorsReportAnswer);

		}

	}

	/**
	 * usage report - if the report is empty we will receive null
	 * 
	 * @param usageReportAnswer ArrayList of ArrayList of String
	 */
	public static void recivedFromserverUsageReport(ArrayList<ArrayList<String>> usageReportAnswer) {
		setUsageReport(null);
		if (usageReportAnswer.isEmpty())
			seterrorInchart(true);
		else {
			seterrorInchart(false);
			setUsageReport(usageReportAnswer);

		}
	}

	/**
	 * revenue report - if the report is empty we will receive null
	 * 
	 * @param revReportAnswer ArrayList of ArrayList of String
	 */

	public static void recivedFromserverRevenueReport(ArrayList<ArrayList<String>> revReportAnswer) {
		setRevReport(null);
		if (revReportAnswer.isEmpty())
			seterrorInchart(true);
		else {
			seterrorInchart(false);
			setRevReport(revReportAnswer);

		}

	}

}
