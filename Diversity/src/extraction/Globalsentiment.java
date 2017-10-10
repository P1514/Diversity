package extraction;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Backend;
import general.Data;
import general.Logging;
import general.Model;
import general.Settings;

/**
 * The Class Globalsentiment.
 *
 * @author Uninova - IControl
 */
public class Globalsentiment extends GetReach {

	private Connection cnlocal = null;
	private static final Logger LOGGER = new Logging().create(Globalsentiment.class.getName());
	private static String[] time = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV",
			"DEC" };

	/**
	 * Class that handles sentiment Requests.
	 */
	public Globalsentiment() {
		super();

	}

	/**
	 * Calculates the Sentiment for the pss id's present in the list psslist, and
	 * saves it in the database for faster fetching when requested.
	 * <p>
	 * Timespan specifies ammount of year to calculate, String param and values the
	 * filtering wanted, top5 the Arraylist with the id's wanted.
	 * 
	 * @param timespan
	 *            whole number that represent years
	 * @param param
	 *            Example:[Age,Age,Gender]
	 * @param values
	 *            Example:[0-30,30-60,Female]
	 * @param psslist
	 *            List with PSS id's
	 * @throws JSONException
	 *             is case JSON creation fails
	 */
	public void globalsentiment(String param, String values, List<Long> psslist) throws JSONException {
		if (psslist.isEmpty())
			return;
		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error Connecting to Database", e);
			return;
		}
		StringBuilder buildstring = new StringBuilder();
		String result = "";
		String delete = "Delete from reach";
		try (PreparedStatement query1 = cnlocal.prepareStatement(delete)) {
			query1.execute();
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "ERROR", e);
		}
		try {
			cnlocal.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		long frequency = calcFrequency(psslist);
		Calendar date = Calendar.getInstance();
		date.add(Calendar.YEAR, -10);
		for (long k : psslist) {

			Data.addmodel((long) -1, new Model(-1, frequency, 0, "", "", k, "0,150", "All", "-1", false,
					date.getTimeInMillis(), 0, -1, true));
			buildstring.append(globalsentiment(param, values, Data.getpss(k).getName(), -1, -1).toString());
			Data.delmodel((long) -1);

		}
		result = buildstring.toString().replaceAll("\\]\\[", ",");
		if ("".equals(result))
			return;
		try {
			dbconnect();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String insert = "Insert into " + Settings.lrtable + " values (?)";
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			query1.setString(1, result);
			query1.execute();
			// System.out.println("TESTE:" + query1.toString());

		} catch (Exception e) {
			LOGGER.log(Level.INFO, "ERROR", e);
		}
		try {
			cnlocal.close();
		} catch (SQLException e) {
			LOGGER.log(Level.INFO, "ERROR", e);
		}

	}

	private long calcFrequency(List<Long> psslist) {

		long max_freq = -1;

		for (Long pss : psslist) {
			try {
				dbconnect();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error Connecting to Database", e);
				return -1;
			}
			String select = "SELECT MAX(" + Settings.lmtable_update + ") FROM " + Settings.lmtable + " WHERE "
					+ Settings.lmtable_pss + "=?";

			try (PreparedStatement query1 = cnlocal.prepareStatement(select)) {
				query1.setString(1, pss.toString());
				try (ResultSet rs = query1.executeQuery()) {
					while (rs.next()) {
						if (rs.getLong(1) > max_freq) {
							max_freq = rs.getLong(1);
						}
					}
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "ERROR", e);
			}
			try {
				cnlocal.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return max_freq;
	}

	/**
	 * Returns the string in the database with the top reach pss global sentiment.
	 *
	 * @return String
	 */
	public String globalsentiment() {

		String select = "Select * from " + Settings.lrtable;
		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			return "";
		}
		try (PreparedStatement query1 = cnlocal.prepareStatement(select)) {
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					String output = rs.getString(1);
					cnlocal.close();
					return output;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
		}
		try {
			cnlocal.close();
		} catch (SQLException e) {
			LOGGER.log(Level.INFO, "ERROR", e);
		}

		return "";

	}

	/**
	 * Calculates Sentiment over the time for the pss with the id provided, timespan
	 * defines the ammount of years to evaluate, Param and Values are expected
	 * string with filtering values separated by ',' , index are expected to math
	 * from both Strings after split.
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
	public JSONArray globalsentiment(String param, String values, String output, long id, long frequency)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj;
		obj = new JSONObject();
		obj.put("Filter", output);
		result.put(obj);
		Model model = Data.getmodel(id);
		if (model == null)
			return Backend.error_message("Model not found");

		Calendar data = Calendar.getInstance();
		Calendar today = Calendar.getInstance();

		// data.setTimeInMillis(model.getDate());
		// data.add(Calendar.YEAR, -1);
		// System.out.println("MODEL START
		// DATE"+"mon:"+data.get(Calendar.MONTH)+"
		// year:"+data.get(Calendar.YEAR));
		// System.out.println("PSS ID:"+ id);

		data.setTimeInMillis(model.getDate());

		/*
		 * if (frequency != -1) { data.add(Calendar.DAY_OF_MONTH, (int) frequency); }
		 * else { data.add(Calendar.MONTH, 1); }
		 */

		// while(today.after(data) &&
		// globalsentimentby(data.get(Calendar.MONTH), data.get(Calendar.YEAR) ,
		// param, values, id)<=0){
		// //System.out.println("GLOBAL
		// SENTIMENT:"+globalsentimentby(data.get(Calendar.MONTH),
		// data.get(Calendar.YEAR) , param, values, id));
		// data.add(Calendar.MONTH, 1);
		// }
		if (firstDate(id) != 0) {
			// System.out.println("DATE:"+"mon:"+data.get(Calendar.MONTH)+"
			// year:"+data.get(Calendar.YEAR));
			if (frequency != -1) {
				for (; today.after(data);) {
					obj = new JSONObject();
					obj.put("Value", globalsentimentby(data.get(Calendar.DAY_OF_MONTH), (data.get(Calendar.MONTH) + 1),
							data.get(Calendar.YEAR), param, values, id, frequency));
					data.add(Calendar.DAY_OF_MONTH, (int) frequency);
					obj.put("Date", data.get(Calendar.DAY_OF_MONTH) + " " + (data.get(Calendar.MONTH) + 1) + " "
							+ data.get(Calendar.YEAR));
					result.put(obj);

				}
			} else {
				for (; today.after(data); data.add(Calendar.MONTH, 1)) {
					obj = new JSONObject();
					obj.put("Date", "01" + " " + (data.get(Calendar.MONTH) + 1) + " " + data.get(Calendar.YEAR));
					obj.put("Value", globalsentimentby(data.get(Calendar.DAY_OF_MONTH), (data.get(Calendar.MONTH) + 1),
							data.get(Calendar.YEAR), param, values, id, frequency));
					// System.out.println("mon:"+data.get(Calendar.MONTH)+"
					// year:"+data.get(Calendar.YEAR));
					result.put(obj);
				}
			}
		}
		return result;
	}

	public JSONArray getCurSentiment(String param, String values, long id, long frequency) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj;
		obj = new JSONObject();
		Model model = Data.getmodel(id);
		double globalSentiment = 0;
		if (model == null)
			return Backend.error_message("Model not found");

		/*
		 * Calendar data = Calendar.getInstance();
		 * data.setTimeInMillis(model.getLastUpdate()); data.add(Calendar.DAY_OF_MONTH,
		 * 1); double globalSentiment =
		 * globalsentimentby(data.get(Calendar.DAY_OF_MONTH), (data.get(Calendar.MONTH)
		 * + 1), data.get(Calendar.YEAR), param, values, id, frequency);
		 */

		String query = "SELECT sum(polarity*reach)/sum(reach) FROM sentimentanalysis.opinions where source in (?) AND account in (?)";
		/*
		 * Calendar data1 = Calendar.getInstance();
		 * data1.setTimeInMillis(model.getLastUpdate()-frequency*86400000); Calendar
		 * data2 = Calendar.getInstance();
		 * data2.setTimeInMillis(model.getLastUpdate()-frequency*86400000);
		 * System.out.println(data1.get(Calendar.DAY_OF_MONTH)+"-"+(data1.get(
		 * Calendar.MONTH)+1)+"-"+data1.get(Calendar.YEAR)+" - ");
		 * System.out.println(data2.get(Calendar.DAY_OF_MONTH)+"-"+(data2.get(
		 * Calendar.MONTH)+1)+"-"+data2.get(Calendar.YEAR)+"\n");
		 */

		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			return Backend.error_message(Settings.err_dbconnect);
		}
		try (PreparedStatement query1 = cnlocal.prepareStatement(query)) {
			query1.setString(1, model.getSources(false));
			query1.setString(2, model.getAccounts(false));
			// System.out.println("Query:" + query1.toString());
			//LOGGER.log(Level.SEVERE, "Query:" + query1.toString());
			// obj.put("query", query1.toString());

			try (ResultSet rs = query1.executeQuery()) {
				if (!rs.next()) {
					globalSentiment = getLastSentiment(model);
				} else if (rs.getDouble(1) != 0)
					globalSentiment = rs.getDouble(1);
				else
					globalSentiment = getLastSentiment(model);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error", e);
			return Backend.error_message("Error Fetching Data Please Try Again");
		} finally {
			try {
				cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}

		/*
		 * while (globalSentiment == -1) { data.add(Calendar.DAY_OF_MONTH, -1);
		 * globalSentiment = globalsentimentby(data.get(Calendar.DAY_OF_MONTH),
		 * data.get(Calendar.MONTH)+1, data.get(Calendar.YEAR), param, values,
		 * id,frequency); }
		 */

		// data.add(Calendar.DAY_OF_MONTH, (int) frequency);

		// obj.put("Month", data.get(Calendar.MONTH) + 1);
		// obj.put("Year", data.get(Calendar.YEAR));

		obj.put("Value", Math.round(globalSentiment));
		result.put(obj);

		return result;
	}

	public JSONArray getWikiCurSentiment(String param, String values, long id, long frequency) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj;
		obj = new JSONObject();
		Model model = Data.getmodel(id);
		double globalSentiment;
		if (model == null)
			return Backend.error_message("Model not found");

		/*
		 * Calendar data = Calendar.getInstance();
		 * data.setTimeInMillis(model.getLastUpdate()); data.add(Calendar.DAY_OF_MONTH,
		 * 1); double globalSentiment =
		 * globalsentimentby(data.get(Calendar.DAY_OF_MONTH), (data.get(Calendar.MONTH)
		 * + 1), data.get(Calendar.YEAR), param, values, id, frequency);
		 */

		String query = "SELECT avg(opinions.polarity) FROM sentimentanalysis.opinions where pss=? and source like 'mediawiki'";

		/*
		 * Calendar data1 = Calendar.getInstance();
		 * data1.setTimeInMillis(model.getLastUpdate()-frequency*86400000); Calendar
		 * data2 = Calendar.getInstance();
		 * data2.setTimeInMillis(model.getLastUpdate()-frequency*86400000);
		 * System.out.println(data1.get(Calendar.DAY_OF_MONTH)+"-"+(data1.get(
		 * Calendar.MONTH)+1)+"-"+data1.get(Calendar.YEAR)+" - ");
		 * System.out.println(data2.get(Calendar.DAY_OF_MONTH)+"-"+(data2.get(
		 * Calendar.MONTH)+1)+"-"+data2.get(Calendar.YEAR)+"\n");
		 */

		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			return Backend.error_message(Settings.err_dbconnect);
		}
		try (PreparedStatement query1 = cnlocal.prepareStatement(query)) {
			query1.setLong(1, model.getPSS());
			LOGGER.log(Level.SEVERE, "Query:" + query1.toString());
			// obj.put("query", query1.toString());
			try (ResultSet rs = query1.executeQuery()) {
				if (!rs.next())
					globalSentiment = -1;
				else
					globalSentiment = rs.getDouble(1);

			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error", e);
			return Backend.error_message("Error Fetching Data Please Try Again");
		} finally {
			try {
				cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}

		/*
		 * while (globalSentiment == -1) { data.add(Calendar.DAY_OF_MONTH, -1);
		 * globalSentiment = globalsentimentby(data.get(Calendar.DAY_OF_MONTH),
		 * data.get(Calendar.MONTH)+1, data.get(Calendar.YEAR), param, values,
		 * id,frequency); }
		 */

		// data.add(Calendar.DAY_OF_MONTH, (int) frequency);

		// obj.put("Month", data.get(Calendar.MONTH) + 1);
		// obj.put("Year", data.get(Calendar.YEAR));

		obj.put("Value", Math.round(globalSentiment));
		result.put(obj);

		return result;
	}

	public double globalsentimentby(int day, int month, int year, String param, String value, long id, long frequency) {

		Model model = Data.getmodel(id);
		parameters par = split_params(param, value);
		String insert = "SELECT " + Settings.lptable + "." + Settings.lptable_polarity + ", " + Settings.lotable + "."
				+ Settings.lotable_reach + " FROM " + Settings.lptable + ", "
				+ Settings.lotable + " WHERE  " + Settings.lotable + "." + Settings.lotable_timestamp + ">=? AND "
				+ Settings.lotable + "." + Settings.lotable_id + "=" + Settings.lptable + "." + Settings.lptable_opinion
				+ " AND timestamp>? && timestamp<? && opinions.id in  (Select id from opinions where " 
				+ (model.getId() == -1 ? Settings.lotable_pss + "=?" : "");

		//LOGGER.log(Level.INFO, "PRE-QUery" + insert);
		return calc_global(false, "polar", insert, par, month, model, year, day, frequency);

	}

	public JSONArray wikiGlobalSentiment(String param, String values, String output, long id, long frequency)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj;
		obj = new JSONObject();
		obj.put("Filter", output);
		result.put(obj);
		Model model = Data.getmodel(id);
		if (model == null)
			return Backend.error_message("Model not found");

		Calendar data = Calendar.getInstance();
		Calendar today = Calendar.getInstance();

		// data.setTimeInMillis(model.getDate());
		// data.add(Calendar.YEAR, -1);
		// System.out.println("MODEL START
		// DATE"+"mon:"+data.get(Calendar.MONTH)+"
		// year:"+data.get(Calendar.YEAR));
		// System.out.println("PSS ID:"+ id);

		data.setTimeInMillis(model.getDate());
		/*
		 * if (frequency != -1) { data.add(Calendar.DAY_OF_MONTH, (int) frequency); }
		 * else { data.add(Calendar.MONTH, 1); }
		 */

		// while(today.after(data) &&
		// globalsentimentby(data.get(Calendar.MONTH), data.get(Calendar.YEAR) ,
		// param, values, id)<=0){
		// //System.out.println("GLOBAL
		// SENTIMENT:"+globalsentimentby(data.get(Calendar.MONTH),
		// data.get(Calendar.YEAR) , param, values, id));
		// data.add(Calendar.MONTH, 1);
		// }
		if (firstDate(id) != 0) {
			// System.out.println("DATE:"+"mon:"+data.get(Calendar.MONTH)+"
			// year:"+data.get(Calendar.YEAR));
			if (frequency != -1) {
				for (; today.after(data);) {
					obj = new JSONObject();
					obj.put("Value", wikiGlobalSentimentBy(data.get(Calendar.DAY_OF_MONTH),
							(data.get(Calendar.MONTH) + 1), data.get(Calendar.YEAR), param, values, id, frequency));
					data.add(Calendar.DAY_OF_MONTH, (int) frequency);
					obj.put("Date", data.get(Calendar.DAY_OF_MONTH) + " " + (data.get(Calendar.MONTH) + 1) + " "
							+ data.get(Calendar.YEAR));
					result.put(obj);

				}
			} else {
				for (; today.after(data); data.add(Calendar.MONTH, 1)) {
					obj = new JSONObject();
					obj.put("Date", "01" + " " + (data.get(Calendar.MONTH) + 1) + " " + data.get(Calendar.YEAR));
					obj.put("Value", wikiGlobalSentimentBy(data.get(Calendar.DAY_OF_MONTH),
							(data.get(Calendar.MONTH) + 1), data.get(Calendar.YEAR), param, values, id, frequency));
					// System.out.println("mon:"+data.get(Calendar.MONTH)+"
					// year:"+data.get(Calendar.YEAR));
					result.put(obj);
				}
			}
		}
		return result;
	}

	public double wikiGlobalSentimentBy(int day, int month, int year, String param, String value, long id,
			long frequency) {

		Model model = Data.getmodel(id);
		parameters par = split_params(param, value);
		String insert = "SELECT " + Settings.lptable + "." + Settings.lptable_polarity + ", " + Settings.lotable + "."
				+ Settings.lotable_reach + " FROM " + Settings.lptable + ", "
				+ Settings.lotable + " WHERE (" + Settings.lotable + "." + Settings.lotable_timestamp + ">=? AND "
				+ Settings.lotable + "." + Settings.lotable_id + "=" + Settings.lptable + "." + Settings.lptable_opinion
				+ " AND timestamp>? && timestamp<? && " + Settings.lotable_pss + "=?"
				+ " AND opinions.source like 'mediawiki'";

		return calc_global(true, "polar", insert, par, month, model, year, day, frequency);

	}

	/**
	 * Calculates Average Sentiment over the time for the pss with the id provided,
	 * timespan defines the ammount of years to evaluate, Param and Values are
	 * expected string with filtering values separated by ',' , index are expected
	 * to math from both Strings after split.
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
	public JSONArray getAvgSentiment(String param, String values, long id) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		double value = 0;
		Calendar data = Calendar.getInstance();
		Calendar today = Calendar.getInstance();

		data.setTimeInMillis(firstDate(id));
		data.add(Calendar.MONTH, 1);

		int avg = 0;
		if (firstDate(id) != 0) {
			for (; today.after(data); data.add(Calendar.MONTH, 1)) {
				value += globalsentimentby(data.get(Calendar.DAY_OF_MONTH) + 1, data.get(Calendar.YEAR),
						data.get(Calendar.YEAR), param, values, id, -1);
				avg++;
			}
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
	 * separated by ',' , index are expected to math from both Strings after split,
	 * output is the string that is going to be returned referencing the type of
	 * filtering applied.
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
		parameters par = split_params(param, value);
		Model model = Data.getmodel(id);
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
				+ " in (Select " + Settings.lotable_id + " from " + Settings.lotable + " where "
				+ Settings.lotable_timestamp+">? AND "+Settings.lotable_account +" in (?)" + " AND "+Settings.lotable_source+" in (?)";/*
						 * AND " + Settings.lptable_authorid + " in (Select " + Settings.latable_id +
						 * " from " + Settings.latable; if (par.age != null || par.gender != null ||
						 * par.location != null) query += " where 1=1 "; if (par.age != null) query +=
						 * " AND " + Settings.latable_age + "<=? AND " + Settings.latable_age + ">?"; if
						 * (par.gender != null) query += " AND " + Settings.latable_gender + "=?"; if
						 * (par.location != null) query += " AND " + Settings.latable_location + "=?";
						 */
		query += ")";

		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			return Backend.error_message(Settings.err_dbconnect);
		}
		try (PreparedStatement query1 = cnlocal.prepareStatement(query)) {
			//query1.setLong(1, model.getPSS());
			int rangeindex = 1;
			/*if (par.products != null) {
				query1.setLong(rangeindex++, Long.valueOf(Data.identifyProduct(par.products)));
			}*/
			query1.setLong(rangeindex++, model.getDate());
			/*if (par.age != null) {
				query1.setString(rangeindex++, par.age.split("-")[1]);
				query1.setString(rangeindex++, par.age.split("-")[0]);
			}

			if (par.gender != null)
				query1.setString(rangeindex++, par.gender);
			if (par.location != null)
				query1.setString(rangeindex++, par.location);
*/
			query1.setString(rangeindex++, model.getAccounts(false));
			query1.setString(rangeindex++, model.getSources(false));
			try (ResultSet rs = query1.executeQuery()) {
				if (!rs.next())
					return Backend.error_message("No results found");

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
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error", e);
			return Backend.error_message("Error Fetching Data Please Try Again");
		} finally {
			try {
				cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}

		return result;

	}

	public JSONArray getWikiPolarityDistribution(long id, String param, String value, String output)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		parameters par = split_params(param, value);
		Model model = Data.getmodel(id);
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
				+ "=?" + " AND " + Settings.lotable_timestamp + ">? ";
		// query += ")";
		query += "and opinions.source like 'mediawiki')";
		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			return Backend.error_message(Settings.err_dbconnect);
		}
		try (PreparedStatement query1 = cnlocal.prepareStatement(query)) {
			query1.setLong(1, model.getPSS());
			int rangeindex = 2;
			query1.setLong(rangeindex++, model.getDate());
			try (ResultSet rs = query1.executeQuery()) {
				if (!rs.next())
					return Backend.error_message("No results found");

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
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "QUERY:" + query);
			LOGGER.log(Level.SEVERE, "Error", e);
			return Backend.error_message("Error Fetching Data Please Try Again");
		} finally {
			try {
				cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}

		return result;

	}

	private Double getLastSentiment(Model model) {
		int month;
		double tempvalue, globalSentiment = 0;
		Calendar today = Calendar.getInstance();
		Calendar firstdate = Calendar.getInstance();
		Data.addmodel((long) -1,
				new Model(-1, 0, 0, "", "", model.getPSS(), "0,150", "All", "-1", false, 0, 0, -1, true));
		firstdate.setTimeInMillis(firstDate(-1));
		Data.delmodel((long) -1);
		Calendar data = firstdate;
		for (month = firstdate.get(Calendar.MONTH); today.after(data); data.add(Calendar.MONTH, 1)) {
			Data.addmodel((long) -1,
					new Model(-1, 0, 0, "", "", model.getPSS(), "0,150", "All", "-1", false, 0, 0, -1, true));
			tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), data.get(Calendar.MONTH),
					data.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
			if (tempvalue != -1)
				globalSentiment = tempvalue;
		}
		return globalSentiment;

	}

	private void dbconnect() throws ClassNotFoundException, SQLException {
		cnlocal = Settings.connlocal();
	}

}
