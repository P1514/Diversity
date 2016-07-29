package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetAuthors {

	private Settings dbc = new Settings();
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
		insert = "Select * FROM authors where id in (Select authors_id from opinions)";
		ResultSet rs = null;

		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			rs = query1.executeQuery();
			for(;rs.next();){
				obj= new JSONObject();
				obj.put("Name", rs.getString("name"));
				obj.put("Gender", rs.getString("gender"));
				obj.put("Age", rs.getInt("age"));
				obj.put("Location", rs.getString("location"));
				int nposts=rs.getInt("posts");
				obj.put("Nposts", nposts);
				double tmp=rs.getInt("comments")/nposts;
				obj.put("Avgcomms", trunc(String.valueOf(tmp)));
				tmp=rs.getInt("likes")/nposts;
				obj.put("Avglikes", trunc(String.valueOf(tmp)));
				tmp = rs.getInt("views")/nposts;
				obj.put("Avgviews", trunc(String.valueOf(tmp)));
				obj.put("Influence", trunc(String.valueOf(rs.getDouble("influence"))));
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
			cnlocal = dbc.connlocal();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
