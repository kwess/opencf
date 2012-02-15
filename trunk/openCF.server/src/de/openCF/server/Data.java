package de.openCF.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.openCF.server.connector.Connection;
import de.openCF.server.data.Server;

public class Data {

	private static Logger					logger		= Logger.getLogger(Data.class);
	private static Server					server		= null;
	private static Map<String, Connection>	connections	= new HashMap<String, Connection>();

	public static Connection addConnection(String key, Connection connection) {
		logger.trace("addConnection");
		return connections.put(key, connection);
	}

	public static Connection getConnection(String key) {
		logger.trace("getConnection");
		return connections.get(key);
	}

	public static Connection removeConnection(String key) {
		logger.trace("removeConnection");
		return connections.remove(key);
	}

	public static void setServer(Server server) {
		logger.trace("setServer");
		Data.server = server;
	}

	public static Server getServer() {
		logger.trace("getServer");
		return Data.server;
	}
}
