package de.openCF.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class PacketReader {

	private static Logger	logger			= Logger.getLogger(PacketReader.class);

	private DataInputStream	dataInputStream	= null;

	public PacketReader(InputStream inputStream) {
		super();
		logger.trace("new");
		this.dataInputStream = new DataInputStream(inputStream);
	}

	public Packet readPacket() throws IOException {
		logger.trace("readPacket(Encoding)");

		Packet packet = new Packet();

		int length = dataInputStream.readInt();
		if (length > 1024) {
			throw new IOException("probalbly wrong packet size[" + length + "] read");
		}
		byte[] rawData = new byte[length];
		dataInputStream.read(rawData, 0, length);
		packet.setRawData(rawData);
		packet.setData(PacketHelper.parseRawData(rawData));

		logger.debug(packet);
		logger.trace(packet.dump());
		logger.trace("readPacket finished");

		return packet;
	}

}
