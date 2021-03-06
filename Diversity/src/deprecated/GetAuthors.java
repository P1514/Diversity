package deprecated;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Settings;
// TODO: Auto-generated Javadoc

/**
 * The Class GetAuthors.
 */
@Deprecated
public class GetAuthors {

	/** The cnlocal. */
	private Connection cnlocal;

	/**
	 * Class to fetch data from all authors.
	 */
	/*
	 * public GetAuthors() { }
	 */

	/**
	 * Returns a JSONArray with all the information from all authors, regarding
	 * each author Name, Gender, Age, Location, Number of Posts, Average
	 * Comments, Likes and Views for the posts each one created, and also their
	 * influence.
	 * <p>
	 * Example : [{"Name":"John", "Gender":"Male", "Age":"Location",
	 * "Nposts":"10", "Avgcomms":"12.22", "Avglikes":"122.25",
	 * "Influence":"1.7"},{...}]
	 *
	 * @return JSONArray with the information in the Database
	 * @throws JSONException
	 *             is case the json is not created successfully
	 */
	@Deprecated
	public JSONArray getAll() throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("Op", "authtable");
		result.put(obj);
		String insert;
		PreparedStatement query1 = null;
		insert = "Select * FROM " + Settings.latable + " where id in (Select " + Settings.lotable_author + " from "
				+ Settings.lotable + ")";
		ResultSet rs = null;

		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			rs = query1.executeQuery();
			while (rs.next()) {
				obj = new JSONObject();
				obj.put("Name", rs.getString(Settings.latable_name));
				obj.put("Gender", rs.getString(Settings.latable_gender));
				obj.put("Age", rs.getInt(Settings.latable_age));
				obj.put("Location", rs.getString(Settings.latable_location));
				int nposts = rs.getInt(Settings.latable_posts);
				obj.put("Nposts", nposts);
				double tmp = rs.getInt(Settings.latable_comments) / nposts;
				obj.put("Avgcomms", trunc(String.valueOf(tmp)));
				tmp = rs.getInt(Settings.latable_likes) / nposts;
				obj.put("Avglikes", trunc(String.valueOf(tmp)));
				tmp = rs.getInt(Settings.latable_views) / nposts;
				obj.put("Avgviews", trunc(String.valueOf(tmp)));
				obj.put("Influence", trunc(String.valueOf(rs.getDouble(Settings.latable_influence))));
				result.put(obj);
			}

		} catch (Exception e) {
			System.out.println("Error Loading from Database");
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				System.out.println("Error Closing Connections");
			}
			try {
				if (query1 != null)
					query1.close();

			} catch (SQLException e) {
				System.out.println("Error Closing Connections");
			}
			try {
				if (cnlocal != null)

					cnlocal.close();
			} catch (SQLException e) {
				System.out.println("Error Closing Connections");
			}
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
