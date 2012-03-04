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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agentPort == null) ? 0 : agentPort.hashCode());
		result = prime * result + ((controllerPort == null) ? 0 : controllerPort.hashCode());
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Server other = (Server) obj;
		if (agentPort == null) {
			if (other.agentPort != null)
				return false;
		} else if (!agentPort.equals(other.agentPort))
			return false;
		if (controllerPort == null) {
			if (other.controllerPort != null)
				return false;
		} else if (!controllerPort.equals(other.controllerPort))
			return false;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
