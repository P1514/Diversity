package resources;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class Settings {

	String url = "jdbc:mysql://localhost:3306/sentimentposts?autoReconnect=true&useSSL=false";
	String user = "root";
	String pass = "1234";
	
	// DB Specs
	
	//Post Table
	String posttn = "post"; // Table Name
	String rpost_id = "post_id"; // Reference Post Id
	String post_id = "id"; // Actual Post Id
	String puser_id = "user_id"; // Poster Id
	String pdate = "timestamp"; //  timestamp
	String plikes = "likes"; // Number of Likes
	String pviews = "views"; // Number of views
	String pmessage = "message"; // Message
	
	// User Table
	String usertn = "user"; // Table name
	String user_id= "id"; // Id
	String uname= "name"; // Name
	String uage = "age"; // Age
	String ugender = "gender"; // Gender
	String uloc = "location"; // Location
	
	
	
	
	public Connection connect() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		return (Connection) DriverManager.getConnection(url, user, pass);
	}
}
