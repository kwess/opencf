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
import util.Logger;

void main() {
	/* setup logging level */
	Logger.level = Logger.Level.DEBUG;
	    
	/* read configuration from agent.cfg */
	Configuration configuration = new Configuration("agent.cfg");
	Logger.myDebug("agent.cfg configuration:\n" ~ configuration.toString());
	
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

	Logger.myDebug("Endlosschleife - hier muss der reconnect rein");
	while(1) {
		Thread.sleep(dur!("seconds")(1));
	}
	Logger.myDebug("main zuende");
}
