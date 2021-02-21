package controllers;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * controller that coordinator all the kinds of alert that we use in the project
 *
 * @author Bar Katz Haim Azulay Hodaya Mekonen
 */

public class AlertController {
	private String result = "";

	/**
	 * showing alert message
	 * 
	 * @param msg String
	 */
	public void setAlert(String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Failed");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	/**
	 * showing success alert message
	 * 
	 * @param title String
	 * @param msg   String
	 */
	public void successAlert(String title, String msg) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	/**
	 * showing failed alert message
	 * 
	 * @param title String
	 * @param msg   String
	 */
	public void failedAlert(String title, String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	/**
	 * showing confirmation alert message
	 * 
	 * @param title String
	 * @param msg   String
	 */
	public void ensureAlert(String title, String msg) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		Optional<ButtonType> action = alert.showAndWait();

		if (action.get() == ButtonType.OK) {
			setResult("OK");
		}
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
