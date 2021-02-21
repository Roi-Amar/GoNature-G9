package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * The MySQLConnection program is the bridge to the DB
 *
 * @author Roi Amar
 */

public class MySQLConnection {

	private static Connection dbConn = null;
	private static String dbScheme = null;

	/**
	 * sets the connection to the DB
	 * 
	 * @param data ArrayList of String cell [0]: server URL, cell [1]: server port
	 *             (default for mySQL is 3306), cell [2]: name of the DB scheme,
	 *             cell [3]: user name cell, [4]: password
	 * @return T/F
	 */
	public static boolean connectToDB(ArrayList<String> data) {
		String dburl = "jdbc:mysql://" + data.get(0) + ":" + data.get(1) + "/" + data.get(2) + "?serverTimezone=IST";
		dbScheme = data.get(2);
		String username = data.get(3);
		String password = data.get(4);
		try {
			dbConn = DriverManager.getConnection(dburl, username, password);
			// DBup = true;
		} catch (SQLException e) {
			System.out.println("OH NO!"); // remove after test
			e.printStackTrace();
			return false;
		}
		System.out.println("Great Succsess! DB connected!"); // remove after test
		return true;
	}

	/**
	 * connection to DB status. NOTE: make sure that you have an open connection
	 * before calling this method
	 * 
	 * @return true if connection closed successfully, false if failed
	 */
	public static boolean disconnectFromDB() {
		if (dbConn == null) {
			System.out.println("disconect failed: the DB is not even connected yet!"); // remove after test
			return false;
		} else {
			try {
				dbConn.close();
				if (dbConn.isClosed()) {
					System.out.println("closed");// remove after test
					dbConn = null;
				}
			} catch (SQLException q) {
				q.printStackTrace();
				return false;
			} // catch (Exception e) {} <--remove after test
			System.out.println("Disconected from DB succsefuly");// remove after test
			return true;
		}
	}

	/**
	 * 
	 * @param data ArrayList of String cell [0]: command (in this case will always
	 *             be "insert") cell [1]: table name to insert to string cell [2]:
	 *             values that should be inserted NOTE: insert only full rows!
	 * @return true if update successful, false if failed
	 */
	public static boolean insert(ArrayList<String> data) {
		String tableName = data.get(1);
		String values = data.get(2);
		String statmentString = ("INSERT into " + dbScheme + "." + tableName + " values (" + values + ")");
		return execute(statmentString, data);

	}

	/**
	 * insert query for DB NOTE: insert only full rows!
	 * 
	 * @param data ArrayList of String cell [0]: command (in this case will always
	 *             be "delete") cell [1]: table name to delete from, cell [2]:
	 *             primary key column name of that table, cell [3]: primary key
	 *             value of the row that need to be deleted,
	 * @return true if update successful, false if failed
	 */
	public static boolean delete(ArrayList<String> data) {
		String tableName = data.get(1);
		String primaryKey = data.get(2);
		String pkValue = data.get(3);
		String StatmentString = ("DELETE FROM " + dbScheme + "." + tableName + " WHERE (" + primaryKey + " = " + pkValue
				+ ")");
		return execute(StatmentString, data);
	}

	/**
	 * delete query for DB
	 * 
	 * @param data ArrayList of String cell [0]: command (in this case will always
	 *             be "delete") cell [1]: table name to delete from cell [2]:
	 *             primary key column name of that table cell [3]: primary key value
	 *             of the row that need to be deleted
	 * @return true if update successful, false if failed
	 */
	public static boolean deleteCond(ArrayList<String> data) {
		String tableName = data.get(1);
		String cond = data.get(2);
		String StatmentString = ("DELETE FROM " + dbScheme + "." + tableName + " WHERE " + cond);
		return execute(StatmentString, data);
	}

	/**
	 * update query for DB
	 * 
	 * @param data ArrayList of String cell [0]: command (in this case will always
	 *             be "update"), cell [1]: table name to update data in cell, [2]:
	 *             column names and value to put inside them (need to look
	 * @return true if update successful, false if failed
	 */
	public static boolean update(ArrayList<String> data) {
		String tableName = data.get(1);
		String values = data.get(2);
		String primaryKey = data.get(3);
		String pkValue = data.get(4);
		String StatmentString = ("UPDATE " + dbScheme + "." + tableName + " SET " + values + " WHERE (" + primaryKey
				+ " = '" + pkValue + "')");
		return execute(StatmentString, data);
	}

	/**
	 * execute query for DB, NOTE: the data ArrayList may contain more than two
	 * commands, but this is the only one we uses.
	 * 
	 * @param StatmentString String string of the DB command to execute, it was
	 *                       build by either in 'update' 'delete' or 'insert' who
	 *                       call this method
	 * @param data           ArrayList<String> cell [0]: command (in this case will
	 *                       always be "update"), cell [1]: table name to delete
	 *                       from
	 * @return true if execution successful, false if failed
	 */
	private static boolean execute(String StatmentString, ArrayList<String> data) {
		String command = data.get(0);
		String tableName = data.get(1);
		int r;
		try {
			Statement s = dbConn.createStatement();
			r = s.executeUpdate(StatmentString);
		} catch (Exception e) {
			System.out.println("Oh no...\n" + e);
			return false;
		}
		System.out.println("executed " + command + " on table " + tableName + " (affected " + r + " rows)");
		return true;
	}


	/**
	 * select query for DB, NOTE: even if you don't want to filter you will need to
	 * provide an empty string on cell [3]
	 * 
	 * @param data ArrayList of String cell [0]: command (in this case will always
	 *             be "select"), cell [1]: table name to select from, cell [2]:
	 *             columns that you want select to return (example: "*" for all,
	 *             "ID,email" for those only two columns), cell [3]: condition can
	 *             contain a filter for a more refine search (example: "WHERE
	 *             ID='1234'", "" if you don't want a filter), cell [4]: the number
	 *             of columns that you want select to return (important! provide a
	 *             string of that number! i.e "7")
	 * 
	 * @return in case of success returns ArrayList that contains ArrayList of
	 *         Strings = each ArrayList of string represent a row in the DB replayed
	 *         all the rows are packed together in an ArrayList that contains
	 *         ArrayList of strings in case of failure returns null
	 */
	public static ArrayList<ArrayList<String>> select(ArrayList<String> data) {
		String tableName = data.get(1);
		String columns = data.get(2);
		String condition = data.get(3);
		int replyColNum = Integer.parseInt(data.get(4));
		ArrayList<ArrayList<String>> reply = new ArrayList<ArrayList<String>>();
		String StatmentString = ("SELECT " + columns + " FROM ");
		if (!data.get(0).equals("complexSelect"))
			StatmentString += dbScheme + ".";
		StatmentString += tableName + " " + condition;
		try {
			ResultSet rs = dbConn.createStatement().executeQuery(StatmentString);
			while (rs.next()) {
				ArrayList<String> row = new ArrayList<String>();
				for (int i = 1; i < replyColNum + 1; i++) {
					row.add(rs.getString(i));
				}
				reply.add(row);
			}
		} catch (Exception e) {
			System.out.println("Oh no...\n" + e);
			System.out.println(e.getStackTrace());
			return null;
		}
		return reply;
	}

	public static Connection getDbConn() {
		return dbConn;
	}

	public static void setDbConn(Connection dbConn) {
		MySQLConnection.dbConn = dbConn;
	}

}
