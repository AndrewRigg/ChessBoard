package main;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import objects.Piece;

public class ValidMoves {

	ChessMate chess;
	GridPane board;
	ArrayList<ArrayList<Integer>> validMoves;
	ArrayList<Piece> whitePieces, blackPieces;
	final static int LOWER_BOUND = 1, UPPER_BOUND = 8;
	
	public ValidMoves(ChessMate chess) {
		this.validMoves = chess.validMoves;
		this.board = chess.board;
		this.whitePieces = chess.whitePieces;
		this.blackPieces = chess.blackPieces;
	}
	
	/**
	  * Returns a list of all the potential squares which a given piece could move to
	  * @param piece
	  * @param col
	  * @param row
	  * @return
	  */
	 public ArrayList<ArrayList<Integer>>getBasicValidMoves(Piece piece){
		 int potentialCol;
		 int potentialRow;
		 int col = piece.getCol();
		 int row = 10 - piece.getRow();
		 switch (piece.getType()){
		 	case "Pawn":
		 		
		 		//Need en passant and promoting
		 		if(piece.isUnmoved()) {
		 			potentialCol = col;
			 		potentialRow = row;
		 			for(int i = 1; i <= 2; i++) {
		 				ArrayList<Integer> thisPawnMove = new ArrayList<>();
			 			if(piece.isWhite()){
					 			thisPawnMove.add(col);
					 			thisPawnMove.add(row+i);
				 		}else{
					 			thisPawnMove.add(col); 
					 			thisPawnMove.add(row-i);
				 		}
			 			validMoves.add(thisPawnMove);
		 			}
		 		}else {
		 			ArrayList<Integer> thisPawnMove = new ArrayList<>();
			 		if(piece.isWhite()){
				 			potentialCol = col;
					 		potentialRow = row++;
					 		if(checkInBounds(potentialCol, potentialRow)){
					 			thisPawnMove.add(col);
					 			thisPawnMove.add(row);
				 		}
			 		}else{
			 			thisPawnMove.clear();
			 			potentialCol = col;
				 		potentialRow = row--;
				 		if(checkInBounds(potentialCol, potentialRow)){
				 			thisPawnMove.add(col); 
				 			thisPawnMove.add(row);
				 		}
			 		}
			 		validMoves.add(thisPawnMove);
		 		}
		 		
			 		
		 		break;
		 		
		 	case "Knight":
		 		for(int i = -2; i <= 2; i ++){
		 			for(int j = -2; j <= 2; j++){
		 				if(Math.abs(j) != Math.abs(i) && i != 0 && j != 0){
		 					potentialCol = col+j;
		 					potentialRow = row+i;
		 					if(checkInBounds(potentialCol, potentialRow)){
			 					ArrayList<Integer> thisKnightMove = new ArrayList<>();
			 					thisKnightMove.add(col+j);
			 					thisKnightMove.add(row+i);
			 					validMoves.add(thisKnightMove);
		 					}
		 				}
		 			}
		 		}
		 		break;
		 		
		 	case "Bishop":
		 		for(int i = 1; i < 8; i++){
		 			for(int j = -1; j <= 1; j+=2){
		 				for(int k = -1; k <= 1; k+=2){
		 					ArrayList<Integer> thisBishopMove = new ArrayList<>();
		 					potentialCol = col+(i*j);
		 					potentialRow = row+(i*k);
		 					if(checkInBounds(potentialCol, potentialRow)){
					 			thisBishopMove.add(col+ i*j);
					 			thisBishopMove.add(row + i*k);
					 			validMoves.add(thisBishopMove);
		 					}
		 				}
		 			}
		 			
		 		}
		 		break;
		 		
		 	case "Rook":
		 		//Need to include castling but this will be activated by the king
		 		for(int i = 1; i < 8; i++){
		 			for(int j = -1; j <= 1; j++){
		 				for(int k = -1; k <= 1; k++){
		 					if(Math.abs(j) != Math.abs(k)){
		 						potentialCol = col + j*i;
		 						potentialRow = row + k*i;
		 						if(checkInBounds(potentialCol, potentialRow)){
						 			ArrayList<Integer> thisRookMove = new ArrayList<>();
						 			thisRookMove.add(col + j*i);
						 			thisRookMove.add(row + k*i);
						 			validMoves.add(thisRookMove);
		 						}
		 					}
		 				}
		 			}
		 		}
		 		break;
		 		
		 	case "Queen":
		 		for(int i = 1; i < 8; i++){
		 			for(int j = -1; j <= 1; j++){
		 				for(int k = -1; k <= 1; k++){
		 					if(!(j == 0 && k == 0)){
		 						potentialCol = col+j*i;
		 						potentialRow = row+k*i;
		 						if(checkInBounds(potentialCol, potentialRow)){
				 					ArrayList<Integer> thisQueenMove = new ArrayList<>();
				 					thisQueenMove.add(col + j*i);
				 					thisQueenMove.add(row + k*i);
				 					validMoves.add(thisQueenMove);
		 						}
		 					}
		 				}
		 			}
			 	}
		 		break;
		 		
		 	case "King":
		 		//Have to deal with castling from here 
		 		//Could have a boolean for whether castling is an option for each
		 		//side which would include whether the two pieces are in the right places, 
		 		//whether they have moved, if the squares in between are free, 
		 		//and whether the move would put the King across check		***This part is hard
	 			for(int j = -1; j <= 1; j++){
	 				for(int k = -1; k <= 1; k++){
	 					if(!(j == 0 && k == 0)){
	 						potentialCol = col + j;
	 						potentialRow = row + k;
	 						if(checkInBounds(potentialCol, potentialRow)){
			 					ArrayList<Integer> thisKingMove = new ArrayList<>();
			 					thisKingMove.add(col + j);
			 					thisKingMove.add(row + k);
			 					validMoves.add(thisKingMove);
	 						}
	 					}
	 				}
	 			}
	 			if(piece.isUnmoved()) {
	 				//for each rook
	 				ArrayList<Integer> castlingMove = new ArrayList<>();
					ArrayList<Piece> pieces = (piece.isWhite()? whitePieces : blackPieces);
					
					//*****Need to add check for 'moving across Check' *****
	 				if(pieces.get(0).isUnmoved()) 
	 				{
	 					boolean queenSide = true;
	 					//if there are free spaces on both spaces to the left of king
	 					ObservableList<Node> children = board.getChildren();
						for(Node node: children){
							if((GridPane.getColumnIndex(node) == col - 1 || GridPane.getColumnIndex(node) == col - 2 ||  GridPane.getColumnIndex(node) == col-3) && GridPane.getRowIndex(node) == 10 - row){
								if(node instanceof ImageView) {
									queenSide = false;
								}
							}
						}
						 if(queenSide) {
							castlingMove.add(col-2);
	 	 					castlingMove.add(row);
	 						validMoves.add(castlingMove);
						 }
	 				}
	 				if(pieces.get(7).isUnmoved())
	 				{
	 					boolean kingSide = true;
	 					//if there are free spaces on both spaces to the right of king
	 					ObservableList<Node> children = board.getChildren();
						for(Node node: children){
							if((GridPane.getColumnIndex(node) == col + 1 || GridPane.getColumnIndex(node) == col + 2) && GridPane.getRowIndex(node) == 10 - row){
								if(node instanceof ImageView) {
									kingSide = false;
								}
							}
						}
						 if(kingSide) {
							castlingMove.add(col+2);
	 	 					castlingMove.add(row);
	 						validMoves.add(castlingMove);
						 }
	 				}
	 			}
		 		break;
		 		
		 	default:
		 		break;
		 }
		 
		return validMoves;
	 }
	
	 
	 /**
	  * Check two integer parameters are within the bounds of the board
	  * @param potentialCol
	  * @param potentialRow
	  * @return
	  */
	 public boolean checkInBounds(int potentialCol, int potentialRow){
		 return (potentialCol >= LOWER_BOUND && 
				 potentialCol <= UPPER_BOUND && 
				 potentialRow >= LOWER_BOUND && 
				 potentialRow <= UPPER_BOUND);
	 }
	 
