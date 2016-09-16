package importDB;

import java.sql.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import backend.Settings;

public class CleanDB {
	Settings dbc = new Settings();
	Connection cndata = null;
	Connection cnlocal = null;

	public CleanDB() {
	}

	public String clean() throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		String query;

		Statement stmt = null;

		// Clean local DB
		try {
			cnlocal = Settings.connlocal();
			query = "DELETE from "+Settings.lptable;
			stmt = cnlocal.createStatement();
			stmt.execute(query);
			stmt.close();
			query = "delete from "+Settings.lotable;
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
			query = "UPDATE `diversitydb`.`general` SET `totalposts`='0', `totallikes`='0', `totalcomments`='0', `totalviews`='0', `lastupdated`='1970-01-01' WHERE `id`='1'";
			stmt = cnlocal.createStatement();
			stmt.execute(query);
		} catch (ClassNotFoundException e) {
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
					e.printStackTrace();
				}
			if (cnlocal != null)
				try {
					cnlocal.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}


		obj.put("Op", "Error");
		obj.put("Message", "Cleaned Successfully");
		result.put(obj);
		return result.toString();
	}
}