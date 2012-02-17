import std.stdio;
import std.conv;
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
		std.concurrency.receive(
			(bool go){
				// when this message is received, the heartbeat thread can start running
			}
		);
		while(this.isRunning) {
			//TODO hier warten über events
//			Thread.sleep(dur!("seconds")(5));
//			JSONValue json;
//			json.type = JSON_TYPE.OBJECT;
//			json.object[type] = JSONValue();
//			json.object[type].type = JSON_TYPE.INTEGER;
//			json.object[type].integer = type_heartbeat;
//			json.object[local_time] = JSONValue();
//			json.object[local_time].type = JSON_TYPE.STRING;
//			json.object[local_time].str = text(core.stdc.time.time(null));
//			Packet p = new Packet(json);
//			send(p);
			
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
		string text = p.getJsonString();
		stdout.writefln("sending %s", text);
		this.stream.write(bswap(text.length));
		this.stream.writeString(text);
		
		return true;
	}
}	