package de.openCF.protocol;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class PacketReader extends Reader {

	private static Logger	logger			= Logger.getLogger(PacketReader.class);

	public static final int	MAX_PACKET_SIZE	= 2048;

	public PacketReader() {
		super();
		logger.trace("new");
	}

	public PacketReader(InputStream inputStream) {
		super(inputStream);
		logger.trace("new(InputStream)");
	}

	public Packet readPacket() throws IOException {
		logger.trace("readPacket(Encoding)");

		Packet packet = new Packet();

		logger.trace("reading length from stream");
		int length = dataInputStream.readInt();
		if (length > MAX_PACKET_SIZE) {
			throw new IOException("probalbly wrong packet size[" + length + "] read");
		}
		byte[] rawData = new byte[length];

		logger.trace("reading data " + length + "(bytes) form stream");
		dataInputStream.read(rawData, 0, length);

		packet.setRawData(rawData);
		packet.setData(PacketHelper.parseRawData(rawData));

		logger.debug(packet);
		logger.trace(packet.dump());
		logger.trace("readPacket finished");

		return packet;
	}

}
