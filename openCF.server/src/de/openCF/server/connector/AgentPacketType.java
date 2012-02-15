package de.openCF.server.connector;

public abstract class AgentPacketType {

	public static final int	INVALID						= -1;
	public static final int	AGENT_HEARTBEAT				= 0;
	public static final int	AGENT_HELLO					= 1;
	public static final int	AGENT_HELLO_RESPONSE		= 2;
	public static final int	AUTOMATION_PREPARE			= 11;
	public static final int	AUTOMATION_PREPARE_RESONSE	= 12;
	public static final int	AUTOMATION_CONTROL			= 13;
	public static final int	AUTOMATION_CONTROL_RESPONSE	= 14;
	public static final int	AUTOMATION_STATUS			= 20;

}
