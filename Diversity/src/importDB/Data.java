package importDB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
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
	public static ConcurrentHashMap<Long, Model> modeldb = new ConcurrentHashMap<Long, Model>();
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
	
	public String loadJSON() throws JSONException {
		
		JSONObject json = new JSONObject();
		try {
			json = readJsonFromUrl(Settings.JSON_uri);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	    System.out.println(json.toString());
	    System.out.println(json.get("id"));
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		long stime = System.nanoTime();
		System.out.println(" Beginning " + stime);

		// DONE FAZER LOAD DAS VARIAVEIS NO GENERAL
		String select = "Select * from general WHERE id=1";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			cnlocal = Settings.connlocal();

			stmt = cnlocal.createStatement();
			rs = stmt.executeQuery(select);
			rs.next();
			totalviews = rs.getInt("totalviews");
			totalcomments = rs.getInt("totalcomments");
			totallikes = rs.getInt("totallikes");
			totalposts = rs.getInt("totalposts");
			LastUpdated = rs.getDate("lastupdated");
			if (rs.getInt("Version") != Settings.dbversion)
				rs.getLong("asdasasd");
		} catch (SQLException | ClassNotFoundException e1) {
			obj.put("Op", "Error");
			obj.put("Message",
					"Error (1): Local Database Error\r\n Please check if populated and Updated to latest version");
			result.put(obj);
			try {
				if (rs != null)

					rs.close();

				if (stmt != null)
					stmt.close();
				if (cndata != null)
					cndata.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			e1.printStackTrace();
			return result.toString();

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
				cal = Calendar.getInstance();
				LastUpdated2 = new java.sql.Date(cal.getTimeInMillis());
				query = ("Select distinct case \r\n when " + Settings.rptable_rpostid + " is null then "
						+ Settings.rptable_postid + "\r\n when " + Settings.rptable_rpostid + " is not null then "
						+ Settings.rptable_rpostid + " end from " + Settings.rptable + " Where " + Settings.ptime
						+ " > \'" + LastUpdated + "\' && " + Settings.ptime + " <= \'" + LastUpdated2
						+ "\' ORDER BY ID ASC");
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
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					continue;
				}
				break;
			} while (true);
			rs.beforeFirst();
			ExecutorService es = Executors.newFixedThreadPool(100);
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
			es = Executors.newFixedThreadPool(100);
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
			cnlocal = Settings.connlocal();

			String querycond = users.toString();
			System.out.println(querycond);
			querycond = querycond.replaceAll("\\[", "(").replaceAll("\\]", "\\)");
			// Load users from local DB
			query = ("Select * from " + Settings.latable + " where " + Settings.latable_id + " in " + querycond);
			// System.out.println(query);
			stmt = cnlocal.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (authordb.containsKey(rs.getInt("id"))) {
				} else {
					Author auth = new Author(rs.getInt(Settings.latable_id), rs.getString(Settings.latable_name),
							rs.getInt(Settings.latable_age), rs.getString(Settings.latable_gender),
							rs.getString(Settings.latable_location));
					auth.setComments(rs.getInt(Settings.latable_comments));
					auth.setLikes(rs.getInt(Settings.latable_likes));
					auth.setPosts(rs.getInt(Settings.latable_posts) - 1);
					auth.setViews(rs.getInt(Settings.latable_views));
					authordb.put(rs.getInt(Settings.latable_id), auth);
				}
			}
			rs.close();
			stmt.close();
			cnlocal.close();
			System.out.println(" Load users local " + (System.nanoTime() - stime));
			stime = System.nanoTime();

			// Load users from foreign DB
			query = ("Select * from " + Settings.rutable + " where " + Settings.rutable_userid + " in " + querycond);
			cndata = dbc.conndata();
			stmt = cndata.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (authordb.containsKey(rs.getInt(Settings.rutable_userid))) {
				} else {
					authordb.put(rs.getInt(Settings.rutable_userid),
							new Author(rs.getInt(Settings.rutable_userid), rs.getString(Settings.rutable_name),
									rs.getInt(Settings.rutable_age), rs.getString(Settings.rutable_gender),
									rs.getString(Settings.rutable_loc)));
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
			cnlocal = Settings.connlocal();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		authordb.forEach((k, author) -> {
			String insert = "INSERT INTO " + Settings.latable + " "
					+ "Values (?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.latable_influence + "=?,"
					+ Settings.latable_comments + "=?," + Settings.latable_likes + "=?," + Settings.latable_views
					+ "=?," + Settings.latable_posts + "=?";
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

		System.out.println(" insert " + Settings.latable + " " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		ExecutorService es = Executors.newFixedThreadPool(100);

		opiniondb.forEach((k, opinion) -> {
			es.execute(new Runnable() {
				@Override
				public void run() {
					String update = "INSERT INTO " + Settings.lotable + " "
							+ "Values (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.lotable_reach + "=?,"
							+ Settings.lotable_polarity + "=?," + Settings.lotable_influence + "=?,"
							+ Settings.lotable_comments + "=?";
					PreparedStatement query1 = null;
					try {
						query1 = cnlocal.prepareStatement(update);
						query1.setInt(1, k);
						query1.setDouble(2, opinion.getReach());
						query1.setDouble(3, opinion.getPolarity());
						query1.setDouble(4, opinion.getTotalInf());
						query1.setInt(5, opinion.getUID());
						query1.setDate(6, opinion.getTime());
						query1.setString(7, opinion.getPSS());
						query1.setInt(8, opinion.ncomments());
						query1.setInt(9, opinion.getProduct());
						query1.setDouble(10, opinion.getReach());
						query1.setDouble(11, opinion.getPolarity());
						query1.setDouble(12, opinion.getTotalInf());
						query1.setInt(13, opinion.ncomments());
						query1.executeUpdate();

						opinion.getPosts().forEach((post) -> {
							PreparedStatement query2 = null;
							try {
								String update1 = "REPLACE INTO " + Settings.lptable + " " + "Values (?,?,?,?,?,?,?)";
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

	public String load() throws JSONException {
		
		if(!(Settings.JSON_uri.equals("")))
		{
			return loadJSON();
		}
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		long stime = System.nanoTime();
		System.out.println(" Beginning " + stime);

		// DONE FAZER LOAD DAS VARIAVEIS NO GENERAL
		String select = "Select * from general WHERE id=1";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			cnlocal = Settings.connlocal();

			stmt = cnlocal.createStatement();
			rs = stmt.executeQuery(select);
			rs.next();
			totalviews = rs.getInt("totalviews");
			totalcomments = rs.getInt("totalcomments");
			totallikes = rs.getInt("totallikes");
			totalposts = rs.getInt("totalposts");
			LastUpdated = rs.getDate("lastupdated");
			if (rs.getInt("Version") != Settings.dbversion)
				rs.getLong("asdasasd");
		} catch (SQLException | ClassNotFoundException e1) {
			obj.put("Op", "Error");
			obj.put("Message",
					"Error (1): Local Database Error\r\n Please check if populated and Updated to latest version");
			result.put(obj);
			try {
				if (rs != null)

					rs.close();

				if (stmt != null)
					stmt.close();
				if (cndata != null)
					cndata.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			e1.printStackTrace();
			return result.toString();

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
				cal = Calendar.getInstance();
				LastUpdated2 = new java.sql.Date(cal.getTimeInMillis());
				query = ("Select distinct case \r\n when " + Settings.rptable_rpostid + " is null then "
						+ Settings.rptable_postid + "\r\n when " + Settings.rptable_rpostid + " is not null then "
						+ Settings.rptable_rpostid + " end from " + Settings.rptable + " Where " + Settings.ptime
						+ " > \'" + LastUpdated + "\' && " + Settings.ptime + " <= \'" + LastUpdated2
						+ "\' ORDER BY ID ASC");
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
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					continue;
				}
				break;
			} while (true);
			rs.beforeFirst();
			ExecutorService es = Executors.newFixedThreadPool(100);
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
			es = Executors.newFixedThreadPool(100);
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
			cnlocal = Settings.connlocal();

			String querycond = users.toString();
			System.out.println(querycond);
			querycond = querycond.replaceAll("\\[", "(").replaceAll("\\]", "\\)");
			// Load users from local DB
			query = ("Select * from " + Settings.latable + " where " + Settings.latable_id + " in " + querycond);
			// System.out.println(query);
			stmt = cnlocal.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (authordb.containsKey(rs.getInt("id"))) {
				} else {
					Author auth = new Author(rs.getInt(Settings.latable_id), rs.getString(Settings.latable_name),
							rs.getInt(Settings.latable_age), rs.getString(Settings.latable_gender),
							rs.getString(Settings.latable_location));
					auth.setComments(rs.getInt(Settings.latable_comments));
					auth.setLikes(rs.getInt(Settings.latable_likes));
					auth.setPosts(rs.getInt(Settings.latable_posts) - 1);
					auth.setViews(rs.getInt(Settings.latable_views));
					authordb.put(rs.getInt(Settings.latable_id), auth);
				}
			}
			rs.close();
			stmt.close();
			cnlocal.close();
			System.out.println(" Load users local " + (System.nanoTime() - stime));
			stime = System.nanoTime();

			// Load users from foreign DB
			query = ("Select * from " + Settings.rutable + " where " + Settings.rutable_userid + " in " + querycond);
			cndata = dbc.conndata();
			stmt = cndata.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (authordb.containsKey(rs.getInt(Settings.rutable_userid))) {
				} else {
					authordb.put(rs.getInt(Settings.rutable_userid),
							new Author(rs.getInt(Settings.rutable_userid), rs.getString(Settings.rutable_name),
									rs.getInt(Settings.rutable_age), rs.getString(Settings.rutable_gender),
									rs.getString(Settings.rutable_loc)));
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
			cnlocal = Settings.connlocal();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		authordb.forEach((k, author) -> {
			String insert = "INSERT INTO " + Settings.latable + " "
					+ "Values (?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.latable_influence + "=?,"
					+ Settings.latable_comments + "=?," + Settings.latable_likes + "=?," + Settings.latable_views
					+ "=?," + Settings.latable_posts + "=?";
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

		System.out.println(" insert " + Settings.latable + " " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		ExecutorService es = Executors.newFixedThreadPool(100);

		opiniondb.forEach((k, opinion) -> {
			es.execute(new Runnable() {
				@Override
				public void run() {
					String update = "INSERT INTO " + Settings.lotable + " "
							+ "Values (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.lotable_reach + "=?,"
							+ Settings.lotable_polarity + "=?," + Settings.lotable_influence + "=?,"
							+ Settings.lotable_comments + "=?";
					PreparedStatement query1 = null;
					try {
						query1 = cnlocal.prepareStatement(update);
						query1.setInt(1, k);
						query1.setDouble(2, opinion.getReach());
						query1.setDouble(3, opinion.getPolarity());
						query1.setDouble(4, opinion.getTotalInf());
						query1.setInt(5, opinion.getUID());
						query1.setDate(6, opinion.getTime());
						query1.setString(7, opinion.getPSS());
						query1.setInt(8, opinion.ncomments());
						query1.setInt(9, opinion.getProduct());
						query1.setDouble(10, opinion.getReach());
						query1.setDouble(11, opinion.getPolarity());
						query1.setDouble(12, opinion.getTotalInf());
						query1.setInt(13, opinion.ncomments());
						query1.executeUpdate();

						opinion.getPosts().forEach((post) -> {
							PreparedStatement query2 = null;
							try {
								String update1 = "REPLACE INTO " + Settings.lptable + " " + "Values (?,?,?,?,?,?,?)";
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
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
	
	private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("Unicode")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
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
				cnlocal = Settings.connlocal();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}

			String insert = "INSERT INTO " + Settings.latable + " "
					+ "Values (?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.latable_influence + "=?,"
					+ Settings.latable_comments + "=?," + Settings.latable_likes + "=?," + Settings.latable_views
					+ "=?," + Settings.latable_posts + "=?";
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
				conlocal = Settings.connlocal();
				boolean remoto = true;
				String query = ("Select * from " + Settings.rptable + " Where " + Settings.rptable_postid + " = " + id);
				Statement stmt = condata.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (!rs.next()) {
					query = ("Select * from " + Settings.lptable + " Where " + Settings.lptable_id + " = " + id);
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
				int postid = remoto ? rs.getInt(Settings.rptable_postid) : rs.getInt(Settings.lptable_opinion);
				// System.out.println(id);
				int user_id = remoto ? rs.getInt(Settings.rptable_userid) : rs.getInt(Settings.lptable_authorid);
				java.sql.Date time = remoto ? rs.getDate(Settings.rptable_date) : null;
				int likes = remoto ? rs.getInt(Settings.rptable_likes) : rs.getInt(Settings.lptable_likes);
				int views = remoto ? rs.getInt(Settings.rptable_views) : rs.getInt(Settings.lptable_views);
				;
				String message = remoto ? rs.getString(Settings.rptable_message)
						: rs.getString(Settings.lptable_message);
				Post _post = remoto ? new Post(postid, user_id, time, likes, views, message)
						: new Post(postid, user_id, null, likes, views, message);
				if (!(users.contains(user_id))) {
					users.add(user_id);
				}
				PSS pss = new PSS();
				String tag = pss.getTag(message);
				int product = pss.getProduct(message);

				opiniondb.put(postid, new Opinion(_post, tag, product));
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
				conlocal = Settings.connlocal();
				String query = ("Select * from " + Settings.rptable + " Where " + Settings.rptable_rpostid + " = "
						+ id);
				Statement stmt = condata.createStatement();
				// System.out.println(query);
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) {
					do {
						// System.out.println("HELLO2");
						int postid = rs.getInt(Settings.rptable_postid);
						int user_id = rs.getInt(Settings.rptable_userid);
						java.sql.Date time = rs.getDate(Settings.rptable_date);
						int likes = rs.getInt(Settings.rptable_likes);
						int views = rs.getInt(Settings.rptable_views);
						String message = rs.getString(Settings.rptable_message);
						Post _post = new Post(postid, user_id, time, likes, views, message);
						if (!(users.contains(user_id))) {
							users.add(user_id);
						}
						_opin.addcomment(_post);
					} while (rs.next());
				}
				rs.close();
				stmt.close();
				query = ("Select * from " + Settings.lptable + " Where " + Settings.lptable_opinion + " = " + id);
				stmt = conlocal.createStatement();
				rs = stmt.executeQuery(query);
				// System.out.println(query);
				if (rs.next()) {
					do {
						// System.out.println("HELLO3");
						int postid = rs.getInt(Settings.lptable_id);
						int user_id = rs.getInt(Settings.lptable_authorid);
						int likes = rs.getInt(Settings.lptable_likes);
						int views = rs.getInt(Settings.lptable_views);
						String message = rs.getString(Settings.lptable_message);
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

/*	class Tmodels implements Runnable {
		private Model model;
		private Connection conlocal;

		public Tmodels() {
		}
*/
		public void Tmodels() {
			// System.out.println("HELLO1");
			try{
			cnlocal = Settings.connlocal();
			String query = ("Select * from " + Settings.lmtable);
			Statement stmt = cnlocal.createStatement();
			// System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				do {
					Model model = new Model(rs.getLong(Settings.latable_id), rs.getInt(Settings.lmtable_update),
							rs.getInt(Settings.lmtable_creator), rs.getString(Settings.lmtable_name),
							rs.getString(Settings.lmtable_uri), rs.getString(Settings.lmtable_pss),
							rs.getString(Settings.lmtable_age), rs.getString(Settings.lmtable_gender),
							rs.getBoolean(Settings.lmtable_monitorfinal), rs.getBoolean(Settings.lmtable_archived));
					Data.modeldb.put(model.getId(), model);
				} while (rs.next());
			}
			rs.close();
			stmt.close();
			cnlocal.close();
			}catch(SQLException | ClassNotFoundException e){
				e.printStackTrace();
			}
		}

	//}
}
