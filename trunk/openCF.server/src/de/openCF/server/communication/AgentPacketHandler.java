package de.openCF.server.communication;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.openCF.protocol.Connection;
import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;
import de.openCF.protocol.PacketHelper.Encoding;
import de.openCF.protocol.Protocol;
import de.openCF.server.Data;
import de.openCF.server.data.Agent;
import de.openCF.server.data.Automation;
import de.openCF.server.data.AutomationStatus;
import de.openCF.server.data.Heartbeat;
import de.openCF.server.data.Message;
import de.openCF.server.data.Plattform;
import de.openCF.server.data.Server;
import de.openCF.server.data.Status;
import de.openCF.server.persistence.Persistence;

/**
 * 
 * @author kristian.wessels
 * 
 */
public class AgentPacketHandler implements PacketHandler {

	private static Logger		logger		= Logger.getLogger(AgentPacketHandler.class);

	private Agent				agent		= null;
	private Connection			connection	= null;
	private boolean				registered	= false;
	private static Persistence	persistence	= Persistence.getInstance();

	/**
	 * 
	 * @param c
	 *            Connection to work with
	 */
	public AgentPacketHandler(Connection c) {
		logger.trace("new(Connection)");
		this.connection = c;
	}

	/**
	 * Main distributor of all Packages, received from an agent, to the logic.
	 * 
	 * @param packet
	 *            the received Packet
	 */
	@Override
	public void handlePacket(Packet packet) {
		logger.trace("handlePacket(Packet)");

		Map<String, Object> data = packet.getData();

		String key = Protocol.Key.TYPE;
		Integer type = (Integer) data.get(key);

		if (type == null) {
			logger.debug("type is null, setting type to invalid");
			type = Protocol.INVALID;
		}

		logger.debug("type: " + type);

		logger.debug("is registered: " + registered);

		if (!registered && type != Protocol.AGENT_HELLO) {
			logger.warn("got unexpected Packet from unregistered Agent, discarding");
			return;
		}

		switch (type) {
			case Protocol.AGENT_HELLO:
				logger.debug("agent hello");
				handleAgentHello(data);
				break;
			case Protocol.AGENT_HEARTBEAT:
				logger.debug("agent heartbeat");
				handleAgentHeartbeat(data);
				break;
			case Protocol.AUTOMATION_STATUS:
				logger.debug("automation status");
				handleAutomationStatus(data);
				break;
			default:
				logger.warn("unexpected type: " + type);
				data.put(Protocol.Key.TYPE, type);
				connection.forward(new Packet(data));
				break;
		}

		logger.trace("handle packet finished");
	}

	/**
	 * Handle an automation status Packet.
	 * 
	 * @param data
	 */
	private void handleAutomationStatus(Map<String, Object> data) {
		logger.trace("handleAutomationStatus(Map)");

		Integer id = (Integer) data.get(Protocol.Key.AUTOMATION_ID);
		String status = (String) data.get(Protocol.Key.AUTOMATION_STATUS);
		String message = (String) data.get(Protocol.Key.AUTOMATION_MESSAGE);

		AutomationStatus automationStatus = null;
		try {
			// try to pase the status
			automationStatus = AutomationStatus.valueOf(status.toLowerCase());
		} catch (IllegalArgumentException ex) {
			// don't set any default, skipping this packet follows
			logger.warn("unknown automation status: " + status);
		}

		// if a status could not be determined, skip packet
		if (automationStatus == null) {
			logger.warn("automation status not set");
			return;
		}

		// check if the automation exists
		Automation automation = (Automation) persistence.get(Automation.class, id);

		// if not, skip this status notification
		if (automation == null) {
			logger.error("got statusupdate for not existing automation with id " + id);
			return;
		}

		Message message2db = new Message();
		message2db.setAutomation(automation);
		message2db.setStatus(automationStatus);
		message2db.setMessage(message);

		// only for logging purposes, distinguish between talking and s.th. else
		if (automationStatus == AutomationStatus.talking) {
			logger.debug("automation [" + id + "] says: " + message);
		} else {
			logger.info("automation [" + id + "] notifies [" + automationStatus + "] " + message);
		}

		// save the message it's self
		persistence.save(message2db);

		// store the link to the automation
		automation.setStatus(automationStatus);
		automation.getMessages().add(message2db);
		persistence.update(automation);

		// now notify all those have an interest
		Data.notifyAutomationStatusListener(id, automationStatus, message);

		// if we don't expect anything further from this automation,
		// we can remove all Listeners, after they have been notified
		if (AutomationStatus.isEndState(automationStatus)) {
			logger.debug("removing this listener for automation: " + id);
			Data.removeAllAutomationStatusListener(id);
		}

		logger.trace("handleAutomationStatus finished");
	}

	private void handleAgentHeartbeat(Map<String, Object> data) {
		logger.trace("handleAgentHeartbeat(Map)");

		String date = (String) data.get(Protocol.Key.AGENT_LOCAL_TIME);
		Long timestamp = 0L;
		try {
			// trying to parse a time stamp
			timestamp = Long.parseLong(date);
		} catch (NumberFormatException e) {
			logger.warn("failed parsing timestamp: " + data);
			// if its invalid, we can't do anything!!!
			return;
		}
		// convert UNIX-like time stamp to java util.Date
		Date agent_localtime = new Date(timestamp * 1000);

		Heartbeat heartbeat = new Heartbeat();
		heartbeat.setAgent_localtime(agent_localtime);
		heartbeat.setAgent(this.agent);

		persistence.save(heartbeat);

		logger.trace("handleAgentHeartbeat finished");
	}

