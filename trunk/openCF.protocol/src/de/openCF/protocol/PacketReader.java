package de.openCF.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PacketReader {

	private static Logger	logger			= Logger.getLogger(PacketReader.class);

	private DataInputStream	dataInputStream	= null;

	public PacketReader(InputStream inputStream) {
		super();
		logger.trace("new");
		this.dataInputStream = new DataInputStream(inputStream);
	}

	public Packet readPacket() throws IOException {
		logger.trace("readPacket");

		Packet packet = new Packet();

		packet.setDataLengt(dataInputStream.readInt());
		if (packet.getDataLengt() > 1024) {
			throw new IOException("probalbly wrong packet size[" + packet.getDataLengt() + "] read");
		}
		byte[] rawData = new byte[packet.getDataLengt()];
		dataInputStream.read(rawData, 0, packet.getDataLengt());
		packet.setRawData(rawData);
		packet.setData(parseRawData(rawData));

		logger.debug(packet);
		logger.trace(packet.dump());
		logger.trace("readPacket finished");

		return packet;
	}

	protected Map<String, Object> parseRawData(byte[] rawData) throws IOException {
		logger.trace("parseRawData");

		Map<String, Object> map = new HashMap<String, Object>();
		String data = new String(rawData, Charset.forName("utf8"));

		JSONTokener jsonTokener = new JSONTokener(data);
		try {
			JSONObject jsonObject = new JSONObject(jsonTokener);
			for (String id : JSONObject.getNames(jsonObject)) {
				map.put(id, jsonObject.get(id));
			}
		} catch (JSONException e) {
			throw new IOException(e.getMessage());
		}

		logger.trace("parseRawDataFinished");

		return map;
	}

}
