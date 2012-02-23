import core.thread;
import std.array;
import std.concurrency;

import AutomationThread;

class AutomationThreadManager {
	
	private synchronized AutomationThread[int] automationThreads = null;
	
	
	
	public int[] getRunningThreadIDs() {
		return this.automationThreads.keys;
	}
	
	public bool startNewAutomation() {
		AutomationThread t = new AutomationThread(thisTid(), automationThreads.length);
		automationThreads[t.getAutomationID()] = t;
		t.start();
		
		return true;
	}
}