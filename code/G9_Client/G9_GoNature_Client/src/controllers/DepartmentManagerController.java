package controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXDatePicker;

import client.ClientUI;
import dataLayer.TableCurrentVisitors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import managerData.ManagerRequest;
import managerData.TableViewSet;

/**
 *
 * controller that coordinator all the kinds of alert that we use in the project
 *
 * @author Haim Azulay, Bar Katz
 */

public class DepartmentManagerController implements Initializable {
	/***** Side Bar *****/
	@FXML
	private Button btnDashboard;
	@FXML
	private Button btnVisitsReport;
	@FXML
	private Button btnCancelsReport;

	/***** Top Title *****/
	@FXML
	private Label lblTitle;
	@FXML
	private Label lblFirstNameTitle;

	/***** Panels *****/
	@FXML
	private Pane pnDashboard;
	@FXML
	private Pane pnVisits;
	@FXML
	private Pane pnCancels;

	/***** Dashboard *****/
	@FXML
	private TableView<TableViewSet> TableDep;
	@FXML
	private TableColumn<TableViewSet, String> parkName;
	@FXML
	private TableColumn<TableViewSet, String> requestType;
	@FXML
	private TableColumn<TableViewSet, String> requestDetails;
	@FXML
	private TableColumn<TableViewSet, String> mark;

	@FXML
	private TableView<TableCurrentVisitors> TVisitors;

	@FXML
	private TableColumn<TableCurrentVisitors, String> visitorColumn;

	@FXML
	private TableColumn<TableCurrentVisitors, String> amountColumn;

	@FXML
	private Label LabelCount;
	@FXML
	private Button btnApprove;
	@FXML
	private Button btnDisaprove;

	/***** Cancel Report *****/
	@FXML
	private BarChart<String, Double> bcCancells;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	@FXML
	private JFXDatePicker dpCancelFrom;
	@FXML
	private JFXDatePicker dpCancelTo;
	@FXML
	private Button btnCancelShow;
	@FXML
	private Button btnLogout;

	/***** Visits Report *****/
	@FXML
	private JFXDatePicker dpVisitorsFrom;
	@FXML
	private JFXDatePicker dpVisitorsTo;
	// @FXML
	// private PieChart pieRegular;
	@FXML
	public PieChart pieRegular;
	// @FXML
	// private PieChart pieMember;
	@FXML
	public PieChart pieMember;
	// @FXML
	// private PieChart pieGroup;
	@FXML
	public PieChart pieGroup;
	@FXML
	private Label lblRegular;
	@FXML
	private Label lblMember;
	@FXML
	private Label lblGroup;

	/***** Global Variables *****/
	private static String firstName;
	private AlertController alert = new AlertController();
	private static ArrayList<ArrayList<String>> parkDetails;

	/***** Dashboard Variables *****/
	// private ArrayList<Object> data = new ArrayList<>();
	private static ArrayList<ArrayList<String>> DBList = new ArrayList<>();
	private static ArrayList<ArrayList<Object>> status;
	private int count = 0;

	/***** Visits Report Variables *****/
	private static ArrayList<Double> regularVisitors = new ArrayList<>();
	private static ArrayList<Double> memberVisitors = new ArrayList<>();
	private static ArrayList<Double> groupVisitors = new ArrayList<>();
	private static boolean isEmpty = false;

	/***** Cancel Report Variables *****/
	private static ArrayList<ArrayList<String>> cancelledOrders = new ArrayList<>();
	private static boolean error = false;
	private int index = 0;

	/**
	 * this function managed the side bar input: button source that pressed output:
	 * switch to the relevant pane
	 * 
	 * @param event ActionEvent
	 */
	@FXML
	public void handleSideBar(ActionEvent event) {
		if (event.getSource() == btnDashboard) {
			lblTitle.setText("Dashboard");
			pnDashboard.toFront();
			setButtonPressed(btnDashboard);
			setButtonReleased(btnVisitsReport, btnCancelsReport);
		} else if (event.getSource() == btnVisitsReport) {
			lblTitle.setText("Visits Report");
			pnVisits.toFront();
			setButtonPressed(btnVisitsReport);
			setButtonReleased(btnDashboard, btnCancelsReport);
		} else if (event.getSource() == btnCancelsReport) {
			lblTitle.setText("Cancels Report");
			pnCancels.toFront();
			setButtonPressed(btnCancelsReport);
			setButtonReleased(btnDashboard, btnVisitsReport);
		}
	}

	/**
	 * Moves to the 'login' screen
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	void logout(ActionEvent event) throws IOException {
		// Data fields
		ArrayList<String> data = new ArrayList<String>();
		// Query
		ArrayList<Object> msg = new ArrayList<Object>();

		msg.add("updateLoggedIn");
		// update as loggedin as logged out
		data.add(LoginController.getUsername());
		data.add(String.valueOf(0));
		// Data fields
		msg.add(data);
		// set up all the order details and the payment method
		ClientUI.sentToChatClient(msg);

		Stage stage = (Stage) btnLogout.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
		stage.setScene(new Scene(root));
	}

	/**
	 * checks if the date is correct and displays the chart if so
	 * 
	 * @param event
	 */
	@FXML
	void show(ActionEvent event) {
		ArrayList<String> data = new ArrayList<>();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// the dates are correct
		if (checkDate(dpCancelFrom.getValue(), dpCancelTo.getValue().plusDays(1))) {
			String fromFormat = dateTimeFormatter.format(dpCancelFrom.getValue());
			String toFormat = dateTimeFormatter.format(dpCancelTo.getValue().plusDays(1));
			data.add(fromFormat);
			data.add(toFormat);
			sendToServerArrayList("getCancellationReports", data);

			if (!getError()) {
				// show all data
				barChart();
			} else {
				alert.failedAlert("Failed", "There is no information on the dates you requested.");
				bcCancells.getData().clear();
			}
		} else {
			alert.failedAlert("Failed", "Start date cannot be greater than end date.");
			bcCancells.getData().clear();
		}
	}

