import std.stdio;
import std.file;
import std.conv;

import Configuration;
import Connection;

void main(string[] args) {
	Configuration configuration = new Configuration("agent.cfg");
	
	configuration.printConfiguration();
	
	string hostname = configuration.get("hostname");
	ushort port = to!short(configuration.get("port"));
	Connection connection = new Connection(hostname, port);
	connection.connect();
	string agent = configuration.get("agentid");
	string myversion = configuration.get("version");
	string plattform = configuration.get("plattform");
	connection.sendHello(agent, myversion, plattform);
	connection.disconnect();
} 