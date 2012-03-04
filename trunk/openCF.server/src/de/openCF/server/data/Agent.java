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
import javax.xml.bind.annotation.XmlRootElement;

import de.openCF.server.Data;

@Entity
@Table(name = "Agent")
@XmlRootElement
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

	@Override
	public String toString() {
		return "Agent [id=" + id + ", version=" + version + ", plattform=" + plattform + ", status=" + status + ", server=" + server + ", updated=" + updated + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((plattform == null) ? 0 : plattform.hashCode());
		result = prime * result + ((server == null) ? 0 : server.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((updated == null) ? 0 : updated.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		Agent other = (Agent) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (plattform != other.plattform)
			return false;
		if (server == null) {
			if (other.server != null)
				return false;
		} else if (!server.equals(other.server))
			return false;
		if (status != other.status)
			return false;
		if (updated == null) {
			if (other.updated != null)
				return false;
		} else if (!updated.equals(other.updated))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}
