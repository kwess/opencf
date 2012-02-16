package de.openCF.server.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "heartbeat")
public class Heartbeat {

	@Id
	@GeneratedValue
	private Integer	id				= null;
	@ManyToOne(optional = false)
	private Agent	agent			= null;
	private Date	agent_localtime	= null;
	private Date	server_receive	= new Date();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getAgent_localtime() {
		return agent_localtime;
	}

	public void setAgent_localtime(Date agent_localtime) {
		this.agent_localtime = agent_localtime;
	}

	public Date getServer_receive() {
		return server_receive;
	}

	public void setServer_receive(Date server_receive) {
		this.server_receive = server_receive;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

}