	 /**
	  * This removes the squares in the valid moves list which are not valid
	  * due to being occupied by a piece of the same colour
	  * @param pieces
	  * @param piece
	 * @return 
	  */
	 public ArrayList<ArrayList<Integer>> removeOwnColours(ArrayList<Piece> pieces, ArrayList<Piece> otherPieces, Piece piece) {
		 for(Piece samePiece: pieces) {
			 ArrayList<Integer> temp = new ArrayList<Integer>();
			 temp.add(samePiece.getCol());
			 temp.add(10 - samePiece.getRow());
			 if(validMoves.contains(temp)) {
				 validMoves.remove(temp);
				 if(piece.getType() == "Bishop" || piece.getType() == "Rook" || piece.getType() == "Queen") {
					 for(int i = 0; i < 8; i++) {
						 ArrayList<Integer> temp2 = new ArrayList<Integer>();
						 int colDir = samePiece.getCol(), rowDir = 10 -samePiece.getRow();
						 if(piece.getCol() != samePiece.getCol()) {
							 colDir = samePiece.getCol() +((samePiece.getCol() - piece.getCol())/Math.abs(samePiece.getCol() - piece.getCol()))*i;
						 }
						 if(piece.getRow() != samePiece.getRow()) {
							 rowDir = 10 - (samePiece.getRow() +((samePiece.getRow() - piece.getRow())/Math.abs(samePiece.getRow() - piece.getRow()))*i);
						 }
						 temp2.add(colDir);
						 temp2.add(rowDir);
						 if(validMoves.contains(temp2)) {
							 validMoves.remove(temp2);
						 }
					 }
				 }
				 else if(piece.getType() == "Pawn") {
					 validMoves.remove(temp);
					 if(piece.isWhite() && piece.isUnmoved()) {
						 if(piece.getCol() == samePiece.getCol() && piece.getRow() == samePiece.getRow()+1) {
							 ArrayList<Integer> temp5 = new ArrayList<>();
							 temp5.add(samePiece.getCol());
							 temp5.add(10 - samePiece.getRow()+1);
							 if(validMoves.contains(temp5)) {
								 validMoves.remove(temp5);
							 }
						 }
					 }else if(piece.isUnmoved()) {
						 if(piece.getCol() == samePiece.getCol() && piece.getRow() == samePiece.getRow()-1) {
							 ArrayList<Integer> temp6 = new ArrayList<>();
							 temp6.add(samePiece.getCol());
							 temp6.add(10 - samePiece.getRow() - 1);
							 if(validMoves.contains(temp6)) {
								 validMoves.remove(temp6);
							 }
						 }
					 }
				 }
			 }
		 }
		 //Here try to prevent certain pieces jumping over pieces of opposite colour
		 for(Piece oppositePiece: otherPieces) {
			 ArrayList<Integer> temp3 = new ArrayList<Integer>();
			 temp3.add(oppositePiece.getCol());
			 temp3.add(10 - oppositePiece.getRow());
			 if(validMoves.contains(temp3)) {
				 if(piece.getType() == "Bishop" || piece.getType() == "Rook" || piece.getType() == "Queen") {
					 for(int i = 1; i < 8; i++) {
						 ArrayList<Integer> temp4 = new ArrayList<Integer>();
						 int colDir = oppositePiece.getCol(), rowDir = 10 - oppositePiece.getRow();
						 if(piece.getCol() != oppositePiece.getCol()) {
							 colDir = oppositePiece.getCol() +((oppositePiece.getCol() - piece.getCol())/Math.abs(oppositePiece.getCol() - piece.getCol()))*i;
						 }
						 if(piece.getRow() != oppositePiece.getRow()) {
							 rowDir = 10 - (oppositePiece.getRow() +((oppositePiece.getRow() - piece.getRow())/Math.abs(oppositePiece.getRow() - piece.getRow()))*i);
						 }
						 temp4.add(colDir);
						 temp4.add(rowDir);
						 if(validMoves.contains(temp4)) {
							 validMoves.remove(temp4);
						 }
					 }
				 }else if(piece.getType() == "Pawn") {
					 validMoves.remove(temp3);
					 if(piece.isWhite() && piece.isUnmoved()) {
						 if(piece.getCol() == oppositePiece.getCol() && piece.getRow() == oppositePiece.getRow()+1) {
							 ArrayList<Integer> temp5 = new ArrayList<>();
							 temp5.add(oppositePiece.getCol());
							 temp5.add(10 - oppositePiece.getRow()+1);
							 if(validMoves.contains(temp5)) {
								 validMoves.remove(temp5);
							 }
						 }
					 }else if(piece.isUnmoved()) {
						 if(piece.getCol() == oppositePiece.getCol() && piece.getRow() == oppositePiece.getRow()-1) {
							 ArrayList<Integer> temp6 = new ArrayList<>();
							 temp6.add(oppositePiece.getCol());
							 temp6.add(10 - oppositePiece.getRow() - 1);
							 if(validMoves.contains(temp6)) {
								 validMoves.remove(temp6);
							 }
						 }
					 }
				 }
			 }
			 //Pawn capture added into moves
			 if(piece.getType() == "Pawn" && (piece.getCol() == oppositePiece.getCol()-1 || piece.getCol() == oppositePiece.getCol()+1) && piece.getRow() == oppositePiece.getRow() + (piece.isWhite()? 1 : -1)) {
				 ArrayList<Integer> temp7 = new ArrayList<>();
				 temp7.add(oppositePiece.getCol());
				 temp7.add(10 - oppositePiece.getRow());
				 validMoves.add(temp7);
			 }
		 }
		 //Remove square in front of pawn (just off the board) 
		 //when it has reached the end from the valid moves list
		 if(piece.getType() == "Pawn" && piece.getRow() == (piece.isWhite() ? 2 : 9)) {
			 validMoves.clear();
		 } 		 
	 return validMoves;
	 }
	  
	 
	 
	 
	 
/*
		for(int a = 0; a < 10; a++) {
			for(int b = 0; b < 13; b++) {
				StackPane pane = new StackPane();
				
				Rectangle rec = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
				createCheckerBoard(rec, a, b-1);
				pane.getChildren().add(rec);
				
				if(a > 0 && b > 1 && a < 9 && b < 10){
					pane.setOnMouseClicked(new EventHandler<MouseEvent>(){
			            @Override
			            public void handle(MouseEvent t) {
			            	castling = false;
			            	int col = GridPane.getColumnIndex(pane);
		            		int row = GridPane.getRowIndex(pane);
			            	if(piecePicked && !occupied[row-2][col-1]) {
			            		ArrayList<Integer> temp = new ArrayList<>();
			            		ArrayList<ImageView> theseImages = (current.isWhite()? whiteImageViews : blackImageViews);
			            		ArrayList<Piece> otherPieces = (current.isWhite()? blackPieces : whitePieces);
		            			ArrayList<Piece> thesePieces = (current.isWhite()? whitePieces : blackPieces);
		            			temp.add(col);
		            			temp.add(10 - row);
			            		if(validMoves.contains(temp)) {
			            			occupied[row-2][col-1] = true;
				            		movePiece(board, currentPiece, col, row);
				            		if(current.getType().equals("King") && Math.abs(current.getCol() - col) == 2) {
				            			//Queen side castling
				            			castling = true;
				            			
				            			if(col < 5) {
				            				movePiece(board, theseImages.get(0), col+1, row);
				            				thesePieces.get(0).setUnmoved(false);
				            				occupied[row-2][0] = false;
				            				occupied[row-2][3] = true;
				            				thesePieces.get(0).setCol(col+1);
				            			}else {
				            			//King side castling
				            				movePiece(board, theseImages.get(7), col-1, row);
				            				thesePieces.get(7).setUnmoved(false);
				            				occupied[row-2][7] = false;
				            				occupied[row-2][5] = true;
				            				thesePieces.get(7).setCol(col-1);
				            			}
				            		}
				            		if(textToSpeech) {
					            		String command = getStringCommand(current, current.getCol(), current.getRow(), col, row, false, castling);
					            		speak(command);
				            		}
				            		occupied[current.getRow()-2][current.getCol()-1] = false;
			            			String str2 = " ";
			            			String str = recordMove_algebraic_notation(current, current.getCol(), current.getRow(), col, row, false, false, false, false, castling);
			            			if(current.isWhite() && firstMove){
			            				str2 = lines++ + ": ";
			            			}else if(current.isWhite()){
			            				str2 = "\r\n" + lines++ + ": ";
			            			}
			            			if(firstMove) {
			            				saveFile = determineSaveFile();
			            			}
			            			saveMovesToFile("" + str2 + str);
			            			current.setCol(col);
				            		current.setRow(row);
				            		current.setUnmoved(false);
				            		firstMove = false;
				            		piecePicked = false;
				            		alreadySelected = false;
				            		swapTurns();
				            		showOccupied();	
			            		}
			            		if(!validMoves.isEmpty()) {
			            			removeHighlights(validMoves);
			            			validMoves.clear();
			            		}
			            		System.out.println("Check: "+ detectCheck(otherPieces.get(5), otherPieces, thesePieces));
				            }
				        }
			        });
			        
			        
			        	public void actionMove(String str, String str2, Piece piece) {
							saveMovesToFile("" + str2 + str);
							current.setCol(piece.getCol());
							current.setRow(piece.getRow());
							current.setUnmoved(false);	
							firstMove = false;
							piecePicked = false;
							alreadySelected = false;
							swapTurns();
							showOccupied();	
	}
	 
	 
	 */
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
