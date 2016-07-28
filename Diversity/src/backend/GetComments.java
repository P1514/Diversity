package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetComments {

	private Settings dbc = new Settings();
	private Connection cnlocal;

	public GetComments() {
	}

	public JSONArray getAll(String param, String value) throws JSONException {
		JSONArray result = new JSONArray();
		String[] pre_result = new String[50];
		JSONObject obj = new JSONObject();
		obj.put("Op", "comments");
		result.put(obj);
		String insert = new String();
		int[] topid = new int[50];
		String[] params = (param != null) ? param.split(",") : null;
		String[] values = (value != null) ? value.split(",") : null;
		PreparedStatement query1 = null;
		int n_tops = 0;
		insert = "Select id from posts where opinions_id=? ORDER BY id ASC";
		ResultSet rs = null;

		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			query1.setString(1, values[0]);
			System.out.print(query1);
			rs = query1.executeQuery();

			for (int i = 0; rs.next(); i++) {
				topid[i] = rs.getInt("id");
				n_tops++;
			}

			insert = "Select name,influence,location,gender,age from authors where id in (Select authors_id from posts where id = ?)";
			for (int i = 0; i < n_tops; i++) {

				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, topid[i]);
				rs = query1.executeQuery();
				rs.next();
				pre_result[i] = rs.getString("name") + ",," + rs.getDouble("influence") + ",,"
						+ rs.getString("location") + ",," + rs.getString("gender") + ",," + rs.getInt("age") + ",,";
				rs.close();
				query1.close();
			}

			insert = "Select polarity from posts where id = ?";
			for (int i = 0; i < n_tops; i++) {

				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, topid[i]);
				rs = query1.executeQuery();
				rs.next();
				pre_result[i] += rs.getDouble("polarity") + ",,";
				rs.close();
				query1.close();
			}

			insert = "Select message from posts where id = ?";
			for (int i = 0; i < n_tops; i++) {

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
			obj.put("Name", pre_results[0]);
			obj.put("Influence", trunc(pre_results[1]));
			obj.put("Location", pre_results[2]);
			obj.put("Gender", pre_results[3]);
			obj.put("Age", pre_results[4]);
			obj.put("Polarity", trunc(pre_results[5]));
			obj.put("Message", pre_results[6]);
			result.put(obj);

		}
		System.out.print(result);

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
