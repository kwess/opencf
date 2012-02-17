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
		
		bool writingOK;
		this.socketWriter = new SocketWriter(this.stream, thisTid);
		this.socketWriter.start();
		receive(
			(bool b) {
				writingOK = b;
			}
		);
		this.connected = listeningOK && writingOK;
		return this.connected;
	}
	
	public bool sendHello(string agent, string myversion, string plattform) {
		if(connected == false) {
			stdout.writeln("Connection.d, sendHello(): connected == false --> unable to send hello");
			return false;
		}

//		JSONValue json;
//		json.type = JSON_TYPE.OBJECT;
//		json.object[type] = JSONValue();
//		json.object[type].type = JSON_TYPE.INTEGER;
//		json.object[type].integer = type_agenthello;
//		json.object[agent_id] = JSONValue();
//		json.object[agent_id].str = agent;
//		json.object[agent_version] = JSONValue();
//		json.object[agent_version].str = myversion;
//		json.object[agent_plattform] = JSONValue();
//		json.object[agent_plattform].str = plattform;
//		
//		Packet p = new Packet(json);
//		this.socketWriter.send(p);
		
		
			Element[] elements;
			elements.insertInPlace(0, new Element(type,text(type_agenthello)));
			elements.insertInPlace(0, new Element(agent_id,agent));
			elements.insertInPlace(0, new Element(agent_version,myversion));
			elements.insertInPlace(0, new Element(agent_plattform,plattform));
			string s2;
			foreach(e; elements) {
				s2 ~= e.toString();
			}
			auto doc = new Document(s2);
			stdout.writeln("doc:", doc);
			
			Packet p = new Packet(s2);
			this.socketWriter.send(p);
		
		bool helloOK = false;
//		receive(
//			(bool ok) {
//				helloOK = ok;
//			}
//		);
		
		return true;
	}
	
	public bool disconnect() {
		this.connected = false;
		//this.socketListener.
		this.socket.close();
		
		stdout.writeln("socket disconnected");
		
		return true;
	}
	
	public bool isConnected() {
		return this.connected;
	}
}