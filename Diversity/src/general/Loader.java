package general;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import general.LoadThreads.*;

public class Loader {
	private static final Logger LOGGER = new Logging().create(Loader.class.getName());
	static Connection cndata = null;
	static Connection cnlocal = null;
	static Connection cncr = null;
	private long stime = 0;
	LoadThreads multiThread = new LoadThreads();
	static long totalposts;
	static long totalviews;
	static long totalcomments;
	static long totallikes;
	private Calendar lastUpdated = Calendar.getInstance();
	private Calendar lastUpdated2 = Calendar.getInstance();
	protected ConcurrentHashMap<Long, Author> authordb = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, Author> authordb2 = new ConcurrentHashMap<>();
	static ConcurrentHashMap<Long, Opinion> opiniondb = new ConcurrentHashMap<>();
	public static List<Long> users;
	public static List<Author> users2;

	public String load(JSONArray json) throws JSONException {
		loadp1(json);
		String done = loadp2();

		String err = insertauthors();
		if (err != null)
			return err;
		err = insertposts();
		if (err != null)
			return err;
		err = updatelocal();
		if (err != null)
			return err;
		return done;
	}

	private String loadp1(JSONArray json) throws JSONException {
		Server.isloading = true;
		users = new ArrayList<Long>();
		users2 = new ArrayList<Author>();
		totalposts = 0;
		totalviews = 0;
		totalcomments = 0;
		totallikes = 0;
		long stime = System.nanoTime();
		System.out.println(" Beginning " + stime);
		loadPSS();
		String err = loadroles();
		if (err != null)
			return err;
		err = loadGeneral();
		if (err != null)
			return err;
		err = loadmodels();
		if (err != null)
			return err;

		err = loaduniqueopinionid(json);

		if (err != null) {
			return err;
		}
		err = loadUsers(json);
		if (err != null)
			return err;

		update("opinions");

		err = insertauthors();
		if (err != null)
			return err;
		err = insertposts();
		if (err != null)
			return err;
		err = updatelocal();
		if (err != null)
			return err;

		return null;
	}

	private String loadp2() throws JSONException {
		long waiting_time = 0;
		for (Opinion op : opiniondb.values()) {
			waiting_time += op.newcomments();
		}
		if (waiting_time != 0) {
			try {
				do {
					Thread.sleep(30 * 1000/* waiting_time*10 */);
				} while (finishcalc());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ExecutorService es = Executors.newFixedThreadPool(50);
		for (Opinion op : opiniondb.values())
			es.execute(multiThread.new Topinions(op.getID()));
		es.shutdown();
		String err = awaittermination(es, "Opinions");
		if (err != null)
			return err;
		es = Executors.newFixedThreadPool(50);
		for (Opinion op : opiniondb.values())
			es.execute(multiThread.new Tposts(op.getID()));
		es.shutdown();

		err = awaittermination(es, "posts");
		if (err != null)
			return err;
		/*
		 * TODO PUT THIS OUTSIDE ON A TIMER WHILE null posts exist on database
		 */ evaluatedata();

		Server.isloading = false;
		return Backend.error_message("Loaded Successfully").toString();
	}

	private boolean finishcalc() {
		String query = "Select * from " + Settings.lptable + " Where " + Settings.lptable_polarity + " is null";
		boolean done = false;
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
		}

		try (Statement st = cnlocal.createStatement()) {
			ResultSet rs;
			rs = st.executeQuery(query);
			done = rs.next();
			rs.close();
		} catch (Exception e) {
			try {
				cnlocal.close();
			} catch (SQLException e1) {
				LOGGER.log(Level.INFO, Settings.err_unknown, e1);
			}
		}
		return done;
	}

	@SuppressWarnings("unused")
	private void loadlocal(JSONArray json) throws JSONException {
		// Load Data
		ExecutorService es = Executors.newFixedThreadPool(50);
		for (int id = 0; id < json.length(); id++)
			es.execute(multiThread.new Topinions(json.getJSONObject(id)));
		es.shutdown();
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			LOGGER.log(Level.FINE, "Error on Thread Opinions while loading data");
		}
		es = Executors.newFixedThreadPool(50);
		for (int id = 0; id < json.length(); id++)
			es.execute(multiThread.new Tposts(json.getJSONObject(id)));
		es.shutdown();
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			LOGGER.log(Level.FINE, "Error on Thread Posts while loading data");
		}
		LOGGER.log(Level.INFO, " Load posts from remote " + (System.nanoTime() - stime));
		stime = System.nanoTime();

