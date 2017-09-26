package general;

import java.sql.DriverManager;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;

/**
 * The Class Settings.
 */
public class Settings {
	
	public static final String version = "2.2";
	
	private static DataSource conlocal = null;
	private static DataSource condata;
	private static DataSource concr;
	
	// To be replaced by properties file
	// Data Origin DB Specs
	private static final Logger LOGGER = new Logging().create(Settings.class.getName());
	private static final String rurl = "jdbc:mysql://127.0.0.1:3306/sentimentposts?autoReconnect=true&useSSL=false";
	private static final String ruser = "diversity";
	private static final String rpass = "!diversity!";

	// Post Table
	public static final String rptable = "post"; // Table Name
	public static final String rptable_rpostid = "post_id"; // Reference Post Id
	public static final String rptable_postid = "id"; // Actual Post Id
	public static final String rptable_userid = "user_id"; // Poster Id
	public static final String rptable_date = "timestamp"; // timestamp
	public static final String rptable_likes = "likes"; // Number of Likes
	public static final String rptable_views = "views"; // Number of views
	public static final String rptable_message = "message"; // Message

	// User Table
	public static final String rutable = "user"; // Table name
	public static final String rutable_userid = "id"; // Id
	public static final String rutable_name = "name"; // Name
	public static final String rutable_age = "age"; // Age
	public static final String rutable_gender = "gender"; // Gender
	public static final String rutable_loc = "location"; // Location

	// Posts Table
	public static final String ptime = "timestamp";

	// Computing Variables
	// Reach
	public static final double pWviews = (double) 1 / 3;
	public static final double pWlikes = (double) 1 / 3;
	public static final double pWcomments = (double) 1 / 3;
	// Influence
	public static final double aWviews = (double) 1 / 3;
	public static final double aWlikes = (double) 1 / 3;
	public static final double aWcomments = (double) 1 / 3;

	// Local DB Specs
	private static final String dbip = "127.0.0.1";
	private static final String dbport = "3306";
	private static final String dbname = "sentimentanalysis";
	private static final String url2 = "jdbc:mysql://" + dbip + ":" + dbport + "/" + dbname
			+ "?autoReconnect=true&useSSL=false";
	private static final String user2 = "diversity";
	private static final String pass2 = "!diversity!";
	public static final Integer dbversion = 11;

	// Author Table
	public static final String latable = "authors";
	public static final String latable_name = "name";
	public static final String latable_age = "age";
	public static final String latable_gender = "gender";
	public static final String latable_location = "location";
	public static final String latable_posts = "posts";
	public static final String latable_comments = "comments";
	public static final String latable_likes = "likes";
	public static final String latable_views = "views";
	public static final String latable_influence = "influence";
	public static final String latable_id = "id";
	// public static final String latable_Uid = "Username";
	public static final String latable_source = "source";

	// Post Table
	public static final String lptable = "posts";
	public static final String lptable_id = "id";
	public static final String lptable_message = "message";
	public static final String lptable_authorid = "authors_id";
	public static final String lptable_likes = "likes";
	public static final String lptable_views = "views";
	public static final String lptable_comments = "comments";
	public static final String lptable_opinion = "opinions_id";
	public static final String lptable_polarity = "polarity";
	public static final String lptable_timestamp = "timestamp";

	// Opinion table
	public static final String lotable = "opinions";
	public static final String lotable_id = "id";
	public static final String lotable_reach = "reach";
	public static final String lotable_polarity = "polarity";
	public static final String lotable_influence = "total_inf";
	public static final String lotable_author = "authors_id";
	public static final String lotable_timestamp = "timestamp";
	public static final String lotable_pss = "pss";
	public static final String lotable_product = "product";
	public static final String lotable_comments = "comments";

	// Models table
	public static final String lmtable = "models";
	public static final String lmtable_id = "id";
	public static final String lmtable_name = "name";
	public static final String lmtable_uri = "uri";
	public static final String lmtable_pss = "pss";
	public static final String lmtable_update = "update_frequency";
	public static final String lmtable_archived = "archived";
	public static final String lmtable_monitorfinal = "products";
	public static final String lmtable_creator = "created_by_user";
	public static final String lmtable_age = "age_range";
	public static final String lmtable_gender = "gender";
	public static final String lmtable_udate = "next_update";
	public static final String lmtable_cdate = "created_date";
	public static final String lmtable_designproject = "design_project";
	public static final String lmtable_add_mediawiki = "media_wiki";

