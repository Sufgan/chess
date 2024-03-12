package com.sufgan.chess;

import java.util.LinkedList;

import com.sufgan.chess.Field.Location;

public abstract class Piece {
	public static LinkedList<Piece> pieces = new LinkedList<Piece>(); 
	protected Location loc;
	public Team team;
	
	public Piece(Location loc, Team team) {
		this.team = team;
		this.loc = loc;
		team.pieces.add(this);
		pieces.add(this);
	}
	
	public Piece(Location loc) {
		this(loc, loc.getY() < 4? Team.black : Team.white);
	}
	
	public abstract boolean canMove(Location loc);
	
	protected boolean checkObstacles(Location loc2) {
		loc2 = loc2.copy();
		while (true) {
			if (getY() != loc2.getY()) {
				loc2.setY(loc2.getY() + (loc2.getY() > getY() ? -1 : 1));
				if (getY() == loc2.getY()) break;
			} 
			if (getX() != loc2.getX()) {
				loc2.setX(loc2.getX() + (loc2.getX() > getX() ? -1 : 1));
				if (getX() == loc2.getX()) break;
			} 
			if (Main.field.getPiece(loc2) != null) return true;
		} 
		return false;
	}
	
	public void setLocation(Location loc) {
		this.loc = loc;
	}
	
	public int getX() {
		return loc.getX();
	}

	public int getY() {
		return loc.getY();
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public Piece remove() {
		pieces.remove(this);
		team.pieces.remove(this);
		return this;
	}

	public enum Team {
		white,
		black;
		
		public LinkedList<Piece> pieces = new LinkedList<>();
		
		private static final Team[] vals = values();
		
		public Team next() {
			return vals[(this.ordinal() + 1) % vals.length];
		}
	}	
}