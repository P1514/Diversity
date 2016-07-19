package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SentimentChart {
	
	private Settings dbc = new Settings();
	private Connection cnlocal;
	
	public SentimentChart(){
		
	}

	public JSONArray chartrequest() {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		try {
			obj.put("age_range", "0-30");
			obj.put("male_avg", sentimentby(0, 30, "GENDER", "MALE"));
			obj.put("female_avg", sentimentby(0, 30, "GENDER", "FEMALE"));
			obj.put("east_avg", sentimentby(0, 30, "LOCATION", "EAST"));
			obj.put("west_avg", sentimentby(0, 30, "LOCATION", "WEST"));
			result.put(obj);
			obj = new JSONObject();
			obj.put("age_range", "30-60");
			obj.put("male_avg", sentimentby(30, 60, "GENDER", "MALE"));
			obj.put("female_avg", sentimentby(30, 60, "GENDER", "FEMALE"));
			obj.put("east_avg", sentimentby(30, 60, "LOCATION", "EAST"));
			obj.put("west_avg", sentimentby(30, 60, "LOCATION", "WEST"));
			result.put(obj);
			obj = new JSONObject();
			obj.put("age_range", "60+");
			obj.put("male_avg", sentimentby(60, 90, "GENDER", "MALE"));
			obj.put("female_avg", sentimentby(60, 90, "GENDER", "FEMALE"));
			obj.put("east_avg", sentimentby(60, 90, "LOCATION", "EAST"));
			obj.put("west_avg", sentimentby(60, 90, "LOCATION", "WEST"));
			result.put(obj);
			return result;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	

	private double sentimentby(int minage, int maxage, String param, String value) {
		double result = (double) 0;
		String insert;
		if (param != "null") {
			insert = "Select polarity FROM posts WHERE author_id in (Select id from authors WHERE (AGE > ? AND AGE < ? AND "
					+ param + "=?))";
		} else {
			insert = "Select polarity FROM posts WHERE author_id in (Select id from authors WHERE (AGE > ? AND AGE < ?";
		}
		PreparedStatement query1 = null;
		ResultSet rs = null;
		Double auxcalc = (double) 0;
		int i;
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			query1.setInt(1, minage);
			query1.setInt(2, maxage);
			if (param != "null")
				query1.setString(3, value);
			rs = query1.executeQuery();

			for (i = 0; rs.next(); i++) {
				auxcalc += (double) rs.getInt("polarity");
			}
			result = auxcalc / (i == 0 ? 1 : i);

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
		String temp;
		temp = String.format("%.2f", result);
		try {
			System.out.println("ERROR 1 " + temp);
			result = Double.valueOf(temp);
		} catch (Exception e) {
			temp = temp.replaceAll(",", ".");
			System.out.println("ERROR 2 " + temp);
			result = Double.parseDouble(temp);
		}

		System.out.println(result);
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
