package server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import dataLayer.EmailMessege;
import orderData.Order;

/**
 * The WaitListSingelton program serves the WaitList program
 *
 * @author Roi Amar
 */

public class WaitListSingelton {

	// singleton WaitListSingelton
	private static WaitListSingelton obj;
	private static Date timeLastChecked;
	private static Date LastNightlyCheck;
	private static Date LastMorningCheck;
	private static boolean nightCheck;
	private static boolean morningCheck;

	// SingletonBuilder
	private WaitListSingelton() {
		timeLastChecked = new Date();
		LastNightlyCheck = new Date();
		LastMorningCheck = new Date();
		nightCheck = true;
		morningCheck = true;
		// on start up do this
	}

	//
	/**
	 * Singleton, create one instance only
	 * 
	 * @return WaitListSingelton. if not existing. creating new one and sending it
	 */
	public static WaitListSingelton getWaitlist() {
		// if (obj == null) {
		if (obj == null) {
			obj = new WaitListSingelton();// instance will be created at request time
		}
		// }
		return obj;
	}

	/**
	 * function that sends notifications to the client (SMS and Mail notification)
	 * 
	 * @param order Order object that is pulled from the wait list
	 */

	public static void sendWaitlistNotification(Order order) {
		String subject = "GoNature Waitlist Notification";
		String body = "Not long ago you were registerd to our waitlist"
				+ "\nWe are happy to inform you that a spot is avilable at the date and time you requested"
				+ "\nPlease rush to our system and confirm your order"
				+ "\nNOTE: if you will not confirm your order within one hour - your spot will be gone and your order will be canceled."
				+ order.messegeString();
		EmailMessege waitlistMail = new EmailMessege(order.getOrderEmail(), subject, body, order);
		insertPending(waitlistMail);
		Comunication.sendNotification(subject, body, order);
		// send mail and sms notification
	}

	/**
	 * send to client: SMS and Mail notification
	 * 
	 * @param order object (that is pulled from the wait list)
	 */
	public static void send24HoursNotification(Order order) {
		String subject = "GoNature 24 Hours Reminder Notification";
		String body = "Not long ago you have registerd an order with our system"
				+ "\nWe remind you that your order is taking plase tommorow, and we cant wait to see you"
				+ "\nPlease rush to our system and confirm your order"
				+ "\nNOTE: if you will not confirm your order within two hours - your order will be canceled."
				+ order.messegeString();
		EmailMessege waitlistMail = new EmailMessege(order.getOrderEmail(), subject, body, order);
		insertPending(waitlistMail);
		Comunication.sendNotification(subject, body, order);
		// send mail and sms notification
	}

	/**
	 * 
	 * DB: delete (if found) expired orders listed in pending (more than 1 hour)
	 * delete (if found) expired orders listed in waitlist (more than 24 hours)
	 * NOTE: the timeLastChecked mechanizem make sure that this function run not
	 * more than once per minute (to not overload the server and DB each time) the
	 * LastDailyCheck mechanizem make sure that the function of deleting from the
	 * waitlist will not run more than once per day the LastNightCheck mechanizem
	 * make sure that the function of sending tomorrow notifications will not run
	 * more than once per day
	 */

	@SuppressWarnings("unused")
	public static void CheckTheWaitList() {
		Date timeNow = new Date();
		Date LastPlusMinute = new Date(timeLastChecked.getTime() + 60 * 1000);
		if ((LastPlusMinute.compareTo(timeNow) <= 0) || nightCheck || morningCheck) {
			ArrayList<ArrayList<String>> expired = selectExpiredPending(); // select all expired in pending
			if (!expired.isEmpty()) { // if not empty
				for (ArrayList<String> row : expired) { // for each expired order
					ArrayList<Object> stubForFetchOrder = new ArrayList<Object>();
					stubForFetchOrder.add("orderByID");
					stubForFetchOrder.add(row);
					Order exp = new Order(
							(ExistingOrderCheck.fechOrder(stubForFetchOrder, "orders", "orderNumber")).get(0));
					CancelOrder.deleteOrder(exp.getOrderNumber());
					ArrayList<Object> stubForpullFromWaitList = new ArrayList<Object>();
					stubForpullFromWaitList.add("waitlist");
					stubForpullFromWaitList.add(exp);
					WaitingList.pullFromWaitList(stubForpullFromWaitList);
				}
				ArrayList<String> query = new ArrayList<String>();
				query.add("deleteCond");
				query.add("pendingwaitlist");
				query.add("timeLimit <= NOW()");
				MySQLConnection.deleteCond(query);
			}

			// send were sorry mail
			timeLastChecked = new Date();
		} // if last check was less than minute ago dont check
		Date LastPlusDay = new Date(LastNightlyCheck.getTime() + 24 * 60 * 60 * 1000);
		if (LastPlusDay.compareTo(timeNow) <= 0
				|| ((Calendar.HOUR_OF_DAY > 22) && !isSameDay(LastNightlyCheck, timeNow))) {
			nightCheck = true;
		}
		if (nightCheck) { // TODO: think if we want to send a messege to the deleted or not.
			ArrayList<String> query = new ArrayList<String>();
			query.add("deleteOver24");
			query.add("waitinglist");
			query.add("arrivedTime <= DATE_SUB(NOW(), INTERVAL 1 DAY)");
			MySQLConnection.deleteCond(query);
			// delete wait list of today
			nightCheck = false;
			LastNightlyCheck = new Date();
		}
		LastPlusDay = new Date(LastMorningCheck.getTime() + 24 * 60 * 60 * 1000);
		if (LastPlusDay.compareTo(timeNow) <= 0 || ((Calendar.HOUR_OF_DAY > 8) && (Calendar.HOUR_OF_DAY < 10)
				&& !isSameDay(LastMorningCheck, timeNow))) {
			morningCheck = true;
		}
		if (morningCheck) { // TODO: think if we want to send a messege to the deleted or not.
			notificationsForTommorowTravelers();
			morningCheck = false;
			LastMorningCheck = new Date();
		}
	}

