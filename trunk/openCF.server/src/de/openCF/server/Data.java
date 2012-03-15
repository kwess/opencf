package de.openCF.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.openCF.protocol.Connection;
import de.openCF.server.communication.AutomationStatusListener;
import de.openCF.server.data.AutomationStatus;

public class Data {

	private static Logger										logger						= Logger.getLogger(Data.class);
	private static String										server_id					= null;
	private static Map<String, Connection>						connections					= new HashMap<String, Connection>();
	private static Map<Integer, List<AutomationStatusListener>>	automationStatusListener	= new HashMap<Integer, List<AutomationStatusListener>>();

	public synchronized static Connection addConnection(String key, Connection connection) {
		logger.trace("addConnection(String, Connection)");
		return connections.put(key, connection);
	}

	public synchronized static Connection getConnection(String key) {
		logger.trace("getConnection(String)");
		return connections.get(key);
	}

	public synchronized static Connection removeConnection(String key) {
		logger.trace("removeConnection(String)");
		return connections.remove(key);
	}

	public synchronized static void setServer(String server) {
		logger.trace("setServer(Server)");
		Data.server_id = server;
	}

	public synchronized static String getServer() {
		logger.trace("getServer");
		return Data.server_id;
	}

	public synchronized static boolean isAgenOnline(String aid) {
		return connections.containsKey(aid);
	}

	public synchronized static void removeAutomationStatusListener(AutomationStatusListener listener) {
		for (Map.Entry<Integer, List<AutomationStatusListener>> e : automationStatusListener.entrySet()) {
			e.getValue().remove(listener);
		}
	}

	public synchronized static boolean addAutomationStatusListerner(Integer aid, AutomationStatusListener listener) {
		logger.trace("addAutomationStatusListener(Integer, AutomationStatusListener)");
		List<AutomationStatusListener> list = Data.automationStatusListener.get(aid);
		if (list == null) {
			logger.debug("creating new listener-list for listeners of automation " + aid);
			list = new ArrayList<AutomationStatusListener>();
			Data.automationStatusListener.put(aid, list);
		}
		return list.add(listener);
	}

	public synchronized static boolean removeAutomationStatusListener(Integer aid, AutomationStatusListener listener) {
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

	public synchronized static boolean removeAllAutomationStatusListener(Integer aid) {
		logger.trace("removeAllAutomationStatusListener(Integer)");
		if (Data.automationStatusListener.containsKey(aid))
			return Data.automationStatusListener.remove(aid) != null ? true : false;
		return false;
	}

	public synchronized static void notifyAutomationStatusListener(Integer aid, AutomationStatus status, String message) {
		logger.trace("notifyAutomationStatusListener");
		logger.debug("Automation ID: " + aid);
		logger.debug("Automation Status: " + status);
		logger.debug("Automation Message: " + message);
		List<AutomationStatusListener> list = Data.automationStatusListener.get(aid);
		if (list == null) {
			logger.warn("no listeners to inform");
			return;
		}
		for (AutomationStatusListener l : list) {
			logger.debug("notify " + l);
			l.statusChanged(aid, status, message);
		}
	}
}
