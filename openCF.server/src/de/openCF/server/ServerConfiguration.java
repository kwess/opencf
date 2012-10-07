package de.openCF.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.openCF.protocol.PacketHelper.Encoding;

public class ServerConfiguration {

	private static final String		AGENT_PORT								= "openCF.server.acceptor.agent.port";
	private static final String		AGENT_NEEDSCLIENTAUTH					= "openCF.server.acceptor.agent.needsClientAuth";
	private static final String		AGENT_POOLSIZE							= "openCF.server.acceptor.agent.poolSize";
	private static final String		AGENT_USE_SSL							= "openCF.server.acceptor.agent.useSSL";
	private static final String		AGENT_DEBUG								= "openCF.server.acceptor.agent.debug";
	private static final String		AGENT_PROTOCOL_ENCODING					= "openCF.server.acceptor.agent.protocol.encoding";

	private static final String		CONTROLLER_PORT							= "openCF.server.acceptor.controller.port";
	private static final String		CONTROLLER_NEEDSCLIENTAUTH				= "openCF.server.acceptor.controller.needsClientAuth";
	private static final String		CONTROLLER_POOLSIZE						= "openCF.server.acceptor.controller.poolSize";
	private static final String		CONTROLLER_USE_SSL						= "openCF.server.acceptor.controller.useSSL";
	private static final String		CONTROLLER_DEBUG						= "openCF.server.acceptor.controller.debug";

	private static final String		SERVER_ID								= "openCF.server.name";

	public static final Integer		DEFAULT_AGENT_PORT						= 12345;
	public static final Boolean		DEFAULT_AGENT_NEEDS_CLIENT_AUTH			= false;
	public static final Boolean		DEFAULT_AGENT_USE_SSL					= false;
	public static final Integer		DEFAULT_AGENT_CONNECTION_POOL_SIZE		= 1024;
	public static final Boolean		DEFAULT_AGENT_DEBUG						= false;
	public static final Encoding	DEFAULT_AGENT_ENCODING					= Encoding.XML;

	public static final Integer		DEFAULT_CONTROLLER_PORT					= 12346;
	public static final Boolean		DEFAULT_CONTROLLER_NEEDS_CLIENT_AUTH	= false;
	public static final Integer		DEFAULT_CONTROLLER_CONNECTION_POOL_SIZE	= 128;
	public static final Boolean		DEFAULT_CONTROLLER_USE_SSL				= false;
	public static final Boolean		DEFAULT_CONTROLLER_DEBUG				= false;

	private Logger					logger									= Logger.getLogger(ServerConfiguration.class);
	private Properties				properties								= new Properties();

	private String					serverId								= "server";

	private Integer					agentPort								= DEFAULT_AGENT_PORT;
	private Boolean					agentNeedsClientAuth					= DEFAULT_AGENT_NEEDS_CLIENT_AUTH;
	private Boolean					agentUseSSL								= DEFAULT_AGENT_USE_SSL;
	private Integer					agentConnectionPoolSize					= DEFAULT_AGENT_CONNECTION_POOL_SIZE;
	private Boolean					agentDebug								= DEFAULT_AGENT_DEBUG;
	private Encoding				agentEncoding							= DEFAULT_AGENT_ENCODING;

	private Integer					controllerPort							= DEFAULT_CONTROLLER_PORT;
	private Boolean					controllerNeedsClientAuth				= DEFAULT_CONTROLLER_NEEDS_CLIENT_AUTH;
	private Integer					controllerConnectionPoolSize			= DEFAULT_CONTROLLER_CONNECTION_POOL_SIZE;
	private Boolean					controllerUseSSL						= DEFAULT_CONTROLLER_USE_SSL;
	private Boolean					controllerDebug							= DEFAULT_CONTROLLER_DEBUG;

	public void load(String path) {
		logger.debug("open config_file: " + path);
		File file = new File(path);
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

		logger.info("loading settings");

		if (this.properties.containsKey(AGENT_PORT))
			agentPort = Integer.parseInt(this.properties.getProperty(AGENT_PORT));
		if (this.properties.containsKey(AGENT_NEEDSCLIENTAUTH))
			agentNeedsClientAuth = Boolean.parseBoolean(this.properties.getProperty(AGENT_NEEDSCLIENTAUTH));
		if (this.properties.containsKey(AGENT_POOLSIZE))
			agentConnectionPoolSize = Integer.parseInt(this.properties.getProperty(AGENT_POOLSIZE));
		if (this.properties.containsKey(AGENT_USE_SSL))
			agentUseSSL = Boolean.parseBoolean(this.properties.getProperty(AGENT_USE_SSL));
		if (this.properties.containsKey(AGENT_DEBUG))
			agentDebug = Boolean.parseBoolean(this.properties.getProperty(AGENT_DEBUG));
		if (this.properties.containsKey(agentEncoding))
			agentEncoding = Encoding.valueOf(this.properties.getProperty(AGENT_PROTOCOL_ENCODING));

		if (this.properties.containsKey(controllerPort))
			controllerPort = Integer.parseInt(this.properties.getProperty(CONTROLLER_PORT));
		if (this.properties.containsKey(controllerNeedsClientAuth))
			controllerNeedsClientAuth = Boolean.parseBoolean(this.properties.getProperty(CONTROLLER_NEEDSCLIENTAUTH));
		if (this.properties.containsKey(CONTROLLER_POOLSIZE))
			controllerConnectionPoolSize = Integer.parseInt(this.properties.getProperty(CONTROLLER_POOLSIZE));
		if (this.properties.containsKey(CONTROLLER_USE_SSL))
			controllerUseSSL = Boolean.parseBoolean(this.properties.getProperty(CONTROLLER_USE_SSL));
		if (this.properties.containsKey(CONTROLLER_DEBUG))
			controllerDebug = Boolean.parseBoolean(this.properties.getProperty(CONTROLLER_DEBUG, "false"));

		if (this.properties.containsKey(SERVER_ID))
			serverId = this.properties.getProperty(SERVER_ID);
	}

	public String getServerId() {
		return serverId;
	}

	public Integer getAgentPort() {
		return agentPort;
	}

	public Boolean getAgentNeedsClientAuth() {
		return agentNeedsClientAuth;
	}

	public Boolean getAgentUseSSL() {
		return agentUseSSL;
	}

	public Integer getAgentConnectionPoolSize() {
		return agentConnectionPoolSize;
	}

	public Boolean getAgentDebug() {
		return agentDebug;
	}

	public Encoding getAgentEncoding() {
		return agentEncoding;
	}

	public Integer getControllerPort() {
		return controllerPort;
	}

	public Boolean getControllerNeedsClientAuth() {
		return controllerNeedsClientAuth;
	}

	public Integer getControllerConnectionPoolSize() {
		return controllerConnectionPoolSize;
	}

	public Boolean getControllerUseSSL() {
		return controllerUseSSL;
	}

	public Boolean getControllerDebug() {
		return controllerDebug;
	}

}
