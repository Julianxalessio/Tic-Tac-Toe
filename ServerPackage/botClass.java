package ServerPackage;
/**
 * <h1>TicTacToe Bot-Script</h1>
 * <h2>Script allows to send messages/responses to Server and play TicTacToe</h2>
 * 
 * !Warning: Start the Server before starting the Player-Script!
 * @version 1.0
 * @author Julian Lombardo
 * @author Diego Zwahlen
 * @author Lean Melone
 */

public class BotClass {

    public BotClass() {
    }
    /**
     * Convertes Board-Array to Int
     * @param boardChar
     * @return
     */
	public String calculateMove(char[] boardChar) {
		// Board Repräsentation: 0 = leer, 1 = X, 2 = O
		
		int[] board = new int[9];
        for (int i = 0; i < boardChar.length; i++) {
            char place = boardChar[i];

            if (place != 'X' && place != 'O') {
                board[i] = 0; // leer
            } else if (place == 'X') {
                board[i] = 1;
            } else if (place == 'O') {
                board[i] = 2;
            }
        }
		botNetwork bot = new botNetwork();
		int bestMoveInt = bot.getBestMove(board) + 1; // Gibt Index 0-8 zurück
		String bestMove = Integer.toString(bestMoveInt);
		
		return bestMove;
	}
}
