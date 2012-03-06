package de.openCF.server.persistence;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.openCF.server.data.Agent;
import de.openCF.server.data.Automation;
import de.openCF.server.data.AutomationAction;
import de.openCF.server.data.AutomationControl;
import de.openCF.server.data.AutomationStatus;
import de.openCF.server.data.Heartbeat;
import de.openCF.server.data.Message;
import de.openCF.server.data.Server;

public class Persistence {

	private static Logger				logger	= Logger.getLogger(Persistence.class);
	public static final Configuration	CONFIGURATION;
	protected static SessionFactory		SESSION_FACTORY;
	protected Session					session;

	static {
		logger.trace("static");

		CONFIGURATION = new Configuration();

		CONFIGURATION.addAnnotatedClass(Server.class);
		CONFIGURATION.addAnnotatedClass(Agent.class);
		CONFIGURATION.addAnnotatedClass(Heartbeat.class);
		CONFIGURATION.addAnnotatedClass(Automation.class);
		CONFIGURATION.addAnnotatedClass(Message.class);
		CONFIGURATION.addAnnotatedClass(AutomationControl.class);
		CONFIGURATION.addAnnotatedClass(AutomationStatus.class);
		CONFIGURATION.addAnnotatedClass(AutomationAction.class);

		logger.info("building session factory");
		SESSION_FACTORY = CONFIGURATION.buildSessionFactory();
	}

	private Persistence() {
		session = getSession();
	}

	public synchronized static Persistence getInstance() {
		return new Persistence();
	}

	protected synchronized Session getSession() {
		logger.trace("getSession");
		if (session == null || !session.isConnected()) {
			logger.info("open fresh session");
			session = SESSION_FACTORY.openSession();
		}
		return session;
	}

	public synchronized Object save(Object o) {
		logger.trace("save(Object)");
		Transaction transaction = session.beginTransaction();
		Object id = session.save(o);
		logger.debug("saving: " + o);
		transaction.commit();
		return id;
	}

	public synchronized void saveOrUpdate(Object o) {
		logger.trace("saveOrUpdate(Object)");
		Transaction transaction = session.beginTransaction();
		session.saveOrUpdate(o);
		logger.debug("saveOrUpdate: " + o);
		transaction.commit();
	}

	public synchronized void delete(Object o) {
		logger.trace("delete(Object)");
		Transaction transaction = session.beginTransaction();
		session.delete(o);
		logger.debug("deleting: " + o);
		transaction.commit();
	}

	public synchronized void update(Object o) {
		logger.trace("update(Object)");
		Transaction transaction = session.beginTransaction();
		session.update(o);
		logger.debug("updating: " + o);
		transaction.commit();
	}

	public synchronized Object get(Class<?> c, Serializable id) {
		logger.trace("get(Object)");
		Object result = session.get(c, id);
		logger.debug("loaded by Class [" + c.getSimpleName() + "] and id [" + id + "]: " + result);
		return result;
	}

}
