package resources;

import resources.Settings;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Data {
	private HashMap<Integer, Author> authordb = new HashMap<Integer, Author>();
	private HashMap<Integer, Opinion> opiniondb = new HashMap<Integer, Opinion>();
	private int totalposts;
	private int totalviews;
	private int totalcomments;
	private int totallikes;

	Settings dbc = new Settings();
	Connection cn = null;

	public Data() {
		this.totalcomments = 0;
		this.totallikes = 0;
		this.totalposts = 0;
		this.totalviews = 0;
	}

	public void load() throws SQLException {

		try {
			cn = dbc.connect();
		} catch (ClassNotFoundException | SQLException e4) {
			e4.printStackTrace();
		}
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
			if (!(users.contains(user_id))) {
				users.add(user_id);
			}
			if (post_id == 0) {
				opiniondb.put(id, new Opinion(_post));
			} else {
				Opinion _opin = opiniondb.get(post_id);
				_opin.addcomment(_post);
				opiniondb.put(post_id, _opin);
			}
		}
		rs = null;
		String querycond = users.toString();
		querycond = querycond.replaceAll("\\[", "(").replaceAll("\\]", "\\)");
		query = ("Select * from " + dbc.usertn + " where " + dbc.user_id + " in " + querycond);
		System.out.println(query);
		stmt = cn.createStatement();
		rs = stmt.executeQuery(query);
		rs.next();// ALTERAR
		do {
			if (authordb.containsKey(rs.getInt(dbc.user_id))) {
			} else {
				authordb.put(rs.getInt(dbc.user_id), new Author(rs.getInt(dbc.user_id), rs.getString(dbc.uname),
						rs.getInt(dbc.uage), rs.getString(dbc.ugender), rs.getString(dbc.uloc)));
			}
		} while (rs.next());

		opiniondb.forEach((k, v) -> {
			Author temp_author = authordb.get(v.getID());
			temp_author.addComments(v.ncomments());
			temp_author.addLikes(v.nlikes());
			temp_author.addViews(v.nviews());
			authordb.put(temp_author.getID(), temp_author);
			totalcomments += v.ncomments();
			totallikes += v.nlikes();
			totalviews += v.nviews();
		});

		this.totalposts = opiniondb.size();
		authordb.forEach((k, v) -> {
			v.calcInfluence(totalcomments / ((double) opiniondb.size()), totallikes / ((double) opiniondb.size()),
					totalviews / ((double) opiniondb.size()));
		});
		opiniondb.forEach((k, v) -> {
			v.evalReach(totalcomments / ((double) opiniondb.size()), totallikes / ((double) opiniondb.size()),
					totalviews / ((double) opiniondb.size()));
			v.evalPolarity(authordb);
		});

		System.out.println("I AM COMPLETE BEHOLD\n\r");
		opiniondb.forEach((k, v) -> {
			System.out.println("AMEN " + v.getPolarity() + "/" + v.getReach());
		});
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
		return authordb.get(op.getID());
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
