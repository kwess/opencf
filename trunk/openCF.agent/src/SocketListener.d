import std.stdio;
import std.stream;
import core.thread;
import core.bitop;

import Protocol;

class SocketListener : Thread {
	private Stream stream;
	this(Stream stream) {
		super(&run);
		this.stream = stream;
	}
	
	private void run() {
		stdout.writeln("start listening");
		while (!stream.eof()) {
			int n = 0;
			stream.read(n);
			stdout.writeln(bswap(n), " byte kommen im naechsten block");
			string datastring = cast(string) stream.readString(bswap(n));
			stdout.writeln(datastring, " gelesen");
			//stdout.writeln("data: ", datastring);
//			switch() {
//				case type_agenthelloresponse:
//					stdout.writeln("type_agenthelloresponse bekommen");
//			}
			
		}
		stdout.writeln("stop listening");
	}
}