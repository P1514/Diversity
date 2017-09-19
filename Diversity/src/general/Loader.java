package general;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
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
	private long stime = 0;
	private long stoptime = 0;
	private long starttime = 0;
	private long pausetime = 0;
	private long new_posts = 0;
	LoadThreads multiThread = new LoadThreads();
	static long totalposts;
	static long totalviews;
	static long totalcomments;
	static long totallikes;
	private Calendar lastUpdated = Calendar.getInstance();
	private Calendar lastUpdated2 = Calendar.getInstance();
	// protected ConcurrentHashMap<Long, Author> authordb = new
	// ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, Author> authordb2 = new ConcurrentHashMap<>();
	static ConcurrentHashMap<Long, Opinion> opiniondb = new ConcurrentHashMap<>();
	public static List<String> users;
	public static List<Author> users2;
	public static boolean first_load = true;

	public String load(JSONArray json) throws JSONException {
		//TODO protect for empty json
		//System.out.println("json: " + json.length());
		starttime = System.nanoTime();
		//if(json.length()<1) return "";
		loadp1(json);
		first_load = false;
		String err = updatelocal();
		if (err != null)
			return err;
		String done = loadp2();
		err = insertauthors();
		if (err != null)
			return err;
		err = insertposts();
		if (err != null)
			return err;
		loadtimescalc();
		first_load = true;
		return done;

	}

	public String loadinit() throws JSONException{
		Server.isloading = true;
		users = new ArrayList<String>();
		users2 = new ArrayList<Author>();
		totalposts = 0;
		totalviews = 0;
		totalcomments = 0;
		totallikes = 0;
		// long stime = System.nanoTime();
		// //system.out.println(" Beginning " + stime);
		loadPSS();
		loadDesignProjects();
		loadUsers();
		String err = loadroles();
		if (err != null)
			return err;
		err = loadGeneral();
		if (err != null)
			return err;
		err = loadmodels();
		if (err != null)
			return err;
		Server.isloading=false;
		return null;
	}
	private String loadp1(JSONArray json) throws JSONException {
		Server.isloading = true;
		users = new ArrayList<String>();
		users2 = new ArrayList<Author>();
		totalposts = 0;
		totalviews = 0;
		totalcomments = 0;
		totallikes = 0;
		// long stime = System.nanoTime();
		// //system.out.println(" Beginning " + stime);
		loadPSS();
		loadDesignProjects();
		loadUsers();
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
		return null;
	}

	private void loadtimescalc() {
		stoptime = System.nanoTime();
		long runtime = stoptime - starttime - pausetime;
		double ns_post = ((double) runtime) / new_posts;
		LOGGER.log(Level.SEVERE, "Took:" + runtime + " ns to finish processing " + new_posts + " posts which averages "
				+ ns_post + " ns/post");
	}

	private String loadp2() throws JSONException {

		pausetime = System.nanoTime();
		LOGGER.log(Level.INFO, "Started Waiting to Check NULLS");
		for (Opinion op : opiniondb.values()) {
			new_posts += op.newcomments();
		}
		if (new_posts != 0) {
			try {
				do {
					Thread.sleep(1000/* 30 * 1000waiting_time*10 */);
				} while (finishcalc());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		pausetime = System.nanoTime() - pausetime;
		ExecutorService es = Executors.newFixedThreadPool(10);
		for (Opinion op : opiniondb.values())
			es.execute(multiThread.new Topinions(op.getID()));
		es.shutdown();
		String err = awaittermination(es, "Opinions");
		if (err != null)
			return err;
		es = Executors.newFixedThreadPool(10);
		for (Opinion op : opiniondb.values())
			es.execute(multiThread.new Tposts(op.getID()));
		es.shutdown();

		err = awaittermination(es, "posts");
		if (err != null)
			return err;

		evaluatedata();
		//System.out.println("1");

		Server.isloading = false;
		return Backend.error_message("Loaded Successfully").toString();
	}

	private boolean finishcalc() {
		String query = "Select * from " + Settings.lptable + " Where " + Settings.lptable_polarity + " is null";
		boolean done = false;
		Connection cnlocal = null;
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
		try {
			cnlocal.close();
		} catch (SQLException e1) {
			LOGGER.log(Level.INFO, Settings.err_unknown, e1);
		}
		return done;
	}

	@SuppressWarnings("unused")
	private void loadlocal(JSONArray json) throws JSONException {
		// Load Data
		ExecutorService es = Executors.newFixedThreadPool(10);
		for (int id = 0; id < json.length(); id++)
			es.execute(multiThread.new Topinions(json.getJSONObject(id)));
		es.shutdown();
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			LOGGER.log(Level.FINE, "Error on Thread Opinions while loading data");
		}
		es = Executors.newFixedThreadPool(10);
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
		Connection cnlocal = null;
		try {
			cnlocal = Settings.connlocal();
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
			// //system.out.println("\n DEBUG IF HAPPENS USERID=" +
			// user.getUID());
			// //system.out.println(" RESULT STRING " + querycond);
			querycond += user.getID() + ",";
		}
		// //system.out.println(querycond);

		// Load users from local DB
		if (users2.size() != 0) {
			select = (Settings.sqlselectall + Settings.latable + Settings.sqlwhere + Settings.latable_id + " in (");

			for (int i = 0; i < users2.size() - 1; i++)
				select += "?,";
			select += "?)";

			try (PreparedStatement stmt2 = cnlocal.prepareStatement(select)) {
				for (int i = 0; i < users2.size(); i++) {
					stmt2.setString(i + 1, querycond.split(",")[i]);
				}
				try (ResultSet rs = stmt2.executeQuery()) {
					// //system.out.println(stmt2);
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
		Connection cncr = null;
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

		if (Settings.include_services) {
			select = Settings.sqlselectall + Settings.crservicetable + " ORDER BY " + Settings.crservicetable_id
					+ " ASC";
			try (PreparedStatement query = cncr.prepareStatement(select)) {
				try (ResultSet rs = query.executeQuery()) {

					while (rs.next()) {
						Data.addservice(rs);
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
		select = Settings.sqlselectall + Settings.crdbname+"."+Settings.cictable;
		//System.out.println(select);

		try (PreparedStatement query = cncr.prepareStatement(select)) {
			try (ResultSet rs = query.executeQuery()) {

				while (rs.next()) {
					//System.out.println(Data.companydb.get(rs.getLong(Settings.cictable_company_id)).getName());
					
					Data.companydb.get(rs.getLong(Settings.cictable_company_id))
							.add_design_project(rs.getLong(Settings.cictable_design_project_id));
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
		select = Settings.sqlselectall + Settings.crpssservicetable;

		try (PreparedStatement query = cncr.prepareStatement(select)) {
			try (ResultSet rs = query.executeQuery()) {

				while (rs.next()) {
					Data.pssdb.get(rs.getLong(Settings.crrpssservicetable_pss))
							.add_service(rs.getLong(Settings.crrpssservicetable_service));
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

	private void loadDesignProjects() {

		String select = Settings.sqlselectall + Settings.crdptable;
		Connection cncr = null;
		try {
			cncr = Settings.conncr();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_cr, e);
			return;
		}

		try (PreparedStatement query = cncr.prepareStatement(select)) {
			try (ResultSet rs = query.executeQuery()) {
				while (rs.next()) {
					Data.designProjectdb.put(rs.getLong(Settings.crdptable_id), new DesignProject(
							rs.getLong(Settings.crdptable_id), rs.getLong(Settings.crdptable_produces_pss_id),
							rs.getString(Settings.crdptable_name), rs.getLong(Settings.crdptable_author),
							rs.getLong(Settings.crdptable_time_created), rs.getLong(Settings.crdptable_wiki_id)));
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

		select = Settings.sqlselectall + Settings.crdpuserstable;

		try (PreparedStatement query = cncr.prepareStatement(select)) {
			try (ResultSet rs = query.executeQuery()) {

				while (rs.next()) {
					Data.designProjectdb.get(rs.getLong(Settings.crdpuserstable_design_project_id))
							.add_team_member_user(rs.getLong(Settings.crdpuserstable_user_id));
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

	private void loadUsers() {

		String select = Settings.sqlselectall + Settings.crdbname + "." + Settings.crusertable + " INNER JOIN  "
				+ Settings.crdbname + "." + Settings.cruserrtable + " ON " + Settings.crusertable + "."
				+ Settings.crusertable_user_role_id + "=" + Settings.cruserrtable + "." + Settings.cruserrtable_id
				+ ";";
		//system.out.println(select);
		Connection cncr = null;
		try {
			cncr = Settings.conncr();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_cr, e);
			return;
		}

		try (PreparedStatement query = cncr.prepareStatement(select)) {
			try (ResultSet rs = query.executeQuery()) {
				while (rs.next()) {
					Data.userdb.put(rs.getLong(Settings.crusertable_id), new User(rs.getLong(Settings.crusertable_id),
							rs.getString(Settings.crusertable_username), rs.getString(Settings.crusertable_password),
							rs.getString(Settings.crusertable_email), rs.getString(Settings.crusertable_first_name),
							rs.getString(Settings.crusertable_last_name), rs.getString(Settings.cruserrtable_name),
							rs.getLong(Settings.crusertable_company_id)));
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
		LOGGER.log(Level.INFO, " Version: " + Settings.version);
		stime = System.nanoTime();
		return null;
	}

	private String loadmodels() throws JSONException {
		Connection cnlocal = null;
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}
		String query = Settings.sqlselectall + Settings.lmtable;
		try (Statement stmt = cnlocal.createStatement()) {
			// //system.out.println(query);
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
		// Load Opinions id first
		if (json == null)
			return loaduopid();

		ExecutorService es = Executors.newFixedThreadPool(10);
		for (int i = 0; i < json.length(); i++)
			es.execute(multiThread.new Topinions(json.getJSONObject(i)));
		es.shutdown();
		err = awaittermination(es, "Opinions");
		LOGGER.log(Level.INFO, " Load Opinions from remote " + (System.nanoTime() - stime));
		if (err != null)
			return err;
		es = Executors.newFixedThreadPool(10);
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

		String querycond = "(";
		for (Author a : users2) {
			if(a==null) continue;
			a.getID();
			querycond += a.getID() + ",";
		}
		if (querycond.length() > 2)
			querycond = querycond.substring(0, querycond.length() - 2);
		
		else {
			for (String a : users) {
				querycond += a + ",";
			}
			if (querycond.length() > 1)
			querycond = querycond.substring(0, querycond.length() - 1);
			else
				querycond += "-1";
		}
		querycond += ");";
		//system.out.println(querycond);
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
			Author auth;
			for (int i = 0; i < json.length(); i++) {
				
				JSONObject obj = json.getJSONObject(i);
				//system.out.println(obj.toString());
				JSONArray obj1 = obj.getJSONArray(Settings.JSON_replies);
				String user1 = obj.getString(Settings.JSON_userid);
				String source1=obj.getString(Settings.JSON_source);
				
				for (int j = 0;j < obj1.length(); j++) {//TO LOAD REPLIES
					obj=obj1.getJSONObject(j);
					user1 = obj.getString(Settings.JSON_userid);
					if (authordb2.containsKey(user1 +","+ source1))
						continue;
					auth = new Author(obj.getString(Settings.JSON_userid), source1,
							(obj.has(Settings.JSON_fname) ? obj.getString(Settings.JSON_fname) : "")
							+ (obj.has(Settings.JSON_lname) ? obj.getString(Settings.JSON_lname) : ""), 0, (obj.has(Settings.JSON_gender) ? obj.getString(Settings.JSON_gender) : "Unknown"),
							(obj.has(Settings.JSON_location) ? obj.getString(Settings.JSON_location) : "Unknown"));
					
					if (!authordb2.containsKey(auth.getID() + "," + auth.getSource()))
						authordb2.put(auth.getID() + "," + auth.getSource(), auth);
					
					//authordb2.get(auth.getID() + "," + auth.getSource()).addPosts();
					
				}
				
				obj = json.getJSONObject(i);
				user1 = obj.getString(Settings.JSON_userid);
				source1=obj.getString(Settings.JSON_source);
				if (authordb2.containsKey(user1 +","+ source1))
					continue;
				

				auth = new Author(obj.getString(Settings.JSON_userid), obj.getString(Settings.JSON_source),
						(obj.has(Settings.JSON_fname) ? obj.getString(Settings.JSON_fname) : "")
								+ (obj.has(Settings.JSON_lname) ? obj.getString(Settings.JSON_lname) : ""),
						/*
						 * (obj.has(Settings.JSON_age) ?
						 * obj.getLong(Settings.JSON_age) : 0)
						 */0, (obj.has(Settings.JSON_gender) ? obj.getString(Settings.JSON_gender) : "Unknown"),
						(obj.has(Settings.JSON_location) ? obj.getString(Settings.JSON_location) : "Unknown"));
				
				/*
				 * auth.setComments(obj.has(Settings.JSON_fname) ?
				 * obj.getLong(Settings.JSON_comments) : 0);
				 * auth.setLikes(obj.has(Settings.JSON_likes) ?
				 * obj.getLong(Settings.JSON_likes) : 0);
				 * auth.setPosts(obj.getLong(Settings.JSON_replies) - 1);
				 * auth.setViews(obj.getLong(Settings.JSON_views));
				 */
				if (!authordb2.containsKey(auth.getID() + "," + auth.getSource()))
					authordb2.put(auth.getID() + "," + auth.getSource(), auth);

				//authordb2.get(auth.getID() + "," + auth.getSource()).addPosts();
				
				

			}

			/*
			 * for (int i = 0; i < json.length(); i++) { JSONObject obj =
			 * json.getJSONObject(i); if
			 * (authordb.containsKey(obj.getLong("id"))) continue;
			 * authordb.put(obj.getLong("id"), new Author(obj.getLong("id"),
			 * obj.getString("name"), obj.getLong("age"),
			 * obj.getString("gender"), obj.getString("location")));
			 * 
			 * }
			 */

		}

		LOGGER.log(Level.INFO, " Load users remote " + (System.nanoTime() - stime));
		stime = System.nanoTime();
		return null;

	}

	private String loadroles() throws JSONException {

		String query;
		Connection cnlocal = null;
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
		try {
			cnlocal.close();
		} catch (SQLException e1) {
			LOGGER.log(Level.INFO, Settings.err_unknown, e1);
		}

		return null;

	}

	private String loadlocalusers(String querycond) throws JSONException {
		String query = Settings.sqlselectall + Settings.latable + Settings.sqlwhere + Settings.latable_id + " in "
				+ querycond;
		Connection cnlocal = null;
		//System.out.println(query);
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}
		try (Statement stmt = cnlocal.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				if (authordb2.containsKey(rs.getString("id")+";"+rs.getString("source")))
					continue;
				Author auth = new Author(rs.getString(Settings.latable_id), rs.getString(Settings.latable_source),rs.getString(Settings.latable_name),
						rs.getLong(Settings.latable_age), rs.getString(Settings.latable_gender),
						rs.getString(Settings.latable_location));
				auth.setComments(rs.getLong(Settings.latable_comments));
				auth.setLikes(rs.getLong(Settings.latable_likes));
				auth.setPosts(rs.getLong(Settings.latable_posts) - 1);
				auth.setViews(rs.getLong(Settings.latable_views));
				authordb2.put(rs.getString(Settings.latable_id) + "," + rs.getString(Settings.latable_source), auth);
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
				List<String> uniqueauthors = new ArrayList<>();
				HashMap<Long, Post> temppost = v.getPosts();
				temppost.forEach((k2, v2) -> {
					if (!uniqueauthors.contains(v2.getUID() + "," + v2.getSource()))
						uniqueauthors.add(v2.getUID() + "," + v2.getSource());
				});
				uniqueauthors.forEach((v3) -> {
					if (!authordb2.containsKey(v3)) {
						LOGGER.log(Level.SEVERE, "NULL POINTER UNIQUE AUTHORS" + v3);
					} else {
						Author tempauthor = authordb2.get(v3);
						tempauthor.addComments(v.newcomments());
						tempauthor.addLikes(v.newlikes());
						tempauthor.addViews(v.newviews());
						tempauthor.addPosts();
						authordb2.put(String.valueOf(tempauthor.getID() + "," + tempauthor.getSource()), tempauthor);
					}
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
		/// Connection cndata = null;
		try (Connection cndata = Settings.conndata()) {
			try (Statement stmt = cndata.createStatement()) {
				try (ResultSet rs = stmt.executeQuery(query)) {
					while (rs.next()) {
						if (authordb2.containsKey(rs.getLong(Settings.rutable_userid))) {
						} else {
							authordb2.put(rs.getString(Settings.rutable_userid) + ", ",
									new Author(rs.getString(Settings.rutable_userid),
											rs.getString(Settings.rutable_name), rs.getLong(Settings.rutable_age),
											rs.getString(Settings.rutable_gender), rs.getString(Settings.rutable_loc)));
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error Accessing Remote Databse Please Check If Populated");
			return Backend.error_message("Error (2): Remote Database Error\r\n Please check if populated").toString();
		}
		return null;
	}

	private void evaluatedata() {
		authordb2.forEach((k, v) -> {
			v.calcInfluence((totalcomments / ((double) totalposts)), totallikes / ((double) totalposts),
					totalviews / ((double) totalposts));
		});
		opiniondb.forEach((k, v) -> {
			v.evalReach(totalcomments / ((double) totalposts), totallikes / ((double) totalposts),
					totalviews / ((double) totalposts));
			if(!authordb2.isEmpty())
			v.evalPolarity2(authordb2);

		});
		LOGGER.log(Level.INFO, " calc eval and reach " + (System.nanoTime() - stime));
		stime = System.nanoTime();
	}

	private String insertauthors() throws JSONException {
		Connection cnlocal = null;
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect);
			return Backend.error_message(Settings.err_dbconnect).toString();
		}
		for (Author author : authordb2.values()) {
			String insert = "INSERT INTO " + Settings.latable + " "
					+ "Values (?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.latable_influence + "=?,"
					+ Settings.latable_comments + "=?," + Settings.latable_likes + "=?," + Settings.latable_views
					+ "=?," + Settings.latable_posts + "=?";
			try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
				query1.setString(1, author.getID());
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
				//system.out.println(query1.toString());
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Error Inserting Author into Database");
				try {
					cnlocal.close();
				} catch (Exception e1) {
					LOGGER.log(Level.INFO, "Nothing can be done here", e1);
				}
				return Backend.error_message("Error Inserting Author into Database").toString();
			}
		}
		try {
			cnlocal.close();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Nothing can be done here, error closing");
		}

		LOGGER.log(Level.INFO, " insert " + Settings.latable + " " + (System.nanoTime() - stime));
		stime = System.nanoTime();
		return null;
	}

	private String insertposts() throws JSONException {
		ExecutorService es = Executors.newFixedThreadPool(10);

		for (Opinion opinion : opiniondb.values()) {
			es.execute(multiThread.new Tinsert(opinion));
		}
		es.shutdown();
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			//system.out.println("ERROR THREAD OP");
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
		Connection cnlocal = null;
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
		String err = null;
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
					ExecutorService es = Executors.newFixedThreadPool(10);
					while (rs.next())
						es.execute(multiThread.new Topinions(rs.getLong(1)));
					es.shutdown();
					err = awaittermination(es, "Opinions");
					if (err != null)
						break;
					rs.beforeFirst();
					es = Executors.newFixedThreadPool(10);
					while (rs.next())
						es.execute(multiThread.new Tposts(rs.getLong(1)));
					es.shutdown();

					err = awaittermination(es, "posts");
					if (err != null)
						break;
					break;
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
					query = "Select id from sentimentposts.post where id in (" + query;
					query = query.replace("ORDER BY ID ASC", ") order by id asc");
					if (error)
						return Backend.error_message("Error Loading opinions ids").toString();
					error = true;
					continue;
				}

			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		finally

		{
			try {
				cndata.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, Settings.err_unknown, e);
			}
			try {
				cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, Settings.err_unknown, e);
			}
		}
		return err;
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
