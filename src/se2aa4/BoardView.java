package se2aa4;
import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * This class is responsible for the GUI of the
 * connect four game. It sets up the frame and 
 * all other aspects of the display.
 */
public class BoardView {
	private static final int PIECE_SIZE = 64;
	
	private final int BOARD_WIDTH;
	private final int BOARD_HEIGHT;
	
	private JFrame frame;
	private JLabel titleLabel;
	private JLabel statusLabel;
	private JButton[][] pieceGrid;
	private JPanel menuPanel;
	private CardLayout menuLayout;
	
	private Icon emptyPiece;
	private Icon bluePiece;
	private Icon redPiece;
	private Icon emptyPieceHL;
	private Icon bluePieceHL;
	private Icon redPieceHL;
	
	private BoardController controller;
	
	private HashMap<Object, BoardComponentType> componentMap;
	private HashMap<Object, Position> buttonPositionMap;
	
	/**
	 * Initialize the view. When the constructor completes, 
	 * the frame will be created and be getting user input.
	 * @param boardWidth the width of the board in pieces
	 * @param boardHeight the height of the board in pieces
	 * @param controller the controller to send user input updates to
	 */
	public BoardView(int boardWidth, int boardHeight, BoardController controller) {
		BOARD_WIDTH = boardWidth;
		BOARD_HEIGHT = boardHeight;
		
		// Initialized the grid of buttons used to display the board pieces
		pieceGrid = new JButton[BOARD_WIDTH][BOARD_HEIGHT];
		
		this.controller = controller;
		
		// Set up the frame on the proper thread
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setUpFrame();
			}
		});
	}
	
	/**
	 * This method creates the frame for the display
	 * and initializes all of its components.
	 */
	private void setUpFrame() {
		// This method contains ugly GUI code!
		
		componentMap = new HashMap<Object, BoardComponentType>();
		buttonPositionMap = new HashMap<Object, Position>();
		
		emptyPiece = new ImageIcon(createPieceImage(PlayerColor.NONE, Color.BLACK));
		bluePiece = new ImageIcon(createPieceImage(PlayerColor.BLUE, Color.BLACK));
		redPiece = new ImageIcon(createPieceImage(PlayerColor.RED, Color.BLACK));
		emptyPieceHL = new ImageIcon(createPieceImage(PlayerColor.NONE, Color.YELLOW));
		bluePieceHL = new ImageIcon(createPieceImage(PlayerColor.BLUE, Color.YELLOW));
		redPieceHL = new ImageIcon(createPieceImage(PlayerColor.RED, Color.YELLOW));
		
		frame = new JFrame("Connect Four");
		
		JPanel contentPanel = new JPanel();
		frame.setContentPane(contentPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{150,500,150};
		gbl_contentPanel.rowHeights = new int[]{50,50,200};
		contentPanel.setLayout(gbl_contentPanel);
		
		titleLabel = new JLabel("Welcome");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		gbc_titleLabel.gridx = 1;
		gbc_titleLabel.gridy = 0;
		gbc_titleLabel.gridheight = 2;
		gbc_titleLabel.gridwidth = 1;
		contentPanel.add(titleLabel, gbc_titleLabel);
		
		JLabel playerBlueLabel = new JLabel("Blue player");
		playerBlueLabel.setForeground(Color.BLUE);
		playerBlueLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		GridBagConstraints gbc_playerBlueLabel = new GridBagConstraints();
		gbc_playerBlueLabel.gridx = 0;
		gbc_playerBlueLabel.gridy = 1;
		contentPanel.add(playerBlueLabel, gbc_playerBlueLabel);
		
		JLabel playerRedLabel = new JLabel("Red player");
		playerRedLabel.setForeground(Color.RED);
		playerRedLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		GridBagConstraints gbc_playerRedLabel = new GridBagConstraints();
		gbc_playerRedLabel.gridx = 2;
		gbc_playerRedLabel.gridy = 1;
		contentPanel.add(playerRedLabel, gbc_playerRedLabel);
		
		Image blueHolderImage = createHolderImage(Color.BLUE);
		JLabel blueHolder = new JLabel(new ImageIcon(blueHolderImage));
		GridBagConstraints gbc_blueHolder = new GridBagConstraints();
		gbc_blueHolder.gridx = 0;
		gbc_blueHolder.gridy = 2;
		gbc_blueHolder.anchor = GridBagConstraints.NORTH;
		contentPanel.add(blueHolder, gbc_blueHolder);
		
		Image redHolderImage = createHolderImage(Color.RED);
		JLabel redHolder = new JLabel(new ImageIcon(redHolderImage));
		GridBagConstraints gbc_redHolder = new GridBagConstraints();
		gbc_redHolder.gridx = 2;
		gbc_redHolder.gridy = 2;
		gbc_redHolder.anchor = GridBagConstraints.NORTH;
		contentPanel.add(redHolder, gbc_redHolder);
		
		JPanel boardPanel = new JPanel();
		GridBagConstraints gbc_boardPanel = new GridBagConstraints();
		gbc_boardPanel.gridx = 1;
		gbc_boardPanel.gridy = 2;
		gbc_boardPanel.insets = new Insets(5, 5, 5, 5);
		contentPanel.add(boardPanel, gbc_boardPanel);
		
		GridLayout gl_boardPanel = new GridLayout(BOARD_HEIGHT, BOARD_WIDTH);
		boardPanel.setLayout(gl_boardPanel);
		
		for (int y = 0; y < BOARD_HEIGHT; y++) {
			for (int x = 0; x < BOARD_WIDTH; x++) {
				JButton piece = new JButton(emptyPiece);
				componentMap.put(piece, BoardComponentType.BOARD_BUTTON);
				buttonPositionMap.put(piece, new Position(x,y));
				piece.addActionListener(controller);
				piece.setBorder(BorderFactory.createEmptyBorder());
				piece.setContentAreaFilled(false);
				pieceGrid[x][y] = piece;
				boardPanel.add(piece);
			}
		}
		
		JPanel startPanel = new JPanel();
		JButton newGameButton2P = new JButton("New Game 2 Player");
		JButton newGameButtonAI = new JButton("New Game with AI");
		JButton editButton = new JButton("Edit");
		JButton loadButton = new JButton("Load Game");
		componentMap.put(newGameButton2P, BoardComponentType.NEW_GAME_2P_BUTTON);
		componentMap.put(newGameButtonAI, BoardComponentType.NEW_GAME_AI_BUTTON);
		componentMap.put(editButton, BoardComponentType.EDIT_BUTTON);
		componentMap.put(loadButton, BoardComponentType.LOAD_BUTTON);
		newGameButton2P.addActionListener(controller);
		newGameButtonAI.addActionListener(controller);
		editButton.addActionListener(controller);
		loadButton.addActionListener(controller);
		startPanel.add(newGameButton2P);
		startPanel.add(newGameButtonAI);
		startPanel.add(loadButton);
		startPanel.add(editButton);
		
		JPanel editPanel = new JPanel();
		JButton blueButton = new JButton("Blue");
		JButton redButton = new JButton("Red");
		JButton noneButton = new JButton("None");
		JButton doneButton = new JButton("Done");
		JButton mainMenuButton1 = new JButton("Main Menu");
		componentMap.put(blueButton, BoardComponentType.BLUE_BUTTON);
		componentMap.put(redButton, BoardComponentType.RED_BUTTON);
		componentMap.put(noneButton, BoardComponentType.NONE_BUTTON);
		componentMap.put(doneButton, BoardComponentType.DONE_BUTTON);
		componentMap.put(mainMenuButton1, BoardComponentType.MAIN_MENU_BUTTON);
		blueButton.addActionListener(controller);
		redButton.addActionListener(controller);
		noneButton.addActionListener(controller);
		doneButton.addActionListener(controller);
		mainMenuButton1.addActionListener(controller);
		editPanel.add(blueButton);
		editPanel.add(redButton);
		editPanel.add(noneButton);
		editPanel.add(doneButton);
		editPanel.add(mainMenuButton1);
		
		JPanel playPanel = new JPanel();
		JButton saveButton = new JButton("Save Game");
		JButton mainMenuButton2 = new JButton("Main Menu"); // Need 2 because buttons can only have one parent
		saveButton.addActionListener(controller);
		mainMenuButton2.addActionListener(controller);
		componentMap.put(saveButton, BoardComponentType.SAVE_BUTTON);
		componentMap.put(mainMenuButton2, BoardComponentType.MAIN_MENU_BUTTON);
		playPanel.add(saveButton);
		playPanel.add(mainMenuButton2);
		
		JPanel winPanel = new JPanel();
		JButton mainMenuButton3 = new JButton("Main Menu");
		mainMenuButton3.addActionListener(controller);
		componentMap.put(mainMenuButton3, BoardComponentType.MAIN_MENU_BUTTON);
		winPanel.add(mainMenuButton3);
		
		JPanel emptyPanel = new JPanel();
		
		menuLayout = new CardLayout();
		menuPanel = new JPanel(menuLayout);
		menuPanel.add(startPanel, BoardPanels.START.toString());
		menuPanel.add(editPanel, BoardPanels.EDIT.toString());
		menuPanel.add(playPanel, BoardPanels.PLAY.toString());
		menuPanel.add(winPanel, BoardPanels.TO_MAIN_MENU.toString());
		menuPanel.add(emptyPanel, BoardPanels.EMPTY.toString());
		
		GridBagConstraints gbc_menuPanel = new GridBagConstraints();
		gbc_menuPanel.gridx = 1;
		gbc_menuPanel.gridy = 3;
		contentPanel.add(menuPanel, gbc_menuPanel);
		
		statusLabel = new JLabel(" ");
		GridBagConstraints gbc_statusLabel = new GridBagConstraints();
		gbc_statusLabel.gridx = 1;
		gbc_statusLabel.gridy = 4;
		contentPanel.add(statusLabel, gbc_statusLabel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	/**
	 * Dynamically creates an image of a piece in a holder
	 * using the color specified as input.
	 * @param color the color of the piece in the holder
	 * @return the image of the piece holder
	 */
	private Image createHolderImage(Color color) {
		// These are the polygon's coordinates representing the holder
		int[] holderPolygonX = {2,9,32,55,62};
		int[] holderPolygonY = {98,55,78,55,98};
		
		BufferedImage holderImage = new BufferedImage(PIECE_SIZE, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = holderImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Draw the piece
		g2.setColor(color);
		g2.fillOval(0, 0, PIECE_SIZE, PIECE_SIZE);
		
		// Draw the holder
		g2.setColor(Color.GRAY);
		g2.fillPolygon(holderPolygonX, holderPolygonY, holderPolygonX.length);
		g2.setStroke(new BasicStroke(3));
		g2.setColor(Color.BLACK);
		g2.drawPolygon(holderPolygonX, holderPolygonY, holderPolygonX.length);
		
		g2.dispose();
		
		return holderImage;
	}
	
	/**
	 * Create an image of a piece in a square of the connect four board.
	 * This image can be tiled to create the whole board.
	 * @param color the color of the piece or NONE for blank
	 * @param borderColor the color of the border
	 * @return an image representing a piece of a certain color.
	 */
	private Image createPieceImage(PlayerColor color, Color borderColor) {
		BufferedImage holderImage = new BufferedImage(PIECE_SIZE, PIECE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = holderImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Depending on which color is input, draw a different colored piece
		if (color == PlayerColor.BLUE) {
			g2.setColor(Color.BLUE);
			g2.fillOval(0, 0, PIECE_SIZE, PIECE_SIZE);
		} else if (color == PlayerColor.RED) {
			g2.setColor(Color.RED);
			g2.fillOval(0, 0, PIECE_SIZE, PIECE_SIZE);
		}
		// If the NONE color is specified just draw an empty position
		
		// Draw the grid border
		g2.setStroke(new BasicStroke(2));
		g2.setColor(borderColor);
		g2.drawRect(0, 0, PIECE_SIZE - 1, PIECE_SIZE - 1);
		g2.drawOval(0, 0, PIECE_SIZE - 1, PIECE_SIZE - 1);
		
		g2.dispose();
		
		return holderImage;
	}
	
	/**
	 * Determine which GUI component an object is and if it is even one.
	 * @param obj an object that is a GUI component but it is unknown which one it is
	 * @return a {@link BoardComponentType} if it is one or INVALID_COMPONENT if not
	 */
	public BoardComponentType lookupComponentType(Object obj) {
		if (!componentMap.containsKey(obj)) {
			// If this object isn't a component on the GUI
			// it is an INVALID_COMPONENT
			return BoardComponentType.INVALID_COMPONENT;
		}
		
		return componentMap.get(obj);
	}
	
	/**
	 * Determine the x,y position of a button on the board.
	 * @param obj a button object
	 * @return the position on the board this button occupies or null if the object is invalid
	 */
	public Position lookupButtonPosition(Object obj) {
		if (!buttonPositionMap.containsKey(obj)) {
			// Return null if the object is not a button
			return null;
		}
		
		return buttonPositionMap.get(obj);
	}
	
	/**
	 * Display the pieces of the board to the screen.
	 * @param model the model of the board to draw
	 */
	public void drawModel(BoardModel model) {
		for (int y = 0; y < BOARD_HEIGHT; y++) {
			for (int x = 0; x < BOARD_WIDTH; x++) {
				PlayerColor currentColor = model.getGridPiece(new Position(x, y));
				// Depending on the piece at this x,y position draw the correct color piece on the screen
				if (currentColor == PlayerColor.BLUE) {
					pieceGrid[x][y].setIcon(bluePiece);
				} else if (currentColor == PlayerColor.RED) {
					pieceGrid[x][y].setIcon(redPiece);
				} else {
					pieceGrid[x][y].setIcon(emptyPiece);
				}
			}
		}
	}
	
	/**
	 * Highlights the button with a yellow border
	 * at a certain spot.
	 * @param position the position of the button to highlight
	 */
	public void highlightPiece(Position position) {
		JButton button = pieceGrid[position.x][position.y];
		Icon buttonIcon = button.getIcon();
		if (buttonIcon == emptyPiece) {
			button.setIcon(emptyPieceHL);
		} else if (buttonIcon == redPiece) {
			button.setIcon(redPieceHL);
		} else if (buttonIcon == bluePiece) {
			button.setIcon(bluePieceHL);
		}
	}

	/**
	 * Display the menu specified.
	 * @param panelID the id of the panel to display
	 */
	public void setCurrentMenu(BoardPanels panelID) {
		menuLayout.show(menuPanel, panelID.toString());
	}
	
	/**
	 * Sets the title label.
	 * @param text the new text of the label
	 */
	public void setTitleLabel(String text) {
		titleLabel.setText(text);
	}
	
	/**
	 * Sets the status label.
	 * @param text the new text of the label
	 */
	public void setStatusLabel(String text) {
		statusLabel.setText(text);
	}
}