	/**
	 * checks if the date is correct and displays the chart if so creates a PDF file
	 * based on data retrieved from the DB input: from = start date to = end date
	 * ArrayList<ArrayList<String>> cancelledOrders, cells: cell [i][0]: parkName
	 * cell [i][1]: date of canceled/dismissed cell [i][2]: amount 0 <= i <=
	 * cancelledOrders.size() - 1 output: PDF report with all the data shown in the
	 * chart
	 * 
	 * @param event
	 */
	@FXML
	void export(ActionEvent event) {
		// call the function to fill the cancelledOrders data
		show(event);

		// cancelledOrders is empty
		if (getError()) {
			return;
		}

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String fromFormat = dateFormatter.format(dpCancelFrom.getValue());
		String toFormat = dateFormatter.format(dpCancelTo.getValue());
		// the date it was created
		String fileNameDate = fileNameFormatter.format(LocalDate.now());

		Font titleFont = FontFactory.getFont(FontFactory.COURIER, 18, Font.BOLD, new BaseColor(46, 139, 87));
		try {
			Document document = new Document();
			// creates a report with the name ==> CanceledReport 'yyyy-MM-dd'.pdf
			// the 'yyyy-MM-dd' is the date it was created
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream("CanceledReport " + fileNameDate + ".pdf"));
			document.open();

			Paragraph title = new Paragraph("Cancellation/Dismissed Report\n", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			Paragraph date = new Paragraph(new Date().toString() + "\n\n");
			date.setAlignment(Element.ALIGN_CENTER);
			document.add(date);

			PdfPTable table = new PdfPTable(3);
			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

			PdfPCell titleCell = new PdfPCell(new Paragraph(fromFormat + " - " + toFormat));
			titleCell.setColspan(3);
			titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			titleCell.setBackgroundColor(BaseColor.GRAY);
			// the title of the table
			table.addCell(titleCell);

			PdfPCell parkName = new PdfPCell(new Paragraph("Park Name"));
			parkName.setColspan(1);
			parkName.setHorizontalAlignment(Element.ALIGN_CENTER);
			parkName.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(parkName);

			PdfPCell missedTime = new PdfPCell(new Paragraph("Missed Time"));
			missedTime.setColspan(1);
			missedTime.setHorizontalAlignment(Element.ALIGN_CENTER);
			missedTime.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(missedTime);

			PdfPCell amount = new PdfPCell(new Paragraph("Amount"));
			amount.setColspan(1);
			amount.setHorizontalAlignment(Element.ALIGN_CENTER);
			amount.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(amount);

			for (int i = 0; i < cancelledOrders.size(); i++) {
				table.addCell(cancelledOrders.get(i).get(0));
				table.addCell(cancelledOrders.get(i).get(1));
				table.addCell(cancelledOrders.get(i).get(2));
			}
			document.add(table);

			alert.successAlert("Success", "The report was created successfully.");

			Desktop.getDesktop().open(new File("CanceledReport " + fileNameDate + ".pdf"));

			document.close();
			writer.close();

		} catch (Exception e) {
			alert.failedAlert("Failed", "It looks like the file is already open, close it and try again.");
		}
	}

