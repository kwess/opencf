using System;

namespace openCF.agent.csharp {
	public class Agent {
		
		
		public Agent () {
		}
			
		public static void Main (String[] args) {
			Client client = new Client("localhost", 5678);
			client.connect();


			Console.WriteLine("fertig");
		}
	}
}

