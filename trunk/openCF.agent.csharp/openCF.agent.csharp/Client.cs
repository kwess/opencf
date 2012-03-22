using System;
using System.Net.Sockets;
using System.Net;

namespace openCF.agent.csharp {
	public class Client {
		private String server = null;
		private int port = 0;
		private Boolean connected = false;
		private TcpClient tcpClient = null;

		public Client (String server, int port) {
			this.server = server;
			this.port = port;
		}

		private bool send(String message) {
			if(connected == false) {
				return false;
			}
			Byte[] data = System.Text.Encoding.UTF8.GetBytes(message);

			NetworkStream stream = this.tcpClient.GetStream();

			int len = IPAddress.HostToNetworkOrder(data.Length);
			Byte[] lenData = BitConverter.GetBytes(len);
			stream.Write(lenData, 0, lenData.Length);
			stream.Write(data, 0, data.Length);

			Console.WriteLine("Sent: {0}", message);

			return true;
		}

		public void connect() {
			try {
				this.tcpClient = new TcpClient(this.server, this.port);
				this.connected = true;
				String helloMessage = "{\"agent_id\":\"TODO-AGENTID-IN-agent.cfg\",\"type\":1,\"agent_version\":\"1\",\"agent_plattform\":\"windows\"}";
				this.send(helloMessage);
				
				// Receive the TcpServer.response.
				Byte[] data = new Byte[256];

				/*String responseData = String.Empty;
				NetworkStream stream = this.tcpClient.GetStream();
				Int32 bytes = stream.Read(data, 0, data.Length);
				responseData = System.Text.Encoding.ASCII.GetString(data, 0, bytes);
				Console.WriteLine("Received: {0}", responseData);*/
	
				// Close everything.
				stream.Close();
				this.tcpClient.Close();
			}
			catch (ArgumentNullException e) {
				Console.WriteLine("ArgumentNullException: {0}", e);
			}
			catch (SocketException e) {
				Console.WriteLine("SocketException: {0}", e);
			}

			Console.WriteLine("\n Press Enter to continue...");
			Console.Read();
		}
	}
}

