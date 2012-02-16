import std.stdio;
import std.stream;
import core.thread;
import core.bitop;
import std.concurrency;
import std.json;
import std.datetime;

import Protocol;

class SocketWriter : Thread {
	private Stream stream;
	private Tid connectionTid;
	
	this(Stream stream, Tid tid) {
		super(&run);
		this.stream = stream;
		this.connectionTid = tid;
	}
	
	private void run() {
		stdout.writeln("SocketWriter ready to send data");
		std.concurrency.send(connectionTid, true);
		while(this.isRunning) {
			JSONValue json;
			json.type = JSON_TYPE.OBJECT;
			json.object[type] = JSONValue();
			json.object[type].type = JSON_TYPE.INTEGER;
			json.object[type].integer = type_heartbeat;
			json.object[local_time] = JSONValue();
			json.object[local_time].str = Clock.currTime(UTC()).toString();
			Packet p = new Packet(json);
			send(p);
			
			Thread.sleep(dur!("seconds")(1));
		}
		stdout.writeln("writer ending");
	}
	
	
	
	public bool send(string message) {
		stdout.writefln("sending %s", message);
		this.stream.write(bswap(message.length));
		this.stream.writeString(message);
		
		return true;
	}
	
	public bool send(Packet p) {
		stdout.writefln("sending %s", p.toString());
		this.stream.write(bswap(p.toString().length));
		this.stream.writeString(p.toString());
		
		return true;
	}
}	