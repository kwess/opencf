package de.openCF.server.persistence;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import de.openCF.server.data.Agent;
import de.openCF.server.data.Automation;
import de.openCF.server.data.AutomationStatus;
import de.openCF.server.data.Heartbeat;
import de.openCF.server.data.Message;
import de.openCF.server.data.Server;

public abstract class Persistence {

	private static Logger				logger	= Logger.getLogger(Persistence.class);
	public static final Configuration	CONFIGURATION;
	protected static SessionFactory		SESSION_FACTORY;
	protected static Session			session;

	static {
		logger.trace("static");

		CONFIGURATION = new Configuration();

		CONFIGURATION.addAnnotatedClass(Server.class);
		CONFIGURATION.addAnnotatedClass(Agent.class);
		CONFIGURATION.addAnnotatedClass(Heartbeat.class);
		CONFIGURATION.addAnnotatedClass(Automation.class);
		CONFIGURATION.addAnnotatedClass(Message.class);
		CONFIGURATION.addAnnotatedClass(AutomationStatus.class);

		logger.info("building session factory");
		SESSION_FACTORY = CONFIGURATION.buildSessionFactory();
	}

	public static Session getSession() {
		logger.trace("getSession");
		if (session == null || !session.isConnected()) {
			logger.info("open fresh session");
			session = SESSION_FACTORY.openSession();
		}
		return session;
	}

}
