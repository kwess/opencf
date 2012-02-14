package de.openCF.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONObject;

public class PacketWriter {

	private DataOutputStream	dataOutputStream	= null;

	public PacketWriter(OutputStream outputStream) {
		this.dataOutputStream = new DataOutputStream(outputStream);
	}

	public int writePacket(Packet packet) throws IOException {
		int sumBytes = 0;

		generateRawData(packet);

		dataOutputStream.writeInt(packet.getDataLengt());
		dataOutputStream.write(packet.getRawData());

		return sumBytes;
	}

	protected void generateRawData(Packet packet) {
		JSONObject jsonObject = new JSONObject(packet.getData());

		byte[] rawData = jsonObject.toString().getBytes();
		packet.setRawData(rawData);
		packet.setDataLengt(rawData.length);
	}

}
