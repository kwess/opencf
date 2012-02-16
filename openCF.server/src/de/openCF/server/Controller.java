package de.openCF.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.openCF.protocol.Packet;
import de.openCF.protocol.PacketHelper;
import de.openCF.server.communication.Connection;
import de.openCF.server.communication.ControllerPacketHandler;

public class Controller implements Runnable {

	public static void main(String[] args) {
		Runnable runnable = new Controller();
		runnable.run();
	}

	@Override
	public void run() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		Connection c = null;

		try {
			Socket socket = new Socket("localhost", 6789);
			c = new Connection();
			c.setSocket(socket);
			c.setPacketHandler(new ControllerPacketHandler());
		} catch (UnknownHostException e1) {
			System.err.println(e1.getMessage());
			return;
		} catch (IOException e1) {
			System.err.println(e1.getMessage());
			return;
		}

		do {
			System.out.print("> ");
			try {
				String line = in.readLine();

				if (line == "")
					continue;

				JSONTokener tokener = new JSONTokener(line);
				JSONObject object = new JSONObject(tokener);

				Map<String, Object> data = PacketHelper.toMap(object);

				Packet p = new Packet();
				p.setData(data);

				System.out.println(p.dump());

				c.forward(p);
			} catch (IOException e) {
				System.err.println("failed reading line");
			} catch (JSONException e) {
				System.err.println("failed parsing json");
			}
		} while (true);
	}
}
