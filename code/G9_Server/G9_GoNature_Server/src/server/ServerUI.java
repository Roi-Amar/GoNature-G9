package server;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import gui.ServerController;

/**
 * The ServerUI program starts the server and shows its status
 *
 * @author Roi Amar
 */

public class ServerUI extends Application {
	final public static int DEFAULT_PORT = 5555;
	private static boolean DBup = false;
	private static boolean serverUP = false;
	private static ServerController control;
	private static EchoServer echoServ;

	public static void main(String args[]) throws Exception {
		launch(args);
	}

	/**
	 * 
	 * @param primaryStage Stage
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		control = new ServerController();
		control.start(primaryStage);
	}

	/**
	 * runs the server
	 * 
	 * @param sv EchoServer
	 * @return T/F
	 */
	public static boolean runServer(EchoServer sv) {
		if (DBup) {
			ServerUI.echoServ = sv;
			try {
				sv.listen();
			} catch (Exception ex) {
				return false;
				// setMsg("ERROR - Could not listen for clients!");
			}
			serverUP = true;
			return true;
		} else {
			return false;
			// setMsg("Please Start DB first");
		}
	}

	/**
	 * stops the server
	 * 
	 * @return T/F
	 */
	public static boolean stopServer() {
		try {
			echoServ.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		serverUP = false;
		return true;
	}

	/**
	 * sets connection to DB
	 * 
	 * @param data ArrayList of String
	 * @return T/F
	 */
	public static boolean connectToDB(ArrayList<String> data) {
		if (MySQLConnection.connectToDB(data)) {
			DBup = true;
			return true;
		}
		return false;
	}

	/**
	 * Disconnects from DB
	 * 
	 * @return T/F
	 */
	public static boolean disconnectFromDB() {
		if (serverUP) {
			return false;
			// SERVER UP FAILURE MSG
		}
		if (MySQLConnection.disconnectFromDB()) {
			DBup = false;
			return true;
		}
		return false;
	}

	/**
	 * checks if the DB is up
	 * 
	 * @return T/F
	 */
	public static boolean isDBup() {
		return DBup;
	}

	/**
	 * checks if the server is up
	 * 
	 * @return T/F
	 */
	public static boolean isServerUP() {
		return serverUP;
	}

}