	// Sources Table
	public static final String lutable = "sources";
	public static final String lutable_source = "source";
	public static final String lutable_account = "account";
	public static final String lutable_lastupdate = "last_updated";
	public static final String lutable_pss = "pss";

	// Reach table
	public static final String lrtable = "reach";
	// PSS File
	public static final String DATA_FOLDER = "data";
	public static final String FILENAME_PRODUCTS = "ListProducts.dat";

	// General Settings
	public static String ages = "0-30,,31-60,,61-90";
	public static String genders = "Female,,Male";
	public static String locations = "Asia,,Europe";
	public static Boolean JSON_use = true;
	public static int session_timeout = 30; // in minutes
	public static long currentProduct = 0;
	public static long currentPss = 0;

	
	//testing epochs:
	//http://opennebula.euprojects.net:8922/intelligent-search/getFeedback?epochsFrom[]=1372350200123&epochsTo[]=1500476864598&pssId=31&accounts[]=AirForce1&accounts[]=AirForcse1
	
	// public static String JSON_uri =
	// "http://diversity.euprojects.net/socialfeedbackextraction/getPosts/?epochsFrom[]=111&epochsFrom[]=111&epochsTo[]=333333333&epochsTo[]=333333333&pssId=3&accounts[]=Spyros&accounts[]=JohnSmith";
	//public static String JSON_uri = "http://www.atb-bremen.de/projects/diversitysoap/index.php/getFeedback?epochsFrom[]=0&epochsFrom[]=0&epochsTo[]=999999999999&epochsTo[]=99999999990&pssId=1&accounts[]=Spyros&accounts[]=OEM";
	//public static final String JSON_uri = "http://opennebula.euprojects.net:8922/intelligent-search/getFeedback?epochsFrom[]=1372350200000&epochsTo[]=1498054243000&pssId=31&accounts[]=AirForce1";
	public static final String JSON_uri = "http://opennebula.euprojects.net/intelligent-search/getFeedback";
	public static final String register_uri = "https://diversity.euprojects.net/socialfeedbackextraction/registerSFE";

	//public static final String JSON_uri = "https://diversity.euprojects.net/socialfeedbackextraction/getPosts/";
	public static final boolean LocalPolarity = false;
	public static Boolean include_services = true;

	// Received JSON Parameters
	public static final String JSON_postid = "postId";
	public static final String JSON_replies = "replies";
	public static final String JSON_userid = "userId";
	public static final String JSON_epoch = "postEpoch";
	public static final String JSON_message = "post";
	public static final String JSON_likes = "likes";
	public static final String JSON_views = "views";
	public static final String JSON_tweets = "tweets";
	public static final String JSON_source = "source";
	public static final String JSON_account = "account";
	public static final String JSON_age = "age";
	public static final String JSON_gender = "gender";
	public static final String JSON_location = "location";
	public static final String JSON_fname = "Fname";
	public static final String JSON_lname = "Lname";

	// Common Repository DB Specs
	private static final String crdbip = "127.0.0.1";
	private static final String crdbport = "3306";
	public static final String crdbname = "diversity_common_repository";
	private static final String crurl = "jdbc:mysql://" + crdbip + ":" + crdbport + "/" + crdbname
			+ "?autoReconnect=true&useSSL=false";
	private static final String cruser = "diversity";
	private static final String crpass = "!diversity!";

	// General Table
	public static final String gentable = "general";
	public static final String gentable_id = "id";
	public static final String gentable_totalposts = "totalposts";
	public static final String gentable_totallikes = "totallikes";
	public static final String gentable_totalcomments = "totalcomments";
	public static final String gentable_totalviews = "totalviews";
	public static final String gentable_lastupdated = "lastupdated";
	public static final String gentable_version = "version";

	// PSS Table
	public static final String crpsstable = "pss";
	public static final String crpsstable_id = "id";
	public static final String crpsstable_type = "type";
	public static final String crpsstable_company = "sold_by_company_id";
	public static final String crpsstable_author = "user_id";
	public static final String crpsstable_name = "name";
	
