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
	
	public JSONArray teamRating(String companies) throws JSONException {
		String productsId="";
		String servicesId="";
	return	teamRating(productsId, servicesId);
	}

	public JSONArray teamRating(String productsId, String servicesId) throws JSONException {
		JSONArray result = new JSONArray();

		productsId = productsId.replace(",", ";");
		servicesId = servicesId.replace(",", ";");

		System.out.println(productsId);
		System.out.println(servicesId);

		HashMap<Long, Double> pssSentiment = pre.predict(productsId, servicesId);
		HashMap<Long, Double> dpSentiment = new HashMap<>();
		HashMap<Long, ArrayList<Double>> userRating = new HashMap<>();

		Collection<DesignProject> designprojects = Data.dbdpall();

		if (pssSentiment == null)
			return result.put("No Team Members");

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

		userRating.forEach((k, v) -> {
			User user1 = Data.getUser(k);
			Double avg = 0.0;
			for (Double aux : v)
				avg += aux;
			avg = avg / v.size();
			try {
				JSONObject obj = new JSONObject();
				obj.put("First_name", user1.getfirst_name());
				obj.put("Last_name", user1.getlast_name());
				obj.put("Company", user1.getlast_name());
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
