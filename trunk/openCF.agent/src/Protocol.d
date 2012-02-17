import std.variant;
import std.json;
import core.bitop;
import std.traits;
import std.conv;
import std.stdio;
import std.xml;
import std.string;

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
	private string xmlString;
	private JSONValue json;
	
	this(JSONValue json) {
		this.json = json;
	}
	
	this(string xmlString) {
		this.xmlString = text("<dummy>", xmlString, "</dummy>");
	}
	
	public int getSize() {
		return bswap(toString().length);
	}
	
	public JSONValue getJson() {
		return json;
	}
	
	public string getJsonString() {
		return toJSON(&json);
	}
	
	public string getXmlString() {
		return xmlString;
	}
	
	public string toString() {
		string text;
		if(xmlString != null && xmlString.length > 0) {
			return getXmlString();
		}
		else {
			return getJsonString();
		}
	}
	
	public int getType() {
		
		if(xmlString != null && xmlString.length > 0) {
			auto doc = new Document(xmlString);
			foreach(element; doc.elements) {
				if(element.tag.name.icmp(type) == 0) {
					string typeString = element.text;
					return parse!int(typeString);
				}
			}
		}
		else {
			return cast(int) json.object[type].integer;
		}
		return -1;
	}
}
