package resources;

import resources.Settings;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Data {
	private HashMap<Integer, Author> authordb = new HashMap<Integer, Author>();
	private HashMap<Integer, Opinion> opiniondb = new HashMap<Integer, Opinion>();
	private double avgPost;
	private double avgLikes;
	private double avgViews;
	Settings dbc = new Settings();
	Connection cn = null;

	public Data() {
	}

	public void load() throws SQLException{

		ResultSet rs = null;
		String query = ("Select * from " + dbc.posttn);
		Statement stmt = cn.createStatement();
		rs = stmt.executeQuery(query);

		List<Integer> users = new ArrayList<Integer>();
		while (rs.next()) {

			int post_id = rs.getInt(dbc.rpost_id);
			int id = rs.getInt(dbc.post_id);
			int user_id = rs.getInt(dbc.puser_id);
			Date time = rs.getDate(dbc.pdate);
			int likes = rs.getInt(dbc.plikes);
			int views = rs.getInt(dbc.pviews);
			String message = rs.getString(dbc.pmessage);
			Post _post = new Post(id, user_id, time, likes, views, message);
			if(!(users.contains(user_id))){
				users.add(user_id);
			}
			if (post_id == 0) {
				opiniondb.put(id, new Opinion(_post));
			}else{
				Opinion _opin = opiniondb.get(post_id);
				_opin.addcomment(_post);
				opiniondb.put(post_id, _opin);
			}
		}
		rs = null;
		String querycond = users.toString();
		querycond = querycond.replaceAll("\\[", "(").replaceAll("\\]","\\)");
		query = ("Select * from " + dbc.usertn + "where " + dbc.user_id + "in " + querycond );
		stmt = cn.createStatement();
		rs = stmt.executeQuery(query);
		
		while(rs.next()){
			authordb.put(rs.getInt(dbc.user_id), new Author(rs.getInt(dbc.user_id), rs.getString(dbc.uname),
						 rs.getInt(dbc.uage), rs.getString(dbc.ugender), rs.getString(dbc.uloc)));
		}
		
		opiniondb.forEach((k,v) -> {
			v.evalReach(avgPost, avgLikes, avgViews);
		});
	
	} 
	
	
	public void getAvgs(){
	   opiniondb.forEach((k,v) -> {
		this.avgPost=+v.ncomments();
		this.avgLikes=+v.nlikes();
		this.avgViews=+v.nviews();
	   });
	   
	   this.avgPost=(this.avgPost/((double)opiniondb.size()));
	   this.avgLikes=(this.avgLikes/((double)opiniondb.size()));
	   this.avgViews=(this.avgViews/((double)opiniondb.size()));
	   
	}
	
}
