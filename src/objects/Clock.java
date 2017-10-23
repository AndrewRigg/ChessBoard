/**@author Andrew Rigg
 * This class controls the clock entity which will be physically represented 
 * on the board.  It will have several settings including being turned off.
 */

package objects;

import java.util.Timer;

public class Clock{
	ClockMode mode;
	Player player;
	int timeAllowance;
	public ClockMode getMode() {
		return mode;
	}

	public int minutes, seconds;
	public double time;
	String timeDisplay = "";
	Timer timer; 
	
	public Clock(ClockMode mode, Player player) {
		this(mode, player, 0);
	}
	
	public Clock(ClockMode mode, Player player, int timeAllowance) {
		this(mode, player, timeAllowance, 0);
	}
	
	public Clock(ClockMode mode, Player player, int timeAllowance, double time) {
		this.mode = mode;
		this.player = player;
		this.timeAllowance = 1000*timeAllowance;
		if(time != 0) {
			mode.setTime(time);
		}
		this.time = 60*mode.getTime();
		minutes = (int) (this.time / 60);
		seconds = (int) (this.time % 60);
		timeDisplay = "" + minutes + ":" + String.format("%02d", seconds);
		timer = new Timer();
		update(this, player.playerTurn, time);
	}
	
	public void update(Clock clock, boolean playerTurn, double time) {
		if(playerTurn) {
			timer.schedule(new UpdateClock(clock), timeAllowance, 1000);
		}else {
			timer.cancel();
			timer = new Timer();
		}
	}
	
	public void setMode(ClockMode mode) {
		this.mode = mode;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getTimeAllowance() {
		return timeAllowance;
	}

	public void setTimeAllowance(int timeAllowance) {
		this.timeAllowance = timeAllowance;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public String getTimeDisplay() {
		return timeDisplay;
	}

	public void setTimeDisplay(String timeDisplay) {
		this.timeDisplay = timeDisplay;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}
}