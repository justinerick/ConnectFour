package se2aa4;
import java.util.Observable;

/**
 * This class contains data and methods regarding the actual game board itself and any game pieces on it.
 */
public class BoardModel extends Observable {
	private static final int GRID_WIDTH = 7;
	private static final int GRID_HEIGHT = 6;
	
	private PlayerColor[][] pieceGrid;
	
	/**
	 * Constructor for BoardModel that initializes an empty game board.
	 */
	public BoardModel() {
		pieceGrid = new PlayerColor[GRID_WIDTH][GRID_HEIGHT];
		
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				pieceGrid[x][y] = PlayerColor.NONE;
			}
		}
	}

	/**
	 * Find how wide the board is in terms of game piece spaces.
	 * @return the width of the board
	 */
	public int getGridWidth() {
		return GRID_WIDTH;
	}

	/**
	 * Find how tall the board is in terms of game piece spaces.
	 * @return the height of the board
	 */
	public int getGridHeight() {
		return GRID_HEIGHT;
	}

	/**
	 * Set the given position on the board to the given colour.
	 * @param position the position on the board to change the colour of
	 * @param color the colour to set that position to
	 */
	public void setGridPiece(Vector2D position, PlayerColor color) {
		pieceGrid[position.x][position.y] = color;
		setChanged();
		notifyObservers();
	}

	/**
	 * Find what colour piece, if any, is at the given spot on the board
	 * @param position the position on the board to check the colour of
	 * @return return the colour of of that position
	 */
	public PlayerColor getGridPiece(Vector2D position) {
		return pieceGrid[position.x][position.y];
	}
	
	/**
	 * Finds all error positions, which are game pieces floating in mid-air.
	 * @return an array of error positions or null if there are none
	 */
	public Vector2D[] getErrorPositions() {
		
		//Find the amount of error positions
		int errorAmt = 0;
		for (int x = 0; x < GRID_WIDTH; x++)
			for (int y = 0; y < GRID_HEIGHT-1; y++)
				if (pieceGrid[x][y] != PlayerColor.NONE && pieceGrid[x][y+1] == PlayerColor.NONE)
					errorAmt++;
		
		//Return null if no error positions were found
		if (errorAmt == 0)
			return null;
		
		//Put all found error positions in an array for returning
		Vector2D[] errorPos = new Vector2D[errorAmt];
		int i = 0;
		for (int x = 0; x < GRID_WIDTH; x++)
			for (int y = 0; y < GRID_HEIGHT-1; y++)
				if (pieceGrid[x][y] != PlayerColor.NONE && pieceGrid[x][y+1] == PlayerColor.NONE)
					errorPos[i++] = new Vector2D(x, y);
		return errorPos;
	}
	
	/**
	 * Determine which player, if any, has a winning connect four.
	 * @return the color of the first player found with a connect four, or NONE if there is no winner
	 */
	public PlayerColor getWinner() {
		
		PlayerColor winner = PlayerColor.NONE;

		//Check the leftmost spots of all possible horizontal connect fours
		for (int x = 0; x < GRID_WIDTH-3; x++)				//All possible starting points
			for (int y = 0; y < GRID_HEIGHT; y++)
				if (pieceGrid[x][y] != PlayerColor.NONE){	//Check for a red or blue piece in that spot
					winner = pieceGrid[x][y];				//Use 'winner' to hold the colour of this potential win
					for (int i = 1; i < 4; i++)				//Check if the other 3 spots needed are the same colour
						if (pieceGrid[x+i][y] != winner){
							winner = PlayerColor.NONE;		//If not set 'winner' back to none and stop checking this one
							break;
						}
					if (winner != PlayerColor.NONE)			 
						return winner;						//If a connect four was found, return the color of the winner
				}
		
		//Check the top spots of all possible vertical connect fours
		for (int x = 0; x < GRID_WIDTH; x++)
			for (int y = 0; y < GRID_HEIGHT-3; y++)
				if (pieceGrid[x][y] != PlayerColor.NONE){
					winner = pieceGrid[x][y];
					for (int i = 1; i < 4; i++)
						if (pieceGrid[x][y+i] != winner){
							winner = PlayerColor.NONE;
							break;
						}
					if (winner != PlayerColor.NONE)
						return winner;
				}
		
		//Check the top-left spots of all possible "\"-diagonal connect fours
		for (int x = 0; x < GRID_WIDTH-3; x++)
			for (int y = 0; y < GRID_HEIGHT-3; y++)
				if (pieceGrid[x][y] != PlayerColor.NONE){
					winner = pieceGrid[x][y];
					for (int i = 1; i < 4; i++)
						if (pieceGrid[x+i][y+i] != winner){
							winner = PlayerColor.NONE;
							break;
						}	
					if (winner != PlayerColor.NONE)
						return winner;
				}
		
		//Check the top-right of all possible "/"-diagonal connect fours
		for (int x = 3; x < GRID_WIDTH; x++)
			for (int y = 0; y < GRID_HEIGHT-3; y++)
				if (pieceGrid[x][y] != PlayerColor.NONE){
					winner = pieceGrid[x][y];
					for (int i = 1; i < 4; i++)
						if (pieceGrid[x-i][y+i] != winner){
							winner = PlayerColor.NONE;
							break;
						}
					if (winner != PlayerColor.NONE)
						return winner;
				}
		return winner;
	}

	/**
	 * Counts the number of pieces of a given player.
	 * @param colour which player to count the number of pieces for
	 * @return the number of pieces found for that player
	 */
	public int getPieceCount(PlayerColor color) {
		int count = 0;
		
		for (int x = 0; x < GRID_WIDTH; x++)
			for (int y = 0; y < GRID_HEIGHT; y++)
				if (pieceGrid[x][y] == color)
					count++;
		
		return count; 
	}

	/**
	 * Determines which player, if any, has more game pieces on the board
	 * @return which colour has more pieces, or NONE if they both have the same amount
	 */
	public PlayerColor whoHasMorePieces(){
		if (getPieceCount(PlayerColor.RED) > getPieceCount(PlayerColor.BLUE))
			return PlayerColor.RED;
		else if (getPieceCount(PlayerColor.RED) < getPieceCount(PlayerColor.BLUE))
			return PlayerColor.BLUE;
		else
			return PlayerColor.NONE;
	}
	
	/**
	 * Determines which player, if any, has too many game pieces compared to the other player
	 * @return which colour has more pieces by at least 2, or NONE if the difference is less than 2
	 */
	public PlayerColor getErrorColor(){
		if (Math.abs(getPieceCount(PlayerColor.RED) - getPieceCount(PlayerColor.BLUE)) > 1)
			return whoHasMorePieces();
		else
			return PlayerColor.NONE;
	}
	
	/**
	 * Determines which player should start, depending on the pieces already on the board 
	 * @return which colour should start, or NONE if there's an error regarding the number of pieces of each colour
	 */
	public PlayerColor getStartPlayer(){
		if (getErrorColor() == PlayerColor.NONE)	//If there's an error regarding the number of pieces of each colour, stop and return NONE
			switch (whoHasMorePieces()){			
			case RED: return PlayerColor.BLUE;		//If there's no error, and one colour has more pieces than the other, return the other colour
			case BLUE: return PlayerColor.RED;		
			case NONE: 
				if (Math.round(Math.random()) == 1)	//If there's the same number of red and blue pieces, randomly return either red or blue
					return PlayerColor.RED;
				else
					return PlayerColor.BLUE;
			}
		return PlayerColor.NONE;
	}
	
	//temporary test method
	public static void main(String[] args){
		BoardModel b = new BoardModel();
		for(int i = 0; i < 4; i++){
			b.setGridPiece(new Vector2D(0+i,2+i), PlayerColor.RED);
			b.setGridPiece(new Vector2D(5,2+i), PlayerColor.RED);
		}
		System.out.println(b.getWinner());
	}
}

