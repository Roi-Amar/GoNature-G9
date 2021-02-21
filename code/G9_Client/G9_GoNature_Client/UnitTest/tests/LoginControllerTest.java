package tests;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import controllers.IAlert;
import controllers.IPassword;
import controllers.IRecievedFromSreverForLogin;
import controllers.IUserName;
import controllers.LoginController;
import javafx.event.ActionEvent;

public class LoginControllerTest {

	// Inside class
	// hodaya
	/*
	 * This is a stub class.
	 * This class meant to check correct username
	 */
	public class StubUserName implements IUserName {

		@Override
		public boolean checkLoginUserName() {

			// TODO Auto-generated method stub
			return valueUserNamePassword;
		}
	}

	// Inside class
	// hodaya
	/*
	 * This is a stub class.
	 * This class meant to check correct password
	 */
	public class StubPassword implements IPassword {

		@Override
		public boolean checkLoginPassword() {
			// TODO Auto-generated method stub
			return valueUserNamePassword;
		}
	}

	// Inside class
	// hodaya
	/*
	 * This is a stub class.
	 * This class meant to send a failed message
	 */
	public class StubFailMessage implements IAlert {

		@Override
		public void failedAlert(String title, String msg) {
			// TODO Auto-generated method stub
			System.out.println("Username / password doesn't match or the user is already logged in.");
		}

	}

	// Inside class
	// hodaya
	/*
	 * This is a stub class.
	 * This class meant to recieved data from the server data base and to send data to the server.
	 */
	public class StubRecivedFromServerConnected implements IRecievedFromSreverForLogin {

		@Override
		public void receivedFromServerLoggedInStatus(boolean msgReceived) {
			// TODO Auto-generated method stub
			if (updateStatusValue)
				System.out.println("Recieved success logged in");
			else
				System.out.println("Recieved failed logged in");
		}

		@Override
		public void sendToServerArrayListLogin(String type, ArrayList<String> dbColumns) {
			// TODO Auto-generated method stub
			System.out.println("Send to server Arraylist");
		}

		@Override
		public void receivedFromServerUserStatus(Object msgReceived) {
			// TODO Auto-generated method stub
			if (msgReceived instanceof String) {
				LoginController.setError("Failed");
			} else if (msgReceived instanceof ArrayList) {
				// ArrayList<String> msgReceived;
				LoginController.setError("");
				LoginController.setStatus("connected");
				LoginController.setFirstName("hodaya");
				LoginController.setParkName("disney");
			}
		}
	}

	
	// Inside class.
	// hodaya
	/*
	 * This is a stub class.
	 * This class meant to recieved data from the server data base and to send data to the server.
	 */
	public class StubRecivedFromServerNotconnected implements IRecievedFromSreverForLogin {

		@Override
		public void receivedFromServerUserStatus(Object msgReceived) {
			// TODO Auto-generated method stub
			if (msgReceived instanceof String) {
				LoginController.setError("Failed");
			} else if (msgReceived instanceof ArrayList) {
				// ArrayList<String> msgReceived;
				LoginController.setError("");
				LoginController.setStatus("Notconnected");
				LoginController.setFirstName("Brithny");
				LoginController.setParkName("Hai-Park");
			}
		}

		@Override
		public void receivedFromServerLoggedInStatus(boolean msgReceived) {
			// TODO Auto-generated method stub
			if (updateStatusValue)
				System.out.println("Recieved success logged in");
			else
				System.out.println("Recieved failed logged in");
		}

		@Override
		public void sendToServerArrayListLogin(String type, ArrayList<String> dbColumns) {
			// TODO Auto-generated method stub
			System.out.println("Send to server Arraylist");
		}

	}

	private IUserName iUserName;
	private IPassword iPassword;
	private IRecievedFromSreverForLogin iRecievedFromSreverForLogin;
	private IRecievedFromSreverForLogin iRecievedFromSreverForNotConnected;
	private IAlert failMessageAlert;

	private LoginController logintest;

	private boolean valueUserNamePassword;
	private boolean updateStatusValue;
	private String expectedResult;
	private String actualResult;

	private ActionEvent event;

	@Before
	public void setUp() throws Exception {

		iUserName = new StubUserName();
		iPassword = new StubPassword();
		failMessageAlert = new StubFailMessage();
		iRecievedFromSreverForLogin = new StubRecivedFromServerConnected();
		iRecievedFromSreverForNotConnected = new StubRecivedFromServerNotconnected();
		logintest = new LoginController(iUserName, iPassword, iRecievedFromSreverForLogin, failMessageAlert);
		valueUserNamePassword = true;
		updateStatusValue=true;
		expectedResult = null;
		actualResult = null;
	}

