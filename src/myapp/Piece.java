package myapp;
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
