package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dataLayer.EmailMessege;
import dataLayer.SmsMessege;
import ocsf.server.ConnectionToClient;
import orderData.Order;

/**
 * The Comunication program is a static program that manages the communication
 * with the users
 *
 * @author Roi Amar
 */

public class Comunication {

	/**
	 * sends notifications
	 * 
	 * @param subject String
	 * @param messege String
	 * @param order   String
	 */
	public static void sendNotification(String subject, String messege, Order order) {
		SmsMessege sms = new SmsMessege(order.getOrderPhone(), subject + "\n\n " + messege, order);
		EmailMessege email = new EmailMessege(order.getOrderEmail(), subject, messege, order);
		ExecutorService exec = Executors.newSingleThreadExecutor();
		if (!order.getOrderEmail().equals(null)) { // email null means random visitor
			exec.execute(new Runnable() {

				/**
				 * here is the simulation for the sending
				 */
				@Override
				public void run() {
					// SIMULATION ONLY
					SendMail.simulateMail(email);
					SendMail.simulateSms(sms);
				}
				/**
				 * here will be the not simulated method for the sending
				 */
			});
		}
	}

	/**
	 * array list of object that contains: [0] - string "waitlistReplay" [1] -
	 * Boolean true if the order is now in place false if something went wrong. if
	 * the client say "no", an empty message will be sent to the client - ergo:
	 * nothing in cell [1].
	 * 
	 * @param recived ArrayList of Object [0] - string "waitlistReplay", [1] - String
	 *                answer (NOTE: have to be "yes" or "no"), [2] - Order class of
	 *                the order that initiated this method
	 * @param client  ConnectionToClient
	 */
	@SuppressWarnings("static-access")
	public static void waitlistReplay(ArrayList<Object> recived, ConnectionToClient client) {
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		String decide = (String) recived.get(1);
		Order order = (Order) recived.get(2);
		if (decide.equals("yes")) {
			answer.add(WaitListSingelton.getWaitlist().orderConfirm(order)); // if true than less than limit was not
																				// reached
		} else if (decide.equals("no")) {
			WaitListSingelton.getWaitlist().removePending(order);
			CancelOrder.deleteOrder(order.getOrderNumber());
			ArrayList<Object> stubForPullFromWaitList = new ArrayList<Object>();
			stubForPullFromWaitList.add("removeIt");
			stubForPullFromWaitList.add(order);
			WaitingList.pullFromWaitList(stubForPullFromWaitList);
		}
		try {
			client.sendToClient(answer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
