package server;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import dataLayer.Park;
import ocsf.server.ConnectionToClient;
import orderData.Order;
import userData.Member;

public class ParkGate {

	/**
	 * 
	 * @param recived ArrayList of Object cell[0] contains "getPrice" String cell[1]
	 *                contains ParkName cell[2] contains String "ID" or "MEMBERID"
	 *                or "ORDERNUMBER" (depends on data in cell3) cell[3] contains
	 *                value detorment by cell2 as String cell[4] how many people
	 *                wants to enter send to client: ArrayList of object cell[0]
	 *                contains "enterThePark" String cell[1] contains
	 *                priceBeforeDiscount as double cell[2] contains
	 *                priceAfterDiscount as double cell[3] contains discount as
	 *                double
	 * @param client  ConnectionToClient
	 */
	public static void getPrice(ArrayList<Object> recived, ConnectionToClient client) {
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		String parkName = (String) recived.get(1);
		String switchKey = (String) recived.get(2);
		String id = null, memberId = null, orderNumber = null;
		switch (switchKey) {
		case "ID":
			id = (String) recived.get(3);
			break;
		case "MEMBERID":
			memberId = (String) recived.get(3);
			break;
		case "ORDERNUMBER":
			orderNumber = (String) recived.get(3);
			break;
		default:
			System.out.println("bootkeError");
			break;
		}
		String howMany = (String) recived.get(4);
		ArrayList<Double> prices = priceingForEntry(orderNumber, memberId, id, howMany, parkName);
		answer.add(prices.get(0)); // priceBeforeDiscount
		answer.add(prices.get(1)); // priceAfterDiscount
		answer.add(prices.get(2)); // discount
		try {
			client.sendToClient(answer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * send to client: ArrayList of object cell[0] contains "enterThePark" String
	 * cell[1] contains String "enter" upon success "notGoodTime" / "allreadyInPark"
	 * / "parkfull" / "noRoomForRandom" / "orderDiffPark" if not cell[2] if enterd the park as random
	 * ticket numbet as int, if enterd not as random than 0
	 * 
	 * @param recived ArrayList of object cell[0] contains "enterThePark" String,
	 *                cell[1] contains Park Class, cell[2] contains String "ID" or
	 *                "MEMBERID" or "ORDERNUMBER" (depends on data in cell3),
	 *                cell[3] contains value determent by cell2 as String, cell[4]
	 *                how many people wants to enter
	 * @param client  ConnectionToClient
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 *                     This class is the general class of exceptions produced by
	 *                     failed or interrupted I/O operations
	 */
	public static void enterThePark(ArrayList<Object> recived, ConnectionToClient client) throws IOException {
		int randomVisitorTicket = 0;
		String extras = null;
		boolean moreThanOrdered = false;
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		Park park = refreshPark(((Park) recived.get(1)).getName());
		String switchKey = (String) recived.get(2);
		String id = null, memberId = null, orderNumber = null;
		switch (switchKey) {
		case "ID":
			id = (String) recived.get(3);
			break;
		case "MEMBERID":
			memberId = (String) recived.get(3);
			break;
		case "ORDERNUMBER":
			orderNumber = (String) recived.get(3);
			break;
		default:
			System.out.println("bootkeError");
			break;
		}
		String howMany = (String) recived.get(4);
		if (park.getMaximumCapacityInPark() < park.getCurrentAmount() + Integer.parseInt(howMany)) {
			answer.add("parkFull");
			client.sendToClient(answer);
			return;
		}
		ArrayList<Object> objForFech = new ArrayList<Object>();
		ArrayList<String> orderNumArr = new ArrayList<String>();
		orderNumArr.add(orderNumber);
		ArrayList<String> stringArr = new ArrayList<String>();
		if (orderNumber != null) {
			stringArr.add(orderNumber);
			objForFech.add("ordersByOrderNumber");
		} else {
			objForFech.add("ordersByIdOrMemberId");
			if (memberId != null)
				stringArr.add(memberId);
			else if (id != null)
				stringArr.add(id);
		}
		objForFech.add(stringArr);
		Order order = null;
		ArrayList<ArrayList<String>> orderWrapped = null;
		if (orderNumber != null)
			orderWrapped = ExistingOrderCheck.fechOrder(objForFech, "orders", "orderNumber");
		else
			orderWrapped = ExistingOrderCheck.fechOrderTodayInPark(objForFech, "orders", park.getName());
		if (!orderWrapped.isEmpty()) {// order was found
			order = new Order(orderWrapped.get(0));
			if (!order.getParkName().equals(park.getName())) {
				answer.add("orderDiffPark");
				client.sendToClient(answer);
				return;
			}
			if (orderWasUsed(order)) {
				answer.add("allreadyInPark");
				client.sendToClient(answer);
				return;
			}
			if (!orderOnTime(order)) {
				answer.add("notGoodTime");
				client.sendToClient(answer);
				return;
			}

			if (order.getVisitorsNumber() < Integer.parseInt(howMany)) { // if more than people on reservation
				moreThanOrdered = true;
				extras = "" + (Integer.parseInt(howMany) - order.getVisitorsNumber());
				updateArrived(order, order.getVisitorsNumber());// insert arrived to order
				order.setAmountArrived(order.getVisitorsNumber());
				insertEnteryExit(order, order.getVisitorsNumber(), "enter");
				// not yet returned
			} else {
				order.setAmountArrived(Integer.parseInt(howMany));
				updateArrived(order, Integer.parseInt(howMany));
			}
		}
		// **********occasional**********
		if (orderWrapped.isEmpty() || moreThanOrdered) { // create an order for random visits
			if (moreThanOrdered)
				howMany = extras;
			if (randomVisitorsAvilableSpot(park) < Integer.parseInt(howMany)) {
				answer.add("noRoomForRandom");
				client.sendToClient(answer);
				return;
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			order = new Order(park.getName(), getCapsuleTime().format(formatter).toString(), memberId, id,
					Integer.parseInt(howMany));
			Member member = NewOrder.MemerCheck(order);
			order = NewOrder.totalPrice(order, member, true);// updating the prices in the order
			order.setOrderNumber(Counter.getCounter().orderNum()); // get an order number
			randomVisitorTicket = order.getOrderNumber();
			NewOrder.insertNewOrder(order);
		}
		// updateArrived(order, order.getVisitorsNumber());// insert arrived to order

		answer.add("enter");
		answer.add(randomVisitorTicket);
		client.sendToClient(answer);
		insertEnteryExit(order, order.getAmountArrived(), "enter");

	}


	/**
	 * send to client: ArrayList of object cell[0] contains "exitThePark" String,
	 * cell[1] contains String "exited" if exited "allreadyExited" or "neverWasHere" or "orderDiffPark"
	 * if not
	 * 
	 * @param recived ArrayList of object cell[0] contains "exitThePark" String,
	 *                cell[1] contains Park Class, cell[2] contains String "ID" or
	 *                "MEMBERID" or "ORDERNUMBER" (depends on data in cell3),
	 *                cell[3] contains value determent by cell2 as String
	 * @param client  ConnectionToClient
	 * @throws IOException Signals that an I/O exception of some sort has occurred.
	 *                     This class is the general class of exceptions produced by
	 *                     failed or interrupted I/O operations
	 */
	public static void exitThePark(ArrayList<Object> recived, ConnectionToClient client) throws IOException {
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		Park park = (Park) recived.get(1);
		String switchKey = (String) recived.get(2);
		String id = null, memberId = null, orderNumber = null;
		switch (switchKey) {
		case "ID":
			id = (String) recived.get(3);
			break;
		case "MEMBERID":
			memberId = (String) recived.get(3);
			break;
		case "ORDERNUMBER":
			orderNumber = (String) recived.get(3);
			break;
		default:
			// System.out.println("bootkeError");
			break;
		}
		ArrayList<Object> objForFech = new ArrayList<Object>();
		ArrayList<String> orderNumArr = new ArrayList<String>();
		orderNumArr.add(orderNumber);
		ArrayList<String> stringArr = new ArrayList<String>();
		if (orderNumber != null) {
			stringArr.add(orderNumber);
			objForFech.add("ordersByOrderNumber");
		} else {
			objForFech.add("ordersByIdOrMemberId");
			if (memberId != null)
				stringArr.add(memberId);
			else if (id != null)
				stringArr.add(id);
		}
		objForFech.add(stringArr);
		Order order = null;
		ArrayList<ArrayList<String>> orderWrapped = null;
		if (orderNumber != null)
			orderWrapped = ExistingOrderCheck.fechOrder(objForFech, "orders", "orderNumber");
		else
			orderWrapped = ExistingOrderCheck.fechOrderTodayInPark(objForFech, "orders", park.getName());
		if (orderWrapped.isEmpty()) {
			answer.add("neverWasHere");
			client.sendToClient(answer);
			return;
		}
		order = new Order(orderWrapped.get(0));
		if (!order.getParkName().equals(park.getName())) {
			answer.add("orderDiffPark");
			client.sendToClient(answer);
			return;
		}
		// select entry and exit time from exitentry
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("enteryandexit"); // table name
		query.add("*"); // columns to select from
		query.add("WHERE orderNumber='" + order.getOrderNumber() + "'"); // condition
		query.add("6"); // how many columns returned
		ArrayList<ArrayList<String>> entryExitArray = MySQLConnection.select(query);
		if (entryExitArray.isEmpty()) { // if not exist never was here
			answer.add("neverWasHere");
			client.sendToClient(answer);
			return;
		} else if (entryExitArray.get(0).get(2) != null) { // if exit time not null than allready exited
			answer.add("allreadyExited");
			client.sendToClient(answer);
			return;
		}
		// if made it here than all good and can exit
		// insertEnteryExit(order, order.getAmountArrived(), "exit"); // update exit
		// time to now entryexit
		answer.add("exited");
		client.sendToClient(answer);
		insertEnteryExit(order, order.getAmountArrived(), "exit");
		return;
	}

	// input: order , amount of visitors , "enter" or "exit" as string
	// output: none
	// DB: if enter than insert a new line to entryandexit table with time enterd
	// if exit than update the time exited
	private static void insertEnteryExit(Order order, int visitorsNumber, String enterOrExit) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		ArrayList<String> query = new ArrayList<String>();
		if (enterOrExit.equals("enter")) {
			query.add("insert");
			query.add("enteryandexit");
			query.add("'" + order.getOrderNumber() + "', '" + now.format(formatter) + "', " + null + ", '"
					+ order.getParkName() + "', '" + order.getOrderType() + "', '" + visitorsNumber + "'");
			MySQLConnection.insert(query);
			updateParkCapacity(order.getParkName(), visitorsNumber);
		} else if (enterOrExit.equals("exit")) {
			query.add("update");
			query.add("enteryandexit");
			query.add("timeExit='" + now.format(formatter) + "'");
			query.add("orderNumber");
			query.add("" + order.getOrderNumber());
			MySQLConnection.update(query);
			updateParkCapacity(order.getParkName(), -1 * visitorsNumber);
		}
	}

	// input=non
	// output=find a capsle time for now
	private static LocalDateTime getCapsuleTime() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime eightAm = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0);
		LocalDateTime twelvePm = LocalDateTime.now().withHour(12).withMinute(0).withSecond(0);
		LocalDateTime fourPm = LocalDateTime.now().withHour(16).withMinute(0).withSecond(0);
		LocalDateTime eightPm = LocalDateTime.now().withHour(20).withMinute(0).withSecond(0);
		if (now.isBefore(eightAm))
			now = eightAm;
		if (now.isAfter(eightAm) && now.isBefore(twelvePm))
			now = eightAm;
		if (now.isAfter(twelvePm) && now.isBefore(fourPm))
			now = twelvePm;
		if (now.isAfter(fourPm) && now.isBefore(eightPm))
			now = fourPm;
		if (now.isAfter(eightPm))
			now = eightAm.plusDays(1);
		return now;
	}

	//
	//
	//
	private static int randomVisitorsAvilableSpot(Park park) {
		// int randomVisitorsSpots =
		// park.getMaximumCapacityInPark()-park.getMaxAmountOrders();
		int randomVisitorsSpots = park.getMaximumCapacityInPark() - capsuleOrdered(park);
		randomVisitorsSpots += lessThanOrdered(park);
		randomVisitorsSpots -= randomStillIn(park);
		return randomVisitorsSpots;
	}

	private static int randomStillIn(Park park) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("enteryandexit"); // table name
		query.add("sum(amountArrived)"); // columns to select from
		query.add("WHERE parkName='" + park.getName() + "' AND DATE(timeEnter)=CURDATE() AND timeExit is null"); // condition
		query.add("1"); // how many columns returned
		ArrayList<ArrayList<String>> randomStillIn = MySQLConnection.select(query);
		if (randomStillIn.isEmpty())
			return 0;
		if (randomStillIn.get(0).get(0) == null)
			return 0;
		return Integer.parseInt(randomStillIn.get(0).get(0));
	}

