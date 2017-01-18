package general;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Class Data.
 */
public class Data {
	private static final Logger LOGGER = Logger.getLogger(Data.class.getName());
	private ConcurrentHashMap<Long, Author> authordb = new ConcurrentHashMap<Long, Author>();
	private ConcurrentHashMap<String, Author> authordb2 = new ConcurrentHashMap<String, Author>();
	private ConcurrentHashMap<Long, Opinion> opiniondb = new ConcurrentHashMap<Long, Opinion>();

	/** The modeldb. */
	public static ConcurrentHashMap<Long, Model> modeldb = new ConcurrentHashMap<Long, Model>();

	/** The pssdb. */
	public static ConcurrentHashMap<Long, PSS> pssdb = new ConcurrentHashMap<Long, PSS>();

	/** The productdb. */
	public static ConcurrentHashMap<Long, Product> productdb = new ConcurrentHashMap<Long, Product>();

	/** The servicedb. */
	public static ConcurrentHashMap<Long, Product> servicedb = new ConcurrentHashMap<Long, Product>();

	/** The companydb. */
	public static ConcurrentHashMap<Long, Company> companydb = new ConcurrentHashMap<Long, Company>();
	private long totalposts;
	private long totalviews;
	private long totalcomments;
	private long totallikes;
	private java.sql.Date LastUpdated = null;
	private java.sql.Date LastUpdated2 = null;
	private Calendar cal = Calendar.getInstance();
	private List<Long> users = new ArrayList<Long>();
	private List<Author> users2 = new ArrayList<Author>();

	private Connection cndata = null;
	private Connection cnlocal = null;
	private Connection cncr = null;

	/**
	 * Instantiates a new data.
	 */
	public Data() {
	}

	/**
	 * Identify PSS byproduct.
	 *
	 * @param product
	 *            the product id
	 * @return the long id
	 */
	public static long identifyPSSbyproduct(long product) {
		if (product == 0)
			return 0;
		long id = productdb.get(product).get_Id();
		for (PSS pss : pssdb.values()) {
			if (pss.get_products().contains(id))
				return pss.getID();
		}
		return 0;

	}

	/**
	 * Identify PSS byname.
	 *
	 * @param name
	 *            the name of the pss
	 * @return the long id
	 */
	public static long identifyPSSbyname(String name) {

		for (PSS a : pssdb.values()) {
			if (a.getName().equals(name))
				return a.getID();
		}
		return 0;

	}

	/**
	 * Identify product by message.
	 *
	 * @param message
	 *            the message
	 * @return the long id
	 */
	public static long identifyProduct(String message) {

		for (Product a : productdb.values()) {
			if (a.checkMessage(message) == true)
				return a.get_Id();
		}

		return 0;
	}

