using System;
using System.Web.Script.Serialization;

namespace openCF.agent.csharp {
	[Serializable]
	public class Packet {
		
		private JavaScriptSerializer serializer = new JavaScriptSerializer();
		
		public PacketType type {get; set;}
		
		public Packet (PacketType type) {
			this.type = type;
		}
		
		public String getJSONString() {
			return serializer.Serialize(this);
		}
	}
	
	[Serializable]
	public class HelloPacket : Packet {
		
		public String agent_id {get; set;}
		public String agent_encoding {get; set;}
		public String agent_version {get; set;}
		public String agent_plattform {get; set;}
		
		public HelloPacket() : base(PacketType.AGENT_HELLO) {
			
		}
	}
}

