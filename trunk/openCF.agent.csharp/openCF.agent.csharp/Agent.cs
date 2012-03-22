using System;

namespace openCF.agent.csharp {
	public class Agent {
		
		public Agent () {
			
		}
			
		public static void Main (String[] args) {
			Configuration config = new Configuration();
			
			Client client = new Client(config);
			client.connect();

			Console.WriteLine("fertig");
		}
	}
}

