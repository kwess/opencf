package de.openCF.server.connector;

import org.apache.log4j.Logger;

import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;

public class AgentPacketHandler implements PacketHandler {

	private static Logger	logger	= Logger.getLogger(AgentPacketHandler.class);

	@Override
	public void handlePacket(Packet packet) {
		logger.trace("handlePacket");
	}

}
