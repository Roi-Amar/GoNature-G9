package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import server.Login;
import server.MySQLConnection;

public class LoginTest {
	// Create variables - results returning from server / DB
	private ArrayList<Object> result;

	// Executed before each test. It is used to prepare the test environment
	@Before
	public void setUp() throws Exception {
		// New connection to the DB
		MySQLConnection.connectToDB(
				new ArrayList<String>(Arrays.asList("localhost", "3306", "g9_gonature", "root", "NewP@ssword4theSQL")));

		// Initialize the variable for the results
		result = new ArrayList<Object>();
	}

	// Incorrect username and password
	@Test
	public void incorrectUsernameAndPassword() {
		String expected = "Failed";
		// sending to the server to get the result
		ArrayList<Object> send = new ArrayList<Object>();
		send.add("login");
		send.add(new ArrayList<String>(Arrays.asList("haim123", "12345679")));

		// send = [login, [haim123, 12345679]]
		// result = [login, [Failed]], because the details incorrect
		result = Login.getAnswerForLogin(send);

		// expected = "Failed"
		// result.get(1) = "Failed",
		// assertEquals ==> return true
		assertEquals(expected, result.get(1));
	}

	// Correct username and incorrect password
	@Test
	public void correctUsernameAndIncorrectPassword() {
		String expected = "Failed";
		ArrayList<Object> send = new ArrayList<Object>();
		send.add("login");
		send.add(new ArrayList<String>(Arrays.asList("haim", "12345679")));

		// send = [login, [haim, 12345679]]
		// result = [login, [Failed]], because the password is incorrect
		result = Login.getAnswerForLogin(send);

		// expected = "Failed"
		// result.get(1) = "Failed",
		// assertEquals ==> return true
		assertEquals(expected, result.get(1));
	}

	// Incorrect username and correct password
	@Test
	public void incorrectUsernameAndCorrectPassword() {
		String expected = "Failed";
		ArrayList<Object> send = new ArrayList<Object>();
		send.add("login");
		send.add(new ArrayList<String>(Arrays.asList("haim123", "12345678")));

		// send = [login, [haim123, 12345678]]
		// result = [login, [Failed]], because the username is incorrect
		result = Login.getAnswerForLogin(send);

		// expected = "Failed"
		// result.get(1) = "Failed",
		// assertEquals ==> return true
		assertEquals(expected, result.get(1));
	}

	// Correct username and password
	@Test
	public void correctUsernameAndPassword() {
		// role, firstName, parkName, loggedin
		ArrayList<String> expected = new ArrayList<String>(Arrays.asList("ParkManager", "Haim", "universal", "0"));
		ArrayList<Object> send = new ArrayList<Object>();
		send.add("login");
		send.add(new ArrayList<String>(Arrays.asList("haim", "12345678")));

		// send = [login, [haim, 12345678]]
		// result = [login, [ParkManager, Haim, universal, 0]], because the details
		// correct
		result = Login.getAnswerForLogin(send);

		// expected = [ParkManager, Haim, universal, 0]
		// result.get(1) = [ParkManager, Haim, universal, 0]
		// assertEquals ==> return true
		assertEquals(expected, result.get(1));
	}

	// The user is not logged in (user information must be correct)
	@SuppressWarnings("unchecked")
	@Test
	public void notLoggedIn() {
		// loggedin = 0
		String expected = "0";
		ArrayList<Object> send = new ArrayList<Object>();
		ArrayList<String> splitToString = new ArrayList<String>();
		send.add("login");
		send.add(new ArrayList<String>(Arrays.asList("haim", "12345678")));

		// send = [login, [haim, 12345678]]
		// result = [login, [ParkManager, Haim, universal, 0]]
		result = Login.getAnswerForLogin(send);
		// splitToString = [ParkManager, Haim, universal, 0]
		splitToString = (ArrayList<String>) result.get(1);
		// expected = "0"
		// splitToString.get(3) = "0"
		// assertEquals ==> return true
		assertEquals(expected, splitToString.get(3));
	}

	// The user is already logged in (user information must be correct)
	@Test
	public void alreadyLoggedIn() {
		// loggedin = 1
		String expected = "Failed";
		ArrayList<Object> send = new ArrayList<Object>();
		// send to server to set the user loggedin status to 1 (as connected)
		send.add("updateLoggedIn");
		send.add(new ArrayList<String>(Arrays.asList("haim", "1")));
		Login.setLoggedInStatus(send);

		send.clear();
		// send to server to get the current status of the user
		send.add("login");
		send.add(new ArrayList<String>(Arrays.asList("haim", "12345678")));

		// send = [login, [haim, 12345678]]
		// result = [login, [Failed]], if the user is already logged on will return
		// "Failed"
		result = Login.getAnswerForLogin(send);
		// expected = "Failed"
		// result.get(1) = "Failed"
		// assertEquals ==> return true
		assertEquals(expected, result.get(1));

		// send to server to set the status of the user as logged out (back to "0")
		send.clear();
		send.add("updateLoggedIn");
		send.add(new ArrayList<String>(Arrays.asList("haim", "0")));
		Login.setLoggedInStatus(send);
	}
}
