package de.openCF.protocol;

public abstract class PacketType {

	public static final int	INVALID					= -1;
	public static final int	AGENT_HEARTBEAT			= 0;
	public static final int	AGENT_HELLO				= 1;
	public static final int	AGENT_HELLO_RESPONSE	= 2;
	public static final int	AUTOMATION_CONTROL		= 13;
	public static final int	AUTOMATION_STATUS		= 20;

}
