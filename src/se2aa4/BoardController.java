package se2aa4;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * This class is responsible for listening to the user input
 * events that the view creates and reacting to them by updating
 * the models. It also listens to the models and updates the
 * view when necessary.
 */
public class BoardController implements ActionListener, Observer {
	private BoardView view;
	private BoardModel boardModel;
	private GameStateModel stateModel;
	private HashMap<GameState, BoardPanels> panelMap;
	
	private static final String stateFileName = "gamestate.bin";
	private static final String boardFileName = "board.bin";
	
	/**
	 * Initializes the board controller and it's associated
	 * view and models. The controller will listen for updates
	 * from the view and model after it is created.
	 */
	public BoardController() {
		setUpPanelMap();
		boardModel = new BoardModel();
		stateModel = new GameStateModel();
		boardModel.addObserver(this);
		stateModel.addObserver(this);
		view = new BoardView(boardModel.getGridWidth(), boardModel.getGridHeight(), this);
	}
	
	/**
	 * This initializes the panelMap. It is used to
	 * identify which panel of the menu should be associated
	 * with each {@link GameState}.
	 */
	private void setUpPanelMap() {
		panelMap = new HashMap<GameState, BoardPanels>();
		panelMap.put(GameState.START_STATE, BoardPanels.START);
		panelMap.put(GameState.EDIT_STATE, BoardPanels.EDIT);
		panelMap.put(GameState.PLAY_STATE, BoardPanels.PLAY);
		panelMap.put(GameState.WIN_STATE, BoardPanels.EMPTY);
		panelMap.put(GameState.DRAW_STATE, BoardPanels.EMPTY);
	}
	
