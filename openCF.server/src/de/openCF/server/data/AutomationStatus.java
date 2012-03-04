package de.openCF.server.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public enum AutomationStatus {
	unknown, preparing, prepared, started, paused, resumed, finished, stopped, start_failed, prepare_failed, pause_failed, resume_failed, stopped_failed, talking, timeout;

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
}
