package importDB;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import backend.Settings;

public class CleanDB {
	private HashMap<Integer, Author> authordb = new HashMap<Integer, Author>();
	private HashMap<Integer, Opinion> opiniondb = new HashMap<Integer, Opinion>();
	private HashMap<String, Integer> pss = new HashMap<String, Integer>();
	private int totalposts;
	private int totalviews;
	private int totalcomments;
	private int totallikes;

	Settings dbc = new Settings();
	Connection cndata = null;
	Connection cnlocal = null;

	public CleanDB() {
		this.totalcomments = 0;
		this.totallikes = 0;
		this.totalposts = 0;
		this.totalviews = 0;
	}

	public String clean() throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		try {
			pss = new PSS().importPSS();
		} catch (IOException e2) {
			System.out.println("FILE IO EXCEPTION" + e2.getMessage());
			obj.put("Op", "Error").toString();
			obj.put("Message", "Problem reading from Product File");
			result.put(obj);
			return result.toString();
		}

		pss.forEach((k, v) -> System.out.println("key: " + k + "value: " + v));

		String query;

		Statement stmt = null;

		// Clean local DB
		try {
			cnlocal = dbc.connlocal();
			query = "DELETE from posts";
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "delete from opinions";
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "delete from authors";
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "delete from influences";
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "UPDATE `diversitydb`.`general` SET `totalposts`='0', `totallikes`='0', `totalcomments`='0', `totalviews`='0', `lastupdated`='1970-01-01' WHERE `id`='1'";
			stmt = cnlocal.createStatement();
			stmt.execute(query);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			obj.put("Op", "Error");
			obj.put("Message", "Error (1): Missing Local Database");
			result.put(obj);
			return result.toString();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (cnlocal != null)
				try {
					cnlocal.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}


		obj.put("Op", "Error");
		obj.put("Message", "Cleaned Successfully");
		result.put(obj);
		return result.toString();
	}

	/**
	 * Returns an Iterator of all Opinions
	 * 
	 * @return Iterator<Opinion>
	 */
	public Iterator<Opinion> getOpinion() {
		return opiniondb.values().iterator();
	}

	/**
	 * Returns an Author Object based on id
	 * 
	 * @param id
	 *            User id to search for
	 * @return Object
	 */
	public Author getAuthor(int id) {
		return authordb.get(id);
	}

	/**
	 * Returns an Author Object based on Opinion
	 * 
	 * @param op
	 *            Opinion Object
	 * @return Object
	 */
	public Author getAuthor(Opinion op) {
		return authordb.get(op.getUID());
	}

	/**
	 * Returns an Author Object based on Post
	 * 
	 * @param comment
	 *            Post Object
	 * @return Object
	 */
	public Author getAuthor(Post comment) {
		return authordb.get(comment.getUID());
	}

	public int getTotalComments() {
		return this.totalcomments;
	}

	public int getTotalViews() {
		return this.totalviews;
	}

	public int getTotalLikes() {
		return this.totallikes;
	}

	public int getTotalPosts() {
		return this.totalposts;
	}

}
