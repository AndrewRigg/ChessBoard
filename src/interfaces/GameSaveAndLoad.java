package interfaces;

import java.util.ArrayList;

import objects.Piece;

/**
 * This will allow each game to be recorded using PGN (Portable Game Notation)
 * which uses algebraic notation to record the moves in pairs, line by line
 * and will save all games as simple text file (or alternative), it
 * will save the state of incomplete games and will allow games to be 
 * loaded (this could be done by reading through algeabraic notation
 * file from the end, filling in relevant pieces until all are resolved).
 * [Or alternatively (and more simply) to just run through all the moves
 * order until the desired state has been reached].
 * @author Andrew
 *
 */
public class GameSaveAndLoad {

	private static final String VOICE = "kevin";
	ArrayList<Integer> rows;
	ArrayList<Character> cols;
	
	
	public GameSaveAndLoad() {
		rows = new ArrayList<>();
		cols = new ArrayList<>();
		for(int i = 1, a = 'a'; i <= 8; i ++, a++){
			rows.add(i);
			cols.add((char) a);
		}
	}
	
	
	/**
	  * Return a string with the algebraic notation of the chess move which can be 
	  * used to save, load or record a game
	  * @param args
	  */
	 public String recordMove_algebraic_notation(Piece piece, int colFrom, int rowFrom, int colTo, int rowTo, 
			 boolean capture, boolean ambiguousCol, boolean ambiguousRow, boolean ambiguousBoth, boolean castling){
		 String move = "";
		 //Could create method to determine if it will be ambiguous or not, this would 
		 //require knowing all the potential squares which identical pieces can move to
		 if(ambiguousRow){
			 move += cols.get(colFrom-1);
		 }else if(ambiguousCol){
			 move += rows.get(9-rowFrom);
		 }else if(ambiguousBoth){
			 move += cols.get(colFrom-1) + rows.get(9-rowFrom);
		 }
		 move += piece.getNotation();
		 if(capture){
			 //If pawn is capturing then we need a way of making the piece unambiguous
			 //Here, two identical pieces could move to this square from the same row so
			 //we need to prefix the move with the column of departure of the piece
			 if(piece.getType().equals("Pawn") || ambiguousRow){
				 move += cols.get(colFrom-1);
			 }else if(ambiguousCol){
			 //Here, two identical pieces could move to this square from the same column so
			 //we need to prefix the move with the row of departure of the piece
				 move += rows.get(9-rowFrom);
			 }else if(ambiguousBoth){
			 //This case only occurs very rarely when at least one pawn has been promoted and so 
			 //three or more identical pieces could move to the same square, thus both the row and 
			 //column of departure must be prefixed
				 move += cols.get(colFrom-1) + rows.get(9-rowFrom);
			 }
			 move += "x";
		 }
		 move += "" + cols.get((colTo-1)) + rows.get(9-rowTo);
		 if(castling) {
			 move = "0-0";
			 if(colTo == 3) {
				 move += "-0";
			 }
		 }
		 return move;
	 }
	
}
