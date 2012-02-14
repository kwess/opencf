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

	public void setDataLengt(int dataLengt) {
		this.dataLengt = dataLengt;
	}

	public byte[] getRawData() {
		return rawData;
	}

	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		String ret = new String("Packet\n");
		ret += "++++++++++++++++++++++++++++++++++++++++++++++++++";
		ret += " [dataLength] " + dataLengt;
		ret += " [data]       " + data;
		ret += " [rawData]    " + Arrays.toString(rawData);
		ret += "--------------------------------------------------";
		return ret;
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
