package algorithms;

import java.util.ArrayList;

import objects.Piece;

/**
 * This class will control the algorithm to arrange the 
 * chess pieces from an arbitrary configuration (hopefully
 * by using an efficient algorithm to do this in the minimal
 * number of moves and with minimal Manhattan distances 
 * (i.e. A* algorithm).  
 * It will also allow a piece to be pulled to the middle of a 
 * square (in case of user placement imperfection) by spiralling 
 * inwards around the square clockwise until centred on the 
 * central point (i.e. where the reed switch is) to allow the 
 * sensors to properly detect the piece on next turn.
 * 
 * @author Andrew
 *
 * Better plan would be to just put all the pieces directly into place
 * using the grid lines between squares to move them past each other then
 * into the centre of their own squares
 */
public class ArrangePieces {

	//list of doubles to show coordinates of each successive square
	//piece will move through to get back to target
	ArrayList<Integer[]> path = new ArrayList<>();
	ArrayList<ArrayList<Integer[]>> paths = new ArrayList<>();
	
	Piece [][] mixedPieces = new Piece [10][10];
	
	public ArrangePieces() {
		
	}
	
	public void moveToInitialPosition(int colFrom, int rowFrom, Piece piece) {
		//****Do this for all pieces starting from the closest to their initial positions***
		
		//1: move arm to piece
		//colFrom, rowFrom
		//2: move piece to edge of square
		//double inBetween = colFrom + 0.5;
		//3: move piece along (colFrom) edge back to centre of desired row 
		//movePiece()
		//4: move piece in from edge to centre of square
	}
	
	/**
	 * gets positions of all pieces
	 * @param occupied
	 * @return
	 */
	public ArrayList<Integer[]> scanPiecePositions(boolean [][] occupied) {
		ArrayList<Integer[]> coords = new ArrayList<>();
		for(int i = 0; i < occupied.length; i++) {
			for(int j = 0; j < occupied.length; j++) {
				if(occupied[i][j]) {
					Integer [] thisCoord = {i, j};
					//readThisPiece()
					coords.add(thisCoord);
				}
			}
		}
		return coords;
	}
	
	public boolean freePathToInitialPos(Piece piece) {
		int homeCol = piece.getCol();
		int homeRow = piece.getRow();
		int currentCol = homeCol;
		int currentRow = homeRow;
		boolean notHome = false;
		boolean free = false;
		while(notHome) {
			
		}
		
		return free;
	}
	
	public int [] manhattanDistanceFromInitialPosition(int col, int row, Piece piece) {
		int [] arr = {piece.getCol() - col, piece.getRow() - row};
		return arr;
	}
}
