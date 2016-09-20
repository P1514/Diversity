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

import importDB.Data;
import importDB.Model;

public class Globalsentiment {

	private Connection cnlocal;

	public Globalsentiment() {
	}

	public JSONArray globalsentiment(int timespan /* years */, String param, String values, long id)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		String[] words;
		obj.put("Op", "graph");
		result.put(obj);

		String[] time = new String[12];
		time[0] = "JAN";
		time[1] = "FEB";
		time[2] = "MAR";
		time[3] = "APR";
		time[4] = "MAY";
		time[5] = "JUN";
		time[6] = "JUL";
		time[7] = "AUG";
		time[8] = "SEP";
		time[9] = "OCT";
		time[10] = "NOV";
		time[11] = "DEC";
		if (param != null) {
			words = values.split(",");
		} else {
			words = new String[1];
			words[0] = "Sentiment";
		}

		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -1);

		for (int month = data.get(Calendar.MONTH); month < timespan * 12 + data.get(Calendar.MONTH); month++) {
			try {
				obj = new JSONObject();
				obj.put("Month", time[month % 12]);
				for (int ii = 0; ii < words.length; ii++)
					obj.put(words[ii],
							globalsentimentby(month % 12, data.get(Calendar.YEAR) + month / 12, param, words[ii], id));
				result.put(obj);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	private double globalsentimentby(int month, int year, String param, String value, long id) {

		double result = (double) 0;
		Model model = Data.modeldb.get(id);
		String insert;
		String[] values = new String[2];
		PreparedStatement query1 = null;
		insert = "Select polarity,reach FROM opinions WHERE timestamp>? && timestamp<? && " + Settings.lotable_pss
				+ "=? AND " + Settings.lotable_product + (model.getProducts() ? "!=0" : "=0");
		if (param != null) {
			if (!value.contains("-")) {
				insert += " && authors_id in (Select id from authors where " + param + "=?)";
			} else {
				values = value.split("-");
				insert += " && authors_id in (Select id from authors where " + param + ">=? && " + param + "<=?)";
			}
		}

		ResultSet rs = null;
		Double auxcalc = (double) 0;
		month -= 1;
		Calendar data = new GregorianCalendar(year, month, 1);
		double totalreach = 0;
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			query1.setDate(1, new java.sql.Date(data.getTimeInMillis()));
			data.add(Calendar.MONTH, 1);
			data.add(Calendar.DAY_OF_MONTH, -1);
			query1.setDate(2, new java.sql.Date(data.getTimeInMillis()));
			query1.setString(3, model.getPSS());
			if (param != null) {
				if (!value.contains("-")) {
					query1.setString(4, value);
				} else {
					query1.setString(4, values[0]);
					query1.setString(5, values[1]);
				}
			}
			// System.out.println(query1);
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

	private void dbconnect() throws ClassNotFoundException, SQLException {
		cnlocal = Settings.connlocal();
	}
}
