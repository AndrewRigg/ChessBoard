/**
 * @author Andrew
 * This is the main class for creating an instance of the chessboard.
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
	ArrayList<ImageView> whitePieces, blackPieces, takenWhitePieces, takenBlackPieces;
	ArrayList<Piece> white, black;
	ImageView currentPiece;
	int currentCol, currentRow;
	GridPane board;
	StackPane clockPane1, clockPane2;
	boolean piecePicked, whiteTurn;
	final int IMAGE_TYPES = 6, SQUARE_SIZE = 80, NUMBER_OF_PIECES = 16;
	int [] indices = {5, 2, 0, 4, 1, 0, 2, 5};
	String [] cols = {"A", "B", "C", "D", "E", "F", "G", "H"};
	String [] rows = {"1", "2", "3", "4", "5", "6", "7", "8"};
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
		whitePieces = new ArrayList<>();
		blackPieces = new ArrayList<>();
		white = new ArrayList<>();
		black = new ArrayList<>();
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
		String [] imageLocations = {"Bishop.png", "King.png", "Knight.png", "Pawn.png", "Queen.png", "Rook.png"};

		  
		  
		  
		  
		for (int i = 0; i < IMAGE_TYPES; i++) {
			whiteImages[i] = new Image("res/" + "white" + imageLocations[i]);
			blackImages[i] = new Image("res/" + "black" + imageLocations[i]);
			//speak(imageLocations[i].substring(0, imageLocations[i].length()-4));
		}
		
		setUpPieces(whitePieces, player1, takenWhitePieces);
		setUpPieces(blackPieces, player2, takenBlackPieces);
		
		setUpImages(whitePieces, whiteImages, indices);
		setUpImages(blackPieces, blackImages, indices);
		
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
		
		addPiecesToBoard(board, whitePieces, false);
		addPiecesToBoard(board, blackPieces, true);
	
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
	public void setUpPieces(ArrayList<ImageView> pieces, Player player, ArrayList<ImageView> takenPieces) {
		for(int i = 0; i < NUMBER_OF_PIECES; i++) {
			ImageView piece = new ImageView();
			piece.setFitWidth(SQUARE_SIZE);
			piece.setFitHeight(SQUARE_SIZE);
			final Integer innerI = new Integer(pieces.size());
			piece.setOnMouseClicked(new EventHandler<MouseEvent>()
	        {
	            @Override
	            public void handle(MouseEvent t) {
	            	if(player.playerTurn) {
		            	piecePicked = true;
		            	currentPiece = pieces.get(innerI);
		            	currentCol = 2;
		            	currentRow = 2;
	            	}else {
	            		if(piecePicked) {
	            			takenPieces.add(pieces.get(innerI));
	            			//pieces.remove(piece);
	            			piecePicked = false;
	            			swapTurns();
	            			//pane.remove(pieces[innerI]);
	            		}
	            	}
	            }
	        });
			pieces.add(piece);
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
	 * Set the associated images of the chess pieces 
	 * @param pieces
	 * @param images
	 */
	public void setUpImages(ArrayList<ImageView> pieces, Image [] images, int [] indices) {
		for(int j = 0; j < 8; j++) {
			pieces.get(j+8).setImage(images[3]);
			pieces.get(j).setImage(images[indices[j]]);
		}
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
	public void movePiece(GridPane board, ImageView thisPiece, int toCol, int toRow) {
		board.getChildren().remove(thisPiece);
		board.add(thisPiece, toCol, toRow);
	}
	
	public String getStringCommand(ImageView piece, int rowFrom, int colFrom, int rowTo, int colTo){
		return (" from " + cols[rowFrom-1] + rows[8-colFrom] + " to " +  cols[rowTo-1] + rows[8-colTo]);
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
