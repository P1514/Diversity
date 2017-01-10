package general;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.Connection;

/**
 * The Class Settings.
 */
public class Settings {
	// To be replaced by properties file
	// Data Origin DB Specs
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
	private static final String dbip="127.0.0.1";
	private static final String dbport="3306";
	private static final String dbname = "sentimentanalysis";
	private static final String url2 = "jdbc:mysql://"+dbip+":"+dbport+"/"+dbname+"?autoReconnect=true&useSSL=false";
	private static final String user2 = "diversity";
	private static final String pass2 = "!diversity!";
	public static final Integer dbversion = 9;

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
	//public static final String latable_Uid = "Username";
	public static final String latable_source = "source";

	// Post Table
	public static final String lptable = "posts";
	public static final String lptable_id = "id";
	public static final String lptable_message= "message";
	public static final String lptable_authorid = "authors_id";
	public static final String lptable_likes = "likes";
	public static final String lptable_views = "views";
	public static final String lptable_comments = "comments";
	public static final String lptable_opinion = "opinions_id";
	public static final String lptable_polarity = "polarity";
	public static final String lptable_timestamp = "timestamp";

	// Opinion table
	public static final String lotable= "opinions";
	public static final String lotable_id="id";
	public static final String lotable_reach="reach";
	public static final String lotable_polarity="polarity";
	public static final String lotable_influence="total_inf";
	public static final String lotable_author="authors_id";
	public static final String lotable_timestamp="timestamp";
	public static final String lotable_pss="pss";
	public static final String lotable_product="product";
	public static final String lotable_comments="comments";
	
	// Models table
	
	public static final String lmtable="models";
	public static final String lmtable_id="id";
	public static final String lmtable_name="name";
	public static final String lmtable_uri="uri";
	public static final String lmtable_pss="pss";
	public static final String lmtable_update="update_frequency";
	public static final String lmtable_archived="archived";
	public static final String lmtable_monitorfinal="products";
	public static final String lmtable_creator="created_by_user";
	public static final String lmtable_age="age_range";
	public static final String lmtable_gender="gender";
	public static final String lmtable_udate = "next_update";
	public static final String lmtable_cdate = "created_date";
	
	// Sources Table
	public static final String lutable="sources";
	public static final String lutable_source="source";
	public static final String lutable_account="account";
	public static final String lutable_lastupdate="last_updated";
	public static final String lutable_pss="pss";
	
	
	// Reach table
	public static final String lrtable="reach";
	// PSS File
	public static final String DATA_FOLDER = "data";
	public static final String FILENAME_PRODUCTS = "ListProducts.dat";

	// General  Settings
	public static String ages = "0-30,,31-60,,61-90";
	public static String genders = "Female,,Male";
	public static String locations = "Asia,,Europe";
	public static Boolean JSON_use = true;
	public static String JSON_uri = "http://diversity.euprojects.net/socialfeedbackextraction/getPosts/?epochsFrom[]=111&epochsFrom[]=111&epochsTo[]=333333333&epochsTo[]=333333333&pssId=3&accounts[]=Spyros&accounts[]=JohnSmith";
	
	// Received JSON Parameters
	public static final String JSON_postid = "postId";
	public static final String JSON_replies = "replies";
	public static final String JSON_userid = "account";
	public static final String JSON_epoch = "postEpoch";
	public static final String JSON_message = "post";
	public static final String JSON_likes = "likes";
	public static final String JSON_views = "views";
	public static final String JSON_tweets = "tweets";
	public static final String JSON_source = "source";
	public static final String JSON_age = "age";
	public static final String JSON_gender = "gender";
	public static final String JSON_location = "location";
	public static final String JSON_fname = "Fname";
	public static final String JSON_lname = "Lname";
	
	// Common Repository DB Specs
	private static final String crdbip="127.0.0.1";
	private static final String crdbport="3306";
	private static final String crdbname = "diversity_common_repository";
	private static final String crurl = "jdbc:mysql://"+crdbip+":"+crdbport+"/"+crdbname+"?autoReconnect=true&useSSL=false";
	private static final String cruser = "diversity";
	private static final String crpass = "!diversity!";
	
	// PSS Table
	public static final String crpsstable ="pss";
	public static final String crpsstable_id="id";
	public static final String crpsstable_type="type";
	public static final String crpsstable_company="sold_by_company_id";
	public static final String crpsstable_author="user_id";
	public static final String crpsstable_name ="name";
	
	
	// Product Table
	public static final String crproducttable="product";
	public static final String crproducttable_id="id";
	public static final String crproducttable_name="name";
	public static final String crproducttable_type="type";
	public static final String crproducttable_parent="parent_product_id";
	public static final String crproducttable_supplied_by="supplied_by_company_id";
	public static final String crproducttable_isfinal="is_final_product";
	
	// Company Table
	public static final String crcompanytable="company";
	public static final String crcompanytable_id="id";
	public static final String crcompanytable_name="name";
	public static final String crcompanytable_type="type";
	public static final String crcompanytable_belongs_to = "belongs_to_company_id";
	
	// PSS has Product Table
	public static final String crpssproducttable="pss_has_product";
	public static final String crrpssproducttable_pss="pss_id";
	public static final String crrpssproducttable_product="product_id";
	
	// Access rights Table
	public static final String artable_vom="view_opinion_model";
	public static final String arttable_pss="pss_id";
	public static final String artable_product="product_id";
	public static final String artable_create_edit_delete_model="create_edit_delete_model";
	public static final String artable_view_opinion_results="view_opinion_results";
	public static final String artable_save_delete_snapshots="save_delete_snapshots";
	public static final String artable_use_opinion_prediction="use_opinion_prediction";
	public static final String artable_role="role";


	
	
	/**
	 * Conndata.
	 *
	 * @return the connection
	 * @throws ClassNotFoundException the class not found exception
	 * @throws SQLException the SQL exception
	 */
	public static Connection conndata() throws ClassNotFoundException, SQLException {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2);
		Class.forName("com.mysql.jdbc.Driver");
		return (Connection) DriverManager.getConnection(rurl, ruser, rpass);
	}

	/**
	 * Connlocal.
	 *
	 * @return the connection
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static Connection connlocal() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		while (true) {
			try {
				return (Connection) DriverManager.getConnection(url2, user2, pass2);
			} catch (SQLException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Conncr.
	 *
	 * @return the connection
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static Connection conncr() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		while (true) {
			try {
				return (Connection) DriverManager.getConnection(crurl, cruser, crpass);
			} catch (SQLException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the conf.
	 *
	 * @param id the id
	 * @return the conf
	 * @throws JSONException the JSON exception
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
		String[] values4 = Data.modeldb.get(id).getProducts().split(",");
		obj.put("Size", values4.length);
		obj.put("Param", "Product");
		result.put(obj);
		for (int i = 0; i < values4.length; i++) {
			if(!Data.productdb.containsKey(Long.valueOf(values4[i]))) continue;
			obj = new JSONObject();
			obj.put("Product", Data.productdb.get(Long.valueOf(values4[i])).get_Name());
			result.put(obj);
		}
		
		
		
		System.out.print(result.toString());
		return result;

	}

	/**
	 * Sets the conf.
	 *
	 * @param msg the msg
	 * @return the JSON object
	 * @throws JSONException the JSON exception
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
}
