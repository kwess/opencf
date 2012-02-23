package de.openCF.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public abstract class Reader {

	private static Logger		logger			= Logger.getLogger(Reader.class);
	protected DataInputStream	dataInputStream	= null;

	protected Reader() {
		logger.trace("new");
	}

	protected Reader(InputStream inputStream) {
		logger.trace("new(InputStream)");
		this.dataInputStream = new DataInputStream(inputStream);
	}

	public void setInputStream(InputStream inputStream) {
		logger.trace("setInputStream(InputStream)");
		this.dataInputStream = new DataInputStream(inputStream);
	}

	public abstract Packet readPacket() throws IOException;
}
