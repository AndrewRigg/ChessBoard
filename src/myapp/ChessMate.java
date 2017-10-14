/**
 * @author Andrew Rigg
 * This is the main class for creating an instance of the chess board.
 */
package myapp;

import java.io.*;
import java.util.ArrayList;

import com.sun.speech.freetts.*;

import javafx.application.*;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ChessMate extends Application {

	Player player1, player2;
	ChessClock clock1, clock2;
	BorderPane root;
	MenuBar menuBar;
	Menu fileMenu, compMenu, tutorialMenu, webMenu;
	MenuItem newMenuItem, saveMenuItem, exitMenuItem; 
	CheckMenuItem p1MenuItem, p2MenuItem;
	RadioMenuItem mycompItem, speedItem, customItem;
	ToggleGroup tGroup;
	Image [] whiteImages, blackImages;
	ArrayList<ImageView> whiteImageViews, blackImageViews;
	ArrayList<Piece> whitePieces, blackPieces;
	ArrayList<Integer> rows;
	ArrayList<Character> cols;
	ImageView currentPiece;
	Piece current;
	GridPane board;
	StackPane clockPane1, clockPane2;
	boolean piecePicked, whiteTurn;
	
	final int IMAGE_TYPES = 6, SQUARE_SIZE = 55, NUMBER_OF_PIECES = 16;
	int [] indices = {5, 2, 0, 4, 1, 0, 2, 5};
	int [] takenWhiteCounts = {0,0,0,0,0}, takenBlackCounts = {0,0,0,0,0};
	int lines = 1;
	boolean [][] occupied = new boolean[12][10];
	String [] imageLocations = {"Bishop", "King", "Knight", "Pawn", "Queen", "Rook"};	
	private static final String VOICE = "kevin";
	
	/**
	 * Constructor to initialise the chess board, players, clocks, menus
	 * pieces, images and values.
	 */
	public ChessMate () {
		player1 = new Player("Player1", PlayerType.HUMAN, 0);
		player2 = new Player("Player2", PlayerType.CPU, 0);
		player1.playerTurn = true;
		root = new BorderPane();
		board = new GridPane();
		clock1 = new ChessClock(ClockMode.CUSTOM, player1, 5, 10);
		clock2 = new ChessClock(ClockMode.CUSTOM, player2, 10, 20); 
		menuBar = new MenuBar();
		fileMenu = new Menu("File");
		compMenu = new Menu("Competition");
		tutorialMenu = new Menu("Tutorial");
		webMenu = new Menu("Clock");
		newMenuItem = new MenuItem("New");
		saveMenuItem = new MenuItem("Save");
		exitMenuItem = new MenuItem("Exit");
		p1MenuItem = new CheckMenuItem("Player1");
		p2MenuItem = new CheckMenuItem("Player2");
		tGroup = new ToggleGroup();
		mycompItem = new RadioMenuItem("Competition");
		speedItem = new RadioMenuItem("Speed");
		customItem = new RadioMenuItem("Custom");
		whiteImages = new Image [IMAGE_TYPES];
		blackImages = new Image [IMAGE_TYPES];
		whiteImageViews = new ArrayList<>();
		blackImageViews = new ArrayList<>();
		whitePieces = new ArrayList<>();
		blackPieces = new ArrayList<>();
		rows = new ArrayList<>();
		cols = new ArrayList<>();
		for(int i = 1, a = 'a'; i <= 8; i ++, a++){
			rows.add(i);
			cols.add((char) a);
		}
		root.setTop(menuBar);
	    exitMenuItem.setOnAction(actionEvent -> Platform.exit());
	    fileMenu.getItems().addAll(newMenuItem, saveMenuItem, new SeparatorMenuItem(), exitMenuItem);
	    tutorialMenu.getItems().addAll(new CheckMenuItem("Rules"), new CheckMenuItem("Matches"), new CheckMenuItem("Moves"));
	    compMenu.getItems().add(tutorialMenu);
	    webMenu.getItems().add(p1MenuItem);
	    webMenu.getItems().add(p2MenuItem);
	    compMenu.getItems().addAll(mycompItem, speedItem, customItem);
        p1MenuItem.setSelected(true);
        mycompItem.setToggleGroup(tGroup);
        mycompItem.setSelected(true);
        speedItem.setToggleGroup(tGroup);
        customItem.setToggleGroup(tGroup);
        menuBar.getMenus().addAll(fileMenu, compMenu, webMenu);
        board.setAlignment(Pos.CENTER);
    	board.setPadding(new Insets(10));
		board.setHgap(0);
		board.setVgap(0);	 
	}
	
	public void start(Stage primaryStage) {		
	    menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
	   
		setUpImages(whiteImages, true);
		setUpImages(blackImages, false);
		
		setUpImageViews(whiteImageViews, whitePieces, player1);
		setUpImageViews(blackImageViews, blackPieces, player2);
		
		assignImages(whiteImageViews, whiteImages, indices);
		assignImages(blackImageViews, blackImages, indices);
		
		setUpPieces(whitePieces, whiteImageViews, imageLocations, indices, true);
		setUpPieces(blackPieces, blackImageViews, imageLocations, indices, false);
		
		setUpBoard();
		
		addPiecesToBoard(board, whitePieces, whiteImageViews, true);
		addPiecesToBoard(board, blackPieces, blackImageViews, false);
			
		board.setStyle("-fx-background-color: #336699;");
		root.setCenter(board);
		BorderPane.setAlignment(board,Pos.CENTER);
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setScene(new Scene(root));
		primaryStage.setX(primaryScreenBounds.getMinX());
		primaryStage.setY(primaryScreenBounds.getMinY());
		primaryStage.setWidth(primaryScreenBounds.getWidth());
		primaryStage.setHeight(primaryScreenBounds.getHeight());
		primaryStage.setTitle("ChessMate");
		primaryStage.show(); 
	}
	
	/**
	 * Assign images based on name of pieces
	 * @param images
	 * @param isWhite
	 */
	public void setUpImages(Image [] images, boolean isWhite){
		for (int i = 0; i < IMAGE_TYPES; i++) {
			images[i] = new Image("res/" + ((isWhite) ? "white" : "black") + imageLocations[i] + ".png");
		}
	}
	
	/**
	 * Set up all the pieces which will fill the board.  This includes
	 * setting up all the events for each piece such as moves, removal etc.
	 * @param pieces
	 * @param player
	 * @param takenPieces
	 */
	public void setUpImageViews(ArrayList<ImageView> imageViews, ArrayList<Piece> pieces, Player player) {
		for(int i = 0; i < NUMBER_OF_PIECES; i++) {
			ImageView imageView = new ImageView();
			imageView.setFitWidth(SQUARE_SIZE);
			imageView.setFitHeight(SQUARE_SIZE);
			final Integer innerI = new Integer(i);
			imageView.setOnMouseClicked(new EventHandler<MouseEvent>()
	        {
	            @Override
	            public void handle(MouseEvent t) {
	            	if(!pieces.get(innerI).taken){
		            	if(player.playerTurn) {
			            	currentPiece = imageViews.get(innerI);
			            	current = pieces.get(innerI);
			            	piecePicked = true;
		            	}else {
		            		if(piecePicked) {
		            			Piece piece = pieces.get(innerI);
		            			piecePicked = false;
		            			removePiece(piece);
		            			movePiece(board, currentPiece, piece.col, piece.row);
		            			String str2 = " ";
		            			String str = recordMove_algebraic_notation(current, current.col, current.row, piece.col, piece.row, true, false, false, false);
		            			//String command = getStringCommand(current, current.col, current.row, piece.col, piece.row);
			            		//speak(command);
		            			if(!piece.isWhite){
		            				str2 = "\r\n" + lines++ + ": ";
		            			}
		            			saveMovesToFile("" + str2 + str);
		            			piece.taken = true;
		            			current.col = piece.col;
		            			current.row = piece.row;
		            			swapTurns();
		            		}
		            	}
		            }
	            }
	        });
			imageViews.add(imageView);
		}
	}
	
	/**
	 * Set the associated images of the chess pieces 
	 * @param pieces
	 * @param images
	 */
	public void assignImages(ArrayList<ImageView> imageViews, Image [] images, int [] indices) {
		for(int j = 0; j < 8; j++) {
			imageViews.get(j+8).setImage(images[3]);
			imageViews.get(j).setImage(images[indices[j]]);
		}
	}
	
	/**
	 * Set the chess piece objects to contain the coordinates, the image and the name of the piece
	 * @param pieces
	 * @param images
	 * @param imageLocations
	 * @param indices
	 */
	public void setUpPieces(ArrayList<Piece> pieces, ArrayList<ImageView> images, String [] imageLocations, int [] indices, boolean isWhite) {
		for(int i = 0; i < NUMBER_OF_PIECES; i++) {
			Piece piece;
			String colour = (isWhite) ? "White " : "Black ";
			if(i > 7){
				piece = new Piece(colour + imageLocations[3] + " " + (i - 7), imageLocations[3], 0, 0, images.get(i), isWhite);
			}else if(i < 3){
				piece = new Piece(colour + imageLocations[indices[i]] + "1", imageLocations[indices[i]], 0, 0, images.get(i), isWhite);
			}else if(i > 5){
				piece = new Piece(colour + imageLocations[indices[i]] + "2", imageLocations[indices[i]], 0, 0, images.get(i), isWhite);
			}else{
				piece = new Piece(colour + imageLocations[indices[i]], imageLocations[indices[i]], 0, 0, images.get(i), isWhite);
			}
			pieces.add(piece);
		}
	}
	
	/**
	 * Set up the board to have an 8x8 grid with a border 1 square thick,
	 * stack panes and clock timers
	 */
	public void setUpBoard(){
		char start = 'a';
		for(int a = 0; a < 10; a++) {
			for(int b = 0; b < 13; b++) {
				StackPane pane = new StackPane();
				if(a > 0 && b > 1 && a < 9 && b < 9){
				pane.setOnMouseClicked(new EventHandler<MouseEvent>()
		        {
	            @Override
	            public void handle(MouseEvent t) {
	            	int col = GridPane.getColumnIndex(pane);
            		int row = GridPane.getRowIndex(pane);
	            	if(piecePicked && !occupied[row][col]) {
	            		occupied[row][col] = true;
	            		movePiece(board, currentPiece, col, row);
	            		//String command = getStringCommand(current, current.col, current.row, col, row);
	            		//speak(command);
            			String str2 = " ";
            			String str = recordMove_algebraic_notation(current, current.col, current.row, col, row, false, false, false, false);
            			System.out.println(str);
            			if(!current.isWhite){
            				str2 = "\r\n" + lines++ + ": ";
            			}
            			saveMovesToFile("" + str2 + str);
            			current.col = col;
	            		current.row = row;
	            		piecePicked = false;
	            		swapTurns();
		            	}
		            }
		        });
				}
		
				//Add clock1 to top left of board
				if(a == 0 && b == 0) {
					pane.setAlignment(Pos.TOP_LEFT);
					updateBoard(pane, player1, clock1);
					pane.getChildren().add(updateClockOnBoard(player1, clock1));
					//pane = clockPane1;
				}
				
				//Add clock2 to top right of board
				if(a == 9 && b == 0) {
					pane.setAlignment(Pos.TOP_RIGHT);
					updateBoard(pane, player2, clock2);
					pane.getChildren().add(updateClockOnBoard(player2, clock2));
					//pane = clockPane2;
				}
				
				//Add row labels
				if( b > 1 && b < 10 && a == 0) {
					Text text = new Text(""+ (10-b));
					pane.getChildren().add(text);
					pane.setAlignment(Pos.CENTER_RIGHT);
				}
				
				
				//Add column labels
				if( a > 0 && a < 9 && b == 10) {
					Text text = new Text(""+ start++);
					pane.getChildren().add(text);
					pane.setAlignment(Pos.TOP_CENTER);
				}
				
				Rectangle rec = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
				createCheckerBoard(rec, a, b-1);
				pane.getChildren().add(rec);
				board.add(pane, a, b);
			}
		}
	}
	
	/**
	 * Add the ImageViews of chess pieces to the board
	 * @param board
	 * @param pieces
	 */
	public void addPiecesToBoard(GridPane board, ArrayList<Piece> pieces, ArrayList<ImageView> images, boolean isWhite) {
		for(int i = 0; i < 8; i++) {
			if(isWhite) {
				board.add(images.get(i), i+1, 9);
				board.add(images.get(i+8), i+1, 8);
				pieces.get(i).col = i+1;
				pieces.get(i).row = 9;
				pieces.get(i+8).col = i+1;
				pieces.get(i+8).row = 8;
			}else {
				board.add(images.get(i), i+1, 2);
				board.add(images.get(i+8), i+1, 3);
				pieces.get(i).col = i+1;
				pieces.get(i).row = 2;
				pieces.get(i+8).col = i+1;
				pieces.get(i+8).row = 3;
			}
		}
	}
	
	/**
	 * Create the black [*or grey] and white checkerboard in the standard 
	 * chess configuration (i.e. black square bottom left corner).
	 * @param rec
	 * @param a
	 * @param b
	 */
	public void createCheckerBoard(Rectangle rec, int a, int b) {
		if( a > 0 && a < 9 && b > 0 && b < 9 && (a+b)%2 == 1) {
			rec.setFill(Color.SLATEGRAY);
		}else if(a > 0 && a < 9 && b > 0 && b < 9){
			rec.setFill(Color.WHITE);
		}else {
			rec.setFill(Color.TRANSPARENT);
		}
	}
	
	/**
	 * Update the board clocks to display the latest count down and 
	 * switch the active clock (i.e. change font colour).
	 * @param player
	 * @param clock
	 * @return
	 */
	public Text updateClockOnBoard(Player player, ChessClock clock) {
		Text text = new Text(player.name + ": " + clock.timeDisplay);
		text.setFont(Font.font ("Verdana", 20));
		if(player.playerTurn){
			text.setFill(Color.RED);
		}else {
			text.setFill(Color.BLACK);
		}
		return text;
	}
	
	/**
	 * Swap the boolean value determining whether it is that player's
	 * turn and switch the clock which is counting down.
	 */
	public void swapTurns() {
		player1.playerTurn = !player1.playerTurn;
		player2.playerTurn = !player2.playerTurn;
		clock1.update(player1.playerTurn, clock1.time);
		clock2.update(player2.playerTurn, clock2.time);
	}
	
	/**
	 * Update timers on the board
	 * @param pane
	 * @param player
	 * @param clock
	 */
	public void updateBoard(StackPane pane, Player player, ChessClock clock) {
		updateClockOnBoard(player, clock);
	}
	
	/**
	 * Move a piece from one square to another by removing the
	 * image from the first square and adding it to the next square.
	 * @param board
	 * @param thisPiece
	 * @param toCol
	 * @param toRow
	 */
	public void movePiece(GridPane board, ImageView image, int toCol, int toRow) {
		board.getChildren().remove(image);
		board.add(image, toCol, toRow);
	}
	
	/**
	 * Remove taken piece from the board and put in zone for that type
	 * @param takenPiece
	 */
	public void removePiece(Piece takenPiece){
		ObservableList<Node> children = board.getChildren();
		int row = 0;
		int col = 1;
		Text count;
		boolean addCounter = false;
		if(takenPiece.type == "King"){
			System.err.println("You cannot take the king!");
		}
		if(takenPiece.type == "Knight"){
			col = 2;
		}else if(takenPiece.type == "Bishop"){
			col = 3;
		}else if(takenPiece.type == "Rook"){
			col = 4;
		}else if(takenPiece.type == "Queen"){
			col = 5;
		}
		if(takenPiece.isWhite){
			row = 11;
			takenWhiteCounts[col-1]++;
			count = new Text("x" + takenWhiteCounts[col-1]);
			if(takenWhiteCounts[col-1] > 1){
				addCounter = true;
			}
		}else{
			takenBlackCounts[col-1]++;
			count = new Text("x" + takenBlackCounts[col-1]);
			if(takenBlackCounts[col-1] > 1){
				addCounter = true;
			}
		}
		if(addCounter){
			for(Node node: children){
				if(GridPane.getRowIndex(node) == row+1 && GridPane.getColumnIndex(node) == col){
					((StackPane)node).getChildren().clear();
					((StackPane)node).getChildren().add(count);
					((StackPane)node).setAlignment(Pos.TOP_CENTER);
				}
			}
		}
		movePiece(board, takenPiece.image, col, row);
		takenPiece.taken = true;
	}
	
	/**
	 * Set the string to be read out by the text-to-speech program
	 * (this allows the CPU to let the player know what has been moved)
	 * @param piece
	 * @param colFrom
	 * @param rowFrom
	 * @param colTo
	 * @param rowTo
	 * @return
	 */
	public String getStringCommand(Piece piece,  int colFrom, int rowFrom,  int colTo, int rowTo){
		return (piece.getName() + " from " + cols.get(colFrom-1) + rows.get(9-rowFrom) + " to " +  cols.get(colTo-1) + rows.get(9-rowTo));
	}
	
	/**
	 * FreeTTS text-to-speech facility for reading out moves for CPU (and player).
	 * 
	 */
	 public void speak(String text) {
		  Voice voice;
		  VoiceManager voiceManager = VoiceManager.getInstance();
		  voice = voiceManager.getVoice(VOICE);
		  voice.allocate();
		  voice.speak(text);
		 }
	
	 /**
	  * Return a string with the algebraic notation of the chess move which can be 
	  * used to save, load or record a game
	  * @param args
	  */
	 public String recordMove_algebraic_notation(Piece piece, int colFrom, int rowFrom, int colTo, int rowTo, 
			 boolean capture, boolean ambiguousCol, boolean ambiguousRow, boolean ambiguousBoth){
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
		 move += piece.notation;
		 if(capture){
			 //If pawn is capturing then we need a way of making the piece unambiguous
			 //Here, two identical pieces could move to this square from the same row so
			 //we need to prefix the move with the column of departure of the piece
			 if(piece.type.equals("Pawn") || ambiguousRow){
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
		 return move;
	 }
	 
	 /**
	  * This method allows the moves to be recorded and stored in a .txt file
	  */
	 public void saveMovesToFile(String str){
		 try(FileWriter fw = new FileWriter("GameFile.txt", true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw))
		 {
		    out.print("" + str);
		 } catch (IOException e) {
		 }
	 }
	 
	 /**
	  * Returns a list of all the potential squares which a given piece could move to
	  * @param piece
	  * @param col
	  * @param row
	  * @return
	  */
	 public ArrayList<ArrayList<Integer>>getValidMoves(Piece piece, int col, int row){
		 ArrayList<ArrayList<Integer>> validMoves = new ArrayList<>();
		 int potentialCol;
		 int potentialRow;
		 switch (piece.type){
		 	case "Pawn":
		 		ArrayList<Integer> thisPawnMove = new ArrayList<>();
		 		//Need to add diagonal taking, en passant and promoting
			 		if(piece.isWhite){
			 			potentialCol = col;
				 		potentialRow = row++;
				 		if(checkInBounds(potentialCol, potentialRow)){
				 			thisPawnMove.add(col, row--);
				 		}
			 		}else{
			 			potentialCol = col;
				 		potentialRow = row--;
				 		if(checkInBounds(potentialCol, potentialRow)){
				 			thisPawnMove.add(col, row++);
				 		}
			 		}
			 		validMoves.add(thisPawnMove);
		 		break;
		 	case "Knight":
		 		for(int i = -2; i <= 2; i ++){
		 			for(int j = -2; j <= 2; j++){
		 				if(Math.abs(j) != Math.abs(i) && i != 0 && j != 0){
		 					//and if square at col+j, row+i is not out of bounds
		 					potentialCol = col+j;
		 					potentialRow = row+i;
		 					if(checkInBounds(potentialCol, potentialRow)){
			 					ArrayList<Integer> thisKnightMove = new ArrayList<>();
			 					thisKnightMove.add(col+j, row+i);
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
		 					potentialCol = col+i*j;
		 					potentialRow = row+i*k;
		 					if(checkInBounds(potentialCol, potentialRow)){
					 			thisBishopMove.add(col+= i*j);
					 			thisBishopMove.add(row += i*k);
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
		 		//and whether the move would put the King across check
	 			for(int j = -1; j <= 1; j++){
	 				for(int k = -1; k <= 1; k++){
	 					if(!(j == 0 && k == 0)){
	 						potentialCol = col + j;
	 						potentialRow = row + k;
	 						if(checkInBounds(potentialCol, potentialRow)){
			 					ArrayList<Integer> thisKingMove = new ArrayList<>();
			 					thisKingMove.add(col += j);
			 					thisKingMove.add(row += k);
			 					validMoves.add(thisKingMove);
	 						}
	 					}
	 				}
	 			}
		 		break;
		 	default:
		 		break;
		 }
		return validMoves;
	 }
	 
	 public boolean checkInBounds(int potentialCol, int potentialRow){
		 int lowerBound = 1, upperBound = 8;
		 if(potentialCol >= lowerBound && potentialCol <= upperBound && potentialRow >= lowerBound && potentialRow <= upperBound){
			 return true;
		 }else{
			 return false;
		 }
	 }
	 
	public static void main(String[] args) {
		launch(args);
	}
}
