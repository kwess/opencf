package de.openCF.server.connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

public class ServerConnectionAgent implements ServerConnection {

	private Logger	logger	= Logger.getLogger(ServerConnectionAgent.class);
	private Socket	socket	= null;

	public ServerConnectionAgent() {
		logger.trace("new");
	}

	@Override
	public void run() {
		logger.trace("run");
		InputStream inputStream = null;
		try {
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			logger.error("cant get input stream from socket", e);
			return;
		}
		while (socket.isConnected()) {
			byte[] buffer = new byte[1024];
			try {
				inputStream.read(buffer);
				logger.debug(new String(buffer));
			} catch (IOException e) {
				logger.error("error while reading from agent", e);
				return;
			}
		}
	}

	@Override
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
