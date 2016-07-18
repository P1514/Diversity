package resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import org.json.*;

public class Backend {
	private Settings dbc = new Settings();
	private Connection cnlocal;

	public Backend() {
		try {
			cnlocal = dbc.connlocal();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public JSONArray globalsentiment(int timespan /* years */, int start /* month */, String param, int n_values, String values) {
		JSONArray result = new JSONArray();
		JSONObject obj;
				
		String[] time = new String[12];
		time[0] = "JANUARY";
		time[1] = "FEBRUARY";
		time[2] = "MARCH";
		time[3] = "APRIL";
		time[4] = "MAY";
		time[5] = "JUNE";
		time[6] = "JULY";
		time[7] = "AUGUST";
		time[8] = "SEPTEMBER";
		time[9] = "OCTOBER";
		time[10] = "NOVEMBER";
		time[11] = "DECEMBER";
		for (int i = start; i < timespan * 12 + start; i++) {
			try {
				obj = new JSONObject();
				obj.put("month", time[i % 12]);
				//for(int ii=0;ii<n_values;ii++)
				obj.put("sentiment", globalsentimentby(i, null, null));
				result.put(obj);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	private double sentimentby(int minage, int maxage, String param, String value) {
		double result = (double) 0;
		String insert;
		if(param!=null){
		insert= "Select polarity FROM posts WHERE author_id in (Select id from authors WHERE (AGE > ? AND AGE < ? AND "
				+ param + "=?))";}else{
					insert = "Select polarity FROM posts WHERE author_id in (Select id from authors WHERE (AGE > ? AND AGE < ?";
				}
		PreparedStatement query1 = null;
		ResultSet rs = null;
		Double auxcalc = (double) 0;
		int i;
		try {
			query1 = cnlocal.prepareStatement(insert);
			query1.setInt(1, minage);
			query1.setInt(2, maxage);
			if(param!=null)query1.setString(3, value);
			rs = query1.executeQuery();

			for (i = 0; rs.next(); i++) {
				auxcalc += (double) rs.getInt("polarity");
			}
			result = auxcalc / (i == 0 ? 1 : i);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    try { if (rs != null) rs.close(); } catch (Exception e) {};
		    try { if (query1 != null) query1.close(); } catch (Exception e) {};
		    try { if (cnlocal != null) cnlocal.close(); } catch (Exception e) {};
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

	private double globalsentimentby(int month, String param, String value) {
		double result = (double) 0;
		String insert;
		PreparedStatement query1;
		if (param == null) {
			insert = "Select polarity,reach FROM opinions WHERE timestamp>? && timestamp<?";
		} else {
			insert = "Select polarity,reach FROM opinions WHERE timestamp>? && timestamp<? && " + param + "=?";
		}
		ResultSet rs = null;
		Double auxcalc = (double) 0;
		int i = 0;
		month -= 1;
		int year = month / 12;
		month = month % 12;
		Calendar data = new GregorianCalendar(2016 + year, month, 1);
		try {
			query1 = cnlocal.prepareStatement(insert);
			query1.setDate(1, new java.sql.Date(data.getTimeInMillis()));
			data.add(Calendar.MONTH, 1);
			query1.setDate(2, new java.sql.Date(data.getTimeInMillis()));
			if (param != null)
				query1.setString(3, value);
			rs = query1.executeQuery();
			System.out.println(query1);

			double totalreach = 0;
			while (rs.next()) {

				auxcalc += (double) rs.getDouble("polarity") * rs.getDouble("reach");
				totalreach += rs.getDouble("reach");
			}

			result = auxcalc / (totalreach == 0 ? 1 : totalreach);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return result;

	}
}
