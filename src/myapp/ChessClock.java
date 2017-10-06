/**
 * This class controls the clock entity which will be physically represented 
 * on the board.  It will have several settings including being turned off.
 */

package myapp;

import java.util.Timer;

public class ChessClock{

	ClockMode mode;
	Player player;
	int timeAllowance;
	int minutes;
	int seconds;
	double time;
	String timeDisplay = "";
	Timer timer; 
	
	public ChessClock(ClockMode mode, Player player, int timeAllowance, double time) {
		this.mode = mode;
		this.player = player;
		this.timeAllowance = timeAllowance;
		if(time != 0) {
			mode.setTime(time);
		}
		this.time = 60*mode.getTime();
		System.out.println("time: " + this.time);
		minutes = (int) (this.time / 60);
		seconds = (int) (this.time % 60);
		timeDisplay = "" + minutes + ":" + String.format("%02d", seconds);
		timer = new Timer();
		if(player.playerTurn) {
			timer.schedule(new UpdateClock(this), 0, 1000);
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
				this.time = time;
			}else {
				System.err.println("Cannot change the time parameter for this mode");
			}
		}
		
		public double getTime() {
			return time;
		}
	};