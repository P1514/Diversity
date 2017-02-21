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

// TODO: Auto-generated Javadoc
/**
 * The Class GetReach.
 *
 * @author Uninova - IControl
 */
public class GetReach {

	private Connection cnlocal;

	/**
	 * Returns an array list with the nTOP number of pss's with higher reach on
	 * the of the last 12 months.
	 * 
	 * @param nTOP
	 *            - Number of PSS wanted
	 * @return ArrayList of pss id's
	 */
	public ArrayList<Long> getTOPReach(int nTOP) {
		ArrayList<Long> tops = new ArrayList<Long>();
		PreparedStatement query1 = null;
		ResultSet rs = null;

		String select = "Select " + Settings.lotable_pss + " from " + Settings.lotable + " where "
				+ Settings.lotable_pss + " in (Select distinct " + Settings.lmtable_pss + " from " + Settings.lmtable
				+ " where " + Settings.lmtable_archived + "=0) group by " + Settings.lotable_pss + " order by AVG("
				+ Settings.lotable_reach + ") desc limit " + nTOP;

		//System.out.println("HEELOO" + select);
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(select);
			rs = query1.executeQuery();
			while (rs.next()) {
				tops.add(rs.getLong(Settings.lotable_pss));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (query1 != null)
					query1.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return tops;

	}

	/**
	 * Calculates Reach over the time for the pss with the id provided, timespan
	 * defines the ammount of years to evaluate, Param and Values are expected
	 * string with filtering values separated by ',' , index are expected to
	 * math from both Strings after split.
	 * 
	 * 
	 * @param timespan
	 *            In years and whole numbers only
	 * @param param
	 *            Example: [Age,Age,Location]
	 * @param values
	 *            Example:[0-30,30-60,Asia]
	 * @param id
	 *            PSS id
	 * @return JSONArray with all the values requested
	 * @throws JSONException
	 *             in case creating a JSON fails
	 */
	public JSONArray getReach(int timespan /* years */, String param, String values, long id) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		String[] words;
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
		if (param != null) {
			words = values.split(",");
		} else {
			words = new String[1];
			words[0] = "Sentiment";
		}

		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -1);
		int avg = 0;
		for (int month = data.get(Calendar.MONTH); month < timespan * 12 + data.get(Calendar.MONTH); month++) {
			value += globalsentimentby(month % 12, data.get(Calendar.YEAR) + month / 12, param, values, id);
			avg++;
		}
		value = value / ((avg != 0) ? avg : 1);
		String temp;
		temp = String.format("%.2f", value);
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
				+ Settings.lotable + " WHERE "+Settings.lotable_timestamp+">=? AND " + Settings.lotable + "." + Settings.lotable_id + "=" + Settings.lptable
				+ "." + Settings.lptable_opinion + " AND timestamp>? && timestamp<? && " + Settings.lotable_pss
				+ "=? AND (" + Settings.lptable + "." + Settings.lptable_authorid + "=" + Settings.latable + "."
				+ Settings.latable_id;
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
		// System.out.println(insert);
		ResultSet rs = null;
		Double auxcalc = (double) 0;
		Calendar data = new GregorianCalendar(year, month, 1);
		double totalreach = 0;
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			query1.setLong(1, model.getDate());
			query1.setLong(2, data.getTimeInMillis());
			data.add(Calendar.MONTH, 1);
			data.add(Calendar.DAY_OF_MONTH, -1);
			query1.setLong(3, data.getTimeInMillis());
			query1.setLong(4, model.getPSS());
			int rangeindex = 5;
			if (age != null) {
				query1.setString(rangeindex++, age.split("-")[1]);
				query1.setString(rangeindex++, age.split("-")[0]);
			}
			if (gender != null)
				query1.setString(rangeindex++, gender);
			if (location != null)
				query1.setString(rangeindex++, location.substring(0, location.length()));
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
				auxcalc += (double) rs.getDouble(Settings.lotable_reach);
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

	/**
	 * Calculates Reach over the time for the pss with the id provided, timespan
	 * defines the years to evaluate, Param and Values are expected string with
	 * filtering values separated by ',' , index are expected to math from both
	 * Strings after split, output is the string that is going to be returned
	 * referencing the type of filtering applied.
	 *
	 * @param timespan
	 *            whole numbers only
	 * @param param
	 *            Example: [Age,Age,Location]
	 * @param values
	 *            Example:[0-30,30-60,Asia]
	 * @param output
	 *            String representing what filtering was applied
	 * @param id
	 *            PSS id
	 * @return JSONArray with all the values requested
	 * @throws JSONException
	 *             in case creating a JSON fails
	 */
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
				if(obj.getDouble("Value") != (double)0 ) result.put(obj);

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
				+ Settings.lptable + ", " + Settings.lotable + " WHERE "+Settings.lotable_timestamp+">=? AND " + Settings.lotable + "." + Settings.lotable_id
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
		Calendar data = new GregorianCalendar(year, month, 1);
		double totalreach = 0;
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			query1.setLong(1, model.getDate());
			query1.setLong(2, data.getTimeInMillis());
			data.add(Calendar.MONTH, 1);
			data.add(Calendar.DAY_OF_MONTH, -1);
			query1.setLong(3, data.getTimeInMillis());
			query1.setLong(4, model.getPSS());
			int rangeindex = 5;
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
