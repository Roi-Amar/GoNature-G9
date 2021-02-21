package dataLayer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * create a Park object
 *
 * @author Anastasia Kokin
 */

public class Park implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int maximumCapacityInPark = 0;
	private int currentAmount = 0;
	private int maxAmountOrders = 0;
	private int enteryPrice;
	private double mangerDiscount;

	/**
	 * Contractor for Park object
	 * 
	 * @param name                  String
	 * @param maxAmountOrders       integer
	 * @param maximumCapacityInPark integer
	 * @param currentAmount         integer
	 */
	public Park(String name, int maxAmountOrders, int maximumCapacityInPark, int currentAmount) {
		this.name = name;
		this.maxAmountOrders = maxAmountOrders;
		this.maximumCapacityInPark = maximumCapacityInPark;
		this.currentAmount = currentAmount;
		if (!check()) {
			System.out.println("Orders capacity is bigger than the Park Capacity!");
		}
	}

	/**
	 * constructor from  DB
	 * 
	 * @param parkFromDB ArrayList of String
	 */
	public Park(ArrayList<String> parkFromDB) {
		this.name = parkFromDB.get(0);
		this.currentAmount = Integer.parseInt(parkFromDB.get(1));
		this.maximumCapacityInPark = Integer.parseInt(parkFromDB.get(2));
		this.maxAmountOrders = Integer.parseInt(parkFromDB.get(3));
		this.enteryPrice = Integer.parseInt(parkFromDB.get(4));
		this.mangerDiscount = Double.parseDouble(parkFromDB.get(5));
	}

	/**
	 * checks if maxAmountOrders smaller than maximumCapacityInPark
	 * 
	 * @return T/F
	 */
	public boolean check() {
		if (maxAmountOrders <= maximumCapacityInPark)
			return false;
		return true;
	}

	public int getEnteryPrice() {
		return enteryPrice;
	}

	public void setEnteryPrice(int enteryPrice) {
		this.enteryPrice = enteryPrice;
	}

	public double getMangerDiscount() {
		return mangerDiscount;
	}

	public void setMangerDiscount(int mangerDiscount) {
		this.mangerDiscount = mangerDiscount;
	}

	public int getMaxAmountOrders() {
		return maxAmountOrders;
	}

	public void setMaxAmountOrders(int maxAmountOrders) {
		this.maxAmountOrders = maxAmountOrders;
	}

	public int getMaximumCapacityInPark() {
		return maximumCapacityInPark;
	}

	public void setMaximumCapacityInPark(int maximumCapacityInPark) {
		this.maximumCapacityInPark = maximumCapacityInPark;
	}

	public Park(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCurrentAmount() {
		return currentAmount;
	}

	public void setCurrentAmount(int currentAmount) {
		this.currentAmount = currentAmount;
	}

}
