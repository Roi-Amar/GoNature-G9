package server;

import java.util.ArrayList;
import dataLayer.CreditCard;
import ocsf.server.ConnectionToClient;
import orderData.OrderType;
import userData.Member;

/**
 * The NewMember program places a new member into the DB
 *
 * @author Anastasia Kokin
 */

public class NewMember {

	/**
	 * inserting a new member:MEMBER\GUIDE in members table in DB sends to client
	 * ArrayList Object = cell[0] function name cell[1] T/F
	 * 
	 * @param recived ArrayList of Object: cell[0] function name cell[1] member
	 *                object, cell[2] credit card object
	 * @param client  ConnectionToClient
	 */
	public static void NewMemberInsert(ArrayList<Object> recived, ConnectionToClient client) {
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		Member memb = (Member) recived.get(1);
		CreditCard cc = (CreditCard) recived.get(2);
		// if member is already exists in DB
		if (MemerCheck(memb.getMemberID())) {
			answer.add(false);
			EchoServer.sendToMyClient(answer, client);
			return;
		} else {
			// adding a member to DB
			String s = "" + Counter.getCounter().memberNum();
			memb.setMemberNumber(s);
			if (cc != null)
				memb.setMemberPaymentType("yes");// has credit card
			else
				memb.setMemberPaymentType("no");// Doesn't has credit card
			ArrayList<String> query = new ArrayList<String>();
			query.add("insert"); // command
			query.add("member"); // table name
			query.add(queryToString(memb)); // values in query format
			MySQLConnection.insert(query);
			answer.add(true);
			if (memb.getMemberOrderType() == OrderType.MEMBER)
				answer.add("m" + memb.getMemberNumber());
			if (memb.getMemberOrderType() == OrderType.GROUP)
				answer.add("g" + memb.getMemberNumber());
			if (cc != null) {
				cc.setOrderNumber(Integer.parseInt(memb.getMemberID()));
				boolean a = NewOrder.creditCardSave(cc);
				if (!a)
					System.out.println("credit card insert fail");
			}
			EchoServer.sendToMyClient(answer, client);
		}
	}

	/**
	 * checks if the member exists in DB with his ID
	 * 
	 * @param memberID String
	 * @return T/F
	 **/
	public static Boolean MemerCheck(String memberID) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("select"); // command
		query.add("member"); // table name
		query.add("*"); // columns to select from
		query.add("WHERE ID='" + memberID + "'");

		query.add("1"); // how many columns returned

		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		if (queryData.isEmpty())
			return false;
		else {
			System.out.println("Member already exist!!");
			return true;
		}
	}

	/**
	 * ToString For DB query
	 * 
	 * @param memb Member object
	 * @return String
	 **/

	public static String queryToString(Member memb) {
		return "'" + memb.getMemberID() + "','" + memb.getMemberFirstName() + "','" + memb.getMemberLastName() + "','"
				+ memb.getMemberNumber() + "','" + memb.getMemberPhoneNumber() + "','" + memb.getMemberEmail() + "','"
				+ memb.getMemberPaymentType() + "','" + memb.getMemberOrderType() + "','" + memb.getMemberAmount()
				+ "'";
	}
}
