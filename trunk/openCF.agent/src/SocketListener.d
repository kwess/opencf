import std.stdio;
import std.stream;
import core.thread;
import core.bitop;
import std.concurrency;
import std.xml;
import std.conv;
import std.string;

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
			stdout.writeln("gelesen: ", datastring);
			Packet p = new Packet(datastring);
			
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
		stdout.writeln("handleAgentHelloResponse function");
		bool success;
		auto doc = new Document(p.getXmlString());
			foreach(element; doc.elements) {
				if(element.tag.name.icmp(successfull) == 0) {
					string successfullString = element.text;
					success = parse!bool(successfullString);
				}
			}
		send(connectionTid, success);
	}
}