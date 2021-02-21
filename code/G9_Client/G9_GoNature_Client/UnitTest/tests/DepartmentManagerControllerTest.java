package tests;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.jfoenix.controls.JFXDatePicker;
import controllers.DepartmentManagerController;
import controllers.IDataBaseManager;
import controllers.IDates;
import controllers.IEmpty;
import controllers.IRecievedFromServer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.PieChart;

public class DepartmentManagerControllerTest {

	/*
	 * This is a stub class. This class meant to recieved array lists from the
	 * server data base.
	 */
	public class StubRecievedFromServer implements IRecievedFromServer {

		@Override
		public void setRegularVisitorsToChart(ArrayList<Object> msgReceived) {
			ArrayList<Object> regular = new ArrayList<>();
			regular.add("regular");
			regular.add(1.61);
			regular.add(11.29);
			regular.add(56.45);
			regular.add(30.65);
			DepartmentManagerController.setRegularVisitors(regular);

		}

		@Override
		public void setMemberVisitorsToChart(ArrayList<Object> msgReceived) {
			ArrayList<Object> member = new ArrayList<>();
			member.add("member");
			member.add(50.0);
			member.add(16.67);
			member.add(33.33);
			member.add(0.0);
			DepartmentManagerController.setMemberVisitors(member);

		}

		@Override
		public void setGroupVisitorsToChart(ArrayList<Object> msgReceived) {
			ArrayList<Object> group = new ArrayList<>();
			group.add("group");
			group.add(25.81);
			group.add(0.0);
			group.add(25.81);
			group.add(48.39);
			DepartmentManagerController.setGroupVisitors(group);

		}
	}

	/*
	 * This is a stub class. This class meant to sent array list to the server.
	 */
	public class StubDataBaseManager implements IDataBaseManager {

		@Override
		public void sendToServer(String caseName, ArrayList<String> date) {
			System.out.println("send To server details");
		}

	}

	/*
	 * This is a stub class. This class meant to check correct dates that show in
	 * the data base
	 */
	public class StubDatesDecember implements IDates {

		@Override
		public boolean checkLocalDate(LocalDate from, LocalDate to) {
			DepartmentManagerController DMC = new DepartmentManagerController();
			LocalDate fromDate = LocalDate.parse("2020-12-01");
			LocalDate toDate = LocalDate.parse("2020-12-10");
			return DMC.checkDate(fromDate, toDate);
		}

		@Override
		public LocalDate getValueDate(JFXDatePicker date) {
			LocalDate toDate = LocalDate.parse("2020-12-10");
			return toDate;
		}
	}

	/*
	 * This is a stub class. This class meant to check correct dates that doesnts
	 * show in the data base
	 */
	public class StubDatesAnoterYear implements IDates {

		@Override
		public boolean checkLocalDate(LocalDate from, LocalDate to) {
			DepartmentManagerController DMC = new DepartmentManagerController();
			LocalDate fromDate = LocalDate.parse("2018-12-01");
			LocalDate toDate = LocalDate.parse("2018-12-10");
			return DMC.checkDate(fromDate, toDate);
		}

		@Override
		public LocalDate getValueDate(JFXDatePicker date) {
			LocalDate toDate = LocalDate.parse("2018-12-10");
			return toDate;
		}
	}

	/*
	 * This is a stub class. This class meant to recieved string from the server
	 * data base and to set boolean value to T/F.
	 */
	public class StubEmpty implements IEmpty {

		@Override
		public boolean isEmptyFromSErver() {
			return value;
		}

		@Override
		public void setEmptyToChart(boolean isEmpty) {
			value = isEmpty;
		}

	}

	/******************************
	 * Test section start here
	 ******************************************/

	private boolean value;
	private IEmpty empty;
	private IDates dateDecember, dateAnotherYear;
	private IDataBaseManager db;
	private IRecievedFromServer rs;
	private DepartmentManagerController dpcExist, dpcNotExist;
	private ActionEvent event;
	private ObservableList<PieChart.Data> actualResult;
	private ObservableList<PieChart.Data> expectedResult;

