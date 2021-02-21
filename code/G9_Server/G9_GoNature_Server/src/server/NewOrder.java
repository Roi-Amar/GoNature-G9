package server;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import dataLayer.CreditCard;
import ocsf.server.ConnectionToClient;
import orderData.Order;
import orderData.OrderType;
import userData.Member;

/**
 * The NewOrder program places a new order into the DB
 *
 * @author Anastasia Kokin
 */

public class NewOrder {

	/**
	 * inserting a new reservation in order table in DB. sends to client :ArrayList
	 * of Object = cell[0] function name, cell[1] Order object with updated cells:
	 * price ,totalPrice
	 * 
	 * @param recived ArrayList of Object: cell[0] function name, cell[1] order
	 *                object
	 * @param client  ConnectionToClient
	 * 
	 **/
	@SuppressWarnings("static-access")
	public static void NewReservation(ArrayList<Object> recived, ConnectionToClient client) {
		WaitingList a = new WaitingList();
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		Order data = (Order) recived.get(1); // order object received
		Member memb = MemerCheck(data); // to check the member type by order
		if (memb != null) {
			data.setMemberId(memb.getMemberNumber());
			data.setID(memb.getMemberID());
		}

		// check if the same ID ordered at the same time already
		if (checkIfHasOrderForMemberInSpecificTimeAndPArk(data)) {
			answer.add("Existing");
			EchoServer.sendToMyClient(answer, client);
			return;
		}
		// check if the capacity of orders is full
		if ((!a.checkForAvailableSpots(recived))) {
			answer.add("Failed");// need to enter waiting list
			EchoServer.sendToMyClient(answer, client);
		}

		else {
			data = totalPrice(data, memb, data.isOccasional());// updating the prices in the order
			data.setOrderNumber(Counter.getCounter().orderNum()); // get an order number
			answer.add(data);
			EchoServer.sendToMyClient(answer, client);
		}

	}

	private static boolean checkIfHasOrderForMemberInSpecificTimeAndPArk(Order ord) {

		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("orders"); // table name
		query.add("orderNumber"); // columns to select from
		query.add("WHERE ID ='" + ord.getID() + "' AND arrivedTime = '" + ord.getArrivedTime() + "'");
		query.add("1");
		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		return (!queryData.isEmpty());
	}

	/**
	 * function that called when we want to insert a new order and credit card into
	 * the DB.sends to client: T/F
	 * 
	 * @param recived cell[0] function name cell[1] order object cell[2] credit card
	 *                object/null
	 * @param client  ConnectionToClient
	 * 
	 * 
	 **/
	public static void queInsert(ArrayList<Object> recived, ConnectionToClient client) {

		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		Order order = (Order) recived.get(1);
		answer.add(insertNewOrder(order));

		if (recived.get(2) != null) {
			CreditCard cc = (CreditCard) recived.get(2);
			cc.setOrderNumber(order.getOrderNumber());
			creditCardSave(cc);
		}

		EchoServer.sendToMyClient(answer, client);
		String subject = "GoNature Order Confirmation";
		String messege = "order completed sucssesfuly!\n "
				+ "dont forget to confirm you arrival 24 hours before the visit"
				+ "\ndont worry, we will send you a reminder!" + "\nwe hope to see you soon!\n\n\n"
				+ order.messegeString();
		Comunication.sendNotification(subject, messege, order);
	}

	/**
	 * sending to DB: new order to list in
	 * 
	 * @param order Order Object to insert into the DB
	 * @return true if successful false if not
	 **/

