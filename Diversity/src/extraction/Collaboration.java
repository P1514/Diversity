package extraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Company;
import general.User;
import general.Data;
import general.DesignProject;
import general.Logging;
import general.Settings;

public class Collaboration {

	Prediction pre = new Prediction();
	
	private static final Logger LOGGER = new Logging().create(Collaboration.class.getName());


	

	public JSONArray teamRating(String productsId, String servicesId, String company, Session session)
			throws JSONException {
		JSONArray result = new JSONArray();
		String productsIdef = productsId.replace(",", ";");
		String servicesIdef = servicesId.replace(",", ";");
		String companyef = company;


		if ("null".equals(productsIdef))
			productsIdef = "";
		if ("null".equals(companyef))
			companyef = "";
		if ("null".equals(servicesIdef))
			servicesIdef = "";
		if ("-%20Select%20-".equals(companyef))
			companyef = "";
		HashMap<Long, Double> pssSentiment = pre.predict(productsIdef, servicesIdef);
		if (pssSentiment == null) {
			pssSentiment = pre.predict(companyef,session);
		}
		HashMap<Long, ArrayList<Double>> userRating = new HashMap<>();

		if (pssSentiment != null) {
			HashMap<Long, Double> dpSentiment = new HashMap<>();

			Collection<DesignProject> designprojects = Data.dbdpall();
			pssSentiment.forEach((k, v) -> { // gives design projects average
												// sentiment
				for (DesignProject dp : designprojects) {
					if (dp.getProducesPssId() == k) {
						dpSentiment.put(dp.getID(), v);
					}
				}

			});
			dpSentiment.forEach((k, v) -> {
				for (Long userid : Data.getDp(k).get_team()) {
					if (!userRating.containsKey(userid))
						userRating.put(userid, new ArrayList<Double>());

					userRating.get(userid).add(v);
				}
			});
		}
		for (User user0 : Data.dbuserall()) {
			if (!userRating.containsKey(user0.getID())) {
				Company company0 = Data.getCompany(user0.getcompany_id());
				try {
					JSONObject obj = new JSONObject();
					obj.put("First_name", user0.getfirst_name());
					obj.put("Last_name", user0.getlast_name());
					obj.put("Company", company0.getName());
					obj.put("Role", user0.getrole());
					result.put(obj);

				} catch (JSONException e) {
					LOGGER.log(Level.INFO, Settings.err_unknown,e);
				}

			}

		}
		userRating.forEach((k, v) -> {
			User user1 = Data.getUser(k);
			Double avg = 0.0;
			for (Double aux : v)
				avg += aux;
			avg = avg / v.size();
			Company company1 = Data.getCompany(user1.getcompany_id());
			try {
				JSONObject obj = new JSONObject();
				obj.put("First_name", user1.getfirst_name());
				obj.put("Last_name", user1.getlast_name());
				obj.put("Company", company1.getName());
				obj.put("Role", user1.getrole());
				obj.put("Ranking", avg);
				result.put(obj);

			} catch (JSONException e) {
				LOGGER.log(Level.INFO, Settings.err_unknown,e);
			}
		});

		return result;
	}
}
