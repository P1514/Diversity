package extraction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Data;
import general.Logging;
import general.PSS;
import general.Product;
import general.Settings;

import org.apache.commons.math3.fitting.*;

public final class Extrapolation extends Globalsentiment {
	private static Extrapolation instance;
	private static final Logger LOGGER = new Logging().create(Extrapolation.class.getName());



	static {
		instance = new Extrapolation();
	}

	public JSONArray extrapolate(String param, String values, String output, long id, long frequency)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj;
		double globalSentiment;
		boolean extraTest = true;

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
		obj.put("Filter", output);
		result.put(obj);

		Calendar data = Calendar.getInstance();
		Calendar today = Calendar.getInstance();

		Calendar dataAux = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);

		data.setTimeInMillis(firstDate(id));
		data.add(Calendar.DAY_OF_MONTH, (int) frequency);
		int index = 0;

		WeightedObservedPoints obs = new WeightedObservedPoints();

		if (firstDate(id) != 0) {
			for (; today.after(data); data.add(Calendar.DAY_OF_MONTH, (int) frequency)) {
				globalSentiment = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), data.get(Calendar.MONTH) + 1,
						data.get(Calendar.YEAR), param, values, id, frequency);
				if (globalSentiment != -1) {
					obs.add(index, globalSentiment);
					dataAux.set(data.get(Calendar.YEAR), data.get(Calendar.MONTH), data.get(Calendar.DAY_OF_MONTH));
				}
				index++;
			}
		}

		// Instantiate a third-degree polynomial fitter.
		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(3);
		// Retrieve fitted parameters (coefficients of the polynomial function).
		double[] coeff = fitter.fit(obs.toList());
		data.add(Calendar.DAY_OF_MONTH, (int) -frequency);
		int indexaux;
		double lastvalue;

		indexaux = obs.toList().size() - 1;
		lastvalue =  obs.toList().get(indexaux).getY();

		coeff[0] = lastvalue
				- (coeff[1] * indexaux + coeff[2] * indexaux * indexaux + coeff[3] * indexaux * indexaux * indexaux);

		today.add(Calendar.MONTH, 3);
		if (extraTest) {
			data.setTimeInMillis(firstDate(id));
			data.add(Calendar.DAY_OF_MONTH, (int) frequency);
			index = 0;
		} else {
			data = dataAux;
			index = obs.toList().size() - 1;
		}

		for (; today.after(data); data.add(Calendar.DAY_OF_MONTH, (int) frequency)) {
			try {

				obj = new JSONObject();
				obj.put("Date", data.get(Calendar.DAY_OF_MONTH) + " " + (data.get(Calendar.MONTH) + 1) + " "
						+ data.get(Calendar.YEAR));
				if (getFutureValue(coeff, index) >= 0)
					obj.put("Value", getFutureValue(coeff, index) > 100 ? 100
							: getFutureValue(coeff,
									index)/*
											 * sig.value((getFutureValue( coeff,
											 * index) *20))
											 */);
				else
					obj.put("Value", 0/*
										 * sig.value((getFutureValue( coeff,
										 * index) *20))
										 */);


				result.put(obj);
				index++;

			} catch (JSONException e) {
				LOGGER.log(Level.INFO, Settings.err_unknown,e);
			}
		}

		return result;
	}

	private double getFutureValue(double[] coeff, int x) {

		return coeff[0] + coeff[1] * x + coeff[2] * x * x + coeff[3] * x * x * x;

	}

	public static Extrapolation getInstance() {
		return instance;
	}

	private static double getSimilarity(long productId1, long productId2) {
		ArrayList<Long> commonid = new ArrayList<>();
		if (productId1 == productId2)
			return 1;
		if (!(Data.dbhasproduct(productId1) && Data.dbhasproduct(productId2)))
			return 0;

		Product pro1 = Data.getProduct(productId1);
		commonid.add(productId1);
		int depth1 = 2;
		int founddepth = -1;
		if (pro1.getParent() != 0) {
			do {
				commonid.add(pro1.getParent());
				pro1 = Data.getProduct(pro1.getParent());
				depth1++;

			} while (pro1.getParent() != 0);
		}
		Product pro2 = Data.getProduct(productId2);
		int depth2 = 2;
		if (commonid.contains(productId2))
			founddepth = 0;
		if (pro2.getParent() != 0) {
			do {
				if (founddepth == -1 &&commonid.contains(pro2.getParent())) {
			
						founddepth = depth2 - 1;
				}
				pro2 = Data.getProduct(pro2.getParent());
				depth2++;
			} while (pro2.getParent() != 0);
		}
		return ((double) 2 * (founddepth == -1 ? 1 : depth2 - founddepth)) / ((double) (depth1 + depth2));
	}

	private static double getSimilarityServices(long serviceId1, long serviceId2) {
		ArrayList<Long> commonid = new ArrayList<>();
		if (serviceId1 == serviceId2)
			return 1;
		if (!(Data.dbhasservice(serviceId1) && Data.dbhasservice(serviceId2)))
			return 0;

		Product ser1 = Data.getService(serviceId1);
		commonid.add(serviceId1);
		int depth1 = 2;
		int founddepth = -1;
		if (ser1.getParent() != 0) {
			do {
				commonid.add(ser1.getParent());
				ser1 = Data.getService(ser1.getParent());
				depth1++;

			} while (ser1.getParent() != 0);
		}
		Product ser2 = Data.getService(serviceId2);
		int depth2 = 2;
		if (commonid.contains(serviceId2))
			founddepth = 0;
		if (ser2.getParent() != 0) {
			do {
				if (founddepth == -1 &&commonid.contains(ser2.getParent())) {
						founddepth = depth2 - 1;
				}
				ser2 = Data.getService(ser2.getParent());
				depth2++;
			} while (ser2.getParent() != 0);
		}
		return ((double) 2 * (founddepth == -1 ? 1 : depth2 - founddepth)) / ((double) (depth1 + depth2));
	}

	public static HashMap<Long, Double> getSimilarityThreshold(String productsId, double threshold,
			boolean isProduct) {
		if (productsId.isEmpty())
			return new HashMap<>();

		HashMap<Long, Double> pssweights = new HashMap<>();
		String[] products = productsId.split(";");
		HashMap<Long, Double> idSimilarity;
		for (String p : products) {
			try {
				idSimilarity = getSimilarityThreshold(Long.parseLong(p), threshold, isProduct);
				idSimilarity.forEach((k, v) -> {
					for (PSS pss : Data.dbpssall()) {
						if (isProduct) {
							if (pss.get_products().contains(k)) {
								if (pssweights.containsKey(pss.getID())) {
									pssweights.put(pss.getID(), pssweights.get(pss.getID()) + v);

								} else {

									pssweights.put(pss.getID(), v);
								}
							}
						} else {
							if (pss.get_services().contains(k)) {
								if (pssweights.containsKey(pss.getID())) {
									pssweights.put(pss.getID(), pssweights.get(pss.getID()) + v);

								} else {

									pssweights.put(pss.getID(), v);
								}
							}
						}
					}
				});
			} catch (NumberFormatException e1) {
				LOGGER.log(Level.INFO, Settings.err_unknown,e1);
				return null;
			}
		}

		return pssweights;

	}

	private static HashMap<Long, Double> getSimilarityThreshold(long productId, double threshold,
			boolean isProduct) {
		double thresholdef= threshold;
		HashMap<Long, Double> idSimilarity = new HashMap<>();
		while (thresholdef > 1)
			thresholdef = thresholdef / ((double) 100);
		if (isProduct) {
			for (Product pro : Data.dbproductall()) {

				if (getSimilarity(productId, pro.get_Id()) >= thresholdef) {
					idSimilarity.put(pro.get_Id(), getSimilarity(productId, pro.get_Id()));
				}
			}
		} else {
			for (Product ser : Data.dbserviceall()) {

				if (getSimilarityServices(productId, ser.get_Id()) >= thresholdef) {
					idSimilarity.put(ser.get_Id(), getSimilarityServices(productId, ser.get_Id()));
				}
			}
		}

		return idSimilarity;

	}

}
