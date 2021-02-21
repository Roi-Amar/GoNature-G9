package dataLayer;

import orderData.Order;

/**
 * The SmsMessege contain the sms message an extend class to Message
 *
 * @author Roi Amar
 */

public class SmsMessege extends Messege{
	private String to;
	private SmsMessege repliedTo;
	
	/**
	 * constructor for SmsMessege
	 * @param to String
	 * @param messege String
	 * @param order Order
	 */
	public SmsMessege(String to, String messege, Order order) {
		super(messege,order);
		this.to = to;
	}
	
	public String read() {
		return "To: " + to + "\n" + super.read();
	}
	
	/**
	 * Creates a return sms message
	 * @param to String
	 * @param messege String
	 * @return replayMsg
	 */
	public SmsMessege replay(String to, String messege) {
		SmsMessege replayMsg = new SmsMessege(to,messege,this.getOrder());
		replayMsg.setRepliedTo(this);
		return replayMsg;
	}
	
	public SmsMessege getRepliedTo() {
		return repliedTo;
	}

	public void setRepliedTo(SmsMessege repliedTo) {
		this.repliedTo = repliedTo;
	}
	
	public String getTo() {
		return to;
	}
}
