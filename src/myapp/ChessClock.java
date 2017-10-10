/**@author Andrew
 * This class controls the clock entity which will be physically represented 
 * on the board.  It will have several settings including being turned off.
 */

package myapp;

import java.util.Timer;

public class ChessClock{
	ClockMode mode;
	Player player;
	int timeAllowance;
	public int minutes, seconds;
	public double time;
	String timeDisplay = "";
	Timer timer; 
	
	public ChessClock(ClockMode mode, Player player, int timeAllowance) {
		this(mode, player, timeAllowance, 0);
	}
	
	public ChessClock(ClockMode mode, Player player, int timeAllowance, double time) {
		this.mode = mode;
		this.player = player;
		this.timeAllowance = timeAllowance;
		if(time != 0) {
			mode.setTime(time);
		}
		this.time = 60*mode.getTime();
		minutes = (int) (this.time / 60);
		seconds = (int) (this.time % 60);
		timeDisplay = "" + minutes + ":" + String.format("%02d", seconds);
		timer = new Timer();
		update(player.playerTurn, time);
	}
	
	public void update(boolean playerTurn, double time) {
		if(playerTurn) {
			//timer = new Timer();
			timer.schedule(new UpdateClock(this), 0, 1000);
		}else {
			timer.cancel();
			timer = new Timer();
		}
	}
}

enum ClockMode{
	
	COMPETITION (25.0), 
	SPEED (5.0), 
	CUSTOM (0.0), 
	OFF (0.0);
	
		private double time;
		ClockMode(double time){
			this.time = time;
		}
		
		public void setTime(double time) {
			if (this == ClockMode.CUSTOM) {
				if(time < 1) {
					System.err.println("Cannot change the time parameter to this value");
					time = 1;
				}else if(time > 60) {
					System.err.println("Cannot change the time parameter to this value");
					time = 60;
				}
				this.time = time;
			}else {
				System.err.println("Cannot change the time parameter for this mode");
			}
		}
		
		public double getTime() {
			return time;
		}
	};