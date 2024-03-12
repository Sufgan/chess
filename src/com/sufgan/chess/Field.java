package com.sufgan.chess;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedList;


import com.sufgan.chess.Piece.Team;
import com.sufgan.chess.pieces.*;

public class Field {
	private LinkedList<Location[]> history;
	private Piece field[][];
	private Piece castling;
	private Team team = Team.white;
	
	King whiteKing;
	King blackKing;
	
	public Field() {
		field = new Piece[8][8];
		history = new LinkedList<>();
		fillField();	
		
		try {
			System.out.print("Print you moves like: e2-e4\nGoog luck!");
			print();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void fillField() {
		for (int[] xy : new int[][] {{0,0}, {0,7}, {7,0}, {7,7}}) 
			createPiece(new Location(xy[0], xy[1]), Rook.class);
		for (int[] xy : new int[][] {{1,0}, {1,7}, {6,0}, {6,7}}) 
			createPiece(new Location(xy[0], xy[1]), Knight.class);
		for (int[] xy : new int[][] {{2,0}, {2,7}, {5,0}, {5,7}}) 
			createPiece(new Location(xy[0], xy[1]), Bishop.class);
		for (int[] xy : new int[][] {{3,0}, {3,7}}) 
			createPiece(new Location(xy[0], xy[1]), Queen.class);
		for (int y : new int[] {1, 6})
			for (int x = 0; x < 8; x++)
				createPiece(new Location(x, y), Pawn.class);
		whiteKing = (King) createPiece(new Location(4, 7), King.class);
		blackKing = (King) createPiece(new Location(4, 0), King.class);
	}
	
	public String move(Location loc1, Location loc2) throws InterruptedException, IOException {
		Piece movedPiece = getPiece(loc1);
		if (movedPiece == null || movedPiece.team != team) return "The figure was chosen incorrectly";
		if (!movedPiece.canMove(loc2) || (getPiece(loc2) != null && getPiece(loc2).team == team))
			return "The figure can't move like that";
		
		Piece replacedPiece = replacePiece(loc2, movedPiece, null);
		if (checkCheck() != null) { // check
			replacePiece(loc1, movedPiece, replacedPiece);
			return "You can't act like that because you're in check";
		} else if (replacedPiece != null) replacedPiece.remove();
		history.addFirst(new Location[] {loc1, loc2}); // move is successful
		
		if (movedPiece.getClass() == Pawn.class && (loc2.y == 7 || loc2.y == 0)) // pawn transform
			createPiece(loc2, Main.pawnTransform(), movedPiece.remove().team);
		
		if (castling != null) { // castling
			replacePiece(new Location((loc1.x + loc2.x) / 2, loc1.y), castling, null);
			castling = null;
		}
		
		team = team.next();	
		
		if (checkMate()) {
			team = team.next();
			return "mate";
		}
		if (checkStalemate()) return "stalemate";
		
		print();
		return null;
	}
	
	public Piece checkCheck() {
		King king = team == Team.white ? whiteKing : blackKing;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) { // mb in range team.pieces
				Piece piece = getPiece(new Location(i, j)); 
				if (piece != null && 								
						piece.team != team && 						
						piece.canMove(king.getLocation())) {		
					return piece;
				}
			}
		}
		return null;
	}
	
