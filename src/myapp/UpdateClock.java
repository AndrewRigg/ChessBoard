package myapp;

import java.util.TimerTask;

public class UpdateClock extends TimerTask{
		
	double time;
	int minutes;
	int seconds;
	boolean playerTurn;
	String timeDisplay = "";
	
	public UpdateClock(ChessClock clock) {
		this.time = clock.time;
		this.minutes = clock.minutes;
		this.seconds = clock.seconds;
		this.playerTurn = playerTurn;
		this.timeDisplay = clock.timeDisplay;
	}

	public void run() {
		time--;
		minutes = (int) (time / 60);
		seconds = (int) (time % 60);
		timeDisplay = "" + minutes + ":" + String.format("%02d", seconds);
		playerTurn = !playerTurn;
	}	
}