	/**
	 * DB: select all orders that are due tomorrow NOTE: send notifications to all
	 * orders that are due tomorrow
	 */

	private static void notificationsForTommorowTravelers() {
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("orders"); // table name
		query.add("*"); // col name
		query.add("WHERE arrivedTime > CURDATE() + 1 AND arrivedTime < CURDATE() + 2"); // condition
		query.add("12"); // col num
		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		if (queryData.isEmpty())
			return;
		else {
			for (ArrayList<String> row : queryData) {
				Order order = new Order(row);
				if (!ExistingOrderCheck.checkIfPending("" + order.getOrderNumber()))
					send24HoursNotification(order);
			}
		}
	}

	/**
	 *
	 * the function returns true if the two dates are on the same day, false if not
	 * 
	 * @param date1 Date
	 * @param date2 Date
	 * @return true if the two dates are on the same day, false if not
	 */
	private static boolean isSameDay(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
				&& calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * check if the limit has been reached
	 * 
	 * @param order Order object
	 * @return T\F
	 */

	private static boolean limitReach(Order order) {
		Date now = (Date) new Date();
		Date limit = new Date(selectDateForPending(order).getTime());
		if (limit.equals(null) || limit.compareTo(now) <= 0) // to<from: to.compareTo(from)<0
			return false;
		return true;
	}

	/**
	 * true if time replayed has not reached the limit false if limit has reached
	 * 
	 * @param order Order object
	 * @return T/F
	 */
	public static boolean orderConfirm(Order order) {
		if (limitReach(order)) {
			removePending(order);
			return true;
		}
		return false;
	}

	/**
	 * true if found the order on the wait list and removed it, false if not. // DB:
	 * delete the wait list entry of that order if found
	 * 
	 * @param recived Order Object
	 * @return T/F
	 */
	public static boolean CancelWaitlist(Order recived) {
		int orderNum = recived.getOrderNumber();
		ArrayList<String> query = new ArrayList<String>();
		query.add("delete");
		query.add("waitingList");
		query.add("waitlistID");
		query.add(String.valueOf(orderNum));
		if (!MySQLConnection.delete(query))
			return false; // error in delete
		return true;
	}

	/**
	 * true if found the order on the wait list and removed it, false if not. //
	 * send to DB: order to remove from pending
	 * 
	 * @param order Order object
	 * @return T/F
	 */
	public static boolean removePending(Order order) {
		int orderNum = order.getOrderNumber();
		ArrayList<String> query = new ArrayList<String>();
		query.add("delete");
		query.add("pendingwaitlist");
		query.add("orderNumber");
		query.add(String.valueOf(orderNum));
		if (!MySQLConnection.delete(query))
			return false; // error in delete
		return true;
	}

	/**
	 * Order to insert into the DB. true if successful false if not send to DB: new
	 * order to list in pending
	 * 
	 * @param messege EmailMessege
	 * @return T/F
	 */

	private static boolean insertPending(EmailMessege messege) {
		String orderNum = String.valueOf(messege.getOrder().getOrderNumber());
		if (ExistingOrderCheck.checkIfPending(orderNum)) {
			return true; // already exist in the DB - avoid duplicates - for safety only
		}
		ArrayList<String> query = new ArrayList<String>();
		query.add("insert"); // command
		query.add("pendingwaitlist"); // table name
		if (messege.getSubject().contains("GoNature 24 Hours Reminder Notification"))
			query.add("'" + messege.getOrder().getOrderNumber() + "','" + messege.getLimitFor24() + "'"); // values in
																											// query
																											// format
		else if (messege.getSubject().contains("GoNature Waitlist Notification"))
			query.add("'" + messege.getOrder().getOrderNumber() + "','" + messege.getLimitFor24() + "'"); // values in
																											// query
																											// format
		else
			return false;
		return MySQLConnection.insert(query);
	}

	/**
	 * query that takes data from DB about ExpiredPending
	 * 
	 * @return ArrayList<ArrayList<String>> contains the expired entries in pending
	 */
	private static ArrayList<ArrayList<String>> selectExpiredPending() {
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("pendingwaitlist"); // table name
		query.add("orderNumber"); // col name
		query.add("WHERE timeLimit<NOW()"); // condition
		query.add("1"); // col num
		return MySQLConnection.select(query);
	}

	/**
	 * Select Date For Pending
	 * 
	 * @param order Order object
	 * @return the time limit of the order if found, if not than null
	 */
	private static Date selectDateForPending(Order order) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("pendingwaitlist"); // table name
		query.add("timeLimit"); // col name
		query.add("WHERE orderNumber='" + order.getOrderNumber() + "'"); // condition
		query.add("1"); // col num
		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		if (queryData.isEmpty())
			return null;
		return java.sql.Timestamp.valueOf(queryData.get(0).get(0)); //////////// check this closely
	}

}
