package de.openCF.server.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;
import de.openCF.protocol.PacketReader;
import de.openCF.protocol.PacketWriter;

public class Connection implements Runnable {

	private Logger			logger			= Logger.getLogger(Connection.class);
	private Socket			socket			= null;
	private InputStream		inputStream		= null;
	private OutputStream	outputStream	= null;
	private PacketReader	packetReader	= null;
	private PacketWriter	packetWriter	= null;
	private PacketHandler	packetHandler	= null;
	private boolean			debug			= false;
	private boolean			running			= true;

	public Connection() {
		logger.trace("new");
	}

	public Connection(boolean debug) {
		logger.trace("new(boolean)");
		this.debug = debug;
	}

	@Override
	public void run() {
		logger.trace("run start");

		logger.debug("debug: " + debug);
		logger.debug("using PacketHandler: " + packetHandler.toString());

		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (IOException e) {
			logger.error("cant get stream from socket", e);
			return;
		}

		packetReader = new PacketReader(inputStream);
		packetWriter = new PacketWriter(outputStream);

		while (running) {
			running = socket.isConnected();
			try {
				Packet packet = packetReader.readPacket();
				if (debug) {
					logger.warn("debug is enabled, discarding packet");
					continue;
				}
				packetHandler.handlePacket(packet);

			} catch (IOException e) {
				logger.error("error while reading from socket: " + e.getMessage());
			} finally {
				packetHandler.handleClose();
				running = false;
			}
		}
		logger.trace("run finished");
	}

	public void setSocket(Socket socket) {
		logger.trace("setSocket");
		this.socket = socket;
	}

	public void forward(Packet packet) {
		logger.trace("forward");
		if (packet != null)
			try {
				packetWriter.writePacket(packet);
			} catch (IOException e) {
				logger.error("cant forward Packet");
				running = false;
			}
	}

	public void setPacketHandler(PacketHandler handler) {
		this.packetHandler = handler;
	}

}
