import core.thread;
import std.stdio;
import std.process;
import std.concurrency;
import std.string;
import std.utf;

class AutomationThread : Thread {
	
	this() {
		super(&run);
	}
	
	private void run() {
		string result;
		try {
			result = shell("ping 192.168.1.101");
		} catch(UTFException e) {
			writefln("wasn hier los mit utf8? %s", e.toString);
		}
		writefln("ich bin Automationthread mit pid %d; result war %s", getpid(), result);
		

	} 
} 