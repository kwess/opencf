package de.openCF.server.connector;

import java.util.Map;

import org.apache.log4j.Logger;

import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;

public class AgentPacketHandler implements PacketHandler {

	private static Logger	logger	= Logger.getLogger(AgentPacketHandler.class);

	@Override
	public void handlePacket(Packet packet) {
		logger.trace("handlePacket");

		Map<String, Object> data = packet.getData();

		String key = AgentPacketKeys.TYPE.key;
		Integer type = (Integer) data.get(key);

		switch (type) {
			case AgentPacketType.AGENT_HELLO:
				logger.debug("agent hello");
				handleAgentHello(data);
				break;
			case AgentPacketType.AGENT_HEARTBEAT:
				logger.debug("agent heartbeat");
				handleAgentHeartbeat(data);
				break;
			case AgentPacketType.AUTOMATION_CONTROL_RESPONSE:
				logger.debug("automation_control response");
				handleAutomationControlResponse(data);
				break;
			case AgentPacketType.AUTOMATION_PREPARE_RESONSE:
				logger.debug("automation prepare response");
				handleAutomationPrepareResponse(data);
				break;
			case AgentPacketType.AUTOMATION_STATUS:
				logger.debug("automation status");
				handleAutomationStatus(data);
				break;
			default:
				logger.warn("unexpected type: " + type);
				break;
		}

		logger.trace("handle packet finished");
	}

	private void handleAutomationStatus(Map<String, Object> data) {
		logger.trace("handleAutomationStatus");

		logger.trace("handleAutomationStatus finished");
	}

	private void handleAutomationPrepareResponse(Map<String, Object> data) {
		logger.trace("handleAutomationPrepareResponse");

		logger.trace("handleAutomationPrepareResponse finished");
	}

	private void handleAutomationControlResponse(Map<String, Object> data) {
		logger.trace("handleAutomationControlResponse");

		logger.trace("handleAutomationControlResponse finished");
	}

	private void handleAgentHeartbeat(Map<String, Object> data) {
		logger.trace("handleAgentHeartbeat");

		logger.trace("handleAgentHeartbeat finished");
	}

	private void handleAgentHello(Map<String, Object> data) {
		logger.trace("handleAgentHello");

		logger.trace("handleAgentHello finished");
	}
}
