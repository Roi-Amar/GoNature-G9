package server;

import java.util.ArrayList;
import dataLayer.Park;
import ocsf.server.ConnectionToClient;

/**
 * The UpdateVisitorsNumber program generates all entry and exit functions also
 * controls the visitors amount in park and responsible for its update
 *
 * @author Haim Azulay, Roi Amar, Anastasia Kokin
 */

public class UpdateVisitorsNumber {

	/**
	 * updates the ParkCurrentVisitors in Park table. sends to client ArrayList of
	 * Object = cell[0] function name, cell[1] T if update succeeded, F if not,
	 * Full if we can add more visitors
	 * 
	 * @param recived ArrayList of Object cell [0]: parkName, cell [1]:
	 *                currentVisitoreAmount
	 * @param client  ConnectionToClient
	 *
	 */

	@SuppressWarnings("unchecked")
	public static void updateParkCurrentVisitors(ArrayList<Object> recived, ConnectionToClient client) {
		ArrayList<Object> answer = new ArrayList<Object>();
		// the service name : updateCurrentVisitors
		answer.add(recived.get(0));
		// currentVisitoreAmount to update
		ArrayList<String> data = (ArrayList<String>) recived.get(1);

		ArrayList<String> query = new ArrayList<String>();
		query.add("update"); // command
		query.add("park"); // table name
		query.add("currentVisitoreAmount = '" + data.get(1) + "'"); // columns to update
		query.add("parkName"); // condition
		query.add(data.get(0)); // parkName value

		answer.add(MySQLConnection.update(query));
		EchoServer.sendToMyClient(answer, client);

		ArrayList<Object> answer2 = new ArrayList<Object>();
		answer2.add("VisitorsUpdateSendToAll");
		answer2.add(data.get(0));// park name
		answer2.add(data.get(1));// updatedVisitorsNumber
		EchoServer.sendToAll(answer2);
	}

	/**
	 * information depending on entry or exit status. sends to
	 * client:rrayList of Object = cell[0] function name // cell[1] T if update
	 * succeeded, F if not, Full if we can add more visitors
	 * 
	 * @param recived ArrayList of Object [0]: case name updateAccessControl cell [1]:
	 *                cell [0]: orderNumber, cell [1]: timeEnter / timeExit cell
	 *                [2]: parkName, cell [3]: orderType, cell [4]: amountArrived
	 * @param client  ConnectionToClient
	 */
	@SuppressWarnings("unchecked")
	public static void updateAccessControl(ArrayList<Object> recived, ConnectionToClient client) {
		// query
		ArrayList<Object> answer = new ArrayList<Object>();
		// the service name : updateAccessControl
		answer.add(recived.get(0));
		// information depending on entry or exit status
		ArrayList<String> data = (ArrayList<String>) recived.get(1);
		ArrayList<String> query = new ArrayList<String>();

		// enter mode: we need to insert all
		if (!checkIfOrderNumberExists(data.get(0))) {
			query.add("insert"); // command
			query.add("enteryandexit"); // table name
			query.add(data.get(0) + ", '" + data.get(1) + "', " + null + ", '" + data.get(2) + "', '" + data.get(3)
					+ "', " + data.get(4));

			answer.add(MySQLConnection.insert(query));
			EchoServer.sendToMyClient(answer, client);
			return;
		}

		query.add("update"); // command
		query.add("enteryandexit"); // table name
		query.add("timeExit = '" + data.get(1) + "'"); // columns to update
		query.add("orderNumber"); // condition
		query.add(data.get(0)); // orderNumber value

		answer.add(MySQLConnection.update(query));
		EchoServer.sendToMyClient(answer, client);
	}

	/**
	 * check if the order number exists in the table access control
	 * 
	 * @param orderNumber String
	 * @return T/F
	 */
	public static boolean checkIfOrderNumberExists(String orderNumber) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("enteryandexit"); // table name
		query.add("orderNumber"); // columns to select from
		query.add("WHERE orderNumber = '" + orderNumber + "'"); // condition
		query.add("1"); // how many columns returned

		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		if (queryData.isEmpty()) {
			return false;
		}

