package de.openCF.server.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public enum AutomationAction {
	start, stop, pause, resume, listen
}
