package security;

import java.sql.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import general.Settings;

/**
 * The Class Roles.
 */
public class Roles {

	
	
	/**
	 * Gets the restrictions.
	 *
	 * @param Role the role
	 * @return the restrictions
	 * @throws JSONException the JSON exception
	 */
	public static JSONArray getRestrictions(String Role)throws JSONException{

		Connection cnlocal = null;
		String query;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();



		try {
			cnlocal = Settings.connlocal();
			query = "SELECT "+Settings.artable_vom+", " +Settings.artable_create_edit_delete_model+", "
					+Settings.artable_view_opinion_results+", "
					+Settings.artable_save_delete_snapshots+", "
					+Settings.artable_use_opinion_prediction
					+ " FROM `sentimentanalysis`.`access_rights`" + " WHERE "+ Settings.artable_role + " = ?";
			stmt = cnlocal.prepareStatement(query);
			stmt.setString(1, Role);
			rs = stmt.executeQuery();
			
			if(rs.next()){
			obj.put("Op","Rights");
			obj.put("view_OM", rs.getBoolean(Settings.artable_vom));		
			obj.put("create_edit_delete_model", rs.getBoolean(Settings.artable_create_edit_delete_model));
			obj.put("view_opinion_results", rs.getBoolean(Settings.artable_view_opinion_results));
			obj.put("view_use_opinion_prediction", rs.getBoolean(Settings.artable_use_opinion_prediction));
			obj.put("save_delete_snapshots", rs.getBoolean(Settings.artable_save_delete_snapshots));
			//System.out.println(Role +" - " + obj);
			result.put(obj);
			rs.close();
			stmt.close();
			cnlocal.close();
			}
			else{ //in case the role dosen't exist on the database it gives the user no rights
				
				obj.put("Op","Rights");
				obj.put("view_OM",false);
				obj.put("create_edit_delete_model", false);
				obj.put("view_opinion_results", false);
				obj.put("view_use_opinion_prediction", false);
				obj.put("save_delete_snapshots", false);
				result.put(obj);
				
			}
			
		} catch (ClassNotFoundException | SQLException e2) {
			//
			e2.printStackTrace();
		}
		return result;
	}
}
