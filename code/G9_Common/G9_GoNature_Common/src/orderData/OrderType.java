package orderData;

/**
 * enum for OrderType
 *
 * @author Bar Katz
 */

public enum OrderType {
	REGULAR("regular"), //no membership, not from a group
	MEMBER("member"),//has a membership
	GROUP("group"); //with a guide
	
	public final String label;
	
	private OrderType(String label) {
        this.label = label;
    }
	
	@Override 
	public String toString() { 
	    return this.label; 
	}
	
}
