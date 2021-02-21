package controllers;

import java.util.ArrayList;

public interface IRecievedFromServer {
	public  void setRegularVisitorsToChart(ArrayList<Object> msgReceived);
	public  void setMemberVisitorsToChart(ArrayList<Object> msgReceived);
	public  void setGroupVisitorsToChart(ArrayList<Object> msgReceived);
}
