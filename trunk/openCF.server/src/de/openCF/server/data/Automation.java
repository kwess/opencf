package de.openCF.server.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "automation")
@XmlRootElement
public class Automation {

	@Id
	@GeneratedValue
	private Integer				id			= null;
	@Enumerated(EnumType.STRING)
	private AutomationStatus	status		= AutomationStatus.unknown;
	@OneToMany
	private List<Message>		messages	= null;
	@ManyToOne
	private Agent				agent		= null;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Message> getMessages() {
		if (messages == null)
			messages = new ArrayList<Message>();
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public AutomationStatus getStatus() {
		return status;
	}

	public void setStatus(AutomationStatus status) {
		this.status = status;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	@Override
	public String toString() {
		int size = messages != null ? messages.size() : 0;
		return "Automation [id=" + id + ", status=" + status + ", messages=" + size + ", agent=" + agent.getId() + "]";
	}

}
