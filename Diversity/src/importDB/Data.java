package importDB;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import backend.Settings;

public class Data {
	private HashMap<Integer, Author> authordb = new HashMap<Integer, Author>();
	private HashMap<Integer, Opinion> opiniondb = new HashMap<Integer, Opinion>();
	private HashMap<String, Integer> pss = new HashMap<String, Integer>();
	private int totalposts;
	private int totalviews;
	private int totalcomments;
	private int totallikes;
	private java.sql.Date LastUpdated = null;
	private java.sql.Date LastUpdated2 = null;
	private Calendar cal = Calendar.getInstance();

	Settings dbc = new Settings();
	Connection cndata = null;
	Connection cnlocal = null;

	public Data() {
	}

	public String load() throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		// DONE FAZER LOAD DAS VARIAVEIS NO GENERAL
		String select = "Select * from general WHERE id=1";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			cnlocal = dbc.connlocal();

			stmt = cnlocal.createStatement();
			rs = stmt.executeQuery(select);
			rs.next();
			totalviews = rs.getInt("totalviews");
			totalcomments = rs.getInt("totalcomments");
			totallikes = rs.getInt("totallikes");
			totalposts = rs.getInt("totalposts");
			LastUpdated = rs.getDate("lastupdated");
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

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

		String query, insert2;

		stmt = null;
		rs = null;

		// Load PSS

		// Load Posts
		try {
			List<Integer> users = new ArrayList<Integer>();
			cndata = dbc.conndata();
			do {
				cal.setTime(LastUpdated);
				cal.add(Calendar.MONTH, 1);
				LastUpdated2 = new java.sql.Date(cal.getTimeInMillis());
				query = ("Select * from " + Settings.posttn + " Where " + Settings.ptime + " > \'" + LastUpdated
						+ "\' && " + Settings.ptime + " <= \'" + LastUpdated2 + "\' ORDER BY ID ASC");
				System.out.println(query);

				stmt = cndata.createStatement();
				rs = stmt.executeQuery(query);

				if (!rs.next()) {
					LastUpdated = LastUpdated2;
					if (LastUpdated.after(new java.sql.Date(Calendar.getInstance().getTimeInMillis()))) {
						obj.put("Op", "Error");
						obj.put("Message", "Error (2): Remote Database Error\r\n Please check if populated");
						result.put(obj);
						if (rs != null)
							rs.close();
						if (stmt != null)
							stmt.close();
						if (cndata != null)
							cndata.close();
						return result.toString();
					}
					continue;
				}
				break;
			} while (true);

			do {

				int post_id = rs.getInt(Settings.rpost_id);
				System.out.println(post_id+ " BLA");
				int id = rs.getInt(Settings.post_id);
				int user_id = rs.getInt(Settings.puser_id);
				java.sql.Date time = rs.getDate(Settings.pdate);
				int likes = rs.getInt(Settings.plikes);
				int views = rs.getInt(Settings.pviews);
				String message = rs.getString(Settings.pmessage);
				Post _post = new Post(id, user_id, time, likes, views, message);
				if (!(users.contains(user_id))) {
					users.add(user_id);
				}
				if (post_id == 0) {
					PSS pss = new PSS();
					int tag = pss.getTag(message);
					opiniondb.put(id, new Opinion(_post, tag));
				} else {// Checks if opinion is new if not loads it from the
						// local db

					if (post_id != 0 && opiniondb.containsKey(post_id)) {
						Opinion _opin = opiniondb.get(post_id);
						_opin.addcomment(_post);
						opiniondb.put(post_id, _opin);
					} else {

						cnlocal = dbc.connlocal();
						// DONE finish this
						insert2 = "Select * from posts where opinions_id = " + post_id + " ORDER BY ID ASC";
						System.out.println(insert2);
						Statement stmt2 = cnlocal.createStatement();
						ResultSet rs2 = stmt2.executeQuery(insert2);
						if (!rs2.next()) {
							obj.put("Op", "Error");
							obj.put("Message", "Error (2): Remote Database Error\r\n Please check if populated");
							result.put(obj);
							return result.toString();
						}
						do {
							id = rs2.getInt("id");
							user_id = rs2.getInt("authors_id");
							likes = rs2.getInt("likes");
							views = rs2.getInt("views");
							message = rs2.getString("message");
							Post _post2 = new Post(id, user_id, null, likes, views, message);
							if (!(users.contains(user_id))) {
								users.add(user_id);
							}
							if (id == post_id) {
								totalposts--;
								PSS pss = new PSS();
								int tag = pss.getTag(message);
								opiniondb.put(id, new Opinion(_post2, tag));
							} else {
								Opinion _opin = opiniondb.get(post_id);
								_opin.addcomment(_post2);
								opiniondb.put(post_id, _opin);
							}

						} while (rs2.next());
						Opinion _opin = opiniondb.get(post_id);
						_opin.addcomment(_post);
						opiniondb.put(post_id, _opin);
						if (rs2 != null)
							rs2.close();
						if (stmt2 != null)
							stmt2.close();
						if (cnlocal != null)
							cnlocal.close();

					}

				}
			} while (rs.next());
			rs.close();

			// TODO ir buscar primeiro à DB LOCAL os users
			cnlocal = dbc.connlocal();

			String querycond = users.toString();
			querycond = querycond.replaceAll("\\[", "(").replaceAll("\\]", "\\)");
			// Load users from local DB
			query = ("Select * from authors where id in " + querycond);
			System.out.println(query);
			stmt = cnlocal.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (authordb.containsKey(rs.getInt("id"))) {
				} else {
					Author auth = new Author(rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
							rs.getString("gender"), rs.getString("location"));
					auth.setComments(rs.getInt("comments"));
					auth.setLikes(rs.getInt("likes"));
					auth.setPosts(rs.getInt("posts") - 1);
					auth.setViews(rs.getInt("views"));
					authordb.put(rs.getInt("id"), auth);
				}
			}
			rs.close();
			stmt.close();
			cnlocal.close();

