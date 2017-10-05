/**
 * This class controls the clock entity which will be 
 * physically represented on the board.  It could have several 
 * settings including being turned off.
 */


package myapp;

public class ChessClock {


	Player p1, p2;
	boolean playerTurn = true;
	
	public ChessClock(ClockMode mode, double p1Time, double p2Time) {
		
		//every second update the display
		long start = System.currentTimeMillis();
		
		long end = System.currentTimeMillis();
		if((end - start) == 1000){
			if(playerTurn) {
				updateClock(p1);
			}else {
				updateClock(p2);
			}
		}
			
	}	
	
	public void updateClock(Player player) {
		p1.time--;
		p1.minutes = (int) (p1.time /60);
		p1.seconds = (int) (p1.time %60);
		p1.timeDisplay = p1.minutes + ":" + p1.seconds;
	}
	
	
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