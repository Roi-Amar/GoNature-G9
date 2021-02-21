package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import server.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerController implements Initializable {
	
    @FXML
    private Button btnConnect;

    @FXML
    private TextField dbhost;

    @FXML
    private Button btnDisonnect;

    @FXML
    private TextField dbusername;

    @FXML
    private PasswordField dbpass;

    @FXML
    private TextField dbscheme;

    @FXML
    private TextField dbport;

    @FXML
    private Button btnStart;

    @FXML
    private Button stopServer;

    @FXML
    private TextField serverport;
    
    @FXML
    private TextArea logUpdate;

    @FXML
    void connect(ActionEvent event) {
		ArrayList<String> str = new ArrayList<String>();
		str.add(getHost());
		str.add(getDBport());
		str.add(getScheme());
		str.add(getUsername());
		str.add(getPassword());
		if(ServerUI.connectToDB(str)) {
			btnDisonnect.setDisable(false);
			btnConnect.setDisable(true);
			logIt("DB connected");
		}
		else
			logIt("DB not connected");
    }

    @FXML
    void disonnect(ActionEvent event) {
    	if(ServerUI.disconnectFromDB()) {
			btnDisonnect.setDisable(true);
			btnConnect.setDisable(false);
			logIt("DB disconnected");
		} else
			logIt("DB still connected");
    }

    @FXML
    void startServerBtn(ActionEvent event) {
    	EchoServer sv = new EchoServer(Integer.parseInt(getServerPort()),this);
    	if (ServerUI.runServer(sv)) {
	    	stopServer.setDisable(false);
	    	btnStart.setDisable(true);
	    	logIt("goNature server up and running");
    	} else //CHECK IF DBUP ERROR
    		logIt("ERROR - Could not listen for clients!");
    	
    }

    @FXML
    void stopServerBtn(ActionEvent event) {
    	if (ServerUI.stopServer()) {
    		stopServer.setDisable(true);
    		btnStart.setDisable(false);
    		Login.disconnectAllUsers();
			System.out.println("All users have been disconnected.");
    		logIt("Stoped Server succsefuly");
		} else
			logIt("goNature server still up");
    }
    
    public void logIt(String str) {
    	logUpdate.appendText(str+"\n");
    }
	
	public String getHost() {
		return dbhost.getText();
	}
	
	public String getUsername() {
		return dbusername.getText();
	}
	
	public String getPassword() {
		return dbpass.getText();
	}
	
	public String getDBport() {
		return dbport.getText();
	}
	
	public String getScheme() {
		return dbscheme.getText();
	}
	
	public String getServerPort() {
		return serverport.getText();
	}
	
	public void start(Stage primaryStage) throws Exception {	
		Parent root = FXMLLoader.load(getClass().getResource("/gui/Server.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Server");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				System.out.println("emergency exit");
				if (ServerUI.isServerUP() && ServerUI.isDBup()) {
					Login.disconnectAllUsers();
					System.out.println("All users have been disconnected.");
				}
				if(ServerUI.isServerUP())
					ServerUI.stopServer();
				if(ServerUI.isDBup())
					ServerUI.disconnectFromDB();
				Platform.exit();
				System.exit(0);
			}
		});
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		dbhost.setText("localhost"); 
		dbusername.setText("root");
		dbpass.setText("NewP@ssword4theSQL");
		dbscheme.setText("g9_gonature");
		dbport.setText("3306");
		serverport.setText("5555");
		btnDisonnect.setDisable(true);
		stopServer.setDisable(true);
	}
	
	
}
