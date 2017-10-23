/**
 * @author Andrew Rigg
 * This is a class for updating the clock in the timer
 */

package objects;

import java.util.TimerTask;

public class UpdateClock extends TimerTask{
		
	Clock clock;
	
	public UpdateClock(Clock clock) {
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
