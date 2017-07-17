package extraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;

import org.json.JSONArray;

import extraction.Extrapolation;
import general.Data;
import general.DesignProject;
import general.Logging;
import general.Model;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Prediction extends Globalsentiment {

	double totalSentiment, totalWeight, totalGsweight, variance, numbOfProd, maxValue, mean, tempvalue, stDeviation;
	int month, i;
	public static final Logger LOGGER = new Logging().create(Data.class.getName());

	public Prediction() {

	}

	public JSONArray predict(int timespan /* years */, String productsId, String servicesId) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		HashMap<Long, Double> pssweights = Extrapolation.get_Similarity_Threshold(productsId, 75, true);
		HashMap<Long, Double> pssweightss = Extrapolation.get_Similarity_Threshold(servicesId, 60, false);

//		pssweights.forEach((k, v) -> {
//			System.out.println("SIMILARITY OF PRODUCTS(" + k + ") -->" + v);
//
//		});
//
//		pssweightss.forEach((k, v) -> {
//			System.out.println("SIMILARITY OF SERVICES(" + k + ") -->" + v);
//
//		});
		if (pssweights.isEmpty() && pssweightss.isEmpty())

		{
			obj.put("Op", "Error");
			obj.put("Message", "No prediction available");
			result.put(obj);
			return result;
		}
		String[] time = new String[12];
		time[0] = "JAN";
		time[1] = "FEB";
		time[2] = "MAR";
		time[3] = "APR";
		time[4] = "MAY";
		time[5] = "JUN";
		time[6] = "JUL";
		time[7] = "AUG";
		time[8] = "SEP";
		time[9] = "OCT";
		time[10] = "NOV";
		time[11] = "DEC";
		obj = new JSONObject();

		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -1);

		for (month = data.get(Calendar.MONTH); month < 12 + data.get(Calendar.MONTH); month++) {
			totalWeight = 0;
			totalGsweight = 0;
			variance = 0;
			numbOfProd = 0;
			pssweights.forEach((k, v) -> {
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12,
						data.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
				totalGsweight += (tempvalue == -1 ? 0 : v * tempvalue);
				Data.delmodel((long) -1);
				totalWeight += (tempvalue == -1 ? 0 : v);
				numbOfProd++;
			});
			pssweightss.forEach((k, v) -> {
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12,
						data.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
				totalGsweight += (tempvalue == -1 ? 0 : v * tempvalue);
				Data.delmodel((long) -1);
				totalWeight += (tempvalue == -1 ? 0 : v);
				numbOfProd++;
			});

			mean = (totalGsweight) / (totalWeight == 0 ? 1 : totalWeight);
			variance = 0;
			pssweights.forEach((k, v) -> {
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12,
						data.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
				variance += Math.pow(tempvalue - mean, 2);
			});

			pssweightss.forEach((k, v) -> {
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12,
						data.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
				variance += Math.pow(tempvalue - mean, 2);
			});

			variance += Math.pow(200 - mean, 2);

			if (totalGsweight != 0) {

				variance = variance / totalGsweight;
				stDeviation = Math.sqrt(variance);
			} else {
				mean = -1;
				variance = -1;
			}
			try {
				obj = new JSONObject();
				obj.put("Month", time[month % 12]);
				obj.put("Value", mean);
				obj.put("Variance", Math.round((1.96 * stDeviation) / Math.sqrt(numbOfProd)));// 95%
																								// confidence
																								// interval
				result.put(obj);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	public JSONArray predictLifeCycle(int timespan /* years */, String productsId, String servicesId)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		HashMap<Long, Double> pssweights = Extrapolation.get_Similarity_Threshold(productsId, 75, true);
		HashMap<Long, Double> pssweightss = Extrapolation.get_Similarity_Threshold(servicesId, 60, false);

//		pssweights.forEach((k, v) -> {
//			System.out.println("SIMILARITY OF PRODUCTS(" + k + ") -->" + v);
//
//		});
//
//		pssweightss.forEach((k, v) -> {
//			System.out.println("SIMILARITY OF SERVICES(" + k + ") -->" + v);
//
//		});
		if (pssweights.isEmpty() && pssweightss.isEmpty())

		{
			obj.put("Op", "Error");
			obj.put("Message", "No prediction available");
			result.put(obj);
			return result;
		}
		String[] time = new String[12];
		time[0] = "JAN";
		time[1] = "FEB";
		time[2] = "MAR";
		time[3] = "APR";
		time[4] = "MAY";
		time[5] = "JUN";
		time[6] = "JUL";
		time[7] = "AUG";
		time[8] = "SEP";
		time[9] = "OCT";
		time[10] = "NOV";
		time[11] = "DEC";
		obj = new JSONObject();

		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -1);

		for (month = -1; month < 12 + data.get(Calendar.MONTH); month++) {
			totalWeight = 0;
			totalGsweight = 0;
			variance = 0;
			numbOfProd = 0;
			pssweights.forEach((k, v) -> {

				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				Calendar dateToShift = Calendar.getInstance();
				dateToShift.setTimeInMillis(firstDate(-1));
				//System.out.println(firstDate(-1) + " " + (dateToShift.get(Calendar.DAY_OF_MONTH)) + "/"
					//	+ (dateToShift.get(Calendar.MONTH) + 1) + "/" + dateToShift.get(Calendar.YEAR));
				
				if (month == -1)// first time it goes iun the for loop it shifts
								// the date
					month = dateToShift.get(Calendar.MONTH) + 1;

				tempvalue = globalsentimentby(dateToShift.get(Calendar.DAY_OF_MONTH), month % 12,
						dateToShift.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
				totalGsweight += (tempvalue == -1 ? 0 : v * tempvalue);
				Data.delmodel((long) -1);
				totalWeight += (tempvalue == -1 ? 0 : v);
				numbOfProd++;
			});
			pssweightss.forEach((k, v) -> {
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				Calendar dateToShift = Calendar.getInstance();
				dateToShift.setTimeInMillis(firstDate(-1));
				//System.out.println(firstDate(-1) + " " + (dateToShift.get(Calendar.DAY_OF_MONTH)) + "/"
				//		+ (dateToShift.get(Calendar.MONTH) + 1) + "/" + dateToShift.get(Calendar.YEAR));
				
				if (month == -1)// first time it goes iun the for loop it shifts
								// the date
					month = dateToShift.get(Calendar.MONTH) + 1;
				
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12,
						data.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
				totalGsweight += (tempvalue == -1 ? 0 : v * tempvalue);
				Data.delmodel((long) -1);
				totalWeight += (tempvalue == -1 ? 0 : v);
				numbOfProd++;
			});

			mean = (totalGsweight) / (totalWeight == 0 ? 1 : totalWeight);
			variance = 0;
			pssweights.forEach((k, v) -> {
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12,
						data.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
				variance += Math.pow(tempvalue - mean, 2);
			});

			pssweightss.forEach((k, v) -> {
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12,
						data.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
				variance += Math.pow(tempvalue - mean, 2);
			});

			variance += Math.pow(200 - mean, 2);

			if (totalGsweight != 0) {

				variance = variance / totalGsweight;
				stDeviation = Math.sqrt(variance);
			} else {
				mean = -1;
				variance = -1;
			}
			try {
				obj = new JSONObject();
				obj.put("Month", time[month % 12]);
				obj.put("Value", mean);
				obj.put("Variance", Math.round((1.96 * stDeviation) / Math.sqrt(numbOfProd)));// 95%
																								// confidence
																								// interval
				result.put(obj);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	public HashMap<Long, Double> predict(String productsId, String servicesId) throws JSONException {

		HashMap<Long, Double> pssSentiment = new HashMap<Long, Double>();

		HashMap<Long, Double> pssweights = Extrapolation.get_Similarity_Threshold(productsId, 75, true);
		HashMap<Long, Double> pssweightss = Extrapolation.get_Similarity_Threshold(servicesId, 60, false);

		// pssweights.forEach((k, v) -> {
		// System.out.println("SIMILARITY OF PRODUCTS(" + k + ") -->" + v);
		//
		// });

		// pssweightss.forEach((k, v) -> {
		// System.out.println("SIMILARITY OF SERVICES(" + k + ") -->" + v);
		//
		// });

		pssweightss.forEach((k2, v2) -> {
			if (pssweights.containsKey(k2))
				pssweights.put(k2, pssweights.get(k2) + v2);
			else
				pssweights.put(k2, v2);

		});

		// pssweights.forEach((k, v) -> {
		// System.out.println("SIMILARITY OF SERVICES & PRODUCTS(" + k + ") -->"
		// + v);
		//
		// });

		if (pssweights.isEmpty() && pssweightss.isEmpty()) {

			return null;
		}

		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -1);

		totalWeight = 0;
		totalGsweight = 0;

		pssweights.forEach((k, v) -> {
			for (month = data.get(Calendar.MONTH); month < 12 + data.get(Calendar.MONTH); month++) {
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12,
						data.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
				totalGsweight += (tempvalue == -1 ? 0 : v * tempvalue);
				Data.delmodel((long) -1);
				totalWeight += (tempvalue == -1 ? 0 : v);
			}
			mean = (totalGsweight) / ((totalWeight == 0 ? 1 : totalWeight));
			pssSentiment.put(k, mean);
		});

		// pssSentiment.forEach((k, v) -> {
		// System.out.println("PSS:(" + k + ") Sentiment-->" + v);
		//
		// });

		return pssSentiment;
	}

	public HashMap<Long, Double> predict(String company) throws JSONException {

		HashMap<Long, Double> pssSentiment = new HashMap<Long, Double>();
		ArrayList<Long> pss = new ArrayList<Long>();
		ArrayList<Long> dp;

		try {
			dp = Data.getcompanybyname(company).get_design_projects();

		} catch (Exception e1) {
			// LOGGER.log(Level.SEVERE, "Company does not exist",e1);
			return null;
		}

		try {
			for (Long dpid : dp) {
				pss.add(Data.getDp(dpid).getProducesPssId());
			}

		} catch (Exception e2) {
			// LOGGER.log(Level.SEVERE, "Company does not have design
			// projects",e2);
			return null;
		}

		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -1);

		totalWeight = 0;
		totalGsweight = 0;

		for (Long pssid : pss) {

			for (month = data.get(Calendar.MONTH); month < 12 + data.get(Calendar.MONTH); month++) {
				Data.addmodel((long) -1,
						new Model(-1, 0, 0, "", "", pssid, "0,150", "All", "-1", false, 0, 0, -1, true));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12,
						data.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
				totalGsweight += (tempvalue == -1 ? 0 : tempvalue);
				Data.delmodel((long) -1);
				totalWeight += (tempvalue == -1 ? 0 : 1);
			}
			mean = (totalGsweight) / ((totalWeight == 0 ? 1 : totalWeight));
			pssSentiment.put(pssid, mean);
		}

		return pssSentiment;
	}
}
