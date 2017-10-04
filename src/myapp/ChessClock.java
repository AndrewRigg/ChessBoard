/**
 * This class controls the clock entity which will be 
 * physically represented on the board.  It could have several 
 * settings including being turned off.
 */


package myapp;

import java.time.*;

public class ChessClock {

	double p1Time;
	double p2Time;
	double timeAllowance;
	int p1Minutes;
	int p2Minutes;
	int p1Seconds;
	int p2Seconds;
	String p1Display = "";
	String p2Display = "";
	boolean p1ClockRunning = true;
	
	public ChessClock(ClockMode mode, double p1Time, double p2Time) {
		
		//every second update the display
		long start = System.currentTimeMillis();
		
		long end = System.currentTimeMillis();
		if((end - start) == 1000){
			//updateClock(){
			
		}
	}	
	
//	public updateClock(Player player) {
//		p1Minutes 
//		p1Display = p1Minutes + ":" + p1Seconds;
//	}
	
	
}




enum ClockMode {
	COMPETITION (25.0, 25.0), 
	SPEED (5.0, 5.0), 
	CUSTOM (0.0,0.0), 
	OFF (0.0,0.0);
	
		private double p1Time, p2Time;
		ClockMode(double p1Time, double p2Time){
			this.p1Time = p1Time;
			this.p2Time = p2Time;
		}
		
		public void setTime(double p1, double p2) {
			p1Time = p1;
			p2Time = p2;
		}
		
		public double getP1Time() {
			return p1Time;
		}
		
		public double getP2Time() {
			return p2Time;
		}
		
	};