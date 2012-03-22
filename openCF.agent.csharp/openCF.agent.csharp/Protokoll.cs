using System;

namespace openCF.agent.csharp {
	public enum PacketType : int {
		INVALID					= -1,
		HEARTBEAT				= 0,
		AGENT_HELLO				= 1,
		AGENT_HELLO_RESPONSE	= 2,
		AUTOMATION_CONTROL		= 13,
		AUTOMATION_STATUS		= 20
	}
	
	public sealed class PacketKeys {
		public static readonly string AGENT_ID = "agent_id";
		public static readonly string AGENT_ENCODING = "agent_encoding";
		public static readonly string AGENT_VERSION = "agent_version";
		public static readonly string AGENT_PLATTFORM = "agent_plattform";
		public static readonly string TYPE = "type";
		public static readonly string SUCCESSFULL = "successfull";
		public static readonly string RETURN_CODE = "return_code";
		public static readonly string MESSAGE = "message";
		public static readonly string LOCAL_TIME = "local_time";
		public static readonly string AUTOMATION_ID = "automation_id";
		public static readonly string AUTOMATION_DESCRIPTOR = "automation_descriptor";
		public static readonly string AUTOMATION_ACTION = "automation_action";
		public static readonly string AUTOMATION_PARAMETER = "automation_parameter";
		public static readonly string TIMEOUT = "timeout";
		public static readonly string ARGUMENTS = "arguments";
		public static readonly string USER = "user";
		public static readonly string GROUP = "group";
		public static readonly string REPOSITORY_URL = "repository_url";
	}
}

