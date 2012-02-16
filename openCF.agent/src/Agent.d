import std.stdio;
import std.file;
import std.conv;
import std.system;
import std.process;
import core.thread;
import std.concurrency;
import std.stream;

import Configuration;
import Connection;
import AutomationThreadManager;
import AutomationThread;

void main() {
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

	while(1) {
		Thread.sleep(dur!("seconds")(1));
		stdout.writeln("Endlosschleife - hier muss der reconnect rein");
	}
	stdout.writeln("ende");
}