		return true;
	}

	/**
	 * pulling details of a selected park from DB. sends to client :
	 * ArrayList of Object = cell[0] function name, cell[1] ArrayList of String = [0]
	 * parkName, [1] number of visitors to add
	 * 
	 * @param recived ArrayList of Object cell[0]: calling function name
	 * @param client ConnectionToClient
	 */
	@SuppressWarnings("unchecked")
	public static void getVisitorsEntryStatus(ArrayList<Object> recived, ConnectionToClient client) {
		// query
		ArrayList<Object> answer = new ArrayList<Object>();
		// the service name : getVisitorsEntryStatus
		answer.add(recived.get(0));
		// the data that sent from the client
		// cell 0: orderNumber
		ArrayList<String> data = (ArrayList<String>) recived.get(1);
		int orderNumber = Integer.parseInt(data.get(0));

		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("enteryandexit"); // table name
		query.add("*"); // columns to select from
		query.add("WHERE orderNumber = '" + orderNumber + "'"); // condition
		query.add("6"); // how many columns returned

		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);

		if (queryData.isEmpty()) {
			answer.add("Didn't enter");
		} else {
			if (queryData.get(0).get(2) == null) {
				answer.add("Entered");
			} else {
				answer.add("Leaved");
			}
		}

		EchoServer.sendToMyClient(answer, client);
	}

	/**
	 * pulling details of a selected park from DB. sends to client ArrayList of
	 * Object= cell[0] function name, cell[1] ArrayList of String = [0] parkName, [1]
	 * number of visitors to add
	 * 
	 * @param recived ArrayList of Object cell[0]: calling function name
	 * @param client  ConnectionToClient
	 */
	@SuppressWarnings("unchecked")
	public static void getParkDetails(ArrayList<Object> recived, ConnectionToClient client) {
		ArrayList<Object> answer = new ArrayList<Object>();
		// the service name : getParkDetails
		answer.add(recived.get(0));
		// the data that sent from the client
		// cell 1: parkName
		ArrayList<String> data = (ArrayList<String>) recived.get(1);
//		int visitorsToAddorRemove = Integer.parseInt(data.get(1));

		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("park"); // table name
		query.add("*"); // columns to select from
		query.add("WHERE parkName = '" + data.get(0) + "'"); // condition
		query.add("6"); // how many columns returned

		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		Park park = new Park(queryData.get(0));

//		if (park.getCurrentAmount() + visitorsToAddorRemove > park.getMaximumCapacityInPark()) {
//			answer.add("Greater");
//		} else if (park.getCurrentAmount() - visitorsToAddorRemove < 0) {
//			answer.add("Lower");
//		} else if (park.getCurrentAmount() == park.getMaximumCapacityInPark()) {
//			answer.add("Full");
//		} else {
			answer.add(park);
//		}

		EchoServer.sendToMyClient(answer, client);
	}

	/**
	 * sends to client ArrayList of ArrayList of String of all park names and
	 * current visitors amount and max visitors amount
	 * 
	 * @param recived ArrayList of Object cell[0]: calling function name
	 * @param client  ConnectionToClient
	 */
	public static void getParkNamesAndAmountVisitorsCurrentlyInThePark(ArrayList<Object> recived,
			ConnectionToClient client) {
		// query
		ArrayList<Object> answer = new ArrayList<Object>();
		// the service name : getParkNamesAndAmountVisitorsCurrentlyInThePark
		answer.add(recived.get(0));
		// the data that sent from the client

		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("park"); // table name
		query.add("parkName, currentVisitoreAmount, maxVisitorAmount"); // columns to select from
		query.add(""); // condition
		query.add("3"); // how many columns returned

		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		answer.add(queryData);
		EchoServer.sendToMyClient(answer, client);
	}

}
