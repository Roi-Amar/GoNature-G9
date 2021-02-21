package controllers;

import java.io.IOException;
import java.util.ArrayList;

import com.jfoenix.controls.JFXCheckBox;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * controller handles the confirmation Entry to the waiting list
 * 
 * @author Bar Katz
 *
 */

public class WaitingListConfirmContoller {

	@FXML
	private Pane pnResetPassword;

	@FXML
	private Button btnListMe;

	@FXML
	private JFXCheckBox agree;

	private AlertController alert = new AlertController();

	private static boolean status = false;
	private static String msg = "not";

	public static String getMsg() {
		return msg;
	}

	public static void setMsg(String msg) {
		WaitingListConfirmContoller.msg = msg;
	}

	public static boolean isStatus() {
		return status;
	}

	public static void setStatus(boolean status) {
		WaitingListConfirmContoller.status = status;
	}

	/**
	 * button ListMe - client's approve for enter to wait list
	 * 
	 * @param ActionEvent event
	 * @throws IOException
	 */
	
	@FXML
	void listMe(ActionEvent event) throws IOException {
		ArrayList<Object> msgWaitList = new ArrayList<>();
		msgWaitList.add("enterTheWaitList");
		msgWaitList.add(OrderController.getOrder());
		if (checkAgreeRadioBtn()) {
			ClientUI.sentToChatClient(msgWaitList);

			if (WaitingListConfirmContoller.status) {
				WaitingListConfirmContoller.status = false;
				alert.ensureAlert("Success!",
						"you are in our wait list we will send you an Email and SMS notification as soon as a spot is available."
								+ "\nbe sure to answer within one hour or you will lose your spot in the line.");
			} else if (msg.equals("alreadyExist")) {
				alert.ensureAlert("information", "You are already in the waiting list");
			} else
				alert.failedAlert("Failed!",
						"something went wrong, our code monkey has been notified and will work on the error, please try again shortly. ");
			if (alert.getResult().equals("OK")) {
				Stage stage = (Stage) btnListMe.getScene().getWindow();
				OrderController ORC = Context.getInstance().getOrderC();
				ORC.getPnOrder().setDisable(false);
				ORC.back(event);
				stage.close();

			}

		}

	}

	/**
	 * receives from server the status of the entry confirmation to the waiting list 
	 * 
	 * @param status Object
	 */
	
	public static void recivedfromWaitListServer(Object status) {
		if (status instanceof Boolean) {
			Boolean flag1 = (boolean) status;
			setStatus(flag1);
		} else {
			String flag2 = (String) status;
			setMsg(flag2);
		}

	}

	/**
	 * check the radio button to agree the terms
	 * 
	 * @return T/F
	 */
	
	public boolean checkAgreeRadioBtn() {
		if (agree.isSelected()) {
			return true;
		}
		alert.setAlert("you need to aprove the terms");
		return false;
	}

}
