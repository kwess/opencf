package de.openCF.server.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "automation_control")
@XmlRootElement
public class AutomationControl {

	@Id
	@GeneratedValue
	private Integer				id			= null;
	@ManyToOne
	private Automation			automation	= null;
	private boolean				successfull	= true;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AutomationAction	action		= null;
	@Column(nullable = false)
	// TODO Der initiator muss noch verdratet werden
	private String				initiator	= "unknown";
	@Column(nullable = false)
	private Date				date		= new Date();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isSuccessfull() {
		return successfull;
	}

	public void setSuccessfull(boolean successfull) {
		this.successfull = successfull;
	}

	public AutomationAction getAction() {
		return action;
	}

	public void setAction(AutomationAction action) {
		this.action = action;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public Automation getAutomation() {
		return automation;
	}

	public void setAutomation(Automation automation) {
		this.automation = automation;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "AutomationControl [id=" + id + ", automation=" + (automation != null ? automation.getId() : "?") + ", successfull=" + successfull + ", action=" + action + ", initiator=" + initiator + ", date=" + date + "]";
	}

}
