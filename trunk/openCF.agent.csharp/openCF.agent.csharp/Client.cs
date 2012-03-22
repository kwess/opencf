using System;
using System.Net.Sockets;
using System.Net;

namespace openCF.agent.csharp {
	public class Client {
		public Configuration config {get; set;}
		public Boolean connected {get; set;}
		public TcpClient tcpClient {get; set;}

		public Client (Configuration config) {
			this.config = config;
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
		
		private bool send(Packet packet) {
			if(connected == false) {
				return false;
			}
			
			NetworkStream stream = this.tcpClient.GetStream();
			
			String message = packet.getJSONString();
			
			Byte[] data = System.Text.Encoding.UTF8.GetBytes(message);
			int len = IPAddress.HostToNetworkOrder(data.Length);
			Byte[] lenData = BitConverter.GetBytes(len);
			stream.Write(lenData, 0, lenData.Length);
			stream.Write(data, 0, data.Length);

			Console.WriteLine("Sent: {0}", message);

			return true;
		}

		public void connect() {
			try {
				this.tcpClient = new TcpClient(this.config.hostname, this.config.port);
				this.connected = true;
				HelloPacket packet = new HelloPacket();
				packet.agent_id = this.config.agentID;
				packet.agent_encoding = this.config.encoding;
				packet.agent_plattform = this.config.platform;
				packet.agent_version = this.config.version;
				//packet.agentID = this.config.agentID;
				this.send (packet);
				
				// Receive the TcpServer.response.
				Byte[] data = new Byte[256];
				
				//TODO response lesen
				String responseData = String.Empty;
				NetworkStream stream = this.tcpClient.GetStream();
				Int32 bytes = stream.Read(data, 0, data.Length);
				int bytes2 = IPAddress.NetworkToHostOrder(bytes);
				Console.WriteLine ("blub: {0}", bytes2);
				responseData = System.Text.Encoding.ASCII.GetString(data, 0, bytes);
				Console.WriteLine("Received: {0}", responseData);
	
				// Close everything.
				this.tcpClient.GetStream().Close();
				this.tcpClient.Close();
			}
			catch (ArgumentNullException e) {
				Console.WriteLine("ArgumentNullException: {0}", e);
			}
			catch (SocketException e) {
				Console.WriteLine("SocketException: {0}", e);
			}
		}
	}
}