	/**
	 * creates a PDF file based on data retrieved from the DB input: from = start
	 * date to = end date ArrayList (Double)
	 * regularVisitors/memberVisitors/groupVisitors, cells: cell [0]: percentage of
	 * visitors between the hours 0 to 1 cell [1]: percentage of visitors between
	 * the hours 1 to 2 cell [2]: percentage of visitors between the hours 2 to 3
	 * cell [3]: percentage of visitors between the hours 3 to 4 output: PDF report
	 * with all the data shown in the chart
	 * 
	 * @param event
	 * @throws Exception 
	 */
	@FXML
	void exportPieChart(ActionEvent event) throws Exception {
		// call the function to fill the cancelledOrders data
		showPieChart(event);

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String fromFormat = dateFormatter.format(dpVisitorsFrom.getValue());
		String toFormat = dateFormatter.format(dpVisitorsTo.getValue());
		// the date it was created
		String fileNameDate = fileNameFormatter.format(LocalDate.now());

		Font titleFont = FontFactory.getFont(FontFactory.COURIER, 18, Font.BOLD, new BaseColor(46, 139, 87));
		try {
			Document document = new Document();
			// creates a report with the name ==> VisitorsReport 'yyyy-MM-dd'.pdf
			// the 'yyyy-MM-dd' is the date it was created
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream("VisitorsReport " + fileNameDate + ".pdf"));
			document.open();

			Paragraph title = new Paragraph("Visitors Report\n", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			Paragraph date = new Paragraph(new Date().toString() + "\n\n");
			date.setAlignment(Element.ALIGN_CENTER);
			document.add(date);

			for (int i = 0; i < 3; i++) {
				String type = "";
				ArrayList<Double> currentType = null;

				if (i == 0) {
					if (regularVisitors.isEmpty()) {
						continue;
					}
					type = "Regular";
					currentType = regularVisitors;
				} else if (i == 1) {
					if (memberVisitors.isEmpty()) {
						continue;
					}
					type = "Member";
					currentType = memberVisitors;
				} else if (i == 2) {
					if (groupVisitors.isEmpty()) {
						continue;
					}
					type = "Group";
					currentType = groupVisitors;
				}

				PdfPTable table = new PdfPTable(2);
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
				table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

				PdfPCell typeCell = new PdfPCell(new Paragraph(type));
				typeCell.setColspan(2);
				typeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				typeCell.setBackgroundColor(new BaseColor(46, 139, 87));
				// the title of the table
				table.addCell(typeCell);

				PdfPCell titleCell = new PdfPCell(new Paragraph(fromFormat + " - " + toFormat));
				titleCell.setColspan(2);
				titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				titleCell.setBackgroundColor(BaseColor.GRAY);
				// the title of the table
				table.addCell(titleCell);

				PdfPCell hours = new PdfPCell(new Paragraph("Hours"));
				hours.setColspan(1);
				hours.setHorizontalAlignment(Element.ALIGN_CENTER);
				hours.setBackgroundColor(BaseColor.LIGHT_GRAY);
				table.addCell(hours);

				PdfPCell arrivalRates = new PdfPCell(new Paragraph("Arrival Rates"));
				arrivalRates.setColspan(1);
				arrivalRates.setHorizontalAlignment(Element.ALIGN_CENTER);
				arrivalRates.setBackgroundColor(BaseColor.LIGHT_GRAY);
				table.addCell(arrivalRates);

				table.addCell("0-1");
				table.addCell(String.valueOf(currentType.get(0) + "%"));
				table.addCell("1-2");
				table.addCell(String.valueOf(currentType.get(1) + "%"));
				table.addCell("2-3");
				table.addCell(String.valueOf(currentType.get(2) + "%"));
				table.addCell("3-4");
				table.addCell(String.valueOf(currentType.get(3) + "%"));

				document.add(table);
				document.add(new Paragraph("\n"));
			}

			alert.successAlert("Success", "The report was created successfully.");

			Desktop.getDesktop().open(new File("VisitorsReport " + fileNameDate + ".pdf"));

			document.close();
			writer.close();

		} catch (Exception e) {
			alert.failedAlert("Failed", "It looks like the file is already open, close it and try again.");
		}
	}

	/******************************** For Testing *******************************/
	private IDataBaseManager dataBaseManager = new DataBaseManager();
	private IDates dates = new Dates();
	private IEmpty empty = new Empty();
	private IRecievedFromServer recievedFromServer = new RecievedFromServer();
	public static ObservableList<PieChart.Data> DataRegular = FXCollections.observableArrayList();
	public static ObservableList<PieChart.Data> DataMember = FXCollections.observableArrayList();
	public static ObservableList<PieChart.Data> DataGroup = FXCollections.observableArrayList();

