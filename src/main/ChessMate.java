/**
 * @author Andrew Rigg
 * This is the main class for creating an instance of the chess board.
 */
package main;

import java.io.*;
import java.util.*;

import com.sun.speech.freetts.*;

import javafx.application.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import objects.Clock;
import objects.ClockMode;
import objects.Moves;
import objects.Piece;
import objects.Player;
import objects.PlayerType;
import speech.VoiceCommandRecognition;

public class ChessMate extends Application {

	/**
	 * Global variables
	 */
	Timer timer;
	Player player1, player2;
	Clock clock1, clock2;
	BorderPane root;
	MenuBar menuBar;
	ToggleGroup tGroup;
	Image [] whiteImages, blackImages;
	Image backButton; 
	ArrayList<ImageView> whiteImageViews, blackImageViews;
	ArrayList<Piece> whitePieces, blackPieces;
	ArrayList<Integer> rows;
	ArrayList<Character> cols;
	ArrayList<ArrayList<Integer>> validMoves;
	ArrayList<Menu> menuOptions;
	ArrayList<Moves> moves;
	ArrayList<MenuItem> menuItemOptions, playerOptions, clockOptions, settingsOptions;
	ArrayList<ArrayList<MenuItem>> allItems;
	ImageView currentPiece;
	Piece current;
	GridPane board;
	Button voiceCommand, back, enableTextToSpeech;
	VoiceCommandRecognition voiceCommandRec;
	boolean piecePicked, whiteTurn, alreadySelected, flag, firstMove, castling, check, checkMate, textToSpeech, enPassant = false, enPassantOn = false;
	final int IMAGE_TYPES = 6, SQUARE_SIZE = 50, NUMBER_OF_PIECES = 16;
	int [] indices = {5, 2, 0, 4, 1, 0, 2, 5}, takenWhiteCounts = {0,0,0,0,0}, takenBlackCounts = {0,0,0,0,0};
	int lines, enPassantRow = 0, enPassantCol = 0;
	boolean [][] occupied = new boolean[8][8];
	String [] imageLocations = {"Bishop", "King", "Knight", "Pawn", "Queen", "Rook"};	
	String [] menus = {"File", "Competition", "Clock", "Tutorial"};
	String [][] allMenuItems = {{"New", "Save", "Exit"}, {"Player1", "Player2"}, {"Competition", "Speed", "Custom"}, {"Rules", "Matches", "Moves"}};
	private static final String VOICE = "kevin";

	/**
	 * Constructor to initialise the chess board, players, clocks, menus
	 * pieces, images and values.
	 */
	
	public ChessMate () {
		this(new Player("Player1", PlayerType.HUMAN, 0), new Player("Player2", PlayerType.CPU, 0));
	}
	
	public ChessMate (Player player1, Player player2) {
		this(player1, player2, new Clock(ClockMode.OFF, player1), new Clock(ClockMode.OFF, player2));
	}
	
