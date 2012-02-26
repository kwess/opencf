package de.openCF.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import de.openCF.protocol.Acceptor;
import de.openCF.protocol.PacketHelper.Encoding;
import de.openCF.server.communication.AgentConnectionFactory;
import de.openCF.server.communication.ControllerConnectionFactory;
import de.openCF.server.persistence.Persistence;

public class Server implements Runnable {

	public static final String	CONFIG_FILE					= "config/server.properties";

	private static final String	SERVER_ID					= "openCF.server.name";

	private static final String	AGENT_PORT					= "openCF.server.acceptor.agent.port";
	private static final String	AGENT_NEEDSCLIENTAUTH		= "openCF.server.acceptor.agent.needsClientAuth";
	private static final String	AGENT_POOLSIZE				= "openCF.server.acceptor.agent.poolSize";
	private static final String	AGENT_USE_SSL				= "openCF.server.acceptor.agent.useSSL";
	private static final String	AGENT_DEBUG					= "openCF.server.acceptor.agent.debug";
	private static final String	AGENT_PROTOCOL_ENCODING		= "openCF.server.acceptor.agent.protocol.encoding";

	private static final String	CONTROLLER_PORT				= "openCF.server.acceptor.controller.port";
	private static final String	CONTROLLER_NEEDSCLIENTAUTH	= "openCF.server.acceptor.controller.needsClientAuth";
	private static final String	CONTROLLER_POOLSIZE			= "openCF.server.acceptor.controller.poolSize";
	private static final String	CONTROLLER_USE_SSL			= "openCF.server.acceptor.controller.useSSL";
	private static final String	CONTROLLER_DEBUG			= "openCF.server.acceptor.controller.debug";

	private Logger				logger						= Logger.getLogger(Server.class);
	private Properties			properties					= new Properties();

	public Server() {
		logger.trace("new");

		logger.debug("open config_file: " + CONFIG_FILE);
		File file = new File(CONFIG_FILE);
		FileInputStream fileInputStream;
		try {
			logger.trace("reading from config_file");
			fileInputStream = new FileInputStream(file);
			logger.trace("loading config_file into properties");
			this.properties.load(fileInputStream);
		} catch (FileNotFoundException e) {
			logger.error("config file not found", e);
		} catch (IOException e) {
			logger.error("cant read config file", e);
		}
	}

	@Override
	public void run() {
		logger.trace("run");

		ByteOrder byteOrder = ByteOrder.nativeOrder();

		logger.debug("native byte order: " + byteOrder);

		String server_id = this.properties.getProperty(SERVER_ID);
		de.openCF.server.data.Server server = new de.openCF.server.data.Server();
		server.setId(server_id);
		Data.setServer(server);

		Persistence.saveOrUpdate(server);

		Integer agentPort = Integer.parseInt(this.properties.getProperty(AGENT_PORT, "12345"));
		Boolean agentNeedsClientAuth = Boolean.parseBoolean(this.properties.getProperty(AGENT_NEEDSCLIENTAUTH, "false"));
		Integer agentConnectionPoolSize = Integer.parseInt(this.properties.getProperty(AGENT_POOLSIZE, "1024"));
		Boolean agentUseSSL = Boolean.parseBoolean(this.properties.getProperty(AGENT_USE_SSL, "false"));
		Boolean agentDebug = Boolean.parseBoolean(this.properties.getProperty(AGENT_DEBUG, "false"));
		Encoding agentEncoding = Encoding.valueOf(this.properties.getProperty(AGENT_PROTOCOL_ENCODING));

		Acceptor acceptorAgents = new Acceptor();
		acceptorAgents.setPort(agentPort);
		acceptorAgents.setNeedsClientAuth(agentNeedsClientAuth);
		acceptorAgents.setConnectionPoolSize(agentConnectionPoolSize);
		acceptorAgents.setUseSSL(agentUseSSL);
		acceptorAgents.setConnectionFactory(new AgentConnectionFactory(agentDebug, agentEncoding));

		Integer controllerPort = Integer.parseInt(this.properties.getProperty(CONTROLLER_PORT, "12346"));
		Boolean controllerNeedsClientAuth = Boolean.parseBoolean(this.properties.getProperty(CONTROLLER_NEEDSCLIENTAUTH, "false"));
		Integer controllerConnectionPoolSize = Integer.parseInt(this.properties.getProperty(CONTROLLER_POOLSIZE, "1024"));
		Boolean controllerUseSSL = Boolean.parseBoolean(this.properties.getProperty(CONTROLLER_USE_SSL, "false"));
		Boolean controllerDebug = Boolean.parseBoolean(this.properties.getProperty(CONTROLLER_DEBUG, "false"));

		Acceptor acceptorControllers = new Acceptor();
		acceptorControllers.setPort(controllerPort);
		acceptorControllers.setNeedsClientAuth(controllerNeedsClientAuth);
		acceptorControllers.setConnectionPoolSize(controllerConnectionPoolSize);
		acceptorControllers.setUseSSL(controllerUseSSL);
		acceptorControllers.setConnectionFactory(new ControllerConnectionFactory(controllerDebug));

		logger.info("starting acceptor for agents");
		Executors.newSingleThreadExecutor().execute(acceptorAgents);
		logger.info("starting acceptor for controllers");
		Executors.newSingleThreadExecutor().execute(acceptorControllers);

		logger.trace("run finished");
	}

	public static void main(String[] args) {
		Executors.newSingleThreadExecutor().execute(new Server());
	}
}
