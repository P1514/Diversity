package general;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sun.util.locale.provider.LocaleServiceProviderPool.LocalizedObjectGetter;

// TODO: Auto-generated Javadoc
/**
 * The Class CleanDB.
 */
public final class CleanDB {
	
	
	/** The cnlocal. */
	private Connection cnlocal = null;
	private static final Logger LOGGER = Logger.getLogger(CleanDB.class.getName());

	/**
	 * Instantiates a new clean DB.
	 */
	public CleanDB() {
	}

	/**
	 * Clean.
	 *
	 * @return the string
	 * @throws JSONException the JSON exception
	 */
	public String clean() throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		String query;
		Statement stmt=null;
		// Clean local DB
		try {
			cnlocal = Settings.connlocal();}
		catch(Exception e){
		LOGGER.log(Level.SEVERE, "ERROR", e);
		}
		try{
			query = "DELETE from "+Settings.lptable;
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "delete from "+Settings.lotable;
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "delete from "+Settings.lmtable;
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "delete from "+Settings.latable;
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "delete from influences";
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "DELETE from "+Settings.lmtable;
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "DELETE from "+Settings.lutable;
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "UPDATE `general` SET `totalposts`='0', `totallikes`='0', `totalcomments`='0', `totalviews`='0', `lastupdated`='1970-01-01' WHERE `id`='1'";
			stmt = cnlocal.createStatement();
			stmt.execute(query);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			return Backend.error_message("Error (1): Missing Local Database").toString();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					LOGGER.log(Level.INFO, "ERROR", e);
				}
			if (cnlocal != null)
				try {
					cnlocal.close();
				} catch (SQLException e) {
					LOGGER.log(Level.INFO, "ERROR", e);
				}
		}

		return Backend.error_message("Cleaned Successfully").toString();
	}}
