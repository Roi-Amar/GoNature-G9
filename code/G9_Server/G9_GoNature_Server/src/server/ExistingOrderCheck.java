package server;

import java.util.ArrayList;

import ocsf.server.ConnectionToClient;
import orderData.Order;

/**
 * The ExistingOrderCheck program checks if there is an existing order
 *
 * @author Roi Amar
 */

public class ExistingOrderCheck {

	/**
	 * send to client: Order Class if an order found, string "No such order" if not
	 * 
	 * @param recived ArrayList of Object [0] String ordersByIdOrMemberId OR
	 *                ordersByOrderNumber [1] ArrayList of String contains: [0]
	 *                orderNumber
	 * @param client  ConnectionToClient
	 */
	public static void getOrderDetailsByOrderNumber(ArrayList<Object> recived, ConnectionToClient client) {
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		ArrayList<ArrayList<String>> queryData = fechOrder(recived, "orders", "orderNumber");
		if (queryData.isEmpty()) {
			answer.add("No such order");
		} else {
			Order kuku = new Order(queryData.get(0));
			answer.add(kuku);

			if (ParkGate.orderWasUsed(kuku))
				answer.add("Order is already used");
		}

		EchoServer.sendToMyClient(answer, client);
	}

	/**
	 * 
	 * @param recived   array list of object contains: [0] String
	 *                  ordersByIdOrMemberId OR ordersByOrderNumber [1] ArrayList of
	 *                  String contains: [0] orderNumber
	 * @param tableName table name as string
	 * @param colName   column name as string
	 * @return ArrayList of ArrayList of String containing the order details, empty
	 *         if no details
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<ArrayList<String>> fechOrder(ArrayList<Object> recived, String tableName, String colName) {
		ArrayList<String> data = (ArrayList<String>) recived.get(1);
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add(tableName); // table name
		query.add("*"); // columns to select from
		if (recived.get(0).equals("ordersByIdOrMemberId")) { // ordersByIdOrMemberId case
			query.add("WHERE ID = '" + data.get(0) + "' OR memberId = '" + data.get(0) + "'"); // condition
		} else { // ordersByOrderNumber case
			query.add("WHERE " + colName + "='" + data.get(0) + "'"); // condition
		}
		query.add("12"); // how many columns returned
		return MySQLConnection.select(query);
	}

	/**
	 * 
	 * @param recived   array list of object contains: [0] String
	 *                  ordersByIdOrMemberId OR ordersByOrderNumber [1] ArrayList of
	 *                  String contains: [0] orderNumber
	 * @param tableName table name as string
	 * @param parkName  park name as string
	 * @return ArrayList of ArrayList of String containing the order details, empty
	 *         if no details
	 */
	public static ArrayList<ArrayList<String>> fechOrderTodayInPark(ArrayList<Object> recived, String tableName,
			String parkName) {
		@SuppressWarnings("unchecked")
		ArrayList<String> data = (ArrayList<String>) recived.get(1);
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add(tableName); // table name
		query.add("*"); // columns to select from
		if (recived.get(0).equals("ordersByIdOrMemberId")) { // ordersByIdOrMemberId case
			query.add("WHERE (ID = '" + data.get(0) + "' OR memberId = '" + data.get(0) + "') AND parkName='" + parkName
					+ "' AND DATE(arrivedTime)=CURDATE()"); // condition
		} else { // ordersByOrderNumber case
			return null;
		}
		query.add("12"); // how many columns returned
		return MySQLConnection.select(query);
	}

	/**
	 * checks if order is pending
	 * 
	 * @param orderNum String
	 * @return boolean true if exist in pending wait list false if not
	 */
	public static boolean checkIfPending(String orderNum) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("pendingwaitlist"); // table name
		query.add("*"); // columns to select from
		query.add("WHERE orderNumber='" + orderNum + "'");
		query.add("2");
		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		return (!queryData.isEmpty());
	}

	/**
	 * send to client: array list of object contains: [0] String checkOrderForGo [1]
	 * Order Class if an order found, string "No such order" if not [2] (if an order
	 * is found only) True if the order is in pending wait list table
	 * 
	 * @param recived ArrayList of Object array list of object contains: [0] String
	 *                checkOrderForGo [1] ArrayList of String contains: [0]
	 *                orderNumber
	 * @param client  ConnectionToClient
	 */
	public static void checkOrderForGo(ArrayList<Object> recived, ConnectionToClient client) {
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		@SuppressWarnings("unchecked")
		String orderNum = ((ArrayList<String>) recived.get(1)).get(0);
		ArrayList<ArrayList<String>> queryData = fechOrder(recived, "orders", "orderNumber");
		if (queryData.isEmpty()) {
			answer.add("No such order");
		} else {
			Order kuku = new Order(queryData.get(0));
			answer.add(kuku);
			answer.add(checkIfPending(orderNum));
		}
		EchoServer.sendToMyClient(answer, client);
	}

}
