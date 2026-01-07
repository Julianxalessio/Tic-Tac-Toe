package Clients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @version 1.0
 * @author Julian Lombardo
 * @author Diego Zwahlen
 * @author Lean Melone
 * 
 *         <h1>TicTacToe Player2-Script</h1>
 *         <h2>Script allows to send messages/responses to Server and play
 *         TicTacToe as Player2</h2>
 * 
 *         !Warning: Start the Server before launching the Player-Script!
 */

public class Player2 {

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

		InetAddress server = InetAddress.getByName("192.168.1.94"); // Serveradresse
		int serverPort = 6969; // Server-Port
		int clientPort = 6970; // Client-Port

		DatagramSocket socketSend = new DatagramSocket(serverPort); // Beliebiger Port für Senden
		DatagramSocket socketReceive = new DatagramSocket(clientPort); // Empfangsport

		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		
		boolean alreadyPlayed = false;

		// Spielstartfrage direkt beim Start
		System.out.print("Start the Game (yes/no) [Add '_bot' if playing with AI]: ");
		String startInput = stdin.readLine();
		if (startInput.equals("yes")) {
			System.out.println("Game is starting...");
		} else if (startInput.equals("yes_bot")) {
			System.out.println("Game is starting...");
		} else {
			System.out.println("Thanks for playing!");
			System.err.println("Session was closed!");
			System.exit(0);
		}

		// Sende Startnachricht an Server
		byte[] startData = startInput.getBytes();
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

				byte[] sendData = userInput.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, server, serverPort);
				socketSend.send(sendPacket);
			} else if (response.contains("Message: X won!")) {
				System.out.print("X Won!");
				if (alreadyPlayed == false) {
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
				if (alreadyPlayed == false) {
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
			} else if (response.contains("Message: Draw!")) {
				System.out.print("Draw!");
				if (alreadyPlayed == false) {
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
