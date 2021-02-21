package userData;

import java.io.Serializable;
import java.util.ArrayList;
import orderData.OrderType;

/**
 *  Create Member object
 *
 * @author Anastasia Kokin
 */

public class Member implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String memberID;
	String memberFirstName;
	String memberLastName;
	String memberNumber;
	String memberPhoneNumber;
	String memberEmail;
	String memberPaymentType;//Yes-has credit card. No-doesn't have credit card
	OrderType memberOrderType;
	String memberAmount;

	/**
	 * constructor from DB
	 * @param memberFromDB ArrayList of String
	 */
	
	public Member(ArrayList<String> memberFromDB) {
		this.memberID = memberFromDB.get(0);
		this.memberFirstName = memberFromDB.get(1);
		this.memberLastName = memberFromDB.get(2);
		this.memberNumber = memberFromDB.get(3);
		this.memberPhoneNumber = memberFromDB.get(4);
		this.memberEmail = memberFromDB.get(5);
		this.memberPaymentType = memberFromDB.get(6);
		this.memberOrderType = OrderType.valueOf(memberFromDB.get(7).toUpperCase());
		this.memberAmount = memberFromDB.get(8);
	}


	/**
	 * constructor for member object
	 * 
	 * @param memberID String
	 * @param memberFirstNAme String
	 * @param memberLastNAme String
	 * @param memberPhoneNumber String
	 * @param memberEmail String
	 * @param memberOrderType OrderType
	 * @param memberAmount String
	 */
	public Member(String memberID, String memberFirstNAme, String memberLastNAme,
			String memberPhoneNumber, String memberEmail, OrderType memberOrderType, String memberAmount) {
		super();
		this.memberID = memberID;
		this.memberFirstName = memberFirstNAme;
		this.memberLastName = memberLastNAme;
		this.memberPhoneNumber = memberPhoneNumber;
		this.memberEmail = memberEmail;
		this.memberOrderType = memberOrderType;
		this.memberAmount = memberAmount;
		this.memberPaymentType = null;
	}

	public String getMemberID() {
		return memberID;
	}

	public void setMemberID(String memberID) {
		this.memberID = memberID;
	}

	public String getMemberFirstName() {
		return memberFirstName;
	}

	public void setMemberFirstName(String memberFirstName) {
		this.memberFirstName = memberFirstName;
	}

	public String getMemberLastName() {
		return memberLastName;
	}

	public void setMemberLastName(String memberLastName) {
		this.memberLastName = memberLastName;
	}

	public String getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(String memberNumber) {
		this.memberNumber = memberNumber;
	}

	public String getMemberPhoneNumber() {
		return memberPhoneNumber;
	}

	public void setMemberPhoneNumber(String memberPhoneNumber) {
		this.memberPhoneNumber = memberPhoneNumber;
	}

	public String getMemberEmail() {
		return memberEmail;
	}

	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}

	public String getMemberPaymentType() {
		return memberPaymentType;
	}

	public void setMemberPaymentType(String memberPaymentType) {
		this.memberPaymentType = memberPaymentType;
	}

	public OrderType getMemberOrderType() {
		return memberOrderType;
	}

	public void setMemberOrderType(OrderType memberOrderType) {
		this.memberOrderType = memberOrderType;
	}

	public String getMemberAmount() {
		return memberAmount;
	}

	public void setMemberAmount(String memberAmount) {
		this.memberAmount = memberAmount;
	}

}
