package backend;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.Connection;

public class Settings {
	// To be replaced by properties file
	// Data Origin DB Specs
	public static final String url = "jdbc:mysql://localhost:3306/sentimentposts?autoReconnect=true&useSSL=false";
	public static final String user = "diversity";
	public static final String pass = "diversity";

	// Post Table
	public static final String posttn = "post"; // Table Name
	public static final String rpost_id = "post_id"; // Reference Post Id
	public static final String post_id = "id"; // Actual Post Id
	public static final String puser_id = "user_id"; // Poster Id
	public static final String pdate = "timestamp"; // timestamp
	public static final String plikes = "likes"; // Number of Likes
	public static final String pviews = "views"; // Number of views
	public static final String pmessage = "message"; // Message

	// User Table
	public static final String usertn = "user"; // Table name
	public static final String user_id = "id"; // Id
	public static final String uname = "name"; // Name
	public static final String uage = "age"; // Age
	public static final String ugender = "gender"; // Gender
	public static final String uloc = "location"; // Location

	// Posts Table
	public static final String ptime = "timestamp";

	// Computing Variables
	// Reach
	public static final double pWviews = (double) 1 / 3;
	public static final double pWlikes = (double) 1 / 3;
	public static final double pWcomments = (double) 1 / 3;
	// Influence
	public static final double aWviews = (double) 1 / 3;
	public static final double aWlikes = (double) 1 / 3;
	public static final double aWcomments = (double) 1 / 3;

	// Local DB Specs
	public static final String url2 = "jdbc:mysql://localhost:3306/diversitydb?autoReconnect=true&useSSL=false";
	public static final String user2 = "diversity";
	public static final String pass2 = "diversity";

	// PSS File
	public static final String DATA_FOLDER = "data";
	public static final String FILENAME_PRODUCTS = "ListProducts.dat";

	// Graph Settings
	public static String ages = "0-30,,31-60,,61-90";
	public static String genders = "Female,,Male";
	public static String locations = "Asia,,Europe";

	public Connection conndata() throws ClassNotFoundException, SQLException {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2);
		Class.forName("com.mysql.jdbc.Driver");
		return (Connection) DriverManager.getConnection(url, user, pass);
	}

	public Connection connlocal() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		while (true) {
			try {
				return (Connection) DriverManager.getConnection(url2, user2, pass2);
			} catch (SQLException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	public JSONArray getConf() throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("Op", "Configs");
		result.put(obj);
		obj = new JSONObject();
		String[] values = ages.split(",,");
		obj.put("Size", values.length);
		obj.put("Param", "Age");
		result.put(obj);
		for (int i = 0; i < values.length; i++) {
			obj = new JSONObject();
			String[] range = values[i].split("-");
			obj.put("Min", range[0]);
			obj.put("Max", range[1]);
			result.put(obj);
		}
		obj = new JSONObject();
		String[] values2 = genders.split(",,");
		obj.put("Size", values2.length);
		obj.put("Param", "Gender");
		result.put(obj);
		for (int i = 0; i < values2.length; i++) {
			obj = new JSONObject();
			obj.put("Gender", values2[i]);
			result.put(obj);
		}
		obj = new JSONObject();
		String[] values3 = locations.split(",,");
		obj.put("Size", values3.length);
		obj.put("Param", "Location");
		result.put(obj);
		for (int i = 0; i < values3.length; i++) {
			obj = new JSONObject();
			obj.put("Location", values3[i]);
			result.put(obj);
		}
		System.out.print(result.toString());
		return result;

	}

	public JSONObject setConf(JSONObject msg) throws JSONException {
		String edited = new String();
		if (msg.has("Gender")) {
			Settings.genders = msg.getString("Gender");
			edited += "Gender ";
		}
		if (msg.has("Age")) {
			Settings.ages = msg.getString("Age");
			edited += "Age ";
		}
		if (msg.has("Location")) {
			Settings.locations = msg.getString("Location");
			edited += "Location ";
		}
		msg = new JSONObject();
		msg.put("Op", "Error");
		if (edited != "") {
			msg.put("Message", edited + "configuration updated");
		} else {
			msg.put("Message", "Nothing Changed");
		}
		return msg;
	}
}
