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

	public JSONArray teamRating(String productsId, String servicesId) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		
		HashMap<Long, Double> pssSentiment = pre.predict(productsId, servicesId);
		HashMap<Long, Double> dpSentiment = new HashMap<Long, Double>();
		HashMap<Long, ArrayList<Double>> userRating = new HashMap<Long, ArrayList<Double>>();

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

		userRating.forEach((k, v) -> {
			System.out.println("USER: " + k + " -->" + v);
			Double avg = 0.0;
			for (Double aux : v)
				avg += aux;
			avg=avg/v.size();
			System.out.println("USER: " + k + " -->" + avg);
		});

		return result;
	}
}
