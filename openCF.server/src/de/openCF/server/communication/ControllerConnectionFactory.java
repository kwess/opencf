package de.openCF.server.communication;

import de.openCF.protocol.Connection;
import de.openCF.protocol.ConnectionFactory;

public class ControllerConnectionFactory extends ConnectionFactory {

	public ControllerConnectionFactory(boolean debug) {
		super(debug);
	}

	@Override
	public Connection createConnection() {
		Connection c = new Connection();
		ControllerPacketHandler handler = new ControllerPacketHandler(c);
		c.setPacketHandler(handler);
		return c;
	}

}
