package myapp;

public class Player {

	int rank;
	String name;
	String notes;
	PlayerType type;
	
	public Player(String name, PlayerType type, int rank) {
		this.name = name;
		this.type = type;
		this.rank = rank;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public int getRank() {
		return rank;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public PlayerType getType() {
		return type;
	}
	
	public void setType(PlayerType type) {
		this.type = type;
	}
	
}


enum PlayerType {HUMAN, REMOTE, CPU, RECORDED};