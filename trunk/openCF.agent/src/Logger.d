module util.Logger;

import std.string;
import std.stdio;

/**
 *  Logger class. Implemented because D does not contain one at the moment.
 *  Implemented as Singleton.
 */
class Logger {
	
	private static Logger loggerInsance; 
	public static int level = 0;
	
	enum Level { 
		DEBUG=1,
		INFO=2,
		ERROR=3
	}
	
	
	private this() {
		
	}
	
	private static Logger getInstance() {
		if(loggerInsance is null) {
			loggerInsance = new Logger();
		}
		return loggerInsance;
	}
	
	public static void myInfo(string message) {
		if(getInstance().level >= Level.INFO) {
			stdout.writeln(message);
		}
	}
	
	public static void myDebug(string message) {
		if(getInstance().level >= Level.DEBUG) {
			stdout.writeln(message);
		}
	}
	
	public static void myError(string message) {
		stderr.writeln(message);
	}
}