package server;

import java.util.ArrayList;

import ocsf.server.ConnectionToClient;

/**
 * The Login program allows you to log into the program with the correct
 * credentials
 *
 * @author Roi Amar, Haim Azulay
 */

public class Login {

	// for testing
	public static void login(ArrayList<Object> recived, ConnectionToClient client) {
		EchoServer.sendToMyClient(getAnswerForLogin(recived), client);
	}
	
	/**
	 * the function add the relevant data for the query and call MySQLConnection
	 * send to client: ArrayList of objects - cell [0]: the client function who
	 * called for service (in that case "login"), cell [1]: ArrayList of String -
	 * {if failed:} cell [0]: the string "failed" {if success:} cell [0]: the
	 * employee role as string
	 * 
	 * @param recived ArrayList of Object cell [0]: the client function who called for
	 *                service (in that case "login"), cell [1]: ArrayList of String
	 *                - cell [0]: user name, cell [1]: password
	 * @param client  ConnectionToClient
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> getAnswerForLogin(ArrayList<Object> recived) {
		// add if db up later
		ArrayList<Object> answer = new ArrayList<Object>();
		answer.add(recived.get(0));
		ArrayList<String> data = (ArrayList<String>) recived.get(1);

		ArrayList<String> query = new ArrayList<String>();
		query.add("select");
		query.add("employee");
		query.add("role, firstName, parkName, loggedin");
		query.add("WHERE username='" + data.get(0) + "' AND password='" + data.get(1) + "'");
		query.add("4");
		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(query);
		// return null or the user is already logged in
		if (queryData.isEmpty() || Integer.parseInt(queryData.get(0).get(3)) == 1) {
			answer.add("Failed");
		} else {
			answer.add(queryData.get(0));
		}
		
		return answer;
	}
	
	// for testing
	public static void updateLoggedInStatus(ArrayList<Object> recived, ConnectionToClient client) {
		EchoServer.sendToMyClient(setLoggedInStatus(recived), client);
	}
	
	/**
	 * the function add the relevant data for the query and call MySQLConnection
	 * send to client: ArrayList of objects = cell [0]: the client function who
	 * called for service (in that case "updateLoggedIn") cell [1]: boolean value =
	 * {if failed:} == false {if success:} == true
	 * 
	 * @param recived ArrayList of Object cell [0]: the client function who called for
	 *                service (in that case "updateLoggedIn"), cell [1]: ArrayList
	 *                of String = cell [0]: user name, cell [1]: logged in status
	 * @param client  ConnectionToClient
	 */ 
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> setLoggedInStatus(ArrayList<Object> recived) {
		// query
		ArrayList<Object> answer = new ArrayList<Object>();
		// the service name : updateLoggedIn
		answer.add(recived.get(0));
		// user loggedin status to update
		ArrayList<String> data = (ArrayList<String>) recived.get(1);

		ArrayList<String> query = new ArrayList<String>();
		query.add("update"); // command
		query.add("employee"); // table name
		query.add("loggedin = '" + data.get(1) + "'"); // columns to update
		query.add("username"); // condition
		query.add(data.get(0)); // username value

		answer.add(MySQLConnection.update(query));
		
		return answer;
	}

	/**
	 * get all the users in the system
	 */
	public static void disconnectAllUsers() {
		ArrayList<String> selectQuery = new ArrayList<String>();
		selectQuery.add("select");
		selectQuery.add("employee");
		selectQuery.add("username");
		selectQuery.add("");
		selectQuery.add("1");

		ArrayList<ArrayList<String>> queryData = MySQLConnection.select(selectQuery);

		for (int i = 0; i < queryData.size(); i++) {
			userToUpdate(queryData.get(i).get(0));
		}
	}

	/**
	 * sends a user to update in DB
	 * 
	 * @param username String
	 */
	public static void userToUpdate(String username) {
		// update all the users as logged out
		ArrayList<String> updateQuery = new ArrayList<String>();
		updateQuery.add("update"); // command
		updateQuery.add("employee"); // table name
		updateQuery.add("loggedin = '" + 0 + "'"); // columns to update
		updateQuery.add("username"); // condition
		updateQuery.add(username); // username value

		MySQLConnection.update(updateQuery);
	}
}
