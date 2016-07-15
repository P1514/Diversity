package resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import org.json.*;

public class Backend /*extends Thread*/ {

	private static ServerSocket service;
	private static Socket conn;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private Connection cnlocal = null;

	public Backend(DataInputStream _dis, DataOutputStream _dos, Socket _conn) {
		dis = _dis;
		dos = _dos;
		conn = _conn;
		Settings dbc = new Settings();

		try {
			cnlocal = dbc.connlocal();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		BufferedReader inbuffer = new BufferedReader(new InputStreamReader(dis));
		String test = "";
		JSONArray result = new JSONArray();
		if (cnlocal == null) {
			return;
		}
		try {
			do {
				test = inbuffer.readLine();
				System.out.println(test);
			} while (inbuffer.ready());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		test = test.trim();
		// if (test.matches("sentimentbygender")) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("age_range", "0-30");
			obj.put("male_avg", sentimentbygender(0, 30, "MALE"));
			obj.put("female_avg", sentimentbygender(0, 30, "FEMALE"));
			result.put(obj);
			obj = new JSONObject();
			obj.put("age_range", "30-60");
			obj.put("male_avg", sentimentbygender(30, 60, "MALE"));
			obj.put("female_avg", sentimentbygender(30, 60, "FEMALE"));
			result.put(obj);
			obj = new JSONObject();
			obj.put("age_range", "60+");
			obj.put("male_avg", sentimentbygender(60, 90, "MALE"));
			obj.put("female_avg", sentimentbygender(60, 90, "FEMALE"));
			result.put(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
		System.out.print(result);
		OutputStreamWriter out;
		try {
			out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
			out.write(result.toString());
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			dis.close();
			dos.close();
		conn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private double sentimentbygender(int minage, int maxage, String gender) {
		double result = (double) 0;
		String insert = "Select polarity FROM posts WHERE author_id in (Select id from authors WHERE (AGE > ? AND AGE < ? AND GENDER=?))";
		PreparedStatement query1;
		ResultSet rs = null;
		Double auxcalc = (double) 0;
		int i;
		try {
			query1 = cnlocal.prepareStatement(insert);
			query1.setInt(1, minage);
			query1.setInt(2, maxage);
			query1.setString(3, gender);
			System.out.println(query1);
			rs = query1.executeQuery();

			for (i = 0; rs.next(); i++) {
				auxcalc += (double) rs.getInt("polarity");
			}
			result = auxcalc / (i == 0 ? 1 : i);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

}
