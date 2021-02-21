package server;

import java.util.ArrayList;

/**
 * The Counter program generates unique numbers to insert in DB as primary keys
 *
 * @author Roi Amar, Anastasia Kokin
 * 
 */

class Counter {
	private static Counter obj;
	private static int count;
	private static int memCount;
	private static int waitlistNum;
	private static int cancelledCount;
	private static int discountsIDcount;

	private Counter() {
		count = getLastNumber("orders", "orderNumber") + 1;
		memCount = getLastNumber("member", "memberNumber") + 1;
		waitlistNum = getLastNumber("waitinglist", "waitlistID") + 1;
		cancelledCount = getLastNumber("canceledorders", "orderNumber") + 1;
		discountsIDcount = getLastNumber("discounts", "requestID") + 1;
	}

	/**
	 * sets new counter
	 * 
	 * @return object Counter
	 */
	public static Counter getCounter() {
		if (obj == null) {
			if (obj == null) {
				obj = new Counter();// instance will be created at request time
			}
		}
		return obj;
	}

	/**
	 * 
	 * @return last number +1
	 */
	public int orderNum() {
		return count++;
	}

	/**
	 * 
	 * @return last number +1
	 */
	public int memberNum() {
		return memCount++;
	}

	/**
	 * 
	 * @return last number +1
	 */
	public int waitlistNum() {
		return waitlistNum++;
	}

	/**
	 * 
	 * @return last number +1
	 */
	public int cancelledCountNum() {
		return cancelledCount++;
	}

	/**
	 * 
	 * @return last number +1
	 */
	public int discountsIDcountNum() {
		return discountsIDcount++;
	}

	private int getLastNumber(String tableName, String Col) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("select");
		query.add(tableName);
		query.add(Col);
		query.add("ORDER BY " + Col + " DESC LIMIT 1");
		query.add("1");
		ArrayList<ArrayList<String>> lastNum = MySQLConnection.select(query);
		if (lastNum.isEmpty())
			return 1005;
		return Integer.parseInt(lastNum.get(0).get(0));
	}
}
