package de.openCF.server.persistence;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import de.openCF.server.data.Agent;
import de.openCF.server.data.Server;

public abstract class Persistence {

	private static Logger				logger	= Logger.getLogger(Persistence.class);
	public static final Configuration	CONFIGURATION;
	protected static SessionFactory		SESSION_FACTORY;

	static {
		CONFIGURATION = new Configuration();

		logger.debug("adding " + Server.class);
		CONFIGURATION.addAnnotatedClass(Server.class);
		logger.debug("adding " + Agent.class);
		CONFIGURATION.addAnnotatedClass(Agent.class);

		logger.debug("building session factory");
		SESSION_FACTORY = CONFIGURATION.buildSessionFactory();
	}

	public static Session getSession() {
		logger.trace("getSession");
		return SESSION_FACTORY.openSession();
	}

}
