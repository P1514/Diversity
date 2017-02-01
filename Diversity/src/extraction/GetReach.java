package extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Data;
import general.Model;
import general.Settings;

/**
 * The Class GetReach.
 *
 * @author Uninova - IControl
 */
public class GetReach {

	private Connection cnlocal;
	private static final Logger LOGGER = Logger.getLogger(Data.class.getName());
	private String[] time = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

	/**
	 * Returns an array list with the nTOP number of pss's with higher reach on
	 * the of the last 12 months.
	 * 
	 * @param nTOP
	 *            - Number of PSS wanted
	 * @return ArrayList of pss id's
	 */
	public List<Long> getTOPReach(int nTOP) {
		ArrayList<Long> tops = new ArrayList<>();

		String select = "Select " + Settings.lotable_pss + " from " + Settings.lotable + " where "
				+ Settings.lotable_pss + " in (Select distinct " + Settings.lmtable_pss + " from " + Settings.lmtable
				+ " where " + Settings.lmtable_archived + "=0) group by " + Settings.lotable_pss + " order by AVG("
				+ Settings.lotable_reach + ") desc limit " + nTOP;

		try {
			dbconnect();
			try (PreparedStatement query1 = cnlocal.prepareStatement(select); ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					tops.add(rs.getLong(Settings.lotable_pss));
				}
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			return new ArrayList<>();
		} finally {
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "ERROR", e);
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
	public JSONArray getReach(int timespan /* years */ , String param, String values, long id) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		double value = 0;

		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -timespan);
		int avg = 0;
		for (int month = data.get(Calendar.MONTH); month < timespan * 12 + data.get(Calendar.MONTH); month++) {
			value += globalsentimentby(month % 12, data.get(Calendar.YEAR) + month / 12, param, values, id);
			avg++;
		}
		value = value / ((avg != 0) ? avg : 1);
		String temp;
		temp = String.format("%.2f", value);
		try {
			value = Double.valueOf(temp.replace(",", "."));
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "ERROR", e);
		}
		obj.put("Param", "Global");
		obj.put("Value", value);
		result.put(obj);

		return result;
	}

	private double globalsentimentby(int month, int year, String param, String value, long id) {

		Model model = Data.getmodel(id);
		String insert;
		parameters par = split_params(param, value);
		insert = "SELECT " + Settings.lptable + "." + Settings.lptable_polarity + ", " + Settings.lotable + "."
				+ Settings.lotable_reach + " FROM " + Settings.latable + "," + Settings.lptable + ", "
				+ Settings.lotable + " WHERE " + Settings.lotable_timestamp + ">=? AND " + Settings.lotable + "."
				+ Settings.lotable_id + "=" + Settings.lptable + "." + Settings.lptable_opinion
				+ " AND timestamp>? && timestamp<? && " + Settings.lotable_pss + "=? AND (" + Settings.lptable + "."
				+ Settings.lptable_authorid + "=" + Settings.latable + "." + Settings.latable_id;
		return calc_global("reach", insert, par, month, model, year);
	}

	protected double calc_global(String type, String insert, parameters par, int month, Model model, int year) {
		avg result = new avg();
		if (par.age != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_age + "<=? AND " + Settings.latable + "."
					+ Settings.latable_age + ">?";
		if (par.gender != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_gender + "=?";
		if (par.location != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_location + "=?";
		if (par.products != null) {
			if (par.products.equals("-1")) {
				insert += " AND " + Settings.lotable_product + " in (" + model.getProducts() + ")";
			} else {
				insert += " AND " + Settings.lotable_product + "=?";
			}
		} else {
			if(!"polar".equals(type))
			insert += " AND " + Settings.lotable_product + " in (" + model.getProducts() + ")";
		}
		insert += ")";
		int nmonth = month - 1;
		Calendar data = new GregorianCalendar(year, nmonth, 1);
		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
		}
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			query1.setLong(1, model.getDate());
			query1.setLong(2, data.getTimeInMillis());
			data.add(Calendar.MONTH, 1);
			data.add(Calendar.DAY_OF_MONTH, -1);
			query1.setLong(3, data.getTimeInMillis());
			query1.setLong(4, model.getPSS());
			int rangeindex = 5;
			if (par.age != null) {
				query1.setString(rangeindex++, par.age.split("-")[1]);
				query1.setString(rangeindex++, par.age.split("-")[0]);
			}
			if (par.gender != null)
				query1.setString(rangeindex++, par.gender);
			if (par.location != null)
				query1.setString(rangeindex++, par.location);
			if (par.products != null)
				query1.setLong(rangeindex++, Long.valueOf(Data.identifyProduct(par.products)));
			try (ResultSet rs = query1.executeQuery()) {
				result = calc_avg(type, rs);

			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR -> " + insert, e);
		} finally {
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}
		result.auxcalc = result.auxcalc / (result.total == 0 ? 1 : result.total);
		String temp;
		temp = String.format("%.2f", result.auxcalc);
		try {
			result.auxcalc = Double.valueOf(temp.replaceAll(",", "."));
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "ERROR", e);
		}
		return result.auxcalc;
	}

	private class avg {
		double auxcalc = 0;
		double total = 0;
	}

	private avg calc_avg(String param, ResultSet rs) throws SQLException {
		avg result = new avg();
		switch (param) {

		case "reach":
			while (rs.next()) {
				result.auxcalc += (double) rs.getDouble(Settings.lotable_reach);
				result.total++;
			}
			break;
		case "polar":
			while (rs.next()) {

				result.auxcalc += (double) rs.getDouble(Settings.lptable_polarity)
						* rs.getDouble(Settings.lotable_reach);
				result.total += rs.getDouble(Settings.lotable_reach);
			}
			break;
		default:
			break;
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
		JSONObject obj;
		obj = new JSONObject();
		obj.put("Filter", output);
		result.put(obj);

		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -timespan);

		for (int month = data.get(Calendar.MONTH); month < timespan * 12 + data.get(Calendar.MONTH); month++) {
			try {
				obj = new JSONObject();
				obj.put("Month", time[month % 12]);
				obj.put("Value", globalreachby(month % 12, data.get(Calendar.YEAR) + month / 12, param, values, id));
				result.put(obj);

			} catch (JSONException e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}

		return result;
	}

	private double globalreachby(int month, int year, String param, String value, long id) {

		Model model = Data.getmodel(id);
		String insert;
		parameters par = split_params(param, value);
		insert = "SELECT " + Settings.lotable + "." + Settings.lotable_reach + " FROM " + Settings.latable + ","
				+ Settings.lptable + ", " + Settings.lotable + " WHERE " + Settings.lotable_timestamp + ">=? AND "
				+ Settings.lotable + "." + Settings.lotable_id + "=" + Settings.lptable + "." + Settings.lptable_opinion
				+ " AND timestamp>? && timestamp<? && " + Settings.lotable_pss + "=? " + "AND (" + Settings.lptable
				+ "." + Settings.lptable_authorid + "=" + Settings.latable + "." + Settings.latable_id;
		return calc_global("reach", insert, par, month, model, year);

	}

	protected static class parameters {
		String age = null;
		String gender = null;
		String location = null;
		String products = null;
	}

	protected static parameters split_params(String param, String value) {
		if (param == null)
			return new parameters();
		String[] params = param.split(",");
		String[] values = value.split(",");
		parameters par = new parameters();
		for (int i = 0; i < params.length; i++) {
			if ("All".equals(values[i]))
				continue;
			switch (params[i]) {
			case "Age":
				par.age = values[i];
				break;

			case "Gender":
				par.gender = values[i];
				break;
			case "Location":
				par.location = values[i];
				break;
			case "Product":
				par.products = values[i];
				break;
			default:
				break;
			}

		}
		return par;

	}

	private void dbconnect() throws ClassNotFoundException, SQLException {
		cnlocal = Settings.connlocal();
	}
}
