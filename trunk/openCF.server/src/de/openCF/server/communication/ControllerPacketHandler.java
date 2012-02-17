package de.openCF.server.communication;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;
import de.openCF.protocol.PacketKeys;
import de.openCF.protocol.PacketType;
import de.openCF.server.Data;

public class ControllerPacketHandler implements PacketHandler {

	private static Logger	logger	= Logger.getLogger(ControllerPacketHandler.class);

	@Override
	public void handlePacket(Packet packet) {
		logger.trace("handlePacket");

		Map<String, Object> data = packet.getData();

		String key = PacketKeys.TYPE;
		Integer type = (Integer) data.get(key);

		if (type == null)
			type = PacketType.INVALID;

		switch (type) {
			case PacketType.AUTOMATION_CONTROL:
				logger.debug("automation control");
				handleAutomationControl(data);
				break;
			default:
				logger.warn("unexpected type: " + type);
				break;
		}

		logger.trace("handle packet finished");
	}

	@SuppressWarnings("unchecked")
	private void handleAutomationControl(Map<String, Object> data) {
		logger.trace("handleAutomationControl");

		List<String> agent_ids = (List<String>) data.get(PacketKeys.AGENT_ID);
		String action = (String) data.get(PacketKeys.AUTOMATION_ACTION);
		String reason = (String) data.get(PacketKeys.AUTOMATION_REASON);
		String descriptor = (String) data.get(PacketKeys.AUTOMATION_DESCRIPTOR);
		Map<String, Object> parameter = (Map<String, Object>) data.get(PacketKeys.AUTOMATION_PARAMETER);

		logger.debug("agent_ids: " + agent_ids);
		logger.debug("action: " + action);
		logger.debug("reason: " + reason);
		logger.debug("descriptor: " + descriptor);
		logger.debug("parameter: " + parameter);

		for (String s : agent_ids) {
			Packet p = new Packet();
			p.setData(data);
			logger.info("forewarding to " + s + ": " + p);
			Data.getConnection(s).forward(p);
		}
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
