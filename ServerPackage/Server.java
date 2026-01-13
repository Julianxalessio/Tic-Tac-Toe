package ServerPackage;

import java.io.IOException;
import java.net.*;

/**
 * <h1>TicTacToe ServerCreator-Script</h1>
 * <h6>Script allows to send messages/responses to Players and has the game logic for Tic-Tac-Toe</h6>
 *
 * @version 2.1.0
 * @author Julian Lombardo
 * @author Diego Zwahlen
 * @author Lean Melone
 */
public class Server {
    //Playingvariable
    boolean playing = false;
    // Board as Array
    char[] board = {
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
    //Is this currently a botserver
    boolean botPlaying = false;

    //Empty variable for the activePlayer (only in Botgames)
    InetAddress activePlayer;

    //Some of the playerlogic
    boolean player1Ready = false, player2Ready = false;
    String player1IP;
    String player2IP;
    String serverId;
    final DatagramSocket socket;
    boolean botServer = false;

    /**
     * Contructor for Server
     * @throws Exception
     */
    public Server(String player1IP, String player2IP, String serverID, int port, boolean botServer) throws SocketException {
        this.player1IP = player1IP;
        this.player2IP = player2IP;
        this.serverId = serverID;
        this.socket =  new DatagramSocket(port);
        this.botServer = botServer;
    }

    /**
     * This is the method, which is started by the thread in the MainGame, to start the server
     * @throws NumberFormatException
     * @throws Exception
     */
    public void startServer() throws NumberFormatException, Exception {
        //Decides which player starts
        int random = (int)(Math.random() * 2) + 1;
        boolean currentPlayer = false; // false = player1 & true = player2
        if (random == 1) currentPlayer = false;
        else currentPlayer = true;

        //Defines the different players and bots
        BotClass bot = new BotClass();
        InetAddress player1 = InetAddress.getByName(player1IP);
        InetAddress player2;
        if (player2IP.equals("bot")){
            player2 = InetAddress.getByName("0.0.0.0");
        } else {
            player2 = InetAddress.getByName(player2IP);
        }

        //------------------------Game-------------------------
        System.out.println("TicTacToe Server ready with id: " + serverId);
        /**
         * While the Server is active
         */
        while (true) {
            /**
             * On the start this while-Loop will start and wait for playerinputs. If both the players have accepted with yes, he will go on to the next while-loop
             */
            while (!playing) {
                try {
                    System.out.println(botServer);
                    //Packetreceiver
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength()).trim();
                    System.out.println("Received: " + msg);
                    String[] msgParts = msg.split(";");
                    //Splits it into different parts
                    msgParts[1] = msgParts[1].trim();
                    InetAddress sender = packet.getAddress();

                    /**
                     * If the packet belongs to this server, it will accept it.
                     */
                    if (msgParts[0].equals(serverId)) {
                        //If the sender is Player1 and the message is yes, then it will set player1Ready = true
                        if (!botServer && sender.getHostAddress().equals(player1.getHostAddress()) && msgParts[1].equals("yes")) {
                            player1Ready = true;
                        }
                        //If the sender is Player2 and the message is yes, then it will set player2Ready = true
                        else if (!botServer && sender.getHostAddress().equals(player2.getHostAddress()) && msgParts[1].equals("yes")) {
                            player2Ready = true;
                        }
                        //If one of the players stop the application the server will stop and send a message to the players
                        else if (msgParts[1].equals("terminate")){
                            sendMessageToPlayer(player1, socket, "terminate");
                            sendMessageToPlayer(player2, socket, "terminate");
                            break;
                        }
                        else if (botServer && sender.getHostAddress().equals(player2.getHostAddress()) && msgParts[1].equals("yes")) {
                            activePlayer = player2;
                            playing = true;
                            botPlaying = true;
                            String stringBoard = boardToString(board);
                            sendMessageToPlayer(activePlayer, socket, stringBoard);
                            break;
                        } else if (botServer && sender.getHostAddress().equals(player1.getHostAddress()) && msgParts[1].equals("yes")) {
                            activePlayer = player1;
                            playing = true;
                            botPlaying = true;
                            String stringBoard = boardToString(board);
                            sendMessageToPlayer(activePlayer, socket, stringBoard);
                            break;
                        }
                    }
                    //If both the players are ready
                    if (player1Ready && player2Ready) {
                        //Start the next while-Loop after this one reached the break on line 139
                        playing = true;
                        //Sends the startingboard to both Players
                        sendCurrentBoard(board, player1, player2, socket);
                        //Sends the message to the player who starts, that he can insert a move
                        if (currentPlayer == false) {
                            sendMessageToPlayer(player1, socket, "Message: Your Move");
                        } else if (currentPlayer == true) {
                            sendMessageToPlayer(player2, socket, "Message: Your Move");
                        }
                        System.out.println("Game started");
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            /**
             * Normal Gamemode if playing is true and botPlaying is false
             */
            while (playing && !botPlaying) {
                //Sets both players Ready = false
                player1Ready = false;
                player2Ready = false;

                //Waits for an input of on of the players
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength()).trim();
                //Splits the message into parts
                String[] msgParts = msg.split(";");
                InetAddress sender = packet.getAddress();

                //If the message is for this server
                if (msgParts[0].equals(serverId)) {
                    // Spieler anhand der Absenderadresse bestimmen und Zeichen korrekt ändern
                    char playerSymbol = sender.equals(player1) ? 'X' : 'O';

                    //Terminates the server and sends it to the players
                    if (msgParts[1].equals("terminate")){
                        sendMessageToPlayer(player1, socket, "terminate");
                        sendMessageToPlayer(player2, socket, "terminate");
                        break;
                    }

                    //Makes sure that the move comes from the right player
                    if ((currentPlayer && sender.equals(player2)) || (!currentPlayer && sender.equals(player1))) {
                        //Transform the move from a String to an int
                        int move = Integer.parseInt(msgParts[1]);

                        // Only make the move if the place on the board is free
                        if (move >= 1 && move <= 9) {
                            if (board[move - 1] != 'X' && board[move - 1] != 'O') {

                                //Places the symbol on the right square
                                board[move - 1] = playerSymbol;

                                //Sends the new board to all players
                                sendCurrentBoard(board, player1, player2, socket);

                                //Looks how many places on the board are still free
                                int fullCounter = 0;
                                for (int i = 0; i < 9; i++) {
                                    if (board[i] == 'X' || board[i] == 'O') {
                                        fullCounter++;
                                    }
                                }
                                //Switches the currentplayer
                                currentPlayer = !currentPlayer;

                                //Checks for a win
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

                                //Sends a message to the player if it is his turn
                                if (currentPlayer == false) {
                                    sendMessageToPlayer(player1, socket, "Message: Your Move");
                                } else if (currentPlayer == true) {
                                    sendMessageToPlayer(player2, socket, "Message: Your Move");
                                }
                            }
                            //Incase the field already has something on it
                            else {
                                System.out.println("Feld " + move + " ist schon belegt!");
                                if (currentPlayer == false) {
                                    sendMessageToPlayer(player1, socket, "Message: Dieses Feld ist bereits belegt!");
                                } else if (currentPlayer == true) {
                                    sendMessageToPlayer(player2, socket, "Message: Dieses Feld ist bereits belegt!");
                                }
                            }
                        }
                        // The move isn't legal
                        else {
                            System.out.println("Kein legaler Zug!");
                            String message = "Dies ist kein legaler Zug";
                            if (currentPlayer == false) {
                                sendMessageToPlayer(player1, socket, message);
                            } else if (currentPlayer == true) {
                                sendMessageToPlayer(player2, socket, message);
                            }
                        }
                    }
                    //If the player whos turn it isn't makes a move
                    else {
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
                            win(activePlayer, fullCounter);
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

                        //Terminates the server and sends it to the player
                        if (msgParts[1].equals("terminate")){
                            sendMessageToPlayer(activePlayer, socket, "terminate");
                            break;
                        }

                        //Makes sure that the move comes from the player
                        if ((!currentPlayer && sender.equals(activePlayer))) {
                            move = Integer.parseInt(msgParts[1]);

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
                                    win(activePlayer, fullCounter);
                                } else {
                                    System.out.println("Feld " + move + " ist schon belegt!");
                                    if (currentPlayer == false) {
                                        sendMessageToPlayer(activePlayer, socket,"Message: Dieses Feld ist bereits belegt!");
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
     *
     * @param activePlayer
     * @param fullCounter
     * @throws IOException
     */
    public void win(InetAddress activePlayer, int fullCounter) throws IOException {
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