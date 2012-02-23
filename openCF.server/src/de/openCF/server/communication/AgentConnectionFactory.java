package de.openCF.server.communication;

import org.apache.log4j.Logger;

import de.openCF.protocol.Connection;
import de.openCF.protocol.ConnectionFactory;
import de.openCF.protocol.PacketHelper.Encoding;
import de.openCF.protocol.PacketReader;
import de.openCF.protocol.PacketWriter;

public class AgentConnectionFactory extends ConnectionFactory {

	private Logger		logger		= Logger.getLogger(AgentConnectionFactory.class);
	protected Encoding	encoding	= Encoding.JSON;

	public AgentConnectionFactory(boolean debug, Encoding encoding) {
		super(debug);
		logger.trace("new(boolean, Encoding)");
		this.encoding = encoding;
	}

	@Override
	public Connection createConnection() {
		logger.trace("createConnection");
		Connection c = new Connection();
		c.setEncoding(encoding);
		c.setReader(new PacketReader());
		c.setWriter(new PacketWriter());
		AgentPacketHandler agentPacketHandler = new AgentPacketHandler(c);
		c.setPacketHandler(agentPacketHandler);
		return c;
	}

}
