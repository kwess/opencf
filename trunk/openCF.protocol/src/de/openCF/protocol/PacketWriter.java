package de.openCF.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.json.JSONObject;

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

		int sumBytes = generateRawData(packet);

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

	protected int generateRawData(Packet packet) {
		logger.trace("generateRawData");

		JSONObject jsonObject = new JSONObject(packet.getData());

		byte[] rawData = jsonObject.toString().getBytes();
		packet.setRawData(rawData);
		packet.setDataLengt(rawData.length);

		logger.trace("generateRawData finished");

		return rawData.length;
	}

}