	// Design Project Table
	public static final String crdptable = "design_project";
	public static final String crdptable_id = "id";
	public static final String crdptable_time_created = "time_created";
	public static final String crdptable_wiki_id = "wiki_id";
	public static final String crdptable_author = "user_id";
	public static final String crdptable_name = "name";
	public static final String crdptable_produces_pss_id = "produces_pss_id";
	
	// Design Project Has Users Table
	public static final String crdpuserstable = "design_project_has_users";
	public static final String crdpuserstable_id = "id";
	public static final String crdpuserstable_user_id = "user_id";
	public static final String crdpuserstable_design_project_id = "design_project_id";
	
	// Users Table
	public static final String crusertable = "user";
	public static final String crusertable_id = "id";
	public static final String crusertable_username = "username";
	public static final String crusertable_password = "password";
	public static final String crusertable_email = "email";
	public static final String crusertable_first_name = "first_name";
	public static final String crusertable_last_name = "last_name";
	public static final String crusertable_user_role_id = "user_role_id";
	public static final String crusertable_company_id = "company_id";
	
	// User role  Table
	public static final String cruserrtable = "user_role";
	public static final String cruserrtable_id = "id";
	public static final String cruserrtable_name = "name";
	public static final String cruserrtable_user_id = "user_id";

	// Product Table
	public static final String crproducttable = "product";
	public static final String crproducttable_id = "id";
	public static final String crproducttable_name = "name";
	public static final String crproducttable_type = "type";
	public static final String crproducttable_parent = "parent_product_id";
	public static final String crproducttable_supplied_by = "supplied_by_company_id";
	public static final String crproducttable_isfinal = "is_final_product";
	
	// Service Table
	public static final String crservicetable = "service";
	public static final String crservicetable_id = "id";
	public static final String crservicetable_name = "name";
	public static final String crservicetable_parent = "parent_service_id";
	public static final String crservicetable_supplied_by = "provided_by_company_id";

	// Company Table
	public static final String crcompanytable = "company";
	public static final String crcompanytable_id = "id";
	public static final String crcompanytable_name = "name";
	public static final String crcompanytable_type = "type";
	public static final String crcompanytable_belongs_to = "belongs_to_company_id";

	// PSS has Product Table
	public static final String crpssproducttable = "pss_has_product";
	public static final String crrpssproducttable_pss = "pss_id";
	public static final String crrpssproducttable_product = "product_id";
	
	// PSS has Service Table
	public static final String crpssservicetable = "pss_has_service";
	public static final String crrpssservicetable_pss = "pss_id";
	public static final String crrpssservicetable_service = "service_id";

	// Access rights Table
	public static final String lartable = "access_rights";
	public static final String lartable_name = "role";
	public static final String lartable_description = "description";
	public static final String lartable_vom = "view_opinion_model";
	public static final String larttable_pss = "pss_id";
	public static final String lartable_product = "product_id";
	public static final String lartable_create_edit_delete_model = "create_edit_delete_model";
	public static final String lartable_view_opinion_results = "view_opinion_results";
	public static final String lartable_save_delete_snapshots = "save_delete_snapshots";
	public static final String lartable_use_opinion_prediction = "use_opinion_prediction";
	public static final String lartable_role = "role";
	public static final String lartable_admin = "admin";

	// Media wiki Table
	public static final String lmwtable = "media_wiki";
	public static final String lmwtable_id = "id";
	public static final String lmwtable_name = "name";
	public static final String lmwtable_pss = "pss";

	// Tag cloud table
	public static final String tctable = "tagcloud";
	public static final String tctable_user = "userid";
	public static final String tctable_model = "modelid";
	public static final String tctable_ignored_words = "ignoredwords";
	
	public static final String ltable = "logs";
	public static final String ltable_user = "user_id";
	public static final String ltable_timestamp = "timestamp";
	public static final String ltable_log = "log";

