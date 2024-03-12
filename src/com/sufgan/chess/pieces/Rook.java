package com.sufgan.chess.pieces;

import com.sufgan.chess.Field.Location;
import com.sufgan.chess.Piece;

public class Rook extends Piece {

	public Rook(Location loc, Team team) {
		super(loc, team);
	}
	
	public Rook(Location loc) {
		super(loc);
	}

	@Override
	public boolean canMove(Location loc) {
		if (checkObstacles(loc)) return false;
		return !((getX() - loc.getX() != 0) && (getY() - loc.getY() != 0));
	}

	@Override
	public String toString() {
		return team == Team.white? "R*" : "R.";
	}
}