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
import std.algorithm;

import Configuration;
import Connection;
import AutomationThreadManager;
import AutomationThread;

void main() {
	
//			string datastring = "<a><message>congratulations, youre registered!</message><type>2</type><return_code>0</return_code><successfull>true</successfull><ebene1><ebene2a>2a</ebene2a><ebene2b>2b</ebene2b></ebene1></a>";
//			auto xml = new DocumentParser(datastring);
//			auto doc = new Document(datastring);
//			check(datastring);
	
//			xml.onEndTag["a"] = (in Element e) {
//				stdout.writeln(e.texts.length, " is length");
//				string text = e.text;
//			};
//			xml.onEndTag["type"] = (in Element e) {
//				writeln("Elem: ", e.text);
//			};
//			
//			xml.parse();
//			stdout.writeln(xml);
//			
//			string[string] elementArray;
//			foreach(element; doc.elements) {
//				string key = element.tag.name;
//				string value;
//				if(element.elements.length == 0) {
//					value = element.text;
//				}
//				else {
//					foreach(element2; element.elements) {
//						value ~= element2.tag.name;
//						value ~= element2.text;
//					}
//				}
//				stdout.writeln("key: ", key);
//				stdout.writeln("value: ", value);
//			}
//			
//			return;

//	auto s = "<a><Test>What &amp; Up</Test><bla>j</bla></a>";
//	check(s);
//	auto xml = new DocumentParser(s);
//
//	xml.onEndTag["Test"] = (in Element e) {
//		writeln("Elem: ", e.text);
//	};
//	xml.onEndTag["bla"] = (in Element e) {
//		writeln("blaElem: ", e.text);
//	};
//	xml.parse();
//	
//	writeln(xml);
//	return;
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
