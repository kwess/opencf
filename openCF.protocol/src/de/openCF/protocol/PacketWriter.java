package de.openCF.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class PacketWriter {

	private static Logger		logger				= Logger.getLogger(PacketWriter.class);

	private DataOutputStream	dataOutputStream	= null;

	public PacketWriter(OutputStream outputStream) {
		super();
		logger.trace("new");
		this.dataOutputStream = new DataOutputStream(outputStream);
	}

	public int writePacket(Packet packet) throws IOException {
		logger.trace("writePacket");

		int sumBytes = packet.getRawData().length;

		dataOutputStream.writeInt(packet.getDataLengt());
		dataOutputStream.write(packet.getRawData());

		sumBytes += 4;

		logger.debug(packet);
		logger.trace(packet.dump());
		logger.trace("writePacket finished");
		logger.debug(sumBytes + " (bytes) written");

		dataOutputStream.flush();

		return sumBytes;
	}

}
