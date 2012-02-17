package de.openCF.protocol;

import java.util.Arrays;
import java.util.Map;

public class Packet {

	private int					dataLengt	= 0;
	private byte[]				rawData		= null;
	private Map<String, Object>	data		= null;

	public Packet() {
		super();
	}

	public int getDataLengt() {
		return dataLengt;
	}

	public byte[] getRawData() {
		return rawData;
	}

	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
		this.dataLengt = rawData.length;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String dump() {
		StringBuffer dump = new StringBuffer();
		int bytes = 16;
		for (int i = 0; i < dataLengt; i++) {
			if (i % bytes == 0) {
				dump.append('\n');
				dump.append(String.format(" %04d:", (int) (i / bytes)));
			}
			if (i % 2 == 0)
				dump.append(' ');
			dump.append(Integer.toHexString(rawData[i]));
		}
		StringBuffer dataFormatted = new StringBuffer();
		for (Map.Entry<String, Object> e : data.entrySet()) {
			String clazz = e.getValue().getClass().getSimpleName();
			dataFormatted.append(String.format(" %15S = (%7s) %s\n", e.getKey(), clazz, e.getValue()));
		}
		String ret = new String("\n");
		ret += " -----------------Packet----------------------\n";
		ret += " [dataLength]   " + dataLengt + "\n";
		ret += " [dataElements] " + data.size() + "\n";
		ret += " ---------------Parsed Data-------------------\n";
		ret += dataFormatted.toString();
		ret += " ---------------Hex Dump----------------------";
		ret += dump.toString() + "\n";
		ret += " ---------------------------------------------";
		return ret + "\n";
	}

	@Override
	public String toString() {
		return "Packet [dataLengt=" + dataLengt + ", data=" + data + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + dataLengt;
		result = prime * result + Arrays.hashCode(rawData);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Packet other = (Packet) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (dataLengt != other.dataLengt)
			return false;
		if (!Arrays.equals(rawData, other.rawData))
			return false;
		return true;
	}

}
