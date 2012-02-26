package de.openCF.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.openCF.protocol.PacketHelper.Encoding;

public class Connection implements Runnable {

	private Logger				logger			= Logger.getLogger(Connection.class);
	private Socket				socket			= null;
	private Reader				packetReader	= null;
	private Writer				packetWriter	= null;
	private PacketHandler		packetHandler	= new DefaultPacketHandler();
	private Encoding			encoding		= Encoding.JSON;
	private boolean				debug			= false;
	private volatile boolean	running			= true;

	public Connection() {
		logger.trace("new");
	}

	public Connection(boolean debug) {
		logger.trace("new(boolean)");
		this.debug = debug;
	}

	@Override
	public void run() {
		logger.trace("run");

		if (packetReader == null)
			throw new IllegalStateException("reader is null");
		if (packetWriter == null)
			throw new IllegalStateException("writer is null");

		try {
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			packetReader.setInputStream(inputStream);
			packetWriter.setOutputStream(outputStream);

			packetHandler.handleOpen();
		} catch (IOException e) {
			logger.error("cant get stream from socket: " + e.getMessage());
			return;
		}

		logger.debug("debug: " + debug);
		logger.debug("running: " + running);
		logger.info("using PacketHandler: " + packetHandler.toString());
		logger.info("using encoding outgoing: " + encoding);
		logger.info("using encoding incoming: " + Encoding.JSON);

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
				running = false;
			}
		}

		packetHandler.handleClose();
	}

	public void setSocket(Socket socket) {
		logger.trace("setSocket(Socket)");
		this.socket = socket;
		logger.debug("socket set to: " + socket);
	}

	public void forward(Packet packet) {
		logger.trace("forward(Packet)");
		if (packet != null)
			try {
				logger.debug("forwarding packet as " + encoding);
				if (debug)
					logger.warn("debung is enabled, not forwarding packet");
				else
					packetWriter.writePacket(packet, encoding);
			} catch (IOException e) {
				logger.error("cant forward Packet: " + e.getMessage());
				running = false;
			}
	}

	public Encoding getEncoding() {
		logger.trace("getEncoding");
		return encoding;
	}

	public void setEncoding(Encoding encoding) {
		logger.trace("setEncoding");
		if (this.encoding.equals(encoding))
			return;
		logger.info("encoding changed to " + encoding);
		this.encoding = encoding;
	}

	public boolean isDebug() {
		logger.trace("isDebug");
		return debug;
	}

	public void setPacketHandler(PacketHandler handler) {
		logger.trace("setPacketHandler");
		if (debug)
			logger.warn("debug is enabled, PacketHandler will remain default");
		else {
			this.packetHandler = handler;
			logger.debug("packet handler changed to " + handler);
		}
	}

	public void setReader(Reader reader) {
		logger.trace("setReader(Reader)");
		this.packetReader = reader;
		logger.debug("Reader set to: " + reader);
	}

	public void setWriter(Writer writer) {
		logger.trace("setWriter(Writer)");
		this.packetWriter = writer;
		logger.debug("Writer set to: " + writer);
	}

}
