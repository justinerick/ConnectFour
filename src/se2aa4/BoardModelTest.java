package se2aa4;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BoardModelTest {

	static Position test1,test2,test3,test4,test5; //different test positions
	static Position[] error,winner; // test position arrays
	static BoardModel theBoard; //the Board
	
	@Before
	public void setUp() throws Exception {
		 test1 = new Position(3,5);
		 test2 = new Position(2,5);
		 test3 = new Position(1,5);
		 test4 = new Position(0,5);
		 test5 = new Position(3,3);
		 error = new Position[1];
		 winner = new Position[4];
		 
		 theBoard = new BoardModel();
		
	}

	@Test
	public void testGetGridWidth() {
		assertEquals(7,theBoard.getGridWidth());
	}

	@Test
	public void testGetGridHeight() {
		assertEquals(6,theBoard.getGridHeight());
	}

	@Test
	public void testGetGridPiece() {
		
		theBoard.setGridPiece(test1,PlayerColor.BLUE);
		Position expected = new Position(3,5);
		assertEquals(PlayerColor.BLUE,theBoard.getGridPiece(expected));
	}

	@Test
	public void testGetErrorPositions() {
		
		theBoard.setGridPiece(test5,PlayerColor.BLUE);
		error[0] = new Position(3,3);
		assertArrayEquals(error,theBoard.getErrorPositions());
	}

	@Test
	public void testGetWinner() {
		
		theBoard.setGridPiece(test1,PlayerColor.BLUE);
		theBoard.setGridPiece(test2,PlayerColor.BLUE);
		theBoard.setGridPiece(test3,PlayerColor.BLUE);
		theBoard.setGridPiece(test4,PlayerColor.BLUE);
		assertEquals(PlayerColor.BLUE,theBoard.getWinner());
	}

	@Test
	public void testGetWinningPieces() {
		
		theBoard.setGridPiece(test1,PlayerColor.BLUE);
		theBoard.setGridPiece(test2,PlayerColor.BLUE);
		theBoard.setGridPiece(test3,PlayerColor.BLUE);
		theBoard.setGridPiece(test4,PlayerColor.BLUE);
		
		winner[3] = new Position(3,5);
		winner[2]= new Position(2,5);
		winner[1] = new Position(1,5);
		winner[0] = new Position(0,5);
		
		assertArrayEquals(winner,theBoard.getWinningPieces());
	}

	@Test
	public void testGetPieceCount() {
		
		theBoard.setGridPiece(test1,PlayerColor.BLUE);
		theBoard.setGridPiece(test2,PlayerColor.BLUE);
		theBoard.setGridPiece(test3,PlayerColor.BLUE);
		theBoard.setGridPiece(test4,PlayerColor.BLUE);
		assertEquals(4,theBoard.getPieceCount(PlayerColor.BLUE));
	}

	@Test
	public void testWhoHasMorePieces() {
		
		theBoard.setGridPiece(test2,PlayerColor.BLUE);
		theBoard.setGridPiece(test3,PlayerColor.RED);
		theBoard.setGridPiece(test4,PlayerColor.BLUE);
		assertEquals(PlayerColor.BLUE,theBoard.whoHasMorePieces());
	}

	@Test
	public void testGetErrorColor() {
		
		theBoard.setGridPiece(test1,PlayerColor.BLUE);
		theBoard.setGridPiece(test2,PlayerColor.BLUE);
		theBoard.setGridPiece(test3,PlayerColor.RED);
		theBoard.setGridPiece(test4,PlayerColor.BLUE);
		assertEquals(PlayerColor.BLUE,theBoard.getErrorColor());
	}

	@Test
	public void testGetStartPlayer() {
		
		theBoard.setGridPiece(test2,PlayerColor.BLUE);
		theBoard.setGridPiece(test3,PlayerColor.RED);
		theBoard.setGridPiece(test4,PlayerColor.BLUE);
		assertEquals(PlayerColor.RED,theBoard.getStartPlayer());
	}

	@Test
	public void testDoMove() {
		
		theBoard.setGridPiece(test2,PlayerColor.BLUE);
		theBoard.setGridPiece(test3,PlayerColor.RED);
		theBoard.setGridPiece(test4,PlayerColor.BLUE);
		assertEquals(true,theBoard.doMove(4, PlayerColor.RED));
	}

	@Test
	public void testDoTemporaryMove() {
		
		theBoard.setGridPiece(test2,PlayerColor.BLUE);
		theBoard.setGridPiece(test3,PlayerColor.RED);
		theBoard.setGridPiece(test4,PlayerColor.BLUE);
		assertEquals(true,theBoard.doTemporaryMove(4, PlayerColor.RED));
	}

	@Test
	public void testUndoTemporaryMove() {
		theBoard.setGridPiece(test2,PlayerColor.BLUE);
		theBoard.setGridPiece(test3,PlayerColor.RED);
		theBoard.setGridPiece(test4,PlayerColor.BLUE);
		theBoard.doTemporaryMove(4, PlayerColor.RED);
		//do the move then undo it.
		assertEquals(PlayerColor.RED,theBoard.getGridPiece(new Position(4,5)));
		theBoard.undoTemporaryMove(4);
		assertEquals(PlayerColor.NONE,theBoard.getGridPiece(new Position(4,5)));
	}

	@Test
	public void testReset() {
		theBoard.setGridPiece(test2,PlayerColor.BLUE);
		theBoard.setGridPiece(test3,PlayerColor.RED);
		theBoard.setGridPiece(test4,PlayerColor.BLUE);
		theBoard.reset();
		//the reset should be empty
		assertEquals(theBoard.getPieceCount(PlayerColor.NONE), 42);
	}
	
	@Test
	public void testCopy() {
		theBoard.setGridPiece(test2,PlayerColor.BLUE);
		theBoard.setGridPiece(test3,PlayerColor.RED);
		theBoard.setGridPiece(test4,PlayerColor.BLUE);
		//the copy should be the same as the board
		BoardModel copy = theBoard.copy();
		assertEquals(PlayerColor.BLUE, copy.getGridPiece(test2));
		assertEquals(PlayerColor.RED, copy.getGridPiece(test3));
		assertEquals(PlayerColor.BLUE, copy.getGridPiece(test4));
	}
}
