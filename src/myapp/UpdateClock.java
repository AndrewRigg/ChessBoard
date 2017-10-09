/**
 * @author Andrew
 * This is a class for updating the clock in the timer
 */

package myapp;

import java.util.TimerTask;
//import javafx.scene.layout.StackPane;

public class UpdateClock extends TimerTask{
		
	ChessClock clock;
	ChessMate chess;
	double time;
	int minutes, seconds;
	Player player;
	String timeDisplay = "";
	
	public UpdateClock(ChessClock clock, ChessMate chess) {
		this.clock = clock;
		this.chess = chess;
		this.time = clock.time;
		this.minutes = clock.minutes;
		this.seconds = clock.seconds;
		this.timeDisplay = clock.timeDisplay;
		player = clock.player;
		
	}

	public void run() {
		time--;
		minutes = (int) (time / 60);
		seconds = (int) (time % 60);
		System.out.println("" + minutes + ":" + String.format("%02d", seconds));
		timeDisplay = "" + minutes + ":" + String.format("%02d", seconds);
		//System.out.println("chess: " + chess.board);
		//chess.updateBoard((StackPane) chess.board.getChildren().get(0), player, clock);
	}	
}