	@Before
	public void setUp() throws Exception {
		value = false;
		empty = new StubEmpty();
		dateDecember = new StubDatesDecember();
		db = new StubDataBaseManager();
		rs = new StubRecievedFromServer();
		dpcExist = new DepartmentManagerController(db, dateDecember, empty, rs);
		actualResult = FXCollections.observableArrayList();
		expectedResult = FXCollections.observableArrayList();
		dateAnotherYear = new StubDatesAnoterYear();
		dpcNotExist = new DepartmentManagerController(db, dateAnotherYear, empty, rs);

	}

	/*
	 * Test case for a size Pai Chart Regular type traveler that have data in the
	 * database. input: value=false, regular = "reglar" fromDate:"2020-12-01
	 * ToDate:2020-12-10 actualResult= dpc.DataRegular.size() expectedResult =
	 * currentexpecteVisitorsData.size()=4
	 */
	@Test
	public void successTestPaiChartSizeRegular() throws Exception {
		db.sendToServer("getRegularsVisitorsData", new ArrayList<String>());
		value = false;
		ArrayList<Object> regular = new ArrayList<Object>();
		regular.add("reglar");
		rs.setRegularVisitorsToChart(regular);
		dpcExist.showPieChart(event);
		actualResult = dpcExist.DataRegular;
		ObservableList<PieChart.Data> currentexpecteVisitorsData = FXCollections.observableArrayList();
		currentexpecteVisitorsData.add(new PieChart.Data("0-1 hours, 1.61%", 1.61));
		currentexpecteVisitorsData.add(new PieChart.Data("1-2 hours, 11.29%", 11.29));
		currentexpecteVisitorsData.add(new PieChart.Data("2-3 hours, 56.45%", 56.45));
		currentexpecteVisitorsData.add(new PieChart.Data("3-4 hours, 30.65%", 30.65));
		expectedResult = currentexpecteVisitorsData;
		assertEquals(expectedResult.size(), actualResult.size());
	}

	/*
	 * Test case for a Pai Chart Regular type traveler that have data in the
	 * database. input: value =false, regular = "reglar" fromDate:"2020-12-01
	 * ToDate:2020-12-10 actualResult= dpc.DataRegular expectedResult =
	 * currentexpecteVisitorsData
	 */
	@Test
	public void successTestPaiChartValuesRegular() throws Exception {
		db.sendToServer("getRegularsVisitorsData", new ArrayList<String>());
		value = false;
		ArrayList<Object> regular = new ArrayList<Object>();
		regular.add("reglar");
		rs.setRegularVisitorsToChart(regular);
		dpcExist.showPieChart(event);
		actualResult = dpcExist.DataRegular;
		ObservableList<PieChart.Data> currentexpecteVisitorsData = FXCollections.observableArrayList();
		currentexpecteVisitorsData.add(new PieChart.Data("0-1 hours, 1.61%", 1.61));
		currentexpecteVisitorsData.add(new PieChart.Data("1-2 hours, 11.29%", 11.29));
		currentexpecteVisitorsData.add(new PieChart.Data("2-3 hours, 56.45%", 56.45));
		currentexpecteVisitorsData.add(new PieChart.Data("3-4 hours, 30.65%", 30.65));
		expectedResult = currentexpecteVisitorsData;
		assertEquals(expectedResult.get(0).getPieValue(), actualResult.get(0).getPieValue(), 0);
		assertEquals(expectedResult.get(1).getPieValue(), actualResult.get(1).getPieValue(), 0);
		assertEquals(expectedResult.get(2).getPieValue(), actualResult.get(2).getPieValue(), 0);
		assertEquals(expectedResult.get(3).getPieValue(), actualResult.get(3).getPieValue(), 0);
	}

