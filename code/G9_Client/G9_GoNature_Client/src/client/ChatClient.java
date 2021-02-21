package client;

import java.io.IOException;
import java.util.ArrayList;

import controllers.Context;
import controllers.DepartmentManagerController;
import controllers.LoginController;
import controllers.ManageOrderController;
import controllers.OrderController;
import controllers.ParkEmployeeController;
import controllers.ParkManagerController;
import controllers.ServiceRepresentativeController;
import controllers.WaitingListConfirmContoller;
import controllers.WaitingListController;
import controllers.WelcomeController;
import ocsf.client.AbstractClient;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
	private boolean awaitResponse = false;

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param control2 clientUI The interface type variable.
	 * @exception IOException Signals that an I/O exception of some sort has
	 *                        occurred
	 * 
	 */

	public ChatClient(String host, int port, WelcomeController control2) throws IOException {
		super(host, port); // Call the superclass constructor

		System.out.println("the Client is connected to server " + host + " over port " + port);
		connect();

	}

	public void connect() {
		try {
			openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // in order to send more than one message
	}

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */

	@SuppressWarnings("unchecked")
	public void handleMessageFromServer(Object msg) {
		awaitResponse = false;
		ArrayList<Object> received = (ArrayList<Object>) msg;
		switch ((String) received.get(0)) {
		case "login":
			LoginController.receivedFromServerUserStatus((Object) received.get(1));
			break;
		case "updateLoggedIn":
			LoginController.receivedFromServerLoggedInStatus((boolean) received.get(1));
			break;
		case "orderParksNameList":
			OrderController.recivedFromServerParksNames(((ArrayList<String>) received.get(1)));
			break;
		case "ordersByIdOrMemberId":
			ParkEmployeeController.receivedFromServerOrderDetails((Object) received.get(1));
			break;
		case "ordersByOrderNumber":
			ParkEmployeeController.receivedFromServerOrderDetails((Object) received.get(1));
			break;
		case "getParkDetails":
			ParkEmployeeController.receivedFromServerParkDetails((Object) received.get(1));
			break;
//		case "updateCurrentVisitors":
//			ParkEmployeeController.receivedFromServerUpdateStatus((boolean) received.get(1));
//			break;
//		case "updateAmountArrived":
//			ParkEmployeeController.receivedFromServerUpdateStatus((boolean) received.get(1));
//			break;
//		case "randomVisitorFakeOrder":
//			ParkEmployeeController.receivedFromServerVisitorsPrice((Object) received.get(1));
//			break;
//		case "addFakeOrder":
//			ParkEmployeeController.receivedFromServerUpdateStatus((boolean) received.get(1));
//			break;
		case "order":
			OrderController.recivedFromServer((Object) received.get(1));
			break;
		case "checkOrderForGo":
			WelcomeController.recievedFromServerValidOrderAndPending((ArrayList<Object>) received);
			break;
		case "confirmOrder":
			OrderController.recivedFromServerConfirmOrder((boolean) received.get(1));
			break;
		case "editOrder":
			ManageOrderController.updatedOrderFromServer((Object) received.get(1));
			break;
		case "cancelOrder":
			ManageOrderController.canceledOrderFromServer((boolean) received.get(1));
			break;
		case "enterTheWaitList":
			WaitingListConfirmContoller.recivedfromWaitListServer((Object) received.get(1));
			break;
		case "newMembershipInsert":
			ServiceRepresentativeController.receivedFromServerAddMemberStatus((boolean) received.get(1),
					(String) received.get(2));
			break;
		case "checkFullDays":
			WaitingListController.getListDatesServer(((ArrayList<String>) received.get(1)));
			break;
		case "parkManagerRequest":
			ParkManagerController.recivedFromserver((boolean) received.get(1));
			break;
		case "requestForEmployeeID":
			ParkManagerController.recivedFromserverEmployeeID((String) received.get(1));
			break;
		case "getCancellationReports":
			DepartmentManagerController
					.receivedFromServerCancelReportsData(((ArrayList<ArrayList<String>>) received.get(1)));
			break;
		case "PendingManagerRequests":
			DepartmentManagerController.setDBList((ArrayList<ArrayList<String>>) received.get(1));
			break;
//		case "updateAccessControl":
//			ParkEmployeeController.receivedFromServerUpdateStatus((boolean) received.get(1));
//			break;
		case "requestForParkDetails":
			ParkManagerController.recivedFromserverParkDetails((Object) received.get(1));
			break;
		case "overallVisitorsReport":
			ParkManagerController.recivedFromserverVisitorsReport((ArrayList<ArrayList<String>>) received.get(1));
			break;
		case "UsageReport":
			ParkManagerController.recivedFromserverUsageReport((ArrayList<ArrayList<String>>) received.get(1));
			break;
		case "waitlistReplay":
			ManageOrderController.receviedFromserverArrivalConfirmation((ArrayList<Object>) received);
			break;
		case "removePendingsManagerReq":
			DepartmentManagerController.setData((ArrayList<ArrayList<Object>>) received.get(1));
			break;
		case "getRegularsVisitorsData":
			DepartmentManagerController.receivedFromServerRegularsVisitorsData((ArrayList<Object>) received);
			break;
		case "getMembersVisitorsData":
			DepartmentManagerController.receivedFromServerMembersVisitorsData((ArrayList<Object>) received);
			break;
		case "getGroupsVisitorsData":
			DepartmentManagerController.receivedFromServerGroupsVisitorsData((ArrayList<Object>) received);
			break;
		case "VisitorsUpdateSendToAll":
			ChatClient.sendToAllEmployee(received);
			break;
		case "parkDateilsForDepartment":
			DepartmentManagerController.setParkDetails((ArrayList<ArrayList<String>>) received.get(1));
			break;
//		case "getVisitorsEntryStatus":
//			ParkEmployeeController.receivedFromEnterAndExitStatus((String) received.get(1));
//			break;
		case "revenueReport":
			ParkManagerController.recivedFromserverRevenueReport((ArrayList<ArrayList<String>>) received.get(1));
			break;
		case "getVisitorsPrice":
			ParkEmployeeController.receivedFromServerVisitorsPrice((ArrayList<Object>) received);
			break;
		case "enterThePark":
			ParkEmployeeController.receivedFromServerEntryStatus((ArrayList<Object>) received);
			break;
		case "exitThePark":
			ParkEmployeeController.receivedFromServerExitStatus((ArrayList<Object>) received);
			break;
		default:
			break;
		}
//		feedback = "";
//		awaitResponse = false;
//		data.clear();
//		if (msg instanceof ArrayList<?>) {
//			@SuppressWarnings("unchecked")
//			ArrayList<ArrayList<String>> got = (ArrayList<ArrayList<String>>) msg;
//			// data.addAll(got);
//			setData(got);
//			System.out.println("got data");
//			feedback = "positive";
//		} else {
//			String answer = (String) msg;
//			System.out.println("--> handleMessageFromServer");
//			System.out.println(answer);
//			if (!answer.contains("error"))
//				feedback = "positive";
//		}
//		/// add send to GUI

	}

	/**
	 * This method handles all data coming from the UI.
	 *
	 * @param message The message from the UI.
	 */

	public void handleMessageFromClientUI(ArrayList<Object> message) {

		try {
			sendToServer(message);
		} catch (IOException e) {
			System.out.println(e);
			System.out.println("Could not send message to server.  Terminating client.");
			quit();
		}
		awaitResponse = true;
		while (awaitResponse) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//		if (!feedback.equals("positive")) {
//			System.out.println("something went wrong");
//		}

	}

	/**
	 * Update the current amount of visitor in this current day. comes after update
	 * from service represintetive send to all employees
	 * 
	 * @param arr cell[0] - park name cell[1] -Visitors Number.s
	 */
	@SuppressWarnings("static-access")
	public static void sendToAllEmployee(ArrayList<Object> arr) {
		String parkName = (String) arr.get(1);
		String updatedVisitorsNumber = (String) arr.get(2);
		if (Context.getInstance().getPMC() != null) {
			if (Context.getInstance().getPMC().getParkName().equals(parkName))
				Context.getInstance().getPMC().setUpdatedCurrentVisitors(updatedVisitorsNumber);
		}

		if (Context.getInstance().getPEC() != null) {
			if (Context.getInstance().getPEC().getParkName().equals(parkName))
				Context.getInstance().getPEC().setCurrentVisitors(updatedVisitorsNumber);
		}

		if (Context.getInstance().getDMC() != null) {
			Context.getInstance().getDMC().setCurrentVisitors(arr);
		}

	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
			System.out.println("did not closed connection");
		}
		System.exit(0);
	}

}
