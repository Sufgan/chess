package com.sufgan.chess.pieces;

import com.sufgan.chess.Field.Location;
import com.sufgan.chess.Piece;

public class Bishop extends Piece{

	public Bishop(Location loc, Team team) {
		super(loc, team);
	}
	
	public Bishop(Location loc) {
		super(loc);
	}
	
	@Override
	public boolean canMove(Location loc) {
		if (checkObstacles(loc)) return false;
		return Math.abs(getX() - loc.getX()) == Math.abs(getY() - loc.getY());
	}

	@Override
	public String toString() {
		return team == Team.white? "B*" : "B.";
	}
}
