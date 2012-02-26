package de.openCF.server.persistence;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
		session = getSession();
	}

	protected synchronized static Session getSession() {
		logger.trace("getSession");
		if (session == null || !session.isConnected()) {
			logger.info("open fresh session");
			session = SESSION_FACTORY.openSession();
		}
		return session;
	}

	public synchronized static Object save(Object o) {
		logger.trace("save(Object)");
		Transaction transaction = session.beginTransaction();
		logger.debug("saving: " + o);
		Object id = session.save(o);
		transaction.commit();
		return id;
	}

	public synchronized static void saveOrUpdate(Object o) {
		logger.trace("saveOrUpdate(Object)");
		Transaction transaction = session.beginTransaction();
		logger.debug("saveOrUpdate: " + o);
		session.saveOrUpdate(o);
		transaction.commit();
	}

	public synchronized static void delete(Object o) {
		logger.trace("delete(Object)");
		Transaction transaction = session.beginTransaction();
		logger.debug("deleting: " + o);
		session.delete(o);
		transaction.commit();
	}

	public synchronized static void update(Object o) {
		logger.trace("update(Object)");
		Transaction transaction = session.beginTransaction();
		logger.debug("updating: " + o);
		session.update(o);
		transaction.commit();
	}

	public synchronized static Object get(Class<?> c, Serializable id) {
		logger.trace("get(Object)");
		Object result = session.get(c, id);
		logger.debug("loaded by Class [" + c.getSimpleName() + "] and id [" + id + "]: " + result);
		return result;
	}

}
