package com.sufgan.chess;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.sufgan.chess.Piece.Team;
import com.sufgan.chess.pieces.*;

public class Main {
	public static Team team = Team.white;
	private static Scanner scanner;
	public static Field field;
	static boolean running;
	private static TwoUserInetrface server;
	private static char lastPawnTransform = 0;
	
	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		run(requestGM());
	}
	
	private static void run(Gamemode mode) {
		if (mode == Gamemode.remote)
			server = new TwoUserInetrface(requestServerMode());
		
		running = true;
		field = new Field();
		String s = "";
		scanner.nextLine(); // в душе не ебу почему так
		
		while (running) {			
			field.printTeam();
			
			if (mode == Gamemode.remote && team != field.getTeam()) {
				printWaiting();
				checkResponse(move(server.get()), "");
			} else {
				if (isCommand(s = scanner.nextLine())) continue;
				checkResponse(move(s), s);		
			}
		}
		if (server != null) {
			server.sendMove(s);
			if (lastPawnTransform != 0) {
				server.sendMove(lastPawnTransform + "");
				lastPawnTransform = 0;
			}
		}
		
		scanner.next(); // hold window
	}
	
	static String move(String move) {
		try {
			char[] moveChars = move.toCharArray();
			int x1 = toLine(moveChars[0]);
			int y1 = toLine(moveChars[1]);
			int x2 = toLine(moveChars[3]);
			int y2 = toLine(moveChars[4]);
			return field.move(Main.field.new Location(x1, y1), Main.field.new Location(x2, y2));
		} catch (Exception e) {
			return "Input error, please try again";
		}
	}
	
	static int toLine(char a) throws Exception {
		if ('a' <= a && a <= 'h') return a - 'a';
		if ('1' <= a && a <= '8') return 7 - (a - '1');
		throw new Exception();
	}
	
	public static String requestIP() {
		clear();
		
		System.out.print("Enter target local network IPn\nIP: ");
		String s = scanner.nextLine();
		if (Pattern.compile("((\\d+\\.){3}\\d)|(.+::(.+:){3}.+)").matcher(s).find())
			return s;
		
		System.out.println("Input error, please try again");
		return requestIP();
	}
	
	// make request all data
	
	public static int requestPort() { 
		clear();
		System.out.println("Enter code from another device");
		return scanner.nextInt();
	}
	
	public static Gamemode requestGM() {
		clear();
		System.out.print("Select game mode:\n1 - local\n2 - remote\nmode: ");
		try {
			switch (scanner.nextInt()) {
			case 1: return Gamemode.local;
			case 2: return Gamemode.remote;
			}
		} catch (Exception e) {}
		System.out.println("Input error, please try again");
		return requestGM();
	}
	
	public static int requestServerMode() {
		clear();
		System.out.print("Choise mode:\n1 - create game\n2 - join game\nmode: ");
		try {
			switch (scanner.nextInt()) {
			case 1: return TwoUserInetrface.MODE_CREATE;
			case 2: return TwoUserInetrface.MODE_JOIN;
			}
		} catch (Exception e) {}
		System.out.println("Input error, please try again");
		return requestServerMode();
	}
	
	private static boolean isCommand(String command) {
		if (command.equals("exit")) running = false;
		else if (command.toLowerCase().equals("save")) field.save();
		// many commands like 'if else ...'
		else return false;
		return true;
	}
	
	private static void checkResponse(String response, String mes) {
		if (response == null) { // send to user2 if move successful
			if (server != null && !mes.equals("")) server.sendMove(mes);
			return; 
		}
		
		if (response.equals("stalemate") || response.equals("mate")) 
			running = false;
		
		if (response.equals("mate")) 
			response = String.format("\nmate\n%s won", field.getTeam());
		
		System.out.printf("%s\n", response); 
	}
	
	public static Class<? extends Piece> pawnTransform() {
		String s1 = "Choise who the pawn will be", s2 = "Input error, please try again";
		System.out.print("\n" + s1);
		
		if (server != null && team != field.getTeam()) lastPawnTransform = server.get().charAt(0);
		else lastPawnTransform = scanner.nextLine().charAt(0);
		
		switch (lastPawnTransform) {
		case 'R': return Rook.class;
		case 'Q': return Queen.class;
		case 'k': return Knight.class;
		case 'B': return Bishop.class;
		default:
			clearLine(s1.length());
			System.out.print(s2);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			clearLine(s2.length());
			return pawnTransform();
		}
	}
	
	static void clear() {
		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void printWaiting() {
		int n = 1;
		do {
			try {
				System.out.print("\r");
				for (int i = 0; i < 30; i++) System.out.print(" ");
				System.out.print("\rWaiting for opponent");
				for (int i = 0; i < n%4; i++) System.out.print(".");
				n++;
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		} while (!server.hasNext());
	}
	
	private static void clearLine(int n) {
		System.out.print('\r');
		for (int i = 0; i < n; i++) System.out.print(" ");
		System.out.print('\r');
	}
	
	enum Gamemode {
		local,
		remote;
	}
}