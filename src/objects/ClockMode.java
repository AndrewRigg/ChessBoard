package objects;

public enum ClockMode{
	
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