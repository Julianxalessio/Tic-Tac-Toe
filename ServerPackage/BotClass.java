package ServerPackage;
/**
 * <h1>TicTacToe BotCreator-Script</h1>
 * <h6>Takes the move from the server and gives it to a new bot</h6>
 *
 * @version 2.0.1
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

        //New Bot
		BotNetwork bot = new BotNetwork();
        //Gets the best move from the bot
		int bestMoveInt = bot.getBestMove(board) + 1; // Gibt Index 0-8 zurück
		String bestMove = Integer.toString(bestMoveInt);
        //Gives the move to the server
		return bestMove;
	}
}