	private void handleAgentHello(Map<String, Object> data) {
		logger.trace("handleAgentHello(Map)");

		Map<String, Object> response = new HashMap<String, Object>();
		response.put(Protocol.Key.TYPE, Protocol.AGENT_HELLO_RESPONSE);

		String agent_id = (String) data.get(Protocol.Key.AGENT_ID);
		String plattform = (String) data.get(Protocol.Key.AGENT_PLATTFORM);
		String version = (String) data.get(Protocol.Key.AGENT_VERSION);
		String encoding = (String) data.get(Protocol.Key.AGENT_ENCODING);

		// check if a change is requested by the agent; if not, we would miss
		// the variable in the data
		if (encoding != null && !"".equals(encoding)) {
			try {
				Encoding e = Encoding.valueOf(encoding.toUpperCase());
				logger.info("agent requested encoding change from " + connection.getEncoding() + " to " + e);
				connection.setEncoding(e);
			} catch (IllegalArgumentException ex) {
				logger.warn("agent requested unknown encoding: " + encoding);
			}
		}

		// get the persistent equivalents of the server and agent to link'em
		Agent agent = (Agent) persistence.get(Agent.class, agent_id);
		Server server = (Server) persistence.get(Server.class, Data.getServer());

		boolean agentOnline = false;
		// check if the agent is connected to this instance of the server
		boolean agentConnectedHere = Data.getConnection(agent_id) == null ? false : true;
		boolean agentConnectedToDifferendServer = false;

		if (agent != null) {
			agentOnline = agent.getStatus() == Status.OFFLINE ? false : true;
			agentConnectedToDifferendServer = server.getId().equals(agent.getServer().getId()) ? false : true;
		}

		logger.debug("agent is online: " + agentOnline);
		logger.debug("agent is connected to this server: " + agentConnectedHere);
		logger.debug("agent connected to differend server: " + agentConnectedToDifferendServer);

		if (agent == null) {
			logger.info("agent [" + agent_id + "] unknown, creating new");

			agent = new Agent();
			agent.setId(agent_id);
			try {
				agent.setPlattform(Plattform.valueOf(plattform.toUpperCase()));
			} catch (IllegalArgumentException ex) {
				logger.warn("agent uses unknown plattform: " + plattform);
				agent.setPlattform(Plattform.UNKNOWN);
			}
			agent.setStatus(Status.ONLINE);
			agent.setVersion(version);
			agent.setServer(server);
			agent.setUpdated(new Date());

			logger.debug("new agent registered: " + agent);
			persistence.save(agent);

			this.agent = agent;

			Data.addConnection(agent_id, connection);

			response.put(Protocol.Key.RETURN_CODE, 0);
			response.put(Protocol.Key.SUCCESSFULL, true);
			response.put(Protocol.Key.MESSAGE, "congratulations " + agent_id + ", youre registered!");

			setRegistered(true);
		} else if (agentOnline && agentConnectedHere) {
			logger.warn("agent [" + agent_id + "] already registered, rejecting request");

			response.put(Protocol.Key.RETURN_CODE, -1);
			response.put(Protocol.Key.SUCCESSFULL, false);
			response.put(Protocol.Key.MESSAGE, "agent [" + agent_id + "] already registered and online");

			setRegistered(false);
		} else if (!agentOnline || (!agentOnline && agentConnectedHere) || (agentOnline && !agentConnectedToDifferendServer)) {
			logger.info("agent [" + agent_id + "] was last seen at " + agent.getUpdated() + ", but is offline, changing status to online, updating prefs");

			agent.setStatus(Status.ONLINE);
			try {
				agent.setPlattform(Plattform.valueOf(plattform.toUpperCase()));
			} catch (IllegalArgumentException ex) {
				logger.warn("agent uses unknown plattform: " + plattform);
				agent.setPlattform(Plattform.UNKNOWN);
			}
			agent.setVersion(version);
			agent.setServer(server);
			agent.setUpdated(new Date());

			logger.debug("agent reregisterd: " + agent);

			this.agent = agent;
			persistence.saveOrUpdate(agent);

			Data.addConnection(agent_id, connection);

			response.put(Protocol.Key.RETURN_CODE, 1);
			response.put(Protocol.Key.SUCCESSFULL, true);
			response.put(Protocol.Key.MESSAGE, "hello back again " + agent_id + " :)");

			setRegistered(true);
		} else {
			logger.warn("agent [" + agent_id + "] is alredy connected to server " + agent.getServer().getId() + ", rejecting request");

			response.put(Protocol.Key.RETURN_CODE, -2);
			response.put(Protocol.Key.SUCCESSFULL, false);
			response.put(Protocol.Key.MESSAGE, "agent [" + agent_id + "] already registered to " + agent.getServer().getId() + " and online");

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

		if (registered) {
			logger.info("agend [" + agent.getId() + "] closed connection, updating stats");
			agent.setStatus(Status.OFFLINE);
			agent.setUpdated(new Date());
			persistence.update(agent);
		} else {
			logger.warn("connection closed before agent registerd");
		}

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
		if (agent != null)
			logger.info("agent [" + agent.getId() + "] now is registerd? " + registered);
	}

	@Override
	public void handleOpen() {
		logger.trace("handleOpen");
	}

}
