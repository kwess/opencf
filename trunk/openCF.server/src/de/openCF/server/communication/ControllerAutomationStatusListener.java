package de.openCF.server.communication;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.openCF.protocol.Connection;
import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketKeys;
import de.openCF.protocol.PacketType;
import de.openCF.server.Data;
import de.openCF.server.data.AutomationStatus;

public class ControllerAutomationStatusListener implements AutomationStatusListener {

	private static Logger	logger		= Logger.getLogger(ControllerAutomationStatusListener.class);

	private Connection		connection	= null;

	public ControllerAutomationStatusListener(Connection connection) {
		super();
		logger.trace("new(Connection)");
		if (connection == null)
			throw new IllegalArgumentException("connectin is null");
		this.connection = connection;
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
		data.put(PacketKeys.AUTOMATION_STATUS, status);
		data.put(PacketKeys.AUTOMATION_ID, id);

		Packet p = new Packet();
		p.setData(data);

		connection.forward(p);
	}

}
