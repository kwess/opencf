import std.stdio;
import std.stream;
import core.thread;
import core.bitop;
import std.concurrency;
import std.xml;
import std.conv;

import Protocol;

class SocketListener : Thread {
	private Stream stream;
	private Tid connectionTid;
	
	this(Stream stream, Tid tid) {
		super(&run);
		this.stream = stream;
		this.connectionTid = tid;
	}
	
	private void run() {
		stdout.writeln("SocketListener ready to receive data");
		send(connectionTid, true);
		while (!stream.eof()) {
			int n = 0;
			stream.read(n);
			stdout.writeln(bswap(n), " byte kommen im naechsten block");
			string datastring = cast(string) stream.readString(bswap(n));
			stdout.writeln(datastring, " gelesen");
			auto xml = new DocumentParser(text("<a>", datastring, "</a>"));
			Packet p = new Packet(xml);
			
			switch(p.getType()) {
				case type_agenthelloresponse:
					handleAgentHelloResponse(connectionTid, p);
					break;
				default:
					
			}
		}
		stdout.writeln("stop listening");
	}
	
	private void handleAgentHelloResponse(Tid connectionTid, Packet p) {
		stdout.writeln("handleAgentHelloResponse function");//, p.getJson.object[successfull]);
//		//send(connectionTid, p.getJson.object[successfull]);
//		bool success = p.getJson.object[successfull];
//		send(connectionTid, true);
	}
}