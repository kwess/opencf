package de.openCF.server.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import de.openCF.protocol.Connection;
import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;
import de.openCF.protocol.Protocol;
import de.openCF.server.Data;
import de.openCF.server.data.Agent;
import de.openCF.server.data.Automation;
import de.openCF.server.data.AutomationAction;
import de.openCF.server.data.AutomationControl;
import de.openCF.server.data.AutomationQueryType;
import de.openCF.server.data.AutomationStatus;
import de.openCF.server.data.Server;
import de.openCF.server.persistence.Persistence;

public class ControllerPacketHandler implements PacketHandler, AutomationStatusListener {

	private static Logger		logger		= Logger.getLogger(ControllerPacketHandler.class);
	private static Persistence	persistence	= Persistence.getInstance();
	private Connection			connection	= null;

	public ControllerPacketHandler(Connection controllerConnection) {
		this.connection = controllerConnection;
	}

	@Override
	public void handlePacket(Packet packet) {
		logger.trace("handlePacket");

		Map<String, Object> data = packet.getData();

		String key = Protocol.Key.TYPE;
		Integer type = (Integer) data.get(key);

		if (type == null)
			type = Protocol.INVALID;

		switch (type) {
			case Protocol.AUTOMATION_CONTROL:
				logger.debug("automation control");
				handleAutomationControl(data);
				break;
			case Protocol.AUTOMATION_QUERY:
				logger.debug("automation query");
				handleAutomationQuery(data);
				break;
			default:
				logger.warn("unexpected type: " + type);
				break;
		}

		logger.trace("handle packet finished");
	}

	@SuppressWarnings("unchecked")
	private void handleAutomationQuery(Map<String, Object> data) {
		logger.trace("handleAutomationQuery");

		String query = (String) data.get(Protocol.Key.AUTOMATION_QUERY);
		AutomationQueryType automationQueryType = AutomationQueryType.valueOf(query.toUpperCase());

		if (automationQueryType == null) {
			logger.warn("unknown automation query type");
			return;
		}

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		switch (automationQueryType) {
			case AGENT:
				DetachedCriteria c = DetachedCriteria.forClass(Agent.class);
				List<Agent> agents = (List<Agent>) persistence.list(c);
				for (Agent a : agents) {
					Map<String, Object> element = new HashMap<String, Object>();
					element.put(Protocol.Key.AGENT_ID, a.getId());
					element.put(Protocol.Key.AGENT_PLATTFORM, a.getPlattform().toString());
					element.put(Protocol.Key.AGENT_VERSION, a.getVersion());
					element.put(Protocol.Key.STATUS, a.getStatus().toString());
					element.put(Protocol.Key.SERVER_ID, a.getServer().getId());
					result.add(element);
				}
				break;
			case SERVER:
				DetachedCriteria c2 = DetachedCriteria.forClass(Server.class);
				List<Server> server = (List<Server>) persistence.list(c2);
				for (Server s : server) {
					Map<String, Object> element = new HashMap<String, Object>();
					element.put(Protocol.Key.SERVER_ID, s.getId());
					element.put(Protocol.Key.SERVER_PLATTFORM, s.getPlattform().toString());
					element.put(Protocol.Key.SERVER_HOSTNAME, s.getHostname());
					result.add(element);
				}
				break;
			case AUTOMATION:
				String status = (String) data.get(Protocol.Key.AUTOMATION_QUERY_PARAMETER);
				DetachedCriteria c3 = DetachedCriteria.forClass(Automation.class);
				if (status != null) {
					AutomationStatus automationStatus = AutomationStatus.valueOf(status.toLowerCase());
					c3.add(Restrictions.eq("status", automationStatus));
				}
				List<Automation> automation = (List<Automation>) persistence.list(c3);
				for (Automation a : automation) {
					Map<String, Object> element = new HashMap<String, Object>();
					element.put(Protocol.Key.AUTOMATION_ID, a.getId());
					element.put(Protocol.Key.AUTOMATION_STATUS, a.getStatus().toString());
					element.put(Protocol.Key.AUTOMATION_AGENT, a.getAgent().getId());
					result.add(element);
				}
				break;
			default:
				logger.warn("unexpected automation query type: " + query);
				break;
		}

		Map<String, Object> d = new HashMap<String, Object>();
		d.put(Protocol.Key.TYPE, Protocol.AUTOMATION_QUERY);
		d.put(Protocol.Key.AUTOMATION_QUERY, query);
		d.put(Protocol.Key.AUTOMATION_QUERY_RESULT, result);

		Packet p = new Packet();
		p.setData(d);

		connection.forward(p);
	}

