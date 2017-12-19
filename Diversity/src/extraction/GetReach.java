package extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.internal.util.collection.Values;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.fabric.xmlrpc.base.Value;
import com.sun.jna.platform.win32.Advapi32Util.Account;
import com.sun.jndi.toolkit.ctx.Continuation;
import com.sun.org.apache.xpath.internal.operations.Mod;

import general.Data;
import general.Logging;
import general.Model;
import general.Settings;

/**
 * The Class GetReach.
 *
 * @author Uninova - IControl
 */
public class GetReach {

	private static final Logger LOGGER = new Logging().create(GetReach.class.getName());
	private String[] time = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

	class PssSA {
		ArrayList<String> accounts = new ArrayList<>();
		ArrayList<String> sources = new ArrayList<>();
		int avgReach;

		void addSourceAccount(String source, String account) {
			accounts.add(account);
			sources.add(source);
		}

		public void setAvgReach(int avgReach) {
			this.avgReach = avgReach;
		}

		public ArrayList<String> getAccounts() {
			return accounts;
		}

		public int getAvgReach() {
			return avgReach;
		}

		public ArrayList<String> getSources() {
			return sources;
		}

	}

	/**
	 * Returns an array list with the nTOP number of pss's with higher reach on the
	 * of the last 12 months.
	 * 
	 * @param nTOP
	 *            - Number of PSS wanted
	 * @return ArrayList of pss id's
	 */

	public List<Long> getTOPReach(int nTOP) {
		ArrayList<Long> tops = new ArrayList<>();
		HashMap<Long, PssSA> psss = new HashMap<>();
		HashMap<Long, Float> avgReachByPss = new HashMap<>();

		for (Model mod : Data.getAllModels().values()) {
			if (mod.getArchived())
				continue;
			String[] sourceAcounts = mod.getURI().split(";");
			long pssId = mod.getPSS();

			if (psss.containsKey(pssId)) {
				for (String sourceAccount : sourceAcounts)
					psss.get(pssId).addSourceAccount(sourceAccount.split(",")[0], sourceAccount.split(",")[1]);
			} else {
				PssSA p = new PssSA();
				for (String sourceAccount : sourceAcounts)
					p.addSourceAccount(sourceAccount.split(",")[0], sourceAccount.split(",")[1]);
				psss.put(pssId, p);
			}

		}

		psss.forEach((k, v) -> {
			// String select = "Select avg(reach) from opinions where account in (";
			// for (String account : v.getAccounts())
			// select += "\"" + account + "\",";
			// select = select.substring(0, select.length() - 1);
			// select += ") and source in (";
			// for (String source : v.getSources())
			// select += "\"" + source + "\",";
			// select = select.substring(0, select.length() - 1);
			// select += ")";

			String select = "Select avg(reach) from opinions where ";
			for (int i = 0; i < v.getAccounts().size(); i++) {
				if (i == 0)
					select += "(account=? AND source=?)";
				else
					select += " OR (account=? AND source=?)";
			}

			try (Connection cnlocal = Settings.connlocal();
					PreparedStatement query1 = cnlocal.prepareStatement(select)) {
				int range_index = 1;
				for (int i = 0; i < v.getAccounts().size(); i++) {
					query1.setString(range_index++, v.getAccounts().get(i));
					query1.setString(range_index++, v.getSources().get(i));
				}
				try (ResultSet rs = query1.executeQuery()) {
					while (rs.next()) {
						avgReachByPss.put(k, rs.getFloat(1));
						Logger.getLogger(GetReach.class.getName()).log(Level.INFO,
								select + " PSS:" + k + "Average Reach: " + rs.getFloat("avg(reach)"));
					}
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "ERROR", e);
			}

		});

		for (int i = 0; i < nTOP; i++) {
			Map.Entry<Long, Float> maxEntry = null;

			for (Map.Entry<Long, Float> entry : avgReachByPss.entrySet()) {
				if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
					maxEntry = entry;
				}
			}

			if (avgReachByPss.isEmpty())
				break;
			Logger.getLogger(GetReach.class.getName()).log(Level.INFO, "Maxreach: " + maxEntry.getValue() + "\n");
			tops.add(maxEntry.getKey());
			avgReachByPss.remove(maxEntry.getKey());
		}

