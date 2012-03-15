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
		Logger.myInfo("SocketWriter ready to send data", __FILE__, __LINE__);
	}
	
	private void run() {
		Logger.myInfo("SocketWriter starting to heartbeat", __FILE__, __LINE__);
		while(this.isRunning) {
			sendHeartbeat();
			Thread.sleep(dur!("minutes")(1));
		}
		Logger.myInfo("SocketWriter Thread (plus heartbeat) ended", __FILE__, __LINE__);
	}
	
	public bool send(Packet p) {
		string text = p.toString();
		Logger.myDebug("sending " ~ text, __FILE__, __LINE__);
		this.stream.write(p.getSize());
		this.stream.writeString(text);
		
		return true;
	}
	
	private bool sendHeartbeat() {
		JSONValue json;
		json.type = JSON_TYPE.OBJECT;
		json.object[Packet.Keys.TYPE] = JSONValue();
		json.object[Packet.Keys.TYPE].type = JSON_TYPE.INTEGER;
		json.object[Packet.Keys.TYPE].integer = Packet.Type.HEARTBEAT;
		json.object[Packet.Keys.LOCAL_TIME] = JSONValue();
		json.object[Packet.Keys.LOCAL_TIME].type = JSON_TYPE.STRING;
		json.object[Packet.Keys.LOCAL_TIME].str = text(core.stdc.time.time(null));
		Packet p = new Packet(json);
		return send(p);
	}
}	