	/*
	 * Test case for a Pai Chart Regular type traveler that lack data in the
	 * database. input: value=true,regular = "reglar" fromDate:"2018-12-01
	 * ToDate:2018-12-10 actualResult=dpc.DataRegular expectedResult = null
	 */
	@Test
	public void FaildTestPaiChartEmptyRegular() throws Exception {
		try {
			db.sendToServer("getRegularsVisitorsData", new ArrayList<String>());
			value = true;
			ArrayList<Object> regular = new ArrayList<Object>();
			regular.add("reglar");
			rs.setRegularVisitorsToChart(regular);
			dpcNotExist.showPieChart(event);
			actualResult = dpcNotExist.DataRegular;

			fail("expected exception");
		} catch (NullPointerException e) {

			assertEquals(null, e.getMessage());
		}
	}

	/*
	 * Test case for a size Pai Chart Member type traveler that have data in the
	 * database. input: value=false, member = "member" fromDate:"2020-12-01
	 * ToDate:2020-12-10 actualResult= dpc.DataMember.size() expectedResult =
	 * currentexpecteVisitorsData.size()=3
	 */
	@Test
	public void successTestPaiChartSizeMember() throws Exception {
		db.sendToServer("getMembersVisitorsData", new ArrayList<String>());
		value = false;
		ArrayList<Object> member = new ArrayList<Object>();
		member.add("member");
		rs.setMemberVisitorsToChart(member);
		dpcExist.showPieChart(event);
		actualResult = dpcExist.DataMember;
		ObservableList<PieChart.Data> currentexpecteVisitorsData = FXCollections.observableArrayList();
		currentexpecteVisitorsData.add(new PieChart.Data("0-1 hours, 50.0%", 50.0));
		currentexpecteVisitorsData.add(new PieChart.Data("1-2 hours, 16.67%", 16.67));
		currentexpecteVisitorsData.add(new PieChart.Data("2-3 hours, 33.33%", 33.33));
		// currentexpecteVisitorsData.add(new PieChart.Data("3-4 hours, 0.0%", 0.0));
		expectedResult = currentexpecteVisitorsData;
		assertEquals(expectedResult.size(), actualResult.size());

	}

	/*
	 * Test case for a Pai Chart Member type traveler that have data in the
	 * database. input: value =false, member = "member" fromDate:"2020-12-01
	 * ToDate:2020-12-10 actualResult= dpc.DataMember expectedResult =
	 * currentexpecteVisitorsData
	 */
	@Test
	public void successTestPaiChartValuesMember() throws Exception {
		db.sendToServer("getMembersVisitorsData", new ArrayList<String>());
		value = false;
		ArrayList<Object> member = new ArrayList<Object>();
		member.add("member");
		rs.setMemberVisitorsToChart(member);
		dpcExist.showPieChart(event);
		actualResult = dpcExist.DataMember;
		ObservableList<PieChart.Data> currentexpecteVisitorsData = FXCollections.observableArrayList();
		currentexpecteVisitorsData.add(new PieChart.Data("0-1 hours, 50.0%", 50.0));
		currentexpecteVisitorsData.add(new PieChart.Data("1-2 hours, 16.67%", 16.67));
		currentexpecteVisitorsData.add(new PieChart.Data("2-3 hours, 33.33%", 33.33));
		// currentexpecteVisitorsData.add(new PieChart.Data("3-4 hours, 0.0%", 0.0)); -
		// do not enter to the ObservableList because it zero
		expectedResult = currentexpecteVisitorsData;
		assertEquals(expectedResult.get(0).getPieValue(), actualResult.get(0).getPieValue(), 0);
		assertEquals(expectedResult.get(1).getPieValue(), actualResult.get(1).getPieValue(), 0);
		assertEquals(expectedResult.get(2).getPieValue(), actualResult.get(2).getPieValue(), 0);
	}

	/*
	 * Test case for a Pai Chart Member type traveler that lack data in the
	 * database. input: value=true,member = "member" fromDate:"2018-12-01
	 * ToDate:2018-12-10 actualResult=dpc.DataGroup expectedResult = null
	 */
	@Test
	public void FaildTestPaiChartEmptyMember() throws Exception {
		try {
			db.sendToServer("getMembersVisitorsData", new ArrayList<String>());
			value = true;
			ArrayList<Object> member = new ArrayList<Object>();
			member.add("member");
			rs.setRegularVisitorsToChart(member);
			dpcNotExist.showPieChart(event);
			actualResult = dpcNotExist.DataMember;

			fail("expected exception");
		} catch (NullPointerException e) {

			assertEquals(null, e.getMessage());
		}
	}

