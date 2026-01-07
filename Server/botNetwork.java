package Server;
import java.util.Random;

/**
 * <h1>Bot</h1>
 * <h6>This is the minmaxed bot which calculates the actual move</h6>
 * 
 * @version 1.0
 * @author Julian Lombardo
 * @author Diego Zwahlen
 * @author Lean Melone
 * @author Claude AI
 */

public class botNetwork {
    
    /** 
     * Gets the best move
     * @param board
     * @return int
     */
    public int getBestMove(int[] board) {
        var start = System.nanoTime();
        float bestEval = -1000;
        var moveEvals = new float[9];
        
        for (int move = 0; move < 9; move++) {
            if (board[move] == 0) { // Leeres Feld
                board[move] = 2; // O spielt (2 = O, 1 = X, 0 = leer)
                float eval = minimax(board, 1, -1000, 1000, false); // O hat gespielt, X ist dran
                moveEvals[move] = eval;
                bestEval = Math.max(eval, bestEval);
                board[move] = 0; // Zug rückgängig machen
            } else {
                moveEvals[move] = -1000;
            }
        }
        
        System.out.printf("Bot Played: Calculated in %.2fms\n", getTime(start));
        
        // Zufällig einen der besten Züge wählen
        Random rand = new Random();
        while (true) {
            int move = rand.nextInt(9);
            if (moveEvals[move] == bestEval)
                return move;
        }
    }

    /** 
     * Gets the time which it needed to perform the move
     * @param start
     * @return float
     */
    static float getTime(long start) {
        return (float) (System.nanoTime() - start) / 1_000_000;
    }

    /** 
     * The minmax algorythm (brain)
     * @param board
     * @param depth
     * @param alpha
     * @param beta
     * @param isMaximizing
     * @return float
     */
    float minimax(int[] board, int depth, float alpha, float beta, boolean isMaximizing) {
        int gameState = checkGameOver(board);
        
        if (gameState != 0) { // Spiel ist vorbei
            if (gameState == 2) return 10 - depth; // O gewinnt
            if (gameState == 1) return depth - 10; // X gewinnt
            return 0; // Unentschieden
        }
        
        if (isMaximizing) { // O spielt (maximiert)
            float maxEval = -1000;
            for (int move = 0; move < 9; move++) {
                if (board[move] == 0) {
                    board[move] = 2; // O spielt
                    maxEval = Math.max(maxEval, minimax(board, depth + 1, alpha, beta, false));
                    board[move] = 0; // Rückgängig machen
                    alpha = Math.max(alpha, maxEval);
                    if (beta <= alpha) break; // Alpha-Beta Pruning
                }
            }
            return maxEval;
        } else { // X spielt (minimiert)
            float minEval = 1000;
            for (int move = 0; move < 9; move++) {
                if (board[move] == 0) {
                    board[move] = 1; // X spielt
                    minEval = Math.min(minEval, minimax(board, depth + 1, alpha, beta, true));
                    board[move] = 0; // Rückgängig machen
                    beta = Math.min(beta, minEval);
                    if (beta <= alpha) break; // Alpha-Beta Pruning
                }
            }
            return minEval;
        }
    }
    
    /** 
     * Wincondition checker
     * @param board
     * @return int
     */
    private int checkGameOver(int[] board) {
        // Gewinnkombinationen prüfen
        int[][] winPatterns = {
            {0,1,2}, {3,4,5}, {6,7,8}, // Reihen
            {0,3,6}, {1,4,7}, {2,5,8}, // Spalten
            {0,4,8}, {2,4,6}           // Diagonalen
        };
        
        for (int[] pattern : winPatterns) {
            if (board[pattern[0]] != 0 && 
                board[pattern[0]] == board[pattern[1]] && 
                board[pattern[1]] == board[pattern[2]]) {
                return board[pattern[0]]; // Gewinner (1=X, 2=O)
            }
        }
        
        // Prüfen ob Brett voll ist (Unentschieden)
        for (int cell : board) {
            if (cell == 0) return 0; // Spiel läuft noch
        }
        return -1; // Unentschieden
    }
}