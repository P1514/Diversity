package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetPopulation {

	private Settings dbc = new Settings();
	private Connection cnlocal;

	public GetPopulation() {
	}

	public JSONArray getAll(String param, int pss) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		String insert = "";
		String params = "";
		if (param.equals("gender")) {
			obj.put("Op", "gengraph");
			obj.put("Param", "Gender");
			params = Settings.genders;
			//insert = "Select distinct gender FROM authors where id in (Select authors_id from posts where opinions_id in (Select id from opinions where tag_id=?)) ORDER BY gender DESC";
		} else if (param.equals("age")) {
			obj.put("Op", "agegraph");
			obj.put("Param", "Age");
			params = Settings.ages;
		} else if (param.equals("location")) {
			obj.put("Op", "locgraph");
			obj.put("Param", "Location");
			params= Settings.locations;
			//insert = "Select distinct location FROM authors where id in (Select authors_id from posts where opinions_id in (Select id from opinions where tag_id=?)) ORDER BY location ASC";
		}
		result.put(obj);
		PreparedStatement query1 = null;
		ResultSet rs = null;
		try {
			dbconnect();
			if (!params.contains("-")) {
				/*query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, pss);
				rs = query1.executeQuery();
				for (; rs.next();) {
					params += rs.getString(param) + ",,";
				}
				rs.close();
				query1.close();*/
				String[] out_params = params.split(",,");
				insert = "Select count(*) from authors where " + param + "=? && id in (Select authors_id from posts where opinions_id in (Select id from opinions where tag_id=?))";
				for (int i = 0; i < out_params.length; i++) {
					query1 = cnlocal.prepareStatement(insert);
					query1.setString(1, out_params[i]);
					query1.setInt(2, pss);
					rs = query1.executeQuery();
					rs.next();
					obj = new JSONObject();
					obj.put("Param", out_params[i]);
					obj.put("Value", rs.getInt("count(*)"));
					result.put(obj);
					rs.close();
					query1.close();
				}
				return result;
			}else{
				String[] out_params = params.split(",,|-");
				insert = "Select count(*) from authors where " + param + ">=? && "+param+"<=? && id in (Select authors_id from posts where opinions_id in (Select id from opinions where tag_id=?))";
				for (int i = 0; i < out_params.length; i++) {
					System.out.println(out_params[i]);
					query1 = cnlocal.prepareStatement(insert);
					query1.setString(1, out_params[i]);
					i++;
					query1.setString(2, out_params[i]);
					query1.setInt(3, pss);
					rs = query1.executeQuery();
					rs.next();
					obj = new JSONObject();
					obj.put("Param", out_params[i-1]+"-"+out_params[i]);
					obj.put("Value", rs.getInt("count(*)"));
					result.put(obj);
					rs.close();
					query1.close();
				}
				return result;
				
				
				
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

	private void dbconnect() {
		try {
			cnlocal = dbc.connlocal();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
