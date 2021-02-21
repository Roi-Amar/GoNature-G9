package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * controller include all three screen of success order
 *
 * @author Bar Katz
 */

public class TermsConditions {

    @FXML
    private Pane pnResetPassword;

    @FXML
    private Button btnContinue;

    
    /**
     * button continue for the screen
     * @param event ActionEvent
     */
    @FXML
    void btnCon(ActionEvent event) {
    	Stage stage = (Stage) btnContinue.getScene().getWindow();
		OrderController ORC = Context.getInstance().getOrderC();
		ORC.getPnPayment().setDisable(false);
		stage.close();
    }

}
