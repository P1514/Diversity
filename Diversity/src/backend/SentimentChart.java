package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SentimentChart {

	private Settings dbc = new Settings();
	private Connection cnlocal;

	public SentimentChart() {

	}

	public JSONArray chartrequest(String param, String value) {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		String[] params = (param != null) ? param.split(",") : null;
		String[] values = (value != null) ? value.split(",") : null;
		String[] agerange = new String[3];
		String[] locs = new String[3];
		String[] genders = new String[3];
		String[] outparams = new String[4];
		int nages = 0, ngenders = 0, nlocs = 0;
		for (int i = 0; i < params.length; i++) {

			if (params[i].contains("Age")) {
				agerange[nages] = values[i];
				nages++;
				continue;
			}
			if (params[i].contains("Gender")) {
				genders[ngenders] = values[i];
				ngenders++;
				continue;
			}
			if (params[i].contains("Location")) {
				locs[nlocs] = values[i];
				nlocs++;
				continue;
			}
		}

		if (nlocs == 1 && ngenders == 2) {
			outparams[0] = "Male";
			outparams[1] = "Female";
		} else {
			if (nlocs == 2 && ngenders == 1) {
				outparams[0] = "East";
				outparams[1] = "West";
			} else {
				if (nlocs == 1 && ngenders == 1) {
					outparams[0] = "Sentiment";
				} else {
					outparams[0] = "Global";

				}
			}
		}

		try {
			obj.put("Op", "graph");
			result.put(obj);

			if (params[0].contains("Global")) {
				agerange[0] = "0-30";
				agerange[1] = "31-60";
				agerange[2] = "61-90";
				nages = 3;
				ngenders = 1;
				nlocs = 1;
				genders[0] = null;
				locs[0] = null;
			}

			for (int age = 0; age < nages; age++) {

				int temp = 0;
				for (int gender = 0; gender < ngenders; gender++) {
					for (int loc = 0; loc < nlocs; loc++) {
						obj = new JSONObject();
						obj.put("Age", agerange[age]);
						obj.put("Param", outparams[temp]);
						obj.put("Value", sentimentby(agerange[age], genders[gender], locs[loc]));
						result.put(obj);
						System.out.println(temp + " " + obj + "\r\n");
						temp++;

					}
				}
			}
			System.out.println(result);
			return result;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private double sentimentby(String age, String gender, String location) {
		String[] agerange = age.split("-");
		int minage = Integer.parseInt(agerange[0]);
		int maxage = Integer.parseInt(agerange[1]);
		double result = (double) 0;
		String insert;
		if (gender != null) {
			insert = "Select polarity FROM posts WHERE authors_id in (Select id from authors WHERE (AGE >= ? AND AGE <= ? AND"
					+ " GENDER = ? AND LOCATION = ?))";
		} else {
			insert = "Select polarity FROM posts WHERE authors_id in (Select id from authors WHERE (AGE >= ? AND AGE <= ?))";
		}
		PreparedStatement query1 = null;
		ResultSet rs = null;
		Double auxcalc = (double) 0;
		int i = 0;
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			query1.setInt(1, minage);
			query1.setInt(2, maxage);
			if (gender != null) {
				query1.setString(3, gender);
				query1.setString(4, location);
			}
			System.out.println(query1);
			rs = query1.executeQuery();

			for (i = 0; rs.next(); i++) {
				auxcalc += (double) rs.getInt("polarity");
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
		result = auxcalc / (i == 0 ? 1 : i);
		String temp;
		temp = String.format("%.2f", result);
		try {
			result = Double.valueOf(temp);
		} catch (Exception e) {
			temp = temp.replaceAll(",", ".");
			result = Double.parseDouble(temp);
		}

		System.out.println(result);
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
