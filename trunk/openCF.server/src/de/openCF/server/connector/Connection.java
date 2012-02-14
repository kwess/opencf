package de.openCF.server.connector;

import java.net.Socket;

public interface Connection extends Runnable {

	public void setSocket(Socket socket);

}
