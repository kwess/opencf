package de.openCF.protocol;

import org.apache.log4j.Logger;

public class DefaultPacketHandler implements PacketHandler {

	private static Logger	logger	= Logger.getLogger(DefaultPacketHandler.class);

	@Override
	public void handlePacket(Packet packet) {
		logger.trace("handlePacket(Packet)");
		logger.debug(packet.dump());
	}

	@Override
	public void handleClose() {
		logger.trace("handleClose");
	}

	@Override
	public void handleOpen() {
		logger.trace("handleOpen");
	}

}
