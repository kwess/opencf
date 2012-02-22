package de.openCF.server.communication;

import de.openCF.protocol.Connection;
import de.openCF.protocol.ConnectionFactory;
import de.openCF.protocol.PacketHelper.Encoding;

public class AgentConnectionFactory extends ConnectionFactory {

	protected Encoding	encoding	= Encoding.JSON;

	public AgentConnectionFactory(boolean debug, Encoding encoding) {
		super(debug);
		this.encoding = encoding;
	}

	@Override
	public Connection createConnection() {
		Connection c = new Connection();
		c.setEncoding(encoding);
		AgentPacketHandler agentPacketHandler = new AgentPacketHandler(c);
		c.setPacketHandler(agentPacketHandler);
		return c;
	}

}
