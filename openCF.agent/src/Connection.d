import std.socket;
import std.socketstream;
import std.stream;
import std.stdio;
import core.bitop;
import std.json;

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
			return false;
		}
//		auto jsons = [
//			"{agent_id:1}",
//			"{agent_version:1}",
//			"{agent_plattform:windows}"
//        ];

        JSONValue root;
        root.type = JSON_TYPE.OBJECT;
        JSONValue[1] leaves;
        JSONValue agentID;
        agentID.type = JSON_TYPE.STRING;
        agentID.str = "{agent_id:1}";
        leaves[0] = agentID;
        root.array = leaves;
        string message = toJSON(&root.array);
//        writeln(message);
        
//		string message = toJSON(&root);
//        writeln(message);
//		send(message);
		
		return true;
	}
	
	public bool disconnect() {
		this.connected = false;
		socket.close();
		
		return true;
	}
	
	private void send(string message) {
		if(connected == false) {
			return;
		}
		this.stream.write(bswap(message.length));
		this.stream.writeString(message);
	}
}