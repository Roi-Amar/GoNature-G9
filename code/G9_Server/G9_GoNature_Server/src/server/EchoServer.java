package server;

import java.io.IOException;
//import java.sql.Connection;
import java.util.ArrayList;

import gui.ServerController;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/**
 * 
 * @author Roi Amar
 *
 */
public class EchoServer extends AbstractServer {
	private static ServerController control;
	private static EchoServer self;

	/**
	 * 
	 * @param port    integer
	 * @param control ServerController
	 */
	public EchoServer(int port, ServerController control) {
		super(port);
		EchoServer.control = control;
		self = this;
	}

	/**
	 * connection with the client
	 * 
	 * @param msg    Object
	 * @param client ConnectionToClient
	 */
	@SuppressWarnings("static-access")
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		@SuppressWarnings("unchecked")
		ArrayList<Object> recived = (ArrayList<Object>) msg;
		WaitListSingelton.getWaitlist().CheckTheWaitList();
		switch ((String) recived.get(0)) {
		case "login":
			Login.login(recived, client);
			break;
		case "updateLoggedIn":
			Login.updateLoggedInStatus(recived, client);
			break;
		case "orderParksNameList":
			NewOrder.parksNames(recived, client);
			break;
		case "ordersByIdOrMemberId":
			ExistingOrderCheck.getOrderDetailsByOrderNumber(recived, client);
			break;
		case "ordersByOrderNumber":
			ExistingOrderCheck.getOrderDetailsByOrderNumber(recived, client);
			break;
		case "getParkDetails":
			UpdateVisitorsNumber.getParkDetails(recived, client);
			break;
		case "updateCurrentVisitors":
			UpdateVisitorsNumber.updateParkCurrentVisitors(recived, client);
			break;
		case "updateAmountArrived":
			NewOrder.updateOrderAmountArrived(recived, client);
			break;
		case "randomVisitorFakeOrder":
			NewOrder.NewReservation(recived, client);
			break;
		case "addFakeOrder":
			NewOrder.queInsert(recived, client);
			break;
		case "order":
			NewOrder.NewReservation(recived, client);
			break;
//		case "checkValidOrderNum":
//			ExistingOrderCheck.getOrderDetailsByOrderNumber(recived, client);
//			break;
		case "checkOrderForGo":
			ExistingOrderCheck.checkOrderForGo(recived, client);
			break;
		case "confirmOrder":
			NewOrder.queInsert(recived, client);
			break;
		case "editOrder":
			EditOrder.edit(recived, client);
			break;
		case "enterTheWaitList":
			WaitingList.enterTheWaitList(recived, client);
			break;
		case "cancelOrder":
			CancelOrder.cancel(recived, client);
			break;
		case "newMembershipInsert":
			NewMember.NewMemberInsert(recived, client);
			break;
		case "checkFullDays":
			EditOrder.checkFullDays(recived, client);
			break;
		case "parkManagerRequest":
			PendingMenagerRequest.InsertToPending(recived, client);
			break;
		case "getCancellationReports":
			Reports.CancellationReport(recived, client);
			break;
		case "requestForEmployeeID":
			PendingMenagerRequest.employeeNumberSet(recived, client);
			break;
		case "PendingManagerRequests":
			PendingMenagerRequest.pendingManagerRequestAllItems(recived, client);
			break;
		case "updateAccessControl":
			UpdateVisitorsNumber.updateAccessControl(recived, client);
			break;
		case "requestForParkDetails":
			PendingMenagerRequest.getParkDetails(recived, client);
			break;
		case "overallVisitorsReport":
			Reports.OverallVisitorsReport(recived, client);
			break;
		case "UsageReport":
			Reports.UsageReport(recived, client);
			break;
		case "waitlistReplay":
			Comunication.waitlistReplay(recived, client);
			break;
		case "removePendingsManagerReq":
			System.out.println("Ehco server: " + recived);
			PendingMenagerRequest.deleteFromPending(recived, client);
			break;
		case "getRegularsVisitorsData":
			Reports.VisitsReport(recived, client);
			break;
		case "getMembersVisitorsData":
			Reports.VisitsReport(recived, client);
			break;
		case "getGroupsVisitorsData":
			Reports.VisitsReport(recived, client);
			break;
		case "parkDateilsForDepartment":
			UpdateVisitorsNumber.getParkNamesAndAmountVisitorsCurrentlyInThePark(recived, client);
			break;
		case "getVisitorsEntryStatus":
			UpdateVisitorsNumber.getVisitorsEntryStatus(recived, client);
			break;
		case "revenueReport":
			Reports.incomesReport(recived, client);
			break;
		case "getVisitorsPrice":
			ParkGate.getPrice(recived, client);
			break;
		case "enterThePark":
			try {
				ParkGate.enterThePark(recived, client);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case "exitThePark":
			try {
				ParkGate.exitThePark(recived, client);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 * @param msg    ArrayList of Object
	 * @param client ConnectionToClient
	 */
	public static void sendToMyClient(ArrayList<Object> msg, ConnectionToClient client) {
		try {
			client.sendToClient(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param msg ArrayList of Object
	 */
	public static void sendToAll(ArrayList<Object> msg) {
		self.sendToAllClients(msg);
	}

	/**
	 * @param client ConnectionToClient
	 */
	@Override
	protected void clientConnected(ConnectionToClient client) {
		control.logIt("New Client conneted: " + client.toString() + " " + this.getPort());
	}

}
