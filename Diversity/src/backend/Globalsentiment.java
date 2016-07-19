package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Globalsentiment {

	private Settings dbc = new Settings();
	private Connection cnlocal;

	public Globalsentiment() {
	}

	public JSONArray globalsentiment(int timespan /* years */, int start /* month */, String param, String values) {
		JSONArray result = new JSONArray();
		JSONObject obj;
		String[] words;

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
		if (param != null) {
			words = values.split(",");
		} else {
			words = new String[1];
			words[0] = "sentiment";
		}

		for (int i = start; i < timespan * 12 + start; i++) {
			try {
				obj = new JSONObject();
				obj.put("month", time[i % 12]);
				for (int ii = 0; ii < words.length; ii++)
					obj.put(words[ii], globalsentimentby(i, param, words[ii]));
				result.put(obj);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	private double globalsentimentby(int month, String param, String value) {

		double result = (double) 0;
		String insert;
		String[] values = new String[2];
		PreparedStatement query1 = null;
		if (param == null) {
			insert = "Select polarity,reach FROM opinions WHERE timestamp>? && timestamp<? ";
		} else {
			if (!value.contains("-")) {
				insert = "Select polarity,reach FROM opinions WHERE timestamp>? && timestamp<? &&"
						+ " authors_id in (Select id from authors where " + param + "=?)";
			} else {
				values = value.split("-");
				insert = "Select polarity,reach FROM opinions WHERE timestamp>? && timestamp<? &&"
						+ " authors_id in (Select id from authors where " + param + ">=? && " + param + "<=?)";
			}
		}
		ResultSet rs = null;
		Double auxcalc = (double) 0;
		month -= 1;
		int year = month / 12;
		month = month % 12;
		Calendar data = new GregorianCalendar(2016 + year, month, 1);
		double totalreach = 0;
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			query1.setDate(1, new java.sql.Date(data.getTimeInMillis()));
			data.add(Calendar.MONTH, 1);
			query1.setDate(2, new java.sql.Date(data.getTimeInMillis()));
			if (param != null) {
				if (!value.contains("-")) {
					query1.setString(3, value);
				} else {
					query1.setString(3, values[0]);
					query1.setString(4, values[1]);
				}
			}
			rs = query1.executeQuery();

			
			while (rs.next()) {
				auxcalc += (double) rs.getDouble("polarity") * rs.getDouble("reach");
				totalreach += rs.getDouble("reach");
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
		result = auxcalc / (totalreach == 0 ? 1 : totalreach);
		String temp;
		temp = String.format("%.2f", result);
		try {
			result = Double.valueOf(temp);
		} catch (Exception e) {
			temp = temp.replaceAll(",", ".");
			result = Double.parseDouble(temp);
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
