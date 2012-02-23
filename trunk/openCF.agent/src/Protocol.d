import std.variant;
import std.json;
import core.bitop;
import std.traits;
import std.conv;
import std.stdio;
import std.xml;
import std.string;

class Packet {
	
	public enum Type : int {
		INVALID					= -1,
		HEARTBEAT				= 0,
		AGENT_HELLO				= 1,
		AGENT_HELLO_RESPONSE	= 2,
		AUTOMATION_CONTROL		= 13,
		AUTOMATION_STATUS		= 20
	}
	
	public enum Keys : string {
		AGENT_ID = "agent_id",
		AGENT_VERSION = "agent_version",
		AGENT_PLATTFORM = "agent_plattform",
		TYPE = "type",
		SUCCESSFULL = "successfull",
		RETURN_CODE = "return_code",
		MESSAGE = "message",
		LOCAL_TIME = "local_time"
	}
	
	private string xmlString;
	private JSONValue json;
	
	this(JSONValue json) {
		this.json = json;
	}
	
	this(string xmlString) {
		// enclose the xml-text in a dummy-tag, otherwise an xml exception will occur
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
				if(element.tag.name.icmp(Keys.TYPE) == 0) {
					string typeString = element.text;
					return parse!int(typeString);
				}
			}
		}
		else {
			return cast(int) json.object[Keys.TYPE].integer;
		}
		return Type.INVALID;
	}
}
