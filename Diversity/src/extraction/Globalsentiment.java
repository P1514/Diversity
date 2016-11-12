package extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Data;
import general.Model;
import general.Settings;

public class Globalsentiment {

	private Connection cnlocal;

	public Globalsentiment() {
	}

	public void calc_TOPreachglobalsentiment(int timespan /* years */, String param, String values,
			ArrayList<Long> top5) throws JSONException {
		try {
			dbconnect();
			String delete = "Delete from reach";
			PreparedStatement query1 = cnlocal.prepareStatement(delete);
			query1.execute();
			String result = "";
			cnlocal.close();

			for (long k : top5) {

				Data.modeldb.put((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false));
				result += globalsentiment(timespan, param, values, Data.pssdb.get(k).getName(), -1).toString();
				Data.modeldb.remove((long) -1);
			}
			dbconnect();
			result = result.replaceAll("\\]\\[", ",");
			String insert = "Insert into " + Settings.lrtable + " values (?)";
			query1 = cnlocal.prepareStatement(insert);
			query1.setString(1, result);
			query1.execute();
			cnlocal.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String Topreachglobalsentiment() {

		String select = "Select * from " + Settings.lrtable;
		ArrayList<String> result = new ArrayList<String>();
		try {
			dbconnect();
			PreparedStatement query1 = cnlocal.prepareStatement(select);
			ResultSet rs = query1.executeQuery();
			while (rs.next()) {
				return rs.getString(1);
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	public JSONArray globalsentiment(int timespan /* years */, String param, String values, String output, long id)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

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
		obj = new JSONObject();
		obj.put("Filter", output);
		result.put(obj);

		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -1);

		for (int month = data.get(Calendar.MONTH); month < timespan * 12 + data.get(Calendar.MONTH); month++) {
			try {
				obj = new JSONObject();
				obj.put("Month", time[month % 12]);
				obj.put("Value",
						globalsentimentby(month % 12, data.get(Calendar.YEAR) + month / 12, param, values, id));
				result.put(obj);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	// TODO change this do open and close opinions and check things inside
	private double globalsentimentby(int month, int year, String param, String value, long id) {

		double result = (double) 0;
		Model model = Data.modeldb.get(id);
		String insert;
		String gender = null;
		String location = null;
		String age = null;
		String products = null;
		String[] params;
		String[] values;
		if (param != null) {

			params = param.split(",");
			values = value.split(",");
			for (int i = 0; i < params.length; i++) {
				switch (params[i]) {
				case "Age":
					if (!values[i].equals("All"))
						age = values[i];
					break;

				case "Gender":
					if (!values[i].equals("All"))
						gender = values[i];
					break;
				case "Location":
					if (!values[i].equals("All"))
						location = values[i];
					break;
				case "Product":
					if (!values[i].equals("All"))
						products = values[i];
					break;
				}
			}
		}
		PreparedStatement query1 = null;
		insert = "SELECT " + Settings.lptable + "." + Settings.lptable_polarity + ", " + Settings.lotable + "."
				+ Settings.lotable_reach + " FROM " + Settings.latable + "," + Settings.lptable + ", "
				+ Settings.lotable + " WHERE  " + Settings.lotable + "." + Settings.lotable_id + "=" + Settings.lptable
				+ "." + Settings.lptable_opinion + " AND timestamp>? && timestamp<? && " + Settings.lotable_pss + "=?"
				// TODO
				// Check
				// if
				// this
				// works
				+ " AND (" + Settings.lptable + "." + Settings.lptable_authorid + "=" + Settings.latable + "."
				+ Settings.latable_id;
		if (age != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_age + "<=? AND " + Settings.latable + "."
					+ Settings.latable_age + ">?";
		if (gender != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_gender + "=?";
		if (location != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_location + "=?";
		if (products != null) {
			if (products.equals("-1")) {
				insert += " AND " + Settings.lotable_product + " in (" + model.getProducts() + ")";
			} else {
				insert += " AND " + Settings.lotable_product + "=?";
			}
		} else {

		}
		insert += ")";

		// System.out.println(insert);
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
			query1.setLong(3, model.getPSS());
			int rangeindex = 4;
			if (age != null) {
				query1.setString(rangeindex++, age.split("-")[1]);
				query1.setString(rangeindex++, age.split("-")[0]);
			}
			//System.out.println(query1);
			if (gender != null)
				query1.setString(rangeindex++, gender);
			if (location != null)
				query1.setString(rangeindex++, location);
			if (products != null)
				query1.setLong(rangeindex++, Long.valueOf(Data.identifyProduct(products)));
			//System.out.println(query1);
			/*
			 * if (param != null) { if (!value.contains("-")) {
			 * query1.setString(4, value); } else { query1.setString(4,
			 * values[0]); query1.setString(5, values[1]); } }
			 */
			// System.out.println(query1);
			rs = query1.executeQuery();

			while (rs.next()) {
				auxcalc += (double) rs.getDouble(Settings.lptable_polarity) * rs.getDouble(Settings.lotable_reach);
				totalreach += rs.getDouble(Settings.lotable_reach);
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

	public JSONArray getAvgSentiment(int timespan /* years */, String param, String values, long id)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		double value = 0;

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

		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -1);
		int avg = 0;
		for (int month = data.get(Calendar.MONTH); month < timespan * 12 + data.get(Calendar.MONTH); month++) {
			value += globalsentimentby(month % 12, data.get(Calendar.YEAR) + month / 12, param, values, id);
			avg++;
		}
		value = value / avg;
		String temp;
		temp = String.format("%.0f", value);
		try {
			value = Double.valueOf(temp);
		} catch (Exception e) {
			temp = temp.replaceAll(",", ".");
			value = Double.parseDouble(temp);
		}
		obj.put("Param", "Global");
		obj.put("Value", value);
		result.put(obj);

		return result;
	}

	public JSONArray getPolarityDistribution(long id, String param, String value, String output) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		String[] params = null;
		String[] values = null;
		String gender = null;
		String location = null;
		String age = null;
		String products = null;
		if (param != null) {

			params = param.split(",");
			values = value.split(",");
			for (int i = 0; i < params.length; i++) {
				switch (params[i]) {
				case "Age":
					if (!values[i].equals("All"))
						age = values[i];
					break;
				case "Gender":
					if (!values[i].equals("All"))
						gender = values[i];
					break;
				case "Location":
					if (!values[i].equals("All"))
						location = values[i];
					break;
				case "Product":
					if (!values[i].equals("All"))
						products = values[i];
					break;
				}
			}
		}
		PreparedStatement query1 = null;
		Model model = Data.modeldb.get(id);
		ResultSet rs = null;
		obj = new JSONObject();
		obj.put("Filter", output);
		result.put(obj);

		String query = "select sum(case when (" + Settings.lptable_polarity + " <=20) then 1 else 0 end) '--',"
				+ "	sum(case when (" + Settings.lptable_polarity + " >20 AND " + Settings.lptable_polarity
				+ "<=40) then 1 else 0 end) '-'," + " sum(case when (" + Settings.lptable_polarity + " >40 AND "
				+ Settings.lptable_polarity + "<=60) then 1 else 0 end) '0'," + " sum(case when ("
				+ Settings.lptable_polarity + " >60 AND " + Settings.lptable_polarity + "<=80) then 1 else 0 end) '+',"
				+ " sum(case when (" + Settings.lptable_polarity + " >80 AND " + Settings.lptable_polarity
				+ "<=100) then 1 else 0 end) '++' " + "from " + Settings.lptable + " where " + Settings.lptable_opinion
				+ " in (Select " + Settings.lotable_id + " from " + Settings.lotable + " where " + Settings.lotable_pss
				+ "=?" + " AND " + Settings.lotable_product
				+ (products != null ? "=?" : " in (" + model.getProducts() + ")") + ") AND " + Settings.lptable_authorid
				+ " in (Select " + Settings.latable_id + " from " + Settings.latable;
		if (age != null || gender != null || location != null)
			query += " where 1=1 ";
		if (age != null)
			query += " AND " + Settings.latable_age + "<=? AND " + Settings.latable_age + ">?";
		if (gender != null)
			query += " AND " + Settings.latable_gender + "=?";
		if (location != null)
			query += " AND " + Settings.latable_location + "=?";

		query += ")";

		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(query);
			query1.setLong(1, model.getPSS());
			int rangeindex = 2;
			if (products != null) {
				query1.setLong(rangeindex++, Long.valueOf(Data.identifyProduct(products)));
			}
			if (age != null) {
				query1.setString(rangeindex++, age.split("-")[1]);
				query1.setString(rangeindex++, age.split("-")[0]);
			}
			
			if (gender != null)
				query1.setString(rangeindex++, gender);
			if (location != null)
				query1.setString(rangeindex++, location);
			// System.out.println(query1);

			System.out.print(query1 + "\n");
			rs = query1.executeQuery();
			rs.next();

			obj = new JSONObject();
			obj.put("Param", "--");
			obj.put("Value", rs.getInt("--"));
			result.put(obj);
			obj = new JSONObject();
			obj.put("Param", "-");
			obj.put("Value", rs.getInt("-"));
			result.put(obj);
			obj = new JSONObject();
			obj.put("Param", "0");
			obj.put("Value", rs.getInt("0"));
			result.put(obj);
			obj = new JSONObject();
			obj.put("Param", "+");
			obj.put("Value", rs.getInt("+"));
			result.put(obj);
			obj = new JSONObject();
			obj.put("Param", "++");
			obj.put("Value", rs.getInt("++"));
			result.put(obj);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public JSONArray globalreach(int timespan /* years */, String param, String values, String output, long id)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

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
		obj = new JSONObject();
		obj.put("Filter", output);
		result.put(obj);

		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -1);

		for (int month = data.get(Calendar.MONTH); month < timespan * 12 + data.get(Calendar.MONTH); month++) {
			try {
				obj = new JSONObject();
				obj.put("Month", time[month % 12]);
				obj.put("Value", globalreachby(month % 12, data.get(Calendar.YEAR) + month / 12, param, values, id));
				result.put(obj);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	// TODO change this do open and close opinions and check things inside
	private double globalreachby(int month, int year, String param, String value, long id) {

		double result = (double) 0;
		Model model = Data.modeldb.get(id);
		String insert;
		String gender = null;
		String age = null;
		String location = null;
		String[] params = null;
		String products = null;
		String[] values = null;
		if (param != null) {

			params = param.split(",");
			values = value.split(",");
			for (int i = 0; i < params.length; i++) {
				switch (params[i]) {
				case "Age":
					if (!values[i].equals("All"))
						age = values[i];
					break;

				case "Gender":
					if (!values[i].equals("All"))
						gender = values[i];
					break;
				case "Location":
					if (!values[i].equals("All"))
						location = values[i];
					break;
				case "Product":
					if (!values[i].equals("All"))
						products = values[i];
					break;
				}
			}
		}
		PreparedStatement query1 = null;
		insert = "SELECT " + Settings.lotable + "." + Settings.lotable_reach + " FROM " + Settings.latable + ","
				+ Settings.lptable + ", " + Settings.lotable + " WHERE  " + Settings.lotable + "." + Settings.lotable_id
				+ "=" + Settings.lptable + "." + Settings.lptable_opinion + " AND timestamp>? && timestamp<? && "
				+ Settings.lotable_pss + "=? " + "AND (" + Settings.lptable + "." + Settings.lptable_authorid + "="
				+ Settings.latable + "." + Settings.latable_id;
		if (age != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_age + "<=? AND " + Settings.latable + "."
					+ Settings.latable_age + ">?";
		if (gender != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_gender + "=?";
		if (location != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_location + "=?";
		if (products != null) {
			insert += " AND " + Settings.lotable_product + "=?";
		} else {
			insert += " AND " + Settings.lotable_product + " in (" + model.getProducts() + ")";
		}
		insert += ")";
		/*
		 * if (param != null) { if (!value.contains("-")) { insert +=
		 * " && authors_id in (Select id from authors where " + param + "=?)"; }
		 * else { values = value.split("-"); insert +=
		 * " && authors_id in (Select id from authors where " + param +
		 * ">=? && " + param + "<=?)"; } }
		 */
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
			query1.setLong(3, model.getPSS());
			int rangeindex = 4;
			if (age != null) {
				query1.setString(rangeindex++, age.split("-")[1]);
				query1.setString(rangeindex++, age.split("-")[0]);
			}
			if (gender != null)
				query1.setString(rangeindex++, gender);
			if (location != null)
				query1.setString(rangeindex++, location);
			if (products != null)
				query1.setLong(rangeindex++, Data.identifyProduct(products));

			// System.out.println(query1);
			rs = query1.executeQuery();

			while (rs.next()) {
				auxcalc += rs.getDouble(Settings.lotable_reach);
				totalreach++;
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
