package com.sufgan.chess.pieces;

import com.sufgan.chess.Field.Location;
import com.sufgan.chess.Piece;

public class Queen extends Piece {

	public Queen(Location loc, Team team) {
		super(loc, team);
		// TODO Auto-generated constructor stub
	}
	
	public Queen(Location loc) {
		super(loc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canMove(Location loc) {
		if (checkObstacles(loc)) return false;
		return !((getX() - loc.getX() != 0) && (getY() - loc.getY() != 0)) || 
				Math.abs(getX() - loc.getX()) == Math.abs(getY() - loc.getY());
	}
	
	@Override
	public String toString() {
		return team == Team.white? "Q*" : "Q.";
	}

}