	/*
	 * Test case for a size Pai Chart Group type traveler that have data in the
	 * database. input: value=false, group = "group" fromDate:"2020-12-01
	 * ToDate:2020-12-10 actualResult= dpc.DataGroup.size() expectedResult =
	 * currentexpecteVisitorsData.size()=3
	 */
	@Test
	public void successTestPaiChartSizeGroup() throws Exception {
		db.sendToServer("getGroupsVisitorsData", new ArrayList<String>());
		value = false;
		ArrayList<Object> group = new ArrayList<Object>();
		group.add("group");
		rs.setGroupVisitorsToChart(group);
		dpcExist.showPieChart(event);
		actualResult = dpcExist.DataGroup;

		ObservableList<PieChart.Data> currentexpecteVisitorsData = FXCollections.observableArrayList();
		currentexpecteVisitorsData.add(new PieChart.Data("0-1 hours, 25.81%", 25.81));
		// currentexpecteVisitorsData.add(new PieChart.Data("1-2 hours, 0.07%", 0.0));-
		// do not enter to the ObservableList because it zero
		currentexpecteVisitorsData.add(new PieChart.Data("2-3 hours, 25.81%", 25.81));
		currentexpecteVisitorsData.add(new PieChart.Data("3-4 hours, 48.39%", 48.39));
		expectedResult = currentexpecteVisitorsData;
		assertEquals(expectedResult.size(), actualResult.size());

	}

	/*
	 * Test case for a Pai Chart Group type traveler that have data in the database.
	 * input: value =false, group = "group" fromDate:"2020-12-01 ToDate:2020-12-10
	 * actualResult= dpc.DataGroup expectedResult = currentexpecteVisitorsData
	 */
	@SuppressWarnings("static-access")
	@Test
	public void successTestPaiChartValuesGroup() throws Exception {
		db.sendToServer("getGroupsVisitorsData", new ArrayList<String>());
		value = false;
		ArrayList<Object> group = new ArrayList<Object>();
		group.add("member");
		rs.setGroupVisitorsToChart(group);
		dpcExist.showPieChart(event);
		actualResult = dpcExist.DataGroup;
		ObservableList<PieChart.Data> currentexpecteVisitorsData = FXCollections.observableArrayList();
		currentexpecteVisitorsData.add(new PieChart.Data("0-1 hours, 25.81%", 25.81));
		// currentexpecteVisitorsData.add(new PieChart.Data("1-2 hours, 0.07%", 0.0));-
		// do not enter to the ObservableList because it zero
		currentexpecteVisitorsData.add(new PieChart.Data("2-3 hours, 25.81%", 25.81));
		currentexpecteVisitorsData.add(new PieChart.Data("3-4 hours, 48.39%", 48.39));
		expectedResult = currentexpecteVisitorsData;
		assertEquals(expectedResult.get(0).getPieValue(), actualResult.get(0).getPieValue(), 0);
		assertEquals(expectedResult.get(1).getPieValue(), actualResult.get(1).getPieValue(), 0);
		assertEquals(expectedResult.get(2).getPieValue(), actualResult.get(2).getPieValue(), 0);
	}

	/*
	 * Test case for a Pai Chart Group type traveler that lack data in the database.
	 * input: value=true,group = "group" fromDate:"2018-12-01 ToDate:2018-12-10
	 * actualResult=dpc.DataGroupr expectedResult = null
	 */
	@Test
	public void FaildTestPaiChartEmptyGRoup() throws Exception {
		try {
			db.sendToServer("getGroupsVisitorsData", new ArrayList<String>());
			value = true;
			ArrayList<Object> group = new ArrayList<Object>();
			group.add("group");
			rs.setRegularVisitorsToChart(group);
			dpcNotExist.showPieChart(event);
			actualResult = dpcNotExist.DataGroup;

			fail("expected exception");
		} catch (NullPointerException e) {

			assertEquals(null, e.getMessage());
		}
	}

}
