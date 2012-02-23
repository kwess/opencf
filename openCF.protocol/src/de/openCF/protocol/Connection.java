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
	private Reader			packetReader	= null;
	private Writer			packetWriter	= null;
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

		if (packetReader == null)
			throw new IllegalStateException("reader is null");
		if (packetWriter == null)
			throw new IllegalStateException("writer is null");

		try {
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			packetReader.setInputStream(inputStream);
			packetWriter.setOutputStream(outputStream);
		} catch (IOException e) {
			logger.error("cant get stream from socket: " + e.getMessage());
			return;
		}

		logger.debug("debug: " + debug);
		logger.debug("running: " + running);
		logger.debug("using PacketHandler: " + packetHandler.toString());
		logger.debug("using encoding outgoing: " + encoding);
		logger.debug("using encoding incoming: " + Encoding.JSON);

		while (running) {
			running = socket.isConnected();
			try {
				Packet packet = packetReader.readPacket();
				if (debug) {
					logger.warn("debug is enabled, discarding packet");
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
				logger.debug("forwarding packet as " + encoding);
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
		logger.trace("setEncoding");
		if (this.encoding.equals(encoding))
			return;
		logger.debug("encoding changed to " + encoding);
		this.encoding = encoding;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setPacketHandler(PacketHandler handler) {
		logger.trace("setPacketHandler");
		logger.debug("packet handler changed to " + handler);
		if (debug)
			logger.warn("debug is enabled, PacketHandler will remain default");
		else
			this.packetHandler = handler;
	}

	public void setReader(Reader reader) {
		this.packetReader = reader;
	}

	public void setWriter(Writer writer) {
		this.packetWriter = writer;
	}

}
