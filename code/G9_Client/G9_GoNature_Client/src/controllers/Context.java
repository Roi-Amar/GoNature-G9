package controllers;

/**
 * controller create instance that save the controller that active now
 * when we want to use another controller we approach from this class
 * @author Bar Katz
 *
 */
public class Context {

	private OrderController OrderC;
	private WaitingListController WLC;
	private WaitingListConfirmContoller WLCC;
	private ParkManagerController PMC;
	private ParkEmployeeController PEC;
	private DepartmentManagerController DMC;
	

	private final static Context instance = new Context();

	public static Context getInstance() {
		return instance;
	}
	public OrderController getOrderC() {
		return OrderC;
	}

	public void setOrderC(OrderController orderC) {
		OrderC = orderC;
	}

	public WaitingListController getWLC() {
		return WLC;
	}

	public void setWLC(WaitingListController wLC) {
		WLC = wLC;
	}

	public WaitingListConfirmContoller getWLCC() {
		return WLCC;
	}

	public void setWLCC(WaitingListConfirmContoller wLCC) {
		WLCC = wLCC;
	}

	public ParkManagerController getPMC() {
		return PMC;
	}

	public void setPMC(ParkManagerController pMC) {
		PMC = pMC;
	}

	public ParkEmployeeController getPEC() {
		return PEC;
	}

	public void setPEC(ParkEmployeeController pEC) {
		PEC = pEC;
	}

	public DepartmentManagerController getDMC() {
		return DMC;
	}

	public void setDMC(DepartmentManagerController dMC) {
		DMC = dMC;
	}

}
