package server;

import java.util.ArrayList;

import ocsf.server.ConnectionToClient;

public interface ISendToClient {

	public void sendToMyClient(ArrayList<Object> msg, ConnectionToClient client);

}
