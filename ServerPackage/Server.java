package ServerPackage;

import java.io.IOException;
import java.net.*;

/**
 * <h1>Server</h1>
 * <h6>This is the Server for the Tic Tac Toe Game</h6>
 * <h6><u>!!!Attention the IPs must be set to the IPs of the Clients!!!</u></h6>
 *
 * @version 1.0
 * @author Julian Lombardo
 * @author Diego Zwahlen
 * @author Lean Melone
 */
public class Server {
    public static boolean playing = false;
    // Board als Array
    public static char[] board = {
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9'
    };

    public static boolean botPlaying = false;

    public static InetAddress activePlayer;

    public static boolean player1Ready = false, player2Ready = false;
    public String player1IP;
    public String player2IP;
    public DatagramSocket socket;
    public String serverId;

    /**
     * Contructor for Server
     * @throws Exception
     */
    public Server(String player1IP, String player2IP, DatagramSocket socket, String serverID) {
        this.player1IP = player1IP;
        this.player2IP = player2IP;
        this.socket = socket;
        this.serverId = serverID;
    }
    //      ------------------------Variables-------------------------


    public void startServer() throws Exception {
        int random = (int)(Math.random() * 2) + 1;
        boolean currentPlayer = false; // false = player1 & true = player2
        if (random == 1) currentPlayer = false;
        else currentPlayer = true;

        BotClass bot = new BotClass();
        InetAddress player1 = InetAddress.getByName(player1IP);
        InetAddress player2 = InetAddress.getByName(player2IP);

        //      ------------------------Game-------------------------
        System.out.println("TicTacToe Server ready");
        while (true) {
            while (!playing) {
                botPlaying = false;

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength()).trim();
                System.out.println("Received: " + msg);
                String[] msgParts = msg.split(";");



                InetAddress sender = packet.getAddress();
                System.out.println(serverId + msgParts[0]);
                if (msgParts[0].equals(serverId)) {
                    System.out.println("Reached serverid");

                    if (sender.equals(player1) && msgParts[1].equals("yes")) {
                        player1Ready = true;
                    } else if (sender.equals(player2) && msg.equals("yes")) {
                        player2Ready = true;
                    } else if (sender.equals(player2) && msg.equals("yes_bot")) {
                        activePlayer = player2;
                        playing = true;
                        botPlaying = true;
                        botPlaying = true;
                        String stringBoard = boardToString(board);
                        sendMessageToPlayer(activePlayer, socket, stringBoard);
                        break;
                    } else if (sender.equals(player1) && msg.equals("yes_bot")) {
                        activePlayer = player1;
                        playing = true;
                        botPlaying = true;
                        String stringBoard = boardToString(board);
                        sendMessageToPlayer(activePlayer, socket, stringBoard);
                        break;
                    }
                }
                if (player1Ready && player2Ready) {
                    playing = true;
                    sendCurrentBoard(board, player1, player2, socket);
                    if (currentPlayer == false) {
                        sendMessageToPlayer(player1, socket, "Message: Your Move");
                    } else if (currentPlayer == true) {
                        sendMessageToPlayer(player2, socket, "Message: Your Move");
                    }
                    System.out.println("Game started");
                    break; // <-- important
                }
            }
            /**
             * Normal Gamemode if playing is true and botPlaying is false
             */
            while (playing && !botPlaying) {
                player1Ready = false;
                player2Ready = false;
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength()).trim();
                String[] msgParts = msg.split(";");


                InetAddress sender = packet.getAddress();

                if (msgParts[0].equals(serverId)) {

                    // Spieler anhand der Absenderadresse bestimmen und Zeichen korrekt ändern
                    char playerSymbol = sender.equals(player1) ? 'X' : 'O';

                    //Makes sure that the move comes from the right player
                    if ((currentPlayer && sender.equals(player2)) || (!currentPlayer && sender.equals(player1))) {
                        int move = Integer.parseInt(msg);
                        // nur setzen, wenn Feld frei
                        if (move >= 1 && move <= 9) {
                            if (board[move - 1] != 'X' && board[move - 1] != 'O') {

                                board[move - 1] = playerSymbol;
                                sendCurrentBoard(board, player1, player2, socket);
                                int fullCounter = 0;
                                for (int i = 0; i < 9; i++) {
                                    if (board[i] == 'X' || board[i] == 'O') {
                                        fullCounter++;
                                    }
                                }
                                currentPlayer = !currentPlayer;
                                if (checkWin(board, 'X')) {
                                    sendMessageToPlayer(player1, socket, "Message: X won!");
                                    sendMessageToPlayer(player2, socket, "Message: X won!");
                                    playing = false;
                                    for (int i = 1; i <= 9; i++) {
                                        board[i - 1] = (char)('0' + i); // '0' hat den Wert 48
                                    }
                                    break;
                                } else if (checkWin(board, 'O')) {
                                    sendMessageToPlayer(player1, socket, "Message: O won!");
                                    sendMessageToPlayer(player2, socket, "Message: O won!");
                                    playing = false;
                                    for (int i = 1; i <= 9; i++) {
                                        board[i - 1] = (char)('0' + i); // '0' hat den Wert 48
                                    }
                                    break;

                                } else if (fullCounter == 9 && !checkWin(board, 'X') && !checkWin(board, 'O')) {
                                    sendMessageToPlayer(player1, socket, "Message: Draw!");
                                    sendMessageToPlayer(player2, socket, "Message: Draw!");
                                    playing = false;
                                    for (int i = 1; i <= 9; i++) {
                                        board[i - 1] = (char)('0' + i); // '0' hat den Wert 48
                                    }
                                    break;
                                }

                                if (currentPlayer == false) {
                                    sendMessageToPlayer(player1, socket, "Message: Your Move");
                                } else if (currentPlayer == true) {
                                    sendMessageToPlayer(player2, socket, "Message: Your Move");
                                }
                            } else {
                                System.out.println("Feld " + move + " ist schon belegt!");
                                if (currentPlayer == false) {
                                    sendMessageToPlayer(player1, socket, "Message: Dieses Feld ist bereits belegt!");
                                } else if (currentPlayer == true) {
                                    sendMessageToPlayer(player2, socket, "Message: Dieses Feld ist bereits belegt!");
                                }
                            }
                        } else {
                            System.out.println("Kein legaler Zug!");
                            String message = "Dies ist kein legaler Zug";
                            if (currentPlayer == false) {
                                sendMessageToPlayer(player1, socket, message);
                            } else if (currentPlayer == true) {
                                sendMessageToPlayer(player2, socket, message);
                            }
                        }
                    } else {
                        System.out.println("Wrong Player");
                        String message = "Der andere Spieler ist am Zug!";
                        if (currentPlayer == true) {
                            sendMessageToPlayer(player1, socket, message);
                        } else if (currentPlayer == false) {
                            sendMessageToPlayer(player2, socket, message);
                        }
                    }
                }
            }
            /**
             * Botgamemode if playing and botPlaying are both true
             */
            while (playing && botPlaying) {
                player1Ready = false;
                player2Ready = false;
                byte[] buffer = new byte[1024];
                String msg;
                int move;


                //Sends the Message to the player, that it is his turn
                if (currentPlayer == false) {
                    sendMessageToPlayer(activePlayer, socket, "Message: Your Move");
                }


                //If the currentPlayer is true (bot), than it makes the botmove and sends it to the player
                if (currentPlayer) {
                    currentPlayer = false;
                    msg = bot.calculateMove(board);
                    move = Integer.parseInt(msg);

                    // nur setzen, wenn Feld frei
                    if (move >= 1 && move <= 9) {
                        if (board[move - 1] != 'X' && board[move - 1] != 'O') {

                            board[move - 1] = 'O';
                            String stringBoard = boardToString(board);
                            sendMessageToPlayer(activePlayer, socket, "Message: botmove");
                            sendMessageToPlayer(activePlayer, socket, stringBoard);
                            int fullCounter = 0;
                            for (int i = 0; i < 9; i++) {
                                if (board[i] == 'X' || board[i] == 'O') {
                                    fullCounter++;
                                }
                            }
                            win(activePlayer, socket, fullCounter);
                        }
                    }
                }
                //If the currentPlayer is false (activePlayer), make the player do a move
                else if (!currentPlayer) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    InetAddress sender = packet.getAddress();

                    msg = new String(packet.getData(), 0, packet.getLength()).trim();
                    System.out.println("Received: " + msg);
                    String[] msgParts = msg.split(";");
                    if (msgParts[0].equals(serverId)) {

                        //Makes sure that the move comes from the player1
                        if ((!currentPlayer && sender.equals(activePlayer))) {
                            move = Integer.parseInt(msg);

                            // nur setzen, wenn Feld frei
                            if (move >= 1 && move <= 9) {
                                if (board[move - 1] != 'X' && board[move - 1] != 'O') {

                                    board[move - 1] = 'X';
                                    String stringBoard = boardToString(board);
                                    sendMessageToPlayer(activePlayer, socket, stringBoard);
                                    int fullCounter = 0;
                                    for (int i = 0; i < 9; i++) {
                                        if (board[i] == 'X' || board[i] == 'O') {
                                            fullCounter++;
                                        }
                                    }
                                    currentPlayer = true;
                                    win(activePlayer, socket, fullCounter);
                                } else {
                                    System.out.println("Feld " + move + " ist schon belegt!");
                                    if (currentPlayer == false) {
                                        sendMessageToPlayer(activePlayer, socket,
                                            "Message: Dieses Feld ist bereits belegt!");
                                    }
                                }
                            } else {
                                System.out.println("Kein legaler Zug!");
                                String message = "Dies ist kein legaler Zug";
                                if (currentPlayer == false) {
                                    sendMessageToPlayer(activePlayer, socket, message);
                                }
                            }
                        } else {
                            System.out.println("Wrong Player");
                            String message = "Der andere Spieler ist am Zug!";
                            if (currentPlayer == true) {
                                sendMessageToPlayer(activePlayer, socket, message);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Handels the different kind of wins/draws
     * @param activePlayer
     * @param socket
     * @param fullCounter
     * @throws IOException
     */
    public static void win(InetAddress activePlayer, DatagramSocket socket, int fullCounter) throws IOException {
        if (checkWin(board, 'X')) {
            sendMessageToPlayer(activePlayer, socket, "Message: X won!");
            playing = false;
            for (int i = 1; i <= 9; i++) {
                board[i - 1] = (char)('0' + i); // '0' hat den Wert 48
            }
        } else if (checkWin(board, 'O')) {
            sendMessageToPlayer(activePlayer, socket, "Message: O won!");
            playing = false;
            for (int i = 1; i <= 9; i++) {
                board[i - 1] = (char)('0' + i); // '0' hat den Wert 48
            }

        } else if (fullCounter == 9 && !checkWin(board, 'X') && !checkWin(board, 'O')) {
            sendMessageToPlayer(activePlayer, socket, "Message: Draw!");
            playing = false;
            for (int i = 1; i <= 9; i++) {
                board[i - 1] = (char)('0' + i); // '0' hat den Wert 48
            }
        }
    }

    /**
     * Transforms the board to a String
     * @param board
     * @return String
     */
    public static String boardToString(char[] board) {
        String boardStateString = "_________\n" + board[0] + " | " + board[1] + " | " + board[2] + "\n" + "--+---+--\n" +
            board[3] + " | " + board[4] + " | " + board[5] + "\n" + "--+---+--\n" + board[6] + " | " + board[7] +
            " | " + board[8] + "\n";
        return boardStateString;
    }

    /**
     * Sends current Board to all Players
     * @param board
     * @param player1
     * @param player2
     * @param socket
     * @throws IOException
     */
    public static void sendCurrentBoard(char[] board, InetAddress player1, InetAddress player2, DatagramSocket socket)
    throws IOException {
        String boardStateString = "_________\n" + board[0] + " | " + board[1] + " | " + board[2] + "\n" + "--+---+--\n" +
            board[3] + " | " + board[4] + " | " + board[5] + "\n" + "--+---+--\n" + board[6] + " | " + board[7] +
            " | " + board[8] + "\n";

        System.out.println(boardStateString);

        byte[] data = boardStateString.getBytes();
        DatagramPacket board1 = new DatagramPacket(data, data.length, player1, 6970);
        DatagramPacket board2 = new DatagramPacket(data, data.length, player2, 6970);
        socket.send(board1);
        socket.send(board2);
    }

    /**
     * Checks for a win on the board
     * @param board
     * @param player
     * @return boolean
     */
    public static boolean checkWin(char[] board, char player) {
        if (board[0] == player && board[1] == player && board[2] == player)
            return true;
        if (board[3] == player && board[4] == player && board[5] == player)
            return true;
        if (board[6] == player && board[7] == player && board[8] == player)
            return true;

        // Spalten
        if (board[0] == player && board[3] == player && board[6] == player)
            return true;
        if (board[1] == player && board[4] == player && board[7] == player)
            return true;
        if (board[2] == player && board[5] == player && board[8] == player)
            return true;

        // Diagonalen
        if (board[0] == player && board[4] == player && board[8] == player)
            return true;
        if (board[2] == player && board[4] == player && board[6] == player)
            return true;

        return false;
    }

    /**
     * Sends Messages to certain Players
     * @param Player
     * @param socket
     * @param msg
     * @throws IOException
     */
    public static void sendMessageToPlayer(InetAddress Player, DatagramSocket socket, String msg) throws IOException {
        byte[] data = msg.getBytes();
        DatagramPacket Message = new DatagramPacket(data, data.length, Player, 6970);
        socket.send(Message);
    }
}