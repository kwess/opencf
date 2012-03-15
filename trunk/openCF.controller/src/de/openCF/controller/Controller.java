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
import de.openCF.protocol.PacketKeys;
import de.openCF.protocol.PacketReader;
import de.openCF.protocol.PacketType;
import de.openCF.protocol.PacketWriter;

// {type:13, agent_id:["agent"], automation_action:"start"}

public class Controller implements Runnable {

	private Connection	c			= null;
	private boolean		connected	= false;

	public static void main(String[] args) {
		Runnable runnable = new Controller();
		runnable.run();
	}

	private class ControllerPacketHandler implements PacketHandler {

		@SuppressWarnings("unchecked")
		@Override
		public void handlePacket(Packet packet) {
			Map<String, Object> data = packet.getData();

			Integer type = (Integer) data.get(PacketKeys.TYPE);
			if (type == PacketType.AUTOMATION_STATUS) {
				String status = (String) data.get(PacketKeys.AUTOMATION_STATUS);
				if ("talking".equals(status)) {
					String message = (String) data.get(PacketKeys.AUTOMATION_MESSAGE);
					System.out.println("> " + data.get(PacketKeys.AUTOMATION_ID) + " +-> " + message.replace('\n', '.'));
				} else {
					System.out.println("> " + data.get(PacketKeys.AUTOMATION_ID) + " --> " + status);
				}
			} else if (type == PacketType.AUTOMATION_QUERY) {
				int i = 0;
				for (Map<String, Object> s : (List<Map<String, Object>>) data.get(PacketKeys.AUTOMATION_QUERY_RESULT)) {
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
				data.put(PacketKeys.TYPE, PacketType.AUTOMATION_CONTROL);
				data.put(PacketKeys.AUTOMATION_ACTION, args[0]);
				if ("start".equals(args[0])) {
					data.put(PacketKeys.AUTOMATION_DESCRIPTOR, args[1]);
					data.put(PacketKeys.REPOSITORY_URL, "http://localhost:8080/jobs/");
					data.put(PacketKeys.AGENT_ID, list);
				} else if ("stop".equals(args[0]) || "listen".equals(args[0])) {
					List<Integer> convert = new ArrayList<Integer>();
					for (String s : list)
						convert.add(Integer.parseInt(s));
					data.put(PacketKeys.AUTOMATION_ID, convert);
				} else if ("query".equals(args[0])) {
					data.put(PacketKeys.TYPE, PacketType.AUTOMATION_QUERY);
					data.put(PacketKeys.AUTOMATION_QUERY, args[1]);
					data.put(PacketKeys.AUTOMATION_QUERY_PARAMETER, args[2]);
				}

				System.out.println("< " + data);

				Packet p = new Packet();
				p.setData(data);

				c.forward(p);
			} catch (IOException e) {
				System.err.println("! failed reading line");
				break;
			}
		} while (connected);
	}
}
