package com.sufgan.chess.pieces;

import com.sufgan.chess.Field.Location;
import com.sufgan.chess.Piece;

public class Knight extends Piece {

	public Knight(Location loc, Team team) {
		super(loc, team);
	}
	
	public Knight(Location loc) {
		super(loc);
	}

	@Override
	public boolean canMove(Location loc) {
		int difX = Math.abs(getX() - loc.getX());
		int difY = Math.abs(getY() - loc.getY());
		return (difX == 2 && difY == 1) || 
				(difX == 1 && difY == 2);
	}

	@Override
	public String toString() {
		return team == Team.white? "k*" : "k.";
	}
}
