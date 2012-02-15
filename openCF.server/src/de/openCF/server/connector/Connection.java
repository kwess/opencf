package de.openCF.server.connector;

import java.net.Socket;

import de.openCF.protocol.Packet;

public interface Connection extends Runnable {

	public void setSocket(Socket socket);

	public void forward(Packet packet);

}
