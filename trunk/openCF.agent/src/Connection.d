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
		json.object[Packet.Keys.TYPE] = JSONValue();
		json.object[Packet.Keys.TYPE].type = JSON_TYPE.INTEGER;
		json.object[Packet.Keys.TYPE].integer = Packet.Type.AGENT_HELLO;
		json.object[Packet.Keys.AGENT_ID] = JSONValue();
		json.object[Packet.Keys.AGENT_ID].str = agent;
		json.object[Packet.Keys.AGENT_VERSION] = JSONValue();
		json.object[Packet.Keys.AGENT_VERSION].str = myversion;
		json.object[Packet.Keys.AGENT_PLATTFORM] = JSONValue();
		json.object[Packet.Keys.AGENT_PLATTFORM].str = plattform;
		
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