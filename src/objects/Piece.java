package objects;
import javafx.scene.image.ImageView;

public class Piece {

	ImageView image;
	String name = "";
	String type = "";
	int col;
	int row;
	boolean taken, isWhite, unmoved;

	String notation = "";
	
	public Piece(String name, String type, int col, int row, ImageView image, boolean isWhite) {
		this.image = image;
		this.name = name;
		this.type = type;
		this.col = col;
		this.row = row;
		this.isWhite = isWhite;
		if(type.equals("Knight")){
			notation = "N";
		}else if (!type.equals("Pawn")){
			notation = type.substring(0,1);
		}
		unmoved = true;
	}
	
	public String getNotation() {
		return notation;
	}

	public void setNotation(String notation) {
		this.notation = notation;
	}

	public boolean isUnmoved() {
		return unmoved;
	}

	public void setUnmoved(boolean unmoved) {
		this.unmoved = unmoved;
	}
	
	public boolean isTaken() {
		return taken;
	}

	public Piece(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setTaken(boolean taken){
		this.taken = taken;
	}
	
	//This might be needed if e.g. a pawn is promoted to a queen
	public void setName(String name){
		this.name = name;
	}
	
	public String getType(){
		return type;
	}
	
	public int getCol() {
		return col;
	}
	
	public void setCol(int col){
		this.col = col;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
	public boolean isWhite() {
		return isWhite;
	}

	public void setWhite(boolean isWhite) {
		this.isWhite = isWhite;
	}

	//This might be needed if e.g. a pawn is promoted to a queen
	public void setType(String type){
		this.type = type;
	}
	
	//This might be needed if e.g. a pawn is promoted to a queen
	public void setImage(ImageView image){
		this.image = image;
	}
	
	public ImageView getImage(){
		return image;
	}
	
	
}
