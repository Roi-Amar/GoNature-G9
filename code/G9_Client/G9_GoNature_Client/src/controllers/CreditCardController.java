package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import dataLayer.CreditCard;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
* Controller responsible for the Creditcard 
*
* @author Bar Katz
*/

public class CreditCardController implements Initializable {

	@FXML
	private Pane pnVerify;

	@FXML
	private JFXTextField txtCardNumber;

	@FXML
	private JFXComboBox<String> cbxExpiryMonth;

	@FXML
	private JFXComboBox<String> cbxExpiryYear;

	@FXML
	private JFXTextField txtHolderName;

	@FXML
	private JFXTextField txtCVV;

	@FXML
	private Button btnSave;

	private String dateCC;

	private static CreditCard details;

	private AlertController alert = new AlertController();
	
	/**
	 * Saves the user's credit card information
	 * @param ActionEvent
	 * @exception IOException
	 * 
	 */

	@FXML
	void save(ActionEvent event) throws IOException {
		if (checkNotEmptyCardFields() && chechCorrectfeilds()) {
			dateCC = cbxExpiryMonth.getValue().toString() + "/" + cbxExpiryYear.getValue().toString();

			try {

				details = new CreditCard(txtCardNumber.getText(), txtHolderName.getText(), dateCC,
						Integer.parseInt(txtCVV.getText()));

			} catch (NullPointerException e) {

				details = new CreditCard(txtCardNumber.getText(), txtHolderName.getText(), dateCC,
						Integer.parseInt(txtCVV.getText()), 0);

			}

			Stage stage = (Stage) btnSave.getScene().getWindow();
			 stage.close();
			 
			 clearDetails();
		}
		
		OrderController ORC = Context.getInstance().getOrderC();
		if(Context.getInstance().getOrderC()!=null)
			ORC.getPnPayment().setDisable(false);
	}
	
	public void clearDetails() {
		txtCardNumber.clear();
		txtHolderName.clear();
		txtCVV.clear();
	}


	public static CreditCard getDetails() {
		return details;
	}

	public static void setDetails(CreditCard details) {
		CreditCardController.details = details;
	}

	/**
	 * patterns:
	 */
	
	public static final Pattern VALIDCVV = Pattern.compile("^[0-9]{3}$", Pattern.CASE_INSENSITIVE);
	public static final Pattern VALIDCardNumber = Pattern.compile("^[0-9]{16}$", Pattern.CASE_INSENSITIVE);
	public static final Pattern VALIDName = Pattern.compile("^[A-Za-z]{1,10}+ [A-Za-z]{1,10}$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Checks that all fields of credit card details are not empty         
	 * @return true if not empty, else false
	 */

	public boolean checkNotEmptyCardFields() {
		String CardNum = txtCardNumber.getText();
		String CVC = txtCVV.getText();
		String CardName = txtHolderName.getText();

		if (CardNum.isEmpty() || CVC.isEmpty() || CardName.isEmpty()) {
			alert.setAlert("One or more of the fields are empty.\n Please fill them in and try again.");
			return false;
		}

		return true;
	}

	/**
	 * checks valid input for each nameMathod according to relevant the pattern
	 * @param nameMathod String
	 * @param txt String
	 * @return true if the pattern are correct ,false otherwise
	 */
	
	public static boolean validInput(String nameMathod, String txt) {
		Matcher matcher = null;
		if (nameMathod.equals("CardNumber")) {
			matcher = VALIDCardNumber.matcher(txt);
		} else if (nameMathod.equals("CVV")) {
			matcher = VALIDCVV.matcher(txt);
		} else if (nameMathod.equals("CardName")) {
			matcher = VALIDName.matcher(txt);
		}
		return matcher.find();
	}
	
	/**
	 * checks valid input for each fields according to relevant the pattern, if false return alert message
	 * @return true if input is correct ,false otherwise
	 */

	public boolean chechCorrectfeilds() {
		if (!validInput("CardNumber", txtCardNumber.getText())) {
			alert.setAlert("Invalid card number");
			return false;
		} else if (!validInput("CVV", txtCVV.getText())) {
			alert.setAlert("Invalid CVV");
			return false;
		} else if (!validInput("CardName", txtHolderName.getText())) {
			alert.setAlert("Invalid card holder name");
			return false;
		}
		return true;
	}

	/**
	 * Initialize all the fields 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ArrayList<String> mounthArr = new ArrayList<>();

		for (int i = 1; i <= 12; i++) {
			mounthArr.add(String.valueOf(i));
		}
		cbxExpiryMonth.setItems(FXCollections.observableArrayList(mounthArr));
		cbxExpiryMonth.getSelectionModel().selectFirst();
		ArrayList<String> yearArr = new ArrayList<>();
		for (int i = 2021; i <= 2031; i++) {
			yearArr.add(String.valueOf(i));
		}
		cbxExpiryYear.setItems(FXCollections.observableArrayList(yearArr));
		cbxExpiryYear.getSelectionModel().selectFirst();
	}

}
