package de.openCF.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PacketReader {

	private DataInputStream	dataInputStream	= null;

	public PacketReader(InputStream inputStream) {
		super();
		this.dataInputStream = new DataInputStream(inputStream);
	}

	public Packet readPacket() throws IOException {
		Packet packet = new Packet();

		packet.setDataLengt(dataInputStream.readInt());
		if (packet.getDataLengt() > 1024) {
			throw new IOException("probalbly wrong packet size[" + packet.getDataLengt() + "] read");
		}
		byte[] rawData = new byte[packet.getDataLengt()];
		dataInputStream.read(rawData, 0, packet.getDataLengt());
		packet.setRawData(rawData);
		packet.setData(parseRawData(rawData));

		return packet;
	}

	protected Map<String, Object> parseRawData(byte[] rawData) throws IOException {
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

		return map;
	}

}
