import std.stdio;
import std.file;
import std.conv;
import std.system;
import std.process;
import core.thread;
import std.concurrency;
import std.stream;
import std.xml;
import std.array;
import std.string;

import Configuration;
import Connection;
import AutomationThreadManager;
import AutomationThread;

void main() {
	
	

//	auto s = "<Test>What &amp; Up</Test>";
//	check(s);
//	auto xml = new DocumentParser(s);
//
//	xml.onEndTag["Test"] = (in Element e) {
//		writeln("Elem: ", e.text);
//	};
//	xml.parse();
//	
//	writeln(xml);
//	
//	Element[] elements;
//	elements.insertInPlace(0, new Element("type","1"));
//	elements.insertInPlace(0, new Element("message","2"));
//	string s2;
//	foreach(e; elements) {
//		s2 ~= e.toString();
//	}
//	auto doc = new Document(s2);
//	writeln(doc);
	    
	/* read configuration from agent.cfg */
	Configuration configuration = new Configuration("agent.cfg");
	configuration.printConfiguration();
	
	/* setup socket connection */
	string hostname = configuration.get("hostname");
	ushort port = to!short(configuration.get("port"));
	Connection connection = new Connection(hostname, port);
	connection.connect();
	
	/* send hello packet to management server */
	string agent = configuration.get("agentid");
	string myversion = configuration.get("version");
	string plattform = configuration.get("plattform");
	connection.sendHello(agent, myversion, plattform);
	

//	AutomationThreadManager manager = new AutomationThreadManager();
//	manager.startNewAutomation();

	stdout.writeln("Endlosschleife - hier muss der reconnect rein");
	while(1) {
		Thread.sleep(dur!("seconds")(1));
	}
	stdout.writeln("ende");
}
