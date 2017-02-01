package extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Backend;
import general.Data;
import general.Settings;

/**
 * The Class GetComments.
 *
 * @author Uninova - IControl
 */
public class GetComments {

	private Connection cnlocal;
	private static final Logger LOGGER = Logger.getLogger(Data.class.getName());

	/**
	 * Class that fetches comments data.
	 */

	/**
	 * User to fetch all comments to a specific parent post
	 * <p>
	 * Returns and JSONArray with all comments for a specific post referring to
	 * the the entry JSONObject.
	 * <p>
	 * Examples:
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
	 * @param msg
	 *            JSONObject with the information o request
	 * @return JSONArray
	 * @throws JSONException
	 *             is case input is not in correct format
	 */

	public JSONArray getAll(JSONObject msg) throws JSONException {
		JSONArray result = new JSONArray();
		String[] preresult = new String[50];
		JSONObject obj = new JSONObject();
		obj.put("Op", "comments");
		result.put(obj);
		String insert = new String();
		int ntops = 0;
		insert = "Select " + Settings.latable_name + "," + Settings.latable_influence + "," + Settings.latable_location
				+ "," + Settings.latable_gender + "," + Settings.latable_age + "," + Settings.lptable_polarity + ","
				+ Settings.lptable_message + " from " + Settings.lptable + "," + Settings.latable + " where ( ("
				+ Settings.lptable + "." + Settings.lptable_id + "=? OR";
		insert += " " + Settings.lptable_opinion + "=? )AND " + Settings.lptable + "." + Settings.lptable_authorid + "="
				+ Settings.latable + "." + Settings.latable_id + ") ORDER BY " + Settings.lptable + "."
				+ Settings.lptable_id + " ASC";
		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error", e);
			return Backend.error_message("Error Connecting to Database Please Try Again Later");
		}
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			query1.setString(1, msg.getString("Values"));
			query1.setInt(2, msg.getInt("Values"));

			try (ResultSet rs = query1.executeQuery()) {

				for (int i = 0;; i++) {
					if (!rs.next())
						break;
					ntops++;
					preresult[i] = rs.getString(Settings.latable_name) + ",," + rs.getDouble(Settings.latable_influence)
							+ ",," + rs.getString(Settings.latable_location) + ",,"
							+ rs.getString(Settings.latable_gender) + ",," + rs.getInt(Settings.latable_age) + ",,";
					preresult[i] += rs.getDouble(Settings.lptable_polarity) + ",,";
					preresult[i] += rs.getString(Settings.lptable_message);
				}

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "ERROR", e);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
		} finally {
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}

		for (int i = 0; i < ntops; i++) {
			obj = new JSONObject();
			String[] preresults = preresult[i].split(",,");
			obj.put("Name", preresults[0]);
			obj.put("Influence", trunc(preresults[1]));
			obj.put("Location", preresults[2]);
			obj.put("Gender", preresults[3]);
			obj.put("Age", preresults[4]);
			obj.put("Polarity", trunc(preresults[5]));
			obj.put("Message", preresults[6]);
			result.put(obj);

		}
		return result;

	}

	/**
	 * Trunc.
	 *
	 * @param number
	 *            the number
	 * @return the string
	 */
	private String trunc(String number) {

		double result = Double.valueOf(number);
		number = String.format("%.1f", result);
		result = Double.parseDouble(number.replaceAll(",", "."));

		return Double.toString(result);

	}

	/**
	 * Dbconnect.
	 */
	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
		}

	}
}
