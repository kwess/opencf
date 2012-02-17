import std.variant;
import std.json;
import core.bitop;
import std.traits;
import std.conv;
import std.stdio;
import std.xml;

immutable string agent_id = "agent_id";
immutable string agent_version = "agent_version";
immutable string agent_plattform = "agent_plattform";
immutable string type = "type";
immutable string successfull = "successfull";
immutable string return_code = "return_code";
immutable string message = "message";
immutable string local_time = "local_time";

immutable int type_heartbeat = 0;
immutable int type_agenthello = 1;
immutable int type_agenthelloresponse = 2;

class Packet {
	private int size;
	private string data;
	private DocumentParser xml;
	private JSONValue json;
	
	this(JSONValue json) {
		this.json = json;
	}
	
	this(DocumentParser xml) {
		this.xml = xml;
	}
	
	this(string data) {
//		this.json = parseJSON(data);
//		this.xml = new DocumentParser(data);
		this.data = data;
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
	
	public string getXMLString() {
		return xml.toString();
	}
	
	public string toString() {
//		return toJSON(&json);
		return this.data;
	}
	
	public int getType() {
		int type;
		xml.onEndTag["type"] = (in Element e) {
			string text = e.text;
			type = parse!int(text);
		};
		xml.parse();
		return type;
	}
}
