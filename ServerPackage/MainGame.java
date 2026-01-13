package ServerPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainGame {

	static void main() throws Exception {
		final List<String[]> servers = new ArrayList<>();
		int port = 6971;
		final DatagramSocket socket = new DatagramSocket(port);
		System.out.println("ServerCreater Ready!");
		boolean active = true;
		while (active) {
			byte[] buffer = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);

			String msg = new String(packet.getData(), 0, packet.getLength()).trim();
			System.out.println("Received: " + msg);
			String[] msgParts = msg.split(";");
			InetAddress sender = packet.getAddress();
			if (msgParts.length == 3) {
				if (msgParts[0].equals("main")) {
					//!!! Make sure that botgames now work
					if (msgParts[1].equals("Create Server_bot")) {
						if (msgParts[2].matches("^[0-9]{4}$")) {
							int items = servers.size();
							for (String[] entry : servers) {
								if (entry[0].equals(msgParts[2])) {
									System.out.println("Server already Exists");
									sendMessageToPlayer(socket, "player;Server Exists;", sender);
								} else
									items--;
							}
							if (items <= 0) {
								servers.add(new String[] { msgParts[2], sender.getHostAddress(), "" });
							}
						}
					} else if (msgParts[1].equals("Create Server")) {
						if (msgParts[2].matches("^[0-9]{4}$")) {
							int items = servers.size();
							for (String[] entry : servers) {
								if (entry[0].equals(msgParts[2])) {
									System.out.println("Server already Exists");
									sendMessageToPlayer(socket, "player;Server Exists;", sender);
								} else
									items--;
							}
							if (items <= 0) {
								servers.add(new String[] { msgParts[2], sender.getHostAddress(), "" });
								//!!! Message Player Server Created
							}
						}
					}
					else if (msgParts[1].equals("Join Server")) {
						if (msgParts[2].matches("^[0-9]{4}$")) {
							int items = servers.size();
							for (int i = 0; i < servers.size(); i++) {
								if (servers.get(i)[0].equals(msgParts[2])) {
									if (servers.get(i)[2].isEmpty()) {
										servers.get(i)[2] = sender.getHostAddress();
										Server server = new Server(servers.get(i)[1],servers.get(i)[2], msgParts[2]);
										//!!! Message both Players Game Starting
										new Thread(() -> {
											try {
												server.startServer();
											} catch (Exception e) {
												System.out.println(e);
											}
										}).start();
									} else {
										System.out.println("Server Full");
										//!!! Message Player Server Full
									}
								} else
									items--;
							}
							if (items <= 0) {
//								Message Player Server not Found
							}

						}
					}
				} else {
					System.out.println("");
				}
			}
		}
	}
	private static void sendMessageToPlayer(DatagramSocket socket, String message, InetAddress address) throws Exception {
		byte[] buffer = message.getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 6970);
		socket.send(packet);
	}
}