	/**
	 * Bar
	 * constructor for DepartmentManagerController with no feilds
	 */
	public DepartmentManagerController() {
		dataBaseManager = new DataBaseManager();
		dates = new Dates();
		empty = new Empty();
		recievedFromServer = new RecievedFromServer();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Bar
	 * constructor for DepartmentManagerController with  feilds
	 * @param dataBaseManager call the server 
	 * @param dates check valid dates
	 * @param empty check the value string from server
	 * @param recievedFromServer check the value aray list object from server
	 */
	public DepartmentManagerController(IDataBaseManager dataBaseManager, IDates dates, IEmpty empty,
			IRecievedFromServer recievedFromServer) {
		super();
		this.dataBaseManager = dataBaseManager;
		this.dates = dates;
		this.empty = empty;
		this.recievedFromServer = recievedFromServer;
	}

	/**
	 * displays the chart for the information retrieved from DB input: from = start
	 * date to = end date ArrayList (Double)
	 * regularVisitors/memberVisitors/groupVisitors, cells: cell [0]: percentage of
	 * visitors between the hours 0 to 1 cell [1]: percentage of visitors between
	 * the hours 1 to 2 cell [2]: percentage of visitors between the hours 2 to 3
	 * cell [3]: percentage of visitors between the hours 3 to 4 output: displays
	 * the data
	 * 
	 * @param event
	 */
	@FXML
	public void showPieChart(ActionEvent event) throws Exception{
		ArrayList<String> data = new ArrayList<>();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		if (pieRegular != null && pieMember != null && pieGroup != null) {
			pieRegular.getData().clear();
			pieMember.getData().clear();
			pieGroup.getData().clear();
			lblRegular.setText("");
			lblMember.setText("");
			lblGroup.setText("");
		}

		// the dates are correct - new Bar
		// if(checkDate(dpVisitorsFrom.getValue(),
		// dpVisitorsTo.getValue().plusDays(1))))
		if (dates.checkLocalDate(dates.getValueDate(dpVisitorsFrom), dates.getValueDate(dpVisitorsTo).plusDays(1))) {
			String fromFormat = dateTimeFormatter.format(dates.getValueDate(dpVisitorsFrom));
			String toFormat = dateTimeFormatter.format(dates.getValueDate(dpVisitorsTo).plusDays(1));
			data.add(fromFormat);
			data.add(toFormat);
			data.add("regular");
			dataBaseManager.sendToServer("getRegularsVisitorsData", data);
			// sendToServerArrayList("getRegularsVisitorsData", data);-Bar

			// if (!isEmpty()) {-Bar
			if (!empty.isEmptyFromSErver()) {
				addPieChart(pieRegular, "Regular");
				// System.out.println("pieRegular" + pieRegular.getData());-Bar
			} else {
				lblRegular.setText("Regular\n\nThere is no\ninformation about\nthis group.");
			}
			empty.setEmptyToChart(false);
			// setEmpty(false);-Bar

			data.clear();
			data.add(fromFormat);
			data.add(toFormat);
			data.add("member");
			dataBaseManager.sendToServer("getMembersVisitorsData", data);
			// sendToServerArrayList("getMembersVisitorsData", data);-Bar

			// if (!isEmpty()) {-Bar
			if (!empty.isEmptyFromSErver()) {
				addPieChart(pieMember, "Member");
				// System.out.println("pieMember" + pieMember.getData());-Bar
			} else {
				lblMember.setText("Member\n\nThere is no\ninformation about\nthis group.");
			}
			empty.setEmptyToChart(false);
			// setEmpty(false);-Bar

			data.clear();
			data.add(fromFormat);
			data.add(toFormat);
			data.add("group");
			dataBaseManager.sendToServer("getGroupsVisitorsData", data);
			// sendToServerArrayList("getGroupsVisitorsData", data);-Bar

			// if (!isEmpty()) {-Bar
			if (!empty.isEmptyFromSErver()) {
				addPieChart(pieGroup, "Group");
			} else {
				lblGroup.setText("Group\n\nThere is no\ninformation about\nthis group.");
			}
			empty.setEmptyToChart(false);
			// setEmpty(false);-Bar
		}
	}

	
	/* Wrapper for  get server method */
	class RecievedFromServer implements IRecievedFromServer {

		/* Wrapper for setRegularVisitors method */
		@Override
		public void setRegularVisitorsToChart(ArrayList<Object> msgReceived) {
			DepartmentManagerController.setRegularVisitors(msgReceived);

		}

		/* Wrapper for setMemberVisitors method */
		@Override
		public void setMemberVisitorsToChart(ArrayList<Object> msgReceived) {
			DepartmentManagerController.setMemberVisitors(msgReceived);

		}

		/* Wrapper for setGroupVisitors method */
		@Override
		public void setGroupVisitorsToChart(ArrayList<Object> msgReceived) {
			DepartmentManagerController.setGroupVisitors(msgReceived);

		}

	}

	/* Wrapper for sendToServer method */
	class DataBaseManager implements IDataBaseManager {

		@Override
		public void sendToServer(String caseName, ArrayList<String> date) {
			DepartmentManagerController DPMC = new DepartmentManagerController();
			DPMC.sendToServerArrayList(caseName, date);

		}

	}

	/* Wrapper for Local Date method */
	class Dates implements IDates {

		/* Wrapper for checkLocalDate method */
		@Override
		public boolean checkLocalDate(LocalDate from, LocalDate to) {
			DepartmentManagerController DMC = new DepartmentManagerController();
			return DMC.checkDate(from, to);
		}

		/* Wrapper for getValueDate method */
		@Override
		public LocalDate getValueDate(JFXDatePicker date) {
			if (date != null)
				return date.getValue();
			// for UnitTest
			LocalDate toDate = LocalDate.parse("2020-12-10");
			return toDate;
		}

	}

	/* Wrapper for Empty String from server method */
	class Empty implements IEmpty {

		@Override
		public boolean isEmptyFromSErver() {
			return DepartmentManagerController.isEmpty();
		}

		@Override
		public void setEmptyToChart(boolean isEmpty) {
			DepartmentManagerController.setEmpty(isEmpty);
		}

	}

	/**
	 * check that the inserted dates are correct
	 * 
	 * @param from LocalDate
	 * @param to   LocalDate
	 * @return true if the date is valid, false otherwise
	 */
	public boolean checkDate(LocalDate from, LocalDate to) {

		// the dates are correct
		if (from.isBefore(to) || from.isEqual(to)) {
			return true;
		}

		return false;
	}

	/**
	 * adding a specific chart
	 * 
	 * cell [0]: percentage of visitors between the hours 0 to 1 cell [1]:
	 * percentage of visitors between the hours 1 to 2 cell [2]: percentage of
	 * visitors between the hours 2 to 3 cell [3]: percentage of visitors between
	 * the hours 3 to 4
	 * 
	 * @param currentPie PieChart
	 * @param title      String
	 */
	public void addPieChart(PieChart currentPie, String title) {

		ObservableList<PieChart.Data> currentVisitorsData = null;

		switch (title) {
		case "Regular":
			/****** change - Bar ******/
			// lblRegular.setText("Regular");
			// System.out.println("regularVisitors : " + regularVisitors);
			DataRegular = getChartData(regularVisitors);
			currentVisitorsData = FXCollections.observableArrayList(DataRegular);

			// currentVisitorsData =
			// FXCollections.observableArrayList(getChartData(regularVisitors));
			break;
		case "Member":
			/****** change - Bar ******/
			// lblMember.setText("Member");
			// System.out.println("memberVisitors : " + memberVisitors);
			DataMember = getChartData(memberVisitors);
			currentVisitorsData = FXCollections.observableArrayList(DataMember);
			break;
		case "Group":
			/****** change - Bar ******/
			// lblGroup.setText("Group");
			// System.out.println("groupVisitors : " + groupVisitors);
			DataGroup = getChartData(groupVisitors);
			currentVisitorsData = FXCollections.observableArrayList(DataGroup);
			break;
		default:
			return;
		}

		// check if currentPie null - chenged
		if (currentPie != null) {
			currentPie.getData().clear();
			// setting the length of the label line
			currentPie.setLabelLineLength(5);
			currentPie.setClockwise(false);
			currentPie.setAnimated(false);
			currentPie.setLegendVisible(false);
			lblGroup.setText("Group");
			lblMember.setText("Member");
			lblRegular.setText("Regular");
			currentPie.setData(currentVisitorsData);
		}

	}

	public ObservableList<PieChart.Data> getChartData(ArrayList<Double> currentList) {

		ObservableList<PieChart.Data> currentVisitorsData = FXCollections.observableArrayList();
		if (!currentList.isEmpty()) {
			if (currentList.get(0) != 0) {
				currentVisitorsData.add(new PieChart.Data("0-1 hours, " + String.valueOf(currentList.get(0) + "%"),
						currentList.get(0)));
			}
			if (currentList.get(1) != 0) {
				currentVisitorsData.add(new PieChart.Data("1-2 hours, " + String.valueOf(currentList.get(1) + "%"),
						currentList.get(1)));
			}
			if (currentList.get(2) != 0) {
				currentVisitorsData.add(new PieChart.Data("2-3 hours, " + String.valueOf(currentList.get(2) + "%"),
						currentList.get(2)));
			}
			if (currentList.get(3) != 0) {
				currentVisitorsData.add(new PieChart.Data("3-4 hours, " + String.valueOf(currentList.get(3) + "%"),
						currentList.get(3)));
			}
		}

		return currentVisitorsData;
	}

	public static String getFirstName() {
		return firstName;
	}

	public static void setFirstName(String firstName) {
		DepartmentManagerController.firstName = firstName;
	}

	// ************ pending messages ***************
	/**
	 * 
	 * @return ArrayList of ArrayList of Object
	 */
	public static ArrayList<ArrayList<Object>> getStatus() {
		return status;
	}

	public static void setStatus(ArrayList<ArrayList<Object>> status) {
		DepartmentManagerController.status = status;
	}

	public static void setData(ArrayList<ArrayList<Object>> status) {
		setStatus(status);
	}

	/**
	 * initializeArr
	 * 
	 * @param arr int[]
	 */
	public void initializeArr(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = 0;

		}
	}