	@SuppressWarnings("resource")
	private void LoadPSS() {
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			String select = "Select * from " + Settings.crpsstable;
			cncr = Settings.conncr();
			query = cncr.prepareStatement(select);
			rs = query.executeQuery();
			while (rs.next()) {
				pssdb.put(rs.getLong(Settings.crpsstable_id),
						new PSS(rs.getLong(Settings.crpsstable_id), rs.getLong(Settings.crpsstable_company),
								rs.getString(Settings.crpsstable_name), rs.getLong(Settings.crpsstable_author),
								rs.getString(Settings.crpsstable_type)));
			}

			select = "Select * from " + Settings.crproducttable + " ORDER BY " + Settings.crproducttable_id + " ASC";
			query = cncr.prepareStatement(select);
			rs = query.executeQuery();

			while (rs.next()) {
				productdb.put(rs.getLong(Settings.crproducttable_id),
						new Product(rs.getLong(Settings.crproducttable_id), rs.getString(Settings.crproducttable_name),
								rs.getBoolean(Settings.crproducttable_isfinal),
								rs.getLong(Settings.crproducttable_supplied_by),
								rs.getLong(Settings.crproducttable_parent)));
				if (rs.getLong(Settings.crproducttable_parent) != 0) {
					Product parent = productdb.get(rs.getLong(Settings.crproducttable_parent));
					parent.setParent(rs.getLong(Settings.crproducttable_id));
					productdb.put(rs.getLong(Settings.crproducttable_parent), parent);
				}
			}

			select = "Select * from " + Settings.crcompanytable;
			query = cncr.prepareStatement(select);
			rs = query.executeQuery();

			while (rs.next()) {
				companydb.put(rs.getLong(Settings.crcompanytable_id),
						new Company(rs.getLong(Settings.crcompanytable_id), rs.getString(Settings.crcompanytable_name),
								rs.getString(Settings.crcompanytable_type),
								rs.getLong(Settings.crcompanytable_belongs_to)));
			}

			select = "Select * from " + Settings.crpssproducttable;

			query = cncr.prepareStatement(select);
			rs = query.executeQuery();

			while (rs.next()) {
				pssdb.get(rs.getLong(Settings.crrpssproducttable_pss))
						.add_product(rs.getLong(Settings.crrpssproducttable_product));
				/*
				 * productdb.get(rs.getLong(Settings.crrpssproducttable_product)
				 * ) .set_PSS(rs.getLong(Settings.crrpssproducttable_pss));
				 */
			}

			rs.close();
			query.close();
			cncr.close();

		} catch (SQLException e1) {
			LOGGER.log(Level.SEVERE, "ERROR: Cannot connect to Common Repository Check setup");
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.FINE, "ERROR: Settings class not found inside LoadPSS() on Data class");
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here error closing");
			}
			try {
				if (query != null)
					query.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here error closing");
			}
			try {
				if (cncr != null)
					cncr.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here error closing");
			}
		}
	}

	/**
	 * Loads the program
	 *
	 * @param json
	 *            the json with the information
	 * @return the string to check if successfull or not
	 * @throws JSONException
	 *             the JSON exception
	 */
	public String load(JSONArray json) throws JSONException {

		return loadJSON(json);
	}

	private String loadJSON(JSONArray json) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		long stime = System.nanoTime();
		System.out.println(" Beginning " + stime);

		LoadPSS();

		// DONE FAZER LOAD DAS VARIAVEIS NO GENERAL
		String select = "Select * from general WHERE id=1";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			cnlocal = Settings.connlocal();

			stmt = cnlocal.createStatement();
			rs = stmt.executeQuery(select);
			rs.next();
			totalviews = rs.getLong("totalviews");
			totalcomments = rs.getLong("totalcomments");
			totallikes = rs.getLong("totallikes");
			totalposts = rs.getLong("totalposts");
			LastUpdated = rs.getDate("lastupdated");
			if (rs.getLong("Version") != Settings.dbversion)
				rs.getLong("asdasasd");
		} catch (SQLException | ClassNotFoundException e1) {
			obj.put("Op", "Error");
			obj.put("Message",
					"Error (1): Local Database Error\r\n Please Update to latest version " + Settings.dbversion);
			result.put(obj);
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (cnlocal != null)
					cnlocal.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			LOGGER.log(Level.SEVERE, "Local database either missing or not on last version please update");
			return result.toString();

		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, "Nothing can be done here error closing");
			}
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, "Nothing can be done here error closing");
			}
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, "Nothing can be done here error closing");
			}
		}
		// Load PSS

		System.out.println(" Variable Init " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		// Load Data
		try {
			ExecutorService es = Executors.newFixedThreadPool(50);
			for (int id = 0; id < json.length(); id++)
				es.execute(new Topinions(json.getJSONObject(id)));
			es.shutdown();
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				LOGGER.log(Level.FINE, "Error on Thread Opinions while loading data");
			}
			// System.out.println("HELLO");
			es = Executors.newFixedThreadPool(50);
			for (int id = 0; id < json.length(); id++)
				es.execute(new Tposts(json.getJSONObject(id)));
			es.shutdown();
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				LOGGER.log(Level.FINE, "Error on Thread Posts while loading data");
			}
			System.out.println(" Load posts from remote " + (System.nanoTime() - stime));
			stime = System.nanoTime();

			// Fetch local DB models

			cnlocal = Settings.connlocal();
			select = "Select * from " + Settings.lmtable;
			stmt = cnlocal.createStatement();
			// System.out.println(select);
			rs = stmt.executeQuery(select);

			if (rs.next()) {
				rs.beforeFirst();
				for (; rs.next();) {
					Model model = new Model(rs.getLong(Settings.lmtable_id), rs.getLong(Settings.lmtable_update),
							rs.getLong(Settings.lmtable_creator), rs.getString(Settings.lmtable_name),
							rs.getString(Settings.lmtable_uri), rs.getLong(Settings.lmtable_pss),
							rs.getString(Settings.lmtable_age), rs.getString(Settings.lmtable_gender),
							rs.getString(Settings.lmtable_monitorfinal), rs.getBoolean(Settings.lmtable_archived),
							rs.getLong(Settings.lmtable_cdate), rs.getLong(Settings.lmtable_udate));
					Data.modeldb.put(model.getId(), model);

				}
			}

			// Fetch local DB for users
			rs.close();
			stmt.close();
			cnlocal.close();
			cnlocal = Settings.connlocal();
			String querycond = "";
			for (Author user : users2) {
				if (user == null)
					continue;
				System.out.println("\n DEBUG IF HAPPENS USERID=" + user.getUID());
				System.out.println(" RESULT STRING " + querycond);
				querycond += user.getUID() + ",";
			}
			System.out.println(querycond);

			// Load users from local DB
			if (users2.size() != 0) {
				select = ("Select * from " + Settings.latable + " where " + Settings.latable_id + " in (");

				for (int i = 0; i < users2.size() - 1; i++)
					select += "?,";
				select += "?)";

				// System.out.println(query);
				PreparedStatement stmt2 = cnlocal.prepareStatement(select);
				for (int i = 0; i < users2.size(); i++) {
					stmt2.setString(i + 1, querycond.split(",")[i]);
				}
				rs = stmt2.executeQuery();
				// System.out.println(stmt2);
				while (rs.next()) {
					if (authordb2
							.containsKey(rs.getString(Settings.latable_id) + rs.getString(Settings.latable_source))) {
					} else {
						Author auth = new Author(rs.getString(Settings.latable_id),
								rs.getString(Settings.latable_source), rs.getString(Settings.latable_name),
								rs.getLong(Settings.latable_age), rs.getString(Settings.latable_gender),
								rs.getString(Settings.latable_location));
						auth.setComments(rs.getLong(Settings.latable_comments));
						auth.setLikes(rs.getLong(Settings.latable_likes));
						auth.setPosts(rs.getLong(Settings.latable_posts) - 1);
						auth.setViews(rs.getLong(Settings.latable_views));
						authordb2.put(rs.getString(Settings.latable_id) + "," + rs.getString(Settings.latable_source),
								auth);
					}
				}
				stmt2.close();
			}

			rs.close();
			stmt.close();
			cnlocal.close();
			System.out.println(" Load users local " + (System.nanoTime() - stime));
			stime = System.nanoTime();

			/*
			 * // Load users from foreign DB select = ("Select * from " +
			 * Settings.rutable + " where " + Settings.rutable_userid + " in " +
			 * querycond); cndata = Settings.conndata(); stmt =
			 * cndata.createStatement(); rs = stmt.executeQuery(select); while
			 * (rs.next()) { if
			 * (authordb.containsKey(rs.getInt(Settings.rutable_userid))) { }
			 * else { authordb.put(rs.getInt(Settings.rutable_userid), new
			 * Author(rs.getInt(Settings.rutable_userid),
			 * rs.getString(Settings.rutable_name),
			 * rs.getInt(Settings.rutable_age),
			 * rs.getString(Settings.rutable_gender),
			 * rs.getString(Settings.rutable_loc))); } }
			 */

			// Load users from JSON

			for (Author user : users2) {
				if (authordb2.containsKey(user.getUID() + "," + user.getSource())) {
					Author luser = authordb2.get(user.getUID() + "," + user.getSource());
					boolean gender = false, location = false, age = false;
					location = luser.getLocation().equals("");
					gender = luser.getGender().equals("");
					age = luser.getAge() == -1;
					if (gender || location || age) {
						authordb2.put(user.getUID() + "," + user.getSource(),
								new Author(user.getUID(), user.getSource(), user.getName(),
										age ? luser.getAge() : user.getAge(),
										gender ? luser.getGender() : user.getGender(),
										location ? luser.getLocation() : user.getLocation()));
					}

				} else {
					authordb2.put(user.getUID() + "," + user.getSource(), user);
				}
				;
			}
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.FINE, e.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			obj.put("Op", "Error");
			obj.put("Message", "Error (2): Remote Database Error\r\n Please check if populated");
			result.put(obj);
			LOGGER.log(Level.SEVERE, "Remote Database Error, Please Check if Populated");
			return result.toString();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					LOGGER.log(Level.FINE, "Nothing can be done here error closing");
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					LOGGER.log(Level.FINE, "Nothing can be done here error closing");
				}
			if (cndata != null)
				try {
					cndata.close();
				} catch (SQLException e) {
					LOGGER.log(Level.FINE, "Nothing can be done here error closing");
				}
			if (cnlocal != null)
				try {
					cnlocal.close();
				} catch (SQLException e) {
					LOGGER.log(Level.FINE, "Nothing can be done here error closing");
				}
		}

		System.out.println(" Load users remote " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		opiniondb.forEach((k, v) -> {
			ArrayList<String> uniqueauthors2 = new ArrayList<String>();
			ArrayList<Post> temp_post = v.getPosts();
			temp_post.forEach((v2) -> {
				if (!uniqueauthors2.contains(v2.getUID(true) + "," + v2.getSource()))
					uniqueauthors2.add(v2.getUID(true) + "," + v2.getSource());
			});
			uniqueauthors2.forEach((v3) -> {
				System.out.println(authordb2.containsKey(v3) + " " + v3);
				Author temp_author = authordb2.get(v3);
				temp_author.addComments(v.newcomments());
				temp_author.addLikes(v.newlikes());
				temp_author.addViews(v.newviews());
				temp_author.addPosts();
				authordb2.put(v3, temp_author);
			});

			totalcomments += v.newcomments();
			totallikes += v.newlikes();
			totalviews += v.newviews();
		});

		System.out.println(" update opinions " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		this.totalposts += opiniondb.size();// Modified
		authordb2.forEach((k, v) -> {
			v.calcInfluence((totalcomments / ((double) totalposts)), totallikes / ((double) totalposts),
					totalviews / ((double) totalposts));
		});
		opiniondb.forEach((k, v) -> {
			v.evalReach(totalcomments / ((double) totalposts), totallikes / ((double) totalposts),
					totalviews / ((double) totalposts));
			v.evalPolarity2(authordb2);
		});
		System.out.println(" calc eval and reach " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		try {
			cnlocal = Settings.connlocal();
		} catch (ClassNotFoundException e1) {
			LOGGER.log(Level.FINE, "Settings Static Class Not Found");
		}
		authordb2.forEach((k, author) -> {
			String insert = "INSERT INTO " + Settings.latable + " "
					+ "Values (?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.latable_influence + "=?,"
					+ Settings.latable_comments + "=?," + Settings.latable_likes + "=?," + Settings.latable_views
					+ "=?," + Settings.latable_posts + "=?";
			PreparedStatement query1 = null;
			try {
				query1 = cnlocal.prepareStatement(insert);
				query1.setString(1, author.getUID());
				query1.setLong(2, author.getAge());
				query1.setString(3, author.getName());
				query1.setString(4, author.getGender());
				query1.setString(5, author.getLocation());
				query1.setDouble(6, author.getInfluence());
				query1.setLong(7, author.getComments());
				query1.setLong(8, author.getLikes());
				query1.setLong(9, author.getViews());
				query1.setLong(10, author.getPosts());
				query1.setString(11, author.getSource());
				query1.setDouble(12, author.getInfluence());
				query1.setLong(13, author.getComments());
				query1.setLong(14, author.getLikes());
				query1.setLong(15, author.getViews());
				query1.setLong(16, author.getPosts());
				// System.out.println(query1);
				query1.executeUpdate();
			} catch (Exception e) {
				LOGGER.log(Level.FINE, e.toString(), e);
				return;
			} finally {
				try {
					if (query1 != null)
						query1.close();
				} catch (Exception e) {
					LOGGER.log(Level.FINE, "Nothing can be done here error closing");
				}

			}
		});

		System.out.println(" insert " + Settings.latable + " " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		ExecutorService es = Executors.newFixedThreadPool(50);

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
						query1.setLong(1, k);
						query1.setDouble(2, opinion.getReach());
						query1.setDouble(3, opinion.getPolarity());
						query1.setDouble(4, opinion.getTotalInf());
						query1.setString(5, opinion.getUID(true));
						query1.setLong(6, opinion.getTime());
						query1.setLong(7, opinion.getPSS());
						query1.setLong(8, opinion.ncomments());
						query1.setLong(9, opinion.getProduct());
						query1.setDouble(10, opinion.getReach());
						query1.setDouble(11, opinion.getPolarity());
						query1.setDouble(12, opinion.getTotalInf());
						query1.setLong(13, opinion.ncomments());
						query1.executeUpdate();

						opinion.getPosts().forEach((post) -> {
							PreparedStatement query2 = null;
							try {
								String update1 = "REPLACE INTO " + Settings.lptable + " " + "Values (?,?,?,?,?,?,?)";
								query2 = cnlocal.prepareStatement(update1);
								query2.setLong(1, post.getID());
								query2.setDouble(2, post.getPolarity());
								query2.setString(3, post.getComment());
								query2.setLong(4, post.getLikes());
								query2.setLong(5, post.getViews());
								query2.setLong(6, k);
								query2.setString(7, post.getUID(true));

								query2.executeUpdate();
								if (query2 != null)
									query2.close();
							} catch (Exception e) {
								LOGGER.log(Level.FINE, "Nothing can be done here error closing");
							} finally {
								try {
									if (query2 != null)
										query2.close();
								} catch (Exception e) {
									LOGGER.log(Level.FINE, "Nothing can be done here error closing");
								}
							}
						});
					} catch (Exception e) {
						LOGGER.log(Level.FINE, e.toString(), e);
					} finally {
						try {
							if (query1 != null)
								query1.close();
						} catch (Exception e) {
							LOGGER.log(Level.FINE, "Nothing can be done here error closing");
						}
					}

				}
			});

		});
		es.shutdown();
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Error on Thread that adds Post to Local Database", e);
		}
		System.out.println(" insert opinions and posts " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		String update = "UPDATE general SET totalposts=?,totallikes=?,totalcomments=?,totalviews=?,lastupdated=? WHERE id=1";

		PreparedStatement query1 = null;
		try {
			query1 = cnlocal.prepareStatement(update);
			query1.setLong(1, totalposts);
			query1.setLong(2, totallikes);
			query1.setLong(3, totalcomments);
			query1.setLong(4, totalviews);
			query1.setDate(5, (Date) LastUpdated2);
			query1.executeUpdate();
		} catch (SQLException e1) {
			LOGGER.log(Level.FINE, "SQL Error", e1);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Nothing can be done here error closing");
			}
			try {
				if (query1 != null)
					query1.close();
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Nothing can be done here error closing");
			}
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Nothing can be done here error closing");
			}
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
	 * Load from local database
	 *
	 * @return the string if successful or not
	 * @throws JSONException
	 *             the JSON exception
	 */
	public String load() throws JSONException {

		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		long stime = System.nanoTime();
		System.out.println(" Beginning " + stime);
		LoadPSS();
		// General Variable Load
		String select = "Select * from general WHERE id=1";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			cnlocal = Settings.connlocal();

			stmt = cnlocal.createStatement();
			rs = stmt.executeQuery(select);
			rs.next();
			totalviews = rs.getLong("totalviews");
			totalcomments = rs.getLong("totalcomments");
			totallikes = rs.getLong("totallikes");
			totalposts = rs.getLong("totalposts");
			LastUpdated = rs.getDate("lastupdated");
			if (rs.getLong("Version") != Settings.dbversion)
				rs.getLong("asdasasd");
		} catch (SQLException | ClassNotFoundException e1) {
			obj.put("Op", "Error");
			obj.put("Message",
					"Error (1): Local Database Error\r\n Please check if populated and Updated to latest version");
			result.put(obj);
			try {
				if (rs != null)

					rs.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
			try {

				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
			try {
				if (cndata != null)
					cndata.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
			LOGGER.log(Level.SEVERE, "Local database either missing or not on last version please update");
			return result.toString();

		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
		}

		String query;

		stmt = null;
		rs = null;

		// Load local models

		try {
			cnlocal = Settings.connlocal();

			query = "Select * from " + Settings.lmtable;
			stmt = cnlocal.createStatement();
			// System.out.println(query);
			rs = stmt.executeQuery(query);

			for (; rs.next();) {
				Model model = new Model(rs.getLong(Settings.lmtable_id), rs.getLong(Settings.lmtable_update),
						rs.getLong(Settings.lmtable_creator), rs.getString(Settings.lmtable_name),
						rs.getString(Settings.lmtable_uri), rs.getLong(Settings.lmtable_pss),
						rs.getString(Settings.lmtable_age), rs.getString(Settings.lmtable_gender),
						rs.getString(Settings.lmtable_monitorfinal), rs.getBoolean(Settings.lmtable_archived),
						rs.getLong(Settings.lmtable_cdate), rs.getLong(Settings.lmtable_udate));
				Data.modeldb.put(model.getId(), model);

			}
			rs.close();
			stmt.close();
			cnlocal.close();
		} catch (ClassNotFoundException | SQLException e2) {
			LOGGER.log(Level.SEVERE, "Error on SQL Input", e2);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
		}

		// Load PSS

		System.out.println(" Variable Init " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		// Load Data
		try {
			cndata = Settings.conndata();
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
				rs = stmt.executeQuery(query);
				if (!rs.next()) {
					obj.put("Op", "Error");
					obj.put("Message", "Loaded Successfully");
					result.put(obj);
					try {
						rs.close();
					} catch (Exception e) {
						LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
					}
					try {
						stmt.close();
					} catch (Exception e) {
						LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
					}
					try {
						cndata.close();
					} catch (Exception e) {
						LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
					}
					try {
						cnlocal.close();
					} catch (Exception e) {
						LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
					}
					return result
							.toString();/*
										 * LastUpdated = LastUpdated2; if
										 * (LastUpdated.after(new
										 * java.sql.Date(Calendar.getInstance().
										 * getTimeInMillis()))) { obj.put("Op",
										 * "Error"); obj.put("Message",
										 * "Error (2): Remote Database Error\r\n Please check if populated"
										 * ); result.put(obj); if (rs != null)
										 * rs.close(); if (stmt != null)
										 * stmt.close(); if (cndata != null)
										 * cndata.close(); return
										 * result.toString(); } if (rs != null)
										 * rs.close(); if (stmt != null)
										 * stmt.close(); continue;
										 */
				}
				break;
			} while (true);
			rs.beforeFirst();
			ExecutorService es = Executors.newFixedThreadPool(50);
			while (rs.next())
				es.execute(new Topinions(rs.getLong(1)));
			es.shutdown();
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, "Error on Thread that loads Opinions");
			}
			rs.beforeFirst();
			// System.out.println("HELLO");
			es = Executors.newFixedThreadPool(50);
			while (rs.next())
				es.execute(new Tposts(rs.getLong(1)));
			es.shutdown();
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, "Error on Thread that Loads posts");
			}
			rs.close();
			stmt.close();
			cndata.close();
			System.out.println(" Load posts from remote " + (System.nanoTime() - stime));
			stime = System.nanoTime();

			// Load local DB users
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
				if (authordb.containsKey(rs.getLong("id"))) {
				} else {
					Author auth = new Author(rs.getLong(Settings.latable_id), rs.getString(Settings.latable_name),
							rs.getLong(Settings.latable_age), rs.getString(Settings.latable_gender),
							rs.getString(Settings.latable_location));
					auth.setComments(rs.getLong(Settings.latable_comments));
					auth.setLikes(rs.getLong(Settings.latable_likes));
					auth.setPosts(rs.getLong(Settings.latable_posts) - 1);
					auth.setViews(rs.getLong(Settings.latable_views));
					authordb.put(rs.getLong(Settings.latable_id), auth);
				}
			}
			rs.close();
			stmt.close();
			cnlocal.close();
			System.out.println(" Load users local " + (System.nanoTime() - stime));
			stime = System.nanoTime();

			// Load users from foreign DB
			query = ("Select * from " + Settings.rutable + " where " + Settings.rutable_userid + " in " + querycond);
			cndata = Settings.conndata();
			stmt = cndata.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (authordb.containsKey(rs.getLong(Settings.rutable_userid))) {
				} else {
					authordb.put(rs.getLong(Settings.rutable_userid),
							new Author(rs.getLong(Settings.rutable_userid), rs.getString(Settings.rutable_name),
									rs.getLong(Settings.rutable_age), rs.getString(Settings.rutable_gender),
									rs.getString(Settings.rutable_loc)));
				}
			}
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.FINE, e.toString(), e);
		} catch (SQLException e) {
			e.printStackTrace();
			obj.put("Op", "Error");
			obj.put("Message", "Error (2): Remote Database Error\r\n Please check if populated");
			result.put(obj);
			LOGGER.log(Level.SEVERE, "Error Accessing Remote Databse Please Check If Populated");
			return result.toString();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
				}
			if (cndata != null)
				try {
					cndata.close();
				} catch (SQLException e) {
					LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
				}
		}
		System.out.println(" Load users remote " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		opiniondb.forEach((k, v) -> {
			ArrayList<Long> uniqueauthors = new ArrayList<Long>();
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
			cnlocal.setAutoCommit(false);
		} catch (ClassNotFoundException e1) {
			LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
		} catch (SQLException e) {
			LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
		}
		authordb.forEach((k, author) -> {
			String insert = "INSERT INTO " + Settings.latable + " "
					+ "Values (?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.latable_influence + "=?,"
					+ Settings.latable_comments + "=?," + Settings.latable_likes + "=?," + Settings.latable_views
					+ "=?," + Settings.latable_posts + "=?";
			PreparedStatement query1 = null;
			try {
				query1 = cnlocal.prepareStatement(insert);
				query1.setLong(1, author.getID());
				query1.setLong(2, author.getAge());
				query1.setString(3, author.getName());
				query1.setString(4, author.getGender());
				query1.setString(5, author.getLocation());
				query1.setDouble(6, author.getInfluence());
				query1.setLong(7, author.getComments());
				query1.setLong(8, author.getLikes());
				query1.setLong(9, author.getViews());
				query1.setLong(10, author.getPosts());
				query1.setString(11, author.getSource() != null ? author.getSource() : "Not Avaliable");
				query1.setDouble(12, author.getInfluence());
				query1.setLong(13, author.getComments());
				query1.setLong(14, author.getLikes());
				query1.setLong(15, author.getViews());
				query1.setLong(16, author.getPosts());
				// System.out.println(query1);
				query1.executeUpdate();
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Error Inserting Author into Database");
				return;
			} finally {
				try {
					if (query1 != null)
						query1.close();
				} catch (Exception e) {
					LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
				}
			}
		});
		try {
			cnlocal.commit();
			cnlocal.close();
			cnlocal = Settings.connlocal();
			cnlocal.setAutoCommit(false);
		} catch (SQLException | ClassNotFoundException e2) {
			try {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
				cnlocal.rollback();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
			LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
		}

		System.out.println(" insert " + Settings.latable + " " + (System.nanoTime() - stime));
		stime = System.nanoTime();
		// TODO
		ExecutorService es = Executors.newFixedThreadPool(50);

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
						query1.setLong(1, k);
						query1.setDouble(2, opinion.getReach());
						query1.setDouble(3, opinion.getPolarity());
						query1.setDouble(4, opinion.getTotalInf());
						query1.setLong(5, opinion.getUID());
						query1.setLong(6, opinion.getTime());
						query1.setLong(7, opinion.getPSS());
						query1.setLong(8, opinion.ncomments());
						query1.setLong(9, opinion.getProduct());
						query1.setDouble(10, opinion.getReach());
						query1.setDouble(11, opinion.getPolarity());
						query1.setDouble(12, opinion.getTotalInf());
						query1.setLong(13, opinion.ncomments());
						query1.executeUpdate();

						opinion.getPosts().forEach((post) -> {
							PreparedStatement query2 = null;
							try {
								String update1 = "REPLACE INTO " + Settings.lptable + " " + "Values (?,?,?,?,?,?,?)";
								query2 = cnlocal.prepareStatement(update1);
								query2.setLong(1, post.getID());
								if (Settings.LocalPolarity) {
									query2.setDouble(2, post.getPolarity());
								} else {
									query2.setNull(2, java.sql.Types.DOUBLE);
								}
								query2.setString(3, post.getComment());
								query2.setLong(4, post.getLikes());
								query2.setLong(5, post.getViews());
								query2.setLong(6, k);
								query2.setLong(7, post.getUID());

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
		try {
			cnlocal.commit();
			cnlocal.close();
			cnlocal = Settings.connlocal();
			cnlocal.setAutoCommit(true);
		} catch (SQLException e2) {
			try {
				cnlocal.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			e2.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(" insert opinions and posts " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		String update = "UPDATE general SET totalposts=?,totallikes=?,totalcomments=?,totalviews=?,lastupdated=? WHERE id=1";

		PreparedStatement query1 = null;
		try {
			query1 = cnlocal.prepareStatement(update);
			query1.setLong(1, totalposts);
			query1.setLong(2, totallikes);
			query1.setLong(3, totalcomments);
			query1.setLong(4, totalviews);
			query1.setDate(5, (Date) LastUpdated2);
			query1.executeUpdate();
		} catch (SQLException e1) {
			//
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
	 * Returns an Author Object based on id.
	 *
	 * @param id
	 *            User id to search for
	 * @return Object
	 */
	public Author getAuthor(int id) {
		return authordb.get(id);
	}

	/**
	 * Returns an Author Object based on Opinion.
	 *
	 * @param op
	 *            Opinion Object
	 * @return Object
	 */
	public Author getAuthor(Opinion op) {
		return authordb.get(op.getUID());
	}

	/**
	 * Returns an Author Object based on Post.
	 *
	 * @param comment
	 *            Post Object
	 * @return Object
	 */
	public Author getAuthor(Post comment) {
		return authordb.get(comment.getUID());
	}

	/**
	 * Gets the total comments.
	 *
	 * @return the total comments
	 */
	public long getTotalComments() {
		return this.totalcomments;
	}

	/**
	 * Gets the total views.
	 *
	 * @return the total views
	 */
	public long getTotalViews() {
		return this.totalviews;
	}

	/**
	 * Gets the total likes.
	 *
	 * @return the total likes
	 */
	public long getTotalLikes() {
		return this.totallikes;
	}

	/**
	 * Gets the total posts.
	 *
	 * @return the total posts
	 */
	public long getTotalPosts() {
		return this.totalposts;
	}

	// Runnables for multithreading

	/**
	 * The Class Tauthors.
	 */
	class Tauthors implements Runnable {
		private Author a;

		/**
		 * Instantiates a new tauthors.
		 *
		 * @param _a
		 *            the a
		 */
		public Tauthors(Author _a) {
			a = _a;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
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
				query1.setLong(1, a.getID());
				query1.setLong(2, a.getAge());
				query1.setString(3, a.getName());
				query1.setString(4, a.getGender());
				query1.setString(5, a.getLocation());
				query1.setDouble(6, a.getInfluence());
				query1.setLong(7, a.getComments());
				query1.setLong(8, a.getLikes());
				query1.setLong(9, a.getViews());
				query1.setLong(10, a.getPosts());
				query1.setDouble(11, a.getInfluence());
				query1.setLong(12, a.getComments());
				query1.setLong(13, a.getLikes());
				query1.setLong(14, a.getViews());
				query1.setLong(15, a.getPosts());
				// System.out.println(query1);
				query1.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			} finally {
				try {
					if (query1 != null)
						query1.close();
					if (cnlocal != null)
						cnlocal.close();
				} catch (Exception e) {
				}
			}

		}
	}

	/**
	 * The Class Topinions.
	 */
	class Topinions implements Runnable {
		private long id;
		private Connection condata;
		private Connection conlocal;
		private JSONObject obj = null;

		/**
		 * Instantiates a new topinions referenced to local data.
		 *
		 * @param _id
		 *            the id of the opinion
		 */
		public Topinions(long _id) {
			id = _id;

		}

		/**
		 * Instantiates a new topinions referenced to remote.
		 *
		 * @param _obj
		 *            the json object from remote
		 */
		public Topinions(JSONObject _obj) {
			obj = _obj;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (obj == null) {
				Statement stmt = null;
				ResultSet rs = null;
				try {
					condata = Settings.conndata();
					conlocal = Settings.connlocal();
					boolean remote = true;
					String query = ("Select * from " + Settings.rptable + " Where " + Settings.rptable_postid + " = "
							+ id);
					stmt = condata.createStatement();
					rs = stmt.executeQuery(query);
					if (!rs.next()) {
						query = ("Select * from " + Settings.lptable + " Where " + Settings.lptable_id + " = " + id);
						stmt = conlocal.createStatement();
						rs = stmt.executeQuery(query);
						remote = false;
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
					long postid = remote ? rs.getLong(Settings.rptable_postid) : rs.getLong(Settings.lptable_opinion);
					// System.out.println(id);
					long user_id = remote ? rs.getLong(Settings.rptable_userid) : rs.getLong(Settings.lptable_authorid);
					long time = 0;
					if (remote) {
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						java.util.Date date = null;
						try {
							date = df.parse(rs.getString(Settings.rptable_date));
						} catch (ParseException e) {
							System.out.print("Error Parsing Date from Local DB");
						}
						time = date.getTime();

					}
					long likes = remote ? rs.getLong(Settings.rptable_likes) : rs.getLong(Settings.lptable_likes);
					long views = remote ? rs.getLong(Settings.rptable_views) : rs.getLong(Settings.lptable_views);
					;
					String message = remote ? rs.getString(Settings.rptable_message)
							: rs.getString(Settings.lptable_message);
					long product = identifyProduct(message);
					if (product == 0) {
						rs.close();
						stmt.close();
						conlocal.close();
						condata.close();
						return;
					}
					Post _post = remote ? new Post(postid, user_id, time, likes, views, message)
							: new Post(postid, user_id, 0, likes, views, message);
					if (!(users.contains(user_id))) {
						users.add(user_id);
					}

					opiniondb.put(postid, new Opinion(_post, identifyPSSbyproduct(product), product));
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
				} finally {
					try {
						if (rs != null)
							rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (stmt != null)
							stmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (condata != null)
							condata.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (conlocal != null)
							conlocal.close();

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else {
				// Run this if importing from JSON
				Statement stmt = null;
				ResultSet rs = null;
				try {
					conlocal = Settings.connlocal();
					boolean remote = false;
					String query = ("Select * from " + Settings.lptable + " Where " + Settings.lptable_id + " = " + id);
					stmt = conlocal.createStatement();
					rs = stmt.executeQuery(query);
					if (!rs.next()) {
						remote = true;
						rs.close();
						stmt.close();
						conlocal.close();
					} else {
						totalposts--;
					}
					Date date = new Date(Long.valueOf(obj.getString("postEpoch")) * 1000L);
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					format.setTimeZone(TimeZone.getTimeZone("GMT"));
					String formatted = format.format(date);
					java.util.Date parsed = format.parse(formatted);

					System.out.println(formatted);
					long postid = obj.getLong("postId");
					// System.out.println(id);
					String source = obj.getString("source");
					String user_id = obj.getString("account");
					long time = parsed.getTime();
					if (!remote) {
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						date = null;
						try {
							date = (Date) df.parse(rs.getString(Settings.lptable_timestamp));
						} catch (ParseException e) {
							System.out.print("Error Parsing Date from Local DB");
						}
						time = date.getTime();

					}
					long likes = obj.has("mediaSpecificInfo") ? obj.has("likes") ? obj.getLong("likes") : 0 : 0;
					long views = obj.has("mediaSpecificInfo") ? obj.has("views") ? obj.getLong("views") : 0 : 0;
					String name = obj.has(Settings.JSON_fname) ? obj.getString(Settings.JSON_fname) + " " : "";
					name += obj.has(Settings.JSON_lname) ? obj.getString(Settings.JSON_lname) : "";
					long age = obj.has(Settings.JSON_age) ? obj.getLong(Settings.JSON_age) : 0;
					String gender = obj.has(Settings.JSON_gender) ? obj.getString(Settings.JSON_gender) : "";
					String location = obj.has(Settings.JSON_location) ? obj.getString(Settings.JSON_location) : "";
					String message = obj.getString("post");
					long product = identifyProduct(message);
					if (product == 0) {
						rs.close();
						stmt.close();
						conlocal.close();
						condata.close();
						return;
					}
					Post _post = new Post(postid, source, user_id, time, likes, views, message);// TODO
																								// create
																								// constructor
																								// for
																								// the
																								// different
																								// type
																								// of
																								// media
																								// specific
																								// info

					Author author = new Author(user_id, source, name, age, gender, location);
					if (!(users2.contains(author))) {
						users2.add(author);
					}
					opiniondb.put(postid, new Opinion(_post, identifyPSSbyproduct(product), product, "google.pt"));// TODO
					// find
					// url
					// to
					// attack
					// here
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (conlocal != null)
						conlocal.close();
				} catch (SQLException | ClassNotFoundException | JSONException e) {
					System.out.println("ERROR loading Opinions");
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (rs != null)
							rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (stmt != null)
							stmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (condata != null)
							condata.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (conlocal != null)
							conlocal.close();

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		}

	}

	/**
	 * The Class Tposts.
	 */
	class Tposts implements Runnable {
		private long id;
		private Opinion _opin;
		private Connection condata;
		private Connection conlocal;
		private JSONObject obj = null;

		/**
		 * Instantiates a new tposts referenced to local data.
		 *
		 * @param _id
		 *            the id of the opinion
		 */
		public Tposts(long _id) {
			id = _id;
			_opin = opiniondb.containsKey(id) ? opiniondb.get(id) : null;

		}

		/**
		 * Instantiates a new tposts referenced to remote data.
		 *
		 * @param _obj
		 *            the json object from remote
		 * @throws JSONException
		 *             the JSON exception
		 */
		public Tposts(JSONObject _obj) throws JSONException {
			obj = _obj;
			_opin = opiniondb.containsKey(_obj.getLong(Settings.JSON_postid))
					? opiniondb.get(_obj.getLong(Settings.JSON_postid)) : null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (_opin == null)
				return;
			Statement stmt = null;
			ResultSet rs = null;
			if (obj == null) {
				try {
					// System.out.println("HELLO1");
					condata = Settings.conndata();
					conlocal = Settings.connlocal();
					String query = ("Select * from " + Settings.rptable + " Where " + Settings.rptable_rpostid + " = "
							+ id);
					stmt = condata.createStatement();
					// System.out.println(query);
					rs = stmt.executeQuery(query);
					if (rs.next()) {
						do {
							// System.out.println("HELLO2");
							long postid = rs.getLong(Settings.rptable_postid);
							long user_id = rs.getLong(Settings.rptable_userid);
							long time = 0;

							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							java.util.Date date = null;
							try {
								date = df.parse(rs.getString(Settings.rptable_date));
							} catch (ParseException e) {
								System.out.print("Error Parsing Date from Local DB");
							}
							time = date.getTime();

							long likes = rs.getLong(Settings.rptable_likes);
							long views = rs.getLong(Settings.rptable_views);
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
							long postid = rs.getLong(Settings.lptable_id);
							long user_id = rs.getLong(Settings.lptable_authorid);
							long likes = rs.getLong(Settings.lptable_likes);
							long views = rs.getLong(Settings.lptable_views);
							String message = rs.getString(Settings.lptable_message);
							Post _post = new Post(postid, user_id, 0, likes, views, message);
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
					opiniondb.put(id, _opin);
				} catch (ClassNotFoundException e) {
					System.out.println("ERROR loading Posts");
					e.printStackTrace();
				} catch (SQLException e) {
					//
					e.printStackTrace();
				} finally {
					try {
						if (rs != null)
							rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (stmt != null)
							stmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (condata != null)
							condata.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (conlocal != null)
							conlocal.close();

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			} else {
				try {

					if (obj.has(Settings.JSON_replies)) {

						// System.out.println("HELLO2");
						JSONArray replies = obj.getJSONArray(Settings.JSON_replies);
						for (int index = 0; index < replies.length() - 1; index++) {

							JSONObject reply;

							reply = replies.getJSONObject(index);

							Date date = new Date(Long.valueOf(reply.getString(Settings.JSON_epoch)) * 1000L);
							// Date date = new Date(Long.valueOf(11111 *
							// 1000L));
							DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							format.setTimeZone(TimeZone.getTimeZone("GMT"));
							String formatted = format.format(date);
							java.util.Date parsed = format.parse(formatted);

							long postid = reply.getLong(Settings.JSON_postid);
							String user_id = reply.getString(Settings.JSON_userid);
							long time = parsed.getTime();
							long likes = reply.has("mediaSpecificInfo")
									? reply.has("likes") ? reply.getLong("likes") : 0 : 0;
							long views = reply.has("mediaSpecificInfo")
									? reply.has("views") ? reply.getLong("views") : 0 : 0;
							String message = reply.getString(Settings.JSON_message);
							String source = obj.getString(Settings.JSON_source);
							Post _post = new Post(postid, source, user_id, time, likes, views, message);
							String name = obj.has(Settings.JSON_fname) ? obj.getString(Settings.JSON_fname) + " " : "";
							name += obj.has(Settings.JSON_lname) ? obj.getString(Settings.JSON_lname) : "";
							long age = obj.has(Settings.JSON_age) ? obj.getLong(Settings.JSON_age) : 0;
							String gender = obj.has(Settings.JSON_gender) ? obj.getString(Settings.JSON_gender) : "";
							String location = obj.has(Settings.JSON_location) ? obj.getString(Settings.JSON_location)
									: "";
							Author author = new Author(user_id, source, name, age, gender, location);
							if (!(users2.contains(author))) {
								users2.add(author);
							}
							_opin.addcomment(_post);
						}

					}

					System.out.println("HELLO");
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (rs != null)
							rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (stmt != null)
							stmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (condata != null)
							condata.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (conlocal != null)
							conlocal.close();

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		}

		/**
		 * Tmodels Multithreading for model
		 */
		/*
		 * class Tmodels implements Runnable { private Model model; private
		 * Connection conlocal;
		 * 
		 * public Tmodels() { }
		 */
		public void Tmodels() {
			// System.out.println("HELLO1");
			Statement stmt = null;
			ResultSet rs = null;
			try {
				cnlocal = Settings.connlocal();
				String query = ("Select * from " + Settings.lmtable);
				stmt = cnlocal.createStatement();
				// System.out.println(query);
				rs = stmt.executeQuery(query);
				if (rs.next()) {
					do {
						Model model = new Model(rs.getLong(Settings.latable_id), rs.getLong(Settings.lmtable_update),
								rs.getLong(Settings.lmtable_creator), rs.getString(Settings.lmtable_name),
								rs.getString(Settings.lmtable_uri), rs.getLong(Settings.lmtable_pss),
								rs.getString(Settings.lmtable_age), rs.getString(Settings.lmtable_gender),
								rs.getString(Settings.lmtable_monitorfinal), rs.getBoolean(Settings.lmtable_archived),
								rs.getLong(Settings.lmtable_cdate), rs.getLong(Settings.lmtable_udate));
						Data.modeldb.put(model.getId(), model);
					} while (rs.next());
				}
				rs.close();
				stmt.close();
				cnlocal.close();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (stmt != null)
						stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (condata != null)
						condata.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (conlocal != null)
						conlocal.close();

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
}