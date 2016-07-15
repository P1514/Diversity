package resources;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mysql.jdbc.Connection;

public class Settings {
	// To be replaced by properties file	
	Date LastUpdated;
	// Data Origin DB Specs
	String url = "jdbc:mysql://localhost:3306/sentimentposts?autoReconnect=true&useSSL=false";
	String user = "root";
	String pass = "1234";

	// Post Table
	String posttn = "post"; // Table Name
	String rpost_id = "post_id"; // Reference Post Id
	String post_id = "id"; // Actual Post Id
	String puser_id = "user_id"; // Poster Id
	String pdate = "timestamp"; // timestamp
	String plikes = "likes"; // Number of Likes
	String pviews = "views"; // Number of views
	String pmessage = "message"; // Message

	// User Table
	String usertn = "user"; // Table name
	String user_id = "id"; // Id
	String uname = "name"; // Name
	String uage = "age"; // Age
	String ugender = "gender"; // Gender
	String uloc = "location"; // Location
	
	//Posts Table
	String ptime="timestamp";

	// Computing Variables
	// Reach
	double pWviews = 0.3333;
	double pWlikes = 0.3333;
	double pWcomments = 0.3334;
	// Influence
	double aWviews = 0.3333;
	double aWlikes = 0.3333;
	double aWcomments = 0.3334;
	
	// Local DB Specs
	String url2 = "jdbc:mysql://localhost:3306/diversitydb?autoReconnect=true&useSSL=false";
	String user2 = "root";
	String pass2 = "1234";

	public Connection conndata() throws ClassNotFoundException, SQLException {
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2);
		//DateFormat newDate = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		this.LastUpdated=cal.getTime();
		Class.forName("com.mysql.jdbc.Driver");
		return (Connection) DriverManager.getConnection(url, user, pass);
	}
	
	public Connection connlocal() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		return (Connection) DriverManager.getConnection(url2, user2, pass2);
	}
	
	public void setLastUpdated(){
		this.LastUpdated = Calendar.getInstance().getTime();		
	}
}
