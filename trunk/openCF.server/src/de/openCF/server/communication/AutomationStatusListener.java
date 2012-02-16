package de.openCF.server.communication;

import de.openCF.server.data.AutomationStatus;

public interface AutomationStatusListener {

	public void statusChanged(Integer id, AutomationStatus status, String Message);

}
