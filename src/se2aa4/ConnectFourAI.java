package se2aa4;

/**
 * This class tries to decide what the best move is for
 * a given player. Sometimes it will seem like the AI missed
 * an obvious win but it's because it already knows it is 
 * guaranteed to win later anyways so it might be setting
 * up for a not so obvious win. It also works the other way, 
 * if the AI knows it will lose, it wont even put up a fight 
 * it might just give up and do something that seems unintelligent 
 * but that is only because it knows it is going to lose for sure.
 */
public class ConnectFourAI {
	
	private static final int MAX_SCORE = 10000;
	private static final int MIN_SCORE = -10000;
	
	private final int GRID_WIDTH;
	private final int GRID_HEIGHT;
	
	private int bestMove;
	private BoardModel board;
	
	/**
	 * Create a new AI object
	 * @param board the starting state of the board to consider
	 */
	public ConnectFourAI(BoardModel board) {
		this.board = board;
		GRID_WIDTH = board.getGridWidth();
		GRID_HEIGHT = board.getGridHeight();
	}
	
	/**
	 * Get the best move.
	 * @param player which player's move
	 * @return the column a piece should be dropped into
	 */
	public int getBestMove(PlayerColor player) {
		// Look ahead 7 moves and figure out which one is best using the negamax algorithm
		negamax(7, -1, player);
		return bestMove;
	}
	
	/**
	 * Figure out what the next move should be using the negamax algorithm
	 * @param depth the current depth in the game tree
	 * @param move the move used to get to this node (the column)
	 * @param player the player whose turn it is
	 * @return
	 */
	private int negamax(int depth, int move, PlayerColor player) {
		// The negamax algorithm was based on the one found here
		// http://en.wikipedia.org/wiki/Negamax
		
		PlayerColor winner = board.getWinner();
		
		if (winner == player) {
			// If the current player won, it is the best possible outcome
			return MAX_SCORE;
		} else if (winner == player.opponent()) {
			// If the current player lost, it is the worst possible outcome
			return MIN_SCORE;
		}
		
		// If it is a draw, it is a neutral outcome
		if (board.getPieceCount(PlayerColor.NONE) == 0) {
			return 0;
		}
		
		// If at the maximum depth evaluate this node
		if (depth == 0) {
			return evaluateBoard(player);
		}
		// Find the best value of all child nodes
		int bestValue = MIN_SCORE;
		int bestColumn = 0;
		for (int column = 0; column < GRID_WIDTH; column++) {
			// Use temporary moves to avoid the overhead of updating observers
			if (board.doTemporaryMove(column, player)) {
				int val = -negamax(depth - 1, column, player.opponent());
				if (val >= bestValue) {
					bestValue = val;
					bestColumn = column;
				}
				board.undoTemporaryMove(column);
			}
		}
		
		// The best move overall will be the last time bestMove is set
		// to bestColumn
		bestMove = bestColumn;
		return bestValue;
	}
	
	/**
	 * Evaluate the value of the board from the current player's perspective.
	 * @param player the current player
	 * @return a score with higher values more favorable to the current player
	 */
	private int evaluateBoard(PlayerColor player) {
		// Check each possible connect four and how much progress the player made in
		// getting them
		int totalValue = 0;
		
		// Horizontals
		totalValue += getConnectFoursValue(0, GRID_WIDTH-3, GRID_HEIGHT, 1, 0, player);
		
		// Verticals
		totalValue += getConnectFoursValue(0, GRID_WIDTH, GRID_HEIGHT-3, 0, 1, player);
		
		// \ Diagonals
		totalValue += getConnectFoursValue(0, GRID_WIDTH- 3, GRID_HEIGHT-3, 1, 1, player);
				
		// / Diagonals
		totalValue += getConnectFoursValue(3, GRID_WIDTH, GRID_HEIGHT-3, -1, 1, player);	
		
		return totalValue;
	}
	
	/**
	 * Get the total value of the specified connect fours from
	 * a given players perspective.
	 * @param startX the X value to start at
	 * @param maxX the maximum X value to look at
	 * @param maxY the maximum Y value to look at
	 * @param dx the connect fours will be added in this X direction
	 * @param dy the connect fours will be added in this Y direction
	 * @param player the better the board is for this player, the higher the score will be
	 */
	private int getConnectFoursValue(int startX, int maxX, int maxY, int dx, int dy, PlayerColor player) {
		int totalValue = 0;
		
		// Loop through the specified region
		for (int x = startX; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				// Check a connect four staring at the point x, y
				// and going in the direction of dx, dy
				PlayerColor connectFourPlayer = PlayerColor.NONE;
				int currentValue = 0;
				boolean connectFourValid = true;
				for (int i = 0; i < 4; i++) {
					PlayerColor currentPiece = board.getGridPiece(new Position(x + i * dx, y + i * dy));
					// Make sure the connect four is made out of all the same color
					if (connectFourPlayer == PlayerColor.NONE) {
						connectFourPlayer = currentPiece;
					}
					if (currentPiece != PlayerColor.NONE) {
						if (connectFourPlayer == currentPiece) {
							// For every part of the connect four that contains the current players
							// increase the value
							currentValue++;
						} else {
							// If the connect four contains another color it is useless
							connectFourValid = false;
						}
					}
				}
				
				// If the connect four is valid either increase the score
				// if the connect four belongs to the current player or decrease
				// the score otherwise
				if (connectFourValid) {
					if (connectFourPlayer == player) {
						// The value of a partially completed connect four is
						// approximately proportional to the square of how many pieces
						// are in it because more pieces are a lot better
						totalValue += currentValue*currentValue;
					} else {
						totalValue -= currentValue*currentValue;
					}
				}
			}
		}
		
		return totalValue;
	}
}
