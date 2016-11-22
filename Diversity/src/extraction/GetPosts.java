package extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Data;
import general.Model;
import general.Settings;

/**
 * @author Uninova - IControl
 *
 */
public class GetPosts {

	private Connection cnlocal;
	private int MAXTOP = 5;

	/**
	 * Class that handles getting Top Parent Posts, and the ammout of Posts
	 */
	public GetPosts() {
	}

	/**
	 * Method that uses the input to get Top 5 parent posts, uses value 
	 * @param param
	 * @param value
	 * @param id
	 * @return
	 * @throws JSONException
	 */
	public JSONArray getTop(String param, String value, long id) throws JSONException {
		JSONArray result = new JSONArray();
		String[] pre_result = new String[MAXTOP];
		JSONObject obj = new JSONObject();
		Calendar inputdate = Calendar.getInstance();
		obj.put("Op", "table");
		result.put(obj);
		String insert = new String();
		int[] topid = new int[MAXTOP];
		PreparedStatement query1 = null;
		int n_tops = 0;
		insert = "Select " + Settings.lotable_id + " FROM " + Settings.lotable + " where (" + Settings.lotable_pss
				+ "=? AND " + Settings.lotable_product;
		Model model = Data.modeldb.get(id);
		if (model == null) {
			result = new JSONArray();
			obj = new JSONObject();
			obj.put("Op", "Error");
			obj.put("Message", "Requested Model Not Found");
			result.put(obj);
			return result;
		}
		if (model.getProducts() != null) {
			insert += " in ("+ model.getProducts() + ")";
		} else {
			insert += "=0";
		}
		if (param != null) {
			insert += " && " + Settings.lotable_timestamp + " >= ? && " + Settings.lotable_timestamp + " <= ?";

			SimpleDateFormat sdf = new SimpleDateFormat("d yyyy MMM", Locale.ENGLISH);
			try {
				inputdate.setTime(sdf.parse("1 " + inputdate.get(Calendar.YEAR) + " " + value));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		insert += ")";

		insert += " ORDER BY reach DESC LIMIT ?";
		ResultSet rs = null;

		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			int rangeindex = 2;
			int i = 0;
			query1.setLong(1, model.getPSS());
			if (param != null) {
				Calendar date = Calendar.getInstance();
				if (!date.after(inputdate))
					inputdate.add(Calendar.YEAR, -1);
				query1.setDate(rangeindex, new java.sql.Date(inputdate.getTimeInMillis()));
				inputdate.add(Calendar.MONTH, 1);
				rangeindex++;
				query1.setDate(rangeindex, new java.sql.Date(inputdate.getTimeInMillis()));
				rangeindex++;

			}
			// System.out.print(query1);
			query1.setInt(rangeindex, MAXTOP);
			rs = query1.executeQuery();

			for (i = 0; rs.next(); i++) {
				topid[i] = rs.getInt("id");
				n_tops++;
			}

			insert = "Select " + Settings.latable_name + "," + Settings.latable_influence + ","
					+ Settings.latable_location + "," + Settings.latable_gender + "," + Settings.latable_age + " from "
					+ Settings.latable + " where " + Settings.latable_id + " in (Select " + Settings.lotable_author
					+ " from " + Settings.lotable + " where " + Settings.lotable_id + " = ? )";
			for (i = 0; i < n_tops; i++) {

				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, topid[i]);
				rs = query1.executeQuery();
				rs.next();
				pre_result[i] = topid[i] + ",," + rs.getString(Settings.latable_name) + ",,"
						+ rs.getDouble(Settings.latable_influence) + ",," + rs.getString(Settings.latable_location)
						+ ",," + rs.getString(Settings.latable_gender) + ",," + rs.getInt(Settings.latable_age) + ",,";
				rs.close();
				query1.close();
			}

			insert = "Select " + Settings.lotable_timestamp + "," + Settings.lotable_polarity + ","
					+ Settings.lotable_reach + "," + Settings.lotable_comments + " from " + Settings.lotable + " where "
					+ Settings.lotable_id + " = ?";
			for (i = 0; i < n_tops; i++) {

				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, topid[i]);
				rs = query1.executeQuery();
				rs.next();
				pre_result[i] += rs.getDate(Settings.lotable_timestamp) + ",," + rs.getDouble(Settings.lotable_polarity) + ",,"
						+ rs.getDouble(Settings.lotable_reach) + ",," + rs.getInt(Settings.lotable_comments) + ",,";
				rs.close();
				query1.close();
			}

			insert = "Select "+Settings.lptable_message+" from "+Settings.lptable+" where "+Settings.lptable_id+" = ?";
			for (i = 0; i < n_tops; i++) {

				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, topid[i]);
				rs = query1.executeQuery();
				rs.next();
				pre_result[i] += rs.getString(Settings.lptable_message);
				rs.close();
				query1.close();
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

		for (int i = 0; i < n_tops; i++) {
			obj = new JSONObject();
			String[] pre_results = pre_result[i].split(",,");
			obj.put("Id", pre_results[0]);
			obj.put("Name", pre_results[1]);
			obj.put("Influence", trunc(pre_results[2]));
			obj.put("Location", pre_results[3]);
			obj.put("Gender", pre_results[4]);
			obj.put("Age", pre_results[5]);
			obj.put("Date", pre_results[6]);
			obj.put("Polarity", trunc(pre_results[7]));
			obj.put("Reach", trunc(pre_results[8]));
			obj.put("Comments", pre_results[9]);
			obj.put("Message", pre_results[10]);
			result.put(obj);

		}

		return result;

	}

	public JSONArray getAmmount(String param, String value, String filter, long id) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		String[] params;
		String[] values;
		String age = null;
		String location = null;
		String gender = null;
		Calendar inputdate = Calendar.getInstance();
		String insert = new String();
		PreparedStatement query1 = null;
		insert = "Select count(*) FROM " + Settings.lotable + " where (" + Settings.lotable_pss + "=? AND "
				+ Settings.lotable_product;
		Model model = Data.modeldb.get(id);
		if (model == null) {
			obj = new JSONObject();
			obj.put("Op", "Error");
			obj.put("Message", "Requested Model not Found");
			result.put(obj);
			return result;
		}
		if (model.getProducts() != null) {
			insert += " in ("+ model.getProducts() + ")";
		} else {
			insert += "=0";
		}
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
				}
			}
		}
		if (age != null)
			insert += " AND age<=? AND age>?";
		if (gender != null)
			insert += " AND gender=?";
		if (location != null)
			insert += " AND location=?";
		insert += " AND timestamp<? AND timestamp>=?)";
		ResultSet rs = null;

		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			int rangeindex = 2;
			query1.setLong(1,model.getPSS());

			if (age != null) {
				query1.setString(rangeindex++, age.split("-")[1]);
				query1.setString(rangeindex++, age.split("-")[0]);
			}
			if (gender != null)
				query1.setString(rangeindex++, gender);
			if (location != null)
				query1.setString(rangeindex++, location.substring(0, location.length()));
			inputdate.add(Calendar.MONTH, 1);
			query1.setDate(rangeindex, new java.sql.Date(inputdate.getTimeInMillis()));
			rangeindex++;
			inputdate.add(Calendar.YEAR, -1);
			query1.setDate(rangeindex, new java.sql.Date(inputdate.getTimeInMillis()));
			rangeindex++;

			System.out.print(query1);
			rs = query1.executeQuery();
			rs.next();
			obj.put("Filter", "Global");
			result.put(obj);
			obj = new JSONObject();
			obj.put("Value", rs.getInt("count(*)"));
			result.put(obj);

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
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
