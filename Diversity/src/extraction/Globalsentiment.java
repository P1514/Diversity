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
 * The Class Globalsentiment.
 *
 * @author Uninova - IControl
 */
public class Globalsentiment {

	private Connection cnlocal;

	/**
	 * Class that handles sentiment Requests.
	 */
	public Globalsentiment() {

	}

	/**
	 * Calculates the Sentiment for the pss id's present in the arraylist top5,
	 * and saves it in the database for faster fetching when requested.
	 * <p>
	 * Timespan specifies ammount of year to calculate, String param and values
	 * the filtering wanted, top5 the Arraylist with the id's wanted.
	 * 
	 * @param timespan
	 *            whole number that represent years
	 * @param param
	 *            Example:[Age,Age,Gender]
	 * @param values
	 *            Example:[0-30,30-60,Female]
	 * @param top5
	 *            ArrayList with id's
	 * @throws JSONException
	 *             is case JSON creation fails
	 */
	public void calc_TOPreachglobalsentiment(int timespan /* years */, String param, String values,
			ArrayList<Long> top5) throws JSONException {
		if (top5.isEmpty())
			return;
		PreparedStatement query1 = null;
		try {
			dbconnect();
			String delete = "Delete from reach";
			query1 = cnlocal.prepareStatement(delete);
			query1.execute();
			String result = "";
			query1.close();
			cnlocal.close();

			for (long k : top5) {

				Data.modeldb.put((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false,0,0));
				result += globalsentiment(timespan, param, values, Data.pssdb.get(k).getName(), -1).toString();
				Data.modeldb.remove((long) -1);
			}
			dbconnect();
			result = result.replaceAll("\\]\\[", ",");
			String insert = new String("Insert into " + Settings.lrtable + " values (?)");
			query1 = cnlocal.prepareStatement(insert);
			query1.setString(1, result);
			query1.execute();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
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
	}

	/**
	 * Returns the string in the database with the top reach pss global
	 * sentiment.
	 *
	 * @return String
	 */
	public String Topreachglobalsentiment() {

		String select = "Select * from " + Settings.lrtable;
		PreparedStatement query1 = null;
		ResultSet rs = null;
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(select);
			rs = query1.executeQuery();
			while (rs.next()) {
				String output = rs.getString(1);
				rs.close();
				query1.close();
				cnlocal.close();
				return output;
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
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
		return "";

	}

	/**
	 * Calculates Sentiment over the time for the pss with the id provided,
	 * timespan defines the ammount of years to evaluate, Param and Values are
	 * expected string with filtering values separated by ',' , index are
	 * expected to math from both Strings after split.
	 *
	 * @param timespan
	 *            In years and whole numbers only
	 * @param param
	 *            Example: [Age,Age,Location]
	 * @param values
	 *            Example:[0-30,30-60,Asia]
	 * @param output
	 *            String with filter information
	 * @param id
	 *            PSS id
	 * @return JSONArray with all the values requested
	 * @throws JSONException
	 *             in case creating a JSON fails
	 */
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
	public double globalsentimentby(int month, int year, String param, String value, long id) {

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
				+ Settings.lotable + " WHERE  "+Settings.lotable + "."+Settings.lotable_timestamp+">=? AND " + Settings.lotable + "." + Settings.lotable_id + "=" + Settings.lptable
				+ "." + Settings.lptable_opinion + " AND timestamp>? && timestamp<? && " + Settings.lotable_pss + "=?"
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
			// System.out.println(query1);
			if (gender != null)
				query1.setString(rangeindex++, gender);
			if (location != null)
				query1.setString(rangeindex++, location);
			if (products != null)
				query1.setLong(rangeindex++, Long.valueOf(Data.identifyProduct(products)));
			// System.out.println(query1);
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
			rs.close();
			query1.close();
			cnlocal.close();
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
	 * Calculates Average Sentiment over the time for the pss with the id
	 * provided, timespan defines the ammount of years to evaluate, Param and
	 * Values are expected string with filtering values separated by ',' , index
	 * are expected to math from both Strings after split.
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
	 * @return JSONArray with the value requested
	 * @throws JSONException
	 *             in case creating a JSON fails
	 */
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
		value = value / ((avg != 0) ? avg : 1);
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

	/**
	 * Calculates Polarity Distribution over the time for the pss with the id
	 * provided, Param and Values are expected string with filtering values
	 * separated by ',' , index are expected to math from both Strings after
	 * split, output is the string that is going to be returned referencing the
	 * type of filtering applied.
	 * <p>
	 * Distribution agregates values:
	 * <ul>
	 * <li>0-20 -&gt; '--'</li>
	 * <li>21-40 -&gt; '-'</li>
	 * <li>41-60 -&gt; '0'</li>
	 * <li>61-80 -&gt; '+'</li>
	 * <li>81-100 -&gt; '++'</li>
	 * </ul>
	 *
	 * @param id
	 *            PSS id
	 * @param param
	 *            Example: [Age,Age,Location]
	 * @param value
	 *            Example:[0-30,30-60,Asia]
	 * @param output
	 *            String representing what filtering was applied
	 * @return JSONArray with all the values requested
	 * @throws JSONException
	 *             in case creating a JSON fails
	 */
	public JSONArray getPolarityDistribution(long id, String param, String value, String output) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		if(!Data.modeldb.containsKey(id)){
			obj.put("Op", "ERROR");
			obj.put("Message", "Model not Found");
			result.put(obj);
			return result;
		}
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
				+ (products != null ? "=?" : " in (" + model.getProducts() + ")") + " AND "+Settings.lotable_timestamp+">?) AND " + Settings.lptable_authorid
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
			query1.setLong(rangeindex++, model.getDate());
			if (age != null) {
				query1.setString(rangeindex++, age.split("-")[1]);
				query1.setString(rangeindex++, age.split("-")[0]);
			}

			if (gender != null)
				query1.setString(rangeindex++, gender);
			if (location != null)
				query1.setString(rangeindex++, location);
			// System.out.println(query1);

			// System.out.print(query1 + "\n");
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

		return result;

	}

	private void dbconnect() throws ClassNotFoundException, SQLException {
		cnlocal = Settings.connlocal();
	}
}
