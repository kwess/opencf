package de.openCF.server.communication;

import org.apache.log4j.Logger;

import de.openCF.protocol.Connection;
import de.openCF.protocol.ConnectionFactory;
import de.openCF.protocol.PacketReader;
import de.openCF.protocol.PacketWriter;

public class ControllerConnectionFactory extends ConnectionFactory {

	private static Logger	logger	= Logger.getLogger(ControllerConnectionFactory.class);

	public ControllerConnectionFactory(boolean debug) {
		super(debug);
		logger.trace("new(boolean)");
	}

	@Override
	public Connection createConnection() {
		logger.trace("getConnection");
		Connection c = new Connection();
		c.setReader(new PacketReader());
		c.setWriter(new PacketWriter());
		ControllerPacketHandler handler = new ControllerPacketHandler(c);
		c.setPacketHandler(handler);
		return c;
	}

}