		// Fetch local DB models

		try {
			Connection cnlocal = Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect);
		}
		String select = Settings.sqlselectall + Settings.lmtable;
		try (Statement stmt = cnlocal.createStatement()) {
			try (ResultSet rs = stmt.executeQuery(select)) {

				if (rs.next()) {
					rs.beforeFirst();
					for (; rs.next();) {
						Model model = new Model(rs.getLong(Settings.lmtable_id), rs.getLong(Settings.lmtable_update),
								rs.getLong(Settings.lmtable_creator), rs.getString(Settings.lmtable_name),
								rs.getString(Settings.lmtable_uri), rs.getLong(Settings.lmtable_pss),
								rs.getString(Settings.lmtable_age), rs.getString(Settings.lmtable_gender),
								rs.getString(Settings.lmtable_monitorfinal), rs.getBoolean(Settings.lmtable_archived),
								rs.getLong(Settings.lmtable_cdate), rs.getLong(Settings.lmtable_udate),
								rs.getLong(Settings.lmtable_designproject),
								rs.getBoolean(Settings.lmtable_add_mediawiki));
						Data.modeldb.put(model.getId(), model);

					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
		}

		// Fetch local DB for users

		String querycond = "";
		for (Author user : users2) {
			if (user == null)
				continue;
			System.out.println("\n DEBUG IF HAPPENS USERID=" + user.getUID());
			System.out.println(" RESULT STRING " + querycond);
			querycond += user.getUID() + ",";
		}
		// System.out.println(querycond);

		// Load users from local DB
		if (users2.size() != 0) {
			select = (Settings.sqlselectall + Settings.latable + Settings.sqlwhere + Settings.latable_id + " in (");

			for (int i = 0; i < users2.size() - 1; i++)
				select += "?,";
			select += "?)";

			// System.out.println(query);
			try (PreparedStatement stmt2 = cnlocal.prepareStatement(select)) {
				for (int i = 0; i < users2.size(); i++) {
					stmt2.setString(i + 1, querycond.split(",")[i]);
				}
				try (ResultSet rs = stmt2.executeQuery()) {
					// System.out.println(stmt2);
					while (rs.next()) {
						if (authordb2.containsKey(
								rs.getString(Settings.latable_id) + rs.getString(Settings.latable_source))) {
						} else {
							Author auth = new Author(rs.getString(Settings.latable_id),
									rs.getString(Settings.latable_source), rs.getString(Settings.latable_name),
									rs.getLong(Settings.latable_age), rs.getString(Settings.latable_gender),
									rs.getString(Settings.latable_location));
							auth.setComments(rs.getLong(Settings.latable_comments));
							auth.setLikes(rs.getLong(Settings.latable_likes));
							auth.setPosts(rs.getLong(Settings.latable_posts) - 1);
							auth.setViews(rs.getLong(Settings.latable_views));
							authordb2.put(
									rs.getString(Settings.latable_id) + "," + rs.getString(Settings.latable_source),
									auth);
						}
					}
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
				try {
					cnlocal.close();
				} catch (SQLException e1) {
					LOGGER.log(Level.INFO, Settings.err_unknown, e1);
				}
			}
			LOGGER.log(Level.INFO, " Load users local " + (System.nanoTime() - stime));
			stime = System.nanoTime();
		}
	}

	private void loadPSS() {

		String select = Settings.sqlselectall + Settings.crpsstable;
		try {
			cncr = Settings.conncr();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_cr, e);
			return;
		}

		try (PreparedStatement query = cncr.prepareStatement(select)) {
			try (ResultSet rs = query.executeQuery()) {
				while (rs.next()) {
					Data.pssdb.put(rs.getLong(Settings.crpsstable_id),
							new PSS(rs.getLong(Settings.crpsstable_id), rs.getLong(Settings.crpsstable_company),
									rs.getString(Settings.crpsstable_name), rs.getLong(Settings.crpsstable_author),
									rs.getString(Settings.crpsstable_type)));
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
			try {
				cncr.close();
			} catch (Exception e1) {
				LOGGER.log(Level.FINEST, Settings.err_unknown, e1);
			}
			return;
		}

		select = Settings.sqlselectall + Settings.crproducttable + " ORDER BY " + Settings.crproducttable_id + " ASC";
		try (PreparedStatement query = cncr.prepareStatement(select)) {
			try (ResultSet rs = query.executeQuery()) {

				while (rs.next()) {
					Data.addproduct(rs);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
			try {
				cncr.close();
			} catch (Exception e1) {
				LOGGER.log(Level.FINEST, "Nothing can be done here", e);
			}
			return;
		}

		select = Settings.sqlselectall + Settings.crcompanytable;
		try (PreparedStatement query = cncr.prepareStatement(select)) {
			try (ResultSet rs = query.executeQuery()) {

				while (rs.next()) {
					Data.companydb.put(rs.getLong(Settings.crcompanytable_id),
							new Company(rs.getLong(Settings.crcompanytable_id),
									rs.getString(Settings.crcompanytable_name),
									rs.getString(Settings.crcompanytable_type),
									rs.getLong(Settings.crcompanytable_belongs_to)));
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
			try {
				cncr.close();
			} catch (Exception e1) {
				LOGGER.log(Level.FINEST, "Nothing can be done here", e);
			}
			return;
		}
		select = Settings.sqlselectall + Settings.crpssproducttable;

		try (PreparedStatement query = cncr.prepareStatement(select)) {
			try (ResultSet rs = query.executeQuery()) {

				while (rs.next()) {
					Data.pssdb.get(rs.getLong(Settings.crrpssproducttable_pss))
							.add_product(rs.getLong(Settings.crrpssproducttable_product));

				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
			try {
				cncr.close();
			} catch (Exception e1) {
				LOGGER.log(Level.FINEST, "Nothing can be done here", e1);
			}
			return;
		}

		try {
			cncr.close();
		} catch (Exception e) {
			LOGGER.log(Level.FINEST, "Nothing can be done here", e);
		}
	}

	private String loadGeneral() throws JSONException {

		String select = Settings.sqlselectall + " " + Settings.gentable + " WHERE " + Settings.gentable_id + "=1";

		Connection cnlocal = null;
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}

		try (Statement stmt = cnlocal.createStatement()) {
			try (ResultSet rs = stmt.executeQuery(select)) {
				rs.next();
				totalviews = rs.getLong("totalviews");
				totalcomments = rs.getLong("totalcomments");
				totallikes = rs.getLong("totallikes");
				totalposts = rs.getLong("totalposts");
				lastUpdated.setTime(rs.getDate("lastupdated"));
				if (rs.getLong("Version") != Settings.dbversion)
					rs.getLong("asdasasd");
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"Error (1): Local Database Error\r\n Please Update to latest version " + Settings.dbversion, e);
			try {
				cnlocal.close();
			} catch (SQLException e1) {
				LOGGER.log(Level.INFO, Settings.err_unknown, e1);
			}
			return Backend
					.error_message(
							"Error (1): Local Database Error\r\n Please Update to latest version " + Settings.dbversion)
					.toString();

		} finally {
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, Settings.err_unknown, e);
			}
		}
		LOGGER.log(Level.INFO, " Variable Init " + (System.nanoTime() - stime));
		stime = System.nanoTime();
		return null;
	}

	private String loadmodels() throws JSONException {
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}
		String query = Settings.sqlselectall + Settings.lmtable;
		try (Statement stmt = cnlocal.createStatement()) {
			// System.out.println(query);
			try (ResultSet rs = stmt.executeQuery(query)) {

				for (; rs.next();) {
					Model model = new Model(rs.getLong(Settings.lmtable_id), rs.getLong(Settings.lmtable_update),
							rs.getLong(Settings.lmtable_creator), rs.getString(Settings.lmtable_name),
							rs.getString(Settings.lmtable_uri), rs.getLong(Settings.lmtable_pss),
							rs.getString(Settings.lmtable_age), rs.getString(Settings.lmtable_gender),
							rs.getString(Settings.lmtable_monitorfinal), rs.getBoolean(Settings.lmtable_archived),
							rs.getLong(Settings.lmtable_cdate), rs.getLong(Settings.lmtable_udate),
							rs.getLong(Settings.lmtable_designproject), rs.getBoolean(Settings.lmtable_add_mediawiki));
					Data.modeldb.put(model.getId(), model);

				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error on SQL Input", e);
			return Backend.error_message(Settings.err_dbconnect).toString();
		} finally {
			try {
				cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
		}
		LOGGER.log(Level.INFO, " Model Loading: " + (System.nanoTime() - stime));
		stime = System.nanoTime();
		return null;
	}

	private String loaduniqueopinionid(JSONArray json) throws JSONException {
		String err;
		try {
			cndata = Settings.conndata();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}
		// Load Opinions id first
		if (json == null)
			return loaduopid();

		ExecutorService es = Executors.newFixedThreadPool(50);
		for (int i = 0; i < json.length(); i++)
			es.execute(multiThread.new Topinions(json.getJSONObject(i)));
		es.shutdown();
		err = awaittermination(es, "Opinions");
		if (err != null)
			return err;
		es = Executors.newFixedThreadPool(50);
		for (int i = 0; i < json.length(); i++)
			es.execute(multiThread.new Tposts(json.getJSONObject(i)));
		es.shutdown();

		err = awaittermination(es, "posts");
		if (err != null)
			return err;

		LOGGER.log(Level.INFO, " Load posts from remote " + (System.nanoTime() - stime));
		stime = System.nanoTime();
		return null;
	}

	private String loadUsers(JSONArray json) throws JSONException {
		String err;
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}

		String querycond = users.toString();
		querycond = querycond.replaceAll("\\[", "(").replaceAll("\\]", "\\)");
		// From local DB
		err = loadlocalusers(querycond);
		if (err != null)
			return err;
		if (json == null) {
			// Load users from simulation DB
			err = loadsimauthors(querycond);
			if (err != null)
				return err;
		} else {
			// Load users from JSON
			for (int i = 1; i < json.length(); i++) {
				JSONObject obj = json.getJSONObject(i);
				if (authordb2.containsKey(obj.getString(Settings.JSON_source) + obj.getString(Settings.latable_source)))
					continue;
				Author auth = new Author(obj.getString(Settings.JSON_source), obj.getString(Settings.latable_source),
						obj.getString(Settings.latable_name), obj.getLong(Settings.latable_age),
						obj.getString(Settings.latable_gender), obj.getString(Settings.latable_location));
				auth.setComments(obj.getLong(Settings.latable_comments));
				auth.setLikes(obj.getLong(Settings.latable_likes));
				auth.setPosts(obj.getLong(Settings.latable_posts) - 1);
				auth.setViews(obj.getLong(Settings.latable_views));
				authordb2.put(obj.getString(Settings.latable_id) + "," + obj.getString(Settings.latable_source), auth);
			}

			for (int i = 0; i < json.length(); i++) {
				JSONObject obj = json.getJSONObject(i);
				if (authordb.containsKey(obj.getLong("id")))
					continue;
				authordb.put(obj.getLong("id"), new Author(obj.getLong("id"), obj.getString("name"), obj.getLong("age"),
						obj.getString("gender"), obj.getString("location")));

			}

		}

		LOGGER.log(Level.INFO, " Load users remote " + (System.nanoTime() - stime));
		stime = System.nanoTime();
		return null;

	}

	private String loadroles() throws JSONException {

		String query;

		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}
		query = "SELECT * from " + Settings.lartable;
		try (PreparedStatement stmt = cnlocal.prepareStatement(query)) {
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String name = rs.getString(Settings.lartable_name);
					String description = rs.getString(Settings.lartable_description);
					Boolean perm0 = rs.getBoolean(Settings.lartable_vom);
					Boolean perm1 = rs.getBoolean(Settings.lartable_create_edit_delete_model);
					Boolean perm2 = rs.getBoolean(Settings.lartable_view_opinion_results);
					Boolean perm3 = rs.getBoolean(Settings.lartable_save_delete_snapshots);
					Boolean perm4 = rs.getBoolean(Settings.lartable_use_opinion_prediction);
					Boolean perm5 = rs.getBoolean(Settings.lartable_admin);
					Data.roledb.put(name, new Role(name, description, perm0, perm1, perm2, perm3, perm4, perm5));
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR LOADING ROLES", e);
		}

		return null;

	}

	private String loadlocalusers(String querycond) throws JSONException {
		String query = Settings.sqlselectall + Settings.latable + Settings.sqlwhere + Settings.latable_id + " in "
				+ querycond;
		try (Statement stmt = cnlocal.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				if (authordb.containsKey(rs.getLong("id")))
					continue;
				Author auth = new Author(rs.getLong(Settings.latable_id), rs.getString(Settings.latable_name),
						rs.getLong(Settings.latable_age), rs.getString(Settings.latable_gender),
						rs.getString(Settings.latable_location));
				auth.setComments(rs.getLong(Settings.latable_comments));
				auth.setLikes(rs.getLong(Settings.latable_likes));
				auth.setPosts(rs.getLong(Settings.latable_posts) - 1);
				auth.setViews(rs.getLong(Settings.latable_views));
				authordb.put(rs.getLong(Settings.latable_id), auth);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
			return Backend.error_message(Settings.err_unknown).toString();
		} finally {
			try {
				cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, Settings.err_unknown, e);
			}
		}
		LOGGER.log(Level.INFO, " Load users local " + (System.nanoTime() - stime));
		stime = System.nanoTime();
		return null;
	}

	private void update(String type) {
		if ("opinions".equals(type)) {
			opiniondb.forEach((k, v) -> {
				List<Long> uniqueauthors = new ArrayList<>();
				HashMap<Long, Post> temppost = v.getPosts();
				temppost.forEach((k2, v2) -> {
					if (!uniqueauthors.contains(v2.getUID()))
						uniqueauthors.add(v2.getUID());
				});
				uniqueauthors.forEach((v3) -> {
					Author tempauthor = authordb.get(v3);
					tempauthor.addComments(v.newcomments());
					tempauthor.addLikes(v.newlikes());
					tempauthor.addViews(v.newviews());
					tempauthor.addPosts();
					authordb.put(tempauthor.getID(), tempauthor);
				});

				totalcomments += v.newcomments();
				totallikes += v.newlikes();
				totalviews += v.newviews();
			});
			LOGGER.log(Level.INFO, " update opinions " + (System.nanoTime() - stime));
			stime = System.nanoTime();
			return;
		}
	}

	private String loadsimauthors(String querycond) throws JSONException {
		String query = (Settings.sqlselectall + Settings.rutable + " where " + Settings.rutable_userid + " in "
				+ querycond);
		try {
			cndata = Settings.conndata();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}
		try (Statement stmt = cndata.createStatement()) {
			try (ResultSet rs = stmt.executeQuery(query)) {
				while (rs.next()) {
					if (authordb.containsKey(rs.getLong(Settings.rutable_userid))) {
					} else {
						authordb.put(rs.getLong(Settings.rutable_userid),
								new Author(rs.getLong(Settings.rutable_userid), rs.getString(Settings.rutable_name),
										rs.getLong(Settings.rutable_age), rs.getString(Settings.rutable_gender),
										rs.getString(Settings.rutable_loc)));
					}
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error Accessing Remote Databse Please Check If Populated");
			return Backend.error_message("Error (2): Remote Database Error\r\n Please check if populated").toString();
		} finally {
			try {
				cndata.close();
			} catch (SQLException e) {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
			}
		}
		return null;
	}

	private void evaluatedata() {
		authordb.forEach((k, v) -> {
			v.calcInfluence((totalcomments / ((double) totalposts)), totallikes / ((double) totalposts),
					totalviews / ((double) totalposts));
		});
		opiniondb.forEach((k, v) -> {
			v.evalReach(totalcomments / ((double) totalposts), totallikes / ((double) totalposts),
					totalviews / ((double) totalposts));
			v.evalPolarity(authordb);
		});
		LOGGER.log(Level.INFO, " calc eval and reach " + (System.nanoTime() - stime));
		stime = System.nanoTime();
	}

	private String insertauthors() throws JSONException {
		try {
			cnlocal = Settings.connlocal();
			cnlocal.setAutoCommit(false);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}
		for (Author author : authordb.values()) {
			String insert = "INSERT INTO " + Settings.latable + " "
					+ "Values (?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.latable_influence + "=?,"
					+ Settings.latable_comments + "=?," + Settings.latable_likes + "=?," + Settings.latable_views
					+ "=?," + Settings.latable_posts + "=?";
			try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
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
				query1.setString(11, author.getSource() != null ? author.getSource() : "Not Available");
				query1.setDouble(12, author.getInfluence());
				query1.setLong(13, author.getComments());
				query1.setLong(14, author.getLikes());
				query1.setLong(15, author.getViews());
				query1.setLong(16, author.getPosts());
				query1.executeUpdate();
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Error Inserting Author into Database");
				try {
					cnlocal.rollback();
					cnlocal.close();
				} catch (Exception e1) {
					LOGGER.log(Level.INFO, "Nothing can be done here", e1);
				}
				return Backend.error_message("Error Inserting Author into Database").toString();
			}
		}
		try {
			cnlocal.commit();
			cnlocal.close();
		} catch (Exception e) {
			try {
				LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
				cnlocal.rollback();
			} catch (SQLException e1) {
				LOGGER.log(Level.FINE, Settings.err_unknown, e1);
			}
			LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
		}

		LOGGER.log(Level.INFO, " insert " + Settings.latable + " " + (System.nanoTime() - stime));
		stime = System.nanoTime();
		return null;
	}

	private String insertposts() throws JSONException {
		ExecutorService es = Executors.newFixedThreadPool(50);

		try {
			cnlocal = Settings.connlocal();
			cnlocal.setAutoCommit(false);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}

		for (Opinion opinion : opiniondb.values()) {
			es.execute(new Runnable() {
				@Override
				public void run() {
					String update = "INSERT INTO " + Settings.lotable + " "
							+ "Values (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.lotable_reach + "=?,"
							+ Settings.lotable_polarity + "=?," + Settings.lotable_influence + "=?,"
							+ Settings.lotable_comments + "=?";
					try (PreparedStatement query1 = cnlocal.prepareStatement(update)) {
						query1.setLong(1, opinion.getID());
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

						for (Post post : opinion.getPosts().values()) {
							PreparedStatement query2 = null;
							try {
								String update1 = "REPLACE INTO " + Settings.lptable + " " + "Values (?,?,?,?,?,?,?)";
								query2 = cnlocal.prepareStatement(update1);
								query2.setLong(1, post.getID());
								if (post.getPolarity() != -1) {
									query2.setDouble(2, post.getPolarity());
								} else {
									query2.setNull(2, java.sql.Types.DOUBLE);
								}
								query2.setString(3, post.getComment());
								query2.setLong(4, post.getLikes());
								query2.setLong(5, post.getViews());
								query2.setLong(6, opinion.getID());
								query2.setLong(7, post.getUID());

								query2.executeUpdate();
								if (query2 != null)
									query2.close();
							} catch (Exception e) {
								LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
							} finally {
								try {
									if (query2 != null)
										query2.close();
								} catch (Exception e) {
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

		}
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

		LOGGER.log(Level.INFO, " insert opinions and posts " + (System.nanoTime() - stime));
		stime = System.nanoTime();
		return null;
	}

	private String updatelocal() throws JSONException {
		lastUpdated2 = Calendar.getInstance();
		lastUpdated2.add(Calendar.DATE, -1);

		String update = "UPDATE " + Settings.gentable + " SET " + Settings.gentable_totalposts + "=?,"
				+ Settings.gentable_totallikes + "=?," + Settings.gentable_totalcomments + "=?,"
				+ Settings.gentable_totalviews + "=?," + Settings.gentable_lastupdated + "=? WHERE "
				+ Settings.gentable_id + "=1";
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
			return Backend.error_message(Settings.err_dbconnect).toString();

		}
		totalposts += opiniondb.size();
		try (PreparedStatement query1 = cnlocal.prepareStatement(update)) {
			query1.setLong(1, totalposts);
			query1.setLong(2, totallikes);
			query1.setLong(3, totalcomments);
			query1.setLong(4, totalviews);
			query1.setDate(5, new java.sql.Date(lastUpdated2.getTimeInMillis()));
			query1.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
			return Backend.error_message(Settings.err_unknown).toString();
		}
		try {
			if (cnlocal != null)
				cnlocal.close();
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Nothing can de done here", e);
		}
		LOGGER.log(Level.INFO, " update " + Settings.gentable + " " + (System.nanoTime() - stime));
		stime = System.nanoTime();
		return null;
	}

	private String loaduopid() throws JSONException {
		String err;
		String query = "Select distinct case \r\n when " + Settings.rptable_rpostid + " is null then "
				+ Settings.rptable_postid + "\r\n when " + Settings.rptable_rpostid + " is not null then "
				+ Settings.rptable_rpostid + " end from " + Settings.rptable + Settings.sqlwhere + Settings.ptime
				+ " > \'" + new java.sql.Date(lastUpdated.getTimeInMillis()) + "\' && " + Settings.ptime + " <= \'"
				+ new java.sql.Date(lastUpdated2.getTimeInMillis()) + "\' ORDER BY ID ASC";

		Connection cndata = null;
		Connection cnlocal = null;
		try {
			cnlocal = Settings.connlocal();
			cndata = Settings.conndata();
		} catch (Exception e1) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e1);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}

		boolean error = false;
		
			try (Statement stmt = cndata.createStatement()) {
				while (true) {
				try (ResultSet rs = stmt.executeQuery(query)) {
					if (!rs.next()) {
						cndata.close();
						cnlocal.close();
						return Backend.error_message("Loaded Successfully").toString();
					}
					rs.beforeFirst();
					ExecutorService es = Executors.newFixedThreadPool(50);
					while (rs.next())
						es.execute(multiThread.new Topinions(rs.getLong(1)));
					es.shutdown();
					err = awaittermination(es, "Opinions");
					if (err != null)
						return err;
					rs.beforeFirst();
					es = Executors.newFixedThreadPool(50);
					while (rs.next())
						es.execute(multiThread.new Tposts(rs.getLong(1)));
					es.shutdown();

					err = awaittermination(es, "posts");
					if (err != null)
						return err;

				}catch (Exception e) {
					LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
					query = "Select id from sentimentposts.post where id in (" + query;
					query = query.replace("ORDER BY ID ASC", ") order by id asc");
					if (error)
						return Backend.error_message("Error Loading opinions ids").toString();
					error = true;
					continue;
				}
				rs.beforeFirst();
				ExecutorService es = Executors.newFixedThreadPool(50);
				while (rs.next())
					es.execute(multiThread.new Topinions(rs.getLong(1)));
				es.shutdown();
				err = awaittermination(es, "Opinions");
				if (err != null)
					return err;
				rs.beforeFirst();
				es = Executors.newFixedThreadPool(50);
				while (rs.next())
					es.execute(multiThread.new Tposts(rs.getLong(1)));
				es.shutdown();

				err = awaittermination(es, "posts");
				if (err != null)
					return err;

			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
			return Backend.error_message("Error Loading opinions ids").toString();
		}

		finally

		{
			try {
				cndata.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, Settings.err_unknown, e);
			}
		}
		return null;
	}

	private String awaittermination(ExecutorService es, String thread) throws JSONException {
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error on Thread that Loads" + thread, e);
			es.shutdownNow();
			return Backend.error_message("Error on Thread that Loads " + thread).toString();
		}
		return null;
	}
}