	public static boolean insertNewOrder(Order order) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("insert"); // command
		query.add("orders"); // table name
		query.add(order.toStringForDB()); // values in query format
		return MySQLConnection.insert(query);
	}

	/**
	 * Checks if you are a member
	 * 
	 * @param ord Order object
	 * @return member/null
	 * 
	 **/

	public static Member MemerCheck(Order ord) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("member"); // table name
		query.add("*"); // columns to select from

		if (ord.getID() == null && ord.getMemberId() == null)
			return null;
		if (ord.getID() != null) {
			query.add("WHERE ID='" + ord.getID() + "'");
		} else if (ord.getMemberId() != null) {
			query.add("WHERE memberNumber='" + ord.getMemberId() + "'");
		}

		query.add("9"); // how many columns returned

		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		if (queryData.isEmpty())
			return null;
		else {
			Member m = new Member(queryData.get(0));
			return m;
		}

	}

	///////////// ************************* price *****************************
	/**
	 * function that calculates the price for an order and puts it into Order object
	 * 
	 * @param ord        Order with empty totalPrice
	 * @param memb       Member object
	 * @param occasional T/F
	 * @return Order with updated totalPrice and price and orderType
	 */
	public static Order totalPrice(Order ord, Member memb, Boolean occasional) {
		double[] a = CurrentPriceInPark(ord);
		double parkEnteryPrice = a[0];
		double parkDiscount = a[1];
		int numberOfPeople;
		if (occasional) {
			numberOfPeople = ord.getAmountArrived();
			ord.setPrice(parkEnteryPrice * numberOfPeople);// full price for all

			if (memb == null) { // if the order is not for a member
				ord.setOrderType(OrderType.REGULAR);
				ord.setTotalPrice((parkEnteryPrice * numberOfPeople) * parkDiscount);
			} else { // if the order is for occasional member
				ord.setMemberId(memb.getMemberNumber());
				ord.setID(memb.getMemberID());
				switch (memb.getMemberOrderType()) {
				case MEMBER:
					ord.setOrderType(OrderType.MEMBER);
					int nonFamily = numberOfPeople - Integer.parseInt(memb.getMemberAmount());
					if (nonFamily > 0)
						ord.setTotalPrice(parkDiscount * nonFamily * parkEnteryPrice
								+ (((Double.parseDouble(memb.getMemberAmount()) * parkEnteryPrice) * 0.8)
										* parkDiscount));
					else
						ord.setTotalPrice(((numberOfPeople * parkEnteryPrice) * 0.8) * parkDiscount);
					break;
				case GROUP:
					ord.setOrderType(OrderType.GROUP);
					ord.setTotalPrice((ord.getPrice() * 0.9) * parkDiscount);
					break;
				default:
					break;
				}
			}
		} else { // pre-order
			numberOfPeople = ord.getVisitorsNumber();
			ord.setPrice(parkEnteryPrice * numberOfPeople);// full price for all
			if (memb == null) { // if the order is not for a member
				ord.setOrderType(OrderType.REGULAR);
				ord.setTotalPrice((ord.getPrice() * 0.85) * parkDiscount);
			} else {// if the order is for non-occasional member
				ord.setMemberId(memb.getMemberNumber());
				ord.setID(memb.getMemberID());
				switch (memb.getMemberOrderType()) {
				case MEMBER:
					ord.setOrderType(OrderType.MEMBER);
					int nonFamily = numberOfPeople - Integer.parseInt(memb.getMemberAmount());
					if (nonFamily > 0) {
						// non family calculation
						ord.setTotalPrice(((nonFamily * parkEnteryPrice) * parkDiscount) * 0.85);
						// add the family members payment to sum
						ord.setTotalPrice(ord.getTotalPrice()
								+ (((Integer.parseInt(memb.getMemberAmount()) * parkEnteryPrice) * 0.8) * 0.85)
										* parkDiscount);
//						ord.setTotalPrice(ord.getTotalPrice() - (ord.getTotalPrice() / numberOfPeople)
//								* ((Integer.parseInt(memb.getMemberAmount()) * parkEnteryPrice) * 0.8));
//						ord.setTotalPrice((ord.getPrice() * 0.85) * parkDiscount);
					} else {
						ord.setTotalPrice((numberOfPeople * parkEnteryPrice) * 0.85);
						ord.setTotalPrice((ord.getTotalPrice() * 0.8) * parkDiscount);
					}
					break;
				case GROUP:
					ord.setOrderType(OrderType.GROUP);
					ord.setTotalPrice((ord.getPrice() * 0.75) * parkDiscount);
					break;
				default:
					break;
				}
			}
		}
		return ord;
	}

	/**
	 * calculates and return current price of entry in the park with manger discount
	 * calculated
	 * 
	 * @param ord Order object
	 * @return entry price
	 **/
	public static double[] CurrentPriceInPark(Order ord) {

		updatingParkCurrentPrice(ord.getParkName());
		// current park discount
		double[] a = new double[2];
		ArrayList<String> query2 = new ArrayList<String>();
		query2.add("select"); // command
		query2.add("park"); // table name
		query2.add("entryPrice, mangerDiscount"); // columns to select from
		query2.add("WHERE parkName='" + ord.getParkName() + "'"); // condition
		query2.add("2"); // how many columns returned
		ArrayList<ArrayList<String>> parkDetails = MySQLConnection.select(query2);

		a[0] = Double.parseDouble(parkDetails.get(0).get(0));
		a[1] = Double.parseDouble(parkDetails.get(0).get(1));

		return a;
	}

	/**
	 * updates the discount in the park table in DB by the right dates
	 * 
	 * @param parkName String
	 */
	public static void updatingParkCurrentPrice(String parkName) {
		LocalDateTime now = LocalDateTime.now();

		// new park discount
		ArrayList<String> query1 = new ArrayList<String>();
		query1.add("select"); // command
		query1.add("discounts"); // table name
		query1.add("parkName, discountscol"); // columns to select from
		query1.add("WHERE parkName='" + parkName + "' AND discounts.from <= '" + now + "' AND discounts.to >= '" + now
				+ "'"); // condition
		query1.add("2"); // how many columns returned
		ArrayList<ArrayList<String>> newDiscountsQ = MySQLConnection.select(query1);
		// current park discount
		ArrayList<String> query2 = new ArrayList<String>();
		query2.add("select"); // command
		query2.add("park"); // table name
		query2.add("entryPrice, mangerDiscount"); // columns to select from
		query2.add("WHERE parkName='" + parkName + "'"); // condition
		query2.add("2"); // how many columns returned
		ArrayList<ArrayList<String>> currentParkDiscountQ = MySQLConnection.select(query2);

		double discount = Double.parseDouble(currentParkDiscountQ.get(0).get(1));
		if (!newDiscountsQ.isEmpty())
			discount = Double.parseDouble(newDiscountsQ.get(0).get(1));
		else
			discount = 1.0;
		// if there is no change in discount
		if (Double.parseDouble(currentParkDiscountQ.get(0).get(1)) == discount) {
			return;
		}

		// update the park table in DB with current discount
		ArrayList<String> query3 = new ArrayList<String>();
		query3.add("update");
		query3.add("park");
		query3.add("mangerDiscount= '" + discount + "'");
		query3.add("parkName");
		query3.add(parkName);
	}

	////////////// ********************* Park **************************************

	/**
	 * function to pull parks names from DB. sends to client a list of parks names
	 * from DB
	 * 
	 * @param recived ArrayList of object cell [0]: calling function name
	 * @param client  ConnectionToClient
	 * 
	 **/
	public static void parksNames(ArrayList<Object> recived, ConnectionToClient client) {

		// the returned values stored here
		ArrayList<Object> answer = new ArrayList<Object>();
		// the service name : ParksNames
		answer.add(recived.get(0));
		// cell 0: recivedFromServerParksNames

		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("park"); // table name
		query.add("parkName"); // columns to select from
		query.add(""); // condition - non -> all parks names required
		query.add("1"); // how many columns returned

		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		if (queryData.isEmpty()) {
			// no parks in DB
			answer.add(new ArrayList<String>(Arrays.asList("Failed")));
		} else {
			ArrayList<String> parkNames = new ArrayList<String>();
			for (ArrayList<String> a : queryData)
				for (String b : a)
					parkNames.add(b);
			answer.add(parkNames);
		}
		EchoServer.sendToMyClient(answer, client);

	}

	/**
	 * updating column amountArrived in order table for an order. sends to client:
	 * ArrayList of Object = cell[0] function name, cell[1]: ArrayList of String [0]
	 * orderNumber, [1] number of visitors to add
	 * 
	 * @param recived ArrayList of object cell[0]: calling function name
	 * @param client  ConnectionToClient
	 * 
	 **/

	@SuppressWarnings("unchecked")
	public static void updateOrderAmountArrived(ArrayList<Object> recived, ConnectionToClient client) {
		// query
		ArrayList<Object> answer = new ArrayList<Object>();
		// the service name : updateAmountArrived
		answer.add(recived.get(0));
		// the data that sent from the client
		// cell 0: orderNumber
		// cell 1: amountArrived
		ArrayList<String> data = (ArrayList<String>) recived.get(1);

		ArrayList<String> query = new ArrayList<String>();
		query.add("update"); // command
		query.add("orders"); // table name
		query.add("amountArrived = '" + data.get(1) + "'"); // columns to update
		query.add("orderNumber"); // condition
		query.add(data.get(0)); // orderNumber value

		if (MySQLConnection.update(query))
			answer.add(true);
		else
			answer.add(false);

		EchoServer.sendToMyClient(answer, client);
	}

	////// ************credit card****************************************

	/**
	 * inserts a new credit card in DB
	 * 
	 * @param cc CreditCard object
	 * @return T\F
	 **/
	public static Boolean creditCardSave(CreditCard cc) {

		ArrayList<String> query = new ArrayList<String>();
		query.add("insert"); // command
		query.add("creditcard"); // table name
		query.add(toStringForCreditCardSave(cc)); // values in query format

		return MySQLConnection.insert(query);
	}

	/**
	 * toString in use for CreditCardSave query
	 * 
	 * @param data CreditCard object
	 * @return String
	 **/
	public static String toStringForCreditCardSave(CreditCard data) {
		return "'" + data.getCardNumber() + "','" + data.getCardHolderName() + "','" + data.getExpirationDate() + "','"
				+ data.getCvc() + "','" + data.getOrderNumber() + "'";
	}

}
