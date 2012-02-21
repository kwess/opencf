import std.stdio;
import std.conv;
import std.stream;
import core.thread;
import core.bitop;
import std.concurrency;
import std.json;
import std.datetime;

import Protocol;
import util.Logger;

class SocketWriter : Thread {
	private Stream stream;
	private Tid connectionTid;
	
	this(Stream stream, Tid tid) {
		super(&run);
		this.stream = stream;
		this.connectionTid = tid;
		Logger.myInfo(__FILE__ ~ __LINE__ ~ ": SocketWriter ready to send data");
	}
	
	private void run() {
		Logger.myInfo(__FILE__ ~ __LINE__ ~ ": SocketWriter starting to heartbeat");
		while(this.isRunning) {
			JSONValue json;
			json.type = JSON_TYPE.OBJECT;
			json.object[type] = JSONValue();
			json.object[type].type = JSON_TYPE.INTEGER;
			json.object[type].integer = type_heartbeat;
			json.object[local_time] = JSONValue();
			json.object[local_time].type = JSON_TYPE.STRING;
			json.object[local_time].str = text(core.stdc.time.time(null));
			Packet p = new Packet(json);
			send(p);
			
			Thread.sleep(dur!("seconds")(1));
		}
		Logger.myInfo(__FILE__ ~ __LINE__ ~ ": SocketWriter Thread (plus heartbeat) ended");
	}
	
	public bool send(Packet p) {
		string text = p.toString();
		Logger.myDebug(__FILE__ ~ __LINE__ ~ ": sending " ~ text);
		this.stream.write(p.getSize());
		this.stream.writeString(text);
		
		return true;
	}
}	