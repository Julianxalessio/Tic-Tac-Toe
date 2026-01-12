package ServerPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainGame {

	public static void main(String[] args) throws Exception {
		List<String[]> servers = new ArrayList<>();
		int port = 6969;
		DatagramSocket socket = new DatagramSocket(port);
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
					if (msgParts[1].equals("Create Server_bot")) {
						if (msgParts[2].matches("\\^[0-9]{4}$")) {
							int items = servers.size();
							for (String[] entry : servers) {
								if (entry[0].equals(msgParts[2])) {
									System.out.println("Server already Exists");
//									Message Player that ID is already in use
								} else
									items--;
							}
							if (items <= 0) {
								servers.add(new String[] { msgParts[2], sender.getHostAddress(), "" });
							}
						}
					} else if (msgParts[1].equals("Create Server")) {
						if (msgParts[2].matches("\\^[0-9]{4}$")) {
							int items = servers.size();
							for (String[] entry : servers) {
								if (entry[0].equals(msgParts[2])) {
									System.out.println("Server already Exists");
//									Message Player that ID is already in use
								} else
									items--;
							}
							if (items <= 0) {
								servers.add(new String[] { msgParts[2], sender.getHostAddress(), "" });
							}
						}
					}
					else if (msgParts[1].equals("Join Server")) {
						if (msgParts[2].matches("\\^[0-9]{4}$")) {
							int items = servers.size();
							for (int i = 0; i < servers.size(); i++) {
								if (servers.get(i)[0].equals(msgParts[2])) {
									servers.get(i)[2] = sender.getHostAddress();
									int finalI = i;
									new Thread(){
										Server server = new Server(servers.get(finalI)[1],servers.get(finalI)[2], socket);
									}.start();
									
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
			socket.close();
		}
	}
}
