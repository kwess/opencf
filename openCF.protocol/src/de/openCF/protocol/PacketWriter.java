package de.openCF.protocol;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import de.openCF.protocol.PacketHelper.Encoding;

public class PacketWriter extends Writer {

	private static Logger	logger	= Logger.getLogger(PacketWriter.class);

	public PacketWriter() {
		super();
		logger.trace("new");
	}

	public PacketWriter(OutputStream outputStream) {
		super(outputStream);
		logger.trace("new(OutputStream)");
	}

	public synchronized int writePacket(Packet packet, Encoding encoding) throws IOException {
		logger.trace("writePacket(Packet, Encoding)");

		logger.trace("generating raw data");
		packet.setRawData(PacketHelper.generateRawData(packet.getData(), encoding));

		int sumBytes = packet.getDataLengt();

		logger.trace("writing length to stream");
		dataOutputStream.writeInt(packet.getDataLengt());
		logger.trace("writing data to stream");
		dataOutputStream.write(packet.getRawData());

		sumBytes += 4;

		logger.debug(packet);
		logger.trace(packet.dump());
		logger.trace("writePacket finished");
		logger.debug(sumBytes + " (bytes) written");

		logger.trace("flushing stream");
		dataOutputStream.flush();

		return sumBytes;
	}

	public synchronized int writePacket(Packet packet) throws IOException {
		logger.trace("writePacket(Packet)");
		return writePacket(packet, Encoding.JSON);
	}

}
