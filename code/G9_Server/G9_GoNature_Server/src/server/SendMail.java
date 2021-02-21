package server;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import dataLayer.EmailMessege;
import dataLayer.SmsMessege;

/**
 * The SendMail program sends mails when necessary
 *
 * @author Roi Amar
 */

public class SendMail {

	/**
	 * simulations for sending email
	 */

	private static String USER_NAME = "sim.gonature.g9"; // GMail user name (just the part before "@gmail.com")
	private static String PASSWORD = "Aa123456!"; // GMail password

	/**
	 * 
	 * @param sendTo     String
	 * @param mailSubjet String
	 * @param mailBody   String
	 */
	public static void send(String sendTo, String mailSubjet, String mailBody) {
		String from = USER_NAME;
		String pass = PASSWORD;
		String[] to = { sendTo }; // list of recipient email addresses
		String subject = mailSubjet;
		String body = mailBody;
		sendFromGMail(from, pass, to, subject, body);
	}

	/**
	 * 
	 * @param email EmailMessege
	 */
	public static void send(EmailMessege email) {
		String from = USER_NAME;
		String pass = PASSWORD;
		String[] to = { email.getTo() }; // list of recipient email addresses
		String subject = email.getSubject();
		String body = email.getMessage();
		sendFromGMail(from, pass, to, subject, body);
	}

	/**
	 * 
	 * @param sms SmsMessege
	 */
	public static void simulateSms(SmsMessege sms) {
		String from = USER_NAME;
		String pass = PASSWORD;
		String[] to = { "sim.gonature.g9@gmail.com" }; // list of recipient email addresses
		String subject = "sms simulation for " + sms.getTo();
		String body = sms.getMessage();
		sendFromGMail(from, pass, to, subject, body);
	}

	/**
	 * 
	 * @param email EmailMessege
	 */
	public static void simulateMail(EmailMessege email) {
		String from = USER_NAME;
		String pass = PASSWORD;
		String[] to = { "sim.gonature.g9@gmail.com" }; // list of recipient email addresses
		String subject = "mail simulation for " + email.getTo();
		String body = email.getSubject() + ":\n\n " + email.getMessage();
		sendFromGMail(from, pass, to, subject, body);
	}

	/**
	 * sending mails to customers via dedicated address
	 * 
	 * @param from    String
	 * @param pass    String
	 * @param to      String[]
	 * @param subject String
	 * @param body    String
	 * @exception AddressException, MessagingException
	 */
	private static void sendFromGMail(String from, String pass, String[] to, String subject, String body) {
		Properties props = System.getProperties();
		String host = "smtp.gmail.com";
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.trust", "*");

		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(from));
			InternetAddress[] toAddress = new InternetAddress[to.length];

			// To get the array of addresses
			for (int i = 0; i < to.length; i++) {
				toAddress[i] = new InternetAddress(to[i]);
			}

			for (int i = 0; i < toAddress.length; i++) {
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			}

			message.setSubject(subject);
			message.setText(body);
			Transport transport = session.getTransport("smtp");
			transport.connect(host, from, pass);
			System.out.println("sending");
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			System.out.println("sent");
		} catch (AddressException ae) {
			ae.printStackTrace();
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}
}