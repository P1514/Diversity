package extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import extraction.GetReach.parameters;
import extraction.GetReach;
import general.Backend;
import general.Data;
import general.Logging;
import general.Model;
import general.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class GetPosts.
 *
 * @author Uninova - IControl
 */
public class GetPosts {

	private Connection cnlocal;
	private int MAXTOP = 5;
	private static final Logger LOGGER = new Logging().create(GetPosts.class.getName());

	/**
	 * Method that uses the input to get Top 5 parent posts information, uses month
	 * for the month requested, param defines if any filtering is expected, the id
	 * is the model ID requested.
	 * 
	 * @param param
	 *            String any value
	 * @param month
	 *            String representing month: anything from Jan to December
	 * @param id
	 *            long int
	 * @return JSONArray with all the information
	 * @throws JSONException
	 *             in case creating json error occurs
	 */
	public JSONArray getTop(String param, String month, long id, String product, String word, int day, int year)
			throws JSONException {
		JSONArray result = new JSONArray();
		String[] pre_result = new String[MAXTOP];
		JSONObject obj = new JSONObject();
		Calendar inputdate = Calendar.getInstance();
		obj.put("Op", "table");
		boolean dateerror = false;
		result.put(obj);
		String insert = new String();
		int[] topid = new int[MAXTOP];
		int n_tops = 0;
		// System.out.print("TEST:"+product);
		insert = "Select " + Settings.lotable_id + " FROM " + Settings.lotable + " where (" + Settings.lotable_source
				+ " in (?) AND "+Settings.lotable_account+" in (?) AND " + Settings.lotable_timestamp + ">=?";

		if (!"Global".equals(product))
			insert += " AND " + Settings.lotable_product;

		Model model = Data.getmodel(id);

		if (model == null) {
			result = new JSONArray();
			obj = new JSONObject();
			obj.put("Op", "Error");
			obj.put("Message", "Requested Model Not Found");
			result.put(obj);
			return result;
		}
		if (!"Global".equals(product)) {
			if (!model.getProducts().isEmpty()) {
				if (product == "noproduct")
					insert += " in (" + model.getProducts() + ")";
				else
					insert += "=" + Data.identifyProduct(product);
			} else {
				insert += "=0";
			}

		}

		if (param != null) {
			insert += " && " + Settings.lotable_timestamp + " >= ? && " + Settings.lotable_timestamp + " <= ?";

			SimpleDateFormat sdf = new SimpleDateFormat("d yyyy MMM", Locale.ENGLISH);
			try {
				inputdate.setTime(sdf.parse(day + " " + year + " " + month));
			} catch (ParseException e1) {
				LOGGER.log(Level.INFO, "ERROR", e1);
				insert = insert.replace(
						" && " + Settings.lotable_timestamp + " >= ? && " + Settings.lotable_timestamp + " <= ?", "");
				dateerror = true;
			}
		}

		if (word != null) {
			insert += " AND " + Settings.lotable_id + " in (Select " + Settings.lptable_opinion + " FROM "
					+ Settings.lptable + " where " + Settings.lptable_message + " LIKE '%" + word + "%')"; // and
																											// views>0)";
																											// // More
																											// comment
		}

		insert += ")";

		insert += " ORDER BY reach DESC LIMIT ?";

		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			return Backend.error_message("Cannot connect to Database Please Try Again Later");
		}
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {

			int rangeindex = 4;
			int i = 0;
			query1.setString(1, model.getSources(false));
			query1.setString(2, model.getAccounts(false));
			query1.setLong(3, model.getDate());
			if (param != null && !dateerror) {
				Calendar date = Calendar.getInstance();
				if (!date.after(inputdate))
					inputdate.add(Calendar.YEAR, -1);
				query1.setLong(rangeindex, inputdate.getTimeInMillis());
				inputdate.add(Calendar.MONTH, 1);
				rangeindex++;
				query1.setLong(rangeindex, inputdate.getTimeInMillis());
				rangeindex++;

			}

			// System.out.print(query1);
			query1.setInt(rangeindex, MAXTOP);

			try (ResultSet rs = query1.executeQuery()) {

				for (i = 0; rs.next(); i++) {
					topid[i] = rs.getInt("id");
					n_tops++;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			try {
				cnlocal.close();
			} catch (SQLException e1) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
			return Backend.error_message("ERROR");
		}
		insert = "Select " + Settings.latable_name + "," + Settings.latable_influence + "," + Settings.latable_location
				+ "," + Settings.latable_gender + "," + Settings.latable_age + " from " + Settings.latable + " where "
				+ Settings.latable_id + " in (Select " + Settings.lotable_author + " from " + Settings.lotable
				+ " where " + Settings.lotable_id + " = ? )";
		for (int i = 0; i < n_tops; i++) {

			try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
				query1.setInt(1, topid[i]);
				try (ResultSet rs = query1.executeQuery()) {
					if (rs.next()) {
						pre_result[i] = topid[i] + ",," + rs.getString(Settings.latable_name) + ",,"
								+ rs.getDouble(Settings.latable_influence) + ",,"
								+ rs.getString(Settings.latable_location) + ",," + rs.getString(Settings.latable_gender)
								+ ",," + rs.getInt(Settings.latable_age) + ",,";
					}
				}
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "ERROR", e);

			}
		}

