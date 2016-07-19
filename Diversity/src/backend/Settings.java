package backend;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.mysql.jdbc.Connection;

public class Settings {
	// To be replaced by properties file	
	public Date LastUpdated;
	// Data Origin DB Specs
	public String url = "jdbc:mysql://localhost:3306/sentimentposts?autoReconnect=true&useSSL=false";
	public String user = "root";
	public String pass = "root";

	// Post Table
	public String posttn = "post"; // Table Name
	public String rpost_id = "post_id"; // Reference Post Id
	public String post_id = "id"; // Actual Post Id
	public String puser_id = "user_id"; // Poster Id
	public String pdate = "timestamp"; // timestamp
	public String plikes = "likes"; // Number of Likes
	public String pviews = "views"; // Number of views
	public String pmessage = "message"; // Message

	// User Table
	public String usertn = "user"; // Table name
	public String user_id = "id"; // Id
	public String uname = "name"; // Name
	public String uage = "age"; // Age
	public String ugender = "gender"; // Gender
	public String uloc = "location"; // Location
	
	//Posts Table
	public String ptime="timestamp";

	// Computing Variables
	// Reach
	public double pWviews = 0.3333;
	public double pWlikes = 0.3333;
	public double pWcomments = 0.3334;
	// Influence
	public double aWviews = 0.3333;
	public double aWlikes = 0.3333;
	public double aWcomments = 0.3334;
	
	// Local DB Specs
	public String url2 = "jdbc:mysql://localhost:3306/diversitydb?autoReconnect=true&useSSL=false";
	public String user2 = "root";
	public String pass2 = "root";

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
