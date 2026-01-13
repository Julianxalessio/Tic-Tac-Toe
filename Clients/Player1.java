package Clients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 
 * <h1>TicTacToe Player1-Script</h1>
 * <h2>Script allows to send messages/responses to Server and play TicTacToe as
 * Player1</h2>
 * 
 * !Warning: Start the Server before launching the Player-Script!
 * 
 * @version 1.0
 * @author Julian Lombardo
 * @author Diego Zwahlen
 * @author Lean Melone
 * 
 */

public class Player1 {

	 /**
	 * Color for TicTacToe-Game
	 * @param c
	 * @return
	 */
	public static String colorize(char c) {
		if (c == 'X') {
			return RED + c + RESET;
		} else if (c == 'O') {
			return BLUE + c + RESET;
		} else if (Character.isDigit(c)) {
			return GRAY + c + RESET;
		} else {
			return String.valueOf(c); // Trennzeichen etc. bleiben normal
		}
	}

	public static final String RESET = "\u001B[0m";
	public static final String GRAY = "\u001B[90m";
	public static final String RED = "\u001B[31m";
	public static final String BLUE = "\u001B[34m";

	/**
	 * Main Method with Send-, Receive- and Winning/Draw Logic
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		InetAddress server = InetAddress.getByName("223.2.1.102"); // Serveradresse
		int serverPort = 0; // Server-Port
		int clientPort = 6970; // Client-Port
		int startPort = 6971; // Client-Port

		DatagramSocket socketSend = new DatagramSocket(serverPort); // Beliebiger Port für Senden
		DatagramSocket socketReceive = new DatagramSocket(clientPort); // Empfangsport
		DatagramSocket socketStart = new DatagramSocket(startPort); // Empfangsport

		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

		boolean alreadyPlayed = false;


		System.out.print("Create a new game or join existing one? (create/join): ");
		String serverType = stdin.readLine();
		System.out.println();
		System.out.print("Enter Server ID (format: 1234): ");
		String serverID = stdin.readLine();
		System.out.println();
		if (serverType.equals("create")) {
			System.out.print("With bot: (yes/no) ");
			String botType = stdin.readLine();
			if (botType.equals("yes")) {
				serverType = "Create Server_bot";
			} else {
				serverType = "Create Server";
			}

		} else if (serverType.equals("join")) {
			serverType = "Join Server";
		}
		byte[] serverStartMessage = ("main;" + serverType + ";" + serverID).getBytes();
		DatagramPacket serverStartMessagePacket = new DatagramPacket(serverStartMessage, serverStartMessage.length, server, startPort);
		socketStart.send(serverStartMessagePacket);
		try {
			// Empfang vom Server
			byte[] receiveBuffer = new byte[1024000];
			DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			socketReceive.receive(receivePacket);
			String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
			String[] responseParts = response.split(";");
			if (responseParts[0].equals("serverPort")) {
				serverPort = Integer.parseInt(responseParts[1]);
				socketSend = new DatagramSocket(serverPort);
			} else {
				System.out.println("Serverport has not been detected");
				System.exit(0);
			}
		} catch (Exception e) {
			System.out.println(e);
		}


		// Spielstartfrage direkt beim Start
		System.out.print("Start the Game (yes/no): ");
		String startInput = stdin.readLine();
		if (startInput.equals("yes")) {
			System.out.println("Game is starting...");
		} else {
			System.out.println("Thanks for playing!");
			System.err.println("Session was closed!");
			System.exit(0);
		}

		String StartInputWithID = serverID + ";" + startInput;

		// Sende Startnachricht an Server
		byte[] startData = StartInputWithID.getBytes();
		DatagramPacket startPacket = new DatagramPacket(startData, startData.length, server, serverPort);
		socketSend.send(startPacket);

		while (true) {
			// Empfang vom Server
			byte[] receiveBuffer = new byte[1024000];
			DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			socketReceive.receive(receivePacket);
			String response = new String(receivePacket.getData(), 0, receivePacket.getLength());

			// Prüfen, ob Spieler am Zug ist
			if (response.contains("Message: Your Move")) {
				System.out.print("Enter your move: ");
				String userInput = stdin.readLine();
				String msg = serverID + ";" + userInput;
				byte[] sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, server, serverPort);
				socketSend.send(sendPacket);
			} else if (response.contains("Message: X won!")) {
				System.out.print("X Won!");
				if (!alreadyPlayed) {
					System.out.print(" Start the Game again (yes/no) [Again with '_bot' if playing with AI]: ");
				} else {
					System.out.print(" Start the Game again: ");
				}
				startInput = stdin.readLine();
				if (startInput.equals("yes")) {
					System.out.println("Game is starting...");
					alreadyPlayed = true;
				} else if (startInput.equals("yes_bot")) {
					System.out.println("Game is starting...");
					alreadyPlayed = true;
				} else {
					System.out.println("Thanks for playing!");
					System.err.println("Session was closed!");
					System.exit(0);
				}

				// Sende Startnachricht an Server
				startData = startInput.getBytes();
				startPacket = new DatagramPacket(startData, startData.length, server, serverPort);
				socketSend.send(startPacket);
			} else if (response.contains("Message: O won!")) {
				System.out.print("O Won!");
				if (!alreadyPlayed) {
					System.out.print(" Start the Game again (yes/no) [Again with '_bot' if playing with AI]: ");
				} else {
					System.out.print(" Start the Game again: ");
				}
				startInput = stdin.readLine();
				if (startInput.equals("yes")) {
					System.out.println("Game is starting...");
					alreadyPlayed = true;
				} else if (startInput.equals("yes_bot")) {
					System.out.println("Game is starting...");
					alreadyPlayed = true;
				} else {
					System.out.println("Thanks for playing!");
					System.err.println("Session was closed!");
					System.exit(0);
				}

				// Sende Startnachricht an Server
				String msg = serverID + ";" + startInput;
				byte[] sendData = msg.getBytes();
				startPacket = new DatagramPacket(startData, startData.length, server, serverPort);
				socketSend.send(startPacket);
			} else if (response.contains("Message: Draw!")) {
				System.out.print("Draw!");
				if (!alreadyPlayed) {
					System.out.print(" Start the Game again (yes/no) [Again with '_bot' if playing with AI]: ");
				} else {
					System.out.print(" Start the Game again: ");
				}
				startInput = stdin.readLine();
				if (startInput.equals("yes")) {
					System.out.println("Game is starting...");
					alreadyPlayed = true;
				} else if (startInput.equals("yes_bot")) {
					System.out.println("Game is starting...");
					alreadyPlayed = true;
				} else {
					System.out.println("Thanks for playing!");
					System.err.println("Session was closed!");
					System.exit(0);
				}

				// Sende Startnachricht an Server
				String msg = serverID + ";" + startInput;
				byte[] sendData = msg.getBytes();
				startPacket = new DatagramPacket(startData, startData.length, server, serverPort);
				socketSend.send(startPacket);
			} else if (response.startsWith("____")) {
				for (String line : response.split("\n")) {
					// jede Zeile Zeichen für Zeichen einfärben
					StringBuilder coloredLine = new StringBuilder();
					for (char c : line.toCharArray()) {
						coloredLine.append(colorize(c));
					}
					System.out.println(coloredLine);
				}
			} else if (response.equals("Message: botmove")) {
				System.out.println();
				System.out.println("Bot played:");
			} else {
				for (String line : response.split("\n")) {
					System.out.println(line);
				}
			}
		}
	}
}
