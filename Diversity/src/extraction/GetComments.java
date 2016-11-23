package extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Settings;
// TODO: Auto-generated Javadoc

/**
 * The Class GetComments.
 *
 * @author Uninova - IControl
 */
public class GetComments {

	/** The cnlocal. */
	private Connection cnlocal;

	/**
	 * Class that fetches comments data.
	 */

	public GetComments() {
	}

	/**
	 * User to fetch all comments to a specific parent post
	 * <p>
	 * Returns and JSONArray with all comments for a specific post referring to
	 * the the entry JSONObject.
	 * <p>Examples:
	 * <ul>
	 * <li>Input:{"Values":"70182","Id":"820"}, it will then fetch comments from
	 * the Database regarding the parent posts with id "70182" and model with
	 * the id "820".</li>
	 * <li>Output: [{"Op":"comments"},{"Message":"They launched the new Austin
	 * Cricket! These are average
	 * sneakers!","Influence":"1.0","Polarity":"50.0","Gender":"Male","Age":"35","Name":"David
	 * Jones","Location":"Europe"},{...}]</li>
	 * </ul>
	 * <p>
	 *
	 * @param msg JSONObject with the information o request
	 * @return JSONArray
	 * @throws JSONException is case input is not in correct format
	 */

	public JSONArray getAll(JSONObject msg) throws JSONException {
		JSONArray result = new JSONArray();
		String[] pre_result = new String[50];
		//String[] genders = Settings.genders.split(",,");
		JSONObject obj = new JSONObject();
		obj.put("Op", "comments");
		result.put(obj);
		String insert = new String();
		PreparedStatement query1 = null;
		//Model model = Data.modeldb.get(msg.getLong("Id"));
		int n_tops = 0;
		insert = "Select " + Settings.latable_name + "," + Settings.latable_influence + "," + Settings.latable_location
				+ "," + Settings.latable_gender + "," + Settings.latable_age + "," + Settings.lptable_polarity + ","
				+ Settings.lptable_message + " from " + Settings.lptable + "," + Settings.latable + " where ( ("
				+ Settings.lptable + "." + Settings.lptable_id + "=? OR";
		ResultSet rs = null;
		/*
		 * if (model.getGender().equals("All")) {
		 * 
		 * insert += ") "; }else{ insert += "AND gender=?) "; }
		 */
		insert += " " + Settings.lptable_opinion + "=? )AND " + Settings.lptable + "." + Settings.lptable_authorid + "="
				+ Settings.latable + "." + Settings.latable_id + ") ORDER BY " + Settings.lptable + "."
				+ Settings.lptable_id + " ASC";
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			int i = 0;
			query1.setString(1, msg.getString("Values"));
			/*
			 * if (model.getGender().equals("All")) { }else{
			 * query1.setString(3+1+i, model.getGender()); i++; }
			 */
			query1.setInt(1 + i + 1, msg.getInt("Values"));
			//System.out.print(query1);

			rs = query1.executeQuery();

			for (i = 0; rs.next(); i++) {
				n_tops++;
				pre_result[i] = rs.getString(Settings.latable_name) + ",," + rs.getDouble(Settings.latable_influence)
						+ ",," + rs.getString(Settings.latable_location) + ",," + rs.getString(Settings.latable_gender)
						+ ",," + rs.getInt(Settings.latable_age) + ",,";
				pre_result[i] += rs.getDouble(Settings.lptable_polarity) + ",,";
				pre_result[i] += rs.getString(Settings.lptable_message);
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

		for (int i = 0; i < n_tops; i++) {
			obj = new JSONObject();
			String[] pre_results = pre_result[i].split(",,");
			obj.put("Name", pre_results[0]);
			obj.put("Influence", trunc(pre_results[1]));
			obj.put("Location", pre_results[2]);
			obj.put("Gender", pre_results[3]);
			obj.put("Age", pre_results[4]);
			obj.put("Polarity", trunc(pre_results[5]));
			obj.put("Message", pre_results[6]);
			result.put(obj);

		}
		//System.out.print(result);

		return result;

	}

	/**
	 * Trunc.
	 *
	 * @param number the number
	 * @return the string
	 */
	private String trunc(String number) {
		double result = 0;
		try {

			result = Double.valueOf(number);
			number = String.format("%.1f", result);
			result = Double.parseDouble(number);

		} catch (Exception e) {
			number = number.replaceAll(",", ".");
			result = Double.parseDouble(number);

		}
		return Double.toString(result);

	}

	/**
	 * Dbconnect.
	 */
	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