	public ChessMate (Player player1, Player player2, Clock clock1, Clock clock2) {
		//player1 = new Player("Player1", PlayerType.HUMAN, 0);
		//player2 = new Player("Player2", PlayerType.CPU, 0);
		this.player1 = player1;
		this.player2 = player2;
		player1.playerTurn = true;
		
		this.clock1 = clock1;
		this.clock2 = clock2;
//		clock1 = new ChessClock(ClockMode.SPEED, player1);
//		clock2 = new ChessClock(ClockMode.SPEED, player2, 0, 20); 
		
		this.clock1 = clock1;
		this.clock2 = clock2;
		
		root = new BorderPane();
		board = new GridPane();
		
		menuBar = new MenuBar();
		
		tGroup = new ToggleGroup();
		allItems = new ArrayList<>();
		menuOptions = new ArrayList<>();
		menuItemOptions = new ArrayList<>();
		clockOptions = new ArrayList<>();
		settingsOptions = new ArrayList<>();
		
		for(int i = 0; i < menus.length; i++) {
			Menu thisMenu = new Menu(menus[i]);
			menuOptions.add(thisMenu);
		}
		
		for(int i = 0; i < allMenuItems[0].length; i++) {
			MenuItem thisMenuItem = new MenuItem(allMenuItems[0][i]);
			menuItemOptions.add(thisMenuItem);
		}
		menuItemOptions.add(2, new SeparatorMenuItem());
		
		for(int i = 0; i < allMenuItems[1].length; i++) {
			RadioMenuItem compRadioItem = new RadioMenuItem(allMenuItems[1][i]);
			compRadioItem.setToggleGroup(tGroup);
			if(i == 0) {
				compRadioItem.setSelected(true);
			}
			clockOptions.add(compRadioItem);
		}
		clockOptions.add(0, menuOptions.get(2));
		clockOptions.add(1, new SeparatorMenuItem());
		
		for(int i = 0; i < allMenuItems[2].length; i++) {
			CheckMenuItem settingsItem = new CheckMenuItem(allMenuItems[2][i]);
			settingsOptions.add(settingsItem);
		}
		
		playerOptions = new ArrayList<>();
		for(int i = 0; i < allMenuItems[3].length; i++) {
			RadioMenuItem thisCheckMenu = new RadioMenuItem(allMenuItems[3][i]);
			playerOptions.add(thisCheckMenu);
		}
		((RadioMenuItem) playerOptions.get(0)).setSelected(true);
		
		allItems.add(menuItemOptions);
		allItems.add(clockOptions);
		allItems.add(settingsOptions);
		allItems.add(playerOptions);
		
		backButton = new Image("undo.png");
		
		whiteImages = new Image [IMAGE_TYPES];
		blackImages = new Image [IMAGE_TYPES];
		whiteImageViews = new ArrayList<>();
		blackImageViews = new ArrayList<>();
		whitePieces = new ArrayList<>();
		blackPieces = new ArrayList<>();
		rows = new ArrayList<>();
		cols = new ArrayList<>();
		validMoves = new ArrayList<>();
		for(int i = 1, a = 'a'; i <= 8; i ++, a++){
			rows.add(i);
			cols.add((char) a);
		}
		root.setTop(menuBar);
		menuItemOptions.get(3).setOnAction(actionEvent -> Platform.exit());

		for(int i = 0; i < menus.length; i++) {
	    	menuOptions.get(i).getItems().addAll(allItems.get(i));
	    }
		ImageView backImage = new ImageView(backButton);
		backImage.setFitHeight(30);
		backImage.setFitWidth(30);
		Image audioMute = new Image("mute.png");
		Image audio = new Image("audio.png");
		ImageView audioMuteImage = new ImageView(audioMute);
		ImageView audioImage = new ImageView(audio);
		audioMuteImage.setFitHeight(30);
		audioMuteImage.setFitWidth(30);
		audioImage.setFitHeight(30);
		audioImage.setFitWidth(30);
		back = new Button("", backImage);
		back.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	System.out.println("Undone move");
		    	//remove last move from pgn file and then reload game based on file
		    }
		});
		
		enableTextToSpeech = new Button("Audio", audioMuteImage);
		enableTextToSpeech.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	textToSpeech = !textToSpeech;
		    	enableTextToSpeech.setGraphic(textToSpeech? audioImage : audioMuteImage);
		    }
		});
		voiceCommand = new Button("Voice Command");
		voiceCommand.setStyle("-fx-font: 20 arial");
		voiceCommand.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	String s = "";
		    	long timeStart = System.currentTimeMillis();
		    	try {
					Process p = Runtime.getRuntime().exec("python C:\\Users\\Andrew\\Documents\\GitHub\\ChessBoard\\src\\speech \\py_speech_stream.py");
//					BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
//					System.out.println("here is python output: ");
//					while((s = stdInput.readLine()) != null && (System.currentTimeMillis()-timeStart) < 10000) {
//						System.out.println(s);
//					}
					// createMoveFromText();
					 //System.exit(0);
				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					System.err.print("Could not run python code!");
//					e1.printStackTrace();
				}
		    	
		    	
		    }
		    
		    //now handle the text to make the move
		   
		});
        menuBar.getMenus().addAll(menuOptions.get(0), menuOptions.get(1), menuOptions.get(3));
        board.setAlignment(Pos.CENTER);
    	board.setPadding(new Insets(10));
		board.setHgap(0);
		board.setVgap(0);	 
		lines = 1;
		firstMove = true;
	}
	
	public void start(Stage primaryStage) {		
	    menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
	   
	    setInitialOccupiedGrid();
	    
	    moves  = new ArrayList<>();
	    
	    setUpBoard();
	    
	    
	    
		setUpImages(whiteImages, true);
		setUpImages(blackImages, false);
		
		setUpImageViews(whiteImageViews, whitePieces, blackPieces, player1);
		setUpImageViews(blackImageViews, blackPieces, whitePieces, player2);
		
		assignImages(whiteImageViews, whiteImages, indices);
		assignImages(blackImageViews, blackImages, indices);
		
		setUpPieces(whitePieces, whiteImageViews, imageLocations, indices, true);
		setUpPieces(blackPieces, blackImageViews, imageLocations, indices, false);
		
		addPiecesToBoard(board, whitePieces, whiteImageViews, true);
		addPiecesToBoard(board, blackPieces, blackImageViews, false);
			
		board.setStyle("-fx-background-color: #336699;");
		GridPane bottomPane = new GridPane();
		bottomPane.setAlignment(Pos.CENTER);
		bottomPane.setPadding(new Insets(10));
		bottomPane.setHgap(10);
		bottomPane.setVgap(10);	 
		bottomPane.add(voiceCommand, 0, 0);
		bottomPane.add(back, 1, 0);
		bottomPane.add(enableTextToSpeech, 2, 0);
		
		root.setCenter(board);
		root.setBottom(bottomPane);
		//root.setBottom(voiceCommand);
		BorderPane.setAlignment(bottomPane, Pos.CENTER);
		//root.setBottom(back);
		BorderPane.setMargin(bottomPane, new Insets(10, 10, 10, 10));
		BorderPane.setAlignment(board,Pos.CENTER);
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setScene(new Scene(root));
		primaryStage.setX(primaryScreenBounds.getMinX());
		primaryStage.setY(primaryScreenBounds.getMinY());
		primaryStage.setWidth(primaryScreenBounds.getWidth());
		primaryStage.setHeight(primaryScreenBounds.getHeight());
		primaryStage.setTitle("ChessMate");
		primaryStage.show(); 
		
		//Testing out functionality of interpreted textfile
//		try {
//			voiceCommandRec = new VoiceCommandRecognition(player1.playerTurn? player1: player2);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Assign images based on name of pieces
	 * @param images
	 * @param isWhite
	 */
	public void setUpImages(Image [] images, boolean isWhite){
		for (int i = 0; i < IMAGE_TYPES; i++) {
			images[i] = new Image(((isWhite) ? "white" : "black") + imageLocations[i] + ".png");
		}
	}
	
	/**
	 * Set up all the pieces which will fill the board.  This includes
	 * setting up all the events for each piece such as moves, removal etc.
	 * @param pieces
	 * @param player
	 * @param takenPieces
	 */
	public void setUpImageViews(ArrayList<ImageView> imageViews, ArrayList<Piece> pieces, ArrayList<Piece> otherPieces, Player player) {
		for(int i = 0; i < NUMBER_OF_PIECES; i++) {
			ImageView imageView = new ImageView();
			imageView.setFitWidth(SQUARE_SIZE);
			imageView.setFitHeight(SQUARE_SIZE);
			final Integer innerI = new Integer(i);
			imageView.setOnMouseClicked(new EventHandler<MouseEvent>()
	        {
	            @Override
	            public void handle(MouseEvent t) {
	            	if(!pieces.get(innerI).isTaken()){
		            	if(player.playerTurn) {
		            		flag = !flag;
		            		if(!validMoves.isEmpty()) {
		            			removeHighlights(validMoves);
		            			validMoves.clear();
		            			flag = false;
		            		}
		            		
			            		if(current!=null) {
			            			if(!current.equals(pieces.get(innerI))) {
			            				alreadySelected = false;
			            			}else {
			            				if(flag) {
			            					alreadySelected = false;
			            				}
			            			}
			            		}
				            	
		            			if(!alreadySelected) {
		            				currentPiece = imageViews.get(innerI);
					            	current = pieces.get(innerI);
					            	validMoves = getValidMoves(current);
					            	removeOwnColours(pieces, otherPieces, current);
					            	if(!validMoves.isEmpty()) {
					            		highlightMoves(validMoves);
					            	}
		            				alreadySelected = true;
		            			}
		            		
			            	piecePicked = true;
		            	}else {
		            		if(piecePicked) {
		            			
		            			Piece piece = pieces.get(innerI);
		            			piecePicked = false;
		            			ArrayList<Integer> temp = new ArrayList<>();
		            			temp.add(piece.getCol());
		            			temp.add(10 - piece.getRow());
		            			if(validMoves.contains(temp)) {
			            			int [] tempPos = removePiece(piece);
			            			occupied[current.getRow()-2][current.getCol()-1] = false;
		            			
			            			movePiece(board, currentPiece, piece.getCol(), piece.getRow());
			            			String str2 = " ";
			            			String str = " ";
			            			str = recordMove_algebraic_notation(current, current.getCol(), current.getRow(), piece.getCol(), piece.getRow(), true, false, false, false, false);
			            			if(textToSpeech) {
				            			String command = getStringCommand(current, current.getCol(), current.getRow(), piece.getCol(), piece.getRow(), true, false);
					            		speak(command);
			            			}
			            			if(current.isWhite() && firstMove){
			            				str2 = lines++ + ": ";
			            			}else if(current.isWhite()){
			            				str2 = "\r\n" + lines++ + ": ";
			            			}
			            			saveMovesToFile("" + str2 + str);
			            			piece.setTaken(true);
			            			current.setCol(piece.getCol());
			            			current.setRow(piece.getRow());
			            			current.setUnmoved(false);	
			            			firstMove = false;
			            			piece.setCol(tempPos[0]);
			            			piece.setRow(tempPos[1]);
			            			alreadySelected = false;
			            			swapTurns();
			            			showOccupied();	
			            			int []thisPiece = {piece.getCol(), piece.getRow()};
				            		Moves thisMove = new Moves(piece, thisPiece);
				            		moves.add(thisMove);
		            			}
		            			if(!validMoves.isEmpty()) {
		            				removeHighlights(validMoves);
		            				validMoves.clear();
		            			}
		            			//System.out.println("Check: "+ detectCheck(pieces.get(5), pieces, otherPieces));
		            		}
		            		
		            	}
		            }
	            }
	        });
			imageViews.add(imageView);
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
			            			if(current.getType().equals("Pawn") &&  Math.abs(current.getRow() - row) == 2){
			            				current.setEnPassant(true);
			            				enPassant = true;
			            				enPassantRow = current.getRow();
			            				enPassantCol = current.getCol();
			            				System.out.println("Set en passant");
			            			}else {
			            				enPassant = false;
			            				current.setEnPassant(false);
			            				System.out.println("En passant unset");
			            			}
			            			System.out.println("enPassantOn: " + enPassantOn + " type " + current.getType().equals("Pawn") + " col: " + col + " current col: " + current.getCol());
			            			if(enPassantOn && current.getType().equals("Pawn") && current.getCol() != col) {
			            				System.out.println("Git here asdf");
			            				for(Piece piece: otherPieces) {
			            					System.out.println("piece.getRow " + piece.getRow());
			            					if(piece.getRow() == (piece.isWhite()? 6: 5) && piece.getCol() == col) {
			            						int [] tempPos = removePiece(piece);
			            						piece.setTaken(true);
			            						piece.setCol(tempPos[0]);
						            			piece.setRow(tempPos[1]);
			            					}
			            				}
			            			}
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
			            			saveMovesToFile("" + str2 + str);
			            			current.setCol(col);
				            		current.setRow(row);
				            		current.setUnmoved(false);
				            		firstMove = false;
				            		piecePicked = false;
				            		alreadySelected = false;
				            		showOccupied();	
				            		swapTurns();
				            		int []thisPiece = {col, row};
				            		Moves thisMove = new Moves(current, thisPiece);
				            		moves.add(thisMove);
			            		}
			            		if(!validMoves.isEmpty()) {
			            			removeHighlights(validMoves);
			            			validMoves.clear();
			            		}
			            		//System.out.println("Check: "+ detectCheck(otherPieces.get(5), otherPieces, thesePieces));
				            }
				        }
			        });
				}
		
				//Add clock1 to top left of board
				if(a == 0 && b == 0) {
					//clock1 = new ChessClock(ClockMode.CUSTOM, player1, 0, 10);
					//clock1 = new ChessClock(ClockMode.OFF, player1);
					
					pane.setAlignment(Pos.TOP_RIGHT);
					updateClockOnBoard(pane, player1, clock1);
					  Platform.runLater(new Runnable() {
		                  @Override public void run() {
		                    updateClockOnBoard(pane, player1, clock1);  
		                  }
		                });   
					}
				
				//Add clock2 to top right of board
				if(a == 9 && b == 0) {
					//clock2 = new ChessClock(ClockMode.OFF, player2, 0, 20); 
					pane.setAlignment(Pos.TOP_LEFT);
					updateClockOnBoard(pane, player2, clock2);
					
					  Platform.runLater(new Runnable() {
		                  @Override public void run() {
		                    updateClockOnBoard(pane, player2, clock2); 
		                  }
		                });
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
				
				
				board.add(pane, a, b);
			}
		}
	}
	
	/**
	 * Method for taking the audio stream of words from the text file and interpreting 
	 * them into a command to move a chess piece
	 * @throws IOException
	 */
