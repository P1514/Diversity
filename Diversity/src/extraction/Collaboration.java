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

import general.Backend;
import general.Company;
import general.Data;
import general.DesignProject;
import general.Logging;
import general.Server;
import general.User;

public class Collaboration {

	Prediction pre = new Prediction();
	
	private static final Logger LOGGER = new Logging().create(Collaboration.class.getName());
	public Collaboration() {

	}

	

	public JSONArray teamRating(String productsId, String servicesId, String company, Session session)
			throws JSONException {
		JSONArray result = new JSONArray();
		productsId = productsId.replace(",", ";");
		servicesId = servicesId.replace(",", ";");

		// System.out.println(productsId);
		// System.out.println(servicesId);
		if ("null".equals(productsId))
			productsId = "";
		if ("null".equals(company))
			company = "";
		if ("null".equals(servicesId))
			servicesId = "";
		if ("-%20Select%20-".equals(company))
			company = "";
		//updateLoadBar(step++, 0, 1, session);
		HashMap<Long, Double> pssSentiment = pre.predict(productsId, servicesId);
		if (pssSentiment == null) {
			pssSentiment = pre.predict(company,session);
		}
		HashMap<Long, ArrayList<Double>> userRating = new HashMap<>();

		if (pssSentiment != null) {
			HashMap<Long, Double> dpSentiment = new HashMap<>();

			Collection<DesignProject> designprojects = Data.dbdpall();
			//updateLoadBar(step, 0, pssSentiment.size(), session);
			//hidden_size=1;
			pssSentiment.forEach((k, v) -> { // gives design projects average
												// sentiment
				//updateLoadBar(step, hidden_size++, 0, session);
				for (DesignProject dp : designprojects) {
					if (dp.getProducesPssId() == k) {
						dpSentiment.put(dp.getID(), v);
					}
				}

			});
			//updateLoadBar(++step, 0, dpSentiment.size(), session);
			//hidden_size=1;
			dpSentiment.forEach((k, v) -> {
				//updateLoadBar(step, hidden_size++, 0, session);
				for (Long userid : Data.getDp(k).get_team()) {
					if (!userRating.containsKey(userid))
						userRating.put(userid, new ArrayList<Double>());

					userRating.get(userid).add(v);
				}
			});
			// return null;
		}
		//updateLoadBar(++step, 0, Data.dbuserall().size(), session);
		//hidden_size=1;
		for (User user0 : Data.dbuserall()) {
			//updateLoadBar(step, hidden_size, 0, session);
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
					e.printStackTrace();
				}

			}

		}
		//updateLoadBar(++step, 0, userRating.size(), session);
		//hidden_size=1;
		userRating.forEach((k, v) -> {
			//updateLoadBar(step, hidden_size, 0, session);
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
				e.printStackTrace();
			}
		});

		return result;
	}
}
