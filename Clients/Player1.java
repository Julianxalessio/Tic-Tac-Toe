package Clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * <h1>TicTacToe Client-Script</h1>
 * <h2>Script allows to send messages/responses to Server and play TicTacToe as
 * Client</h2>
 * 
 * !Warning: Start the Server before launching the Player-Script!
 * 
 * @version 2.1.0
 * @author Julian Lombardo
 * @author Diego Zwahlen
 * @author Lean Melone
 * 
 */

public class Player1 {

    public Player1() throws UnknownHostException, SocketException {
    }

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
	 * Variable for Server-IP
	 */
	public static InetAddress server; // Serveradresse
    static {
        try {
            server = InetAddress.getByName("223.2.1.102");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * Defines Server-, Client- and Start-Ports and also the Server-ID
	 */
    public static int serverPort = 0; // Server-Port
	public static int clientPort = 6970; // Client-Port
	public static int startPort = 6971; // Client-Port
	public static String serverID = "";

	public static String enteredID = "";
	public static boolean botServer = false;
	public static CountDownLatch latch = new CountDownLatch(1);

	public static DatagramSocket socketSend; // Beliebiger Port für Senden
    static {
        try {
            socketSend = new DatagramSocket(serverPort);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static DatagramSocket socketReceive; // Empfangsport
    static {
        try {
            socketReceive = new DatagramSocket(clientPort);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static DatagramSocket socketStart; // Empfangsport
    static {
        try {
            socketStart = new DatagramSocket(startPort);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

	public static boolean alreadyPlayed = false;

	/**
	 * Sends a message to the server to inform him, that a terminate just happened
	 * @throws IOException
	 */
	public static void sendTerminate() throws IOException {
		String msg = "main;terminate;"+serverID;
		byte[] endData = msg.getBytes();
		DatagramPacket endPacket = new DatagramPacket(endData, endData.length, server, startPort);
		try {
			socketStart.send(endPacket);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (serverPort != 0) {
			msg = serverID + ";terminate";
			endData = msg.getBytes();
			endPacket = new DatagramPacket(endData, endData.length, server, serverPort);
			socketSend.send(endPacket);
			System.err.println("Session was closed!");
		}
	}
	
	public static boolean createJoinServer(String serverType, String id) throws IOException {
		byte[] serverStartMessage = ("main;" + serverType + ";" + id).getBytes();
		DatagramPacket serverStartMessagePacket = new DatagramPacket(serverStartMessage, serverStartMessage.length, server, startPort);
		socketStart.send(serverStartMessagePacket);
		boolean success = true;
		try {
			// Empfang vom Server
			byte[] receiveBuffer = new byte[1024000];
			DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			socketReceive.receive(receivePacket);
			String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
			String[] responseParts = response.split(";");
			if (responseParts[0].equals("Server Full")) {
				success = false;
			} else if (responseParts[1].equals("Server Exists")) {
				success = false;
			} else if (responseParts[1].equals("Server created")) {
				success = true;
				serverID = id;
			} else if (responseParts[0].equals("serverport")) {
				serverPort = Integer.parseInt(responseParts[2]);
				success = true;
				serverID = id;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return success;
	}

	public static void getTerminate() {
		System.err.println("Session was closed by the Server!");
		Player_GUI.resetApplication();
	}
	public static String[] waitForMessage(String p1, String p2, String p3) throws IOException {
		String[] responseParts = null;
		try {
			while (true){
				byte[] receiveBuffer = new byte[1024000];
				DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
				socketReceive.receive(receivePacket);
				String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
				responseParts = response.split(";");
				if (responseParts[0].equals(p1)) {
					responseParts[0] = "serverport";
					serverPort = Integer.parseInt(responseParts[2]);
					return responseParts;
				} else if (responseParts[0].equals("terminate")) {
					getTerminate();
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return responseParts;
	}
	/**
	 * Main Method with Send-, Receive-, Winning/Draw- and Error-Logic
	 * @param args
	 * @throws Exception
	 */
	public static void init() throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                sendTerminate();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
		latch.countDown();
	}
/* 
	while (true) {
		if (!gui.frame.isVisible()) {
			return; // Beendet main, wenn das Fenster geschlossen wurde
		}
		while (true) {

			// Spielstartfrage direkt beim Start
			System.out.print("Start the Game (yes/no): ");
			String startInput = stdin.readLine();
			if (startInput.equals("yes")) {
				System.out.println("Game is starting...");
			} else {
				System.out.println("Thanks for playing!");
				String msg = "main;terminate;"+serverID;
				byte[] endData = msg.getBytes();
				DatagramPacket endPacket = new DatagramPacket(endData, endData.length, server, startPort);
				socketStart.send(endPacket);
				msg = serverID + ";terminate";
				endData = msg.getBytes();
				endPacket = new DatagramPacket(endData, endData.length, server, serverPort);

				socketSend.send(endPacket);
				System.err.println("Session was closed!");
				break;
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
				} else if (response.contains("Server not existing")) {
					System.err.println("Server not existing...");
					break;
				} else if (response.contains("Message: X won!")) {
					System.out.print("X Won!");
					if (!alreadyPlayed) {
						System.out.print(" Start the Game again (yes/no): ");
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
					}else {
						System.out.println("Thanks for playing!");
						String msg = "main;terminate;"+serverID;
						byte[] endData = msg.getBytes();
						DatagramPacket endPacket = new DatagramPacket(endData, endData.length, server, startPort);
						socketStart.send(endPacket);
						msg = serverID + ";terminate";
						endData = msg.getBytes();
						endPacket = new DatagramPacket(endData, endData.length, server, serverPort);

						socketSend.send(endPacket);
						System.err.println("Session was closed!");
						break;
					}

					// Sende Startnachricht an Server
					String msg = serverID + ";" + startInput;
					byte[] sendData = msg.getBytes();
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
					} else if (startInput.equals("yes_bot")) {
						System.out.println("Game is starting...");
						alreadyPlayed = true;
					}else {
						System.out.println("Thanks for playing!");
						String msg = "main;terminate;"+serverID;
						byte[] endData = msg.getBytes();
						DatagramPacket endPacket = new DatagramPacket(endData, endData.length, server, startPort);
						socketStart.send(endPacket);
						msg = serverID + ";terminate";
						endData = msg.getBytes();
						endPacket = new DatagramPacket(endData, endData.length, server, serverPort);

						socketSend.send(endPacket);
						System.err.println("Session was closed!");
						break;
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
					} else if (startInput.equals("yes_bot")) {
						System.out.println("Game is starting...");
						alreadyPlayed = true;
					}else {
						System.out.println("Thanks for playing!");
						String msg = "main;terminate;"+serverID;
						byte[] endData = msg.getBytes();
						DatagramPacket endPacket = new DatagramPacket(endData, endData.length, server, startPort);
						socketStart.send(endPacket);
						msg = serverID + ";terminate";
						endData = msg.getBytes();
						endPacket = new DatagramPacket(endData, endData.length, server, serverPort);

						socketSend.send(endPacket);
						System.err.println("Session was closed!");
						break;
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
				} else if (response.contains("terminate")) {
					break;
				} else {
					for (String line : response.split("\n")) {
						System.out.println(line);
					}
				}
			}
		}
	}
	}
	*/
}
