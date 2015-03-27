package se2aa4;
/**
 * This enum represents a player color in connect four.
 * The none player can be used to signal an error
 * in situations where it doesn't make sense.
 */
public enum PlayerColor {
	NONE,
	BLUE,
	RED;
	
	/**
	 * Get the opposite player
	 * @return the other player
	 */
	public PlayerColor opponent() {
		if (this == NONE) {
			return NONE;
		}
		return (this == BLUE) ? RED : BLUE;
	}
}