		insert = "Select " + Settings.lotable_timestamp + "," + Settings.lotable_polarity + "," + Settings.lotable_reach
				+ "," + Settings.lotable_comments + " from " + Settings.lotable + " where " + Settings.lotable_id
				+ " = ?";
		for (int i = 0; i < n_tops; i++) {

			try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
				query1.setInt(1, topid[i]);
				try (ResultSet rs = query1.executeQuery()) {
					rs.next();
					pre_result[i] += rs.getLong(Settings.lotable_timestamp) + ",,"
							+ rs.getDouble(Settings.lotable_polarity) + ",," + rs.getDouble(Settings.lotable_reach)
							+ ",," + rs.getInt(Settings.lotable_comments) + ",,";
				}
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}

		insert = "Select " + Settings.lptable_message + " from " + Settings.lptable + " where " + Settings.lptable_id
				+ " = ?";

		for (int i = 0; i < n_tops; i++) {

			try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
				query1.setInt(1, topid[i]);

				try (ResultSet rs = query1.executeQuery()) {
					if (rs.next())
						pre_result[i] += rs.getString(Settings.lptable_message);
					else
						pre_result[i] += "";
				}
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}

		try {

			cnlocal.close();
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "ERROR", e);
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < n_tops; i++) {
			obj = new JSONObject();
			String[] pre_results = pre_result[i].split(",,");
			obj.put("Id", pre_results[0]);
			obj.put("Name", pre_results[1]);
			obj.put("Influence", trunc(pre_results[2]));
			obj.put("Location", pre_results[3]);
			obj.put("Gender", pre_results[4]);
			obj.put("Age", pre_results[5]);

			Date date = new Date(Long.parseLong(pre_results[6]));
			obj.put("Date", df.format(date));
			obj.put("Polarity", trunc(pre_results[7]));
			obj.put("Reach", trunc(pre_results[8]));
			obj.put("Comments", pre_results[9]);
			obj.put("Message", pre_results[10]);
			result.put(obj);
		}

		return result;

	}

	public JSONArray getTopWithPolarity(String param, String month, long id, String product, String word, int min,
			int max, int day, int year) throws JSONException {
		JSONArray result = new JSONArray();
		String[] pre_result = new String[MAXTOP];
		JSONObject obj = new JSONObject();
		Calendar inputdate = Calendar.getInstance();
		obj.put("Op", "table");
		boolean dateerror = false;
		result.put(obj);
		String insert = new String();
		int[] topid = new int[MAXTOP];
		int n_tops = 0;
		// System.out.print("TEST:"+product);´
		
		Model m = Data.getmodel(id);
		String[] uri = m.getURI().split(";");
		String queryAdd = "";
		for (int i = 0; i < uri.length; i++) {
			queryAdd += "(" + Settings.lotable_source + "=? " + "AND " + Settings.lotable_account + "=?)";  
			if (i != (uri.length - 1)) {
				queryAdd +=  "OR ";
			}
		}
		
		
		
		if (min == -1)
			min = 0;
		if (max == -1)
			max = 100;

//		insert = "Select " + Settings.lotable_id + " FROM " + Settings.lotable + " where (" + Settings.lotable_pss
//				+ "=? AND " + Settings.lotable_timestamp + ">=? AND " + Settings.lotable_polarity + "<=" + max + " AND "
//				+ Settings.lotable_polarity + ">=" + min;
		
		insert = "Select " + Settings.lotable_id + " FROM " + Settings.lotable + " where (" + Settings.lotable_timestamp + ">=? "
				+ "AND " + Settings.lotable_polarity + "<=" + max + " AND "
				+ Settings.lotable_polarity + ">=" + min;


		if (!"Global".equals(product))
			insert += " AND " + Settings.lotable_product;


		Model model = Data.getmodel(id);

		if (model == null) {
			result = new JSONArray();
			obj = new JSONObject();
			obj.put("Op", "Error");
			obj.put("Message", "Requested Model Not Found");
			result.put(obj);
			return result;
		}
		if (!"Global".equals(product)) {
			if (!model.getProducts().isEmpty()) {
				if (product == "noproduct")
					insert += " in (" + model.getProducts() + ")";
				else
					insert += "=" + Data.identifyProduct(product);
			} else {
				insert += "=0";
			}

		}

		if (param != null) {
			insert += " && " + Settings.lotable_timestamp + " >= ? && " + Settings.lotable_timestamp + " <= ?";

			SimpleDateFormat sdf = new SimpleDateFormat("d yyyy MMM", Locale.ENGLISH);
			try {
				inputdate.setTime(sdf.parse(day + " " + year + " " + month));
			} catch (ParseException e1) {
				LOGGER.log(Level.INFO, "ERROR", e1);
				insert = insert.replace(
						" && " + Settings.lotable_timestamp + " >= ? && " + Settings.lotable_timestamp + " <= ?", "");
				dateerror = true;
			}
		}

		if (word != null) {
			insert += " AND " + Settings.lotable_id + " in (Select " + Settings.lptable_opinion + " FROM "
					+ Settings.lptable + " where " + Settings.lptable_message + " LIKE '%" + word + "%')"; // and
																											// views>0)";
																											// // More
																											// comment
		}
		insert += "AND (" + queryAdd + ")" ;
		insert += ")";

		insert += " ORDER BY reach DESC LIMIT ?";

		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			return Backend.error_message("Cannot connect to Database Please Try Again Later");
		}
		LOGGER.log(Level.INFO, "TagCloud Query: " + insert);
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {


			int rangeindex = 2;
			int i = 0;
			query1.setLong(1, model.getDate());
			if (param != null && !dateerror) {
				Calendar date = Calendar.getInstance();
				if (!date.after(inputdate))
					inputdate.add(Calendar.YEAR, -1);
				query1.setLong(rangeindex, inputdate.getTimeInMillis());
				inputdate.add(Calendar.MONTH, 1);
				rangeindex++;
				query1.setLong(rangeindex, inputdate.getTimeInMillis());
				rangeindex++;
			}
			for (String s1 : uri) {
				query1.setString(rangeindex, s1.split(",")[0]);
				rangeindex++;
				query1.setString(rangeindex, s1.split(",")[1]);
				rangeindex++;
			}

			
			// System.out.print(query1);
			query1.setInt(rangeindex, MAXTOP);
			
			
			LOGGER.log(Level.INFO, query1.toString());
			try (ResultSet rs = query1.executeQuery()) {

				for (i = 0; rs.next(); i++) {
					topid[i] = rs.getInt("id");
					n_tops++;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			try {
				cnlocal.close();
			} catch (SQLException e1) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
			return Backend.error_message("ERROR");
		}
		insert = "Select " + Settings.latable_name + "," + Settings.latable_influence + "," + Settings.latable_location
				+ "," + Settings.latable_gender + "," + Settings.latable_age + " from " + Settings.latable + " where "
				+ Settings.latable_id + " in (Select " + Settings.lotable_author + " from " + Settings.lotable
				+ " where " + Settings.lotable_id + " = ? )";
		for (int i = 0; i < n_tops; i++) {

			try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
				query1.setInt(1, topid[i]);
				try (ResultSet rs = query1.executeQuery()) {
					rs.next();
					pre_result[i] = topid[i] + ",," + rs.getString(Settings.latable_name) + ",,"
							+ rs.getDouble(Settings.latable_influence) + ",," + rs.getString(Settings.latable_location)
							+ ",," + rs.getString(Settings.latable_gender) + ",," + rs.getInt(Settings.latable_age)
							+ ",,";
				}
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "ERROR", e);

			}
		}

		insert = "Select " + Settings.lotable_timestamp + "," + Settings.lotable_polarity + "," + Settings.lotable_reach
				+ "," + Settings.lotable_comments + " from " + Settings.lotable + " where " + Settings.lotable_id
				+ " = ? AND " + Settings.lotable_polarity + "<=" + max + " AND " + Settings.lotable_polarity + ">="
				+ min;

		for (int i = 0; i < n_tops; i++) {

			try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
				query1.setInt(1, topid[i]);
				try (ResultSet rs = query1.executeQuery()) {
					if (rs.next() == false)
						continue;
					pre_result[i] += rs.getLong(Settings.lotable_timestamp) + ",,"
							+ rs.getDouble(Settings.lotable_polarity) + ",," + rs.getDouble(Settings.lotable_reach)
							+ ",," + rs.getInt(Settings.lotable_comments) + ",,";
				}
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}

		insert = "Select " + Settings.lptable_message + " from " + Settings.lptable + " where " + Settings.lptable_id
				+ " = ?";

		for (int i = 0; i < n_tops; i++) {
			if (topid[i] == 0)
				continue;
			try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
				query1.setInt(1, topid[i]);

				try (ResultSet rs = query1.executeQuery()) {
					if (rs.next())
						pre_result[i] += rs.getString(Settings.lptable_message);
					else
						pre_result[i] += "";
				}
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}

		try {

			cnlocal.close();
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "ERROR", e);
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < n_tops; i++) {
			if ("".equals(pre_result[i]))
				continue;
			obj = new JSONObject();
			String[] pre_results = pre_result[i].split(",,");
			obj.put("Id", pre_results[0]);
			obj.put("Name", pre_results[1]);
			obj.put("Influence", trunc(pre_results[2]));
			obj.put("Location", pre_results[3]);
			obj.put("Gender", pre_results[4]);
			obj.put("Age", pre_results[5]);

			Date date = new Date(Long.parseLong(pre_results[6]));
			obj.put("Date", df.format(date));
			obj.put("Polarity", trunc(pre_results[7]));
			obj.put("Reach", trunc(pre_results[8]));
			obj.put("Comments", pre_results[9]);
			obj.put("Message", pre_results[10]);
			result.put(obj);
		}

		return result;

	}

	/**
	 * Method that returns the amount of parent post that exist to the specific
	 * model, with the filtering specified. The Value param expects a String with
	 * parameters to filter separated by ',', same with value but regarding values
	 * to that specific parameters. Filter specifies what are you filtering by, for
	 * the output JSON. Id is reference to the model that we want the results for.
	 * <p>
	 * Filtering
	 * 
	 * @param param
	 *            Example: [Age,Age,Gender]
	 * @param value
	 *            Example: [15,50,Female]
	 * @param filter
	 *            Anything can be entered
	 * @param id
	 *            - Reference to Model
	 * @return JSONArray with all the information requested
	 * @throws JSONException
	 *             when creating JSON fails to execute
	 */
	public JSONArray getAmmount(boolean wiki, String param, String value, String filter, long id) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		Calendar inputdate = Calendar.getInstance();
		String insert = new String();
		insert = "Select count(*) FROM " + Settings.lotable + " where (";
		if (wiki)
			insert += " " + Settings.lotable_pss + "=? AND";
		/*
		 * if (!wiki) insert += " AND " + Settings.lotable_product;
		 */
		Model model = Data.getmodel(id);
		if (model == null) {
			obj = new JSONObject();
			obj.put("Op", "Error");
			obj.put("Message", "Requested Model not Found");
			result.put(obj);
			return result;
		}
		/*
		 * if (!wiki) { if (!model.getProducts().isEmpty()) { insert += " in (" +
		 * model.getProducts() + ")"; } else { insert += "=0"; } }
		 */
		parameters par = GetReach.split_params(param, value);
		if (par.age != null)
			insert += " age<=? AND age>? AND ";
		if (par.gender != null)
			insert += " gender=? AND";
		if (par.location != null)
			insert += " location=? AND";
		insert += " timestamp<? AND " + Settings.lotable_timestamp + ">=? AND ";
		insert += " source in (?) AND ";
		// if (!wiki)
		insert += "account in (?)";
		insert += ")";
		// ResultSet rs = null;
		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			return Backend.error_message("Cannot connect to database please try again later");
		}
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			int rangeindex = 1;
			if (wiki)
				query1.setLong(rangeindex++, model.getPSS());

			if (par.age != null) {
				query1.setString(rangeindex++, par.age.split("-")[1]);
				query1.setString(rangeindex++, par.age.split("-")[0]);
			}
			if (par.gender != null)
				query1.setString(rangeindex++, par.gender);
			if (par.location != null)
				query1.setString(rangeindex++, par.location);
			inputdate.add(Calendar.MONTH, 1);
			query1.setLong(rangeindex, inputdate.getTimeInMillis());
			rangeindex++;
			/*
			 * inputdate.add(Calendar.YEAR, -1); query1.setLong(rangeindex,
			 * inputdate.getTimeInMillis()); rangeindex++;
			 */
			query1.setLong(rangeindex++, model.getDate());
			query1.setString(rangeindex++, wiki ? "mediawiki" : model.getSources(false));
			// if (!wiki)
			query1.setString(rangeindex++, wiki ? "mediawiki" : model.getAccounts(false));
			//LOGGER.log(Level.INFO, query1.toString());
			try (ResultSet rs = query1.executeQuery()) {
				rs.next();
				obj.put("Filter", "Global");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Value", rs.getInt("count(*)"));
				result.put(obj);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
		} finally {
			try {
				cnlocal.close();
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
		}

		return result;

	}

	/**
	 * Method that finds and returns all the comments of a given post
	 * 
	 * @param post_id
	 *            - the id of the post
	 * @return - JSONArray with the comments
	 * @throws JSONException
	 */
	public JSONArray getComments(long post_id) throws JSONException {
		JSONArray result = new JSONArray();
		String insert = new String();

		insert = "Select " + Settings.lptable_message + " FROM " + Settings.lptable + " WHERE "
				+ Settings.lptable_opinion + "=?";

		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			return Backend.error_message("Cannot connect to Database Please Try Again Later");
		}

		try (PreparedStatement query = cnlocal.prepareStatement(insert)) {
			query.setLong(1, post_id);
			try (ResultSet rs = query.executeQuery()) {
				for (int i = 0; rs.next(); i++) {
					result.put(rs.getString(1));
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR", e);
			try {
				cnlocal.close();
			} catch (SQLException e1) {
				LOGGER.log(Level.INFO, "ERROR", e);
			}
			return Backend.error_message("ERROR");
		}
		try {
			cnlocal.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	private String trunc(String number) {
		double result = 0;
		try {

			result = Double.valueOf(number);
			number = String.format("%.2f", result);
			result = Double.parseDouble(number);

		} catch (Exception e) {
			number = number.replaceAll(",", ".");
			result = Double.parseDouble(number);

		}
		return Double.toString(result);

	}

	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
		}

	}
}
