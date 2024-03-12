package com.sufgan.chess.pieces;

import com.sufgan.chess.Field.Location;
import com.sufgan.chess.Main;
import com.sufgan.chess.Piece;

public class Pawn extends Piece {

	public Pawn(Location loc, Team team) {
		super(loc, team);
	}
	
	public Pawn(Location loc) {
		super(loc);
	}
	
	@Override
	public boolean canMove(Location loc) {
		int d = team == Team.white ? 1 : -1;
		int x = loc.getX();
		int y = loc.getY();
		
		if (checkObstacles(Main.field.new Location(x, y - d))) return false;			// way is clear
		
		switch (d * (getY() - y)) { 													// difference 'y' 
			case 1: 
				if (Math.abs(getX() - x) == 1) { 										// if 'x' switch
					Piece p2 = Main.field.getPiece(Main.field.new Location(x, y + d));
					if (isEnemy(Main.field.getPiece(loc))) return true;					// if eat
					else if (getY() == (team == Team.white ? 3 : 4) &&
							isEnemy(p2) &&
							p2.getClass().equals(Pawn.class) &&
							Main.field.wasMove(Main.field.new Location(p2.getX(), p2.getY() - d*2), p2.getLocation())) { // way exist
						Main.field.replacePiece(loc, p2, null);
						return true;
					}
				} else if (getX() - x == 0) return true;
				break;
			case 2: if (getX() - x == 0 && (getY() == 1 || getY() == 6)) return true;		  				// step from
		} 
		
		return false;
	}
	
	private boolean isEnemy(Piece piece) {
		return piece != null && piece.team != team;
	}
	
	@Override
	public String toString() {
		return team == Team.white? "p*" : "p."; // ♟ <- why so big???
	}
}