			// Load users from foreign DB
			query = ("Select * from " + Settings.usertn + " where " + Settings.user_id + " in " + querycond);
			stmt = cndata.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (authordb.containsKey(rs.getInt(Settings.user_id))) {
				} else {
					authordb.put(rs.getInt(Settings.user_id),
							new Author(rs.getInt(Settings.user_id), rs.getString(Settings.uname),
									rs.getInt(Settings.uage), rs.getString(Settings.ugender),
									rs.getString(Settings.uloc)));
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.print(e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			obj.put("Op", "Error");
			obj.put("Message", "Error (2): Remote Database Error\r\n Please check if populated");
			result.put(obj);
			return result.toString();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (cndata != null)
				try {
					cndata.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		opiniondb.forEach((k, v) -> {
			ArrayList<Integer> uniqueauthors = new ArrayList<Integer>();
			ArrayList<Post> temp_post = v.getPosts();
			temp_post.forEach((v2) -> {
				if (!uniqueauthors.contains(v2.getUID()))
					uniqueauthors.add(v2.getUID());
			});
			uniqueauthors.forEach((v3) -> {
				System.out.println(authordb.containsKey(v3) + " " + v3);
				Author temp_author = authordb.get(v3);
				temp_author.addComments(v.newcomments());
				temp_author.addLikes(v.newlikes());
				temp_author.addViews(v.newviews());
				temp_author.addPosts();
				authordb.put(temp_author.getID(), temp_author);
			});

			totalcomments += v.newcomments();
			totallikes += v.newlikes();
			totalviews += v.newviews();
		});

		this.totalposts += opiniondb.size();// Modificado
		authordb.forEach((k, v) -> {
			v.calcInfluence((totalcomments / ((double) totalposts)), totallikes / ((double) totalposts),
					totalviews / ((double) totalposts));
		});
		opiniondb.forEach((k, v) -> {
			v.evalReach(totalcomments / ((double) totalposts), totallikes / ((double) totalposts),
					totalviews / ((double) totalposts));
			v.evalPolarity(authordb);
		});

		try {
			cnlocal = dbc.connlocal();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		authordb.forEach((k, author) -> {
			String insert = "INSERT INTO authors "
					+ "Values (?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE influence=?,comments=?,likes=?,views=?,posts=?";
			PreparedStatement query1 = null;
			try {
				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, author.getID());
				query1.setInt(2, author.getAge());
				query1.setString(3, author.getName());
				query1.setString(4, author.getGender());
				query1.setString(5, author.getLocation());
				query1.setDouble(6, author.getInfluence());
				query1.setInt(7, author.getComments());
				query1.setInt(8, author.getLikes());
				query1.setInt(9, author.getViews());
				query1.setInt(10, author.getPosts());
				query1.setDouble(11, author.getInfluence());
				query1.setInt(12, author.getComments());
				query1.setInt(13, author.getLikes());
				query1.setInt(14, author.getViews());
				query1.setInt(15, author.getPosts());
				System.out.println(query1);
				query1.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			} finally {
				try {
					if (query1 != null)
						query1.close();
				} catch (Exception e) {
				}
			}
		});

		opiniondb.forEach((k, opinion) -> {
			String update = "INSERT INTO opinions "
					+ "Values (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE reach=?,polarity=?,total_inf=?,comments=?";

			PreparedStatement query1 = null;
			try {
				query1 = cnlocal.prepareStatement(update);
				query1.setInt(1, k);
				query1.setDouble(2, opinion.getReach());
				query1.setDouble(3, opinion.getPolarity());
				query1.setDouble(4, opinion.getTotalInf());
				query1.setInt(5, opinion.getUID());
				query1.setDate(6, opinion.getTime());
				query1.setInt(7, opinion.getTag());
				query1.setInt(8, opinion.ncomments());
				query1.setDouble(9, opinion.getReach());
				query1.setDouble(10, opinion.getPolarity());
				query1.setDouble(11, opinion.getTotalInf());
				query1.setInt(12, opinion.ncomments());
				query1.executeUpdate();

				opinion.getPosts().forEach((post) -> {
					PreparedStatement query2 = null;
					try {
						String update1 = "REPLACE INTO posts " + "Values (?,?,?,?,?,?,?)";
						query2 = cnlocal.prepareStatement(update1);
						query2.setInt(1, post.getID());
						query2.setDouble(2, post.getPolarity());
						query2.setString(3, post.getComment());
						query2.setInt(4, post.getLikes());
						query2.setInt(5, post.getViews());
						query2.setInt(6, k);
						query2.setInt(7, post.getUID());

						query2.executeUpdate();
						if (query2 != null)
							query2.close();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if (query2 != null)
								query2.close();
						} catch (Exception e) {
						}
					}
				});
			} catch (Exception e) {
			} finally {
				try {
					if (query1 != null)
						query1.close();
				} catch (Exception e) {
				}
			}
		});

		String update = "UPDATE general SET totalposts=?,totallikes=?,totalcomments=?,totalviews=?,lastupdated=? WHERE id=1";

		PreparedStatement query1 = null;
		try {
			query1 = cnlocal.prepareStatement(update);
			query1.setInt(1, totalposts);
			query1.setInt(2, totallikes);
			query1.setInt(3, totalcomments);
			query1.setInt(4, totalviews);
			query1.setDate(5, (Date) LastUpdated2);
			query1.executeUpdate();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			if (cnlocal != null)
				cnlocal.close();
		} catch (Exception e) {
		}
		;

		obj.put("Op", "Error");
		obj.put("Message", "Loaded Successfully");
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
