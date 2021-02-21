package managerData;

import java.io.Serializable;
import javafx.scene.control.CheckBox;

/**
 * class to display a table and value in it
 *
 * @author Bar Katz
 *
 */

public class TableViewSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String reqType;
	private String reqDetails;
	private String ParkName;
	private String idEmp;
	private CheckBox MarkCh;

	/**
	 * @param parkName   String
	 * @param reqType    String
	 * @param reqDetails String
	 */

	public TableViewSet(String parkName, String reqType, String reqDetails) {
		this.ParkName = parkName;
		this.reqType = reqType;
		this.reqDetails = reqDetails;
		this.MarkCh = new CheckBox();
	}

	public String getParkName() {
		return ParkName;
	}

	public void setParkName(String parkName) {
		ParkName = parkName;
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	public String getReqDetails() {
		return reqDetails;
	}

	public void setReqDetails(String reqDetails) {
		this.reqDetails = reqDetails;
	}

	public CheckBox getMarkCh() {
		return MarkCh;
	}

	public void setMarkCh(CheckBox checkBox) {
		this.MarkCh = checkBox;
	}

	public String getIdEmp() {
		return idEmp;
	}

	public void setIdEmp(String idEmp) {
		this.idEmp = idEmp;
	}

}
