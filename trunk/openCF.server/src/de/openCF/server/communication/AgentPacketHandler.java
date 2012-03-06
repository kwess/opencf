package de.openCF.server.communication;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.openCF.protocol.Connection;
import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;
import de.openCF.protocol.PacketHelper.Encoding;
import de.openCF.protocol.PacketKeys;
import de.openCF.protocol.PacketType;
import de.openCF.server.Data;
import de.openCF.server.data.Agent;
import de.openCF.server.data.Automation;
import de.openCF.server.data.AutomationStatus;
import de.openCF.server.data.Heartbeat;
import de.openCF.server.data.Message;
import de.openCF.server.data.Server;
import de.openCF.server.persistence.Persistence;

public class AgentPacketHandler implements PacketHandler {

	private static Logger		logger		= Logger.getLogger(AgentPacketHandler.class);

	private Agent				agent		= null;
	private Connection			connection	= null;
	private boolean				registered	= false;
	private static Persistence	persistence	= Persistence.getInstance();

	public AgentPacketHandler(Connection c) {
		logger.trace("new(Connection)");
		this.connection = c;
	}

	@Override
	public void handlePacket(Packet packet) {
		logger.trace("handlePacket(Packet)");

		Map<String, Object> data = packet.getData();

		String key = PacketKeys.TYPE;
		Integer type = (Integer) data.get(key);

		if (type == null) {
			logger.debug("type is null, setting type to invalid");
			type = PacketType.INVALID;
		}

		logger.debug("type: " + type);

		logger.debug("is registered: " + registered);

		if (!registered && type != PacketType.AGENT_HELLO) {
			logger.warn("got unexpected Packet from unregistered Agent, discarding");
			return;
		}

		switch (type) {
			case PacketType.AGENT_HELLO:
				logger.debug("agent hello");
				handleAgentHello(data);
				break;
			case PacketType.AGENT_HEARTBEAT:
				logger.debug("agent heartbeat");
				handleAgentHeartbeat(data);
				break;
			case PacketType.AUTOMATION_STATUS:
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
		logger.trace("handleAutomationStatus(Map)");

		Integer id = (Integer) data.get(PacketKeys.AUTOMATION_ID);
		String status = (String) data.get(PacketKeys.AUTOMATION_STATUS);
		String message = (String) data.get(PacketKeys.AUTOMATION_MESSAGE);

		AutomationStatus automationStatus = AutomationStatus.valueOf(status.toLowerCase());

		if (automationStatus == null) {
			logger.warn("automation status not set");
			return;
		}

		Automation automation = (Automation) persistence.get(Automation.class, id);

		if (automation == null) {
			logger.error("got statusupdate for not existing automation with id " + id);
			return;
		}

		Message message2db = new Message();
		message2db.setAutomation(automation);
		message2db.setStatus(automationStatus);
		message2db.setMessage(message);

		if (automationStatus == AutomationStatus.talking) {
			logger.debug("automation [" + id + "] says: " + message);
			automation.setStatus(automationStatus);
			persistence.update(automation);
		} else {
			logger.info("automation [" + id + "] notifies [" + automationStatus + "] " + message);
		}

		persistence.save(message2db);

		Data.notifyAutomationStatusListener(id, automationStatus, message);

		if (AutomationStatus.isEndState(automationStatus)) {
			logger.debug("removing this listener for automation: " + id);
			Data.removeAllAutomationStatusListener(id);
		}

		logger.trace("handleAutomationStatus finished");
	}

	private void handleAgentHeartbeat(Map<String, Object> data) {
		logger.trace("handleAgentHeartbeat(Map)");

		String date = (String) data.get(PacketKeys.AGENT_LOCAL_TIME);
		Date agent_localtime = new Date(Long.parseLong(date) * 1000);

		Heartbeat heartbeat = new Heartbeat();
		heartbeat.setAgent_localtime(agent_localtime);
		heartbeat.setAgent(this.agent);

		persistence.save(heartbeat);

		logger.trace("handleAgentHeartbeat finished");
	}

	private void handleAgentHello(Map<String, Object> data) {
		logger.trace("handleAgentHello(Map)");

		Map<String, Object> response = new HashMap<String, Object>();
		response.put(PacketKeys.TYPE, PacketType.AGENT_HELLO_RESPONSE);

		String agent_id = (String) data.get(PacketKeys.AGENT_ID);
		String plattform = (String) data.get(PacketKeys.AGENT_PLATTFORM);
		String version = (String) data.get(PacketKeys.AGENT_VERSION);
		String encoding = (String) data.get(PacketKeys.AGENT_ENCODING);

		if (encoding != null && !"".equals(encoding)) {
			Encoding e = Encoding.valueOf(encoding);
			logger.info("agent requested encoding change from " + connection.getEncoding() + " to " + e);
			connection.setEncoding(e);
		}

		Agent agent = (Agent) persistence.get(Agent.class, agent_id);
		Server server = Data.getServer();

		boolean agentOnline = false;
		boolean agentConnectedHere = Data.getConnection(agent_id) == null ? false : true;
		boolean agentConnectedToDifferendServer = false;

		if (agent != null) {
			agentOnline = agent.getStatus() == Agent.Status.OFFLINE ? false : true;
			agentConnectedToDifferendServer = server.getId().equals(agent.getServer().getId()) ? false : true;
		}

		logger.debug("agent is online: " + agentOnline);
		logger.debug("agent is connected to this server: " + agentConnectedHere);
		logger.debug("agent connected to differend server: " + agentConnectedToDifferendServer);

		if (agent == null) {
			logger.info("agent [" + agent_id + "] unknown, creating new");

			agent = new Agent();
			agent.setId(agent_id);
			agent.setPlattform(Agent.Plattform.valueOf(plattform.toUpperCase()));
			agent.setStatus(Agent.Status.ONLINE);
			agent.setVersion(version);
			agent.setUpdated(new Date());

			this.agent = agent;
			persistence.save(agent);

			Data.addConnection(agent_id, connection);

			response.put(PacketKeys.RETURN_CODE, 0);
			response.put(PacketKeys.SUCCESSFULL, true);
			response.put(PacketKeys.MESSAGE, "congratulations " + agent_id + ", youre registered!");

			setRegistered(true);
		} else if (!agentOnline || (agentOnline && agentConnectedHere) || (agentOnline && !agentConnectedToDifferendServer)) {
			logger.info("agent [" + agent_id + "] was known since " + agent.getUpdated() + ", but is offline, changing status to online, updating prefs");

			agent.setStatus(Agent.Status.ONLINE);
			agent.setPlattform(Agent.Plattform.valueOf(plattform.toUpperCase()));
			agent.setVersion(version);
			agent.setServer(server);
			agent.setUpdated(new Date());

			this.agent = agent;
			persistence.update(agent);

			Data.addConnection(agent_id, connection);

			response.put(PacketKeys.RETURN_CODE, 1);
			response.put(PacketKeys.SUCCESSFULL, true);
			response.put(PacketKeys.MESSAGE, "hello back again " + agent_id + " :)");

			setRegistered(true);
		} else if (agentOnline && agentConnectedHere) {
			logger.warn("agent [" + agent_id + "] already registered, rejecting request");

			response.put(PacketKeys.RETURN_CODE, -1);
			response.put(PacketKeys.SUCCESSFULL, false);
			response.put(PacketKeys.MESSAGE, "agent [" + agent_id + "] already registered and online");

			setRegistered(false);
		} else {
			logger.warn("agent [" + agent_id + "] is alredy connected to server " + agent.getServer().getId() + ", rejecting request");

			response.put(PacketKeys.RETURN_CODE, -2);
			response.put(PacketKeys.SUCCESSFULL, false);
			response.put(PacketKeys.MESSAGE, "agent [" + agent_id + "] already registered to " + agent.getServer().getId() + " and online");

			setRegistered(false);
		}

		Packet p = new Packet();
		p.setData(response);

		connection.forward(p);

		logger.trace("handleAgentHello finished");
	}

	@Override
	public void handleClose() {
		logger.trace("handleClose");

		if (agent == null || !registered) {
			logger.warn("connection closed before agent registerd");
			// nothing to do...
			return;
		}

		agent.setStatus(Agent.Status.OFFLINE);
		agent.setUpdated(new Date());
		persistence.update(agent);

		Data.removeConnection(agent.getId());

		String id = "?";
		if (registered)
			id = agent.getId();

		logger.info("agent [" + id + "] closed connection");

		setRegistered(false);

		logger.trace("handleClose finished");
	}

	protected boolean isRegistered() {
		logger.trace("isRegistered");
		return registered;
	}

	protected void setRegistered(boolean registered) {
		logger.trace("setRegistered(boolean)");
		this.registered = registered;
		logger.info("agent [" + agent.getId() + "] now is registerd? " + registered);
	}

	@Override
	public void handleOpen() {
		logger.trace("handleOpen");
	}

}
