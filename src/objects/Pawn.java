package objects;

import javafx.scene.image.ImageView;

public class Pawn extends Piece{

	public Pawn(String name, int col, int row, ImageView image, Colour colour) {
		super(name, col, row, image, colour);
		enPassant = false;
	}

	private boolean enPassant;

		

	public boolean getEnPassant() {
		return enPassant;
	}

	public void setEnPassant(boolean enPassant) {
		this.enPassant = enPassant;
	}
	
}
