package de.openCF.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import de.openCF.server.connector.ServerConnectionAgent;
import de.openCF.server.connector.ServerConnector;

public class Server implements Runnable {

	public static final String	CONFIG_FILE	= "config/server.properties";

	private static final String	PROPERTIES_AGENTS_PORT				= "de.openCF.server.connector.agent.port";
	private static final String	PROPERTIES_AGENTS_NEEDSCLIENTAUTH	= "de.openCF.server.connector.agent.needsClientAuth";
	private static final String	PROPERTIES_AGENTS_POOLSIZE			= "de.openCF.server.connector.agent.poolSize";

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
		ServerConnector serverConnectorAgents = new ServerConnector();
		serverConnectorAgents.setPort(Integer.parseInt(this.properties.getProperty(PROPERTIES_AGENTS_PORT)));
		serverConnectorAgents.setNeedsClientAuth(Boolean.parseBoolean(this.properties.getProperty(PROPERTIES_AGENTS_NEEDSCLIENTAUTH)));
		serverConnectorAgents.setConnectionPoolSize(Integer.parseInt(this.properties.getProperty(PROPERTIES_AGENTS_POOLSIZE)));
		serverConnectorAgents.setConnectionImplementation(new ServerConnectionAgent());

		Executors.newSingleThreadExecutor().execute(serverConnectorAgents);
	}
	
}
