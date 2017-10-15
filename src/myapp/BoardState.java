package myapp;

import java.util.ArrayList;

/**
 * This class will involve the algorithm to determine 
 * the state of the board from the previous state and the
 * values of a square array of boolean values representing
 * whether a piece is on a square or not.  This will check 
 * which piece has moved (knowing whose move it is) based 
 * on which square is now active after being inactive and 
 * which square is now inactive after being active and if 
 * only one square is inactive then a piece must have been
 * taken from that square.  For special moves such as castling
 * 
 * @author Andrew
 *
 */
public class BoardState {
	
	public boolean [][] currentGridOccupied;
	public boolean [][] previousGridOccupied;
	ArrayList<Piece> previousBoardState;
	ArrayList<Piece> currentBoardState;
	
	public BoardState(boolean[][] currentGridOccupied, ArrayList<Piece> previousBoardState){
		this.currentGridOccupied = currentGridOccupied;
		this.previousBoardState = previousBoardState;
		previousGridOccupied = getPreviousGrid(previousBoardState);
		currentBoardState = new ArrayList<>();
	}
	
	public boolean [][] getPreviousGrid(ArrayList<Piece> previousBoard){
		boolean [][] previousGrid = new boolean[8][8];
		for(Piece piece: previousBoard){
			previousGrid[piece.col][piece.row]= true;
		}
		return previousGrid;
	}
	
	public ArrayList<Piece> getCurrentBoard(boolean [][] previousGrid, 
			boolean [][] currentGrid, ArrayList<Piece> previousBoard){
		ArrayList<Piece> currentBoard = new ArrayList<>();
		ArrayList<Piece> tempPiecesTo = new ArrayList<>();
		ArrayList<Piece> tempPiecesFrom = new ArrayList<>();
		int differences = 0;
		for(int i = 0; i < currentGrid.length; i++){
			for(int j = 0; j < currentGrid.length; j++){
				if(!previousGrid[i][j] && currentGrid[i][j]){
					differences++;
					previousGrid[i][j] = true;
					for(Piece piece: previousBoard){
						if(piece.col == i && piece.row == j){
							tempPiecesTo.add(piece);
						}
					}
				}else if(previousGrid[i][j] && !currentGrid[i][j]){
					differences++;
					previousGrid[i][j] = true;
					for(Piece piece: previousBoard){
						if(piece.col == i && piece.row == j){
							tempPiecesFrom.add(piece);
						}
					}
				}else{
					previousGrid[i][j] = false;
				}
			}
		}
		
		if(differences == 1){
			//This represents a piece capture.  The value of the square landed
			//on remains the same but the square left goes from true to false.
			Piece piece = tempPiecesFrom.get(0);
			//For all places which the piece could move to, which could it have?
			//This can lead to ambiguous states which would then require the RFID
			//reader to determine which of two or more pieces have been taken.
			
			//For all pieces in previousBoard, add them to current unless they were
			//taken, and update the coordinates of the one that moved.
			//Alternatively could look at which of the two or more squares had a temporary
			//glitch in voltage (i.e. where was a piece removed from and then added to
			//which would require pretty much continuous reading and would still not 
			//guarantee a non-ambiguous reading (i.e. if a piece was pushed out the way
			//or moved in place very quickly).
			for(Piece p: previousBoard){
				if(p.isWhite /*is unremarkable*/){
					currentBoard.add(p);
				}else if(p.isWhite /*is the changed location*/){
					//check the grid where 
//					p.col = newCol;
//					p.row = newRow;
				}
			}
			
						
		}else if(differences == 2){
			//This represents a regular move (i.e. a piece goes from one place
			//to another which changes one value from true to false and another 
			//from false to true.
			
			
		}else if(differences == 4){
			//This would be the case if castling 
			
		}else{
			//If any other number of differences occur
			System.err.println("There has been an error with the board reading or "
					+ "the player has made an invalid move!");
			return null;
		}
		return currentBoard;
	}
}
