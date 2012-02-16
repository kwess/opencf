import std.variant;
import std.json;
import core.bitop;

immutable string agent_id = "agent_id";
immutable string agent_version = "agent_version";
immutable string agent_plattform = "agent_plattform";
immutable string type = "type";

immutable int type_agenthello = 1;
immutable int type_agenthelloresponse = 2;

class Packet {
	private int size;
	private JSONValue json;
	
	this(JSONValue json) {
		this.json = json;
	}
	
	this(string data) {
		//this.json = parseJSON(data);
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public JSONValue getJson() {
		return json;
	}
	
	public string getJsonString() {
		return toJSON(&json);
	}
	
	public string toString() {
		return toJSON(&json);
	}
	
	public int getType() {
		return cast(int) json.object[Protocol.type].integer;
	}
}

