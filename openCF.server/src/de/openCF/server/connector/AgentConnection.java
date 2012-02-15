package de.openCF.server.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;
import de.openCF.protocol.PacketReader;
import de.openCF.protocol.PacketWriter;

public class AgentConnection implements Connection {

	private Logger			logger			= Logger.getLogger(AgentConnection.class);
	private Socket			socket			= null;
	private PacketHandler	packetHandler	= new AgentPacketHandler();
	private boolean			debug			= false;

	public AgentConnection() {
		logger.trace("new");
	}

	public AgentConnection(boolean debug) {
		logger.trace("new(boolean)");
		this.debug = debug;
	}

	@Override
	public void run() {
		logger.trace("run start");

		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (IOException e) {
			logger.error("cant get stream from socket", e);
			return;
		}

		PacketReader packetReader = new PacketReader(inputStream);
		PacketWriter packetWriter = new PacketWriter(outputStream);

		boolean continueWork = true;

		while (continueWork) {
			continueWork = socket.isConnected();
			try {
				Packet packet = packetReader.readPacket();
				if (debug) {
					logger.warn("debug is enabled, discarding packet");
					continue;
				}
				Packet response = packetHandler.handlePacket(packet);
				if (response != null)
					packetWriter.writePacket(response);
			} catch (IOException e) {
				logger.error("error while reading from agent");
			} finally {
				packetHandler.handleClose();
				continueWork = false;
			}
		}
		logger.trace("run finished");
	}

	@Override
	public void setSocket(Socket socket) {
		logger.trace("setSocket");
		this.socket = socket;
	}

	@Override
	public void forward(Packet packet) {
		logger.trace("forward");

	}

}
