import std.socket;
import std.socketstream;
import std.stream;
import std.stdio;
import core.bitop;
import std.json;

import Protocol;

class Connection {
	private string hostname;
	private ushort port;
	private TcpSocket socket;
	private Stream stream;
	private bool connected;
	
	this(string hostname, ushort port) {
		this.hostname = hostname;
		this.port = port;
		this.connected = false;
	}
	
	public bool connect() {
		this.socket = new TcpSocket(new InternetAddress(this.hostname, this.port));
		this.stream = new SocketStream(socket);
		
		this.connected = true;
		return true;
	}
	
	public bool sendHello(string agent, string myversion, string plattform) {
		if(connected == false) {
			writeln("Connection.d, sendHello(): connected == false --> unable to send hello");
			return false;
		}
		

		JSONValue json;
		json.type = JSON_TYPE.OBJECT;
		json.object[type] = JSONValue();
		json.object[type].type = JSON_TYPE.INTEGER;
		json.object[type].integer = 1;
		json.object[agent_id] = JSONValue();
		json.object[agent_id].str = agent;
		json.object[agent_version] = JSONValue();
		json.object[agent_version].str = myversion;
		json.object[agent_plattform] = JSONValue();
		json.object[agent_plattform].str = plattform;
		string message = toJSON(&json);
		send(message);
		
		return true;
	}
	
	public bool disconnect() {
		this.connected = false;
		socket.close();
		
		return true;
	}
	
	private bool send(string message) {
		if(connected == false) {
			return false;
		}
		writefln("sending %s", message);
		this.stream.write(bswap(message.length));
		this.stream.writeString(message);
		
		return true;
	}
}