	private boolean checkMate() { // very primitive 
		Piece attackPiece = checkCheck(); 
		if (attackPiece == null) return false;
		
		Location loc1, loc2;
		
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				for (Piece piece : team.pieces) {
					loc2 = new Location(x, y);
					loc1 = piece.getLocation().copy();
					
					if (!piece.canMove(loc2)) continue;
					if (getPiece(loc2) != null && getPiece(loc2).team == team) continue;
					Piece replacedPiece = replacePiece(loc2, piece, null);
						
					if (checkCheck() == null) { 
						replacePiece(loc1, piece, null);
						movePiece(loc2, replacedPiece);
						return false;						
					}
					replacePiece(loc1, piece, null);
					movePiece(loc2, replacedPiece);
				}
			}
		}
		return true;
	}
	
	private boolean checkStalemate() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				for (Piece piece : team.pieces) {
					Location loc = new Location(x, y);
					if (piece.canMove(loc) && 
							(getPiece(loc) == null || getPiece(loc).team == team)) 
						return false;
				}
			}
		}
		return true;
	}
	
	public Piece createPiece(Location loc, Class<? extends Piece> cls, Team team) {
		Piece piece = createPiece(loc, cls);
		piece.team = team;
		return piece;
	}
	
	public Piece createPiece(Location loc, Class<? extends Piece> cls) {
		Piece piece = null;
		try {
			piece = (Piece) cls
					.getConstructor(new Class<?>[] {Location.class})
					.newInstance(new Object[] {loc});
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return createPiece(loc, piece);
	}
	
	public Piece createPiece(Location loc, Piece piece) {
		return field[loc.y][loc.x] = piece;
	}
	
	public Piece replacePiece(Location loc, Piece piece, Piece replace) {
		movePiece(piece.getLocation(), replace);
		return movePiece(loc, piece);
	}
	
	public Piece movePiece(Location loc, Piece piece) {
		Piece replaced = getPiece(loc);
		createPiece(loc, piece);
		if (piece != null) piece.setLocation(loc);
		return replaced;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public Piece getPiece(Location loc) {
		return field[loc.y][loc.x];
	}
	
	public int lastMove(Piece p) {
		int i = 0;
		for (Location[] loc : history) {
			i++;
			if (loc[1].equals(p.getLocation())) 
				return i;
		}
		return -1;
	}
	
	public boolean wasMove(Location from, Location to) {
		for (Location[] loc : history)
			if (loc[0].equals(from) && loc[1].equals(to)) 
				return true;
		return false;	
	}
	
	public void setCastlingRook(Piece rook) {
		castling = rook;
	}
	
	public void print() throws InterruptedException, IOException {
		new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		System.out.println(this);
	}
	
	public void printTeam() {
		System.out.print(team + ": ");
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		
		switch (Main.team == null ? team : Main.team) {
		case white: 
			for (int i = 0; i < 8; i++) 
				printLine(out, i);
			break;
		case black:
			for (int i = 7; i >= 0; i--) 
				printLine(out, i);
			break;
		}
				
		for (int i = 0; i < 8; i++) out.append(" | " + (char)(i + 'A'));
		return out.toString();
	}
	
	private void printLine(StringBuilder out, int i) {
		out.append(String.format("%d", 8-i));
		for (Piece piece : field[i]) 
			out.append(String.format("| %2s", piece == null ? "" : piece));
		out.append("\n");
		for (int j = 0; j < 8; j++) out.append("----");
		out.append("-\n");
	}
	
	void clear(Team team) {
		for (Piece[] line : field) 
			for (int i = 0; i < 8; i++) 
				if (line[i] != null && line[i].team != team) 
					line[i] = null;
		
	}
	
	public class Location {
		private int x, y;
		
		public Location(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(Object loc) {
			return x == ((Location)loc).x && y == ((Location)loc).y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public void setX(int x) {
			this.x = x;
		}
		
		public void setY(int y) {
			this.y = y;
		}
		
		public Location copy() {
			return new Location(x, y);
		}
		
		@Override
		public String toString() {
			return String.format("[x:%d,y:%d]", x, y);
		}
	}
	
	public void save() {
		try {
			BufferedWriter bf = new BufferedWriter(new FileWriter("./save_" + System.currentTimeMillis() + ".txt"));
			for (int i = history.size(); i >= 0; i++) {
				Location[] l = history.get(i);
				bf.append(String.format("%s%d-%s%d\n", (char)(l[0].getX()+'a'), (8-l[0].getY()), (char)(l[1].getX()+'a'), (8-l[1].getY())));
			}
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}