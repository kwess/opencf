package de.openCF.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import de.openCF.protocol.PacketHelper.Encoding;

public abstract class Writer {

	private static Logger		logger				= Logger.getLogger(Writer.class);
	protected DataOutputStream	dataOutputStream	= null;

	protected Writer() {
		logger.trace("new");
	}

	protected Writer(OutputStream outputStream) {
		logger.trace("new(OutputStream)");
		this.dataOutputStream = new DataOutputStream(outputStream);
	}

	public void setOutputStream(OutputStream outputStream) {
		logger.trace("setOutputStream(OutputStream)");
		this.dataOutputStream = new DataOutputStream(outputStream);
	}

	public abstract int writePacket(Packet packet, Encoding encoding) throws IOException;
}
