package de.openCF.protocol;

public abstract class Protocol {

	public static final int	INVALID					= -1;
	public static final int	AGENT_HEARTBEAT			= 0;
	public static final int	AGENT_HELLO				= 1;
	public static final int	AGENT_HELLO_RESPONSE	= 2;
	public static final int	AUTOMATION_CONTROL		= 13;
	public static final int	AUTOMATION_STATUS		= 20;
	public static final int	AUTOMATION_QUERY		= 30;
	public static final int	DATA_GET				= 41;
	public static final int	DATA_PUT				= 42;

	public static abstract class Key {

		public static final String	TYPE						= "type";
		public static final String	AGENT_ID					= "agent_id";
		public static final String	AGENT_VERSION				= "agent_version";
		public static final String	AGENT_PLATTFORM				= "agent_plattform";
		public static final String	AGENT_ENCODING				= "agent_encoding";
		public static final String	SUCCESSFULL					= "successfull";
		public static final String	RETURN_CODE					= "return_code";
		public static final String	MESSAGE						= "message";
		public static final String	AGENT_LOCAL_TIME			= "local_time";
		public static final String	REPOSITORY_URL				= "repository_url";
		public static final String	AUTOMATION_DESCRIPTOR		= "automation_descriptor";
		public static final String	AUTOMATION_ID				= "automation_id";
		public static final String	AUTOMATION_ACTION			= "automation_action";
		public static final String	AUTOMATION_STATUS			= "automation_status";
		public static final String	AUTOMATION_AGENT			= "automation_agent";
		public static final String	AUTOMATION_MESSAGE			= "automation_message";
		public static final String	AUTOMATION_REASON			= "automation_reason";
		public static final String	AUTOMATION_PARAMETER		= "automation_parameter";
		public static final String	AUTOMATION_QUERY			= "automation_query";
		public static final String	AUTOMATION_QUERY_PARAMETER	= "automation_query_parameter";
		public static final String	AUTOMATION_QUERY_RESULT		= "automation_query_result";
		public static final String	SERVER_ID					= "server_id";
		public static final String	SERVER_HOSTNAME				= "server_hostname";
		public static final String	SERVER_PLATTFORM			= "server_plattform";
		public static final String	STATUS						= "status";
		public static final String	DATA						= "data";
		public static final String	DATA_RELATIVE				= "data_relative";
		public static final String	DATA_SOURCE					= "data_source";
		public static final String	DATA_SIZE					= "data_size";
		public static final String	DATA_FORMAT					= "data_format";

	}

}
