import std.variant;
import std.json;
import core.bitop;
import std.traits;
import std.conv;

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
	private JSONValue json;
	
	this(JSONValue json) {
		this.json = json;
	}
	
	this(string data) {
		this.json = toJsonValue(data);
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

//TODO DAS HIER IST EINE HILFSMETHODE WEGEN BUG http://d.puremagic.com/issues/show_bug.cgi?id=7323
JSONValue toJsonValue(T)(T a) {
	JSONValue val;
	static if(is(T == JSONValue)) {
		val = a;
	} else static if(__traits(compiles, val = a.makeJsonValue())) {
		val = a.makeJsonValue();
	} else static if(isIntegral!(T)) {
		val.type = JSON_TYPE.INTEGER;
		val.integer = to!long(a);
	} else static if(isFloatingPoint!(T)) {
		val.type = JSON_TYPE.FLOAT;
		val.floating = to!real(a);
		static assert(0);
	} else static if(is(T == void*)) {
		val.type = JSON_TYPE.NULL;
	} else static if(is(T == bool)) {
		if(a == true)
			val.type = JSON_TYPE.TRUE;
		if(a == false)
			val.type = JSON_TYPE.FALSE;
	} else static if(isSomeString!(T)) {
		val.type = JSON_TYPE.STRING;
		val.str = to!string(a);
	} else static if(isAssociativeArray!(T)) {
		val.type = JSON_TYPE.OBJECT;
		foreach(k, v; a) {
			val.object[to!string(k)] = toJsonValue(v);
		}
	} else static if(isArray!(T)) {
		val.type = JSON_TYPE.ARRAY;
		val.array.length = a.length;
		foreach(i, v; a) {
			val.array[i] = toJsonValue(v);
		}
	} else static if(is(T == struct)) {
		val.type = JSON_TYPE.OBJECT;

		foreach(i, member; a.tupleof) {
			string name = a.tupleof[i].stringof[2..$];
			static if(a.tupleof[i].stringof[2] != '_')
				val.object[name] = toJsonValue(member);
		}
	} else { /* our catch all is to just do strings */
		val.type = JSON_TYPE.STRING;
		val.str = to!string(a);
	}

	return val;
}
