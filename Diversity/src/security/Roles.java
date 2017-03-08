package security;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Data;
import general.Role;
import general.Settings;

/**
 * The Class Roles.
 */
public class Roles {

	/**
	 * Gets the restrictions.
	 *
	 * @param Role
	 *            the role
	 * @return the restrictions
	 * @throws JSONException
	 *             the JSON exception
	 */
	public static JSONArray getRestrictions(String role) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		Role arole = Data.getRole(role);
		int i = 0;
		obj.put("Op", "Rights");
		obj.put("view_OM", arole.getPermission(i++));
		obj.put("create_edit_delete_model", arole.getPermission(i++));
		obj.put("view_opinion_results", arole.getPermission(i++));
		obj.put("save_delete_snapshots", arole.getPermission(i++));
		obj.put("view_use_opinion_prediction", arole.getPermission(i++));
		result.put(obj);

		return result;
	}
}
