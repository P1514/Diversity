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
	public static final String rurl = "jdbc:mysql://localhost:3306/sentimentposts?autoReconnect=true&useSSL=false";
	public static final String ruser = "diversity";
	public static final String rpass = "diversity";

	// Post Table
	public static final String rptable = "post"; // Table Name
	public static final String rptable_rpostid = "post_id"; // Reference Post Id
	public static final String rptable_postid = "id"; // Actual Post Id
	public static final String rptable_userid = "user_id"; // Poster Id
	public static final String rptable_date = "timestamp"; // timestamp
	public static final String rptable_likes = "likes"; // Number of Likes
	public static final String rptable_views = "views"; // Number of views
	public static final String rptable_message = "message"; // Message

	// User Table
	public static final String rutable = "user"; // Table name
	public static final String rutable_userid = "id"; // Id
	public static final String rutable_name = "name"; // Name
	public static final String rutable_age = "age"; // Age
	public static final String rutable_gender = "gender"; // Gender
	public static final String rutable_loc = "location"; // Location

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
	public static final Integer dbversion = 2;

	// Author Table
	public static final String latable = "authors";
	public static final String latable_name = "name";
	public static final String latable_age = "age";
	public static final String latable_gender = "gender";
	public static final String latable_location = "location";
	public static final String latable_posts = "posts";
	public static final String latable_comments = "comments";
	public static final String latable_likes = "likes";
	public static final String latable_views = "views";
	public static final String latable_influence = "influence";
	public static final String latable_id = "id";

	// Post Table
	public static final String lptable = "posts";
	public static final String lptable_id = "id";
	public static final String lptable_message= "message";
	public static final String lptable_authorid = "authors_id";
	public static final String lptable_likes = "likes";
	public static final String lptable_views = "views";
	public static final String lptable_comments = "comments";
	public static final String lptable_opinion = "opinions_id";
	public static final String lptable_polarity = "polarity";

	// Opinion table
	public static final String lotable= "opinions";
	public static final String lotable_id="id";
	public static final String lotable_reach="reach";
	public static final String lotable_polarity="polarity";
	public static final String lotable_influence="total_inf";
	public static final String lotable_author="authors_id";
	public static final String lotable_timestamp="timestamp";
	public static final String lotable_pss="tag_id";
	public static final String lotable_comments="comments";
	
	// Models table
	
	public static final String lmtable="models";
	public static final String lmtable_id="id";
	public static final String lmtable_name="name";
	public static final String lmtable_uri="uri";
	public static final String lmtable_pss="pss";
	public static final String lmtable_update="update_frequency";
	public static final String lmtable_archived="archived";
	public static final String lmtable_monitorfinal="monitor_final_products";
	public static final String lmtable_creator="created_by_user";
	public static final String lmtable_age="age_range";
	public static final String lmtable_gender="gender";
	
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
		return (Connection) DriverManager.getConnection(rurl, ruser, rpass);
	}

	public static Connection connlocal() throws ClassNotFoundException {
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
