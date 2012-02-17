package de.openCF.protocol;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;
import org.json.XMLTokener;

public abstract class PacketHelper {

	public enum Encoding {
		JSON, XML
	}

	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();

		for (String id : JSONObject.getNames(object)) {
			Object o = object.get(id);
			if (o instanceof JSONObject) {
				map.put(id, toMap((JSONObject) o));
			} else if (o instanceof JSONArray) {
				map.put(id, toList((JSONArray) o));
			} else {
				map.put(id, o);
			}
		}

		return map;
	}

	public static List<Object> toList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();

		for (int i = 0; i < array.length(); i++) {
			Object o = array.get(i);
			if (o instanceof JSONObject) {
				list.add(toMap((JSONObject) o));
			} else if (o instanceof JSONArray) {
				list.add(toList((JSONArray) o));
			} else {
				list.add(o);
			}
		}

		return list;
	}

	public static byte[] generateRawData(Map<String, Object> data, Encoding encoding) throws IOException {
		JSONObject jsonObject = new JSONObject(data);

		String dataString = null;

		switch (encoding) {
			case XML:
				try {
					dataString = XML.toString(jsonObject);
				} catch (JSONException e) {
					throw new IOException("failed transforming json to xml", e);
				}
				break;
			case JSON:
			default:
				dataString = jsonObject.toString();
				break;
		}

		byte[] rawData = dataString.getBytes();

		return rawData;
	}

	public static Map<String, Object> parseRawData(byte[] rawData, Encoding encoding) throws IOException {
		Map<String, Object> map = null;
		String data = new String(rawData, Charset.forName("utf8"));

		JSONTokener jsonTokener = null;
		switch (encoding) {
			case XML:
				jsonTokener = new XMLTokener(data);
				break;
			case JSON:
			default:
				jsonTokener = new JSONTokener(data);
				break;
		}

		System.err.println(encoding);
		System.err.println(jsonTokener);

		try {
			map = PacketHelper.toMap(new JSONObject(jsonTokener));
		} catch (JSONException e) {
			throw new IOException(e.getMessage());
		}

		return map;
	}
}
