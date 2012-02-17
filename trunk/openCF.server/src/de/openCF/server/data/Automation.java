package de.openCF.server.data;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "automation")
public class Automation {

	@Id
	@GeneratedValue
	private Integer				id			= null;
	@Enumerated(value = EnumType.STRING)
	private AutomationStatus	status		= AutomationStatus.unknown;
	@OneToMany
	@JoinTable(name = "automation2messages")
	private Set<Message>		messages	= null;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public AutomationStatus getStatus() {
		return status;
	}

	public void setStatus(AutomationStatus status) {
		this.status = status;
	}

}
