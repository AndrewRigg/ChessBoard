package objects;

public class Player {

	int rank;
	String name;
	String notes;
	public PlayerType type;
	public boolean playerTurn;
	boolean isWhite;
	
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
	
	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
	}
}