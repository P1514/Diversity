package importDB;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import backend.Settings;

public class Data {
	private ConcurrentHashMap<Integer, Author> authordb = new ConcurrentHashMap<Integer, Author>();
	private ConcurrentHashMap<Integer, Opinion> opiniondb = new ConcurrentHashMap<Integer, Opinion>();
	private int totalposts;
	private int totalviews;
	private int totalcomments;
	private int totallikes;
	private java.sql.Date LastUpdated = null;
	private java.sql.Date LastUpdated2 = null;
	private Calendar cal = Calendar.getInstance();
	private List<Integer> users = new ArrayList<Integer>();

	Settings dbc = new Settings();
	Connection cndata = null;
	Connection cnlocal = null;

	public Data() {
	}

	public String load() throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		long stime = System.nanoTime();
		System.out.println(" Beginning " + stime);

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
			System.out.println("ERROR ACCESSING LOCAL DB");
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

		String query;

		stmt = null;
		rs = null;

		// Load PSS

		System.out.println(" Variable Init " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		// Load Data
		try {
			cndata = dbc.conndata();
			do {
				// Load Opinions id first
				cal.setTime(LastUpdated);
				cal.add(Calendar.MONTH, 1);
				LastUpdated2 = new java.sql.Date(cal.getTimeInMillis());
				query = ("Select distinct case \r\n when " + Settings.rpost_id + " is null then " + Settings.post_id
						+ "\r\n when " + Settings.rpost_id + " is not null then " + Settings.rpost_id + " end from "
						+ Settings.posttn + " Where " + Settings.ptime + " > \'" + LastUpdated + "\' && "
						+ Settings.ptime + " <= \'" + LastUpdated2 + "\' ORDER BY ID ASC");
				stmt = cndata.createStatement();
				// System.out.println(query);
				rs = stmt.executeQuery(query);

				// TODO refactor after this read post
				/*
				 * query = ("Select * from " + Settings.posttn + " Where " +
				 * Settings.ptime + " > \'" + LastUpdated + "\' && " +
				 * Settings.ptime + " <= \'" + LastUpdated2 +
				 * "\' ORDER BY ID ASC"); //System.out.println(query); stmt =
				 * cndata.createStatement(); rs = stmt.executeQuery(query);
				 */
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
			rs.beforeFirst();
			ExecutorService es = Executors.newCachedThreadPool();
			while (rs.next())
				es.execute(new Topinions(rs.getInt(1)));
			es.shutdown();
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				System.out.println("ERROR THREAD OP");
				e.printStackTrace();
			}
			rs.beforeFirst();
			// System.out.println("HELLO");
			es = Executors.newCachedThreadPool();
			while (rs.next())
				es.execute(new Tposts(rs.getInt(1)));
			es.shutdown();
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				System.out.println("ERROR THREAD Posts");
				e.printStackTrace();
			}
			rs.close();
			stmt.close();
			cndata.close();
			System.out.println(" Load posts from remote " + (System.nanoTime() - stime));
			stime = System.nanoTime();

			// TODO ir buscar primeiro à DB LOCAL os users
			cnlocal = dbc.connlocal();

			String querycond = users.toString();
			System.out.println(querycond);
			querycond = querycond.replaceAll("\\[", "(").replaceAll("\\]", "\\)");
			// Load users from local DB
			query = ("Select * from authors where id in " + querycond);
			// System.out.println(query);
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
			System.out.println(" Load users local " + (System.nanoTime() - stime));
			stime = System.nanoTime();

			// Load users from foreign DB
			query = ("Select * from " + Settings.usertn + " where " + Settings.user_id + " in " + querycond);
			cndata = dbc.conndata();
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

		System.out.println(" Load users remote " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		opiniondb.forEach((k, v) -> {
			ArrayList<Integer> uniqueauthors = new ArrayList<Integer>();
			ArrayList<Post> temp_post = v.getPosts();
			temp_post.forEach((v2) -> {
				if (!uniqueauthors.contains(v2.getUID()))
					uniqueauthors.add(v2.getUID());
			});
			uniqueauthors.forEach((v3) -> {
				// System.out.println(authordb.containsKey(v3) + " " + v3);
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

		System.out.println(" update opinions " + (System.nanoTime() - stime));
		stime = System.nanoTime();

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
		System.out.println(" calc eval and reach " + (System.nanoTime() - stime));
		stime = System.nanoTime();

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
				// System.out.println(query1);
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

		System.out.println(" insert authors " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		ExecutorService es = Executors.newCachedThreadPool();

		opiniondb.forEach((k, opinion) -> {	
			es.execute(new Runnable() {
				@Override
				public void run() {
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
						e.printStackTrace();
					} finally {
						try {
							if (query1 != null)
								query1.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			});

		});
		es.shutdown();
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.out.println("ERROR THREAD OP");
			e.printStackTrace();
		}
		System.out.println(" insert opinions and posts " + (System.nanoTime() - stime));
		stime = System.nanoTime();

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
			e.printStackTrace();
		}
		;

		System.out.println(" update general " + (System.nanoTime() - stime));
		stime = System.nanoTime();

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

	// Runnables for multithreading

	class Tauthors implements Runnable {
		private Author a;

		public Tauthors(Author _a) {
			a = _a;
		}

		public void run() {
			try {
				cnlocal = dbc.connlocal();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}

			String insert = "INSERT INTO authors "
					+ "Values (?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE influence=?,comments=?,likes=?,views=?,posts=?";
			PreparedStatement query1 = null;
			try {
				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, a.getID());
				query1.setInt(2, a.getAge());
				query1.setString(3, a.getName());
				query1.setString(4, a.getGender());
				query1.setString(5, a.getLocation());
				query1.setDouble(6, a.getInfluence());
				query1.setInt(7, a.getComments());
				query1.setInt(8, a.getLikes());
				query1.setInt(9, a.getViews());
				query1.setInt(10, a.getPosts());
				query1.setDouble(11, a.getInfluence());
				query1.setInt(12, a.getComments());
				query1.setInt(13, a.getLikes());
				query1.setInt(14, a.getViews());
				query1.setInt(15, a.getPosts());
				// System.out.println(query1);
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

		}
	}

	class Topinions implements Runnable {
		private int id;
		private Connection condata;
		private Connection conlocal;

		public Topinions(int _id) {
			id = _id;

		}

		public void run() {
			try {
				condata = dbc.conndata();
				conlocal = dbc.connlocal();
				boolean remoto = true;
				String query = ("Select * from " + Settings.posttn + " Where " + Settings.post_id + " = " + id);
				Statement stmt = condata.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (!rs.next()) {
					query = ("Select * from posts Where id = " + id);
					stmt = conlocal.createStatement();
					rs = stmt.executeQuery(query);
					remoto = false;
					totalposts--;
					if (!rs.next()) {
						rs.close();
						stmt.close();
						condata.close();
						conlocal.close();
						return;
					}
				}
				// System.out.println(id);
				int postid = remoto ? rs.getInt(Settings.post_id) : rs.getInt(Settings.post_id);
				// System.out.println(id);
				int user_id = remoto ? rs.getInt(Settings.puser_id) : rs.getInt("authors_id");
				java.sql.Date time = remoto ? rs.getDate(Settings.pdate) : null;
				int likes = remoto ? rs.getInt(Settings.plikes) : rs.getInt("likes");
				int views = remoto ? rs.getInt(Settings.pviews) : rs.getInt("views");
				;
				String message = remoto ? rs.getString(Settings.pmessage) : rs.getString("message");
				Post _post = remoto ? new Post(postid, user_id, time, likes, views, message)
						: new Post(postid, user_id, null, likes, views, message);
				if (!(users.contains(user_id))) {
					users.add(user_id);
				}
				PSS pss = new PSS();
				int tag = pss.getTag(message);

				opiniondb.put(postid, new Opinion(_post, tag));
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (condata != null)
					condata.close();
				if (conlocal != null)
					conlocal.close();
			} catch (SQLException | ClassNotFoundException e) {
				System.out.println("ERROR loading Opinions");
				e.printStackTrace();
			}
		}
	}

	class Tposts implements Runnable {
		private int id;
		private Opinion _opin;
		private Connection condata;
		private Connection conlocal;

		public Tposts(int _id) {
			id = _id;
			_opin = opiniondb.get(id);

		}

		public void run() {
			try {
				// System.out.println("HELLO1");
				condata = dbc.conndata();
				conlocal = dbc.connlocal();
				String query = ("Select * from " + Settings.posttn + " Where " + Settings.rpost_id + " = " + id);
				Statement stmt = condata.createStatement();
				// System.out.println(query);
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					do {
						// System.out.println("HELLO2");
						int postid = rs.getInt(Settings.post_id);
						int user_id = rs.getInt(Settings.puser_id);
						java.sql.Date time = rs.getDate(Settings.pdate);
						int likes = rs.getInt(Settings.plikes);
						int views = rs.getInt(Settings.pviews);
						String message = rs.getString(Settings.pmessage);
						Post _post = new Post(postid, user_id, time, likes, views, message);
						if (!(users.contains(user_id))) {
							users.add(user_id);
						}
						_opin.addcomment(_post);
					} while (rs.next());
				}
				rs.close();
				stmt.close();
				query = ("Select * from posts Where opinions_id = " + id);
				stmt = conlocal.createStatement();
				rs = stmt.executeQuery(query);
				// System.out.println(query);
				if (rs.next()) {
					do {
						// System.out.println("HELLO3");
						int postid = rs.getInt("id");
						int user_id = rs.getInt("authors_id");
						int likes = rs.getInt("likes");
						int views = rs.getInt("views");
						String message = rs.getString("message");
						Post _post = new Post(postid, user_id, null, likes, views, message);
						if (!(users.contains(user_id)))
							users.add(user_id);
						_opin.addcomment(_post);
					} while (rs.next());
				}
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (condata != null)
					condata.close();
				if (conlocal != null)
					conlocal.close();
			} catch (

					SQLException | ClassNotFoundException e) {
				System.out.println("ERROR loading Posts");
				e.printStackTrace();
			}

			opiniondb.put(id, _opin);
		}
	}

}
