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

public abstract class PacketHelper {
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

	public static byte[] generateRawData(Map<String, Object> data) {
		JSONObject jsonObject = new JSONObject(data);

		byte[] rawData = jsonObject.toString().getBytes();

		return rawData;
	}

	public static Map<String, Object> parseRawData(byte[] rawData) throws IOException {
		Map<String, Object> map = null;
		String data = new String(rawData, Charset.forName("utf8"));

		JSONTokener jsonTokener = new JSONTokener(data);
		try {
			map = PacketHelper.toMap(new JSONObject(jsonTokener));
		} catch (JSONException e) {
			throw new IOException(e.getMessage());
		}

		return map;
	}
}
