package dataLayer;

import orderData.Order;

/**
 * The EmailMessege contain the email message an extend class to Message
 *
 * @author Roi Amar
 */

public class EmailMessege extends Messege{
	private String to;
	private String subject;
	private EmailMessege repliedTo;
	
	/**
	 * constructor for EmailMessege
	 * @param to String
	 * @param subject String
	 * @param messege String
	 * @param order Order
	 */
	public EmailMessege(String to, String subject, String messege, Order order) {
		super(messege,order);
		this.to = to;
		this.subject = subject;
	}
	
	public String read() {
		return "To: " + to + "\n" + super.read();
	}
	
	/**
	 * Creates a return email
	 * @param to String
	 * @param messege String
	 * @return replayMsg
	 */
	
	public EmailMessege replay(String to, String messege) {
		EmailMessege replayMsg = new EmailMessege(to,"re: "+subject,messege,this.getOrder());
		replayMsg.setRepliedTo(this);
		return replayMsg;
	}
	
	public EmailMessege getRepliedTo() {
		return repliedTo;
	}

	public void setRepliedTo(EmailMessege repliedTo) {
		this.repliedTo = repliedTo;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getTo() {
		return to;
	}

}
