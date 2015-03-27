package se2aa4;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;

/**
 * This class is used to represent the current state of the game.
 * It is {@link Observable} so a class can observe it for updates, such as
 * when the state changes.
 */
public class GameStateModel extends Observable {
	private GameState state;
	private PlayerColor editColor;
	private PlayerColor currentPlayer;
	private PlayerColor aiPlayer;
	
	/**
	 * Initialize in the state of GameState.START_STATE.
	 */
	public GameStateModel() {
		state = GameState.START_STATE;
		editColor = PlayerColor.NONE;
		currentPlayer = PlayerColor.NONE;
		aiPlayer = PlayerColor.NONE;
	}
	
	/**
	 * Set a new state and notify observers of the change.
	 * @param state the state to change to
	 */
	public void setState(GameState state) {
		this.state = state;
		// This lets the observers know the state has changed
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Get the current state.
	 * @return the current state
	 */
	public GameState getState() {
		return state;
	}
	
	/**
	 * Update the current edit color. This color is used
	 * when in the edit state and can only be updated when
	 * in this state.
	 * @param color the color to set the current edit color to
	 */
	public void setEditColor(PlayerColor color) {
		// Only allow the color to be changed in edit mode
		if (state == GameState.EDIT_STATE) {
			editColor = color;
		}
	}
	
	/**
	 * Get the current edit color.
	 * @return the current edit color
	 */
	public PlayerColor getEditColor() {
		return editColor;
	}
	
	/**
	 * Sets the current player. Only accepts actual players,
	 * it does nothing is NONE is passed in.
	 * @param player the new current player
	 */
	public void setCurrentPlayer(PlayerColor player) {
		if (player != PlayerColor.NONE) {
			currentPlayer = player;
			// This lets the observers know the state has changed
			setChanged();
			notifyObservers();
		}
	}
	
	/**
	 * Get the current player whose turn it is.
	 * @return the current player
	 */
	public PlayerColor getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Alternate the current player. Goes to the 
	 * other player's turn.
	 */
	public void nextTurn() {
		setCurrentPlayer((currentPlayer == PlayerColor.BLUE) ? PlayerColor.RED : PlayerColor.BLUE);
	}
	
	/**
	 * Set which player is the AI player.
	 * @param player
	 */
	public void setAIPlayer(PlayerColor player) {
			aiPlayer = player;
			// This lets the observers know the state has changed
			setChanged();
			notifyObservers();
	}
	
	/**
	 * If there is an AI player, get which player it is.
	 * @return the AI player or NONE if there isn't one
	 */
	public PlayerColor getAIPlayer() {
		return aiPlayer;
	}
	
	/**
	 * Captures the current state of this object and stores it in a file
	 * for later retrieval.
	 * @param fileName the file to store the state in
	 * @throws IOException
	 */
	public void saveToFile(String fileName) throws IOException {
		DataOutputStream outStream = new DataOutputStream(new FileOutputStream(fileName, false));
		// Can assume that the value of the enum is less than 255 and
		// can be safely stored in a byte
		outStream.writeByte(state.ordinal());
		outStream.writeByte(currentPlayer.ordinal());
		outStream.writeByte(aiPlayer.ordinal());
		
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
		// Read the bytes representing the enums back in
		// and convert them back into enums
		state = GameState.values()[inStream.readByte()];
		currentPlayer = PlayerColor.values()[inStream.readByte()];
		aiPlayer = PlayerColor.values()[inStream.readByte()];
		
		inStream.close();
		
		// This lets the observers know the state has changed
		// since after loading the state could be very different
		setChanged();
		notifyObservers();
	}
}
