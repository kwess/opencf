import core.thread;
import std.stdio;
import std.process;
import std.concurrency;
import std.string;
import std.utf;
import std.conv;

import util.Logger;

class AutomationThread : Thread {
	
	private Tid managerTid;
	private int automationID;
	
	this(Tid tid, int automationID) {
		super(&run);
		this.managerTid = tid;
		this.automationID = automationID;
	}
	
	private void run() {
		Logger.myInfo("AutomationThread startet, pid " ~ text(getpid()), __FILE__, __LINE__);
		string result;
		try {
			result = shell("hostname");
		} catch(UTFException e) {
			Logger.myError("wasn hier los mit utf8? " ~ e.toString(), __FILE__, __LINE__);
			result = e.toString();
		}
		Logger.myInfo("ich bin Automationthread mit pid " ~ text(getpid()) ~ "; result war " ~ result, __FILE__, __LINE__);
		

	} 
	
	public int getAutomationID() {
		return this.automationID;
	}
} 