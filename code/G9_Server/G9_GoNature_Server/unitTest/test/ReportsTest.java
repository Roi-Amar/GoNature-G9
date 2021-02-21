package test;

import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import ocsf.server.ConnectionToClient;
import server.MySQLConnection;
import server.Reports;

/**
 * @Author Anastasia Kokin
 */

public class ReportsTest {

	private ArrayList<Object> acctualResult;
	public static ConnectionToClient client;

	@Before
	public void setUp() throws Exception {
		// setting a connection for DB without the Gui
		ArrayList<String> data = new ArrayList<String>(
				Arrays.asList("localhost", "3306", "g9_gonature", "root", "NewP@ssword4theSQL"));
		MySQLConnection.connectToDB(data);

		acctualResult = new ArrayList<Object>();
	}

	//a test for good outcome. right input
	@Test
	public void testGoodOutcome() {
		ArrayList<Object> recived = new ArrayList<Object>();
		recived.add("getRegularsVisitorsData");
		ArrayList<String> a = new ArrayList<String>();

		a.add("2020-12-01");
		a.add("2020-12-10");
		a.add("member");
		recived.add(a);

		acctualResult = Reports.getVisitsReportData(recived);
		assertEquals(5, acctualResult.size());
	}

	//test for no data came back from the DB. returns empty
	@Test
	public void testNoDataToPresrntFromDB() {
		ArrayList<Object> recived = new ArrayList<Object>();
		recived.add("getRegularsVisitorsData");
		ArrayList<String> a = new ArrayList<String>();

		a.add("2022-11-01");
		a.add("2022-11-10");
		a.add("member");
		recived.add(a);

		acctualResult = Reports.getVisitsReportData(recived);
		assertEquals(2, acctualResult.size());
	}

	//test for no good connection to the DB
	@Test
	public void testNoConnectionToDB() {
		MySQLConnection.setDbConn(null);
		ArrayList<Object> recived = new ArrayList<Object>();
		recived.add("getRegularsVisitorsData");
		ArrayList<String> a = new ArrayList<String>();

		a.add("2020-12-01");
		a.add("2020-12-10");
		a.add("member");
		recived.add(a);

		acctualResult = Reports.getVisitsReportData(recived);
		assertEquals(null, acctualResult);
	}
}
