/**
 * @author Andrew Rigg
 * This is the main class for creating an instance of the chess board.
 */
package myapp;

import java.util.ArrayList;

import com.sun.speech.freetts.*;

import javafx.application.*;
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
	final int IMAGE_TYPES = 6, SQUARE_SIZE = 60, NUMBER_OF_PIECES = 16;
	int [] indices = {5, 2, 0, 4, 1, 0, 2, 5};
	int [] takenWhiteCounts = {0,0,0,0,0}, takenBlackCounts = {0,0,0,0,0};
	String [] imageLocations = {"Bishop", "King", "Knight", "Pawn", "Queen", "Rook"};	
	private static final String VOICE = "kevin";
	int currentCol, currentRow;
	
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
		for(int i = 1, a = 'A'; i <= 8; i ++, a++){
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
    	board.setPadding(new Insets(10));
		board.setHgap(5);
		board.setVgap(5);	 
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
	            	if(player.playerTurn) {
		            	currentPiece = imageViews.get(innerI);
		            	current = pieces.get(innerI);
		            	piecePicked = true;
	            	}else {
	            		if(piecePicked) {
	            			Piece piece = pieces.get(innerI);
	            			//This breaks because once taken, there is a different number of pieces
	            			//Possible solution add boolean token to Piece class for taken and get rid of taken arrays
	            			piecePicked = false;
	            			removePiece(piece);
	            			movePiece(board, currentPiece, piece.col, piece.row);
	            			current.col = piece.col;
	            			current.row = piece.row;
	            			swapTurns();
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
			System.out.println("Piece: " + piece.getName());
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
			for(int b = 0; b < 11; b++) {
				StackPane pane = new StackPane();
				pane.setOnMouseClicked(new EventHandler<MouseEvent>()
		        {
		            @Override
		            public void handle(MouseEvent t) {
		            	if(piecePicked) {
		            		
		            		int col = GridPane.getColumnIndex(pane);
		            		int row = GridPane.getRowIndex(pane);
		            		movePiece(board, currentPiece, col, row);
		            		//String command = getStringCommand(current, current.col, current.row, col, row);
		            		current.col = col;
		            		current.row = row;
		            		//speak(command);
		            		piecePicked = false;
		            		swapTurns();
		            	}
		            }
		        });
				
				if(b == 0 || b == 10){
					if(a > 0 && a < 6){
						Text takenCount = new Text("0");
						pane.getChildren().add(takenCount);	
						pane.setAlignment(Pos.TOP_CENTER);
					}
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
					//pane.getChildren().add(updateClockOnBoard(player2, clock2));
					//pane = clockPane2;
				}
				
				if( b > 0 && b < 9 && a == 0) {
					Text text = new Text(""+ (9-b));
					pane.getChildren().add(text);
					pane.setAlignment(Pos.CENTER_RIGHT);
				}
				if( a > 0 && a < 9 && b == 9) {
					
					Text text = new Text(""+ start++);
					pane.getChildren().add(text);
					pane.setAlignment(Pos.TOP_CENTER);
				}
				
				Rectangle rec = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
				createCheckerBoard(rec, a, b);
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
				board.add(images.get(i), i+1, 8);
				board.add(images.get(i+8), i+1, 7);
				pieces.get(i).col = i;
				pieces.get(i).row = 8;
				pieces.get(i+8).col = i;
				pieces.get(i+8).row = 7;
			}else {
				board.add(images.get(i), i+1, 1);
				board.add(images.get(i+8), i+1, 2);
				pieces.get(i).col = i;
				pieces.get(i).row = 1;
				pieces.get(i+8).col = i;
				pieces.get(i+8).row = 2;
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
		System.out.println("Entered updateBoard");
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
	
	public void removePiece(Piece takenPiece){
		int row = 0;
		int col = 1;
		if(takenPiece.isWhite){
			row = 10;
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
//		for(int i = 0; i < .length; i++){
//			
//		}
		movePiece(board, takenPiece.image, col, row);
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
		return (piece.getName() + " from " + cols.get(colFrom) + rows.get(8-rowFrom) + " to " +  cols.get(colTo-1) + rows.get(8-rowTo));
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
	
	public static void main(String[] args) {
		launch(args);
	}
}
