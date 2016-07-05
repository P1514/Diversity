package resources;

import resources.Settings;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Data {
	private HashMap<Integer, Author> authordb = new HashMap<Integer, Author>();
	private HashMap<Integer, Opinion> opiniondb = new HashMap<Integer, Opinion>();

	Settings dbc = new Settings();
	Connection cn = null;

	public Data() {
	}

	public void load() throws SQLException{

		ResultSet rs = null;
		String query = ("Select * from " + dbc.posttn);
		Statement stmt = cn.createStatement();
		rs = stmt.executeQuery(query);

		while (rs.next()) {

			int post_id = rs.getInt(dbc.rpost_id);
			int id = rs.getInt(dbc.post_id);
			int user_id = rs.getInt(dbc.puser_id);
			Date time = rs.getDate(dbc.pdate);
			int likes = rs.getInt(dbc.plikes);
			int views = rs.getInt(dbc.pviews);
			String message = rs.getString(dbc.pmessage);
			Post _post = new Post(id, user_id, time, likes, views, message);
			
			if (post_id == 0) {
				opiniondb.put(id, new Opinion(_post));
			}else{
				Opinion _opin = opiniondb.get(post_id);
				_opin.addcomment(_post);
				opiniondb.put(post_id, _opin);
			}
		}
		rs = null;
		query = ("Select * from " + dbc.usertn);
		stmt = cn.createStatement();
		rs = stmt.executeQuery(query);
		
		while(rs.next()){
			authordb.put(rs.getInt(dbc.user_id), new Author(rs.getInt(dbc.user_id), rs.getString(dbc.uname),
						 rs.getInt(dbc.uage), rs.getString(dbc.ugender), rs.getString(dbc.uloc)));
		}
	
	} 
	
}