	/**
	 * button approve will remove the row and will update the table from DB will
	 * send to server array list of object [0] name method [1] string "yes" [2]
	 * object of ManagerRequest
	 * 
	 * @param event ActionEvent
	 */
	@FXML
	void approve(ActionEvent event) {
		int flag = 0;
		int[] arrItems = new int[TableDep.getItems().size()];
		initializeArr(arrItems);
		ArrayList<Object> Server = new ArrayList<>();
		ArrayList<ArrayList<Object>> allItems = new ArrayList<>();
		ArrayList<Object> data = new ArrayList<>();
		Server.add("removePendingsManagerReq");
		for (int i = 0; i < TableDep.getItems().size(); i++) {
			if (TableDep.getItems().get(i).getMarkCh().isSelected()) {
				TableViewSet s = TableDep.getItems().get(i);
				ManagerRequest mnData = new ManagerRequest(s.getParkName(), Integer.parseInt(s.getIdEmp()),
						s.getReqType());

				data.add(mnData);
				data.add("yes");
				allItems.add(new ArrayList<Object>(data));
				arrItems[i] = i;
				if (s.getReqType().equals("max_c") || s.getReqType().equals("max_o")) {
					flag = 1;
				}

			}
			data.clear();
		}

		if (!allItems.isEmpty()) {
			Server.add(new ArrayList<Object>(allItems));
			allItems.clear();
			// remove items from list after the all lists for server - DO NOT DELETE!
//			for (int i = arrItems.length - 1; i >= 0; i--) {
//				if (arrItems[i] != 0)
//					TableDep.getItems().remove(TableDep.getItems().get(i));
//			}

			ClientUI.sentToChatClient(Server);
		}

		if (flag == 1) {
			iniailTabelVisitors();
		}

		if (status != null && !status.isEmpty()) {
			for (ArrayList<Object> item : status) {
				if (!(boolean) item.get(1))
					alert.failedAlert("Failed", "something went wrong, please try later again!");
				else {
					if (!(boolean) item.get(2)) {
						ManagerRequest manager = (ManagerRequest) item.get(0);
						alert.failedAlert("Failed",
								"Already has discount on these dates for park " + manager.getParkName() + "!");
					}
				}
			}

		}
		iniailTabelPending();
	}

