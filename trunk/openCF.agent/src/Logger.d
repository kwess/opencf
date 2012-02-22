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
		stdout.writeln("new logger");
	}
	
public static Logger getInstance() {
  if(loggerInsance is null) {
   loggerInsance = new Logger();
   
  }
  return loggerInsance;
 }
	
	public static void myInfo(string message) {
		stdout.writeln("-> info");
		if(getInstance().level >= Level.INFO) {
			stdout.writeln("INFO  " ~ message);
		}
		stdout.writeln("<- info");
	}
	
	public static void myDebug(string message) {
		stdout.writeln("-> debug");
		if(getInstance().level >= Level.DEBUG) { 
			stdout.writeln("DEBUG " ~ message);
		}
		stdout.writeln("<- debug");
	}
	
	public static void myError(string message) {
		stdout.writeln("error");
		stderr.writeln("ERROR " ~ message); 
	}
}