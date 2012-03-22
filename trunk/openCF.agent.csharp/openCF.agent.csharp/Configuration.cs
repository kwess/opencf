using System;
using System.Configuration;

namespace openCF.agent.csharp {
	public class Configuration {
		public String hostname {get; set;}
		public int port {get; set;}
		public String version {get; set;}
		public String agentID {get; set;}
		public String platform {get; set;}
		public String encoding {get; set;}
		
		public Configuration () {
			AppSettingsReader reader = new AppSettingsReader();
			
			this.hostname = (String)reader.GetValue("hostname", typeof(String));
			this.port = (int)reader.GetValue ("port", typeof(int));
			this.version = (String)reader.GetValue("version", typeof(String));
			this.agentID = System.Net.Dns.GetHostName();
			//TODO hier wäre doch das auslesen von der Plattform toll, aber es kommen Strings wie "Win32NT" raus, was nicht vorgesehen ist
			//this.platform = Environment.OSVersion.Platform.ToString();
			this.platform = (String)reader.GetValue("platform", typeof(String));
			this.encoding = "JSON";
		}
	}
}

