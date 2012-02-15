package de.openCF.server.connector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;
import de.openCF.server.Data;
import de.openCF.server.data.Agent;
import de.openCF.server.persistence.Persistence;

public class AgentPacketHandler implements PacketHandler {

	private static Logger	logger		= Logger.getLogger(AgentPacketHandler.class);

	private Agent			agent		= null;

	private Connection		connection	= null;

	public AgentPacketHandler(Connection c) {
		logger.trace("new(Connection)");
		this.connection = c;
	}

	@Override
	public void handlePacket(Packet packet) {
		logger.trace("handlePacket");

		Map<String, Object> data = packet.getData();

		String key = AgentPacketKeys.TYPE;
		Integer type = (Integer) data.get(key);

		if (type == null)
			type = AgentPacketType.INVALID;

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

		Map<String, Object> response = new HashMap<String, Object>();

		String agent_id = (String) data.get(AgentPacketKeys.AGENT_ID);
		String plattform = (String) data.get(AgentPacketKeys.AGENT_PLATTFORM);
		String version = (String) data.get(AgentPacketKeys.AGENT_VERSION);

		Session session = Persistence.getSession();
		Agent agent = (Agent) session.get(Agent.class, agent_id);

		session.beginTransaction();

		if (agent == null) {
			logger.info("agent [" + agent_id + "] unknown, creating new");

			agent = new Agent();
			agent.setId(agent_id);
			agent.setPlattform(Agent.Plattform.valueOf(plattform.toUpperCase()));
			agent.setStatus(Agent.Status.ONLINE);
			agent.setVersion(version);

			this.agent = agent;
			session.save(agent);

			Data.addConnection(agent_id, connection);

			response.put(AgentPacketKeys.RETURN_CODE, 0);
			response.put(AgentPacketKeys.SUCCESSFULL, true);
			response.put(AgentPacketKeys.MESSAGE, "congratulations, youre registered!");
		} else if (agent != null && agent.getStatus() == Agent.Status.OFFLINE) {
			logger.info("agent [" + agent_id + "] was known, but is offline, changing status to online, updating prefs");

			agent.setStatus(Agent.Status.ONLINE);
			agent.setPlattform(Agent.Plattform.valueOf(plattform.toUpperCase()));
			agent.setVersion(version);

			this.agent = agent;
			session.update(agent);

			Data.addConnection(agent_id, connection);

			response.put(AgentPacketKeys.RETURN_CODE, 1);
			response.put(AgentPacketKeys.SUCCESSFULL, true);
			response.put(AgentPacketKeys.MESSAGE, "hello back again :)");
		} else {
			logger.warn("agent [" + agent_id + "] already registered, rejecting request");

			response.put(AgentPacketKeys.RETURN_CODE, -1);
			response.put(AgentPacketKeys.SUCCESSFULL, false);
			response.put(AgentPacketKeys.MESSAGE, "agent_id already registered and online");
		}

		session.getTransaction().commit();

		Packet p = new Packet();
		p.setData(response);

		try {
			connection.forward(p);
		} catch (IOException e) {
			logger.error("cant forward agend hello response");
		}

		logger.trace("handleAgentHello finished");
	}

	@Override
	public void handleClose() {
		logger.trace("handleClose");

		if (agent == null) {
			logger.warn("connection closed before agent registerd");
			// nothing to do...
			logger.trace("handleClose finished");
			return;
		}

		Session session = Persistence.getSession();
		session.beginTransaction();

		agent.setStatus(Agent.Status.OFFLINE);
		session.update(agent);

		session.getTransaction().commit();

		Data.removeConnection(agent.getId());

		logger.trace("handleClose finished");
	}

	@Override
	public void handleOpen() {
		logger.trace("handleOpen");

		logger.trace("handleOpen finished");
	}

}
