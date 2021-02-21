package controllers;

import java.util.ArrayList;

public interface IRecievedFromSreverForLogin {

		public void receivedFromServerUserStatus(Object msgReceived);
		public void receivedFromServerLoggedInStatus(boolean msgReceived) ;
		public void sendToServerArrayListLogin(String type, ArrayList<String> dbColumns);
}
