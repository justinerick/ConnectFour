package se2aa4;
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
	
	/**
	 * Initialize in the state of GameState.START_STATE.
	 */
	public GameStateModel() {
		state = GameState.START_STATE;
		editColor = PlayerColor.NONE;
		currentPlayer = PlayerColor.NONE;
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
	 * it does nothing is NONE is passed in. Also the current
	 * state needs to be the PLAY_STATE.
	 * @param player the new current player
	 */
	public void setCurrentPlayer(PlayerColor player) {
		if (player != PlayerColor.NONE && state == GameState.PLAY_STATE) {
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
}
