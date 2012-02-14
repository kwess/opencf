package de.openCF.server.data;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "openCF.server.data.Agent")
public class Agent {

	public enum PLATTFORM {
		linux, unix, windows
	};

	@Id
	private String		id			= null;
	private String		version		= null;
	private PLATTFORM	plattform	= null;
	private Server		server		= null;

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

	public PLATTFORM getPlattform() {
		return plattform;
	}

	public void setPlattform(PLATTFORM plattform) {
		this.plattform = plattform;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

}
