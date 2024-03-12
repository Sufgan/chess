package com.sufgan.chess.pieces;

import com.sufgan.chess.Field.Location;
import com.sufgan.chess.Main;
import com.sufgan.chess.Piece;

public class King extends Piece {

	public King(Location loc, Team team) {
		super(loc, team);
	}
	
	public King(Location loc) {
		super(loc);
	}

	@Override
	public boolean canMove(Location loc) {
		int difX = Math.abs(getX() - loc.getX());
		int difY = Math.abs(getY() - loc.getY());
		
		if (Main.field.lastMove(this) == -1 && // if don't moved
				difX == 2 && getY() == loc.getY()) { // and position is right, castling 
			Piece rook = null;
			if (loc.getX() == 2) rook = Main.field.getPiece(Main.field.new Location(0, loc.getY()));
			if (loc.getX() == 6) rook = Main.field.getPiece(Main.field.new Location(7, loc.getY()));
			if (!checkObstacles(loc) &&
					rook != null &&  
					rook.getClass() == Rook.class &&
					rook.team == team &&
					Main.field.lastMove(rook) == -1) {
				Main.field.setCastlingRook(rook);
				return true;
			}
		}
		
		if (difX > 1 || difY > 1) return false;
		return true;
	}
	
	@Override
	public String toString() {
		return team == Team.white? "K*" : "K.";
	}
}