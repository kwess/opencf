import core.thread;

import AutomationThread;

class AutomationThreadManager : ThreadGroup {
	
	
	public bool startNewAutomation() {
		Thread t = new AutomationThread();
		add(t);
		
		t.start;
		
		return true;
	}
}