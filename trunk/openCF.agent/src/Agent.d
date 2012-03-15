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
//	Logger.myDebug("agent.cfg configuration:\n" ~ configuration.toString());
	string hostname = configuration.get("hostname");
	ushort port = to!short(configuration.get("port"));
	string agent = configuration.get("agentid");
	string myversion = configuration.get("version");
	string plattform = configuration.get("plattform");
	
	while(1) {
		
		/* setup socket connection */
		Connection connection = new Connection(hostname, port);
		
		if( connection.connect() ) {
			/* send hello packet to management server */
			bool helloAccepted = connection.sendHello(agent, myversion, plattform);
		
			if(helloAccepted) {
				AutomationThreadManager manager = new AutomationThreadManager();
				
				
				receive(
					(bool b) {
						Logger.myInfo("bla");
					}
				);
				Logger.myInfo("blub");
			}
			else {
				Logger.myDebug("sendHello fehlgeschlagen", __FILE__, __LINE__);
			}
		}
		
		Logger.myDebug("keine Connection zum Server, retry in 10 Sekunden", __FILE__, __LINE__);
		Thread.sleep(dur!("seconds")(10));
	}
	Logger.myDebug("openCFAgent beendet", __FILE__, __LINE__);
}
