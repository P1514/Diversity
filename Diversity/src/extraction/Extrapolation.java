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

import org.apache.commons.math3.fitting.*;
import org.apache.commons.math3.analysis.function.*;

public final class Extrapolation extends Globalsentiment {
	private static Extrapolation instance;
	private static final Logger LOGGER = new Logging().create(Extrapolation.class.getName());

	public Extrapolation() {
	}

	static {
		instance = new Extrapolation();
	}

	public JSONArray extrapolate(String param, String values, String output, long id, long frequency)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
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
		index--;
		int indexaux;
		double lastvalue;

		indexaux = obs.toList().size() - 1;
		lastvalue = (double) obs.toList().get(indexaux).getY();

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

				// obj.put("Value",sig.value(1.25));

				result.put(obj);
				index++;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	private static double get_Similarity(long product_id1, long product_id2) {
		ArrayList<Long> commonid = new ArrayList<Long>();
		if (product_id1 == product_id2)
			return 1;
		if (!(Data.dbhasproduct(product_id1) && Data.dbhasproduct(product_id2)))
			return 0;

		Product pro1 = Data.getProduct(product_id1);
		commonid.add(product_id1);
		int depth1 = 2;
		int founddepth = -1;
		if (pro1.getParent() != 0) {
			do {
				commonid.add(pro1.getParent());
				pro1 = Data.getProduct(pro1.getParent());
				depth1++;

			} while (pro1.getParent() != 0);
		}
		Product pro2 = Data.getProduct(product_id2);
		int depth2 = 2;
		if (commonid.contains(product_id2))
			founddepth = 0;
		if (pro2.getParent() != 0) {
			do {
				if (founddepth == -1) {
					if (commonid.contains(pro2.getParent()))
						founddepth = depth2 - 1;
				}
				pro2 = Data.getProduct(pro2.getParent());
				depth2++;
			} while (pro2.getParent() != 0);
		}
		double result = ((double) 2 * (founddepth == -1 ? 1 : depth2 - founddepth)) / ((double) (depth1 + depth2));
		return result;
	}
	
	private static double get_Similarity_Services(long service_id1, long service_id2) {
		ArrayList<Long> commonid = new ArrayList<Long>();
		if (service_id1 == service_id2)
			return 1;
		if (!(Data.dbhasservice(service_id1) && Data.dbhasservice(service_id2)))
			return 0;

		Product ser1 = Data.getService(service_id1);
		commonid.add(service_id1);
		int depth1 = 2;
		int founddepth = -1;
		if (ser1.getParent() != 0) {
			do {
				commonid.add(ser1.getParent());
				ser1 = Data.getService(ser1.getParent());
				depth1++;

	private static double get_Similarity_Services(long service_id1, long service_id2) {
		ArrayList<Long> commonid = new ArrayList<Long>();
		if (service_id1 == service_id2)
			return 1;
		if (!(Data.dbhasservice(service_id1) && Data.dbhasservice(service_id2)))
			return 0;

		Product ser1 = Data.getService(service_id1);
		commonid.add(service_id1);
		int depth1 = 2;
		int founddepth = -1;
		if (ser1.getParent() != 0) {
			do {
				commonid.add(ser1.getParent());
				ser1 = Data.getService(ser1.getParent());
				depth1++;


			} while (ser1.getParent() != 0);
		}
		Product ser2 = Data.getService(service_id2);
		int depth2 = 2;
		if (commonid.contains(service_id2))
			founddepth = 0;
		if (ser2.getParent() != 0) {
			do {
				if (founddepth == -1) {
					if (commonid.contains(ser2.getParent()))
						founddepth = depth2 - 1;
				}
				ser2 = Data.getService(ser2.getParent());
				depth2++;
			} while (ser2.getParent() != 0);
		}
		double result = ((double) 2 * (founddepth == -1 ? 1 : depth2 - founddepth)) / ((double) (depth1 + depth2));
		return result;
	}


	public static HashMap<Long, Double> get_Similarity_Threshold(String productsId, double threshold,
			boolean is_product) {

		if (productsId.isEmpty())
			return new HashMap<Long, Double>();

		HashMap<Long, Double> pssweights = new HashMap<Long, Double>();
		String[] products = productsId.split(";");
		HashMap<Long, Double> id_similarity = new HashMap<Long, Double>();
		for (String p : products) {
			try {

				id_similarity = get_Similarity_Threshold(Long.parseLong(p), threshold, is_product);
				id_similarity.forEach((k, v) -> {
					for (PSS pss : Data.dbpssall()) {
						if (is_product) {
							if (pss.get_products().contains(k)) {
								if (pssweights.containsKey(pss.getID())) {
									pssweights.put(pss.getID(), pssweights.get(pss.getID()) + v);
									
								} else {
									
									pssweights.put(pss.getID(), v);
								}
							}
						}
						else{
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
				LOGGER.log(Level.SEVERE, "Parsing String to Long error String = " + p);
				return null;
			}
		}


		return pssweights;

	}

	private static HashMap<Long, Double> get_Similarity_Threshold(long product_id, double threshold, boolean is_product) {
		HashMap<Long, Double> id_similarity = new HashMap<Long, Double>();
		while (threshold > 1)
			threshold = threshold / ((double) 100);
		if (is_product) {
			for (Product pro : Data.dbproductall()) {
				// if (pro.get_Id() == product_id)
				// continue;

				if (get_Similarity(product_id, pro.get_Id()) >= threshold) {
					id_similarity.put(pro.get_Id(), get_Similarity(product_id, pro.get_Id()));
					System.out.println("SIMILARITY OF PRODUCTS(" + pro.get_Id() + "," + product_id + ") -->"
							+ get_Similarity(product_id, pro.get_Id()));
				}
			}
		} else {
			for (Product ser : Data.dbserviceall()) {
				// if (ser.get_Id() == product_id)
				// continue;

				if (get_Similarity_Services(product_id, ser.get_Id()) >= threshold) {
					id_similarity.put(ser.get_Id(), get_Similarity_Services(product_id, ser.get_Id()));
					System.out.println("SIMILARITY OF SERVICES(" + ser.get_Id() + "," + product_id + ") -->"
							+ get_Similarity_Services(product_id, ser.get_Id()));
				}

			}
		}

		return id_similarity;

	}

}
