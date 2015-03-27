package se2aa4;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
		reset();
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
	 * Set the given position on the board to the given color.
	 * @param position the position on the board to change the color of
	 * @param color the color to set that position to
	 */
	public void setGridPiece(Position position, PlayerColor color) {
		pieceGrid[position.x][position.y] = color;
		setChanged();
		notifyObservers();
	}

	/**
	 * Find what color piece, if any, is at the given spot on the board
	 * @param position the position on the board to check the color of
	 * @return return the color of of that position
	 */
	public PlayerColor getGridPiece(Position position) {
		return pieceGrid[position.x][position.y];
	}
	
	/**
	 * Finds all error positions, which are game pieces floating in mid-air.
	 * @return an array of error positions or null if there are none
	 */
	public Position[] getErrorPositions() {
		
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
		Position[] errorPos = new Position[errorAmt];
		int i = 0;
		for (int x = 0; x < GRID_WIDTH; x++)
			for (int y = 0; y < GRID_HEIGHT-1; y++)
				if (pieceGrid[x][y] != PlayerColor.NONE && pieceGrid[x][y+1] == PlayerColor.NONE)
					errorPos[i++] = new Position(x, y);
		return errorPos;
	}
	
	/**
	 * Determine which player, if any, has a winning connect four.
	 * @return the color of the first player found with a connect four, or NONE if there is no winner
	 */
	public PlayerColor getWinner() {
		Position[] winner = getWinningPieces();
		if(winner[0] == null)
			return PlayerColor.NONE;
		else
			return pieceGrid[winner[0].x][winner[0].y];
	}
	
	/**
	 * Determine the locations of the pieces of the winning connect four, if there is one.
	 * @return an array containing the locations of the four winning pieces, or an empty array (all elements are null) if there is no winner
	 */
	public Position[] getWinningPieces(){
		
		PlayerColor checkColor = PlayerColor.NONE;
		Position[] winPieces = new Position[4];

		//Check the leftmost spots of all possible horizontal connect fours
		for (int x = 0; x < GRID_WIDTH-3; x++)				//All possible starting points
			for (int y = 0; y < GRID_HEIGHT; y++)
				if (pieceGrid[x][y] != PlayerColor.NONE){	//Check for a red or blue piece in that spot
					checkColor = pieceGrid[x][y];				//Use 'checkColor' to hold the color of this potential win
					winPieces[0] = new Position(x, y);
					for (int i = 1; i < 4; i++){				//Check if the other 3 spots needed are the same color
						winPieces[i] = new Position(x+i, y);		//Record the location of each spot checked
						if (pieceGrid[x+i][y] != checkColor){
							checkColor = PlayerColor.NONE;			//If not set 'checkColor' back to none and stop checking this one
							winPieces = new Position[4];			//Clear any positions recorded in 'winPieces'
							break;
						}
					}
					if (checkColor != PlayerColor.NONE)			 
						return winPieces;						//If a connect four was found, return the color of the checkColor
				}
		
		//Check the top spots of all possible vertical connect fours
		for (int x = 0; x < GRID_WIDTH; x++)
			for (int y = 0; y < GRID_HEIGHT-3; y++)
				if (pieceGrid[x][y] != PlayerColor.NONE){
					checkColor = pieceGrid[x][y];
					winPieces[0] = new Position(x, y);
					for (int i = 1; i < 4; i++){
						winPieces[i] = new Position(x, y+i);
						if (pieceGrid[x][y+i] != checkColor){
							checkColor = PlayerColor.NONE;
							winPieces = new Position[4];
							break;
						}
					}
					if (checkColor != PlayerColor.NONE)
						return winPieces;
				}
		
		//Check the top-left spots of all possible "\"-diagonal connect fours
		for (int x = 0; x < GRID_WIDTH-3; x++)
			for (int y = 0; y < GRID_HEIGHT-3; y++)
				if (pieceGrid[x][y] != PlayerColor.NONE){
					checkColor = pieceGrid[x][y];
					winPieces[0] = new Position(x, y);
					for (int i = 1; i < 4; i++){
						winPieces[i] = new Position(x+i, y+i);
						if (pieceGrid[x+i][y+i] != checkColor){
							checkColor = PlayerColor.NONE;
							winPieces = new Position[4];
							break;
						}	
					}
					if (checkColor != PlayerColor.NONE)
						return winPieces;
				}
		
		//Check the top-right of all possible "/"-diagonal connect fours
		for (int x = 3; x < GRID_WIDTH; x++)
			for (int y = 0; y < GRID_HEIGHT-3; y++)
				if (pieceGrid[x][y] != PlayerColor.NONE){
					checkColor = pieceGrid[x][y];
					winPieces[0] = new Position(x, y);
					for (int i = 1; i < 4; i++){
						winPieces[i] = new Position(x-i, y+i);
						if (pieceGrid[x-i][y+i] != checkColor){
							checkColor = PlayerColor.NONE;
							winPieces = new Position[4];
							break;
						}
					}
					if (checkColor != PlayerColor.NONE)
						return winPieces;
				}
		return winPieces;
	}

	/**
	 * Counts the number of pieces of a given player.
	 * @param color which player to count the number of pieces for
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
	 * @return which color has more pieces, or NONE if they both have the same amount
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
	 * @return which color has more pieces by at least 2, or NONE if the difference is less than 2
	 */
	public PlayerColor getErrorColor(){
		if (Math.abs(getPieceCount(PlayerColor.RED) - getPieceCount(PlayerColor.BLUE)) > 1)
			return whoHasMorePieces();
		else
			return PlayerColor.NONE;
	}
	
	/**
	 * Determines which player should start, depending on the pieces already on the board 
	 * @return which color should start, or NONE if there's an error regarding the number of pieces of each color
	 */
	public PlayerColor getStartPlayer(){
		if (getErrorColor() == PlayerColor.NONE)	//If there's an error regarding the number of pieces of each color, stop and return NONE
			switch (whoHasMorePieces()){			
			case RED: return PlayerColor.BLUE;		//If there's no error, and one color has more pieces than the other, return the other color
			case BLUE: return PlayerColor.RED;		
			case NONE: 
				if (Math.round(Math.random()) == 1)	//If there's the same number of red and blue pieces, randomly return either red or blue
					return PlayerColor.RED;
				else
					return PlayerColor.BLUE;
			}
		return PlayerColor.NONE;
	}
	
	/**
	 * Drops a piece from the top of the board and lets it fall to the lowest
	 * available position. Returns true if there was room. Doesn't notify observers.
	 * @param column the column to drop the piece into
	 * @param color the color of the piece to drop
	 * @return true if the piece fit and false if the column is full
	 */
	private boolean dropPiece(int column, PlayerColor color) {
		boolean success = false;
		// This works by trying every spot starting from the bottom
		// and placing the piece in the first empty spot it finds
		for (int row = GRID_HEIGHT - 1; row >= 0; row--) {
			if (pieceGrid[column][row] == PlayerColor.NONE) {
				pieceGrid[column][row] = color;
				success = true;
				break;
			}
		}
		
		return success;
	}
	
	/**
	 * Drops a piece from the top of the board and lets it fall to the lowest
	 * available position. Returns true if there was room. Notifies observers of new state.
	 * @param column the column to drop the piece into
	 * @param color the color of the piece to drop
	 * @return true if the piece fit and false if the column is full
	 */
	public boolean doMove(int column, PlayerColor color) {
		boolean success = dropPiece(column, color);
		if (success) {
			setChanged();
			notifyObservers();
		}
		
		return success;
	}
	
	/**
	 * Drops a piece from the top of the board and lets it fall to the lowest
	 * available position. Returns true if there was room. Does not notify observers.
	 * @param column the column to drop the piece into
	 * @param color the color of the piece to drop
	 * @return true if the piece fit and false if the column is full
	 */
	public boolean doTemporaryMove(int column, PlayerColor color) {
		return dropPiece(column, color);
	}
	
	/**
	 * Undoes a previous temporary move by removing a piece.
	 * Does not notify observers of change.
	 * @param column the column of the move to undo
	 */
	public void undoTemporaryMove(int column) {
		// This works by trying every spot starting from the top
		// and removing the first piece it finds
		for (int row = 0; row < GRID_HEIGHT; row++) {
			if (pieceGrid[column][row] != PlayerColor.NONE) {
				pieceGrid[column][row] = PlayerColor.NONE;
				break;
			}
		}
	}
	
	/**
	 * Captures the current state of this object and stores it in a file
	 * for later retrieval.
	 * @param fileName the file to store the state in
	 * @throws IOException
	 */
	public void saveToFile(String fileName) throws IOException {
		DataOutputStream outStream = new DataOutputStream(new FileOutputStream(fileName, false));
		// There are less than 255 colors for the player to be
		// so it should be safe to store it in a byte
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				outStream.writeByte(pieceGrid[x][y].ordinal());
			}
		}
		
		outStream.close();
	}
	
	/**
	 * Reloads the state of this object from a previously saved state
	 * stored in the specified file.
	 * @param fileName the file to load the state in
	 * @throws IOException
	 */
	public void loadFromFile(String fileName) throws IOException {
		DataInputStream inStream = new DataInputStream(new FileInputStream(fileName));
		// Load the stored bytes back in and convert them
		// back into enums
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				pieceGrid[x][y] = PlayerColor.values()[inStream.readByte()];
			}
		}
		
		inStream.close();
		
		// This lets the observers know the state has changed
		// since after loading the state could be very different
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Resets the state of this object to the default state
	 */
	public void reset() {
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				pieceGrid[x][y] = PlayerColor.NONE;
			}
		}
		// The board has changed since it has been cleared so
		// let the observers know
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Get a copy of this board models state but without
	 * updating the observers.
	 * @return the copy of this board
	 */
	public BoardModel copy() {
		BoardModel newBoard = new BoardModel();
		for (int x = 0; x < GRID_WIDTH; x++) {
			for (int y = 0; y < GRID_HEIGHT; y++) {
				newBoard.pieceGrid[x][y] = pieceGrid[x][y];
			}
		}
		
		return newBoard;
	}
}

