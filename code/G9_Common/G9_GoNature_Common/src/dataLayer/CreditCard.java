package dataLayer;

import java.io.Serializable;

/**
 * The CreditCard creating CreditCard object
 *
 * @author Bar Katz
 */

public class CreditCard  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor for CreditCard object
	 * @param cardNumber String
	 * @param cardHolderName String
	 * @param expirationDate String
	 * @param cvc integer
	 * @param orderNumber integer
	 */
	public CreditCard(String cardNumber, String cardHolderName, String expirationDate, int cvc, int orderNumber) {
		super();
		this.cardNumber = cardNumber;
		this.cardHolderName = cardHolderName;
		this.expirationDate = expirationDate;
		this.cvc = cvc;
		
		this.orderNumber = orderNumber;
	}
	/**
	 * constructor for CreditCard object
	 * @param cardNumber String
	 * @param cardHolderName String
	 * @param expirationDate String
	 * @param cvc integer
	 */
	public CreditCard(String cardNumber, String cardHolderName, String expirationDate, int cvc) {
		super();
		this.cardNumber = cardNumber;
		this.cardHolderName = cardHolderName;
		this.expirationDate = expirationDate;
		this.cvc = cvc;
	}

	String cardNumber;
	String cardHolderName;
	String expirationDate;
	int cvc;
	int orderNumber;

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getCvc() {
		return cvc;
	}

	public void setCvc(int cvc) {
		this.cvc = cvc;
	}

	

}
