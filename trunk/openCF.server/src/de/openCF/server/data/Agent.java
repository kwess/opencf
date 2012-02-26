package de.openCF.server.data;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.openCF.server.Data;

@Entity
@Table(name = "Agent")
public class Agent {

	public enum Plattform {
		LINUX, UNIX, WINDOWS
	};

	public enum Status {
		ONLINE, OFFLINE
	};

	@Id
	private String			id			= null;
	private String			version		= null;
	@Enumerated(value = EnumType.STRING)
	private Plattform		plattform	= null;
	@Enumerated(value = EnumType.STRING)
	private Status			status		= Status.OFFLINE;
	@ManyToOne
	private Server			server		= Data.getServer();
	@Column(nullable = false)
	private Date			updated		= null;
	@OneToMany
	@JoinTable(name = "agent2automation")
	private Set<Automation>	automations	= null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Plattform getPlattform() {
		return plattform;
	}

	public void setPlattform(Plattform plattform) {
		this.plattform = plattform;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Set<Automation> getAutomations() {
		return automations;
	}

	public void setAutomations(Set<Automation> automations) {
		this.automations = automations;
	}

}
