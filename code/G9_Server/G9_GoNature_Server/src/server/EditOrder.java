package server;

import java.io.IOException;
import java.util.ArrayList;

import ocsf.server.ConnectionToClient;
import orderData.Order;

/**
 * The EditOrder program editing an existing order
 *
 * @author Roi Amar
 */

public class EditOrder {

	/**
	 * changes can be made only for ArivelTime and VisitorsNumber parameters send to
	 * client: ArrayList of objects that contains: [0] - String "EditOrder" [1] -
	 * true if update successful, false if not
	 * 
	 * @param recived ArrayList of Object
	 * @param client  ConnectionToClient
	 */
	@SuppressWarnings("static-access")
	public static void edit(ArrayList<Object> recived, ConnectionToClient client) {
		WaitingList waitlist = new WaitingList(); // see if even needed
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		Order newOrder = (Order) recived.get(1);
		Order originalOrder = (Order) recived.get(2);
		Order stubOrder = new Order(newOrder);
		ArrayList<Object> objectArrayStub = new ArrayList<Object>();
		ArrayList<Object> stubForAvilableSpots = new ArrayList<Object>();
		objectArrayStub.add(recived.get(0));
		stubForAvilableSpots.add(recived.get(0));
		newOrder = NewOrder.totalPrice(newOrder, NewOrder.MemerCheck(newOrder), false);
		if (newOrder.getArrivedTime().equals(originalOrder.getArrivedTime())) { // if same time
			if (newOrder.getVisitorsNumber() < originalOrder.getVisitorsNumber()) { // if less than original
				updateVisitorsNumber(originalOrder.getOrderNumber(), newOrder.getVisitorsNumber());
				stubOrder.setVisitorsNumber(originalOrder.getVisitorsNumber() - newOrder.getVisitorsNumber()); // will
																												// stay
																												// positive
																												// cause
																												// of
																												// the
																												// if
				objectArrayStub.add(stubOrder);
				waitlist.pullFromWaitList(recived); //// fix the wait list
			} else { // if more than original
				stubOrder.setVisitorsNumber(newOrder.getVisitorsNumber() - originalOrder.getVisitorsNumber()); // will
																												// stay
																												// positive
																												// cause
																												// of
																												// the
																												// if
				stubForAvilableSpots.add(stubOrder);
				if (waitlist.checkForAvailableSpots(stubForAvilableSpots)) {
					updateVisitorsNumber(originalOrder.getOrderNumber(), newOrder.getVisitorsNumber());
				} else { // nospots to expand
					answer.add(false);
					try {
						client.sendToClient(answer);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
			}
			updatePrice(newOrder);
			answer.add(true);
		} else {
			stubForAvilableSpots.add(newOrder);
			if (waitlist.checkForAvailableSpots(stubForAvilableSpots)) {
				updatePrice(newOrder);
				objectArrayStub.add(originalOrder);
				CancelOrder.cancel(objectArrayStub, client);
				NewOrder.insertNewOrder(newOrder);
				answer.add(true);
			} else {
				answer.add(false);
			}
		}
		try {
			client.sendToClient(answer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * send to DB: update the visitors number in that given order
	 * 
	 * @param orderNum    integer
	 * @param visitorsNum integer
	 * @return T/F
	 */
	private static boolean updateVisitorsNumber(int orderNum, int visitorsNum) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("update"); // command
		query.add("orders"); // table name
		query.add("visitorsNumber='" + visitorsNum + "'"); // values
		query.add("orderNumber"); // primaryKey
		query.add("" + orderNum); // pkValue
		return MySQLConnection.update(query);
	}

	/**
	 * send to DB: update the price of a given order
	 * 
	 * @param order object
	 * @return T/F
	 */
	private static boolean updatePrice(Order order) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("update"); // command
		query.add("orders"); // table name
		query.add("beforeDiscountPrice='" + order.getPrice() + "' ,afterDiscountPrice='" + order.getTotalPrice() + "'"); // values
		query.add("orderNumber"); // primaryKey
		query.add("" + order.getOrderNumber()); // pkValue
		return MySQLConnection.update(query);
	}

	/**
	 * checkFullDays
	 * 
	 * @param recived ArrayList of Object
	 * @param client  ConnectionToClient
	 */
	public static void checkFullDays(ArrayList<Object> recived, ConnectionToClient client) {
		ArrayList<Object> answer = new ArrayList<Object>();
		ArrayList<String> fullDays = new ArrayList<String>();
		answer.add(recived.get(0));
		Order order = (Order) recived.get(1);
		String parkName = order.getParkName();
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // select
		query.add("orders"); // tableName
		query.add("arrivedTime,SUM(visitorsNumber) AS visitorsNumber"); // columns
		query.add("WHERE parkName='" + parkName + "' GROUP BY arrivedTime ORDER BY arrivedTime"); // condition
		query.add("2"); // replyColNum
		ArrayList<ArrayList<String>> parkSummedCapacityByCapsule = MySQLConnection.select(query);
		query.clear();
		query.add("select"); // select
		query.add("park"); // tableName
		query.add("maxOrderVisitorsAmount"); // columns
		query.add("WHERE parkName='" + parkName + "'"); // condition
		query.add("1"); // replyColNum
		ArrayList<ArrayList<String>> maxCapacityForPark = MySQLConnection.select(query);
		int maxCapacity = Integer.parseInt(maxCapacityForPark.get(0).get(0));
		for (ArrayList<String> row : parkSummedCapacityByCapsule) {
			String capacityInCapsule = row.get(1);
			double newCapacity = Double.parseDouble(capacityInCapsule) + order.getVisitorsNumber();
			if (newCapacity > maxCapacity)
				fullDays.add(row.get(0));
		}
		answer.add(fullDays);
		try {
			client.sendToClient(answer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
