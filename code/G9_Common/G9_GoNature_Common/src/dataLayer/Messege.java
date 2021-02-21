package dataLayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import orderData.Order;

/**
 * The Messege class contain the a Message
 *
 * @author Roi Amar
 */

public class Messege {
	private String message;
	private Date sentTime;
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private ArrayList<Object> attachments;
	private Order order;

	/**
	 * constructor for Message
	 * @param message String
	 * @param order Order
	 */
	public Messege(String message, Order order){
		setMessage(message);
		sentTime = new Date(); 
		this.order = order;
	}
	
	public String read() {
		return ("sent time: " + formatter.format(sentTime) + "\n" +
				message + "\n");
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getSentTime() {
		return sentTime;
	}
	
	public String getSentTimeDB() {
		return formatter.format(sentTime);
	}
	/**
	 * 
	 * @return send time plus 1 hour
	 */
	
	public String getLimitForWaitlist() { 
		return formatter.format(new Date(sentTime.getTime() + 60*60*1000));
	}
	
	/**
	 * 
	 * @return return send time plus 2 hours
	 */
	public String getLimitFor24 () { 
		return formatter.format(new Date(sentTime.getTime() + 120*60*1000));
	}

	public ArrayList<Object> getAttachments() {
		return attachments;
	}

	public void setAttachments(ArrayList<Object> attachments) {
		this.attachments = attachments;
	}
	
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}