	// Snapshots Table
	public static final String lsstable = "snapshots";
	public static final String lsstable_id = "id";
	public static final String lsstable_name = "name";
	public static final String lsstable_creation_user = "creation_user";
	public static final String lsstable_creation_date = "creation_date";
	public static final String lsstable_result = "result";
	public static final String lsstable_type = "type";
	public static final String lsstable_timespan = "timespan";
	public static final String lsstable_model_id = "model_id";
	
	// Company_is_costumer_of_design_project Table
	public static final String cictable = "company_is_costumer_of_design_project";
	public static final String cictable_id = "id";
	public static final String cictable_company_id = "company_id";
	public static final String cictable_design_project_id = "design_project_id";


	// SQL Common String

	public static final String sqlwhere = " Where ";
	public static final String sqlselectall = "Select * from ";
	// Errors
	public static final String err_unknown = "ERROR ";
	public static final String err_dbconnect = "Cannot connect to database Please Try Again Later.";
	public static final String err_cr = "Cannot connect to common repository";

	/**
	 * Conndata.
	 *
	 * @return the connection
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static Connection conndata() throws ClassNotFoundException, SQLException {
		  try {
			  while(condata==null){}
		    Future<Connection> future = condata.getConnectionAsync();
		    while (future == null || !future.isDone()) {
		      try {
		        Thread.sleep(100); //simulate work
		      }catch (InterruptedException x) {
		        Thread.currentThread().interrupt();
		      }
		    }
		 
		    return future.get(); //should return instantly
		  }catch(Exception e){
			  LOGGER.log(Level.SEVERE, err_dbconnect);
			  return null;
		  }
	}

	/**
	 * Connlocal.
	 *
	 * @return the connection
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	public static Connection connlocal() throws ClassNotFoundException, SQLException {
		try {

			if(conlocal==null)
				startconnections();

		    Future<Connection> future = conlocal.getConnectionAsync();
		    while (!future.isDone()) {
		      try {
		        Thread.sleep(100); //simulate work
		      }catch (InterruptedException x) {
		        Thread.currentThread().interrupt();
		      }
		    }
		 
		    return future.get(); //should return instantly
		  }catch(Exception e){
			  LOGGER.log(Level.SEVERE, err_dbconnect);
			  return null;
		  }
	}

	/**
	 * Conncr.
	 *
	 * @return the connection
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws SQLException
	 */
	public static Connection conncr() throws ClassNotFoundException, SQLException {
		try {
			while(concr==null){}
		    Future<Connection> future = concr.getConnectionAsync();
		    while (future == null || !future.isDone()) {
		      try {
		        Thread.sleep(100); //simulate work
		      }catch (InterruptedException x) {
		        Thread.currentThread().interrupt();
		      }
		    }
		    
		    return future.get(); //should return instantly
		  }catch(Exception e){
			  LOGGER.log(Level.SEVERE, err_dbconnect);
			  return null;
		  }
	}

	/**
	 * Gets the conf.
	 *
	 * @param id
	 *            the id
	 * @return the conf
	 * @throws JSONException
	 *             the JSON exception
	 */
	public JSONArray getConf(long id) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("Op", "Configs");
		result.put(obj);
		obj = new JSONObject();
		String[] values = ages.split(",,");
		obj.put("Size", values.length);
		obj.put("Param", "Age");
		result.put(obj);
		for (int i = 0; i < values.length; i++) {
			obj = new JSONObject();
			String[] range = values[i].split("-");
			obj.put("Min", range[0]);
			obj.put("Max", range[1]);
			result.put(obj);
		}
		obj = new JSONObject();
		String[] values2 = genders.split(",,");
		obj.put("Size", values2.length);
		obj.put("Param", "Gender");
		result.put(obj);
		for (int i = 0; i < values2.length; i++) {
			obj = new JSONObject();
			obj.put("Gender", values2[i]);
			result.put(obj);
		}
		obj = new JSONObject();
		String[] values3 = locations.split(",,");
		obj.put("Size", values3.length);
		obj.put("Param", "Location");
		result.put(obj);
		for (int i = 0; i < values3.length; i++) {
			obj = new JSONObject();
			obj.put("Location", values3[i]);
			result.put(obj);
		}
		obj = new JSONObject();
		if (!Data.getmodel(id).getProducts().isEmpty()) {
			String[] values4 = Data.getmodel(id).getProducts().split(",");
			obj.put("Size", values4.length);
			obj.put("Param", "Product");
			result.put(obj);
			for (int i = 0; i < values4.length; i++) {
				if (!Data.dbhasproduct(Long.valueOf(values4[i])))
					continue;
				obj = new JSONObject();
				obj.put("Product", Data.getProduct(Long.valueOf(values4[i])).get_Name());
				result.put(obj);
			}
		}

