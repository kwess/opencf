package de.openCF.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.openCF.protocol.Connection;
import de.openCF.server.communication.AutomationStatusListener;
import de.openCF.server.data.AutomationStatus;

/**
 * 
 * Data Class for keeping runtime Data like assignment of an agent identifier to
 * its connection reference.
 * 
 * @author kristian.wessels
 * 
 */
public class Data {

	private static Logger										logger						= Logger.getLogger(Data.class);
	private static String										server_id					= null;
	private static Map<String, Connection>						connections					= new HashMap<String, Connection>();
	private static Map<Integer, List<AutomationStatusListener>>	automationStatusListener	= new HashMap<Integer, List<AutomationStatusListener>>();

	/**
	 * Stores a agend identifier - connection association
	 * 
	 * @param key
	 *            agent identifier
	 * @param connection
	 *            reference to its corresponding connection instance
	 * @return the connection which has been stored
	 */
	public synchronized static Connection addConnection(String key, Connection connection) {
		logger.trace("addConnection(String, Connection)");
		return connections.put(key, connection);
	}

	/**
	 * Retrieves a connection identified by the agent id
	 * 
	 * @param key
	 *            agent identifier
	 * @return the associated connection
	 */
	public synchronized static Connection getConnection(String key) {
		logger.trace("getConnection(String)");
		return connections.get(key);
	}

	/**
	 * Removes the connection
	 * 
	 * @param key
	 *            agent id
	 * @return the removed connection
	 */
	public synchronized static Connection removeConnection(String key) {
		logger.trace("removeConnection(String)");
		return connections.remove(key);
	}

	/**
	 * Sets the actual Server identifier
	 * 
	 * @param server
	 *            Server identifier
	 */
	public synchronized static void setServer(String server) {
		logger.trace("setServer(Server)");
		Data.server_id = server;
	}

	/**
	 * Get the actual Server identifier
	 * 
	 * @return Server idenitifer
	 */
	public synchronized static String getServer() {
		logger.trace("getServer");
		return Data.server_id;
	}

	/**
	 * Check the status of the agent whether its online
	 * 
	 * @param aid
	 *            agent identifier
	 * @return online-status
	 */
	public synchronized static boolean isAgenOnline(String aid) {
		return connections.containsKey(aid);
	}

	/**
	 * Removes an Listener of an automation Status
	 * 
	 * @param listener
	 */
	public synchronized static void removeAutomationStatusListener(AutomationStatusListener listener) {
		for (Map.Entry<Integer, List<AutomationStatusListener>> e : automationStatusListener.entrySet()) {
			e.getValue().remove(listener);
		}
	}

	/**
	 * 
	 * 
	 * @param aid
	 * @param listener
	 * @return
	 */
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
