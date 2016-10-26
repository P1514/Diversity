package Extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import General.Settings;

public class GetAuthors {

	private Connection cnlocal;

	public GetAuthors() {
	}

	public JSONArray getAll() throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("Op", "authtable");
		result.put(obj);
		String insert = new String();
		PreparedStatement query1 = null;
		insert = "Select * FROM "+Settings.latable+" where id in (Select "+Settings.lotable_author+" from "+Settings.lotable+")";
		ResultSet rs = null;

		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			rs = query1.executeQuery();
			for(;rs.next();){
				obj= new JSONObject();
				obj.put("Name", rs.getString(Settings.latable_name));
				obj.put("Gender", rs.getString(Settings.latable_gender));
				obj.put("Age", rs.getInt(Settings.latable_age));
				obj.put("Location", rs.getString(Settings.latable_location));
				int nposts=rs.getInt(Settings.latable_posts);
				obj.put("Nposts", nposts);
				double tmp=rs.getInt(Settings.latable_comments)/nposts;
				obj.put("Avgcomms", trunc(String.valueOf(tmp)));
				tmp=rs.getInt(Settings.latable_likes)/nposts;
				obj.put("Avglikes", trunc(String.valueOf(tmp)));
				tmp = rs.getInt(Settings.latable_views)/nposts;
				obj.put("Avgviews", trunc(String.valueOf(tmp)));
				obj.put("Influence", trunc(String.valueOf(rs.getDouble(Settings.latable_influence))));
				result.put(obj);
			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			;
			try {
				if (query1 != null)
					query1.close();
			} catch (Exception e) {
			}
			;
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (Exception e) {
			}
			;
		}

		return result;

	}

	private String trunc(String number) {
		double result = 0;
		try {

			result = Double.valueOf(number);
			number = String.format("%.2f", result);
			result = Double.parseDouble(number);

		} catch (Exception e) {
			number = number.replaceAll(",", ".");
			result = Double.parseDouble(number);

		}
		return Double.toString(result);

	}

	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
