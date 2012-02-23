import core.thread;

import AutomationThread;

class AutomationThreadManager : ThreadGroup {
	
	
	public void getRunningThreads() {
		
	}
	
	public bool startNewAutomation() {
		Thread t = new AutomationThread();
		add(t);
		
		t.start();
		
		return true;
	}
}