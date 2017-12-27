package extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import extraction.GetPosts.Post_gp;
import general.Backend;
import general.Logging;
import general.Settings;

/**
 * The Class GetComments.
 *
 * @author Uninova - IControl
 */
public class GetComments {

	private static final Logger LOGGER = new Logging().create(GetComments.class.getName());

	/**
	 * Class that fetches comments data.
	 */

	/**
	 * User to fetch all comments to a specific parent post
	 * <p>
	 * Returns and JSONArray with all comments for a specific post referring to the
	 * the entry JSONObject.
	 * <p>
	 * Examples:
	 * <ul>
	 * <li>Input:{"Values":"70182","Id":"820"}, it will then fetch comments from the
	 * Database regarding the parent posts with id "70182" and model with the id
	 * "820".</li>
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
		ArrayList<Post_gp> pre_result = new ArrayList<>();
		JSONObject obj = new JSONObject();
		obj.put("Op", "comments");
		result.put(obj);
		String insert;
		insert = "Select p."+Settings.lptable_id+"," + Settings.latable_name + "," + Settings.latable_influence + "," + Settings.latable_location
				+ "," + Settings.latable_gender + "," + Settings.latable_age + "," + Settings.lptable_polarity + ","
				+ Settings.lptable_message + " from " + Settings.lptable + " p left join " + Settings.latable + " a on "+Settings.lptable_authorid+"=a."+Settings.latable_id+" where ( ("
				+ "p." + Settings.lptable_id + "=? OR"
				+ " " + Settings.lptable_opinion + "=? )AND p." + Settings.lptable_authorid + "=a." + Settings.latable_id + ") ORDER BY Abs(p."
				+Settings.lptable_id+"-"+Settings.lptable_opinion+") ASC";
		
		//LOGGER.log(Level.INFO, "Get Comments Query"+insert);

		try (Connection cnlocal = Settings.connlocal(); PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			query1.setString(1, msg.getString("Values"));
			query1.setString(2, msg.getString("Values"));

			try (ResultSet rs = query1.executeQuery()) {
				Post_gp temp_post = new Post_gp();
				if(!rs.next() || rs.getLong(Settings.lptable_id) != Long.parseLong(msg.getString("Values"))) {
				}else {
					temp_post.name=rs.getString(Settings.latable_name);
					temp_post.influence = rs.getDouble(Settings.latable_influence);
					temp_post.location = rs.getString(Settings.latable_location);
					temp_post.gender=rs.getString(Settings.latable_gender);
					temp_post.age=rs.getInt(Settings.latable_age);
					temp_post.polarity=rs.getDouble(Settings.lptable_polarity);
					temp_post.message=rs.getString(Settings.lptable_message);
					
				}
				pre_result.add(temp_post);
				while(rs.next()) {
					temp_post=new Post_gp();
					temp_post.name=rs.getString(Settings.latable_name);
					temp_post.influence = rs.getDouble(Settings.latable_influence);
					temp_post.location = rs.getString(Settings.latable_location);
					temp_post.gender=rs.getString(Settings.latable_gender);
					temp_post.age=rs.getInt(Settings.latable_age);
					temp_post.polarity=rs.getDouble(Settings.lptable_polarity);
					temp_post.message=rs.getString(Settings.lptable_message);
					pre_result.add(temp_post);
				}

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
		}

		for (Post_gp answer : pre_result) {
			obj = new JSONObject();
			obj.put("Name", answer.name);
			obj.put("Influence", trunc(answer.influence+""));
			obj.put("Location", answer.location);
			obj.put("Gender", answer.gender);
			obj.put("Age", answer.age);
			obj.put("Polarity", trunc(answer.polarity+""));
			obj.put("Message", answer.message);
			result.put(obj);

		}
		return result;

	}
	
	class Post_gp{
		public String name = "No Name";
		public double influence = 0.0;
		public String location = "No Location";
		public String gender = "No Gender";
		public int age = 0;
		public Long date = (long)0;
		public Double polarity = 0.0;
		public Double reach = 0.0;
		public Integer comments = 0;
		public String message = "No Message";

	}

	/**
	 * Trunc.
	 *
	 * @param number
	 *            the number
	 * @return the string
	 */
	static String trunc(String number) {
		String numberef = number;
		double result = Double.parseDouble(numberef);
		numberef = String.format("%.1f", result);
		result = Double.parseDouble(numberef.replaceAll(",", "."));

		return Double.toString(result);

	}
}
