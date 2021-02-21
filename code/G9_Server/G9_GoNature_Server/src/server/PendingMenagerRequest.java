package server;

import java.io.Serializable;
import java.util.ArrayList;

import dataLayer.Park;
import managerData.ManagerRequest;
import ocsf.server.ConnectionToClient;

/**
 * The PendingMenagerRequest program deals with managers requests
 *
 * @author Anastasia Kokin
 */

public class PendingMenagerRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Inserts To Pending table in DB. send to client ArrayList of Object : cell[0]
	 * name ,cell[1] T/F
	 * 
	 * @param recived ArrayList of Object: cell[0] name, cell[1] ManagerRequest
	 *                object
	 * @param client  ConnectionToClient
	 */
	public static void InsertToPending(ArrayList<Object> recived, ConnectionToClient client) {

		ArrayList<Object> answer = new ArrayList<Object>();
		// the service name
		answer.add(recived.get(0));

		ManagerRequest mr = (ManagerRequest) recived.get(1);
		if (!mr.getRequestType().equals("discount")) {
			mr.setFromDate("01-01-00 00:00:00");
			mr.setToDate("01-01-00 00:00:00");
		}
		// select pending requests and to check if not more than 1 in every type
		ArrayList<String> query1 = new ArrayList<String>();
		query1.add("select"); // command
		query1.add("pendingmanagerrequests"); // table name
		query1.add("*"); // columns to select from
		query1.add("WHERE requesttype='" + mr.getRequestType() + "' AND employeeID='" + mr.getEmployeeID() + "'");
		query1.add("8");
		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query1);
		if (!queryData.isEmpty()) {
			answer.add(false);
		}
		// insert if possible
		else {
			ArrayList<String> query2 = new ArrayList<String>();
			query2.add("insert"); // command
			query2.add("pendingmanagerrequests"); // table name
			query2.add(toStringForDB(mr)); // values in query format
			if (!MySQLConnection.insert(query2)) {
				answer.add(false);
			} else {
				answer.add(true);

			}

		}
		EchoServer.sendToMyClient(answer, client);
	}

	/**
	 * select the emloyeeID by his user name and password sends to client F if no
	 * such employee OR employeeID sends to client : ArrayList of Object: cell[0]
	 * name, cell[1] false if no such employee OR employeeID
	 * 
	 * @param recived ArrayList of Object: cell[0] name, cell[1] user_name, cell[2]
	 *                password
	 * @param client  ConnectionToClient
	 */

	public static void employeeNumberSet(ArrayList<Object> recived, ConnectionToClient client) {

		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		String username = (String) recived.get(1);
		String password = (String) recived.get(2);
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("employee"); // table name
		query.add("employeeNumber"); // columns to select from
		query.add("WHERE username='" + username + "' AND password='" + password + "'"); // condition
		query.add("1"); // how many columns returned

		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		if (queryData.isEmpty()) {
			System.out.println("no such employee");
			answer.add(false);
		} else {
			answer.add(queryData.get(0).get(0));
		}

		EchoServer.sendToMyClient(answer, client);
	}

	/**
	 * pulling details of a selected park from DB. sends to client: ArrayList of
	 * Object cell[0] func_name, cell[1] Park Object
	 * 
	 * @param recived ArrayList of Object: cell[0] func_name, cell[1]: ArrayList of
	 *                String cell[0] park name
	 * @param client  ConnectionToClient
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static void getParkDetails(ArrayList<Object> recived, ConnectionToClient client) {
		// query
		ArrayList<Object> answer = new ArrayList<Object>();
		// the service name : getParkDetails
		answer.add(recived.get(0));
		// the data that sent from the client

		// cell 0: parkName
		ArrayList<String> data = (ArrayList<String>) recived.get(1);
		NewOrder.updatingParkCurrentPrice(data.get(0));
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("park"); // table name
		query.add("*"); // columns to select from
		query.add("WHERE parkName = '" + data.get(0) + "'"); // condition
		query.add("6"); // how many columns returned
		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		Park park = new Park(queryData.get(0));

		answer.add(park);
		EchoServer.sendToMyClient(answer, client);
	}

	/**
	 * pulls all items from pending manager request. sends to client ArrayList of
	 * ArrayList of String of all items in pending manager request
	 * 
	 * @param recived ArrayList of Object: cell[0] name
	 * @param client  ConnectionToClient
	 */
	public static void pendingManagerRequestAllItems(ArrayList<Object> recived, ConnectionToClient client) {

		ArrayList<Object> answer = new ArrayList<Object>();
		// the service name
		answer.add(recived.get(0));
		// select pending requests and to check if not more than 1 in every type
		ArrayList<String> query1 = new ArrayList<String>();
		query1.add("select"); // command
		query1.add("pendingmanagerrequests"); // table name
		query1.add("*"); // columns to select from
		query1.add("");
		query1.add("8");
		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query1);
		answer.add(queryData);
		EchoServer.sendToMyClient(answer, client);
	}

	/**
	 * deletes approved and disapproved managers requests and executes them in their
	 * DB, sends to client: ArrayList of Object: cell[0] calling function name,
	 * (Updated upstream cell[1] ArrayList of ArrayList of Object = call[0 to n] =
	 * ArrayList of Object = cell[0] = request cell[1] = True if deleteCond success,
	 * false if failed cell[2] = false if discount on this day or failed to insert
	 * DB, true ======= cell[1] ArrayList of ArrayList of Object = call[0 to n] =
	 * ArrayList of Object = cell[0] = request cell[1] = True if deleteCond success,
	 * false if failed cell[2] = false if discount on this day or failed to insert
	 * DB, true ) Stashed changes otherwise
	 * 
	 * @param recived ArrayList of Object : cell[0] = String
	 *                removePendingsManagerReq cell[1] = ArrayList of ArrayList of
	 *                Object = cell[0 to n] = ArrayList Object = cell[0]
	 *                ManagerRequest object cell[1] yes/no
	 * @param client  ConnectionToClient
	 * 
	 */
	public static void deleteFromPending(ArrayList<Object> recived, ConnectionToClient client) {
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		@SuppressWarnings("unchecked")
		ArrayList<ArrayList<Object>> reqTable = (ArrayList<ArrayList<Object>>) recived.get(1);
		ArrayList<ArrayList<Object>> retTable = new ArrayList<ArrayList<Object>>();

		for (ArrayList<Object> req : reqTable) {
			ArrayList<Object> ret = new ArrayList<Object>();
			ManagerRequest mr = (ManagerRequest) req.get(0);
			ret.add(mr);
			String yesno = (String) req.get(1);
			// delete from pendingmanagerrequests
			ArrayList<String> query = new ArrayList<String>();
			query.add("deleteCond");
			query.add("pendingmanagerrequests");
			query.add("employeeID ='" + mr.getEmployeeID() + "' AND requesttype='" + mr.getRequestType() + "'");
			if (yesno.equals("no")) {
				ret.add(MySQLConnection.deleteCond(query));
				ret.add(true);
				retTable.add(ret);
				continue;
			}

			else {
				ArrayList<String> query1 = new ArrayList<String>();
				query1.add("select"); // command
				query1.add("pendingmanagerrequests"); // table name
				query1.add("*"); // columns to select from
				query1.add(
						"WHERE employeeID ='" + mr.getEmployeeID() + "' AND requesttype='" + mr.getRequestType() + "'");
				query1.add("8");
				ArrayList<ArrayList<String>> queryData1 = MySQLConnection.select(query1);
				mr = new ManagerRequest(queryData1.get(0));
				ret.add(MySQLConnection.deleteCond(query));/////////////////////////////////////////////////////////////////

				if (mr.getRequestType().equals("discount")) {
					String dateCond = "NOT ( GREATEST('" + mr.getFromDate() + "','" + mr.getToDate()
							+ "') < discounts.from" + " OR LEAST('" + mr.getFromDate() + "','" + mr.getToDate()
							+ "') > discounts.to)";
					// search if the dates already exist
					ArrayList<String> query2 = new ArrayList<String>();
					query2.add("select"); // command
					query2.add("discounts"); // table name
					query2.add("*"); // columns returned
					query2.add("WHERE parkName ='" + mr.getParkName() + "' AND " + dateCond);
					query2.add("5");
					ArrayList<ArrayList<String>> queryData2 = MySQLConnection.select(query2);
					if (!queryData2.isEmpty()) {
						System.out.println("Already has discount on these dates!");
						ret.add(false);
						retTable.add(ret);
						continue;
					} else {

						ArrayList<String> query3 = new ArrayList<String>();
						query3.add("insert"); // command
						query3.add("discounts");
						query3.add(toStringForDBDiscounts(mr));
						ret.add(MySQLConnection.insert(query3));
						retTable.add(ret);
						continue;
					}

				}

				if (mr.getRequestType().equals("max_c")) {
					// ret.add(MySQLConnection.deleteCond(query));///////////////////////////////////////////////////////////////////////////////////

					ArrayList<String> query2 = new ArrayList<String>();
					query2.add("update"); // command
					query2.add("park"); // table name
					query2.add("maxVisitorAmount='" + mr.getMaxCapacity() + "'");
					query2.add("parkName");
					query2.add(mr.getParkName());
					ret.add(MySQLConnection.update(query2));
					retTable.add(ret);
					continue;
				}

				if (mr.getRequestType().equals("max_o")) {
					ret.add(MySQLConnection.deleteCond(query));
					ArrayList<String> query2 = new ArrayList<String>();
					query2.add("update"); // command
					query2.add("park"); // table name
					query2.add("maxOrderVisitorsAmount='" + mr.getOrdersCapacity() + "'");
					query2.add("parkName");
					query2.add(mr.getParkName());
					ret.add(MySQLConnection.update(query2));
					retTable.add(ret);
					continue;
				}
			}

		}
		answer.add(retTable);
		EchoServer.sendToMyClient(answer, client);
	}

	/**
	 * toString in use for ManagerRequest query
	 * 
	 * @param mr ManagerRequest
	 * @return String
	 */
	public static String toStringForDB(ManagerRequest mr) {
		return "'" + mr.getEmployeeID() + "','" + mr.getRequestType() + "','" + mr.getMaxCapacity() + "','"
				+ mr.getOrdersCapacity() + "','" + mr.getDiscount() + "','" + mr.getFromDate() + "','" + mr.getToDate()
				+ "','" + mr.getParkName() + "'";
	}

	/**
	 * toString in use for Discounts query
	 * 
	 * @param mr ManagerRequest
	 * @return String
	 */
	public static String toStringForDBDiscounts(ManagerRequest mr) {

		return "'" + mr.getParkName() + "','" + mr.getFromDate() + "','" + mr.getToDate() + "','"
				+ Counter.getCounter().discountsIDcountNum() + "','" + mr.getDiscount() + "'";
	}

}
