package de.openCF.server.connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;
import de.openCF.protocol.PacketReader;

public class AgentConnection implements Connection {

	private Logger			logger			= Logger.getLogger(AgentConnection.class);
	private Socket			socket			= null;
	private PacketHandler	packetHandler	= new AgentPacketHandler();

	public AgentConnection() {
		logger.trace("new");
	}

	@Override
	public void run() {
		logger.trace("run start");
		InputStream inputStream = null;
		try {
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			logger.error("cant get input stream from socket", e);
			return;
		}
		while (socket.isConnected()) {
			PacketReader packetReader = new PacketReader(inputStream);
			try {
				Packet packet = packetReader.readPacket();
				logger.debug(packet.toString());
				packetHandler.handlePacket(packet);
			} catch (IOException e) {
				logger.error("error while reading from agent", e);
				return;
			}
		}
		logger.trace("run finished");
	}

	@Override
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
