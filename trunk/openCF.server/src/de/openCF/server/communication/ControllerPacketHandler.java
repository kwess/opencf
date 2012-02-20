package de.openCF.server.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import de.openCF.protocol.Connection;
import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;
import de.openCF.protocol.PacketKeys;
import de.openCF.protocol.PacketType;
import de.openCF.server.Data;
import de.openCF.server.data.Automation;
import de.openCF.server.data.AutomationAction;
import de.openCF.server.data.AutomationStatus;
import de.openCF.server.persistence.Persistence;

public class ControllerPacketHandler implements PacketHandler, AutomationStatusListener {

	private static Logger	logger		= Logger.getLogger(ControllerPacketHandler.class);

	private Connection		connection	= null;

	public ControllerPacketHandler(Connection controllerConnection) {
		this.connection = controllerConnection;
	}

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
		List<Integer> automation_ids = (List<Integer>) data.get(PacketKeys.AUTOMATION_ID);
		String action = (String) data.get(PacketKeys.AUTOMATION_ACTION);
		String reason = (String) data.get(PacketKeys.AUTOMATION_REASON);
		String descriptor = (String) data.get(PacketKeys.AUTOMATION_DESCRIPTOR);
		Map<String, Object> parameter = (Map<String, Object>) data.get(PacketKeys.AUTOMATION_PARAMETER);

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

		Session session = Persistence.getSession();
		session.beginTransaction();

		for (String s : agent_ids) {
			Packet p = new Packet();
			p.setData(data);
			logger.info("forewarding to " + s + ": " + p);
			if (Data.isAgenOnline(s)) {
				if (automationAction == AutomationAction.start) {
					logger.info("starting new automation");
					Automation automation = new Automation();
					session.save(automation);
					automation_ids = new ArrayList<Integer>();
					automation_ids.add(automation.getId());
					data.put(PacketKeys.AUTOMATION_ID, automation.getId());
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
				statusChanged(0, AutomationStatus.start_failed, "Agent not online");
			}
		}

		session.getTransaction().commit();
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
		if (AutomationStatus.isEndState(status)) {
			logger.debug("removing this listener for automation: " + id);
			Data.removeAutomationStatusListener(id, this);
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(PacketKeys.TYPE, PacketType.AUTOMATION_STATUS);
		data.put(PacketKeys.AUTOMATION_STATUS, status.toString());
		data.put(PacketKeys.AUTOMATION_ID, id);
		data.put(PacketKeys.AUTOMATION_MESSAGE, Message);

		Packet p = new Packet();
		p.setData(data);

		connection.forward(p);
	}

}
