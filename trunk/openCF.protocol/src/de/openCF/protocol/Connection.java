package de.openCF.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.openCF.protocol.PacketHelper.Encoding;

public class Connection implements Runnable {

	private Logger			logger			= Logger.getLogger(Connection.class);
	private Socket			socket			= null;
	private InputStream		inputStream		= null;
	private OutputStream	outputStream	= null;
	private PacketReader	packetReader	= null;
	private PacketWriter	packetWriter	= null;
	private PacketHandler	packetHandler	= new DefaultPacketHandler();
	private Encoding		encoding		= Encoding.JSON;
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
		logger.debug("using encoding outgoing: " + encoding);
		logger.debug("using encoding incoming: " + Encoding.JSON);

		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (IOException e) {
			logger.error("cant get stream from socket: " + e.getMessage());
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
				packetWriter.writePacket(packet, encoding);
			} catch (IOException e) {
				logger.error("cant forward Packet: " + e.getMessage());
				running = false;
			}
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setPacketHandler(PacketHandler handler) {
		this.packetHandler = handler;
	}

}
