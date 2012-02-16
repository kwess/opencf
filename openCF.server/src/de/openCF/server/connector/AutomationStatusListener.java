package de.openCF.server.connector;

import de.openCF.server.data.AutomationStatus;

public interface AutomationStatusListener {

	public void statusChanged(Integer id, AutomationStatus status);

}
