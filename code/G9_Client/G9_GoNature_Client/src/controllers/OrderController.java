package controllers;

import java.time.LocalTime;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import orderData.Order;

/**
 * controller include all three screen of success order
 *
 * @author Bar Katz
 */

public class OrderController implements Initializable {
	/********* Pane **************/
	@FXML
	private StackPane pnStackOrder;
	@FXML
	private Pane pnOrder;
	@FXML
	private Pane pnPayment;
	@FXML
	private Pane pnConfirmation;

	/******** Button ********/
	@FXML
	private Button btnBack;
	@FXML
	private Button btnClear;
	@FXML
	private Button btnNext;
	@FXML
	private Button btnContinue;
	@FXML
	private Button btnHome;
	@FXML
	private Button information;
	@FXML
	private Hyperlink hyperTerms;

	/********* Order screen ************/
	@FXML
	private JFXDatePicker txtdate;
	@FXML
	private JFXComboBox<String> cbxArrivelTime;
	@FXML
	private JFXComboBox<String> cbxParkName;
	@FXML
	private JFXTextField txtVisitorsNumber;
	@FXML
	private JFXTextField txtInvitingEmail;
	@FXML
	private JFXTextField txtmemberID;
	@FXML
	private JFXTextField txtPhoneNum;
	@FXML
	private ImageView imgOrder;

	/***** confirmation screen ***/
	@FXML
	private Hyperlink btnHere;
	@FXML
	private Label txtOrderNum;

	/*************** payment screen ******************/

	@FXML
	private JFXRadioButton radioCash;
	@FXML
	private JFXRadioButton radioPayPal;
	@FXML
	private JFXRadioButton radioCreditCard;
	@FXML
	private JFXCheckBox CheckBoxAgreed;
	@FXML
	private Label txtprice;
	@FXML
	private Label txtVisitoramountPrice;
	@FXML
	private Label txtdDiscount;
	@FXML
	private Label txtTotalPrice;

	/************* my values ***************/

	private Image imgOrderEmpty = new Image("/gui/cart-removebg-80.png");
	private Image imgOrderFull = new Image("/gui/cartfull-removebg-80.png");

	private static ArrayList<String> ParksNames = new ArrayList<>();
	private static Order orderSuccess;
	private static Order order;
	private static ArrayList<Object> saveDetails = new ArrayList<>();
	private static int flagOrder = 0;
	private static String status = "not";
	private static boolean faildDB = true;
	private static boolean confirmOrder = false;
	private String memberId = null;
	private String ID = null;
	private AlertController alert = new AlertController();

	public URL getLocation() {
		return location;
	}

	public void setLocation(URL location) {
		this.location = location;
	}

	public ResourceBundle getResources() {
		return resources;
	}

	public void setResources(ResourceBundle resources) {
		this.resources = resources;
	}

	private URL location;
	private ResourceBundle resources;

	public Pane getPnOrder() {
		return pnOrder;
	}

	public void setPnOrder(Pane pnOrder) {
		this.pnOrder = pnOrder;
	}

	public Pane getPnPayment() {
		return pnPayment;
	}

	public void setPnPayment(Pane pnPayment) {
		this.pnPayment = pnPayment;
	}

	//***************** Getters and Setters for statics *****************

	public static boolean isConfirmOrder() {
		return confirmOrder;
	}

	public static void setConfirmOrder(boolean confirmOrder) {
		OrderController.confirmOrder = confirmOrder;
	}

	public static Order getOrder() {
		return order;
	}

	public static void setOrder(Order order) {
		OrderController.order = order;
	}

	public static boolean isFaildDB() {
		return faildDB;
	}

	public static void setFaildDB(boolean faildDB) {
		OrderController.faildDB = faildDB;
	}

	// status to check if the order success
	public static String getStatus() {
		return status;
	}

	// status to check if the order success
	public static void setStatus(String status) {
		OrderController.status = status;
	}

	// order after server
	public static Order getOrderSuccess() {
		return orderSuccess;
	}

	// order after server
	public static void setOrderSuccess(Order orderSuccess) {
		OrderController.orderSuccess = orderSuccess;
	}

	public static void setParksNames(ArrayList<String> parksNames) {
		ParksNames = parksNames;
	}

