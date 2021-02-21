package dataLayer;

/**
 * The TableCurrentVisitors contain current visitors for a park
 *
 * @author Bar Katz
 */

public class TableCurrentVisitors {
	
	private String parkNameVis;
	private String currentAmount;
	private String MaxVisitors;
	
	
	/**
	 * constructor for TableCurrentVisitors
	 * @param parkName String
	 * @param currentAmount String
	 */
	
	public TableCurrentVisitors(String parkName, String currentAmount) {
		super();
		this.parkNameVis = parkName;
		this.currentAmount = currentAmount;
	}
	

	public String getParkNameVis() {
		return parkNameVis;
	}
	public void setParkNameVis(String parkName) {
		this.parkNameVis = parkName;
	}
	public String getCurrentAmount() {
		return currentAmount;
	}
	public void setCurrentAmount(String currentAmount) {
		this.currentAmount = currentAmount;
	}

	public String getMaxVisitors() {
		return MaxVisitors;
	}


	public void setMaxVisitors(String maxVisitors) {
		MaxVisitors = maxVisitors;
	}
	
}
