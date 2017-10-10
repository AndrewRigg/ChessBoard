package myapp;

import java.util.ArrayList;

public class WordCommands {

	ArrayList<String> commands;
	String [] defaultCommands = {"Hello", "Goodbye", "Pawn", "Queen", "King", 
			"Knight", "Bishop", "Castle", "Rook", "Knave", "Check", 
			"Checkmate", "En Passen", "Fork", "to", "Castling", 
			"Foul", "Illegal", "Invalid", "Resign", "Stalemate", "Draw", "50", 
			"move", "K", "Q", "R", "B", "N", "Capture", "x", "()", "(=)", "0", 
			"0-0", "0-0-0", "+", "ch", "†", "++", "dbl ch", "#", "mate"};
	String [] cols = {"A", "B", "C", "D", "E", "F", "G", "H"};
	String [] rows = {"1", "2", "3", "4", "5", "6", "7", "8"};
	public WordCommands(String [] cols, String [] rows) {
		this.cols = cols;
		this.rows = rows;
		commands = new ArrayList<>();
		for(int i = 0; i < defaultCommands.length; i++) {
			commands.add(defaultCommands[i]);
		}
	}
	
	
	
	
}
