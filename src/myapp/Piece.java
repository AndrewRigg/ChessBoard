package myapp;
import javafx.scene.image.ImageView;

public class Piece {

	String name = "";
	int col;
	int row;
	ImageView image;
	
	public Piece(String name, int col, int row, ImageView image) {
		this.name = name;
		this.col = col;
		this.row = row;
		this.image = image;
	}
	
	public Piece(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
