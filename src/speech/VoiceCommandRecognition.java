package speech;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import main.ChessMate;
import objects.Piece;
import objects.Player;
import objects.PlayerType;

/**
 * This class will deal with the API and control of the voice
 * recognition.  This will allow a user to make a move by speaking
 * into a microphone (on the board) which will allow the Google Voice
 * API to determine which words or word combinations were spoken from
 * a predefined list of selected words and then action a corresponding
 * move if valid (or tell the user a warning if invalid).
 * @author Andrew
 *
 */
public class VoiceCommandRecognition {

	Player player;
	String [] imageLocations = {"bishop", "king", "knight", "pawn", "queen", "rook"};	
	String [] defaultCommands = {"hello", "goodbye", "pawn", "queen", "king", 
			"knight", "bishop", "castle", "rook", "knave", "check", 
			"checkmate", "en Passen", "fork", "to", "castling", 
			"foul", "illegal", "invalid", "resign", "stalemate", "draw", "50", 
			"move", "k", "q", "r", "b", "n", "capture", "x", "()", "(=)", "0", 
			"0-0", "0-0-0", "+", "ch", "†", "++", "dbl ch", "#", "mate"};
	String [] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};
	String [] rows = {"1", "2", "3", "4", "5", "6", "7", "8"};
	
	public VoiceCommandRecognition(Player player) throws IOException {
		this.player = player;
		createMoveFromText();
	}
	
	/**
	 * Method for taking the audio stream of words from the text file and interpreting 
	 * them into a command to move a chess piece
	 * @throws IOException
	 */
	public void createMoveFromText() throws IOException{
		@SuppressWarnings("resource")
		BufferedReader brTest = new BufferedReader(new FileReader("src/files/out.txt"));
		String text = brTest.readLine().toLowerCase();
		String[] strArray = text.split(" ");
		ArrayList<String> pieces = new ArrayList<String> (Arrays.asList(imageLocations));
		ArrayList<String> theseWords = new ArrayList<String> (Arrays.asList(strArray));
		ArrayList<String> potentialWords = new ArrayList<>();
		Piece thisPiece;
		String type = ""; 
		String longest = "";
		String squareTo = "";
		int colTo = 0, rowTo = 0;
		int index = 1;
		boolean foundPiece = false;
		
		System.out.println("Pieces: " + pieces.toString());
		System.out.println("These words: " + theseWords.toString());
		
		// Stop. text is the first line.
		System.out.println("Interpreted python text: ");
		
		for(String s: strArray) {
			System.out.println(index++ + ": "+ s);
			if(pieces.contains(s)) {
				String st = s.substring(0,1).toUpperCase();
				String end = s.substring(1,s.length());
				s = st + end;
				type = s;
				foundPiece = true;
				
			}
			else if(!foundPiece){
				for(int i = 0; i < s.length(); i++) {
					for(int j = i+1; j < s.length()+1; j++) {
						String temp = s.substring(i, j);
						System.out.println("\t --> " + temp);
						for(String piece: pieces) {
							if(piece.contains(temp)) {
								potentialWords.add(temp);
							}
						}
					}
				}
				System.out.println("Pt: " + potentialWords.toString());
				for(String str: potentialWords) {
					if(str.length() > longest.length()) {
						longest = str;
					}
				}
			}
			for(int i = 0; i < rows.length; i++) {
				if(s.contains(rows[i])) {
					squareTo = s;
					System.out.println("squareTo: " + squareTo);
					rowTo = i+1;
				}
			}
		}
		
		for(int j = 0; j < cols.length; j++) {
			if(squareTo.contains(cols[j])) {
				System.out.println(" cols j: " + cols[j]);
				colTo = j+1;
			}
		}

		if (!foundPiece) {
			for(String s: pieces) {
				if(s.contains(longest)) {
					String st = s.substring(0,1).toUpperCase();
					String end = s.substring(1,s.length());
					s = st + end;
					type = s;
				}
			}
		}
		
		String nm = player.playerTurn? "White " : "Black "; 
		thisPiece = new Piece(nm + type);
		thisPiece.setCol(colTo);
		thisPiece.setRow(rowTo);
		System.out.println("Piece: " + thisPiece.getName());
		System.out.println("col: " + thisPiece.getCol() + " row: " + thisPiece.getRow());
	}
}