		return tops;

	}

	/**
	 * Calculates Reach over the time for the pss with the id provided, timespan
	 * defines the ammount of years to evaluate, Param and Values are expected
	 * string with filtering values separated by ',' , index are expected to math
	 * from both Strings after split.
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
	public JSONArray getReach(String param, String values, long id) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		double value = 0;

		Calendar data = Calendar.getInstance();
		Calendar today = Calendar.getInstance();

		data.setTimeInMillis(firstDate(id));
		data.add(Calendar.MONTH, 1);
		today.add(Calendar.MONTH, 1);

		int avg = 0;
		double last_value = 0;
		if (firstDate(id) != 0) {
			for (; today.after(data); data.add(Calendar.MONTH, 1)) {

				value += globalsentimentby(data.get(Calendar.DAY_OF_MONTH), data.get(Calendar.MONTH),
						data.get(Calendar.YEAR), param, values, id, -1);

				if (Double.compare(last_value, value) != 0)
					avg++;
				last_value = value;
			}
		}
		value = value / ((avg != 0) ? avg : 1);
		String temp;
		temp = String.format("%.2f", value);
		try {
			value = Double.valueOf(temp.replace(",", "."));
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "ERROR", e);
		}
		value = value == (double) 0 ? -1 : value;
		obj.put("Param", "Global");
		obj.put("Value", value);
		result.put(obj);

		return result;
	}

	private double globalsentimentby(int day, int month, int year, String param, String value, long id,
			long frequency) {

		Model model = Data.getmodel(id);
		String insert;
		parameters par = split_params(param, value);
		insert = "SELECT " + Settings.lptable + "." + Settings.lptable_polarity + ", " + Settings.lotable + "."
				+ Settings.lotable_reach + " FROM " + Settings.lptable + ", " + Settings.lotable + " WHERE "
				+ Settings.lotable_timestamp + ">=? AND " + Settings.lotable + "." + Settings.lotable_id + "="
				+ Settings.lptable + "." + Settings.lptable_opinion
				+ " AND opinions.id in (Select opinions.id from opinions,authors where opinions.authors_id = authors.id AND timestamp>? && timestamp<=? ";/*
																																							 * and
																																							 * "
																																							 *//*
																																								 * &&
																																								 * (" + Settings.lptable + "
																																								 * ."
																																								 * +
																																								 * Settings.
																																								 * lptable_authorid
																																								 * +
																																								 * "="
																																								 * +
																																								 * Settings.
																																								 * latable
																																								 * +
																																								 * "."
																																								 * +
																																								 * Settings.
																																								 * latable_id;
																																								 */
		return calc_global(false, "reach", insert, par, month, model, year, day, frequency);
	}

	protected double calc_global(boolean wiki, String type, String insert, parameters par, int month, Model model,
			int year, int day, long frequency) {
		avg result = new avg();
		/*
		 * if (par.age != null) insert += " AND " + Settings.latable + "." +
		 * Settings.latable_age + "<=? AND " + Settings.latable + "." +
		 * Settings.latable_age + ">?"; if (par.gender != null) insert += " AND " +
		 * Settings.latable + "." + Settings.latable_gender + "=?"; if (par.location !=
		 * null) insert += " AND " + Settings.latable + "." + Settings.latable_location
		 * + "=?";
		 */

		if (par.age != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_age + "<=? AND " + Settings.latable + "."
					+ Settings.latable_age + ">?";

		if (par.gender != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_gender + "=?";

		if (par.location != null)
			insert += " AND " + Settings.latable + "." + Settings.latable_location + "=?";

		/*
		 * if (!wiki) { if (par.products != null) { if (par.products.equals("-1")) {
		 * insert += " AND " + Settings.lotable_product + " in (" + model.getProducts()
		 * + ")"; } else { insert += " AND " + Settings.lotable_product + "=?"; } } else
		 * { if (!"polar".equals(type)) insert += " AND " + Settings.lotable_product +
		 * " in (" + model.getProducts() + ")"; } } if (!model.getMediawiki()) insert +=
		 * " AND " + Settings.lotable + "." + Settings.lotable_product + " is not null";
		 */

		/*
		 * if (!wiki) { if (par.products != null) { if (par.products.equals("-1") &&
		 * model.getProducts() != "") { insert += " AND " + Settings.lotable_product +
		 * " in (" + model.getProducts() + ")"; } else { insert += " AND " +
		 * Settings.lotable_product + "=?"; } } else { //if (!"polar".equals(type) &&
		 * model.getProducts() != "") //insert += " AND " + Settings.lotable_product +
		 * " in (" + model.getProducts() + ")"; } }
		 */

		if (!model.getMediawiki()) {
			insert += " AND " + Settings.lotable + "." + Settings.lotable_product + " is not null";
		}
		if (model.getId() != -1 && !wiki) {
			insert += " AND opinions.source in (";
			int sourceaccountlength = model.getSources(false).size();
			for (int i = 0; i < sourceaccountlength; i++)
				insert += "?,";
			insert = insert.substring(0, insert.length() - 1) + ") AND opinions." + Settings.lotable_account + " in (";
			sourceaccountlength = model.getAccounts(false).size();
			for (int i = 0; i < sourceaccountlength; i++)
				insert += "?,";
			insert = insert.substring(0, insert.length() - 1) + ")";
		}
		insert += ")";
		int nmonth = month - 1;

		Calendar data = new GregorianCalendar(year, nmonth, day);

		try (Connection cnlocal = Settings.connlocal(); PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			int rangeindex = 1;
			query1.setLong(rangeindex++, model.getDate());
			query1.setLong(rangeindex++, data.getTimeInMillis());
			if (frequency == -1) {
				data.add(Calendar.MONTH, 1);
				data.add(Calendar.DAY_OF_MONTH, -1);
			} else
				data.add(Calendar.DAY_OF_MONTH, (int) frequency);

			query1.setLong(rangeindex++, data.getTimeInMillis());

			if (wiki || model.getId() == -1)
				query1.setLong(rangeindex++, model.getPSS());

			if (par.age != null) {
				query1.setString(rangeindex++, par.age.split("-")[1]);
				query1.setString(rangeindex++, par.age.split("-")[0]);
			}

			if (par.gender != null)
				query1.setString(rangeindex++, par.gender);

			if (par.location != null)
				query1.setString(rangeindex++, par.location);

			/*
			 * if (par.products != null) query1.setLong(rangeindex++,
			 * Long.valueOf(Data.identifyProduct(par.products)));
			 */

			if (model.getId() != -1 && !wiki) {
				ArrayList<String> sourceaccount = model.getSources(false);
				for (int ii = 0; ii < sourceaccount.size(); ii++)
					query1.setString(rangeindex++, sourceaccount.get(ii));
				sourceaccount = model.getAccounts(false);
				for (int ii = 0; ii < sourceaccount.size(); ii++)
					query1.setString(rangeindex++, sourceaccount.get(ii));
			}

			// LOGGER.log(Level.INFO," WIKI "+ wiki + " " +query1);
			// System.out.println(query1.toString());
			try (ResultSet rs = query1.executeQuery()) {
				result = calc_avg(type, rs);

			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR -> " + insert, e);
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
		boolean notzero = false;
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
				notzero = true;
			}

			if (!notzero) {
				result.total = 1;
				result.auxcalc = -1;
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
	public JSONArray globalreach(String param, String values, String output, long id, long frequency)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj;
		obj = new JSONObject();
		obj.put("Filter", output);
		result.put(obj);

		Calendar data = Calendar.getInstance();
		Calendar today = Calendar.getInstance();

		data.setTimeInMillis(firstDate(id));
		if (frequency != -1) {
			data.add(Calendar.DAY_OF_MONTH, (int) frequency);
			today.add(Calendar.DAY_OF_MONTH, (int) frequency);
		} else {
			data.add(Calendar.MONTH, 1);
			today.add(Calendar.MONTH, 1);
		}

		if (firstDate(id) != 0) {
			if (frequency != -1) {
				for (; today.after(data); data.add(Calendar.DAY_OF_MONTH, (int) frequency)) {
					try {
						obj = new JSONObject();
						obj.put("Date", data.get(Calendar.DAY_OF_MONTH) + " " + (data.get(Calendar.MONTH) + 1) + " "
								+ data.get(Calendar.YEAR));
						obj.put("Value", globalreachby(data.get(Calendar.DAY_OF_MONTH), data.get(Calendar.MONTH),
								data.get(Calendar.YEAR), param, values, id));
						result.put(obj);
					} catch (JSONException e) {
						LOGGER.log(Level.INFO, "ERROR", e);
					}
				}
			} else {
				for (; today.after(data); data.add(Calendar.MONTH, 1)) {
					try {
						obj = new JSONObject();
						obj.put("Date", (data.get(Calendar.MONTH) + 1) + " 01 " + data.get(Calendar.YEAR));
						obj.put("Value", globalreachby(data.get(Calendar.DAY_OF_MONTH), data.get(Calendar.MONTH),
								data.get(Calendar.YEAR), param, values, id));
						result.put(obj);

					} catch (JSONException e) {
						LOGGER.log(Level.INFO, "ERROR", e);
					}
				}
			}
		}
		return result;
	}

	private double globalreachby(int day, int month, int year, String param, String value, long id) {

		Model model = Data.getmodel(id);
		String insert;
		parameters par = split_params(param, value);
		insert = "SELECT " + Settings.lotable + "." + Settings.lotable_reach + " FROM " + Settings.lptable + ", "
				+ Settings.lotable + " WHERE " + Settings.lotable_timestamp + ">=? AND " + Settings.lotable + "."
				+ Settings.lotable_id + "=" + Settings.lptable + "." + Settings.lptable_opinion
				+ " AND timestamp>? && timestamp<? && opinions.id in (Select opinions.id from opinions,authors where opinions.authors_id = authors.id ";

		return calc_global(false, "reach", insert, par, month, model, year, day, -1);

	}

	protected static class parameters {
		String age = null;
		String gender = null;
		String location = null;
		String products = null;
	}

	protected static parameters split_params(String param, String value) {
		if (param == null || value == null)
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

	public long firstDate(long id) {
		Model model = Data.getmodel(id);
		long result = 0;
		String insert = "Select min(timestamp) from opinions where ";
		if (model.getMediawiki()) {
			insert += "pss=? or ";
		}
		ArrayList<String> sources = model.getSources(false);
		ArrayList<String> accounts = model.getAccounts(false);
		if (sources.size() != 0) {
			insert += "( account in (?";
			for (int i = 1; i < accounts.size(); i++)
				insert += ",?";
			insert += ") and source in (?";
			for (int i = 1; i < sources.size(); i++)
				insert += ",?";
			insert += "))";
		} else {
			insert += "1=0";
		}

		try (Connection cnlocal = Settings.connlocal(); PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			int rangeindex = 1;
			if (model.getMediawiki())
				query1.setLong(rangeindex++, model.getPSS());

			for (int i = 0; i < accounts.size(); i++)
				query1.setString(rangeindex++, accounts.get(i));
			for (int i = 0; i < sources.size(); i++)
				query1.setString(rangeindex++, sources.get(i));

			try (ResultSet rs = query1.executeQuery()) {
				if (!rs.next())
					return Calendar.getInstance().getTimeInMillis() * 2;

				if (model.getDate() > rs.getLong(1))
					result = model.getDate();
				else
					result = rs.getLong(1);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
		}
		return result;

	}
}