	/**
	 * button disapprove will not remove the row and will update the server will
	 * send to server array list of Object : [0] name method [1] string "no" [2]
	 * Object of ManagerRequest
	 * 
	 * @param event
	 */
	@FXML
	void disapprove(ActionEvent event) {
		int[] arrItems = new int[TableDep.getItems().size()];
		initializeArr(arrItems);
		ArrayList<Object> Server = new ArrayList<>();
		Server.add("removePendingsManagerReq");
		ArrayList<ArrayList<Object>> allItems = new ArrayList<>();
		ArrayList<Object> data = new ArrayList<>();
		for (int i = 0; i < TableDep.getItems().size(); i++) {
			if (TableDep.getItems().get(i).getMarkCh().isSelected()) {
				TableViewSet s = TableDep.getItems().get(i);
				ManagerRequest mnData = new ManagerRequest(s.getParkName(), Integer.parseInt(s.getIdEmp()),
						s.getReqType());
				data.add(mnData);
				data.add("no");
				allItems.add(new ArrayList<Object>(data));
				arrItems[i] = i;

			}
			data.clear();
		}

		if (!allItems.isEmpty()) {
			Server.add(new ArrayList<Object>(allItems));
			allItems.clear();
			// remove items from list after the all lists for server - DO NOT DELETE!
//			for (int i = arrItems.length - 1; i > -1; i--) {
//				if (arrItems[i] != 0)
//					TableDep.getItems().remove(TableDep.getItems().get(i));
//			}
			ClientUI.sentToChatClient(Server);
		}
		// Server.clear();

		if (status != null && !status.isEmpty()) {
			for (ArrayList<Object> item : status) {
				if (!(boolean) item.get(1))
					alert.failedAlert("Failed", "something went wrong, please try later again!");
				else {
					if ((boolean) item.get(2)) {
						ManagerRequest manager = (ManagerRequest) item.get(0);
						alert.failedAlert("Failed", "You disapprove the request type: " + manager.getRequestType());
					}

				}
			}

		}
		iniailTabelPending();

	}

	/**
	 * add data to the table in the department screen
	 * 
	 * @param al ArrayList of ArrayList of String
	 */
	public void addData(ArrayList<ArrayList<String>> al) {
		TableDep.getItems().clear();
		ObservableList<TableViewSet> listForTable = FXCollections.observableArrayList();
		String str = null;
		for (ArrayList<String> arrayList : al) {

			if (arrayList.get(1).equals("discount")) {
				String[] FromDate = arrayList.get(5).split(" ");
				String[] ToDate = arrayList.get(6).split(" ");
				String discString = arrayList.get(4);
				double disc = Double.parseDouble(discString);
				disc = (1 - disc) * 100;
				// int discount =(1 - Integer.parseInt())*100;
				str = "Discount : " + String.format("%.1f", disc) + "%" + " in the following dates: " + FromDate[0]
						+ " - " + ToDate[0];
			} else if (arrayList.get(1).equals("max_c")) {
				str = "Visitors Capacity : " + arrayList.get(2);
			} else if (arrayList.get(1).equals("max_o")) {
				str = "Visitors Order Capacity : " + arrayList.get(3);
			}
			TableViewSet TVS = new TableViewSet(arrayList.get(7), arrayList.get(1), str);
			TVS.setIdEmp(arrayList.get(0));
			listForTable.add(TVS);
		}

		TableDep.setItems(listForTable);
	}

	public static ArrayList<ArrayList<String>> getDBList() {
		return DBList;
	}

	public static void setDBList(ArrayList<ArrayList<String>> dBList) {
		DBList = dBList;
	}

	/**
	 * add data to the pending manager requests
	 * 
	 */
	public void iniailTabelPending() {
		DBList.clear();
		ArrayList<Object> msg = new ArrayList<>();
		msg.add("PendingManagerRequests");
		ClientUI.sentToChatClient(msg);
		count = DBList.size();
		LabelCount.setText(String.valueOf(count));
		addData(DBList);
	}

	/*****************************************/

	/**
	 * set style for the pressed button
	 * 
	 * @param button Button
	 */
	public void setButtonPressed(Button button) {
		button.setStyle("-fx-background-color: transparent;" + "-fx-border-color: brown;"
				+ "-fx-border-width: 0px 0px 0px 3px;");
	}

	/**
	 * set style for the released buttons
	 * 
	 * @param button  Button
	 * @param button1 Button
	 */
	public void setButtonReleased(Button button, Button button1) {
		button.setStyle("-fx-background-color: transparent;");
		button1.setStyle("-fx-background-color: transparent;");
	}