//	public void createMoveFromText() throws IOException{
//		@SuppressWarnings("resource")
//		BufferedReader brTest = new BufferedReader(new FileReader("out.txt"));
//		String text = brTest.readLine().toLowerCase();
//		String[] strArray = text.split(" ");
//		ArrayList<String> pieces = new ArrayList<String> (Arrays.asList(imageLocations));
//		ArrayList<String> theseWords = new ArrayList<String> (Arrays.asList(strArray));
//		ArrayList<String> potentialWords = new ArrayList<>();
//		Piece thisPiece;
//		String type = "";
//		String longest = "";
//		int colTo = 0, rowTo = 0;
//		int index = 1;
//		boolean foundPiece = false;
//		
//		ListIterator<String> iterator = pieces.listIterator();
//	    while (iterator.hasNext())
//	    {
//	        iterator.set(iterator.next().toLowerCase());
//	    }
//		
//		System.out.println("Pieces: " + pieces.toString());
//		System.out.println("These words: " + theseWords.toString());
//		
//		// Stop. text is the first line.
//		System.out.println("Interpreted python text: ");
//		
//		for(String s: strArray) {
//			System.out.println(index++ + ": "+ s);
//			if(pieces.contains(s)) {
//				String st = s.substring(0,1).toUpperCase();
//				String end = s.substring(1,s.length());
//				s = st + end;
//				type = s;
//				foundPiece = true;
//				
//			}
//			else if(!foundPiece){
//				for(int i = 0; i < s.length(); i++) {
//					for(int j = i+1; j < s.length() - i; j++) {
//						String temp = s.substring(i, j);
//						if(pieces.contains(temp)) {
//							potentialWords.add(temp);
//						}
//					}
//				}
//				for(String str: potentialWords) {
//					if(str.length() > longest.length()) {
//						str = longest;
//					}
//				}
//			}
//		}
//
//		if (longest.length() > 2 && !foundPiece) {
//			for(String s: strArray) {
//				if(s.contains(longest)) {
//					type = s;
//				}
//			}
//		}
//		System.out.println("Type: " + type);
//		String nm = player1.playerTurn? "White " : "Black "; 
//		thisPiece = new Piece(nm + type);
//		thisPiece.setCol(colTo);
//		thisPiece.setRow(rowTo);
//		System.out.println("Piece: " + thisPiece.getName());
		
		
		
		/**
		 * Takes the text from out.txt and creates an appropriate move based on which
		 * player's turn it is
		 * @throws IOException
		 */
		public void createMoveFromText() throws IOException{
			@SuppressWarnings("resource")
			BufferedReader brTest = new BufferedReader(new FileReader("out.txt"));
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
			
			ListIterator<String> iterator = pieces.listIterator();
		    while (iterator.hasNext())
		    {
		        iterator.set(iterator.next().toLowerCase());
		    }
			
			System.out.println("Pieces: " + pieces.toString());
			System.out.println("These words: " + theseWords.toString());
			
			// Stop. text is the first line.
			System.out.println("Interpreted python text: ");
			
			for(String s: strArray) {
				potentialWords.clear();
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
						for(int j = i+1; j < s.length() - i + 1; j++) {
							String temp = s.substring(i, j);
							System.out.println("\t --> " + temp);
							for(String piece: pieces) {
								if(piece.contains(temp)) {
									System.out.println("Adding temp: " + temp);
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
			
				for(int i = 0; i < rows.size(); i++) {
					if(s.contains(rows.get(i).toString())) {
						squareTo = s;
						System.out.println("squareTo: " + squareTo);
						rowTo = i+1;
					}
				}
			}
			
			for(int j = 0; j < cols.size(); j++) {
				if(squareTo.contains(cols.get(j).toString())) {
					System.out.println(" cols j: " + cols.get(j));
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
			
			String nm = player1.playerTurn? "White " : "Black "; 
			thisPiece = new Piece(nm + type);
			thisPiece.setCol(colTo);
			thisPiece.setRow(rowTo);
			System.out.println("Piece: " + thisPiece.getName());
			System.out.println("col: " + thisPiece.getCol() + " row: " + thisPiece.getRow());
		
			
			//Need to first get actual pieces from board
//			ImageView thisImage;
//			ObservableList<Node> children = board.getChildren();
//			for(Node node: children){
//				if(GridPane.getRowIndex(node) == rowTo && GridPane.getColumnIndex(node) == colTo){
//					//find whether we are this image and get imageview
//				}
//			}
			
		ArrayList<Integer> temp = new ArrayList<>();
		temp.add(colTo);
		temp.add(rowTo);
		
		
		for(Piece piece: player1.playerTurn? whitePieces : blackPieces) {
			validMoves.clear();
			if(piece.getType().equals(type)) {
				if((getValidMoves(piece)).contains(temp)) {
					movePiece(board, (player1.playerTurn? whiteImageViews : blackImageViews).get((player1.playerTurn? whitePieces : blackPieces).indexOf(piece)), colTo,10 - rowTo);
					swapTurns();
					System.out.println("Got here bud");
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public void showOccupied(){
		//System.out.println("Occupied: ");
		for(int i = 0; i < 8; i ++){
			for(int j = 0; j < 8; j++){
				if(occupied[i][j] == true){
				//System.out.print(1 + " ");
				}else{
				//	System.out.print(0 + " ");
				}
			}
			//System.out.print("\n");
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
	 * Add the ImageViews of chess pieces to the board
	 * @param board
	 * @param pieces
	 */
	public void addPiecesToBoard(GridPane board, ArrayList<Piece> pieces, ArrayList<ImageView> images, boolean isWhite) {
		for(int i = 0; i < 8; i++) {
			if(isWhite) {
				board.add(images.get(i), i+1, 9);
				board.add(images.get(i+8), i+1, 8);
				pieces.get(i).setCol(i+1);
				pieces.get(i).setRow(9);
				pieces.get(i+8).setCol(i+1);
				pieces.get(i+8).setRow(8);
			}else {
				board.add(images.get(i), i+1, 2);
				board.add(images.get(i+8), i+1, 3);
				pieces.get(i).setCol(i+1);
				pieces.get(i).setRow(2);
				pieces.get(i+8).setCol(i+1);
				pieces.get(i+8).setRow(3);
			}
		}
	}
	
	
	
	
	public void setInitialOccupiedGrid(){
		for(int j = 0; j < 8; j++){
			occupied[0][j] = true;
			occupied[1][j] = true;
			occupied[6][j] = true;
			occupied[7][j] = true;
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
	public void updateClockOnBoard(StackPane pane, Player player, Clock clock) {
		if(!(clock.getMode() == ClockMode.OFF)) {
			pane.getChildren().remove(pane.getChildren().size()-1);
			Text text = new Text(player.getName() + ": " + clock.getTimeDisplay());
			text.setFont(Font.font ("Verdana", 20));
			if(player.playerTurn){
				text.setFill(Color.RED);
			}else {
				text.setFill(Color.BLACK);
			}
			pane.getChildren().add(text);
		}
	}
	
	/**
	 * Swap the boolean value determining whether it is that player's
	 * turn and switch the clock which is counting down.
	 */
	public void swapTurns() {
		player1.playerTurn = !player1.playerTurn;
		player2.playerTurn = !player2.playerTurn;
		ObservableList<Node> children = board.getChildren();
		for(Node node: children){
			if(GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) == 0){
				updateClockOnBoard((StackPane) node, player1, clock1);
			}else if(GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) == 9) {
				updateClockOnBoard((StackPane) node, player2, clock2);
			}
		}
		if(!(clock1.getMode() == ClockMode.OFF)) {
			clock1.update(clock1, player1.playerTurn, clock1.time);
		}
		if(!(clock2.getMode() == ClockMode.OFF)) {
		clock2.update(clock2, player2.playerTurn, clock2.time);
		}
		
		
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
	public int[] removePiece(Piece takenPiece){
		ObservableList<Node> children = board.getChildren();
		int row = 0;
		int col = 1;
		Text count;
		boolean addCounter = false;
		if(takenPiece.getType() == "King"){
			System.err.println("You cannot take the king!");
		}
		if(takenPiece.getType() == "Knight"){
			col = 2;
		}else if(takenPiece.getType() == "Bishop"){
			col = 3;
		}else if(takenPiece.getType() == "Rook"){
			col = 4;
		}else if(takenPiece.getType() == "Queen"){
			col = 5;
		}
		if(takenPiece.isWhite()){
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
		movePiece(board, takenPiece.getImage(), col, row);
		takenPiece.setTaken(true);
		int [] temp = {col, row};
		return temp;
	}
	
	/**
	 * Determine whether the king is in check (i.e. an opposite piece is attacking it)
	 * @param king
	 * @param pieces
	 * @param oppositePieces
	 * @return
	 */
	public boolean detectCheck(Piece king, ArrayList<Piece> pieces, ArrayList<Piece> oppositePieces) {
		ArrayList<Integer> kingCoordinates = new ArrayList<>();
		kingCoordinates.add(king.getCol()-1);
		kingCoordinates.add(10 - king.getRow());
		//System.out.println("king coords: " + (king.getCol()-1) + " " + (10 - king.getRow()));
		check = false;
		for(Piece opposite: oppositePieces) {
			validMoves = getValidMoves(opposite);
			removeOwnColours(oppositePieces, pieces, opposite);
			if(validMoves.contains(kingCoordinates)){
				check = true;
			}
			//System.out.println("valid move coords for " + opposite.getType() + ": " + validMoves.toString());
			validMoves.clear();
		}
//		if(check) {
//			checkMate = detectCheckMate(king, pieces, oppositePieces);
//		}
		return check;
	}
	
	/**
	 * Determine whether the king is in checkmate.  This returning true signifies the end of the game.
	 * @param king
	 * @param pieces
	 * @param oppositePieces
	 * @return
	 */
	public boolean detectCheckMate(Piece king, ArrayList<Piece> pieces, ArrayList<Piece> oppositePieces) {
		ArrayList<Integer> kingCoordinates = new ArrayList<>();
		kingCoordinates.add(king.getCol()-1);
		kingCoordinates.add(10 - king.getRow());
		checkMate = false;
		for(Piece opposite: oppositePieces) {
			validMoves = getValidMoves(opposite);
			removeOwnColours(oppositePieces, pieces, opposite);
			if(validMoves.contains(kingCoordinates)){
				check = true;
			}
			validMoves.clear();
		}
		return checkMate;
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
	public String getStringCommand(Piece piece,  int colFrom, int rowFrom,  int colTo, int rowTo, boolean capture, boolean castling){
		String command = "";
		if(castling) {
			if(colTo < 5) {
				command = "Queenside Castle";
			}else {
				command = "Kingside Castle";
			}
		}else {
			command = piece.getType() + " from " + cols.get(colFrom-1) + rows.get(9-rowFrom) + " to " +  cols.get(colTo-1) + rows.get(9-rowTo);
		}
		if (capture) {
			command += "Capture";
		}
		return command;
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
	 
	 /**
	  * This method allows the moves to be recorded and stored in a .txt file
	  */
	 public void saveMovesToFile(String str){
		 if(firstMove) {
			 File game = new File("GameFile.txt");
			 game.delete();
		 }
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
	 public ArrayList<ArrayList<Integer>>getValidMoves(Piece piece){
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
			 			
			 			//White en passant
			 			
			 			if(piece.getRow() == 5) {
			 				//if black pawn adjacent and just moved 2
			 				if(enPassant && Math.abs(enPassantCol - piece.getCol()) == 1){
			 					ArrayList<Integer> enPassantMove =  new ArrayList<>();
			 					enPassantMove.add(enPassantCol);
			 					enPassantMove.add(6);
			 					validMoves.add(enPassantMove);
			 					enPassantOn = true;
			 				}
			 			}
			 			
				 			potentialCol = col;
					 		potentialRow = row++;
					 		if(checkInBounds(potentialCol, potentialRow)){
					 			thisPawnMove.add(col);
					 			thisPawnMove.add(row);
				 		}
			 		}else{
			 			System.out.println("piece " + piece.getRow());
			 			//Black en passant
			 			if(piece.getRow() == 6) {
				 			//if white pawn adjacent and just moved 2
			 				if(enPassant && Math.abs(enPassantCol - piece.getCol()) == 1){
			 					ArrayList<Integer> enPassantMove2 =  new ArrayList<>();
			 					enPassantMove2.add(enPassantCol);
			 					enPassantMove2.add(3);
			 					validMoves.add(enPassantMove2);
			 					enPassantOn = true;
			 				}
				 		}
			 			
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
	  * This removes the squares in the valid moves list which are not valid
	  * due to being occupied by a piece of the same colour
	  * @param pieces
	  * @param piece
	  */
	 public void removeOwnColours(ArrayList<Piece> pieces, ArrayList<Piece> otherPieces, Piece piece) {
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
	 }
	 
	 public void highlightMoves(ArrayList<ArrayList<Integer>> validMoves) {
		 ObservableList<Node> children = board.getChildren();
		 for(ArrayList<Integer> array: validMoves) {
			 for(Node node: children){
				 if(!(node instanceof ImageView)) {
					if(GridPane.getRowIndex(node) == 10 - array.get(1) && GridPane.getColumnIndex(node) == array.get(0)){
						Rectangle rec =  new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
						rec.setFill(Color.GREENYELLOW);
						((Pane) node).getChildren().add(rec);
					}
				 }
			 }
		 }
	 }
	 
	 public void removeHighlights(ArrayList<ArrayList<Integer>> validMoves) {
		 ObservableList<Node> children = board.getChildren();
		 for(ArrayList<Integer> array: validMoves) {
			 for(Node node: children){
				 if(!(node instanceof ImageView)) {
					if(GridPane.getRowIndex(node) == 10 - array.get(1) && GridPane.getColumnIndex(node) == array.get(0)){
						((Pane) node).getChildren().remove(((Pane) node).getChildren().size()-1);
					}
				 }
			 }
		 }
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
