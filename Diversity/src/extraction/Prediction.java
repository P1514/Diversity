package extraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import org.json.JSONArray;

import extraction.Extrapolation;
import general.Backend;
import general.Data;
import general.DesignProject;
import general.Logging;
import general.Model;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.Session;

public class Prediction extends Globalsentiment {

	double totalSentiment, totalWeight, totalGsweight, variance, numbOfProd, maxValue, mean, tempvalue, stDeviation;
	int month, i;
	public static final Logger LOGGER = new Logging().create(Data.class.getName());
	Calendar data = Calendar.getInstance();
	private long update_size=0;
	private long hidden_size;

	public Prediction() {

	}

	public JSONArray predict(int timespan /* years */, String productsId, String servicesId) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		HashMap<Long, Double> pssweights = Extrapolation.getSimilarityThreshold(productsId, 75, true);
		HashMap<Long, Double> pssweightss = Extrapolation.getSimilarityThreshold(servicesId, 60, false);

		// pssweights.forEach((k, v) -> {
		// System.out.println("SIMILARITY OF PRODUCTS(" + k + ") -->" + v);
		//
		// });
		//
		// pssweightss.forEach((k, v) -> {
		// System.out.println("SIMILARITY OF SERVICES(" + k + ") -->" + v);
		//
		// });
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
				Data.delmodel((long) -1);
			});

			pssweightss.forEach((k, v) -> {
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12,
						data.get(Calendar.YEAR) + month / 12, "Global", "", (long) -1, -1);
				variance += Math.pow(tempvalue - mean, 2);
				Data.delmodel((long) -1);
			});

			variance += Math.pow(200 - mean, 2);

			if (totalGsweight != 0) {

				variance = variance / totalGsweight;
				stDeviation = Math.sqrt(variance);
				variance = Math.round((1.96 * stDeviation) / Math.sqrt(numbOfProd));
			} else {
				mean = -1;
				variance = -1;
			}
			try {
				obj = new JSONObject();
				obj.put("Month", time[month % 12]);
				obj.put("Value", mean);
				obj.put("Variance", variance);// 95%
												// confidence
												// interval
				result.put(obj);

			} catch (JSONException e) {
				LOGGER.log(Level.WARNING,"Class:Prediction, ERROR1");
			}
		}
		String productsName = "";
		String servicesName = "";

		if (!productsId.equals("")) {
			String[] products = productsId.split(";");

			for (String s : products) {
				if (Data.dbhasproduct(Long.parseLong(s)))
					productsName += Data.getProduct(Long.parseLong(s)).get_Name() + ",";
			}
		}
		if (!servicesId.equals("")) {
			String[] services = servicesId.split(";");

			for (String s : services) {
				if (Data.dbhasservice(Long.parseLong(s)))
					servicesName += Data.getService(Long.parseLong(s)).get_Name() + ",";
			}
		}

		try {
			obj = new JSONObject();
			obj.put("Products", productsName);
			obj.put("Services", servicesName);
			result.put(obj);
		} catch (JSONException e) {
			LOGGER.log(Level.WARNING,"Class:Prediction, ERROR 2");
		}

		return result;
	}

	Calendar firstdate = Calendar.getInstance();
	Calendar firstdateaux = Calendar.getInstance();

	public JSONArray predictLifeCycle(int timespan /* years */, String productsId, String servicesId)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		HashMap<Long, Double> pssweights = Extrapolation.getSimilarityThreshold(productsId, 75, true);
		HashMap<Long, Double> pssweightss = Extrapolation.getSimilarityThreshold(servicesId, 60, false);

		// pssweights.forEach((k, v) -> {
		// System.out.println("SIMILARITY OF PRODUCTS(" + k + ") -->" + v);
		//
		// });
		//
		// pssweightss.forEach((k, v) -> {
		// System.out.println("SIMILARITY OF SERVICES(" + k + ") -->" + v);
		//
		// });
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

		pssweights.forEach((k, v) -> {
			Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
			firstdateaux.setTimeInMillis(firstDate(-1));
			if (firstdateaux.before(firstdate)) {
				firstdate = firstdateaux;
			}
			Data.delmodel((long) -1);
		});

		pssweightss.forEach((k, v) -> {
			Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
			firstdateaux.setTimeInMillis(firstDate(-1));
			if (firstdateaux.before(firstdate)) {
				firstdate = firstdateaux;
			}
			Data.delmodel((long) -1);
		});

		Calendar data = firstdate;
		Calendar today = Calendar.getInstance();
		for (month = 0; today.after(data); data.add(Calendar.MONTH, 1)) {
			totalWeight = 0;
			totalGsweight = 0;
			variance = 0;
			numbOfProd = 0;
			// System.out.println((data.get(Calendar.DAY_OF_MONTH)) + "/"
			// + (data.get(Calendar.MONTH) + 1) + "/" +
			// data.get(Calendar.YEAR));
			// System.out.println((today.get(Calendar.DAY_OF_MONTH)) + "/"
			// + (today.get(Calendar.MONTH) + 1) + "/" +
			// today.get(Calendar.YEAR));
			pssweights.forEach((k, v) -> {

				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				Calendar dateToShift = Calendar.getInstance();
				dateToShift.setTimeInMillis(firstDate(-1));
				// System.out.println(firstDate(-1) + " " +
				// (dateToShift.get(Calendar.DAY_OF_MONTH)) + "/"
				// + (dateToShift.get(Calendar.MONTH) + 1) + "/" +
				// dateToShift.get(Calendar.YEAR));
				dateToShift.add(Calendar.MONTH, month);

				tempvalue = globalsentimentby(dateToShift.get(Calendar.DAY_OF_MONTH), dateToShift.get(Calendar.MONTH),
						dateToShift.get(Calendar.YEAR), "Global", "", (long) -1, -1);
				totalGsweight += (tempvalue == -1 ? 0 : v * tempvalue);
				Data.delmodel((long) -1);
				totalWeight += (tempvalue == -1 ? 0 : v);
				if(tempvalue!=-1)
				numbOfProd++;
			});
			pssweightss.forEach((k, v) -> {

				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				Calendar dateToShift = Calendar.getInstance();
				dateToShift.setTimeInMillis(firstDate(-1));
				// System.out.println(firstDate(-1) + " " +
				// (dateToShift.get(Calendar.DAY_OF_MONTH)) + "/"
				// + (dateToShift.get(Calendar.MONTH) + 1) + "/" +
				// dateToShift.get(Calendar.YEAR));
				dateToShift.add(Calendar.MONTH, month);

				tempvalue = globalsentimentby(dateToShift.get(Calendar.DAY_OF_MONTH), dateToShift.get(Calendar.MONTH),
						dateToShift.get(Calendar.YEAR), "Global", "", (long) -1, -1);
				totalGsweight += (tempvalue == -1 ? 0 : v * tempvalue);
				Data.delmodel((long) -1);
				totalWeight += (tempvalue == -1 ? 0 : v);
				if(tempvalue!=-1)
				numbOfProd++;
			});

			mean = (totalGsweight) / (totalWeight == 0 ? 1 : totalWeight);
			variance = 0;
			pssweights.forEach((k, v) -> {
				Calendar dateToShift = Calendar.getInstance();

				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				dateToShift.setTimeInMillis(firstDate(-1));
				// System.out.println(firstDate(-1) + " " +
				// (dateToShift.get(Calendar.DAY_OF_MONTH)) + "/"
				// + (dateToShift.get(Calendar.MONTH) + 1) + "/" +
				// dateToShift.get(Calendar.YEAR));
				dateToShift.add(Calendar.MONTH, month);

				tempvalue = globalsentimentby(dateToShift.get(Calendar.DAY_OF_MONTH), dateToShift.get(Calendar.MONTH),
						dateToShift.get(Calendar.YEAR), "Global", "", (long) -1, -1);
				if(tempvalue!=-1)
				variance += Math.pow(tempvalue - mean, 2);
				Data.delmodel((long) -1);
			});

			pssweightss.forEach((k, v) -> {
				Calendar dateToShift = Calendar.getInstance();
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				dateToShift.setTimeInMillis(firstDate(-1));
				// System.out.println(firstDate(-1) + " " +
				// (dateToShift.get(Calendar.DAY_OF_MONTH)) + "/"
				// + (dateToShift.get(Calendar.MONTH) + 1) + "/" +
				// dateToShift.get(Calendar.YEAR));
				dateToShift.add(Calendar.MONTH, month);
				tempvalue = globalsentimentby(dateToShift.get(Calendar.DAY_OF_MONTH), dateToShift.get(Calendar.MONTH),
						dateToShift.get(Calendar.YEAR), "Global", "", (long) -1, -1);
				if(tempvalue!=-1)
				variance += Math.pow(tempvalue - mean, 2);
				Data.delmodel((long) -1);

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
				obj.put("Month", time[data.get(Calendar.MONTH)]);
				obj.put("Value", mean);
				obj.put("Variance", Math.round((1.96 * stDeviation) / Math.sqrt(numbOfProd)));// 95%
																								// confidence
																								// interval
				result.put(obj);

			} catch (JSONException e) {
				LOGGER.log(Level.WARNING,"Class:Prediction, ERROR 3");
			}
			month++;
		}

		String productsName = "";
		String servicesName = "";
		if (!productsId.equals("")) {
			String[] products = productsId.split(";");

			for (String s : products) {
				if (Data.dbhasproduct(Long.parseLong(s)))
					productsName += Data.getProduct(Long.parseLong(s)).get_Name() + ",";
			}
		}
		if (!servicesId.equals("")) {
			String[] services = servicesId.split(";");

			for (String s : services) {
				if (Data.dbhasservice(Long.parseLong(s)))
					servicesName += Data.getService(Long.parseLong(s)).get_Name() + ",";
			}
		}
		try {
			obj = new JSONObject();
			obj.put("Products", productsName);
			obj.put("Services", servicesName);
			result.put(obj);
		} catch (JSONException e) {
			LOGGER.log(Level.WARNING,"Class:Prediction, ERROR 4");
		}

		return result;
	}

	public JSONArray predictSeassonal(int timespan /* years */, String productsId, String servicesId)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		HashMap<Long, Double> pssweights = Extrapolation.getSimilarityThreshold(productsId, 75, true);
		HashMap<Long, Double> pssweightss = Extrapolation.getSimilarityThreshold(servicesId, 60, false);

		// pssweights.forEach((k, v) -> {
		// System.out.println("SIMILARITY OF PRODUCTS(" + k + ") -->" + v);
		//
		// });
		//
		// pssweightss.forEach((k, v) -> {
		// System.out.println("SIMILARITY OF SERVICES(" + k + ") -->" + v);
		//
		// });
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

		pssweights.forEach((k, v) -> {
			Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
			firstdateaux.setTimeInMillis(firstDate(-1));
			if (firstdateaux.before(firstdate)) {
				firstdate = firstdateaux;
			}
			Data.delmodel((long) -1);
		});

		pssweightss.forEach((k, v) -> {
			Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
			firstdateaux.setTimeInMillis(firstDate(-1));
			if (firstdateaux.before(firstdate)) {
				firstdate = firstdateaux;
			}
			Data.delmodel((long) -1);
		});

		Calendar today = Calendar.getInstance();
		for (month = 0; month < 12; month++) {
			totalWeight = 0;
			totalGsweight = 0;
			variance = 0;
			numbOfProd = 0;
			// System.out.println((data.get(Calendar.DAY_OF_MONTH)) + "/" +
			// (data.get(Calendar.MONTH) + 1) + "/"
			// + data.get(Calendar.YEAR));
			// System.out.println((today.get(Calendar.DAY_OF_MONTH)) + "/"
			// + (today.get(Calendar.MONTH) + 1) + "/" +
			// today.get(Calendar.YEAR));
			data.set(firstdate.get(Calendar.YEAR), month, firstdate.get(Calendar.DAY_OF_MONTH));

			// System.out.println((data.get(Calendar.DAY_OF_MONTH)) + "/" +
			// (data.get(Calendar.MONTH) + 1) + "/"
			// + data.get(Calendar.YEAR));
			variance = 0;
			for (; today.after(data); data.add(Calendar.YEAR, 1)) {
				pssweights.forEach((k, v) -> {

					Data.addmodel((long) -1,
							new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));

					tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month,
							data.get(Calendar.YEAR), "Global", "", (long) -1, -1);
					if(tempvalue==0)
						tempvalue=-1;
					totalGsweight += (tempvalue == -1 ? 0 : v * tempvalue);
					Data.delmodel((long) -1);
					totalWeight += (tempvalue == -1 ? 0 : v);
					if(tempvalue!=-1)
					numbOfProd++;
				});
				pssweightss.forEach((k, v) -> {

					Data.addmodel((long) -1,
							new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));

					tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month,
							data.get(Calendar.YEAR), "Global", "", (long) -1, -1);
					if(tempvalue==0)
						tempvalue=-1;
					totalGsweight += (tempvalue == -1 ? 0 : v * tempvalue);
					Data.delmodel((long) -1);
					totalWeight += (tempvalue == -1 ? 0 : v);
					if(tempvalue!=-1)
					numbOfProd++;
				});
			}

			mean = (totalGsweight) / (totalWeight == 0 ? 1 : totalWeight);
			data.set(firstdate.get(Calendar.YEAR), month, firstdate.get(Calendar.DAY_OF_MONTH));
			// System.out.println((data.get(Calendar.DAY_OF_MONTH)) + "/" +
			// (data.get(Calendar.MONTH) + 1) + "/"
			// + data.get(Calendar.YEAR));
			for (; today.after(data); data.add(Calendar.YEAR, 1)) {
				pssweights.forEach((k, v) -> {
					Data.addmodel((long) -1,
							new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
					tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month,
							data.get(Calendar.YEAR), "Global", "", (long) -1, -1);
					if(tempvalue==0)
						tempvalue=-1;
					if(tempvalue!=-1)
					variance += Math.pow(tempvalue - mean, 2);
					Data.delmodel((long) -1);
				});

				pssweightss.forEach((k, v) -> {
					Data.addmodel((long) -1,
							new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
					tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month,
							data.get(Calendar.YEAR), "Global", "", (long) -1, -1);
					if(tempvalue==0)
						tempvalue=-1;
					if(tempvalue!=-1)
					variance += Math.pow(tempvalue - mean, 2);
					Data.delmodel((long) -1);
				});
			}

			variance += Math.pow(200 - mean, 2);

			if (totalGsweight != 0) {

				variance = variance / totalGsweight;
				stDeviation = Math.sqrt(variance);
				variance = Math.round((1.96 * stDeviation) / Math.sqrt(numbOfProd));
			} else {
				mean = -1;
				variance = -1;
			}
			try {
				obj = new JSONObject();
				obj.put("Month", time[(month) % 12]);
				obj.put("Value", mean);
				obj.put("Variance", variance);// 95%
												// confidence
												// interval
				result.put(obj);
			} catch (JSONException e) {
				LOGGER.log(Level.WARNING,"Class:Prediction, ERROR 5");
			}

		}

		String productsName = "";
		String servicesName = "";
		if (!productsId.equals("")) {
			String[] products = productsId.split(";");

			for (String s : products) {
				if (Data.dbhasproduct(Long.parseLong(s)))
					productsName += Data.getProduct(Long.parseLong(s)).get_Name() + ",";
			}
		}
		if (!servicesId.equals("")) {
			String[] services = servicesId.split(";");

			for (String s : services) {
				if (Data.dbhasservice(Long.parseLong(s)))
					servicesName += Data.getService(Long.parseLong(s)).get_Name() + ",";
			}
		}
		try {
			obj = new JSONObject();
			obj.put("Products", productsName);
			obj.put("Services", servicesName);
			result.put(obj);
		} catch (JSONException e) {
			LOGGER.log(Level.WARNING,"Class:Prediction, ERROR 6");
		}

		return result;
	}

	public HashMap<Long, Double> predict(String productsId, String servicesId) throws JSONException {

		HashMap<Long, Double> pssSentiment = new HashMap<Long, Double>();

		HashMap<Long, Double> pssweights = Extrapolation.getSimilarityThreshold(productsId, 75, true);
		HashMap<Long, Double> pssweightss = Extrapolation.getSimilarityThreshold(servicesId, 60, false);

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



		totalWeight = 0;
		totalGsweight = 0;
		Calendar today = Calendar.getInstance();
		
		pssweights.forEach((k, v) -> {
			Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
			firstdate.setTimeInMillis(firstDate(-1));
			Data.delmodel((long) -1);
			Calendar data = firstdate;
			for (month = firstdate.get(Calendar.MONTH); today.after(data); data.add(Calendar.MONTH, 1)) {
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1, true));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), data.get(Calendar.MONTH),
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
	
	private void updateLoadBar(long step, long done, long total, Session session) {
		if(session == null)return;
		if(total!=0) {
			update_size=total;
		}
		double percentage= ((double)done)/((double)update_size)*100;
		JSONArray msg=new JSONArray();
		try {
			JSONObject obj = new JSONObject();
			obj.put("Op", "Loading");
			obj.put("Ammount", percentage);
			msg.put(obj);
		} catch (JSONException e) {
			LOGGER.log(Level.WARNING,"Class:Prediction, ERROR 7");
			return;
		}
		LOGGER.log(Level.INFO, "OUT: " + msg.toString());
		try {
			session.getBasicRemote().sendText(msg.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.WARNING,"Class:Prediction, ERROR 8");
		}
	}

	public HashMap<Long, Double> predict(String company,Session session) throws JSONException {

		HashMap<Long, Double> pssSentiment = new HashMap<Long, Double>();
		ArrayList<Long> pss = new ArrayList<Long>();
		ArrayList<Long> dp;

		try {

			if (company.equals("")) {
				Set<Long> keySet = Data.getallDp().keySet();
				dp = new ArrayList<Long>(keySet);
			} else
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

		totalWeight = 0;
		totalGsweight = 0;
		Calendar today = Calendar.getInstance();
		updateLoadBar(0, 0, pss.size(), session);
		hidden_size=1;
		for (Long pssid : pss) {
			updateLoadBar(0, hidden_size++, pss.size(), session);
			Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", pssid, "0,150", "All", "-1", false, 0, 0, -1, true));
			firstdate.setTimeInMillis(firstDate(-1));
			Data.delmodel((long) -1);
			Calendar data = firstdate;
			for (month = firstdate.get(Calendar.MONTH); today.after(data); data.add(Calendar.MONTH, 1)) {
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", pssid, "0,150", "All", "-1", false, 0, 0, -1, true));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), data.get(Calendar.MONTH),
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
