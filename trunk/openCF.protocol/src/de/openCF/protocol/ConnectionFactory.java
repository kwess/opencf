package de.openCF.protocol;

public abstract class ConnectionFactory {

	protected boolean	debug	= false;

	public ConnectionFactory(boolean debug) {
		super();
		this.debug = debug;
	}

	public abstract Connection createConnection();

}
