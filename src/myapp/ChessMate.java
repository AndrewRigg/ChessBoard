/**
 * @author Andrew Rigg
 * This is the main class for creating an instance of the chess board.
 */
package myapp;

import java.util.ArrayList;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
	ArrayList<ImageView> whiteImageViews, blackImageViews, takenWhitePieces, takenBlackPieces;
	ArrayList<Piece> whitePieces, blackPieces;
	ImageView currentPiece;
	Piece current;
	int currentCol, currentRow;
	GridPane board;
	StackPane clockPane1, clockPane2;
	boolean piecePicked, whiteTurn;
	final int IMAGE_TYPES = 6, SQUARE_SIZE = 60, NUMBER_OF_PIECES = 16;
	int [] indices = {5, 2, 0, 4, 1, 0, 2, 5};
	ArrayList<Integer> rows;
	ArrayList<Character> cols;
	private static final String VOICE = "kevin";
	
	/**
	 * Constructor to initialise the chess board, players, clocks, menus
	 * pieces, images and values.
	 */
	public ChessMate () {
		player1 = new Player("Player1", PlayerType.HUMAN, 0); 
		player1.playerTurn = true;
		player2 = new Player("Player2", PlayerType.CPU, 0);
		root = new BorderPane();
		board = new GridPane();
		clock1 = new ChessClock(ClockMode.CUSTOM, player1, 5, 10);
		clock2 = new ChessClock(ClockMode.CUSTOM, player2, 10, 20); 
		menuBar = new MenuBar();
		fileMenu = new Menu("File");
		compMenu = new Menu("Competition");
		tutorialMenu = new Menu("Tutorial");
		newMenuItem = new MenuItem("New");
		saveMenuItem = new MenuItem("Save");
		exitMenuItem = new MenuItem("Exit");
		p1MenuItem = new CheckMenuItem("Player1");
		p2MenuItem = new CheckMenuItem("Player2");
		webMenu = new Menu("Clock");
		tGroup = new ToggleGroup();
		mycompItem = new RadioMenuItem("Competition");
		speedItem = new RadioMenuItem("Speed");
		customItem = new RadioMenuItem("Custom");
		takenWhitePieces = new ArrayList<>();
		takenBlackPieces = new ArrayList<>();
		whiteImages = new Image [IMAGE_TYPES];
		blackImages = new Image [IMAGE_TYPES];
		whiteImageViews = new ArrayList<>();
		blackImageViews = new ArrayList<>();
		whitePieces = new ArrayList<>();
		blackPieces = new ArrayList<>();
		rows = new ArrayList<Integer>();
		cols = new ArrayList<Character>();
		for(int i = 1, a = 'A'; i <= 8; i ++, a++){
			rows.add(i);
			cols.add((char) a);
		}
	}
	
	public void start(Stage primaryStage) {		
		
	    menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
	    root.setTop(menuBar);
	    exitMenuItem.setOnAction(actionEvent -> Platform.exit());
	    fileMenu.getItems().addAll(newMenuItem, saveMenuItem, new SeparatorMenuItem(), exitMenuItem);
        p1MenuItem.setSelected(true);
        webMenu.getItems().add(p1MenuItem);
        p1MenuItem.setSelected(true);
        webMenu.getItems().add(p2MenuItem);
        mycompItem.setToggleGroup(tGroup);
        mycompItem.setSelected(true);
        speedItem.setToggleGroup(tGroup);
        customItem.setToggleGroup(tGroup);
        compMenu.getItems().addAll(mycompItem, speedItem, customItem,
        new SeparatorMenuItem());
        tutorialMenu.getItems().addAll(
        new CheckMenuItem("Rules"),
        new CheckMenuItem("Matches"),
        new CheckMenuItem("Moves"));
        compMenu.getItems().add(tutorialMenu);
        menuBar.getMenus().addAll(fileMenu, webMenu, compMenu);
	
		board.setPadding(new Insets(10));
		board.setHgap(5);
		board.setVgap(5);
		String [] imageLocations = {"Bishop", "King", "Knight", "Pawn", "Queen", "Rook"};

		  
		  
		  
		  
		for (int i = 0; i < IMAGE_TYPES; i++) {
			whiteImages[i] = new Image("res/" + "white" + imageLocations[i] + ".png");
			blackImages[i] = new Image("res/" + "black" + imageLocations[i] + ".png");
			//speak(imageLocations[i].substring(0, imageLocations[i].length()-4));
		}
		
		setUpImageViews(whiteImageViews, player1, takenWhitePieces);
		setUpImageViews(blackImageViews, player2, takenBlackPieces);
		
		setUpImages(whiteImageViews, whiteImages, indices);
		setUpImages(blackImageViews, blackImages, indices);
		
		setUpPieces(whitePieces, whiteImageViews, imageLocations, indices);
		setUpPieces(blackPieces, blackImageViews, imageLocations, indices);
		
		setUpBoard();
		
		addPiecesToBoard(board, whiteImageViews, false);
		addPiecesToBoard(board, blackImageViews, true);
	
		System.out.println("chesses: " + board.getChildren());
		
		board.setStyle("-fx-background-color: #336699;");
		BorderPane p = new BorderPane();
		Text t = new Text("Hello FX");
		t.setFont(Font.font("Arial", 60));
		t.setEffect(new DropShadow());
		p.setCenter(t);
		root.setCenter(board);
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("ChessMate");
		primaryStage.show();
		
		//GamePlay game = new GamePlay();
		System.out.println("chesses: " + board.getChildren().get(0));
		//updateBoard((StackPane)board.getChildren().get(0), player1, clock1);
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
	 * Set up all the pieces which will fill the board.  This includes
	 * setting up all the events for each piece such as moves, removal etc.
	 * @param pieces
	 * @param player
	 * @param takenPieces
	 */
	public void setUpImageViews(ArrayList<ImageView> imageViews, Player player, ArrayList<ImageView> takenPieces) {
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
		            	piecePicked = true;
		            	currentPiece = imageViews.get(innerI);
		            	currentCol = 2;
		            	currentRow = 2;
	            	}else {
	            		if(piecePicked) {
	            			takenPieces.add(imageViews.get(innerI));
	            			//pieces.remove(piece);
	            			piecePicked = false;
	            			swapTurns();
	            			//pane.remove(pieces[innerI]);
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
	public void setUpImages(ArrayList<ImageView> imageViews, Image [] images, int [] indices) {
		for(int j = 0; j < 8; j++) {
			imageViews.get(j+8).setImage(images[3]);
			imageViews.get(j).setImage(images[indices[j]]);
		}
	}
	
	/**
	 * Set the piece objects to contain the coordinates, the image and the name of the piece
	 * @param pieces
	 * @param images
	 * @param imageLocations
	 * @param indices
	 */
	public void setUpPieces(ArrayList<Piece> pieces, ArrayList<ImageView> images, String [] imageLocations, int [] indices) {
		for(int i = 0; i < NUMBER_OF_PIECES; i++) {
			Piece piece;
			if(i > 7){
				piece = new Piece(imageLocations[3], 0, 0, images.get(i));
			}else{
				piece = new Piece(imageLocations[indices[i]], 0, 0, images.get(i));
			}
			pieces.add(piece);
		}
	}
	
	public void setUpBoard(){
		char start = 'a';
		piecePicked = false;
		for(int a = 0; a < 10; a++) {
			for(int b = 0; b < 10; b++) {
				StackPane pane = new StackPane();
				pane.setOnMouseClicked(new EventHandler<MouseEvent>()
		        {
		            @Override
		            public void handle(MouseEvent t) {
		            	if(piecePicked) {
		            		int col = GridPane.getColumnIndex(pane);
		            		int row = GridPane.getRowIndex(pane);
		            		movePiece(board, currentPiece, col, row);
		            		piecePicked = false;
		            		String command = getStringCommand(currentPiece, currentCol, currentRow, col, row);
		            		speak(command);
		            		swapTurns();
		            	}
		            }
		        });
				Rectangle rec = new Rectangle();
				rec.setWidth(SQUARE_SIZE);
				rec.setHeight(SQUARE_SIZE);		
		
				if(a == 0 && b == 0) {
					pane.setAlignment(Pos.TOP_LEFT);
					updateBoard(pane, player1, clock1);
					pane.getChildren().add(updateClockOnBoard(player1, clock1));
					//pane = clockPane1;
				}
				
				if(a == 9 && b == 0) {
					pane.setAlignment(Pos.TOP_RIGHT);
					updateBoard(pane, player2, clock2);
					pane.getChildren().add(updateClockOnBoard(player2, clock2));
					//pane = clockPane2;
				}
				
				createCheckerBoard(rec, a, b);
				
				
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
				
				pane.getChildren().addAll(rec);
				board.add(pane, a, b);
			}
		}
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
	 * Add the ImageViews of chess pieces to the board
	 * @param board
	 * @param pieces
	 */
	public void addPiecesToBoard(GridPane board, ArrayList<ImageView> pieces, boolean black) {
		for(int i = 0; i < 8; i++) {
			if(black) {
				board.add(pieces.get(i), i+1, 1);
				board.add(pieces.get(i+8), i+1, 2);
			}else {
				board.add(pieces.get(i), i+1, 8);
				board.add(pieces.get(i+8), i+1, 7);
			}
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
	public void movePiece(GridPane board, ImageView piece, int toCol, int toRow) {
		board.getChildren().remove(piece);
		board.add(piece, toCol, toRow);
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
	public String getStringCommand(ImageView piece,  int colFrom, int rowFrom,  int colTo, int rowTo){
		return (" from " + rows.get(rowFrom-1) + cols.get(8-colFrom) + " to " +  cols.get(colTo-1) + rows.get(8-rowTo));
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
