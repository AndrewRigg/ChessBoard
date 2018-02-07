package objects;
import javafx.scene.image.ImageView;

public class Piece {

	ImageView image;
	String name;
	char notation;
	int col;
	int row;
	Colour colour;
	boolean taken;
	boolean moved;
	
	public Piece(String name, int col, int row, ImageView image, Colour colour) {
		this.image = image;
		this.name = name;
		this.col = col;
		this.row = row;
		this.colour = colour;
		moved = true;
	}

	public boolean getMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}
	
	public boolean getTaken() {
		return taken;
	}
	
	public void setTaken(boolean taken){
		this.taken = taken;
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
}
