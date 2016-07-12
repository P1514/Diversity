package resources;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.json.JSONArray;

public class Server {

	private static ServerSocket service;
	private static Socket conn;
	private static DataInputStream dis;
	private static DataOutputStream dos;

	public static void main(String[] args) {
		// Server Initialization
		try {
			service = new ServerSocket(20000);
		} catch (IOException e) {
			System.out.println(e);
		}
		while (true) {
			try {
				conn = service.accept();
				dis = new DataInputStream(conn.getInputStream());
				dos = new DataOutputStream(conn.getOutputStream());
			} catch (IOException e) {
				System.out.println(e);
			}
			
			Backend resolve = new  Backend(dis, dos, conn);
			Thread execute = new Thread(resolve);
			execute.start();

			
		}
		/*
		try {
			Data test = new Data();
			test.load();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("TEST");*/

	}

}
