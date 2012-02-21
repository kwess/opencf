import std.socket;
import std.socketstream;
import std.stream;
import std.stdio;
import core.bitop;
import core.thread;
import std.concurrency;
import std.json;
import std.xml;
import std.array;
import std.string;
import std.conv;

import Protocol;
import SocketListener;
import SocketWriter;
import util.Logger;

class Connection {
	private string hostname;
	private ushort port;
	private static Stream stream;
	private bool connected;
	private Socket socket;
	private Thread socketListener;
	private SocketWriter socketWriter;
	
	this(string hostname, ushort port) {
		this.hostname = hostname;
		this.port = port;
		this.connected = false;
	}
	
	public bool connect() {
		this.socket = new TcpSocket(new InternetAddress(this.hostname, this.port));
		this.stream = new SocketStream(socket);
		
		this.socketListener = new SocketListener(this.stream, thisTid);
		this.socketListener.start();
		bool listeningOK;
		receive(
			(bool b) {
				listeningOK = b;
			}
		);
		
		this.socketWriter = new SocketWriter(this.stream, thisTid);
		
		this.connected = listeningOK;
		return this.connected;
	}
	
	public bool sendHello(string agent, string myversion, string plattform) {
		if(connected == false) {
			Logger.myError(__FILE__ ~ __LINE__ ~ ": connected == false --> unable to send hello");
			return false;
		}

		JSONValue json;
		json.type = JSON_TYPE.OBJECT;
		json.object[type] = JSONValue();
		json.object[type].type = JSON_TYPE.INTEGER;
		json.object[type].integer = type_agenthello;
		json.object[agent_id] = JSONValue();
		json.object[agent_id].str = agent;
		json.object[agent_version] = JSONValue();
		json.object[agent_version].str = myversion;
		json.object[agent_plattform] = JSONValue();
		json.object[agent_plattform].str = plattform;
		
		Packet p = new Packet(json);
		this.socketWriter.send(p);
		
		bool helloOK = false;
		receive(
			(bool ok) {
				helloOK = ok;
			}
		);
		
		if(helloOK) {
			this.socketWriter.start();
		}
		
		return helloOK;
	}
	
	public bool disconnect() {
		this.connected = false;
		//this.socketListener.
		this.socket.close();
		
		Logger.myInfo(__FILE__ ~ __LINE__ ~ "socket disconnected");
		
		return true;
	}
	
	public bool isConnected() {
		return this.connected;
	}
}