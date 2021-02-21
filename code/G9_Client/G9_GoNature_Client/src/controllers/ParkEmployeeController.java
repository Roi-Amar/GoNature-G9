package controllers;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import client.ClientUI;
import dataLayer.Park;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import orderData.Order;
import sim.BarcodeSimulation;

/**
 *
 * Controller that gets the user login details
 *
 * @author Haim Azulay Hodaya Mekonen Roi Amar
 */

public class ParkEmployeeController implements Initializable {
	// ***** Top/Side Panels Details *****
	@FXML
	private Label lblFirstNameTitle;
	@FXML
	private Button btnLogout;

	// ***** Current Park Details *****
	@FXML
	private Label lblCurrentVisitors;

	// ***** Order Details *****
	@FXML
	private Label lblOrderNumber;
	@FXML
	private Label lblParkName;
	@FXML
	private Label lblDate;
	@FXML
	private Label lblTime;
	@FXML
	private Label lblVisitorsNumber;
	@FXML
	private Label lblEmail;

	// ***** Payment Details *****
	@FXML
	private Label lblPrice;
	@FXML
	private Label lblDiscount;
	@FXML
	private Label lblTotalPrice;

	// ***** BarcodeScan *****
	@FXML
	private Button btnBarcodeScan;

	// ***** Show Details => Regular Entry *****
	@FXML
	private Button btnManualAccess;
	@FXML
	private JFXTextField txtOrderNumber;
	@FXML
	private JFXTextField txtVisitorsAmount;
	@FXML
	private Button btnShowDetails;

	// ***** Random Entry *****
	@FXML
	private Button btnRandomVisitor;
	@FXML
	private Label lblDateTitle;
	@FXML
	private Label lblRandomDate;
	@FXML
	private Label lblTimeTitle;
	@FXML
	private Label lblRandomTime;
	@FXML
	private JFXTextField txtIdOrMemberId;

	// ***** Bottom Right Screen *****
	@FXML
	private JFXRadioButton radEnter;
	@FXML
	private JFXRadioButton radExit;
	@FXML
	private ToggleGroup radGroupStatus;
	@FXML
	private Button btnApprove;

	// ***** Global Variables *****
	private AlertController alert = new AlertController();
	private boolean informationExists = false;
	private boolean orderStatus = false;
	private static String firstName;
	private static String parkName;
	private static Park parkDetails;
	private static Order orderDetails;
	private static String error = "";
	private static String entryStatus = "";
	private static String exitStatus = "";
	private static ArrayList<Double> visitorsPrice = new ArrayList<Double>();
	private static int randomVisitorTicket = 0;

	/**
	 * Moves to the 'login' screen
	 * 
	 * @param event ActionEvent
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 *                     Thisclass is the general class of exceptions produced by
	 *                     failed orinterrupted I/O operations
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
		msg.add(data);
		ClientUI.sentToChatClient(msg);

		Stage stage = (Stage) btnLogout.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
		stage.setScene(new Scene(root));
	}

	/**
	 * External simulator that receives ID / MemberId and returns order number
	 * 
	 * @param event ActionEvent
	 */
	@SuppressWarnings("static-access")
	@FXML
	void barcodeScan(ActionEvent event) {
		setError("");
		informationExists = true;
		btnManualAccess.setVisible(false);

		String fromSimulator = BarcodeSimulation.getSim().read(3);

		// memberId case: memberId = G2 ==> memberId = 2
		if (Character.isLetter(fromSimulator.charAt(0))) {
			fromSimulator.substring(1);
		}

		sendToServerArrayList("ordersByOrderNumber", new ArrayList<String>(Arrays.asList(fromSimulator)));

		if (getError().equals("No such order")) {
			alert.failedAlert("Failed", "No such order.");
			clearAllFields();
			return;
		}

		txtOrderNumber.setText(String.valueOf(orderDetails.getOrderNumber()));
		txtVisitorsAmount.setText(String.valueOf(orderDetails.getVisitorsNumber()));

		showDetails(event);
	}

