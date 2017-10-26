/**
 * @author Andrew Rigg
 * This is the main class for creating an instance of the chess board.
 */
package main;

import objects.*;
import speech.*;

import com.sun.speech.freetts.*;

import interfaces.GameSaveAndLoad;

import java.io.*;
import java.text.*;
import java.util.*;

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

public class ChessMate extends Application {

	/**
	 * Global variables
	 */
	Timer timer;
	Player player1, player2;
	Clock clock1, clock2;
	String date;
	BorderPane root;
	MenuBar menuBar;
	ToggleGroup tGroup;
	Image [] whiteImages, blackImages;
	Image backButton; 
	ArrayList<ImageView> whiteImageViews, blackImageViews;
	ArrayList<Piece> whitePieces, blackPieces;
//	ArrayList<Integer> rows;
//	ArrayList<Character> cols;
	ArrayList<ArrayList<Integer>> validMoves;
	ArrayList<Menu> menuOptions;
	ArrayList<MenuItem> menuItemOptions, playerOptions, clockOptions, settingsOptions;
	ArrayList<ArrayList<MenuItem>> allItems;
	ImageView currentPiece;
	Piece current;
	GridPane board;
	Button voiceCommand, back, enableTextToSpeech;
	VoiceCommandRecognition voiceCommandRec;
	boolean piecePicked, whiteTurn, alreadySelected, flag, firstMove, castling, check, checkMate, textToSpeech;
	final int IMAGE_TYPES = 6, SQUARE_SIZE = 65, NUMBER_OF_PIECES = 16;
	int [] indices = {5, 2, 0, 4, 1, 0, 2, 5}, takenWhiteCounts = {0,0,0,0,0}, takenBlackCounts = {0,0,0,0,0};
	int lines, saveFile = 0;
	boolean [][] occupied = new boolean[8][8];
	String [] imageLocations = {"Bishop", "King", "Knight", "Pawn", "Queen", "Rook"};	
	String [] menus = {"File", "Competition", "Clock", "Tutorial"};
	String [][] allMenuItems = {{"New", "Save", "Exit"}, {"Player1", "Player2"}, {"Competition", "Speed", "Custom"}, {"Rules", "Matches", "Moves"}};
//	private static final String VOICE = "kevin";
	ValidMoves valid;
	GameSaveAndLoad gameData;
	CommandTextToSpeech tts;

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
		this.player1 = player1;
		this.player2 = player2;
		player1.playerTurn = true;
		
		this.clock1 = clock1;
		this.clock2 = clock2;
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
		
		backButton = new Image("res/undo.png");
		
		whiteImages = new Image [IMAGE_TYPES];
		blackImages = new Image [IMAGE_TYPES];
		whiteImageViews = new ArrayList<>();
		blackImageViews = new ArrayList<>();
		whitePieces = new ArrayList<>();
		blackPieces = new ArrayList<>();
//		rows = new ArrayList<>();
//		cols = new ArrayList<>();
		validMoves = new ArrayList<>();
//		for(int i = 1, a = 'a'; i <= 8; i ++, a++){
//			rows.add(i);
//			cols.add((char) a);
//		}
		root.setTop(menuBar);
		menuItemOptions.get(3).setOnAction(actionEvent -> Platform.exit());

		for(int i = 0; i < menus.length; i++) {
	    	menuOptions.get(i).getItems().addAll(allItems.get(i));
	    }
		ImageView backImage = new ImageView(backButton);
		backImage.setFitHeight(30);
		backImage.setFitWidth(30);
		Image audioMute = new Image("res/mute.png");
		Image audio = new Image("res/audio.png");
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
//		    	try {
//					Process p = Runtime.getRuntime().exec("python C:\\Users\\Andrew\\Documents\\GitHub\\ChessBoard\\src\\myapp\\py_speech_stream.py");
//					BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
//					System.out.println("here is python output: ");
//					while((s = stdInput.readLine()) != null && (System.currentTimeMillis()-timeStart) < 10000) {
//						System.out.println(s);
//					}
//					 //createMoveFromText();
//					 //System.exit(0);
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					System.err.print("Could not run python code!");
//					e1.printStackTrace();
//				}
		    	
		    	
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
	    
	    Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yy");
		System.out.println("Current Date: " + ft.format(dNow));
		date = ft.format(dNow);
	    
