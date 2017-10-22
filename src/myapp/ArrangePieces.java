package myapp;

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
 */
public class ArrangePieces {

	Piece [][] mixedPieces = new Piece [10][10];
	
	public ArrangePieces() {
		
	}
}
