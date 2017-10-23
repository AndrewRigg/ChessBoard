package main;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import objects.Clock;
import objects.ClockMode;
import objects.Player;
import objects.PlayerType;

public class StartMenu  extends Application{
	
	BorderPane root;
	GridPane menu;
	Button players, clock, start;
	ChessMate chessMate;
	Player player1, player2;
	Clock clock1, clock2;
	MenuBar menuBar;
	
	public StartMenu() {
		root = new BorderPane();
		menu = new GridPane();
		menu.setStyle("-fx-background-color: #336699;");
		players = new Button("Select Players");
		clock = new Button("Clock On");
		start = new Button("Start Game");
		
		menuBar = new MenuBar();
	    player1 = new Player("Player1", PlayerType.HUMAN, 0);
        player2 = new Player("Player2", PlayerType.CPU, 0);
        
        clock1 = new Clock(ClockMode.SPEED, player1);
        clock2 = new Clock(ClockMode.SPEED, player2);
		
		players.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	//Allow the user to select the types of players
		    	player1 = new Player("Player1", PlayerType.HUMAN, 0);
		        player2 = new Player("Player2", PlayerType.CPU, 0);
		    }
		});
		clock.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	//Allow the user to select the types of clocks
		        clock1 = new Clock(ClockMode.SPEED, player1);
		        clock2 = new Clock(ClockMode.SPEED, player2);
		    }
		});
	
		
		menu.add(players, 0, 0);
		menu.add(clock, 0, 1);
		menu.add(start, 0, 2);
		
		menu.setAlignment(Pos.CENTER);
		menu.setPadding(new Insets(10));
		menu.setHgap(0);
		menu.setVgap(0);
		
		
		
		BorderPane.setAlignment(menu,Pos.CENTER);
		
		root.setCenter(menu);
	}
	
	public void start(Stage primaryStage) throws IOException {		
		 menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		
	
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setScene(new Scene(root));
		primaryStage.setX(primaryScreenBounds.getMinX());
		primaryStage.setY(primaryScreenBounds.getMinY());
		primaryStage.setWidth(primaryScreenBounds.getWidth());
		primaryStage.setHeight(primaryScreenBounds.getHeight());
		primaryStage.setTitle("ChessMate");
		primaryStage.show(); 
		
		
		//Process p = Runtime.getRuntime().exec("python py_speech_stream.py");
		
		start.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        chessMate = new ChessMate(player1, player2, clock1, clock2);
		        chessMate.start(primaryStage);
		    }
		});
		
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
