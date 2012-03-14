package de.openCF.server.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "message")
@XmlRootElement
public class Message {

	@Id
	@GeneratedValue
	private Integer				id			= null;
	@ManyToOne
	private Automation			automation	= null;
	private AutomationStatus	status		= AutomationStatus.unknown;
	private String				message		= "";

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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", automation=" + automation.getId() + ", status=" + status + ", message=" + message + "]";
	}

}