	/**
	 * This method is called when an event on the view happens.
	 * This will be some sort of a user input event and this
	 * method reacts to the user input events by updating the
	 * appropriate models.
	 * @param e the event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Look up the component identifier of this event
		BoardComponentType type = view.lookupComponentType(e.getSource());
		
		// Decide what to do based off of what component it is
		switch (type) {
		case BOARD_BUTTON:
			// If a button on the game board was pressed in the edit state
			// set that position to be the current edit color
			Vector2D buttonPosition = view.lookupButtonPosition(e.getSource());
			if (stateModel.getState() == GameState.EDIT_STATE) {
				boardModel.setGridPiece(buttonPosition, stateModel.getEditColor());
			} else if (stateModel.getState() == GameState.PLAY_STATE) {
				// If the piece was successfully added
				if (boardModel.dropPiece(buttonPosition.x, stateModel.getCurrentPlayer())) {
					// Check if this move is a winning move
					PlayerColor winner = boardModel.getWinner();
					// If there is no winner it is the next player's turn
					if (winner == PlayerColor.NONE)  {
						stateModel.nextTurn();
					} else {
						// If there is a winner go to the win state
						stateModel.setState(GameState.WIN_STATE);
					}
				}
				
				// If there are no more empty spaces and there was no winner, there must be a draw
				if (boardModel.getPieceCount(PlayerColor.NONE) == 0 && stateModel.getState() != GameState.WIN_STATE) {
					stateModel.setState(GameState.DRAW_STATE);
				}
			}
			break;
		case NEW_GAME_BUTTON:
			stateModel.setState(GameState.PLAY_STATE);
			stateModel.setCurrentPlayer(boardModel.getStartPlayer());
			break;
		case EDIT_BUTTON:
			stateModel.setState(GameState.EDIT_STATE);
			view.setTitleLabel("Edit mode");
			break;
		case BLUE_BUTTON:
			stateModel.setEditColor(PlayerColor.BLUE);
			view.setTitleLabel("Blue selected");
			break;
		case RED_BUTTON:
			stateModel.setEditColor(PlayerColor.RED);
			view.setTitleLabel("Red selected");
			break;
		case NONE_BUTTON:
			stateModel.setEditColor(PlayerColor.NONE);
			view.setTitleLabel("None selected");
			break;
		case DONE_BUTTON:
			// If done is pressed while in the edit state
			// this means done editing so try go to the
			// play state
			if (stateModel.getState() == GameState.EDIT_STATE) {
				// Check if the current state is valid and if not report the error
				boolean valid = true;
				String errorMessage = "";
				
				// Make sure the amount of pieces is balanced
				PlayerColor errorPlayer = boardModel.getErrorColor();
				if (errorPlayer != PlayerColor.NONE) {
					errorMessage = "Too many " + errorPlayer.toString() + "S";
					valid = false;
				}
				
				// Make sure there are no winners
				errorPlayer = boardModel.getWinner();
				if (errorPlayer != PlayerColor.NONE) {
					errorMessage = errorPlayer.toString() + " already won";
					valid = false;
				}
				
				// Make sure there are no floating pieces and if there is highlight them
				Vector2D[] errors = boardModel.getErrorPositions();
				if (errors != null) {
					errorMessage = "There are floating pieces";
					for (Vector2D position : errors) {
						view.highlightPiece(position);
					}
					valid = false;
				}
				
				// If valid move to the PLAY_STATE and figure out who should go first
				if (valid) {
					stateModel.setState(GameState.PLAY_STATE);
					stateModel.setCurrentPlayer(boardModel.getStartPlayer());
				} else {
					// Or else an error happened in which case the error should be displayed
					view.setTitleLabel(errorMessage);
				}
			}
			break;
		case LOAD_BUTTON:
			try {
				stateModel.loadFromFile(stateFileName);
				boardModel.loadFromFile(boardFileName);
				view.setStatusLabel("Load successful.");
			} catch (IOException exception) {
				view.setStatusLabel("An error occured while loading or no save exists.");
			}
			break;
		case SAVE_BUTTON:
			try {
				// Save the models to files and if an error happens
				// display an appropriate message
				stateModel.saveToFile(stateFileName);
				boardModel.saveToFile(boardFileName);
				view.setStatusLabel("Save successful.");
			} catch (IOException exception) {
				view.setStatusLabel("An error occured while saving.");
			}
			break;
		default:
			// This should never happen
			break;
		}
	}
	
	/**
	 * This method is called by {@link Observable} classes to
	 * let the controller know that the model has been updated.
	 * @param arg0 the {@link Observable} object that called this method
	 * @param arg1 any parameter the {@link Observable} object passed
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 == boardModel) {
			// If the model that was updated was the board model
			// update the view with the new board model
			view.drawModel(boardModel);
		} else if (arg0 == stateModel) {
			// If the model that was updated was the state model
			// update the view with the new state info
			
			GameState currentState = stateModel.getState();
			if (panelMap.containsKey(currentState)) {
				// If the panelMap contains info regarding which
				// panel should be displayed for this state,
				// display that panel
				view.setCurrentMenu(panelMap.get(currentState));
			}
			
			// If the current state is the PLAY_STATE the current player could
			// have been updated so update the view
			PlayerColor currentPlayer = stateModel.getCurrentPlayer();
			if (currentState == GameState.PLAY_STATE) {
				// If the current player is NONE that is invalid so ignore it
				if (currentPlayer != PlayerColor.NONE) {
					view.setTitleLabel(currentPlayer.toString() + "'s turn");
				}
			} else if (currentState == GameState.WIN_STATE) {
				// If a player won the game display a winning message
				view.setTitleLabel(currentPlayer.toString() + " won!");
			} else if (currentState == GameState.DRAW_STATE) {
				// If there is a draw display the draw message
				view.setTitleLabel("Draw :(");
			}
			
			// Can clear the status if the state changes
			view.setStatusLabel(" ");
		}
	}
	
	/**
	 * The entry point of the application.
	 */
	public static void main(String[] args) {
		// Create a new controller which in turn will
		// start the game
		
		// It's actually used because the constructor sets everything up
		@SuppressWarnings("unused")
		BoardController bc = new BoardController();
	}

}