		valid = new ValidMoves(this);
	    
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
		try {
			voiceCommandRec = new VoiceCommandRecognition(player1.playerTurn? player1: player2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
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
					            	validMoves = valid.getBasicValidMoves(current);
					            	validMoves = valid.removeOwnColours(pieces, otherPieces, current);
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
			            			//str = recordMove_algebraic_notation(current, current.getCol(), current.getRow(), piece.getCol(), piece.getRow(), true, false, false, false, false);
			            			str = gameData.recordMove_algebraic_notation(current, current.getCol(), current.getRow(), piece.getCol(), piece.getRow(), true, false, false, false, false);
			            			if(textToSpeech) {
			            				String command = tts.getStringCommand(current, current.getCol(), current.getRow(), piece.getCol(), piece.getRow(), true, false);
				            			//String command = getStringCommand(current, current.getCol(), current.getRow(), piece.getCol(), piece.getRow(), true, false);
			            				tts.speak(command);
					            		//speak(command);
			            			}
			            			if(current.isWhite() && firstMove){
			            				str2 = lines++ + ": ";
			            			}else if(current.isWhite()){
			            				str2 = "\r\n" + lines++ + ": ";
			            			}
			            			if(firstMove) {
			            				saveFile = determineSaveFile();
			            			}
			            			piece.setTaken(true);
			            			piece.setCol(tempPos[0]);
			            			piece.setRow(tempPos[1]);
			            			
			            			actionMove(str, str2, piece.getCol(), piece.getRow()); 
		            			}
		            			if(!validMoves.isEmpty()) {
		            				removeHighlights(validMoves);
		            				validMoves.clear();
		            			}
		            			System.out.println("Check: "+ detectCheck(pieces.get(5), pieces, otherPieces));
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
					            		String command = tts.getStringCommand(current, current.getCol(), current.getRow(), col, row, false, castling);
					            		tts.speak(command);
				            		}
				            		occupied[current.getRow()-2][current.getCol()-1] = false;
			            			String str2 = " ";
			            			String str = gameData.recordMove_algebraic_notation(current, current.getCol(), current.getRow(), col, row, false, false, false, false, castling);
			            			if(current.isWhite() && firstMove){
			            				str2 = lines++ + ": ";
			            			}else if(current.isWhite()){
			            				str2 = "\r\n" + lines++ + ": ";
			            			}
			            			if(firstMove) {
			            				saveFile = determineSaveFile();
			            			}
			            			actionMove(str, str2, col, row); 
			            		}
			            		if(!validMoves.isEmpty()) {
			            			removeHighlights(validMoves);
			            			validMoves.clear();
			            		}
			            		System.out.println("Check: "+ detectCheck(otherPieces.get(5), otherPieces, thesePieces));
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
	
	public void actionMove(String str, String str2, int col, int row) {
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
	
	public void showOccupied(){
		System.out.println("Occupied: ");
		for(int i = 0; i < 8; i ++){
			for(int j = 0; j < 8; j++){
				if(occupied[i][j] == true){
				System.out.print(1 + " ");
				}else{
					System.out.print(0 + " ");
				}
			}
			System.out.print("\n");
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
	
	/**
	 * Initialise the occupied grid to show the 
	 * positions of pieces on the board
	 */
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
		rec.setFill(valid.checkInBounds(a, b)? ((a+b)%2 == 1 ? Color.SLATEGRAY : Color.WHITE) : Color.TRANSPARENT);
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
			text.setFill(player.playerTurn? Color.RED : Color.BLACK);
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
		//This doesn't work
		//((RadioMenuItem) clockOptions.get(player1.playerTurn? 0 : 1)).setSelected(true);
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
		System.out.println("king coords: " + kingCoordinates.toString());
		check = false;
		for(Piece opposite: oppositePieces) {
        	validMoves = valid.getBasicValidMoves(opposite);
			validMoves = valid.removeOwnColours(oppositePieces, pieces, opposite);
			if(validMoves.contains(kingCoordinates)){
				check = true;
			}
			System.out.println("valid move coords for " + opposite.getType() + ": " + validMoves.toString());
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
        	validMoves = valid.getBasicValidMoves(opposite);
			validMoves = valid.removeOwnColours(oppositePieces, pieces, opposite);
			if(validMoves.contains(kingCoordinates)){
				check = true;
			}
			validMoves.clear();
		}
		return checkMate;
	}
	
//	/**
//	 * Set the string to be read out by the text-to-speech program
//	 * (this allows the CPU to let the player know what has been moved)
//	 * @param piece
//	 * @param colFrom
//	 * @param rowFrom
//	 * @param colTo
//	 * @param rowTo
//	 * @return
//	 */
//	public String getStringCommand(Piece piece,  int colFrom, int rowFrom,  int colTo, int rowTo, boolean capture, boolean castling){
//		String command = "";
//		if(castling) {
//			if(colTo < 5) {
//				command = "Queenside Castle";
//			}else {
//				command = "Kingside Castle";
//			}
//		}else {
//			command = piece.getType() + " from " + cols.get(colFrom-1) + rows.get(9-rowFrom) + " to " +  cols.get(colTo-1) + rows.get(9-rowTo);
//		}
//		if (capture) {
//			command += "Capture";
//		}
//		return command;
//	}
//	
//	/**
//	 * FreeTTS text-to-speech facility for reading out moves for CPU (and player).
//	 * 
//	 */
//	 public void speak(String text) {
//		  Voice voice;
//		  VoiceManager voiceManager = VoiceManager.getInstance();
//		  voice = voiceManager.getVoice(VOICE);
//		  voice.allocate();
//		  voice.speak(text);
//		 }
	
//	 /**
//	  * Return a string with the algebraic notation of the chess move which can be 
//	  * used to save, load or record a game
//	  * @param args
//	  */
//	 public String recordMove_algebraic_notation(Piece piece, int colFrom, int rowFrom, int colTo, int rowTo, 
//			 boolean capture, boolean ambiguousCol, boolean ambiguousRow, boolean ambiguousBoth, boolean castling){
//		 String move = "";
//		 //Could create method to determine if it will be ambiguous or not, this would 
//		 //require knowing all the potential squares which identical pieces can move to
//		 if(ambiguousRow){
//			 move += cols.get(colFrom-1);
//		 }else if(ambiguousCol){
//			 move += rows.get(9-rowFrom);
//		 }else if(ambiguousBoth){
//			 move += cols.get(colFrom-1) + rows.get(9-rowFrom);
//		 }
//		 move += piece.getNotation();
//		 if(capture){
//			 //If pawn is capturing then we need a way of making the piece unambiguous
//			 //Here, two identical pieces could move to this square from the same row so
//			 //we need to prefix the move with the column of departure of the piece
//			 if(piece.getType().equals("Pawn") || ambiguousRow){
//				 move += cols.get(colFrom-1);
//			 }else if(ambiguousCol){
//			 //Here, two identical pieces could move to this square from the same column so
//			 //we need to prefix the move with the row of departure of the piece
//				 move += rows.get(9-rowFrom);
//			 }else if(ambiguousBoth){
//			 //This case only occurs very rarely when at least one pawn has been promoted and so 
//			 //three or more identical pieces could move to the same square, thus both the row and 
//			 //column of departure must be prefixed
//				 move += cols.get(colFrom-1) + rows.get(9-rowFrom);
//			 }
//			 move += "x";
//		 }
//		 move += "" + cols.get((colTo-1)) + rows.get(9-rowTo);
//		 if(castling) {
//			 move = "0-0";
//			 if(colTo == 3) {
//				 move += "-0";
//			 }
//		 }
//		 return move;
//	 }
	 
	 
	 public int determineSaveFile() {
	     Scanner s = null;
	     File saved = new File("src/files/savedgames.txt");
		 try {
	            s = new Scanner(saved);
	            while(s.hasNext()){
	                saveFile = s.nextInt();
	            }
		 System.out.println("save file: " + saveFile);
		 }catch (IOException e){
			 System.err.println("No file found!");
		 }
		 
		 try(FileWriter fw = new FileWriter(saved, true);
			  BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			 {
			    out.print("\n" + ++saveFile);
		 } catch (IOException e1) {
			 // TODO Auto-generated catch block
			 e1.printStackTrace();
		 }
		 System.out.println("Updated savefile: " + saveFile);
		 return saveFile;
	 }
	 
	 /**
	  * This method allows the moves to be recorded and stored in a .txt file
	  */
	 public void saveMovesToFile(String str){
		 File game = null;
		 String filename = "src/files/game_file_" + saveFile + "_" + date + ".pgn";
		 System.out.println("filename: " + filename);
		 if(firstMove) {
			 game = new File(filename);
			 game.delete();
		 }else {
			 game = new File(filename);
		 }
		 try(FileWriter fw = new FileWriter(game, true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw))
		 {
		    out.print("" + str);
		 } catch (IOException e) {
			 System.err.println("IO Exception!");
		 }
	 }
	 
	 /**
	  * Highlight grid squares which a piece can potentially move to
	  * @param validMoves
	  */
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
	 
	 /**
	  * Remove the highlighted grid which shows the potential moves a piece can make
	  * @param validMoves
	  */
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
	 	 
	public static void main(String[] args) {
		launch(args);
	}
}
