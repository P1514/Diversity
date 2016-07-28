package backend;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;

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
	public static final double pWviews = 0.3333;
	public static final double pWlikes = 0.3333;
	public static final double pWcomments = 0.3334;
	// Influence
	public static final double aWviews = 0.3333;
	public static final double aWlikes = 0.3333;
	public static final double aWcomments = 0.3334;

	// Local DB Specs
	public static final String url2 = "jdbc:mysql://localhost:3306/diversitydb?autoReconnect=true&useSSL=false";
	public static final String user2 = "diversity";
	public static final String pass2 = "diversity";
	
	// PSS File
	public static final String DATA_FOLDER = "data";
	public static final String FILENAME_PRODUCTS = "ListProducts.dat";

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
}
