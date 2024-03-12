package com.sufgan.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.sufgan.chess.Piece.Team;

public class TwoUserInetrface {
	public static final int MODE_CREATE = 1;
	public static final int MODE_JOIN = 2;
	private String ip = "127.0.0.1"; 
	Socket user2;
	
	BufferedReader in;
	PrintWriter out;

	public TwoUserInetrface(int mode) {
		if (mode == MODE_JOIN) ip = Main.requestIP();
		create(mode);
	}
	
	private void create(int mode) {
		switch (mode) {
		case MODE_CREATE: createServer(); break;
		case MODE_JOIN: connectToServer();
		}
		try {
			in = new BufferedReader(new InputStreamReader(user2.getInputStream()));
			out = new PrintWriter(user2.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createServer() {
		Main.team = Team.white;
		ServerSocket server = takeOpenPort();
		System.out.printf("Enter %d on another device\n", server.getLocalPort());
		try {
			user2 = server.accept();
		} catch (IOException e) {}
	}
	
	private void connectToServer() {
		Main.team = Team.black;
		try {
			user2 = new Socket(ip, Main.requestPort()); // can be error
		} catch (IOException e) {}
	}
	
	public void sendMove(String move) {
		out.println(move);
	}
	
	public String get() {
		try {
			return in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public boolean hasNext() {
		try {
			return in.ready();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private ServerSocket takeOpenPort() {
		for (int port = 3000; port < 10000; port++) {
			try {
				return new ServerSocket(port);
			} catch (IOException e) {}
		}
		return null;
	}
}