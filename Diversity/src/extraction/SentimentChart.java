package extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Data;
import general.Model;
import general.Settings;

public class SentimentChart {

	private Connection cnlocal;
	private Settings dbc = new Settings();

	public SentimentChart() {

	}

	public JSONArray chartrequest(String param, String value, long id) {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		String[] params = (param != null) ? param.split(",") : null;
		String[] values = (value != null) ? value.split(",") : null;
		String[] agerange = new String[Settings.ages.split(",,").length];
		String[] locs = new String[Settings.locations.split(",,").length];
		String[] genders = new String[Settings.genders.split(",,").length];
		String[] outparams = Stream.of(Settings.genders.split(",,"), Settings.locations.split(",,")).flatMap(Stream::of)
				.toArray(String[]::new);
		int nages = 0, ngenders = 0, nlocs = 0;
		for (int i = 0; i < params.length; i++) {

			if (params[i].contains("Age")) {
				agerange[nages] = values[i];
				nages++;
				continue;
			}
			if (params[i].contains("Gender")) {
				genders[ngenders] = values[i];
				ngenders++;
				continue;
			}
			if (params[i].contains("Location")) {
				locs[nlocs] = values[i];
				nlocs++;
				continue;
			}
		}
		// TODO find a cleaner way to process ALL this Class need an Overhaul
		// OVERHAUL java needs a OVERHAUL tag
		if (nlocs == 2 && ngenders == 2) {
			try {
				obj = new JSONObject();
				obj.put("Op", "graph");
				result.put(obj);

				int temp = 0;
				for (int gender = 0; gender < ngenders; gender++) {
					obj = new JSONObject();

					obj.put("Age", agerange[0]);
					obj.put("Param", outparams[temp]);
					obj.put("Value", sentimentby(agerange[0], genders[gender], null, id));
					result.put(obj);
					temp++;

				}
				for (int loc = 0; loc < nlocs; loc++) {
					obj = new JSONObject();

					obj.put("Age", agerange[0]);
					obj.put("Param", outparams[temp]);
					obj.put("Value", sentimentby(agerange[0], null, locs[loc], id));
					result.put(obj);
					temp++;

				}
				System.out.print(result.toString());
				return result;
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {
			if (nlocs == 1 && ngenders == 2) {
				outparams[0] = "Male";
				outparams[1] = "Female";
			} else {
				if (nlocs == 2 && ngenders == 1) {
					outparams[0] = "Asia";
					outparams[1] = "Europe";
				} else {
					if ((nlocs == 1 && ngenders == 1)) {
						outparams[0] = "Global";
					} /*
						 * else { outparams[0] = "Global";
						 * 
						 * }
						 */
				}
			}
		}

		try {
			obj.put("Op", "graph");
			result.put(obj);

			if (params[0].contains("Global")) {
				outparams[0] = "Global";
				agerange = Settings.ages.split(",,");
				nages = agerange.length;
				ngenders = 1;
				nlocs = 1;
				genders[0] = null;
				locs[0] = null;
			}

			for (int age = 0; age < nages; age++) {
				int temp = 0;
				for (int gender = 0; gender < ngenders; gender++) {
					for (int loc = 0; loc < nlocs; loc++) {
						obj = new JSONObject();
						obj.put("Age", agerange[age]);
						obj.put("Param", outparams[temp]);
						obj.put("Value", sentimentby(agerange[age], genders[gender], locs[loc], id));
						result.put(obj);
						temp++;
					}
				}
			}
			System.out.print(result.toString());
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	private double sentimentby(String age, String gender, String location, long id) {
		String[] agerange = age.split("-");
		int minage = Integer.parseInt(agerange[0]);
		int maxage = Integer.parseInt(agerange[1]);
		String[] genders = Settings.genders.split(",,");
		double result = (double) 0;
		Model model = Data.modeldb.get(id);
		String insert = "Select " + Settings.lptable_polarity + " FROM " + Settings.lptable + " WHERE "
				+ Settings.lptable_opinion + " in (" + "Select " + Settings.lotable_id + " from opinions where "
				+ Settings.lotable_pss + "=? "
				+ (model.getProducts().equals("-1") ? ""
						: "AND " + Settings.lotable_product + " in (" + model.getProducts() + ")")
				+ ") && " + Settings.lotable_author + " in (Select " + Settings.latable_id + " from " + Settings.latable
				+ " WHERE ((" + Settings.latable_age + " >= ? AND " + Settings.latable_age + " <= ? ";

		insert += ") AND (" + Settings.latable_age + " >= ? AND " + Settings.latable_age + " <= ?)";

		if (model.getGender() == "All") {
			/*
			 * for(int i=0; i<genders.length; i++){ insert += i==0 ? "": " OR ";
			 * insert += "gender=?";
			 * 
			 * }
			 */
			insert += ")";
		} else {
			insert += "AND (" + Settings.latable_gender + "=?))";

		}
		if (gender != null) {
			if (!gender.contains("-")) {
				insert += " AND (" + Settings.latable_gender + " = ?)";
			}
		}
		if (location != null) {
			if (!location.contains("-")) {
				insert += " AND (" + Settings.latable_location + " = ?)";
			} else {
				insert += " AND (" + Settings.latable_location + " >= ? AND " + Settings.latable_location + " <= ?)";
			}
		}

		insert += ")";

		PreparedStatement query1 = null;
		ResultSet rs = null;
		Double auxcalc = (double) 0;
		int i = 6;
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert);
			query1.setString(1, model.getPSS());
			query1.setInt(2, minage);
			query1.setInt(3, maxage);
			query1.setString(4, model.getAge().split(",")[0]);
			query1.setString(5, model.getAge().split(",")[1]);

			if (model.getGender() == "All") {
				/*
				 * for(; i<genders.length; i++){ query1.setString(i,
				 * model.getAge().split(",")[i-6]); }
				 */
				// insert+=")";
			} else {
				query1.setString(i, model.getGender());
				i++;
			}

			if (gender != null) {
				if (gender.contains("-")) {
					genders = gender.split("-");
					query1.setString(i, genders[0]);
					i++;
					query1.setString(i, genders[1]);
					i++;
				} else {
					query1.setString(i, gender);
					i++;
				}
			}
			if (location != null) {
				if (location.contains("-")) {
					String[] locations = location.split("-");
					query1.setString(i, locations[0]);
					i++;
					query1.setString(i, locations[1]);
					i++;
				} else {
					query1.setString(i, location);
					i++;
				}
			}
			System.out.print(query1);
			rs = query1.executeQuery();

			for (i = 0; rs.next(); i++) {
				auxcalc += (double) rs.getInt(Settings.lptable_polarity);
			}

		} catch (Exception e) {
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
		result = auxcalc / (i == 0 ? 1 : i);
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
			cnlocal = Settings.connlocal();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
