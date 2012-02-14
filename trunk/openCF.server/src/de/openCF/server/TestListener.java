package de.openCF.server;

import org.apache.log4j.Logger;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;

public class TestListener implements PostInsertEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4069085681573666040L;

	@Override
	public void onPostInsert(PostInsertEvent arg0) {
		Logger.getLogger(TestListener.class).debug("listen");
	}

}
