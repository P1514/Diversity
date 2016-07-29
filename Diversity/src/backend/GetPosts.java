package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetPosts {

	private Settings dbc = new Settings();
	private Connection cnlocal;
	private int MAXTOP = 5;

	public GetPosts() {
	}

	public JSONArray getTop(String param, String value, int pss) throws JSONException {
		JSONArray result = new JSONArray();
		String[] pre_result = new String[MAXTOP];
		JSONObject obj = new JSONObject();
		Calendar inputdate = Calendar.getInstance();
		obj.put("Op", "table");
		result.put(obj);
		String insert = new String();
		int[] topid = new int[MAXTOP];
		PreparedStatement query1 = null;
		int n_tops = 0;
		insert = "Select id FROM opinions where (tag_id=?";
		if (param != null) {
			insert += " && timestamp >= ? && timestamp <= ?";
			
			SimpleDateFormat sdf = new SimpleDateFormat("d yyyy MMM", Locale.ENGLISH);
			try {
				inputdate.setTime(sdf.parse("1 " + 
			inputdate.get(Calendar.YEAR) + " " + value));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		insert += ")";

		insert += " ORDER BY reach DESC LIMIT ?";
		ResultSet rs = null;

		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			int rangeindex = 2;
			int i = 0;
			query1.setInt(1, pss);
			if (param != null) {
				Calendar date = Calendar.getInstance();
				if (!date.after(inputdate))
					inputdate.add(Calendar.YEAR, -1);
				query1.setDate(rangeindex, new java.sql.Date(inputdate.getTimeInMillis()));
				inputdate.add(Calendar.MONTH, 1);
				rangeindex++;
				query1.setDate(rangeindex, new java.sql.Date(inputdate.getTimeInMillis()));
				rangeindex++;

			}
			//System.out.print(query1);
			query1.setInt(rangeindex, MAXTOP);
			rs = query1.executeQuery();

			for (i = 0; rs.next(); i++) {
				topid[i] = rs.getInt("id");
				n_tops++;
			}

			insert = "Select name,influence,location,gender,age from authors where id in (Select authors_id from opinions where id = ?)";
			for (i = 0; i < n_tops; i++) {

				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, topid[i]);
				rs = query1.executeQuery();
				rs.next();
				pre_result[i] = topid[i] + ",," + rs.getString("name") + ",," + rs.getDouble("influence") + ",,"
						+ rs.getString("location") + ",," + rs.getString("gender") + ",," + rs.getInt("age") + ",,";
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
			obj.put("Id", pre_results[0]);
			obj.put("Name", pre_results[1]);
			obj.put("Influence", trunc(pre_results[2]));
			obj.put("Location", pre_results[3]);
			obj.put("Gender", pre_results[4]);
			obj.put("Age", pre_results[5]);
			obj.put("Date", pre_results[6]);
			obj.put("Polarity", trunc(pre_results[7]));
			obj.put("Reach", trunc(pre_results[8]));
			obj.put("Comments", pre_results[9]);
			obj.put("Message", pre_results[10]);
			result.put(obj);

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
