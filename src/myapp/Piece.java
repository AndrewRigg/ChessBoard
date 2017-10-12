package myapp;
import javafx.scene.image.ImageView;

public class Piece {

	String name = "";
	String type = "";
	int col;
	int row;
	ImageView image;
	
	public Piece(String name, String type, int col, int row, ImageView image) {
		this.name = name;
		this.type = type;
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
	
	//This might be needed if e.g. a pawn is promoted to a queen
	public void setName(String name){
		this.name = name;
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
