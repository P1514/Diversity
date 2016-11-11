package security;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
//import org.json.JSONObject;

import general.Data;
import general.Model;
import general.Settings;

public class Roles {

	
	
	public static JSONArray getRestrictions(String Role) {

		Connection cnlocal = null;
		String query;
		Statement stmt = null;
		ResultSet rs = null;
		JSONArray result = new JSONArray();

		Role = '"' + Role + '"';
		Role = Role + ";";

		try {
			cnlocal = Settings.connlocal();

			query = "SELECT view_opinion_model, create_edit_delete_model, view_opinion_results, save_delete_snapshots, use_opinion_prediction"
					+ "FROM `sentimentanalysis`.`access_rights`" + "WHERE role = " + Settings.lmtable;
			stmt = cnlocal.createStatement();
			System.out.println("TEST****** - " + query);
			rs = stmt.executeQuery(query);

			for (; rs.next();) {
//				Model model = new Model(rs.getLong(Settings.lmtable_id), rs.getLong(Settings.lmtable_update),
//						rs.getLong(Settings.lmtable_creator), rs.getString(Settings.lmtable_name),
//						rs.getString(Settings.lmtable_uri), rs.getString(Settings.lmtable_pss),
//						rs.getString(Settings.lmtable_age), rs.getString(Settings.lmtable_gender),
//						rs.getString(Settings.lmtable_monitorfinal), rs.getBoolean(Settings.lmtable_archived));
//				Data.modeldb.put(model.getId(), model);

			}
			rs.close();
			stmt.close();
			cnlocal.close();

		} catch (ClassNotFoundException | SQLException e2) {
			//
			e2.printStackTrace();
		}
		return result;
	}
}
