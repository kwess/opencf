package de.openCF.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import de.openCF.server.connector.AgentConnection;
import de.openCF.server.connector.Connector;

public class Server implements Runnable {

	public static final String	CONFIG_FILE							= "config/server.properties";

	private static final String	PROPERTIES_AGENTS_PORT				= "de.openCF.server.connector.agent.port";
	private static final String	PROPERTIES_AGENTS_NEEDSCLIENTAUTH	= "de.openCF.server.connector.agent.needsClientAuth";
	private static final String	PROPERTIES_AGENTS_POOLSIZE			= "de.openCF.server.connector.agent.poolSize";
	private static final String	PROPERTIES_AGENTS_USE_SSL			= "de.openCF.server.connector.agents.useSSL";
	private static final String	PROPERTIES_AGENTS_DEBUG				= "de.openCF.server.connector.agents.debug";

	private Logger				logger								= Logger.getLogger(Server.class);
	private Properties			properties							= new Properties();

	public Server() {
		File file = new File(CONFIG_FILE);
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			this.properties.load(fileInputStream);
		} catch (FileNotFoundException e) {
			logger.error("config file not found", e);
		} catch (IOException e) {
			logger.error("cant read config file", e);
		}
	}

	@Override
	public void run() {
		Integer port = Integer.parseInt(this.properties.getProperty(PROPERTIES_AGENTS_PORT, "12345"));
		Boolean needsClientAuth = Boolean.parseBoolean(this.properties.getProperty(PROPERTIES_AGENTS_NEEDSCLIENTAUTH, "false"));
		Integer connectionPoolSize = Integer.parseInt(this.properties.getProperty(PROPERTIES_AGENTS_POOLSIZE, "1024"));
		Boolean useSSL = Boolean.parseBoolean(this.properties.getProperty(PROPERTIES_AGENTS_USE_SSL, "false"));
		Boolean debug = Boolean.parseBoolean(this.properties.getProperty(PROPERTIES_AGENTS_DEBUG, "false"));

		AgentConnection agentConnection = new AgentConnection(debug);

		Connector serverConnectorAgents = new Connector();

		serverConnectorAgents.setPort(port);
		serverConnectorAgents.setNeedsClientAuth(needsClientAuth);
		serverConnectorAgents.setConnectionPoolSize(connectionPoolSize);
		serverConnectorAgents.setUseSSL(useSSL);
		serverConnectorAgents.setConnectionImplementation(agentConnection);

		Executors.newSingleThreadExecutor().execute(serverConnectorAgents);
	}

}