	@SuppressWarnings("unchecked")
	private void handleAutomationControl(Map<String, Object> data) {
		logger.trace("handleAutomationControl");

		List<String> agent_ids = (List<String>) data.get(Protocol.Key.AGENT_ID);
		List<Integer> automation_ids = (List<Integer>) data.get(Protocol.Key.AUTOMATION_ID);
		String action = (String) data.get(Protocol.Key.AUTOMATION_ACTION);
		String reason = (String) data.get(Protocol.Key.AUTOMATION_REASON);
		String descriptor = (String) data.get(Protocol.Key.AUTOMATION_DESCRIPTOR);
		Map<String, Object> parameter = (Map<String, Object>) data.get(Protocol.Key.AUTOMATION_PARAMETER);

		logger.debug("agent_ids: " + agent_ids);
		logger.debug("action: " + action);
		logger.debug("reason: " + reason);
		logger.debug("descriptor: " + descriptor);
		logger.debug("parameter: " + parameter);

		AutomationAction automationAction = AutomationAction.valueOf(action.toLowerCase());
		if (automationAction == null) {
			logger.warn("unknown automation action");
			return;
		}

		switch (automationAction) {
			case stop:
				logger.debug("action stop");
				for (Integer i : automation_ids) {
					Automation a = (Automation) persistence.get(Automation.class, i);
					AutomationControl automationControl = new AutomationControl();
					automationControl.setAutomation(a);
					automationControl.setAction(automationAction);

					if (a == null) {
						logger.warn("cant stop unknown automation with id: " + i);
						automationControl.setSuccessfull(false);
						continue;
					}

					String a_id = a.getAgent().getId();
					if (Data.isAgenOnline(a_id)) {
						automationControl.setSuccessfull(true);

						Connection c = Data.getConnection(a_id);
						Map<String, Object> d = new HashMap<String, Object>();
						d.put(Protocol.Key.TYPE, Protocol.AUTOMATION_CONTROL);
						d.put(Protocol.Key.AUTOMATION_ACTION, automationAction.toString());
						d.put(Protocol.Key.AUTOMATION_ID, i);
						Packet p = new Packet();
						p.setData(d);
						c.forward(p);
					} else {
						automationControl.setSuccessfull(false);
					}

					persistence.save(automationControl);
				}
				break;
			case listen:
				logger.debug("action listen");
				for (Integer i : automation_ids) {
					Data.addAutomationStatusListerner(i, this);

					Automation a = (Automation) persistence.get(Automation.class, i);
					AutomationControl automationControl = new AutomationControl();
					automationControl.setAutomation(a);
					automationControl.setAction(automationAction);

					if (a == null) {
						logger.warn("cant listen to unknown automation with id: " + i);
						automationControl.setSuccessfull(false);
						continue;
					}

					persistence.save(automationControl);
				}
				break;
			case start:
				logger.debug("action start");
				for (String s : agent_ids) {
					Packet p = new Packet();
					p.setData(data);
					logger.info("forewarding to " + s + ": " + p);

					AutomationControl automationControl = new AutomationControl();
					automationControl.setAction(automationAction);

					if (Data.isAgenOnline(s)) {
						if (automationAction == AutomationAction.start) {
							logger.info("starting new automation");
							Automation automation = new Automation();
							Agent agent = (Agent) persistence.get(Agent.class, s);
							automation.setAgent(agent);
							persistence.save(automation);
							persistence.saveOrUpdate(agent);
							automation_ids = new ArrayList<Integer>();
							automation_ids.add(automation.getId());
							data.put(Protocol.Key.AUTOMATION_ID, automation.getId());

							automationControl.setAutomation(automation);
						}

						if (automation_ids == null)
							continue;

						for (Integer id : automation_ids) {
							if (automationAction != AutomationAction.listen) {
								logger.info("forward command");
								Data.getConnection(s).forward(p);
							}

							Data.addAutomationStatusListerner(id, this);
						}
					} else {
						automationControl.setSuccessfull(false);
						statusChanged(0, AutomationStatus.start_failed, "Agent not online");
					}

					persistence.save(automationControl);
				}
				break;
			default:
				logger.warn("unexpected action: " + automationAction);
				break;
		}
	}

	@Override
	public void handleClose() {
		logger.trace("handleClose");

		Data.removeAutomationStatusListener(this);
	}

	@Override
	public void handleOpen() {
		logger.trace("handleOpen");
	}

	@Override
	public void statusChanged(Integer id, AutomationStatus status, String Message) {
		logger.trace("statusChanged(Integer, AutomationStatus, String)");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Protocol.Key.TYPE, Protocol.AUTOMATION_STATUS);
		data.put(Protocol.Key.AUTOMATION_STATUS, status.toString());
		data.put(Protocol.Key.AUTOMATION_ID, id);
		data.put(Protocol.Key.AUTOMATION_MESSAGE, Message);

		Packet p = new Packet();
		p.setData(data);

		connection.forward(p);
	}
}
