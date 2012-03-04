package de.openCF.server.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "automation_action")
@XmlRootElement
public enum AutomationAction {
	start, stop, pause, resume, listen;

	@Id
	private String	action	= toString();

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
