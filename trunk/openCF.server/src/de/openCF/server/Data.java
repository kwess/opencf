package de.openCF.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.openCF.protocol.Connection;
import de.openCF.server.communication.AutomationStatusListener;
import de.openCF.server.data.AutomationStatus;
import de.openCF.server.data.Server;

public class Data {

	private static Logger										logger						= Logger.getLogger(Data.class);
	private static Server										server						= null;
	private static Map<String, Connection>						connections					= new HashMap<String, Connection>();
	private static Map<Integer, List<AutomationStatusListener>>	automationStatusListener	= new HashMap<Integer, List<AutomationStatusListener>>();

	public static Connection addConnection(String key, Connection connection) {
		logger.trace("addConnection(String, Connection)");
		return connections.put(key, connection);
	}

	public static Connection getConnection(String key) {
		logger.trace("getConnection(String)");
		return connections.get(key);
	}

	public static Connection removeConnection(String key) {
		logger.trace("removeConnection(String)");
		return connections.remove(key);
	}

	public static void setServer(Server server) {
		logger.trace("setServer(Server)");
		Data.server = server;
	}

	public static Server getServer() {
		logger.trace("getServer");
		return Data.server;
	}

	public static boolean isAgenOnline(String aid) {
		return connections.containsKey(aid);
	}

	public static void removeAutomationStatusListener(AutomationStatusListener listener) {
		for (Map.Entry<Integer, List<AutomationStatusListener>> e : automationStatusListener.entrySet()) {
			e.getValue().remove(listener);
		}
	}

	public static boolean addAutomationStatusListerner(Integer aid, AutomationStatusListener listener) {
		logger.trace("addAutomationStatusListener(Integer, AutomationStatusListener)");
		List<AutomationStatusListener> list = Data.automationStatusListener.get(aid);
		if (list == null) {
			logger.debug("creating new listener-list for listeners of automation " + aid);
			list = new ArrayList<AutomationStatusListener>();
			Data.automationStatusListener.put(aid, list);
		}
		return list.add(listener);
	}

	public static boolean removeAutomationStatusListener(Integer aid, AutomationStatusListener listener) {
		logger.trace("removeAutomationStatusListener(Integer, AutomationStatusListener)");
		if (!Data.automationStatusListener.containsKey(aid))
			return false;
		boolean ret = Data.automationStatusListener.get(aid).remove(listener);
		if (Data.automationStatusListener.isEmpty()) {
			logger.debug("no more registered listeners, removing list for " + aid);
			Data.automationStatusListener.remove(aid);
		}
		return ret;
	}

	public static boolean removeAllAutomationStatusListener(Integer aid) {
		logger.trace("removeAllAutomationStatusListener(Integer)");
		if (Data.automationStatusListener.containsKey(aid))
			return Data.automationStatusListener.remove(aid) != null ? true : false;
		return false;
	}

	public static void notifyAutomationStatusListener(Integer aid, AutomationStatus status, String message) {
		logger.trace("notifyAutomationStatusListener");
		logger.debug("Automation ID: " + aid);
		logger.debug("Automation Status: " + status);
		logger.debug("Automation Message: " + message);
		List<AutomationStatusListener> list = Data.automationStatusListener.get(aid);
		for (AutomationStatusListener l : list) {
			logger.debug("notify " + l);
			l.statusChanged(aid, status, message);
		}
	}
}
