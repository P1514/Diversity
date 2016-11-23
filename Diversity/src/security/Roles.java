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
			query = "SELECT "/*+Settings.latable_viewopinion+"*/+"view_opinion_model, create_edit_delete_model, view_opinion_results, save_delete_snapshots, use_opinion_prediction"
					+ " FROM `sentimentanalysis`.`access_rights`" + " WHERE role = ?";
			stmt = cnlocal.prepareStatement(query);
			stmt.setString(1, Role);
			rs = stmt.executeQuery();
			
			if(rs.next()){
			obj.put("Op","Rights");
			obj.put("view_OM", rs.getBoolean("view_opinion_model"));		
			obj.put("create_edit_delete_model", rs.getBoolean("create_edit_delete_model"));
			obj.put("view_opinion_results", rs.getBoolean("view_opinion_results"));
			obj.put("view_use_opinion_prediction", rs.getBoolean("use_opinion_prediction"));
			obj.put("save_delete_snapshots", rs.getBoolean("save_delete_snapshots"));
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
