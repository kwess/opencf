package de.openCF.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import de.openCF.protocol.Acceptor;
import de.openCF.server.communication.AgentConnectionFactory;
import de.openCF.server.communication.ControllerConnectionFactory;
import de.openCF.server.data.Plattform;
import de.openCF.server.data.Status;
import de.openCF.server.persistence.Persistence;

public class Server implements Runnable {

	public static final String	CONFIG_FILE		= "config/server.properties";

	private Logger				logger			= Logger.getLogger(Server.class);
	private ServerConfiguration	configuration	= new ServerConfiguration();

	public Server() {

	}

	@Override
	public void run() {
		configuration.load(CONFIG_FILE);

		Persistence persistence = Persistence.getInstance();

		ByteOrder byteOrder = ByteOrder.nativeOrder();

		logger.debug("native byte order: " + byteOrder);

		String hostname = "unknown";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			logger.debug("got hostname: " + hostname);
		} catch (UnknownHostException e) {
			logger.warn("cant get hostname of localhost", e);
		}

		String server_id = configuration.getServerId();
		de.openCF.server.data.Server server = (de.openCF.server.data.Server) persistence.get(de.openCF.server.data.Server.class, server_id);
		if (server != null) {
			logger.info("server loaded");
		} else {
			logger.info("server online for first time");
			server = new de.openCF.server.data.Server(server_id);
		}
		server.setAgentPort(configuration.getAgentPort());
		server.setControllerPort(configuration.getControllerPort());
		server.setPlattform(Plattform.GENERIC);
		server.setStatus(Status.ONLINE);
		server.setUpdated(new Date());
		if (!hostname.equals(server.getHostname()))
			logger.warn("server switched system since last startup: " + server.getHostname() + " --> " + hostname);
		server.setHostname(hostname);

		logger.info("publish server");
		Data.setServer(server.getId());
		persistence.saveOrUpdate(server);

		logger.debug("createing acceptor for agents");

		Acceptor acceptorAgents = new Acceptor();
		acceptorAgents.setPort(configuration.getAgentPort());
		acceptorAgents.setNeedsClientAuth(configuration.getAgentNeedsClientAuth());
		acceptorAgents.setConnectionPoolSize(configuration.getAgentConnectionPoolSize());
		acceptorAgents.setUseSSL(configuration.getAgentUseSSL());
		acceptorAgents.setConnectionFactory(new AgentConnectionFactory(configuration.getAgentDebug(), configuration.getAgentEncoding()));

		logger.debug("createing acceptor for controllers");

		Acceptor acceptorControllers = new Acceptor();
		acceptorControllers.setPort(configuration.getControllerPort());
		acceptorControllers.setNeedsClientAuth(configuration.getControllerNeedsClientAuth());
		acceptorControllers.setConnectionPoolSize(configuration.getControllerConnectionPoolSize());
		acceptorControllers.setUseSSL(configuration.getControllerUseSSL());
		acceptorControllers.setConnectionFactory(new ControllerConnectionFactory(configuration.getControllerDebug()));

		logger.info("server online");
		logger.debug(server);

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