	/**
	 * function for the back button Checks which window you are currently in and
	 * goes back to the previous page
	 * 
	 * @param ActionEvent event
	 * @exception IOException
	 */

	@FXML
	void back(ActionEvent event) throws IOException {
		ObservableList<Node> stackPanels = this.pnStackOrder.getChildren();

		if (stackPanels.size() > 1) {
			Node topNode = stackPanels.get(stackPanels.size() - 1);

			if (topNode.getId().equals("pnOrder")) {// in order screen
				saveOrder();
				Stage stage = (Stage) btnBack.getScene().getWindow();
				Parent root = FXMLLoader.load(getClass().getResource("/gui/Welcome.fxml"));
				stage.setScene(new Scene(root));
			} else if (topNode.getId().equals("pnPayment")) {// in payment screen
				imgOrder.setImage(imgOrderEmpty);
				pnOrder.toFront();
			} else if (topNode.getId().equals("pnConfirmation")) {// in confirmation screen
				// pnPayment.toFront();
				alert.successAlert("confirmation", "You have new order : " + orderSuccess.getOrderNumber()
						+ "\nThank you for ordering in Go - Nature");
				Stage stage = (Stage) btnBack.getScene().getWindow();
				Parent root = FXMLLoader.load(getClass().getResource("/gui/Welcome.fxml"));
				stage.setScene(new Scene(root));
			}
		}
	}

	/**
	 * save the order details
	 */

	@SuppressWarnings("static-access")
	public void saveOrder() {
		this.flagOrder = 1;
		saveDetails.add(txtmemberID.getText());
		saveDetails.add(cbxParkName.getValue());
		saveDetails.add(txtdate.getValue());
		saveDetails.add(cbxArrivelTime.getValue());
		saveDetails.add(txtVisitorsNumber.getText());
		saveDetails.add(txtInvitingEmail.getText());
		saveDetails.add(txtPhoneNum.getText());

	}

	/**
	 * clear all fields in order screen
	 * 
	 * @param ActionEvent
	 */
	@FXML
	void clear(ActionEvent event) {
		txtVisitorsNumber.clear();
		txtInvitingEmail.clear();
		txtmemberID.clear();
		txtPhoneNum.clear();
		cbxParkName.getSelectionModel().clearSelection();
	}

	/**
	 * radio button for open screen creditCard
	 * 
	 * @param ActionEvent
	 * @exception IOException
	 */

	@FXML
	void crditCardClick(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		Pane root = FXMLLoader.load(getClass().getResource("/gui/CreditCard.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {

				radioCash.setSelected(true);
				pnPayment.setDisable(false);

			}
		});

