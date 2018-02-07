package engine;

import java.util.ArrayList;

import objects.Piece;

/**
 * This is where the main AI will be written for the CPU to play
 * the user.  It should incorporate all valid moves and a scoring 
 * system by which to determine minimax algorithm and should have 
 * different difficulties (i.e. based on number of seconds given to 
 * calculate next move).
 * @author Andrew
 *
 */
public class ChessEngine {

	
	//Check all possible moves and then do any that takes a piece
	//otherwise make a random move
	public void makeUnthinkingMove(ArrayList<Piece> pieces) {
		for(Piece pieceLeft: pieces) {
			//if(pieceLeft.getValidMoves().contains() //a capture){
				//then save it to potential list of moves in order of score
				//where score is equal, choose randomly, higher value piece to capture = higher score
			}
		}
	

	/*
	 * 
	 * This takes into account 
	 */
	public void makeBasicMove() {
		
	}
	
	
	public void makeAverageMove() {
		
	}
	
	/**
	 * These should be minimax calculations based on complex score function and computing for
	 * number of seconds in the method name (i.e. run through all permutations of moves to a 
	 * certain depth and do minimax on each level until time is up, then select optimal (highest)
	 * score from computed values)
	 */
	public void makeComputedMove1s() {
		
	}
	
	public void makeComputedMove2s() {
		
	}
	
	public void makeComputedMove3s() {
		
	}

	public void makeComputedMove4s() {
	
	}
	
	public void makeComputedMove5s() {
		
	}
	
	public void makeComputedMove7s() {
		
	}
	
	public void makeComputedMove8s() {
		
	}
	
	public void makeComputedMove9s() {
			
	}
	
	public void makeComputedMove10s() {
		
	}
	
	
	//NEURAL NETWORK 
	//create an AI that plays itself and gets better with each turn.  Change the score system dynamically
	//Only tangible factors are pieces on board, positions of pieces on board, positions of pieces in relation to other pieces on the board
}
