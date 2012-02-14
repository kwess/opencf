package de.openCF.server.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import de.openCF.server.data.Agent;
import de.openCF.server.data.Server;

public abstract class Persistence {

	public static final Configuration	CONFIGURATION	= new Configuration();
	protected static SessionFactory		SESSION_FACTORY;

	static {
		CONFIGURATION.addClass(Server.class);
		CONFIGURATION.addClass(Agent.class);

		SESSION_FACTORY = CONFIGURATION.buildSessionFactory();
	}

	public static Session getSession() {
		return SESSION_FACTORY.openSession();
	}

}
