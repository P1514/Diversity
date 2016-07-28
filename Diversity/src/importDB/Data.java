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
	private HashMap<String,Integer> pss = new HashMap<String, Integer>();
	private int totalposts;
	private int totalviews;
	private int totalcomments;
	private int totallikes;

	Settings dbc = new Settings();
	Connection cndata = null;
	Connection cnlocal = null;

	public Data() {
		this.totalcomments = 0;
		this.totallikes = 0;
		this.totalposts = 0;
		this.totalviews = 0;
	}

	public String load() throws JSONException {
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
		
		pss.forEach((k,v) -> System.out.println("key: " + k + "value: " + v));

		String query;

		Statement stmt = null;
		ResultSet rs = null;

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
			query = "delete from general";
			stmt = cnlocal.createStatement();
			stmt.execute(query);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (cnlocal != null)
				try {
					cnlocal.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		// Load PSS

		// Load Posts
		try {
			cndata = dbc.conndata();
			query = ("Select * from " + Settings.posttn + " Where " + Settings.ptime + " < \'" + dbc.LastUpdated + "\' ORDER BY ID ASC");
			System.out.println(query);
			dbc.setLastUpdated();
			stmt = cndata.createStatement();
			rs = stmt.executeQuery(query);

			List<Integer> users = new ArrayList<Integer>();
			if(!rs.next()){
				obj.put("Op", "Error");
				obj.put("Message", "Error (2): Remote Database Error\r\n Please check if populated");
				result.put(obj);
				return result.toString();
			}

			do{

				int post_id = rs.getInt(Settings.rpost_id);
				int id = rs.getInt(Settings.post_id);
				int user_id = rs.getInt(Settings.puser_id);
				Date time = rs.getDate(Settings.pdate);
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
				} else {
					Opinion _opin = opiniondb.get(post_id);
					_opin.addcomment(_post);
					opiniondb.put(post_id, _opin);
				}
			}while (rs.next());
			rs.close();
			String querycond = users.toString();
			querycond = querycond.replaceAll("\\[", "(").replaceAll("\\]", "\\)");
			// Load users
			query = ("Select * from " + Settings.usertn + " where " + Settings.user_id + " in " + querycond);
			stmt = cndata.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (authordb.containsKey(rs.getInt(Settings.user_id))) {
				} else {
					authordb.put(rs.getInt(Settings.user_id), new Author(rs.getInt(Settings.user_id), rs.getString(Settings.uname),
							rs.getInt(Settings.uage), rs.getString(Settings.ugender), rs.getString(Settings.uloc)));
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (cndata != null)
				try {
					cndata.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
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
				Author temp_author = authordb.get(v3);
				temp_author.addComments(v.ncomments());
				temp_author.addLikes(v.nlikes());
				temp_author.addViews(v.nviews());
				temp_author.addPosts();
				authordb.put(temp_author.getID(), temp_author);
			});

			totalcomments += v.ncomments();
			totallikes += v.nlikes();
			totalviews += v.nviews();
		});

		this.totalposts = opiniondb.size();
		authordb.forEach((k, v) -> {
			v.calcInfluence((totalcomments / ((double) opiniondb.size())), totallikes / ((double) opiniondb.size()),
					totalviews / ((double) opiniondb.size()));
		});
		opiniondb.forEach((k, v) -> {
			v.evalReach(totalcomments / ((double) opiniondb.size()), totallikes / ((double) opiniondb.size()),
					totalviews / ((double) opiniondb.size()));
			v.evalPolarity(authordb);
		});

		try {
			cnlocal = dbc.connlocal();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		authordb.forEach((k, author) -> {
			String insert = "INSERT INTO authors " + "Values (?,?,?,?,?,?,?,?,?)";
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
				query1.executeUpdate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (query1 != null)
						query1.close();
				} catch (Exception e) {
				}
			}
		});

		opiniondb.forEach((k, opinion) -> {
			String update = "INSERT INTO opinions " + "Values (?,?,?,?,?,?,?,?)";

			PreparedStatement query1 = null;
			try {
				query1 = cnlocal.prepareStatement(update);
				query1.setInt(1, k);
				query1.setDouble(2, opinion.getReach());
				query1.setDouble(3, opinion.getPolarity());
				query1.setDouble(4, opinion.getTotalInf());
				query1.setInt(5, opinion.getUID());
				java.sql.Date sqlDate = new java.sql.Date(opinion.getTime().getTime());
				query1.setDate(6, sqlDate);
				query1.setInt(7, opinion.getTag());
				query1.setInt(8, opinion.ncomments());
				query1.executeUpdate();

				opinion.getPosts().forEach((post) -> {
					PreparedStatement query2 = null;
					try {
						String update1 = "INSERT INTO posts " + "Values (?,?,?,?,?)";
						query2 = cnlocal.prepareStatement(update1);
						query2.setInt(1, post.getID());
						query2.setDouble(2, post.getPolarity());
						query2.setString(3, post.getComment());
						query2.setInt(4, k);
						query2.setInt(5, post.getUID());

						query2.executeUpdate();
						if (query2 != null)
							query2.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
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