	private static int lessThanOrdered(Park park) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("orders"); // table name
		query.add("sum(visitorsNumber-amountArrived)"); // columns to select from
		query.add("WHERE parkName='" + park.getName() + "' AND arrivedTime='"
				+ getCapsuleTime().format(formatter).toString()
				+ "' AND visitorsNumber>amountArrived AND amountArrived>0"); // condition
		query.add("1"); // how many columns returned
		ArrayList<ArrayList<String>> lessThanOrdered = MySQLConnection.select(query);
		if (lessThanOrdered.isEmpty())
			return 0;
		if (lessThanOrdered.get(0).get(0) == null)
			return 0;
		int ret = (int) Double.parseDouble(lessThanOrdered.get(0).get(0));
		return ret;
		}

	private static int capsuleOrdered(Park park) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("orders"); // table name
		query.add("SUM(visitorsNumber)"); // columns to select from
		query.add("WHERE parkName='" + park.getName() + "' AND arrivedTime='"
				+ getCapsuleTime().format(formatter).toString() + "'"); // condition
		query.add("1"); // how many columns returned
		ArrayList<ArrayList<String>> summedCapsule = MySQLConnection.select(query);
		if (summedCapsule.isEmpty())
			return 0;
		if (summedCapsule.get(0).get(0) == null)
			return 0;
		int ret = (int) Double.parseDouble(summedCapsule.get(0).get(0));
		return ret;
		// return Integer.parseInt(summedCapsule.get(0).get(0));
	}

	// input: park name and number to update
	// output: none
	// DB: updates the park capacity
	private static void updateParkCapacity(String parkName, int num) {
		Park park = refreshPark(parkName);
		int newAmount = park.getCurrentAmount() + num;
		ArrayList<String> query = new ArrayList<String>();
		query.add("update");
		query.add("park");
		query.add("currentVisitoreAmount='" + newAmount + "'");
		query.add("parkName");
		query.add(parkName);
		MySQLConnection.update(query);
		ArrayList<Object> answer2 = new ArrayList<Object>();
		answer2.add("VisitorsUpdateSendToAll");
		answer2.add(parkName);// park name
		answer2.add(String.valueOf(newAmount));// updatedVisitorsNumber
		EchoServer.sendToAll(answer2);
	}

	// input: order class and amount arrived
	// output: none
	// DB: update amount arrive in order table
	private static void updateArrived(Order order, int visitorsNumber) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("update");
		query.add("orders");
		query.add("amountArrived='" + visitorsNumber + "'");
		query.add("orderNumber");
		query.add("" + order.getOrderNumber());
		MySQLConnection.update(query);
	}

	// input: park name
	// output: park class fresh from the db
	private static Park refreshPark(String parkName) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("park"); // table name
		query.add("*"); // columns to select from
		query.add("WHERE parkName='" + parkName + "'"); // condition
		query.add("6"); // how many columns returned
		ArrayList<ArrayList<String>> parkArray = MySQLConnection.select(query);
		if (parkArray.isEmpty())
			return null;
		return new Park(parkArray.get(0));
	}

	// input: order
	// note: check if order is on time (not before capsule start and not after
	// capsule ends)
	// output: true if on time, false if not
	private static boolean orderOnTime(Order order) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime capsuleStart = LocalDateTime.parse(order.getArrivedTime(), formatter);
		LocalDateTime capsuleEnd = LocalDateTime.parse(order.getArrivedTime(), formatter).plusHours(4);
		if (now.isBefore(capsuleStart) || now.isAfter(capsuleEnd))
			return false;
		return true;
	}

	// input: order class
	// note: check if the order exist in entry and exit table
	// output: true if exist false if not
	public static boolean orderWasUsed(Order order) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("enteryandexit"); // table name
		query.add("*"); // columns to select from
		query.add("WHERE orderNumber='" + order.getOrderNumber() + "'"); // condition
		query.add("6"); // how many columns returned
		ArrayList<ArrayList<String>> usedOrder = MySQLConnection.select(query);
		if (usedOrder.isEmpty())
			return false;
		return true;
	}

	// input: order number, member id, id, how many, park name
	// note: calculate the price the visitor need to pay
	// output: array list of doubles [0]=priceBeforeDiscount,
	// [1]=priceBeforeDiscount, [0]=discount
	private static ArrayList<Double> priceingForEntry(String orderNumber, String memberId, String id, String howMany,
			String parkName) {
		ArrayList<Double> ret = new ArrayList<Double>();
		boolean moreThanOrdered = false;
		Double priceBeforeDiscount = 0.0;
		Double priceAfterDiscount = 0.0;
		Double discount = 0.0;
		ArrayList<Object> objForFech = new ArrayList<Object>();
		ArrayList<String> stringArr = new ArrayList<String>();
		if (orderNumber != null) {
			stringArr.add(orderNumber);
			objForFech.add("ordersByOrderNumber");
		} else {
			objForFech.add("ordersByIdOrMemberId");
			if (memberId != null)
				stringArr.add(memberId);
			else if (id != null)
				stringArr.add(id);
		}
		objForFech.add(stringArr);
		Order order = null;
		ArrayList<ArrayList<String>> orderWrapped = null;
		if (orderNumber != null)
			orderWrapped = ExistingOrderCheck.fechOrder(objForFech, "orders", "orderNumber");
		else
			orderWrapped = ExistingOrderCheck.fechOrderTodayInPark(objForFech, "orders", parkName);
		if (!orderWrapped.isEmpty()) {// order was found
			order = new Order(orderWrapped.get(0));
			priceBeforeDiscount += order.getPrice();
			priceAfterDiscount += order.getTotalPrice();
			discount = ((priceBeforeDiscount - priceAfterDiscount) / priceBeforeDiscount) * 100; // discount in
																									// precent
			// suck the order price
			if (order.getVisitorsNumber()==0)
				order.setVisitorsNumber(order.getAmountArrived());
			if ((order.getVisitorsNumber() < Integer.parseInt(howMany))) { // if more than people on reservation
				moreThanOrdered = true;
				howMany = "" + (Integer.parseInt(howMany) - order.getVisitorsNumber());
			}
		}
		if (orderWrapped.isEmpty() || moreThanOrdered) { // if order not exist
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			Order stubOrder = new Order(parkName, now.format(dtf).toString(), memberId, id, Integer.parseInt(howMany));
			System.out.println("stubOrder: " + stubOrder);
			Member member = NewOrder.MemerCheck(stubOrder);
			stubOrder = NewOrder.totalPrice(stubOrder, member, true);
			// don't forget to insert the + thing
			priceBeforeDiscount += stubOrder.getPrice();
			priceAfterDiscount += stubOrder.getTotalPrice();
			discount = ((priceBeforeDiscount - priceAfterDiscount) / priceBeforeDiscount) * 100;
		}
		ret.add(priceBeforeDiscount);
		ret.add(discount);
		ret.add(priceAfterDiscount);
		return ret;
	}
}
