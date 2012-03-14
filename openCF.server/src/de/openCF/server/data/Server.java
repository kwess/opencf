package de.openCF.server.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "Server")
@XmlRootElement
public class Server {

	@Id
	private String		id				= null;
	private String		hostname		= "";
	private Integer		agentPort		= 0;
	private Integer		controllerPort	= 0;
	@Enumerated(EnumType.STRING)
	private Plattform	plattform		= Plattform.UNKNOWN;
	@Enumerated(EnumType.STRING)
	private Status		status			= Status.OFFLINE;
	@Column(nullable = false)
	private Date		updated			= new Date();

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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Plattform getPlattform() {
		return plattform;
	}

	public void setPlattform(Plattform plattform) {
		this.plattform = plattform;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "Server [id=" + id + ", hostname=" + hostname + ", agentPort=" + agentPort + ", controllerPort=" + controllerPort + "]";
	}

}
