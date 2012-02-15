package de.openCF.server.data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "message")
public class Message {

	@Id
	private Integer				id			= null;
	@ManyToOne
	private Automation			automation	= null;
	@Enumerated(value = EnumType.STRING)
	private AutomationStatus	status		= AutomationStatus.unknown;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Automation getAutomation() {
		return automation;
	}

	public void setAutomation(Automation automation) {
		this.automation = automation;
	}

	public AutomationStatus getStatus() {
		return status;
	}

	public void setStatus(AutomationStatus status) {
		this.status = status;
	}

}
