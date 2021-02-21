package server;

import java.io.IOException;
import java.util.ArrayList;

import dataLayer.EmailMessege;
import ocsf.server.ConnectionToClient;
import orderData.Order;

/**
 * The CancelOrder program canceling an existing order and deleting it from the
 * DB
 *
 * @author Roi Amar , Anastasia Kokin
 */

public class CancelOrder {

	/**
	 * sends to client ArrayList of Objects: in cell [0]: String "cancelOrder" in
	 * cell [1]: boolean : true if entry successful false if not
	 * 
	 * @param recived ArrayList of Objects: in cell [0]: String "cancelOrder" in
	 *                cell, [1]: Order class of a given order to cancel
	 * @param client  ConnectionToClient
	 */
	public static void cancel(ArrayList<Object> recived, ConnectionToClient client){
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		Order order = (Order) recived.get(1); // order object received
		answer.add(deleteOrder(order.getOrderNumber()));
		try {
			client.sendToClient(answer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		WaitingList.pullFromWaitList(recived);
		addToDBCanceledOrder(order);
		String subject = "GoNature Cancelation Confirmation";
		String body = "Oh boy, we hate to see you go..."
				+ "\nWe would like to inform you that your order has been canceled" + "\nWe hope to see you again"
				+ "\n\n" + order.messegeString();
		@SuppressWarnings("unused")
		EmailMessege waitlistMail = new EmailMessege(order.getOrderEmail(), subject, body, order);
		Comunication.sendNotification(subject, body, order);
	}

	/**
	 * deleting an order from DB
	 * 
	 * @param orderNum Integer
	 * @return T/F
	 */
	public static boolean deleteOrder(int orderNum) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("delete");
		query.add("orders");
		query.add("orderNumber");
		query.add(String.valueOf(orderNum));
		return MySQLConnection.delete(query);
	}

	/**
	 * inserting cancelled orders into 'canceledorders' table in DB
	 * 
	 * @param ord Order object
	 */
	public static void addToDBCanceledOrder(Order ord) {
		ord.setOrderNumber(Counter.getCounter().cancelledCountNum());
		ArrayList<String> query = new ArrayList<String>();
		query.add("insert"); // command
		query.add("canceledorders"); // table name
		query.add(ord.toStringForDB()); // values in query format
		if (MySQLConnection.insert(query))
			System.out.println("inserted");
		else
			System.out.println("not");

	}

}
