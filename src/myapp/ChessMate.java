package myapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

public class ChessMate extends Application {

	@Override
	public void start(Stage primaryStage) {
		
		Player player1 = new Player("Player1", PlayerType.HUMAN, 0, 1500, 5); 
		Player player2 = new Player("Player2", PlayerType.CPU, 0, 1500, 5);
		BorderPane root = new BorderPane();
		GridPane board = new GridPane();
		MenuBar menuBar = new MenuBar();
		ChessClock clock = new ChessClock(ClockMode.COMPETITION);
	    menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
	    root.setTop(menuBar);
	    
	    Menu fileMenu = new Menu("File");
	    MenuItem newMenuItem = new MenuItem("New");
	    MenuItem saveMenuItem = new MenuItem("Save");
	    MenuItem exitMenuItem = new MenuItem("Exit");
	    exitMenuItem.setOnAction(actionEvent -> Platform.exit());
	    
	    fileMenu.getItems().addAll(newMenuItem, saveMenuItem,
	            new SeparatorMenuItem(), exitMenuItem);

	        Menu webMenu = new Menu("Clock");
	        CheckMenuItem p1MenuItem = new CheckMenuItem("Player1");
	        p1MenuItem.setSelected(true);
	        webMenu.getItems().add(p1MenuItem);
	        CheckMenuItem p2MenuItem = new CheckMenuItem("Player2");
	        p1MenuItem.setSelected(true);
	        webMenu.getItems().add(p2MenuItem);

	        Menu compMenu = new Menu("Competition");
	        ToggleGroup tGroup = new ToggleGroup();
	        RadioMenuItem mycompItem = new RadioMenuItem("Competition");
	        mycompItem.setToggleGroup(tGroup);
	        mycompItem.setSelected(true);

	        RadioMenuItem speedItem = new RadioMenuItem("Speed");
	        speedItem.setToggleGroup(tGroup);
	        
	        
	        RadioMenuItem customItem = new RadioMenuItem("Custom");
	        customItem.setToggleGroup(tGroup);

	        compMenu.getItems().addAll(mycompItem, speedItem, customItem,
	            new SeparatorMenuItem());

	        Menu tutorialMenu = new Menu("Tutorial");
	        tutorialMenu.getItems().addAll(
	            new CheckMenuItem("Rules"),
	            new CheckMenuItem("Matches"),
	            new CheckMenuItem("Moves"));

	        compMenu.getItems().add(tutorialMenu);

	        menuBar.getMenus().addAll(fileMenu, webMenu, compMenu);
	    
	    
		final int IMAGE_TYPES = 12;
		final int SQUARE_SIZE = 80;
		final int NUMBER_OF_PIECES = 32;
		board.setPadding(new Insets(10));
		board.setHgap(5);
		board.setVgap(5);
		String [] imageLocations = {"blackBishop.png", "blackKing.png", "blackKnight.png", "blackPawn.png", "blackQueen.png", "blackRook.png", 
								   "whiteBishop.png", "whiteKing.png", "whiteKnight.png", "whitePawn.png", "whiteQueen.png", "whiteRook.png"};
		Image [] images = new Image [IMAGE_TYPES];
		ImageView [] pieces = new ImageView [NUMBER_OF_PIECES];
		
		
		for (int i = 0; i < IMAGE_TYPES; i++) {
			images[i] = new Image("res/" + imageLocations[i]);
		}
		for(int i = 0; i < NUMBER_OF_PIECES; i++) {
			pieces[i] = new ImageView();
			pieces[i].setFitWidth(SQUARE_SIZE);
			pieces[i].setFitHeight(SQUARE_SIZE);
		}
		for(int j = 0; j < 8; j++) {
			pieces[j+8].setImage(images[3]);
			pieces[j+16].setImage(images[9]);
		}
	
		
		setUpPieces(pieces, images);
		
		char start = 'a';
		for(int a = 0; a < 10; a++) {
			for(int b = 0; b < 10; b++) {
				StackPane pane = new StackPane();
				Rectangle rec = new Rectangle();
				rec.setWidth(SQUARE_SIZE);
				rec.setHeight(SQUARE_SIZE);		
		
				if(a == 0 && b == 0) {
					Text text = new Text(""+ player1.name + ": " + player1.timeDisplay);
					text.setFont(Font.font ("Verdana", 20));
					text.setFill(Color.RED);	
					pane.getChildren().add(text);
					pane.setAlignment(Pos.TOP_LEFT);
				}
				
				if(a == 9 && b == 0) {
					Text text = new Text(""+ player2.name + ": " + player2.timeDisplay);
					text.setFont(Font.font ("Verdana", 20));
					text.setFill(Color.DARKGRAY);
					pane.getChildren().add(text);
					pane.setAlignment(Pos.TOP_RIGHT);
				}
				
				
				if( a > 0 && a < 9 && b > 0 && b < 9 && (a+b)%2 == 1) {
					rec.setFill(Color.SLATEGRAY);
					 rec.setOnMouseClicked(new EventHandler<MouseEvent>()
				        {
				            @Override
				            public void handle(MouseEvent t) {
				                rec.setFill(Color.RED);
				            }
				        });
				}else if(a > 0 && a < 9 && b > 0 && b < 9){
					rec.setFill(Color.WHITE);
					rec.setOnMouseClicked(new EventHandler<MouseEvent>()
			        {
			            @Override
			            public void handle(MouseEvent t) {
			                rec.setFill(Color.RED);
			            }
			        });
				}
				else {
					rec.setFill(Color.TRANSPARENT);
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
				
				pane.getChildren().addAll(rec);
				board.add(pane, a, b);
			}
		}
		
		for(int i = 0; i < 8; i++) {
			board.add(pieces[i], i+1, 1);
			board.add(pieces[i+8], i+1, 2);
			board.add(pieces[i+16], i+1, 7);
			board.add(pieces[i+24], i+1, 8);
		}
		
		
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
		
		//ChessClock clock = new ChessClock();
		GamePlay game = new GamePlay();
		
		//example of how pieces can be moved
		//movePiece(board, pieces[15], 4, 4);
	}
	
	
	public void setUpPieces(ImageView [] pieces, Image [] images) {
		pieces[0].setImage(images[5]);
		pieces[7].setImage(images[5]);
		pieces[1].setImage(images[2]);
		pieces[6].setImage(images[2]);
		pieces[2].setImage(images[0]);
		pieces[5].setImage(images[0]);
		pieces[3].setImage(images[4]);
		pieces[4].setImage(images[1]);
		
		pieces[0+24].setImage(images[5+6]);
		pieces[7+24].setImage(images[5+6]);
		pieces[1+24].setImage(images[2+6]);
		pieces[6+24].setImage(images[2+6]);
		pieces[2+24].setImage(images[0+6]);
		pieces[5+24].setImage(images[0+6]);
		pieces[3+24].setImage(images[4+6]);
		pieces[4+24].setImage(images[1+6]);
	}
	
	
	public void movePiece(GridPane board, ImageView thisPiece, int toCol, int toRow) {
		board.getChildren().remove(thisPiece);
		board.add(thisPiece, toCol, toRow);
	}
	
	

	public static void main(String[] args) {
		launch(args);
	}
}
