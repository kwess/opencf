import std.stdio;
import std.stream;
import core.thread;
import core.bitop;

class SocketListener : Thread {
	private Stream stream;
	this(Stream stream) {
		super(&run);
		this.stream = stream;
	}
	
	private void run() {
		stdout.writeln("start listening");
		while (!stream.eof()) {
			stdout.writeln("stream.eof() == false");
			int n = 0;
			stream.read(n);
			stdout.writeln("was bekommen: ", bswap(n));
		}
		stdout.writeln("stop listening");
	}
}