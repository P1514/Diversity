package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetPosts {

	private Settings dbc = new Settings();
	private Connection cnlocal;
	private int MAXTOP = 5;

	public GetPosts() {
	}

	public JSONArray getTop(String param, String value) {
		JSONArray result = new JSONArray();
		String[] pre_result = new String[MAXTOP];
		JSONObject obj = new JSONObject();
		String insert = new String();
		int[] topid = new int[MAXTOP];
		String[] params = (param != null) ? param.split(","): null;
		String[] values = (value != null) ? value.split(","): null;
		PreparedStatement query1 = null;
		int n_tops = 0;
		insert = "Select id FROM opinions";
		if (param != null) {
			insert += " where authors_id in (Select id from authors where ";

			for (int i = 0; i < params.length; i++) {
				if (i > 0)
					insert += "&& ";
				if (!values[i].contains("-")) {
					insert += param + "=? ";
				} else {
					insert += params[i] + ">=? && " + params[i] + "<=? ";
				}
			}
			insert+=")";
		}

		insert += " ORDER BY reach DESC LIMIT ?";
		ResultSet rs = null;
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			int rangeindex = 0;
			int i = 0;
			for (i = 1;value != null && i <= values.length; i++) {

				if (!values[i - 1].contains("-")) {
					query1.setString(i + rangeindex, values[i - 1]);
				} else {
					String[] range = values[i - 1].split("-");
					query1.setString(i + rangeindex, range[0]);
					rangeindex++;
					query1.setString(i + rangeindex, range[1]);
				}

			}
			query1.setInt(i + rangeindex, MAXTOP);
			System.out.println(query1);
			rs = query1.executeQuery();

			for (i = 0, rs.next(); rs.next(); i++) {
				topid[i] = rs.getInt("id");// Done so far return id of the top
				n_tops++; // MAXTOP opinion makers of the
				// asked parameters
			}

			insert = "Select name,influence,location,gender,age from authors where id in (Select authors_id from opinions where id = ?)";
			for (i = 0; i < n_tops; i++) {

				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, topid[i]);
				rs = query1.executeQuery();
				rs.next();
				pre_result[i] = rs.getString("name") + ",," + rs.getDouble("influence") + ",,"
						+ rs.getString("location") + ",," + rs.getString("gender") + ",," + rs.getInt("age") + ",,";
				System.out.println(pre_result[i] + "\r\n");
				rs.close();
				query1.close();
			}

			insert = "Select timestamp,polarity,reach,comments from opinions where id = ?";
			for (i = 0; i < n_tops; i++) {

				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, topid[i]);
				rs = query1.executeQuery();
				rs.next();
				pre_result[i] += rs.getDate("timestamp") + ",," + rs.getDouble("polarity") + ",,"
						+ rs.getDouble("reach") + ",," + rs.getInt("comments") + ",,";
				rs.close();
				query1.close();
			}

			insert = "Select message from posts where opinions_id = ?";
			for (i = 0; i < n_tops; i++) {

				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, topid[i]);
				rs = query1.executeQuery();
				rs.next();
				pre_result[i] += rs.getString("message");
				rs.close();
				query1.close();
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
			try {
				obj.put("name", pre_results[0]);
				obj.put("influence", pre_results[1]);
				obj.put("location", pre_results[2]);
				obj.put("gender", pre_results[3]);
				obj.put("age", pre_results[4]);
				obj.put("date", pre_results[5]);
				obj.put("polarity", pre_results[6]);
				obj.put("reach", pre_results[7]);
				obj.put("comments", pre_results[8]);
				obj.put("message", pre_results[9]);
				result.put(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;

	}

	private void dbconnect() {
		try {
			cnlocal = dbc.connlocal();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
