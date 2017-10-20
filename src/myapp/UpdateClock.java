/**
 * @author Andrew
 * This is a class for updating the clock in the timer
 */

package myapp;

import java.util.TimerTask;

public class UpdateClock extends TimerTask{
		
	ChessClock clock;
	
	public UpdateClock(ChessClock clock) {
		this.clock = clock;
	}

	public void run() {
		clock.time--;
		clock.minutes = (int) (clock.time / 60);
		clock.seconds = (int) (clock.time % 60);
		//System.out.println("" + clock.minutes + ":" + String.format("%02d", clock.seconds));
		clock.timeDisplay = "" + clock.minutes + ":" + String.format("%02d", clock.seconds);
	}	
}
