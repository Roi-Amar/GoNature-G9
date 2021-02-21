package orderData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *  Create Order object
 *
 * @author Anastasia Kokin
 */

public class Order implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * tostring function
	 * @return tostring order 
	 */
	@Override
	public String toString() {
		return "Order [visitorsNumber=" + visitorsNumber + ", orderEmail=" + orderEmail + ", orderPhone=" + orderPhone
				+ ", orderType=" + orderType + ", price=" + price + ", totalPrice=" + totalPrice + ", parkName="
				+ parkName + ", arrivedTime=" + arrivedTime + ", memberId=" + memberId + ", ID=" + ID
				+ ", amountArrived=" + amountArrived + ", orderNumber=" + orderNumber + "]";
	}

	int visitorsNumber;
	String orderEmail;
	String orderPhone;
	OrderType orderType;
	double price; // before discount
	double totalPrice; // after discount
	String parkName;
	String arrivedTime;
	String memberId;
	String ID;
	int amountArrived;
	int orderNumber;

	/**
	 *this constructor is only for ParkEmployeeController from method next -DO NOT USE
	 * server side updates : check if member,calculate price
	 * 
	 * @param parkName String
	 * @param arrivedTime String
	 * @param memberId String
	 * @param ID String
	 * @param amountArrived integer
	 */
	
	public Order(String parkName, String arrivedTime, String memberId, String ID, int amountArrived) {
		this.parkName = parkName;
		this.arrivedTime = arrivedTime;
		this.amountArrived = amountArrived;
		this.memberId = memberId;
		this.ID = ID;
		this.orderEmail = null;
		this.orderPhone = null;
	}

	/**
	 * this constructor is only for OrderConroller from method next -DO NOT USE IT!!!
	 * @param visitorsNumber integer
	 * @param orderEmail String
	 * @param orderPhone String
	 * @param parkName String
	 * @param arrivedTime String
	 * @param memberId String
	 * @param ID String
	 */
	
	public Order(int visitorsNumber, String orderEmail, String orderPhone, String parkName, String arrivedTime,
			String memberId, String ID) {
		this.visitorsNumber = visitorsNumber;
		this.orderEmail = orderEmail;
		this.orderPhone = orderPhone;
		this.parkName = parkName;
		this.arrivedTime = arrivedTime;
		this.memberId = memberId;
		this.ID = ID;
	}

	//public Order(int orderNumber, int visitorsNumber, String orderEmail, String orderPhone, OrderType orderType,
	//		double price, double totalPrice, String parkName, String arrivedTime, String memberId, String ID) {

	//	this.visitorsNumber = visitorsNumber;
	//	this.orderEmail = orderEmail;
	//	this.orderPhone = orderPhone;
	//	this.orderType = orderType;
	//	this.price = price;
	//	this.totalPrice = totalPrice;
	//	this.parkName = parkName;
	//	this.arrivedTime = arrivedTime;
	//	this.memberId = memberId;
	//	this.ID = ID;
	//	this.amountArrived = 0;
	//	this.orderNumber = orderNumber;
	//}


	/**
	 * constructor from ArrayList of String
	 * 
	 * @param orderFromDB ArrayList of String
	 */
	
	public Order(ArrayList<String> orderFromDB) {

		this.visitorsNumber = Integer.parseInt(orderFromDB.get(0));
		this.orderEmail = orderFromDB.get(1);
		this.orderPhone = orderFromDB.get(2);
		this.orderType = OrderType.valueOf(orderFromDB.get(3).toUpperCase());
		this.price = Double.parseDouble(orderFromDB.get(4));
		this.totalPrice = Double.parseDouble(orderFromDB.get(5));
		this.parkName = orderFromDB.get(6);
		this.arrivedTime = orderFromDB.get(7);
		this.memberId = orderFromDB.get(8);
		this.ID = orderFromDB.get(9);
		this.amountArrived = Integer.parseInt(orderFromDB.get(10));
		this.orderNumber = Integer.parseInt(orderFromDB.get(11));
	}
	
	/**
	 * constructor 
	 * @param ord Order
	 */
	public Order(Order ord) {
		this.visitorsNumber = ord.getVisitorsNumber();
		this.orderEmail = ord.getOrderEmail();
		this.orderPhone = ord.getOrderPhone();
		this.orderType = ord.getOrderType();
		this.price = ord.getPrice();
		this.totalPrice = ord.getTotalPrice();
		this.parkName = ord.getParkName();
		this.arrivedTime = ord.getArrivedTime();
		this.memberId = ord.getMemberId();
		this.ID = ord.getID();
		this.amountArrived = ord.getAmountArrived();
		this.orderNumber = ord.getOrderNumber();
	}

	/**
	 * 
	 * @return to string for a query to insert in DB
	 */
	
	public String toStringForDB() {

		String afterDiscount = Double.toString(getTotalPrice());
		String beforDiscount = Double.toString(getPrice());
		return 	("'" + getVisitorsNumber() + "','"
					+ getOrderEmail() + "','"
					+ getOrderPhone() + "','"
					+ getOrderType().toString() + "','"
					+ beforDiscount + "','"
					+ afterDiscount + "','"
					+ getParkName() + "','"
					+ getArrivedTime() + "','"
					+ getMemberId() + "','"
					+ getID() + "','"
					+ getAmountArrived() + "','"
					+ getOrderNumber() + "'");
	}
	
	/**
	 * 
	 * @return order details
	 */
	
	public String messegeString() {
		String details = "\n\tOrder Details:\n"
				+ "Order Number:\t" + getOrderNumber()
				+ "\nPark:\t\t\t" + getParkName()
				+ "\nDate & Time:\t" + getArrivedTime()
				+ "\nVisitors:\t\t" + getVisitorsNumber()
				+ "\n\nPrice:\t\t" + getPrice() + "₪"
				+ "\nDiscount:\t" + (getPrice()-getTotalPrice())/getPrice() + "%"
				+ "\nTotal:\t\t" + getTotalPrice() +"₪"
				+ "\n\nID:\t" + getID()
				+ "\nContact Details:\t" + getOrderEmail() + "\t" + getOrderPhone();
		return details;
	}

	/**
	 * checks if the order is for occasional visitor
	 * @return T/F
	 */
	
	public boolean isOccasional() {
		if (this.orderEmail == null && this.orderPhone == null)
			return true;
		return false;
	}
	
	public int getAmountArrived() {
		return amountArrived;
	}

	public void setAmountArrived(int amountArrived) {
		this.amountArrived = amountArrived;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getVisitorsNumber() {
		return visitorsNumber;
	}

	public void setVisitorsNumber(int visitorsNumber) {
		this.visitorsNumber = visitorsNumber;
	}

	public String getOrderEmail() {
		return orderEmail;
	}

	public void setOrderEmail(String orderEmail) {
		this.orderEmail = orderEmail;
	}

	public String getOrderPhone() {
		return orderPhone;
	}

	public void setOrderPhone(String orderPhone) {
		this.orderPhone = orderPhone;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getParkName() {
		return parkName;
	}

	public void setParkName(String parkName) {
		this.parkName = parkName;
	}

	public String getArrivedTime() {
		return arrivedTime;
	}

	public void setArrivedTime(String arrivedTime) {
		this.arrivedTime = arrivedTime;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}


}