		LOGGER.log(Level.INFO, result.toString());
		return result;

	}

	
	/**
	 * Gets the default conf.
	 *
	 *
	 * @return the conf
	 * @throws JSONException
	 *             the JSON exception
	 */
	public JSONArray getConf() throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("Op", "Configs");
		result.put(obj);
		obj = new JSONObject();
		obj.put("Size", 3);
		obj.put("Param", "Age");
		result.put(obj);

		obj.put("Min", 0);
		obj.put("Max", 30);
		result.put(obj);
		obj.put("Min", 31);
		obj.put("Max", 60);
		result.put(obj);
		obj.put("Min", 61);
		obj.put("Max", 99);
		result.put(obj);
		
		obj = new JSONObject();
		obj.put("Size", 2);
		obj.put("Param", "Gender");
		result.put(obj);
		obj = new JSONObject();
		obj.put("Gender", "Male");
		result.put(obj);
		obj.put("Gender", "Female");
		result.put(obj);
		
		obj = new JSONObject();
		obj.put("Size", 3);
		obj.put("Param", "Location");
		result.put(obj);
		obj = new JSONObject();
		obj.put("Location", "Asia");
		result.put(obj);
		obj.put("Location", "Europe");
		result.put(obj);
		
//		obj = new JSONObject();
//		if (!Data.getmodel(id).getProducts().isEmpty()) {
//			String[] values4 = Data.getmodel(id).getProducts().split(",");
//			obj.put("Size", values4.length);
//			obj.put("Param", "Product");
//			result.put(obj);
//			for (int i = 0; i < values4.length; i++) {
//				if (!Data.dbhasproduct(Long.valueOf(values4[i])))
//					continue;
//				obj = new JSONObject();
//				obj.put("Product", Data.getProduct(Long.valueOf(values4[i])).get_Name());
//				result.put(obj);
//			}
//		}

		LOGGER.log(Level.INFO, result.toString());
		return result;

	}
	
	/**
	 * Sets the conf.
	 *
	 * @param msg
	 *            the msg
	 * @return the JSON object
	 * @throws JSONException
	 *             the JSON exception
	 */
	public JSONObject setConf(JSONObject msg) throws JSONException {
		String edited = new String();
		if (msg.has("Gender")) {
			Settings.genders = msg.getString("Gender");
			edited += "Gender ";
		}
		if (msg.has("Age")) {
			Settings.ages = msg.getString("Age");
			edited += "Age ";
		}
		if (msg.has("Location")) {
			Settings.locations = msg.getString("Location");
			edited += "Location ";
		}
		msg = new JSONObject();
		msg.put("Op", "Error");
		if (edited != "") {
			msg.put("Message", edited + "configuration updated");
		} else {
			msg.put("Message", "Nothing Changed");
		}
		return msg;
	}
	
	public static void startconnections(){
		PoolProperties p = new PoolProperties();
        p.setUrl(rurl);
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername(ruser);
        p.setPassword(rpass);
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setFairQueue(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(40);
        p.setMaxIdle(1);
        p.setInitialSize(1);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(1);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
          "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
          + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
          + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");
        condata = new DataSource();
        condata.setPoolProperties(p);
        
        p = new PoolProperties();
        p.setMaxIdle(40);
        p.setUrl(url2);
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername(user2);
        p.setPassword(pass2);
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setFairQueue(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(80);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(30);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
          "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
          "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        conlocal = new DataSource();
        conlocal.setPoolProperties(p);
        
        p = new PoolProperties();
        p.setMaxIdle(40);
        p.setUrl(crurl);
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername(cruser);
        p.setPassword(crpass);
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setFairQueue(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(40);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
          "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
          "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        concr = new DataSource();
        concr.setPoolProperties(p);
	}
}
