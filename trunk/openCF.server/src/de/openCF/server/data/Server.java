package de.openCF.server.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "Server")
@XmlRootElement
public class Server {

	@Id
	private String	id				= null;
	private String	hostname		= null;
	private Integer	agentPort		= null;
	private Integer	controllerPort	= null;

	public Server() {
		super();
	}

	public Server(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Integer getAgentPort() {
		return agentPort;
	}

	public void setAgentPort(Integer agentPort) {
		this.agentPort = agentPort;
	}

	public Integer getControllerPort() {
		return controllerPort;
	}

	public void setControllerPort(Integer controllerPort) {
		this.controllerPort = controllerPort;
	}

	@Override
	public String toString() {
		return "Server [id=" + id + ", hostname=" + hostname + ", agentPort=" + agentPort + ", controllerPort=" + controllerPort + "]";
	}

}