	/**
	 * displays the chart for the information retrieved from DB
	 *
	 * ArrayList of ArrayList of String cancelledOrders, cells: cell [0]: parkName
	 * cell [1]: date of canceled/dismissed cell [2]: amount
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void barChart() {
		int i;
		int count = 0;
		ArrayList<String> parks = new ArrayList<String>();

		for (i = 0; i < cancelledOrders.size(); i++) {
			if (!parks.contains(cancelledOrders.get(i).get(0))) {
				parks.add(cancelledOrders.get(i).get(0));
			}
		}

		String minDateFormat = cancelledOrders.get(0).get(1).substring(0, 10);
		String maxDateFormat = cancelledOrders.get(cancelledOrders.size() - 1).get(1).substring(0, 10);
		LocalDate minDate = LocalDate.parse(minDateFormat);
		LocalDate maxDate = LocalDate.parse(maxDateFormat);

		xAxis = new CategoryAxis();
		yAxis = new NumberAxis(0, 1000, 50);

		bcCancells.getData().clear();
		bcCancells.setAnimated(false);
		bcCancells.setBarGap(0d);
		bcCancells.setCategoryGap(4.0);

		bcCancells.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		bcCancells.setPrefSize(613, 430);
		bcCancells.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

		bcCancells.setTitle("Cancellation/Dismissed Reports");

		Series<String, Double>[] parksSeries = new Series[parks.size()];
		int[] parksDateFlag = new int[parks.size()];

		for (i = 0; i < parks.size(); i++) {
			parksSeries[i] = new Series<String, Double>();
			parksSeries[i].setName(parks.get(i));
		}

		for (LocalDate date = minDate; date.isBefore(maxDate.plusDays(1)); date = date.plusDays(1)) {

			count = checkIfExists(date);
			if (count == 0) {
				for (Series<String, Double> data : parksSeries) {
					data.getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(), 0));
				}
				continue;
			}

			for (i = 0; i < parksDateFlag.length; i++) {
				parksDateFlag[i] = 0;
			}
			i = 0;

			while (i < count) {
				if (cancelledOrders.get(index).get(0).equals(parks.get(i))) {
					parksDateFlag[i] = index;
				}

				index++;
				i++;
			}

			for (i = 0; i < parks.size(); i++) {
				if (cancelledOrders.get(i).get(0).equals(parks.get(0)) && parksDateFlag[i] == 0) {
					parksSeries[i].getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(),
							Double.parseDouble(cancelledOrders.get(0).get(2))));
				} else if (parksDateFlag[i] != 0) {
					parksSeries[i].getData().add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(),
							Double.parseDouble(cancelledOrders.get(parksDateFlag[i]).get(2))));
					parksDateFlag[i] = 0;
				} else {
					parksSeries[i].getData()
							.add(new XYChart.Data(date.getDayOfMonth() + "/" + date.getMonthValue(), 0));
				}
			}
		}
		for (Series<String, Double> data : parksSeries) {
			bcCancells.getData().add(data);
		}
	}

	/**
	 * checks if the date exists and saves the first position where it appears
	 *
	 * @param date LocalDate
	 * @return count
	 */
	public int checkIfExists(LocalDate date) {
		int count = 0;
		boolean flag = false;
		index = 0;

		for (int i = 0; i < cancelledOrders.size(); i++) {
			String DateFormat = cancelledOrders.get(i).get(1).substring(0, 10);
			LocalDate checkDate = LocalDate.parse(DateFormat);
			if (date.equals(checkDate)) {
				if (index == 0 && !flag) {
					index = i;
					flag = true;
				}
				count++;
			}
		}

		return count;
	}

	/**
	 * Sends ArrayList to server
	 * 
	 * ArrayList(String) data, sending to the server to get data [0] case name [1]
	 * month [2] year
	 *
	 * @param caseName String
	 * @param date     ArrayList of String
	 */
	public void sendToServerArrayList(String caseName, ArrayList<String> date) {
		// Query
		ArrayList<Object> msg = new ArrayList<Object>();
		msg.add(caseName);
		// add date
		msg.add(date);
		// set up all the order details and the payment method
		ClientUI.sentToChatClient(msg);
	}

	/**
	 * getting data from the server for cancel report
	 * 
	 * ArrayList(Object): cell[0] list of cancelled orders cell[1] list of dismissed
	 * orders
	 *
	 * @param cancelData ArrayList of ArrayList of String
	 */
	public static void receivedFromServerCancelReportsData(ArrayList<ArrayList<String>> cancelData) {
		if (cancelData.isEmpty()) {
			setError(true);
		} else {
			setError(false);
			DepartmentManagerController.cancelledOrders = cancelData;
		}
	}

	/**
	 * getting data from the server for regular visitors report
	 * 
	 * if empty == String "empty", otherwise ArrayList(Object): cell[0] 0 to 1 hour
	 * cell[1] 1 to 2 hours cell[2] 2 to 3 hours cell[3] 3 to 4 hours
	 *
	 * @param msgReceived ArrayList Object
	 */
	public static void receivedFromServerRegularsVisitorsData(ArrayList<Object> msgReceived) {
		DepartmentManagerController.regularVisitors.clear();
		if (msgReceived.get(1) instanceof String) {
			setEmpty(true);
		} else if (msgReceived instanceof ArrayList) {
			// call the new method
			setRegularVisitors(msgReceived);
		}
	}

