import std.stdio;
import std.stream;
import core.thread;
import core.bitop;
import std.concurrency;
import std.xml;
import std.conv;
import std.string;

import Protocol;
import util.Logger;

class SocketListener : Thread {
	private Stream stream;
	private Tid connectionTid;
	
	this(Stream stream, Tid tid) {
		super(&run);
		this.stream = stream;
		this.connectionTid = tid;
	}
	
	private void run() {
		Logger.myInfo("SocketListener ready to receive data", __FILE__, __LINE__);
		send(connectionTid, true);
		while (!stream.eof()) {
			int n = 0;
			stream.read(n);
			Logger.myDebug(text(bswap(n)) ~ " byte kommen im naechsten block", __FILE__, __LINE__);
			string datastring = cast(string) stream.readString(bswap(n));
			Logger.myDebug("gelesen: " ~ datastring, __FILE__, __LINE__);
			Packet p = new Packet(datastring);
			
			switch(p.getType()) {
				case Packet.Type.AGENT_HELLO_RESPONSE:
					handleAgentHelloResponse(p);
					break;
				case Packet.Type.AUTOMATION_CONTROL:
					handleAutomationControl(p);
					break;
				default:
					Logger.myInfo("invalid packet type received: " ~ text(p.getType()), __FILE__, __LINE__);
			}
		}
		Logger.myInfo("SocketListener thread ended", __FILE__, __LINE__);
	}
	
	private void handleAgentHelloResponse(Packet p) {
		Logger.myDebug("handleAgentHelloResponse function", __FILE__, __LINE__);
		bool success;
		auto doc = new Document(p.getXmlString());
		foreach(element; doc.elements) {
			if(element.tag.name.icmp(Packet.Keys.SUCCESSFULL) == 0) {
				string successfullString = element.text;
				success = parse!bool(successfullString);
			}
		}
		send(connectionTid, success);
	}
	
	private void handleAutomationControl(Packet p) {
		//TODO
		Logger.myDebug("handleAutomationControl function", __FILE__, __LINE__);
		auto doc = new Document(p.getXmlString());
		foreach(element; doc.elements) {
			Logger.myDebug(element.tag.name);
		}
	}
}