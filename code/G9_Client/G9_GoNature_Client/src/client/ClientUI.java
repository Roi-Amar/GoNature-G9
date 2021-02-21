package client;

import java.io.IOException;
import java.util.ArrayList;
import controllers.WelcomeController;
import javafx.application.Application;
import javafx.stage.Stage;


public class ClientUI extends Application {
	private static ChatClient client;

	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	public void start(Stage primaryStage) throws Exception {
		
		WelcomeController startFrame = new WelcomeController();
		try {
			client = new ChatClient("localhost", 5555, startFrame);
		} catch (IOException exception) {
			System.out.println("Error: Can't setup connection! Terminating client.");
			System.exit(1);
		}
		startFrame.start(primaryStage);		
	}
	
	public static void sentToChatClient(ArrayList<Object> message) {
		client.handleMessageFromClientUI(message);
	}
}