	// new method for set the array list of regular from server
	public static void setRegularVisitors(ArrayList<Object> msgReceived) {
		DepartmentManagerController.regularVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(1))));
		DepartmentManagerController.regularVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(2))));
		DepartmentManagerController.regularVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(3))));
		DepartmentManagerController.regularVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(4))));
	}

	/**
	 * getting data from the server for members visitors report
	 * 
	 * if empty == String "empty", otherwise ArrayList(Object): cell[0] 0 to 1 hour
	 * cell[1] 1 to 2 hours cell[2] 2 to 3 hours cell[3] 3 to 4 hours
	 *
	 * @param msgReceived ArrayList Object
	 */
	public static void receivedFromServerMembersVisitorsData(ArrayList<Object> msgReceived) {
		DepartmentManagerController.memberVisitors.clear();
		if (msgReceived.get(1) instanceof String) {
			setEmpty(true);
		} else if (msgReceived instanceof ArrayList) {
			// call the new method
			setMemberVisitors(msgReceived);
		}
	}

	// new method for set the array list of member from server
	public static void setMemberVisitors(ArrayList<Object> msgReceived) {
		DepartmentManagerController.memberVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(1))));
		DepartmentManagerController.memberVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(2))));
		DepartmentManagerController.memberVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(3))));
		DepartmentManagerController.memberVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(4))));
	}

	/**
	 * getting data from the server for groups visitors report
	 * 
	 * if empty == String "empty", otherwise ArrayList(Object): cell[0] 0 to 1 hour
	 * cell[1] 1 to 2 hours cell[2] 2 to 3 hours cell[3] 3 to 4 hours
	 *
	 * @param msgReceived ArrayList of Object
	 */
	public static void receivedFromServerGroupsVisitorsData(ArrayList<Object> msgReceived) {
		DepartmentManagerController.groupVisitors.clear();
		if (msgReceived.get(1) instanceof String) {
			setEmpty(true);
		} else if (msgReceived instanceof ArrayList) {
			// call the new method
			setGroupVisitors(msgReceived);
		}
	}

	// new method for set the array list of group from server
	public static void setGroupVisitors(ArrayList<Object> msgReceived) {
		DepartmentManagerController.groupVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(1))));
		DepartmentManagerController.groupVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(2))));
		DepartmentManagerController.groupVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(3))));
		DepartmentManagerController.groupVisitors
				.add(Double.parseDouble(new DecimalFormat("##.##").format((msgReceived).get(4))));
	}

	/**
	 * get the error
	 * 
	 * @return error
	 */
	public static boolean getError() {
		return error;
	}

	/**
	 * set errors
	 * 
	 * @param error boolean
	 */
	public static void setError(boolean error) {
		DepartmentManagerController.error = error;
	}

	/**
	 * get the isEmpty
	 * 
	 * @return isEmpty
	 */
	public static boolean isEmpty() {
		return isEmpty;
	}

	/**
	 * set isEmpty
	 * 
	 * @param isEmpty boolean
	 */
	public static void setEmpty(boolean isEmpty) {
		DepartmentManagerController.isEmpty = isEmpty;
	}

	/**
	 * iniailize table current visitors number
	 */
	public void iniailTabelVisitors() {

		ArrayList<Object> answer = new ArrayList<>();
		answer.add("parkDateilsForDepartment");
		ClientUI.sentToChatClient(answer);
		addDataToTable();

	}

	/**
	 * add data to the tabel current visitors number
	 */
	public void addDataToTable() {
		TVisitors.getItems().clear();
		ObservableList<TableCurrentVisitors> listForTable = FXCollections.observableArrayList();

		for (ArrayList<String> arrayList : parkDetails) {
			TableCurrentVisitors TVV = new TableCurrentVisitors(arrayList.get(0),
					arrayList.get(1) + " / " + arrayList.get(2));
			TVV.setMaxVisitors(arrayList.get(2));
			listForTable.add(TVV);
		}

		TVisitors.setItems(listForTable);
	}

	/**
	 * 
	 * update current visitors number in the park.The number is updated every time
	 * when visitor enters or exits the park
	 * 
	 * @param arr visitNum
	 */

	public void setCurrentVisitors(ArrayList<Object> arr) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String parkName = (String) arr.get(1);
				String VisAmount;

				for (ArrayList<String> arrayList : parkDetails) {

					if (parkName.equals(arrayList.get(0))) {
						VisAmount = (String) arr.get(2);
						arrayList.set(1, VisAmount);
					}
				}

				addDataToTable();
			}
		});

	}

	public static void setParkDetails(ArrayList<ArrayList<String>> Parks) {
		DepartmentManagerController.parkDetails = Parks;
	}

	public static ArrayList<ArrayList<String>> getParkDetails() {
		return parkDetails;
	}

	/**
	 * Initializing and force each of the fields according to the Required templates
	 * 
	 * @param location  URL
	 * @param resources ResourceBundle
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		Context.getInstance().setDMC(this);

		setFirstName(LoginController.getFirstName());
		lblFirstNameTitle.setText(getFirstName());

		/*************** for deshbord ***********/
		iniailTabelVisitors();

		visitorColumn.setCellValueFactory(new PropertyValueFactory<TableCurrentVisitors, String>("parkNameVis"));
		amountColumn.setCellValueFactory(new PropertyValueFactory<TableCurrentVisitors, String>("currentAmount"));

		iniailTabelPending();

		parkName.setCellValueFactory(new PropertyValueFactory<TableViewSet, String>("ParkName"));
		requestType.setCellValueFactory(new PropertyValueFactory<TableViewSet, String>("reqType"));
		requestDetails.setCellValueFactory(new PropertyValueFactory<TableViewSet, String>("reqDetails"));
		mark.setCellValueFactory(new PropertyValueFactory<TableViewSet, String>("MarkCh"));

		/***********************/

		/***** Visitors Reports *****/
		dpVisitorsFrom.setValue(LocalDate.now().withDayOfMonth(1));
		// listener for updating the date
		dpVisitorsFrom.valueProperty().addListener((ov, oldValue, newValue) -> {
			dpVisitorsFrom.setValue(newValue);
		});

		// plusMonths(1) to get the next month
		// withDayOfMonth(1) to get the first day
		dpVisitorsTo.setValue(dpVisitorsFrom.getValue().plusMonths(1).withDayOfMonth(1));
		// listener for updating the date
		dpVisitorsTo.valueProperty().addListener((ov, oldValue, newValue) -> {
			dpVisitorsTo.setValue(newValue);
		});

		/***** Cancel Reports *****/
		dpCancelFrom.setValue(LocalDate.now().withDayOfMonth(1));
		// listener for updating the date
		dpCancelFrom.valueProperty().addListener((ov, oldValue, newValue) -> {
			dpCancelFrom.setValue(newValue);
		});

		// plusMonths(1) to get the next month
		// withDayOfMonth(1) to get the first day
		dpCancelTo.setValue(dpCancelFrom.getValue().plusMonths(1).withDayOfMonth(1));
		// listener for updating the date
		dpCancelTo.valueProperty().addListener((ov, oldValue, newValue) -> {
			dpCancelTo.setValue(newValue);
		});
	}
}
