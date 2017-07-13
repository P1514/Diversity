package general;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import extraction.Prediction;

public class Collaboration {

	Prediction pre = new Prediction();

	public Collaboration() {

	}

	public JSONArray teamRating(String productsId, String servicesId, String company) throws JSONException {
		JSONArray result = new JSONArray();

		productsId = productsId.replace(",", ";");
		servicesId = servicesId.replace(",", ";");

		System.out.println(productsId);
		System.out.println(servicesId);

		HashMap<Long, Double> pssSentiment = pre.predict(productsId, servicesId);
		if (pssSentiment == null) {
			pssSentiment = pre.predict(company);
		}
		if (pssSentiment == null) {
			return null;
		}
		HashMap<Long, Double> dpSentiment = new HashMap<>();
		HashMap<Long, ArrayList<Double>> userRating = new HashMap<>();

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
					e.printStackTrace();
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
				e.printStackTrace();
			}
		});

		return result;
	}
}
