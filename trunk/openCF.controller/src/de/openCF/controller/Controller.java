package de.openCF.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import de.openCF.protocol.Connection;
import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHandler;
import de.openCF.protocol.PacketHelper.Encoding;
import de.openCF.protocol.PacketReader;
import de.openCF.protocol.PacketWriter;
import de.openCF.protocol.Protocol;

/**
 * 
 * Syntax:<br>
 * connect [server]<br>
 * logout <br>
 * start [descriptor] [list o' agents]<br>
 * stop [automation-id]<br>
 * query [what to know] [parameter]<br>
 * 
 * @author kristian.wessels
 * 
 */
public class Controller implements Runnable {

	private Connection	c			= null;
	private boolean		connected	= false;
	private boolean		debug		= false;

	public static void main(String[] args) {
		Runnable runnable = new Controller();
		runnable.run();
	}

	private class ControllerPacketHandler implements PacketHandler {

		@SuppressWarnings("unchecked")
		@Override
		public void handlePacket(Packet packet) {
			Map<String, Object> data = packet.getData();

			if (debug)
				System.out.println("* " + data.toString().replace("\n", ""));

			Integer type = (Integer) data.get(Protocol.Key.TYPE);
			if (type == Protocol.AUTOMATION_STATUS) {
				String status = (String) data.get(Protocol.Key.AUTOMATION_STATUS);

				if ("talking".equals(status)) {
					String message = (String) data.get(Protocol.Key.AUTOMATION_MESSAGE);
					System.out.println("> " + data.get(Protocol.Key.AUTOMATION_ID) + " +-> " + message.replace('\n', '.'));
				} else {
					System.out.println("> " + data.get(Protocol.Key.AUTOMATION_ID) + " --> " + status);
					if (status.contains("failed"))
						System.err.println("! " + data.get(Protocol.Key.AUTOMATION_ID) + " --> " + data.get(Protocol.Key.AUTOMATION_MESSAGE));
				}
			} else if (type == Protocol.AUTOMATION_QUERY) {
				int i = 0;
				for (Map<String, Object> s : (List<Map<String, Object>>) data.get(Protocol.Key.AUTOMATION_QUERY_RESULT)) {
					System.out.println("> [" + ++i + "]-------------------------------");
					for (String key : s.keySet())
						System.out.println("> " + key + " = " + s.get(key));
				}
				if (i == 0)
					System.out.println("> query returned no results");
			}
		}

		@Override
		public void handleClose() {
			System.err.println("! Verbindung abgebrochen");
		}

		@Override
		public void handleOpen() {
			System.out.println("! Verbindung hergestellt");
		}

	}

	private boolean connect(String host) {
		try {
			Socket socket = new Socket(host, 6789);
			c = new Connection();
			c.setReader(new PacketReader());
			c.setWriter(new PacketWriter());
			c.setPacketHandler(new ControllerPacketHandler());
			c.setSocket(socket);
			c.setEncoding(Encoding.JSON);
			Executors.newSingleThreadExecutor().execute(c);
		} catch (UnknownHostException e1) {
			System.err.println("! " + e1.getMessage());
			return false;
		} catch (IOException e1) {
			System.err.println("! " + e1.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public void run() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		do {
			try {
				String line = in.readLine();

				if (line == "")
					continue;

				String[] args = line.split(" ");

				if ("help".equals(args[0])) {
					printHelp(args.length > 1 ? args[1] : null);
					continue;
				} else if ("exit".equals(args[0])) {
					System.out.println("! exit");
					break;
				}

				if (args.length < 2 && !"logout".equals(args[0])) {
					System.err.println("! not enough arguments");
					continue;
				}

				if (!connected && !"connect".equals(args[0])) {
					System.err.println("! yre not connected");
					continue;
				} else if (!connected && "connect".equals(args[0])) {
					this.connected = connect(args[1]);
					if (!connected)
						System.err.println("! connect failed");
					continue;
				} else if (connected && "connect".equals(args[0])) {
					System.err.println("! yre already connected");
					continue;
				} else if (connected && "logout".equals(args[0])) {
					System.exit(0);
				}

				List<String> list = new ArrayList<String>();
				for (int i = "stop".equals(args[0]) ? 1 : 2; i < args.length; i++)
					list.add(args[i]);

				Map<String, Object> data = new HashMap<String, Object>();
				data.put(Protocol.Key.TYPE, Protocol.AUTOMATION_CONTROL);
				data.put(Protocol.Key.AUTOMATION_ACTION, args[0]);
				if ("start".equals(args[0])) {
					data.put(Protocol.Key.AUTOMATION_DESCRIPTOR, args[1]);
					data.put(Protocol.Key.REPOSITORY_URL, "http://localhost:8080/jobs/");
					data.put(Protocol.Key.AGENT_ID, list);
				} else if ("stop".equals(args[0]) || "listen".equals(args[0])) {
					List<Integer> convert = new ArrayList<Integer>();
					for (String s : list)
						convert.add(Integer.parseInt(s));
					data.put(Protocol.Key.AUTOMATION_ID, convert);
				} else if ("query".equals(args[0])) {
					data.put(Protocol.Key.TYPE, Protocol.AUTOMATION_QUERY);
					data.put(Protocol.Key.AUTOMATION_QUERY, args[1]);
					if (args.length > 2)
						data.put(Protocol.Key.AUTOMATION_QUERY_PARAMETER, args[2]);
				}

				if (debug)
					System.out.println("* " + data);

				Packet p = new Packet();
				p.setData(data);

				c.forward(p);
			} catch (IOException e) {
				System.err.println("! failed reading line");
				break;
			}
		} while (true);
	}

	private void printHelp(String a) {
		if (a == null) {
			System.out.println("> HELP!");
			System.out.println("connect [server]");
			System.out.println("start   [descriptor] [agent]+");
			System.out.println("stop    [automationid]+");
			System.out.println("query   [typ] [option]*");
			System.out.println("help    [detail]");
		} else if ("connect".equals(a)) {
			System.out.println("> HELP " + a);
			System.out.println(a + " [server]");
			System.out.println("        connects to a given server");
			System.out.println("        the server must be an url");
		} else if ("start".equals(a)) {
			System.out.println("> HELP " + a);
			System.out.println(a + "  [descriptor] [agent]+");
			System.out.println("      starts an automation");
			System.out.println("      descriptor: repository-relative path to the automation descriptor");
			System.out.println("      agent:      agent-id(s) to run the automation on");
		} else if ("stop".equals(a)) {
			System.out.println("> HELP " + a);
			System.out.println(a + " [automationid]");
			System.out.println("     stops an automation");
			System.out.println("     automationid: identifier of the automation to stop");
		} else if ("query".equals(a)) {
			System.out.println("> HELP " + a);
			System.out.println(a + "  [typ] [option]*");
			System.out.println("      queries informations from the server");
			System.out.println("      typ:    query typ, can be server, agent, automation");
			System.out.println("      option: options of the query typ");
		} else {
			System.out.println("> HELP! cant help you... " + a);
		}
	}
}
