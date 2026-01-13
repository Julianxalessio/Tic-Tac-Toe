package ServerPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainGame {
	static void main() throws Exception {
		/**
		 * The Serverlist
		 */
		final List<String[]> servers = new ArrayList<>();

		/**
		 * Creates the Portlis for the 10 Servers
		 */
		List<int[]> serverPorts = new ArrayList<>();
		int startPort = 6972;
		for (int i = 0; i <10; i++){
			int port = startPort + i;
			int[] tmp = {0, port};
			serverPorts.add(tmp);
		}

		/**
		 * The MainGame Socket
		 */
		int port = 6971;
		final DatagramSocket socket = new DatagramSocket(port);

		/**
		 * Mainpart of the Code
		 */
		System.out.println("ServerCreater Ready!");
		boolean active = true;
		while (active) {
			/**
			 * Messagereceiver
			 */
			byte[] buffer = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String msg = new String(packet.getData(), 0, packet.getLength()).trim();
			System.out.println("Received: " + msg);
			String[] msgParts = msg.split(";");
			InetAddress sender = packet.getAddress();

			//Message has 3 Parts
			if (msgParts.length == 3) {
				//Message belongs to MainGame
				if (msgParts[0].equals("main")) {
					//!!! Make sure that botgames now work
					/**
					* Create a Server with a Bot
					 */
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
					}
					/**
					* Player cancelled a new Game
					 */
					else if (msgParts[1].equals("terminate")) {
						//Removes the Server with the corresponding ID
						System.out.println("Server tyring to delete");
						String[] tempEntry = new String[4];
						for (String[] entry : servers) {
							if (entry[0].equals(msgParts[2])) {
								for (int[] tmp : serverPorts) {
									String tmpString = Integer.toString(tmp[1]);
									if (tmpString.equals(entry[3])) {
										tmp[0] = 0;
									}
								}
								tempEntry = entry;
							}
						}
						servers.remove(tempEntry);
						System.out.println("Server deleted");

					}
					/**
					 * Player creates a new Server
					 */
					else if (msgParts[1].equals("Create Server")) {
						//Is a 4 digit number
						if (msgParts[2].matches("^[0-9]{4}$")) {
							//Checks if Server already exists
							int items = servers.size();
							for (String[] entry : servers) {
								if (entry[0].equals(msgParts[2])) {
									System.out.println("Server already Exists");
									sendMessageToPlayer(socket, "player;Server Exists;", sender);
								} else
									items--;
							}
							//If server doesnt exist, create a new Server
							if (items <= 0) {
									servers.add(new String[]{msgParts[2], sender.getHostAddress(), "", ""});
							}
						}
					}
					/**
					 * Player joins
					 */
					else if (msgParts[1].equals("Join Server")) {
						//Is a 4 digit number
						if (msgParts[2].matches("^[0-9]{4}$")) {
							//Looks if Server exists
							int items = servers.size();
							for (int i = 0; i < servers.size(); i++) {
								if (servers.get(i)[0].equals(msgParts[2])) {
									if (servers.get(i)[2].isEmpty()) {
										//Chooses port for a server
										int portChosen = 0;
										for (int[] tmp : serverPorts){
											if (tmp[0] == 0){
												portChosen = tmp[1];
												tmp[0] = 1;
											}
										}
										if (portChosen == 0){
											sendMessageToPlayer(socket, "player;No Ports are avaiable;", sender);
										} else {
											//Sends the port to the Players
											servers.get(i)[2] = sender.getHostAddress();
											sendMessageToPlayer(socket, "serverPort;"+portChosen+";", InetAddress.getByName(servers.get(i)[1]));
											sendMessageToPlayer(socket, "serverPort;"+portChosen+";", InetAddress.getByName(servers.get(i)[2]));

											for (String[] entry : servers) {
												if (msgParts[2].equals(entry[0])) {
													entry[3] = Integer.toString(portChosen);
												}
											}
											Server server = new Server(servers.get(i)[1], servers.get(i)[2], msgParts[2], portChosen);
											//!!! Message both Players Game Starting
											new Thread(() -> {
												try {
													server.startServer();
												} catch (Exception e) {
													System.out.println(e);
												}
											}).start();
										}
									} else {
										System.out.println("Server Full");
										//!!! Message Player Server Full
									}
								} else
									items--;
							}
							if (items <= 0) {
								sendMessageToPlayer(socket, "Server not existing", sender);
							}
						}
					}
				} else {
					System.out.println("Message unknown!");
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