	/**
	 * Displays the order data
	 * 
	 * @param event ActionEvent
	 */
	@FXML
	void showDetails(ActionEvent event) {
		setError("");
		if (btnManualAccess.isVisible()) {
			setRandomModeOff();
			return;
		}

		if (txtOrderNumber.getText().isEmpty()) {
			alert.failedAlert("Failed", "You must enter order number.");
			return;
		}

		// will not enter if already has information from the barcodeScan
		if (!informationExists) {
			sendToServerArrayList("ordersByOrderNumber",
					new ArrayList<String>(Arrays.asList(txtOrderNumber.getText())));

			if (getError().equals("No such order")) {
				alert.failedAlert("Failed", "No such order.");
				clearAllFields();
				return;
			}
		}

		if (!(String.valueOf(orderDetails.getOrderNumber()).equals(txtOrderNumber.getText()))) {
			alert.failedAlert("Failed", "No such order.");
			clearAllFields();
			return;
		}

		if (orderDetails.getVisitorsNumber() != 0) {
			printOrderDetails();
		}

		informationExists = false;
		btnApprove.setDisable(false);
		orderStatus = true;
	}

	/**
	 * Payment confirmation at the entrance to the park / update on exit
	 * 
	 * @param event ActionEvent
	 */
	@FXML
	void approve(ActionEvent event) {
		if (!btnRandomVisitor.isVisible() && radEnter.isSelected()) {
			if (txtIdOrMemberId.getText().isEmpty()) {
				alert.failedAlert("Input Error", "You must enter id/memberid.");
				return;
			} else if (txtVisitorsAmount.getText().isEmpty()) {
				alert.failedAlert("Input Error", "You must enter amount of visitors.");
				return;
			} else if (!Character.isLetter(txtIdOrMemberId.getText().charAt(0))
					&& txtIdOrMemberId.getText().length() != 9) {
				alert.failedAlert("Input Error", "Id must be 9 digits long.");
				return;
			} else if (txtVisitorsAmount.getText().charAt(0) == '0') {
				alert.failedAlert("Input Error", "Number of visitors '0#' is invalid.");
				return;
			}
			// try to enter the park
			orderStatus = false;
			execEnter();
		} else if (btnRandomVisitor.isVisible() && radEnter.isSelected()) {
			if (txtVisitorsAmount.getText().isEmpty()) {
				alert.failedAlert("Input Error", "You must enter amount of visitors.");
				return;
			} else if (txtOrderNumber.getText().isEmpty()) {
				alert.failedAlert("Input Error", "You must enter Order Number!");
				return;
			} else if (txtVisitorsAmount.getText().charAt(0) == '0') {
				alert.failedAlert("Input Error", "Number of visitors '0#' is invalid.");
				return;
			}
			// try to enter the park
			orderStatus = true;
			execEnter();
		} else if (!btnRandomVisitor.isVisible() && radExit.isSelected()) {
			if (txtIdOrMemberId.getText().isEmpty()) {
				alert.failedAlert("Input Error", "You must enter id/memberid.");
				return;
			} else if (!Character.isLetter(txtIdOrMemberId.getText().charAt(0))
					&& txtIdOrMemberId.getText().length() != 9) {
				alert.failedAlert("Input Error", "Id must be 9 digits long.");
				return;
			}
			// try to exit park
			orderStatus = false;
			execExit();
		} else if (btnRandomVisitor.isVisible() && radExit.isSelected()) {
			if (txtOrderNumber.getText().isEmpty()) {
				alert.failedAlert("Input Error", "You must enter Order Number!.");
				return;
			}
			// try to exit park
			orderStatus = true;
			execExit();
		}
		orderStatus = false;
		clearAllFields();
		txtVisitorsAmount.setDisable(true);
	}

