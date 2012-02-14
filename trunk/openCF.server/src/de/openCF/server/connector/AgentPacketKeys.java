package de.openCF.server.connector;

public enum AgentPacketKeys {

	//@formatter:off
	TYPE("type"),
	AGENT_ID("agent_id"),
	AGENT_VERSION("agent_version"),
	AGENT_PLATTFORM("agent_plattform"),
	SUCCESSFULL("successfull"),
	RETURN_CODE("return_code"),
	MESSAGE("message"),
	AGENT_LOCAL_TIME("local_time"),
	REPOSITORY_URL("repository_url"),
	AUTOMATION_DESCRIPTOR("automation_descriptor"),
	AUTOMATION_ID("automation_id"),
	AUTOMATION_ACTION("automation_action"),
	AUTOMATION_STATUS("automation_status"),
	AUTOMATION_MESSAGE("automation_message");
	//@formatter:on

	public final String	key;

	private AgentPacketKeys(String key) {
		this.key = key;
	}

}
