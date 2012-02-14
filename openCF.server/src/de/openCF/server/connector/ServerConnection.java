package de.openCF.server.connector;

import java.net.Socket;

public interface ServerConnection extends Runnable {

	public void setSocket(Socket socket);

}
