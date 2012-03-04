package de.openCF.server.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "automation_status")
@XmlRootElement
public enum AutomationStatus {
	unknown, preparing, prepared, started, paused, resumed, finished, stopped, start_failed, prepare_failed, pause_failed, resume_failed, stopped_failed, talking, timeout;

	@Id
	private String	status	= toString();

	public static boolean isEndState(AutomationStatus status) {
		switch (status) {
			case finished:
			case stopped:
			case start_failed:
			case prepare_failed:
			case timeout:
				return true;
			default:
				return false;
		}
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
