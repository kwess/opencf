module util.Logger;

import std.string;
import std.stdio;
import std.conv;

/**
 *  Logger class. Implemented because D does not contain one at the moment.
 *  Implemented as Singleton.
 */
class Logger {
	
	private static Logger loggerInsance; 
	public static shared int level;
	
	enum Level { 
		DEBUG=1,
		INFO=2,
		ERROR=3
	}
	
	
	shared static this() {
//		stdout.writeln("new logger");
	}
	
	public static Logger getInstance() {
	  if(loggerInsance is null) {
	   loggerInsance = new Logger();
	   
	  }
	  return loggerInsance;
	 }
	
	public static void myInfo(string message) {
		stdout.writeln("---> info");
		if(getInstance().level <= Level.INFO) {
			stdout.writeln("<INFO>  " ~ message);
		}
		stdout.writeln("<--- info");
	}
	
	public static void myDebug(string message) {
		stdout.writeln("---> debug");
		if(getInstance().level == Level.DEBUG) { 
			stdout.writeln("<DEBUG> " ~ message);
		}
		stdout.writeln("<--- debug");
	}
	
	
	
	public static void myInfo(string message, string srcFile, int line) {
		stdout.writeln("---> info");
		if(getInstance().level <= Level.INFO) {
			stdout.writefln("<INFO>  %s, line %s: %s", srcFile, text(line), message);
		}
		stdout.writeln("<--- info");
	}
	
	public static void myDebug(string message, string srcFile, int line) {
		stdout.writeln("---> debug");
		if(getInstance().level == Level.DEBUG) { 
			stdout.writefln("<DEBUG>  %s, line %s: %s", srcFile, text(line), message);
		}
		stdout.writeln("<--- debug");
	}
	
	public static void myError(string message) {
		stdout.writeln("---> error");
		if(getInstance().level <= Level.ERROR) { 
			stderr.writeln("<ERROR> " ~ message);
		}
		stdout.writeln("<--- error");
	}
	
	public static void myError(string message, string srcFile, int line) {
		stdout.writeln("---> error");
		if(getInstance().level <= Level.ERROR) { 
			stderr.writefln("<ERROR>  %s, line %s: %s", srcFile, text(line), message);
		}
		stdout.writeln("<--- error");
	}
}