		pnPayment.setDisable(true);

	}

	/**
	 * radio button for open screen alert
	 * 
	 * @param event
	 */
	@FXML
	void payPalClick(ActionEvent event) {
		alert.successAlert("Simulation", "PayPal - simulation");
	}

	/**
	 * msgForServer is ArrayList of objects [0] name of the class "order" [1] order
	 * Object / String / boolean output from server : Object Order / String /
	 * boolean
	 * 
	 * By clicking button next the function will check if the values in the fields
	 * are correct if so, send them to server Or if there is no place will open the
	 * waiting list screen
	 * 
	 * @param ActionEvent
	 * @throws IOException
	 */
	@FXML
	void next(ActionEvent event) throws IOException {
		ArrayList<Object> msgNewOrderForServer = new ArrayList<>();
		ArrayList<Object> msgConfirmForServer = new ArrayList<>();
		// continue only if the fields are correct
		if (checkNotEmptyFields() && checkCorrectFields() && checkCurrentTime()) {
			msgNewOrderForServer.add("order");
			String strDateTime = txtdate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " "
					+ getArrivalTime();
			OrderController.order = new Order(Integer.parseInt(txtVisitorsNumber.getText()), txtInvitingEmail.getText(),
					txtPhoneNum.getText(), cbxParkName.getValue().toString(), strDateTime, this.memberId, this.ID);

			msgNewOrderForServer.add(OrderController.order);
			imgOrder.setImage(imgOrderFull);

			if (btnNext == event.getSource()) {

				ClientUI.sentToChatClient(msgNewOrderForServer);
				this.memberId = null;
				this.ID = null;

				if (status.equals("Failed")) { // the user can't order
					OrderController.status = "not";
					pnOrder.setDisable(true);
					Stage stage = new Stage();
					Pane root = FXMLLoader.load(getClass().getResource("/gui/WaitingList.fxml"));
					Scene scene = new Scene(root);
					stage.setResizable(false);
					stage.setScene(scene);
					stage.show();

					stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
						@Override
						public void handle(WindowEvent t) {

							pnOrder.setDisable(false);

						}
					});

				} else if (status.equals("Existing")) {
					alert.setAlert("You already have an order on this day for this time ");
				} else if (!faildDB) { // the Order details didnt enter to DB
					alert.setAlert("something went wrong\nplease close the program and start again");
				} else { // Order success
					double value=orderSuccess.getPrice();
					this.txtprice.setText(String.format("%.1f", value) + " ₪");
					value=orderSuccess.getTotalPrice();
					this.txtTotalPrice.setText(String.format("%.1f", value) + " ₪");
					 value = (1 - (orderSuccess.getTotalPrice() / orderSuccess.getPrice())) * 100;
					this.txtdDiscount.setText(String.format("%.1f", value) + "%");
					this.txtVisitoramountPrice.setText(String.valueOf(orderSuccess.getVisitorsNumber()));
					pnPayment.toFront();
					groupRadioButton();

				}
			} else if (btnContinue == event.getSource()) { // in payment screen
				msgConfirmForServer.add("confirmOrder");
				msgConfirmForServer.add(OrderController.orderSuccess);
				msgConfirmForServer.add(CreditCardController.getDetails());

				if (checkNotEmptyFieldsPaymentScreen()) {
					ClientUI.sentToChatClient(msgConfirmForServer);

					// System.out.println("order con line 310");

					if (OrderController.confirmOrder) {
						OrderController.confirmOrder = false;
						this.txtOrderNum.setText(String.valueOf(OrderController.orderSuccess.getOrderNumber()));
						pnConfirmation.toFront();
					} else
						alert.setAlert("something went wrong\nplease close the program and start again");
				}

				CreditCardController.setDetails(null);// set null in credut card

			}

		} else

		{
			this.memberId = null;
			this.ID = null;
		}
	}

	/**
	 * returns to the home page
	 * 
	 * @param ActionEvent
	 * @throws IOException
	 */

	@FXML
	void home(ActionEvent event) throws IOException {		
		alert.successAlert("confirmation", "You have new order : " + orderSuccess.getOrderNumber()
		+ "\nThank you for ordering in Go - Nature");
		Stage stage = (Stage) btnHere.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("/gui/Welcome.fxml"));
		stage.setScene(new Scene(root));
	}

	
	/**
	 * doesnt work - if we will want in the future to edit orders
	 * @param event
	 * @throws IOException
	 */
	@FXML
    void here(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnHere.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/gui/EditOrder.fxml"));
        stage.setScene(new Scene(root));

    }
	
	
	/**
	 *  hyperlink for terms and coditiond
	 * @param event ActionEvent
	 * @throws IOException
	 */

	@FXML
	void termsCond(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		Pane root = FXMLLoader.load(getClass().getResource("/gui/terms.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				pnPayment.setDisable(false);

			}
		});

		pnPayment.setDisable(true);
	}

	/*******************************
	 * Code for tests and additions
	 ****************************************/

	/**
	 * Checks which payment method the user has selected and returns a string
	 * accordingly
	 * 
	 * @return the payment method that the user choose
	 */

	public String paymentChosen() {
		if (radioCash.isSelected())
			return "Cash";
		if (radioPayPal.isSelected())
			return "PayPal";
		return "CreditCard";
	}

	/**
	 * for Radio Button
	 */
	public void groupRadioButton() {
		final ToggleGroup group = new ToggleGroup();
		radioCash.setToggleGroup(group);
		radioCreditCard.setToggleGroup(group);
		radioPayPal.setToggleGroup(group);
	}

	/**
	 * Checks if the fields are empty for the payment screen
	 * 
	 * @return true if the fields are not empty ,false otherwise
	 * 
	 */

	public boolean checkNotEmptyFieldsPaymentScreen() {

		if (!(radioCash.isSelected() || radioCreditCard.isSelected() || radioPayPal.isSelected())) {
			alert.setAlert("you need to choose payment method");
			return false;
		}
		if (!CheckBoxAgreed.isSelected()) {
			alert.setAlert("you need to approve the terms");
			return false;
		}
		return true;
	}

	/**
	 * Checks if the fields are empty for the order screen
	 * 
	 * @return true if the fields are not empty ,false otherwise
	 * 
	 */

	public boolean checkNotEmptyFields() {
		String visitorsNumber = txtVisitorsNumber.getText();
		String email = txtInvitingEmail.getText();
		String parkName = new String();
		if (cbxParkName.getValue() != null)
			parkName = cbxParkName.getValue().toString();
		String memberId = txtmemberID.getText();
		String Phone = txtPhoneNum.getText();

		if (visitorsNumber.isEmpty() || email.isEmpty() || parkName.isEmpty() || memberId.isEmpty()
				|| Phone.isEmpty() ||txtdate.getValue()==null) {
			alert.setAlert("One or more of the fields are empty.\n Please fill them in and try again.");
			return false;
		}
		return true;
	}

	/**
	 * Checks whether the time selected on today's date is relevant to placing an
	 * order
	 * 
	 * @return true if the reservation is in the correct time ,false otherwise
	 */

	public boolean checkCurrentTime() {
		LocalDate date = txtdate.getValue();
		String[] arrSplit = cbxArrivelTime.getValue().toString().split("-");
		String[] hour = arrSplit[0].split(":");
		LocalTime arrivalTime = LocalTime.of(Integer.parseInt(hour[0]), 00, 00);

		LocalTime now = LocalTime.now();
		if (date.compareTo(LocalDate.now()) <= 0 && now.compareTo(arrivalTime) >= 0) {
			alert.setAlert("You are trying to book for a time that has already passed. Please select a future time");

			return false;
		}
		return true;
	}
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
	 * creates a string for the DB according to cbxArrivelTime
	 * 
	 * @return string of the reservation time
	 */

	public String getArrivalTime() {
		String[] array = cbxArrivelTime.getValue().toString().split("-");
		return array[0];
	}

	/**
	 * checks valid input
	 * 
	 * @return true if all the field are correct ,false otherwise
	 * 
	 **/

	public boolean checkCorrectFields() {
		if(checkToolate()) {
			alert.setAlert("Invalid date");
			txtdate.setValue(LocalDate.now());
			return false;
		}
		if (!validInput("email", txtInvitingEmail.getText())) {
			alert.setAlert("Invalid email address");
			return false;
		}
		if (!validInput("Phone", txtPhoneNum.getText())) {
			alert.setAlert("Invalid phone number");
			return false;
		}
		if (!validInput("AmountVisitor", txtVisitorsNumber.getText()) || txtVisitorsNumber.getText().equals("0")) {
			alert.setAlert("Invalid amount of visitors");
			return false;
		}
		if (validInput("memberId", txtmemberID.getText())) {
			if (txtmemberID.getText().contains("g")) {
				if (Integer.parseInt(txtVisitorsNumber.getText()) > 15) {
					alert.setAlert("An organized group is limited to 15 participants");
					return false;
				}
			}
			this.memberId = txtmemberID.getText().substring(1);
			return true;
		} else if (validInput("ID", txtmemberID.getText())) {
			this.ID = txtmemberID.getText();

			return true;
		}
		alert.setAlert("Invalid member-ID / ID ");
		return false;
	}

	/**
	 * patterns:
	 */
	public static final Pattern VALIDEMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);
	public static final Pattern VALIDAmountVisito = Pattern.compile("^[0-9]{0,3}$", Pattern.CASE_INSENSITIVE);
	public static final Pattern VALIDMemberId = Pattern.compile("^[m,g]{1}[0-9]{4}$", Pattern.CASE_INSENSITIVE);
	public static final Pattern VALIDID = Pattern.compile("^[0-9]{9}$", Pattern.CASE_INSENSITIVE);
	public static final Pattern VALIDPhone = Pattern.compile("^[0-9]{3}[0-9]{7}$", Pattern.CASE_INSENSITIVE);
	
	/**
	 * checks valid input for each nameMathod according to relevant the pattern
	 * 
	 * @param nameMathod String
	 * @param txt        String
	 * @return true if the pattern are correct ,false otherwise
	 */

	public static boolean validInput(String nameMathod, String txt) {
		Matcher matcher = null;
		if (nameMathod.equals("email")) {
			matcher = VALIDEMAIL.matcher(txt);
		} else if (nameMathod.equals("AmountVisitor")) {
			matcher = VALIDAmountVisito.matcher(txt);
		} else if (nameMathod.equals("memberId")) {
			matcher = VALIDMemberId.matcher(txt);
		} else if (nameMathod.equals("ID")) {
			matcher = VALIDID.matcher(txt);
		} else if (nameMathod.equals("Phone")) {
			matcher = VALIDPhone.matcher(txt);
		}
		return matcher.find();
	}
	
	/**********************
	 * Methods that get answer from server
	 *************************************/

	/**
	 * Receives from the server the status of the action, if it is success Receives
	 * success message and an object of order ,else receives a failure message
	 * 
	 * @param newOrder Object
	 * 
	 */

	public static void recivedFromServer(Object newOrder) {
		System.out.println(newOrder);
		if (newOrder instanceof String) {
			String status = (String) newOrder;
			setStatus(status);
		} else if (newOrder instanceof Boolean) {
			boolean flag = (boolean) newOrder;
			setFaildDB(flag);
			setStatus("not");
		} else {
			Order myOrder = (Order) newOrder;
			setOrderSuccess(myOrder);
			setStatus("not");
		}
	}

	/**
	 * return the list of all the parks names
	 * 
	 * @param parks ArrayList of String of parks
	 **/

	public static void recivedFromServerParksNames(ArrayList<String> parks) {
		setParksNames(parks);
	}

	/**
	 * return true if the user is confirm the order and the server success to enter
	 * 
	 * @param msg boolean
	 **/

	public static void recivedFromServerConfirmOrder(boolean msg) {
		setConfirmOrder(msg);
	}

	/***************************************
	 * Done with the server
	 **********************************************/

	/**
	 * Initialize the fields according to the actions performed by the user
	 */
	@SuppressWarnings("static-access")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Context.getInstance().setOrderC(this);

		txtVisitorsNumber.setText("1");
		cbxArrivelTime.setItems(FXCollections.observableArrayList("08:00-12:00", "12:00-16:00", "16:00-20:00"));

		// user can choose to date only date today until next year.

		txtdate.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				LocalDate nextYear = LocalDate.of(today.getYear() + 1, today.getMonth(), today.getDayOfMonth());
				setDisable(empty || (date.compareTo(nextYear) > 0 || date.compareTo(today) < 0));
			}
		});

		cbxParkName.setItems(FXCollections.observableArrayList(ParksNames));

		if (WaitingListController.getSetDateFromWaitList() == 1) {
			txtdate.setValue((LocalDate) WaitingListController.getAnotherDates().get(0));
			cbxArrivelTime.setValue((String) WaitingListController.getAnotherDates().get(1));
			WaitingListController.setSetDateFromWaitList(0);
		}

		else if (flagOrder == 1) {
			this.flagOrder = 0;
			txtmemberID.setText((String) saveDetails.get(0));
			cbxParkName.setValue((String) saveDetails.get(1));
			txtdate.setValue((LocalDate) saveDetails.get(2));
			cbxArrivelTime.setValue((String) saveDetails.get(3));
			txtVisitorsNumber.setText((String) saveDetails.get(4));
			txtInvitingEmail.setText((String) saveDetails.get(5));
			txtPhoneNum.setText((String) saveDetails.get(6));
			saveDetails.clear();
		} else {
			cbxArrivelTime.getSelectionModel().selectFirst();
			txtdate.setValue(LocalDate.now());
		}

		information.setTooltip(new Tooltip(
				"In order to get a discount insert member ID or ID number\nof the person that made the order"));
		btnBack.setTooltip(new Tooltip("Don't worry your details will wait here"));

		/*********** need to do this**** for save detail after fill **********/

	}

}