	/**
	 * Entrance control to the park for the case of an invited visitor and an casual
	 * visitor
	 * 
	 */
	public void execEnter() {
		ArrayList<String> idOrMemberId;
		if (!orderStatus) { // random visitor
			idOrMemberId = checkForIdOrMemberId();
			sendToGetEntryStatus(idOrMemberId.get(0), idOrMemberId.get(1), txtVisitorsAmount.getText());
		} else { // has an order number
			sendToGetEntryStatus("ORDERNUMBER", txtOrderNumber.getText(), txtVisitorsAmount.getText());
		}
		switch (getEntryStatus()) { // "enter"/"notGoodTime" / "allreadyInPark" / "parkfull" / "noRoomForRandom"
		case "notGoodTime":
			alert.failedAlert("Failed", "The time in the existing order not match the time now");
			break;
		case "allreadyInPark":
			alert.failedAlert("Failed", "The ticket user is allready in the park");
			break;
		case "parkFull":
			alert.failedAlert("Failed", "The park is too full for so many visitors");
			break;
		case "noRoomForRandom":
			alert.failedAlert("Failed",
					"The park is has reserved room for Orders\nif someone leave they will have room");
			break;
		case "orderDiffPark":
			alert.failedAlert("Failed", "The order belongs to another park");
			break;
		case "enter":
			if (!orderStatus)
				alert.successAlert("Success",
						txtVisitorsAmount.getText() + " visitors entered.\nYour ticket is: " + randomVisitorTicket);
			else {
				String message = txtVisitorsAmount.getText() + " visitor/s entered.";
				if (randomVisitorTicket != 0)
					message = message + "\nYour ticket number for the extra people is: " + randomVisitorTicket;
				alert.successAlert("Success", message);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Exit control to the park
	 * 
	 */
	public void execExit() {
		ArrayList<String> idOrMemberId;
		if (!orderStatus) {
			idOrMemberId = checkForIdOrMemberId();
			sendToGetExitStatus(idOrMemberId.get(0), idOrMemberId.get(1), txtVisitorsAmount.getText());
			// invited visitor
		} else {
			sendToGetExitStatus("ORDERNUMBER", txtOrderNumber.getText(), txtVisitorsAmount.getText());
		}

		switch (getExitStatus()) {
		case "allreadyExited":
			alert.failedAlert("Failed", "The order already has an exit registered");
			break;
		case "neverWasHere":
			alert.failedAlert("Failed", "The order does not match any entery listed");
			break;
		case "orderDiffPark":
			alert.failedAlert("Failed", "The order number match a diffrent park");
			break;
		case "exited":
			alert.successAlert("Success", "Thanks for visiting, hope to see you again soon.");
			break;
		default:
			break;
		}
	}

	/**
	 * Checks for the case of a casual visitor whether to enter ID / MemberId
	 * 
	 * @return idOrMemberId
	 */
	public ArrayList<String> checkForIdOrMemberId() {
		ArrayList<String> idOrMemberId = new ArrayList<String>();

		if (Character.isLetter(txtIdOrMemberId.getText().charAt(0))) {
			idOrMemberId.add("MEMBERID");
			idOrMemberId.add(txtIdOrMemberId.getText().substring(1));
		} else {
			idOrMemberId.add("ID");
			idOrMemberId.add(txtIdOrMemberId.getText());
		}

		return idOrMemberId;
	}

	/**
	 * Displays the price according to the cases
	 * 
	 */
	public void setPrice() {
		ArrayList<String> idOrMemberId;

		// price for random visitor
		if (!btnRandomVisitor.isVisible()) {
			idOrMemberId = checkForIdOrMemberId();
			sendToGetPrice(idOrMemberId.get(0), idOrMemberId.get(1), txtVisitorsAmount.getText());
			System.out.println(txtVisitorsAmount.getText());
			// price for ordered visitor
		} else {
			sendToGetPrice("ORDERNUMBER", txtOrderNumber.getText(), txtVisitorsAmount.getText());
		}

		lblPrice.setText(String.format("%.1f", visitorsPrice.get(0)) + "₪");
		lblDiscount.setText(String.format("%.1f", visitorsPrice.get(1)) + "%");
		lblTotalPrice.setText(String.format("%.1f", visitorsPrice.get(2)) + "₪");

		if (radExit.isSelected()) {
			clearPaymentFields();
		}
	}

	/**
	 * Sends the server some data to get a matching price.
	 * 
	 * [0] = getVisitorsPrice [1] = parkName [2] = String "ID" / "MEMBERID" /
	 * "ORDERNUMBER" [3] = Value ID / MemberId / orderNumber [4] = amount of
	 * visitors
	 * 
	 * @param type   String
	 * @param value  String
	 * @param amount String
	 */
	public void sendToGetPrice(String type, String value, String amount) {
		ArrayList<Object> msg = new ArrayList<Object>();
		msg.add("getVisitorsPrice");
		msg.add(getParkName());
		msg.add(type);
		msg.add(value);
		msg.add(amount);
		ClientUI.sentToChatClient(msg);
	}

	/**
	 * Sends the server some data to check park entrance cases
	 * 
	 * [0] = enterThePark [1] = Park object = parkDetails [2] = String "ID" /
	 * "MEMBERID" / "ORDERNUMBER" [3] = Value ID / MemberId / orderNumber [4] =
	 * amount of visitors
	 * 
	 * @param type   String
	 * @param value  String
	 * @param amount String
	 */
	public void sendToGetEntryStatus(String type, String value, String amount) {
		ArrayList<Object> msg = new ArrayList<Object>();
		msg.add("enterThePark");
		msg.add(parkDetails);
		msg.add(type);
		msg.add(value);
		msg.add(amount);
		ClientUI.sentToChatClient(msg);
	}

	/**
	 * Sends the server some data to check park exit cases
	 * 
	 * [0] = exitThePark [1] = Park object = parkDetails [2] = String "ID" /
	 * "MEMBERID" / "ORDERNUMBER" [3] = Value ID / MemberId / orderNumber [4] =
	 * amount of visitors
	 * 
	 * @param type   String
	 * @param value  String
	 * @param amount String
	 */
	public void sendToGetExitStatus(String type, String value, String amount) {
		// Query
		ArrayList<Object> msg = new ArrayList<Object>();
		msg.add("exitThePark");
		msg.add(parkDetails);
		msg.add(type);
		msg.add(value);
		msg.add(amount);
		ClientUI.sentToChatClient(msg);
	}

	/**
	 * Sends the server a park name to get the right object for it
	 * 
	 * @param addToEnter (number of visitors to add / remove)
	 */
	public void updateParkStatus(int addToEnter) {
		ArrayList<String> data = new ArrayList<String>();
		data.add(getParkName());
		data.add(String.valueOf(addToEnter));
		sendToServerArrayList("getParkDetails", data);

		lblCurrentVisitors.setText("[" + getParkName() + "]:  " + String.valueOf(parkDetails.getCurrentAmount()) + "/"
				+ parkDetails.getMaximumCapacityInPark());
	}

	/**
	 * Send an object to a server to get his data
	 * 
	 * @param type      (the case we use)
	 * @param dbColumns (the ArrayList to send)
	 */
	public void sendToServerArrayList(String type, ArrayList<String> dbColumns) {
		ArrayList<Object> msg = new ArrayList<Object>();
		msg.add(type);
		msg.add(dbColumns);
		ClientUI.sentToChatClient(msg);
	}

	/**
	 * update current visitors number in the park.The number is updated every time
	 * when visitor enters or exits the park
	 * 
	 * @param visitNum String
	 */
	public void setCurrentVisitors(String visitNum) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				lblCurrentVisitors.setText(
						"[" + getParkName() + "]:  " + visitNum + "/" + parkDetails.getMaximumCapacityInPark());
			}
		});
	}

	/**
	 * prints order data
	 * 
	 */
	public void printOrderDetails() {
		// 2021-01-01 08:00:00
		String DateAndTime = orderDetails.getArrivedTime();
		String[] splitDateAndTime = DateAndTime.split(" ");
		// 2021-01-01
		String date = splitDateAndTime[0];
		// changing the date format from "yyyy-MM-dd" to "dd-MM-yyyy"
		// iFormatter -> input format
		DateFormat iFormatter = new SimpleDateFormat("yyyy-MM-dd");
		// oFormatter -> output format
		DateFormat oFormatter = new SimpleDateFormat("dd-MM-yyyy");
		try {
			String strDateTime = oFormatter.format(iFormatter.parse(date));

			// 08:00:00 -> 08:00
			String time = (String) splitDateAndTime[1].subSequence(0, 5);

			lblOrderNumber.setText(String.valueOf(orderDetails.getOrderNumber()));
			lblParkName.setText(orderDetails.getParkName());
			lblDate.setText(strDateTime);
			lblTime.setText(time);
			lblEmail.setText(orderDetails.getOrderEmail());
			// random visitor order
			if (orderDetails.getOrderEmail().equals("null")) {
				lblVisitorsNumber.setText(String.valueOf(orderDetails.getAmountArrived()));
			} else {
				lblVisitorsNumber.setText(String.valueOf(orderDetails.getVisitorsNumber()));
			}

			if (!txtVisitorsAmount.getText().isEmpty()) {
				setPrice();
			} else {
				txtVisitorsAmount.setText(lblVisitorsNumber.getText());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getting information from the server about an order
	 * 
	 * @param msg (Object)
	 */
	public static void receivedFromServerOrderDetails(Object msg) {
		if (msg instanceof Order) {
			ParkEmployeeController.orderDetails = (Order) msg;
		} else {
			setError((String) msg);
		}
	}

	/**
	 * Getting information from the server about visitor Prices
	 * 
	 * @param received (ArrayList(Object))
	 */
	public static void receivedFromServerVisitorsPrice(ArrayList<Object> received) {
		ParkEmployeeController.visitorsPrice.clear();
		ParkEmployeeController.visitorsPrice.add((Double) received.get(1));
		ParkEmployeeController.visitorsPrice.add((Double) received.get(2));
		ParkEmployeeController.visitorsPrice.add((Double) received.get(3));
	}

	/**
	 * Getting information from the server about park entrance status
	 * 
	 * @param received (ArrayList(Object))
	 */
	public static void receivedFromServerEntryStatus(ArrayList<Object> received) {
		setEntryStatus((String) received.get(1));
		if (received.get(1).equals("enter"))
			randomVisitorTicket = (int) received.get(2);
	}

	/**
	 * Getting information from the server about park exit status
	 * 
	 * @param received (ArrayList(Object))
	 */
	public static void receivedFromServerExitStatus(ArrayList<Object> received) {
		setExitStatus((String) received.get(1));
	}

	/**
	 * Getting information from the server about an park
	 * 
	 * @param msg (Object)
	 */
	public static void receivedFromServerParkDetails(Object msg) {
		if (msg instanceof Park)
			ParkEmployeeController.parkDetails = (Park) msg;
	}

	/**
	 * get the 'firstName' of the user
	 * 
	 * @return firstName
	 */
	public static String getFirstName() {
		return firstName;
	}

	/**
	 * set the 'firstName' of the user
	 * 
	 * @param firstName String
	 */
	public static void setFirstName(String firstName) {
		ParkEmployeeController.firstName = firstName;
	}

	/**
	 * get the error
	 * 
	 * @return error
	 */
	public static String getError() {
		return error;
	}

	/**
	 * set errors
	 * 
	 * @param error String
	 */
	public static void setError(String error) {
		ParkEmployeeController.error = error;
	}

	/**
	 * get the entryStatus
	 * 
	 * @return entryStatus
	 */
	public static String getEntryStatus() {
		return entryStatus;
	}

	/**
	 * set entryStatus
	 * 
	 * @param entryStatus String
	 */
	public static void setEntryStatus(String entryStatus) {
		ParkEmployeeController.entryStatus = entryStatus;
	}

	/**
	 * get the exitStatus
	 * 
	 * @return exitStatus
	 */
	public static String getExitStatus() {
		return exitStatus;
	}

	/**
	 * set exitStatus
	 * 
	 * @param exitStatus String
	 */
	public static void setExitStatus(String exitStatus) {
		ParkEmployeeController.exitStatus = exitStatus;
	}

	/**
	 * get the orderDetails
	 * 
	 * @return orderDetails
	 */
	public static Order getOrderDetails() {
		return orderDetails;
	}

	/**
	 * get the parkName
	 * 
	 * @return parkName
	 */
	public static String getParkName() {
		return parkName;
	}

	/**
	 * set parkName
	 * 
	 * @param parkName String
	 */
	public static void setParkName(String parkName) {
		ParkEmployeeController.parkName = parkName;
	}

	/**
	 * Enter random mode
	 * 
	 * @param event
	 */
	@FXML
	void randomVisitor(ActionEvent event) {
		btnManualAccess.setVisible(true);
		btnRandomVisitor.setVisible(false);
		lblDateTitle.setVisible(true);
		lblRandomDate.setVisible(true);
		lblTimeTitle.setVisible(true);
		lblRandomTime.setVisible(true);
		txtIdOrMemberId.setVisible(true);
		clearAllOrderFields();
		DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
		LocalDateTime arrivelTime = LocalDateTime.now();
		lblRandomTime.setText(arrivelTime.format(time));
	}

	/**
	 * Turns off order mode
	 * 
	 */
	public void clearAllOrderFields() {
		txtOrderNumber.clear();
		lblOrderNumber.setText("");
		lblParkName.setText("");
		lblDate.setText("");
		lblTime.setText("");
		lblVisitorsNumber.setText("");
		lblEmail.setText("");
	}

	/**
	 * Turns off random mode
	 * 
	 */
	public void setRandomModeOff() {
		btnManualAccess.setVisible(false);
		btnRandomVisitor.setVisible(true);
		lblDateTitle.setVisible(false);
		lblRandomDate.setVisible(false);
		lblTimeTitle.setVisible(false);
		lblRandomTime.setVisible(false);
		txtIdOrMemberId.setVisible(false);
	}

	/**
	 * Clear the screen fields
	 * 
	 */
	public void clearAllFields() {
		/***** Entry / Exit *****/
		setError("");
		setEntryStatus("");
		setExitStatus("");
		/***** Order Details *****/
		txtOrderNumber.clear();
		lblOrderNumber.setText("");
		lblParkName.setText("");
		lblDate.setText("");
		lblTime.setText("");
		lblVisitorsNumber.setText("");
		lblEmail.setText("");
		/***** Payment Details *****/
		lblPrice.setText("");
		lblDiscount.setText("");
		lblTotalPrice.setText("");
		/***** Random Mode Details *****/
		lblDateTitle.setVisible(false);
		lblRandomDate.setVisible(false);
		lblTimeTitle.setVisible(false);
		lblRandomTime.setVisible(false);
		txtIdOrMemberId.clear();
		btnRandomVisitor.setVisible(true);
		/***** Global *****/
		txtVisitorsAmount.clear();
		btnApprove.setDisable(true);
		orderStatus = false;
		radEnter.setSelected(true);
	}

	/**
	 * Clear all payment fields (on exit)
	 * 
	 */
	public void clearPaymentFields() {
		lblPrice.setText("");
		lblDiscount.setText("");
		lblTotalPrice.setText("");
		txtVisitorsAmount.clear();
		txtVisitorsAmount.setDisable(true);
		orderStatus = false;
	}

	/**
	 * Initializing and force each of the fields according to the Required templates
	 * 
	 * @param location  URL
	 * @param resources ResourceBundle
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Context.getInstance().setPEC(this);

		txtVisitorsAmount.setDisable(true);
		btnApprove.setDisable(true);
		radEnter.setSelected(true);
		setRandomModeOff();
		btnManualAccess.setVisible(true);

		setFirstName(LoginController.getFirstName());
		lblFirstNameTitle.setText(getFirstName());
		setParkName(LoginController.getParkName());
		updateParkStatus(0);

		/***** Random *****/
		LocalDateTime arrivelDate = LocalDateTime.now();
		lblRandomDate.setText(arrivelDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

		/***** Barcode / Regular *****/
		// force the field to be numeric only
		txtOrderNumber.textProperty().addListener((obs, oldValue, newValue) -> {
			txtVisitorsAmount.setDisable(false);
			// \\d -> only digits
			// * -> escaped special characters
			if (!newValue.isEmpty() && !newValue.matches("\\d")) {
				// ^\\d -> everything that not a digit
				txtOrderNumber.setText(newValue.replaceAll("[^\\d]", ""));
			}
		});

		// force the field to be numeric only
		txtVisitorsAmount.textProperty().addListener((obs, oldValue, newValue) -> {
			btnApprove.setDisable(false);

			// \\d -> only digits
			// * -> escaped special characters
			if (newValue.length() == 1 || !newValue.isEmpty() && !newValue.matches("\\d")) {
				// ^\\d -> everything that not a digit
				txtVisitorsAmount.setText(newValue.replaceAll("[^\\d]", ""));

				// } else if (!newValue.isEmpty() && newValue.matches("\\d")) {
				if (newValue.charAt(0) != '0') {
					if (!btnRandomVisitor.isVisible() && !txtIdOrMemberId.getText().isEmpty()) {
						// update random visitor prices
						// createFakeOrder(null, null, Integer.parseInt(txtVisitorsAmount.getText()));
						setPrice();
						System.out.println("sent to get update price...");
					} else if (!txtOrderNumber.getText().isEmpty()) {
						showDetails(null);
					}
				}
			}
		});

		/***** Random *****/
		// force the field to be numeric only
		txtIdOrMemberId.textProperty().addListener((obs, oldValue, newValue) -> {
			btnApprove.setDisable(false);
			txtVisitorsAmount.setDisable(false);

			if (!newValue.isEmpty()) {
				txtIdOrMemberId.setText(newValue);
			}
		});

		txtOrderNumber.textProperty().addListener((obs, oldValue, newValue) -> {
			if (radExit.isSelected()) {
				btnApprove.setDisable(false);
				txtVisitorsAmount.setDisable(false);

				if (!newValue.isEmpty()) {
					txtOrderNumber.setText(newValue);
				}
			}
		});

		/***** Approve *****/
		// listen to changes in selected toggle
		radGroupStatus.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
			if (newToggle == radEnter) {
				txtVisitorsAmount.setDisable(false);
				((RadioButton) radGroupStatus.getSelectedToggle()).getText();
			} else if (newToggle == radExit) {
				clearPaymentFields();
				((RadioButton) radGroupStatus.getSelectedToggle()).getText();
			}
		});
	}
}