	/*
	 *  hodaya and bar
	 * Test case for first name exist and correct in the database(username and password correct).
	 * input: updateStatusValue=true,valueUserNamePassword = true
	 * actualResult="hodaya"
	 * expectedResult ="hodaya"
	 */
	@Test
	public void firstNameExist() throws IOException {
		updateStatusValue=true;
		valueUserNamePassword = true;
		iRecievedFromSreverForLogin.receivedFromServerUserStatus(new ArrayList<String>());
		logintest.login(event);
		expectedResult = "hodaya";
		actualResult = LoginController.getFirstName();
		assertEquals(expectedResult, actualResult);

	}
	
	/*
	 *  hodaya and bar
	 * Test case for first name not correct (username or password not correct).
	 * input: updateStatusValue=false,valueUserNamePassword = false
	 * actualResult=null
	 * expectedResult =null
	 */

	@Test
	public void firstNameNotCorrect() throws IOException {
		updateStatusValue=false;
		LoginController.setFirstName(null);
		valueUserNamePassword = false;
		logintest.login(event);
		expectedResult = null;
		actualResult = LoginController.getFirstName();
		assertEquals(expectedResult, actualResult);

	}
	
	/*
	 *  hodaya and bar
	 * Test case for park name exist and correct in the database(username and password correct).
	 * input: updateStatusValue=true,valueUserNamePassword = true
	 * actualResult="disney"
	 * expectedResult ="disney"
	 */
	
	@Test
	public void parkNameExist() throws IOException {
		updateStatusValue=true;
		valueUserNamePassword = true;
		iRecievedFromSreverForLogin.receivedFromServerUserStatus(new ArrayList<String>());
		logintest.login(event);
		expectedResult = "disney";
		actualResult = LoginController.getParkName();
		assertEquals(expectedResult, actualResult);

	}

	/*
	 *  hodaya and bar
	 * Test case for park name not correct (username or password not correct).
	 * input: updateStatusValue=false,valueUserNamePassword = false
	 * actualResult=null
	 * expectedResult =null
	 */
	
	@Test
	public void parkNameNotcorrect() throws IOException {
		updateStatusValue=false;
		LoginController.setParkName(null);
		valueUserNamePassword = false;
		logintest.login(event);
		expectedResult = null;
		actualResult = LoginController.getParkName();
		assertEquals(expectedResult, actualResult);

	}

	/*
	 * hodaya and bar
	 * Test case for Status connected in the database(username and password correct).
	 * input: updateStatusValue=true,valueUserNamePassword = true
	 * actualResult= "connected"
	 * expectedResult = "connected"
	 */

	@Test
	public void statusConnected() throws IOException {
		updateStatusValue=true;
		valueUserNamePassword = true;
		iRecievedFromSreverForLogin.receivedFromServerUserStatus(new ArrayList<String>());
		logintest.login(event);
		expectedResult = "connected";
		actualResult = LoginController.getStatus();
		assertEquals(expectedResult, actualResult);

	}
	
	/*
	 * bar and hodaya
	 * Test case for Status not connected can't connected twice(username and password correct).
	 * input: updateStatusValue=false,valueUserNamePassword = true
	 * actualResult="Notconnected"
	 * expectedResult ="Notconnected"
	 */
	
	@Test
	public void statusNotConnected() throws IOException {
		updateStatusValue=false;
		valueUserNamePassword = true;
		iRecievedFromSreverForNotConnected.receivedFromServerUserStatus(new ArrayList<String>());
		logintest.login(event);
		expectedResult = "Notconnected";
		actualResult = LoginController.getStatus();
		assertEquals(expectedResult, actualResult);

	}

	/*
	 * bar and hodaya
	 * Test case for failed Message(username or password not exist in database).
	 * input: updateStatusValue=false,valueUserNamePassword = true
	 * actualResult=null
	 * expectedResult =null
	 */

	@Test
	public void failedMessageTest() throws IOException {
		updateStatusValue=false;
		valueUserNamePassword = true;
		iRecievedFromSreverForNotConnected.receivedFromServerUserStatus(new String());
		failMessageAlert.failedAlert("Failed", "Username / password doesn't match or the user is already logged in.");
		logintest.login(event);
		expectedResult = null;
		actualResult = LoginController.getParkName();
		assertEquals(expectedResult, actualResult);
	}

}
