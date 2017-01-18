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
import general.PSS;
import general.Product;

import org.apache.commons.math3.fitting.*;
import org.apache.commons.math3.analysis.function.*;

public final class Extrapolation extends Globalsentiment {
	private static Extrapolation instance;
	private static final Logger LOGGER = Logger.getLogger(Data.class.getName());

	public Extrapolation() {
	}

	static {
		instance = new Extrapolation();
	}

	public JSONArray extrapolate(int timespan /* years */, String param, String values, String output, long id)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		Sigmoid sig = new Sigmoid(-100, 100);

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
		data.add(Calendar.MONTH, 1);
		data.add(Calendar.YEAR, -1);
		int month;
		int index = 0;

		WeightedObservedPoints obs = new WeightedObservedPoints();

		for (month = data.get(Calendar.MONTH); month < timespan * 12 + data.get(Calendar.MONTH); month++) {
			if (globalsentimentby(month % 12, data.get(Calendar.YEAR) + month / 12, param, values, id) != -1)
				obs.add(index, globalsentimentby(month % 12, data.get(Calendar.YEAR) + month / 12, param, values, id));
			index++;

		}
		// Instantiate a Second-degree polynomial fitter.
		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
		// Retrieve fitted parameters (coefficients of the polynomial function).
		double[] coeff = fitter.fit(obs.toList());
		month--;
		index--;
		// index=0;

		for (/* month = data.get(Calendar.MONTH) */; month < timespan * 12 + data.get(Calendar.MONTH)
				+ Math.floor((timespan * 12) / 3) - 1; month++) {
			try {
				obj = new JSONObject();
				obj.put("Month", time[month % 12]);
				obj.put("Value", sig.value((getFutureValue(coeff, index) / 50)));
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

		return coeff[0] + coeff[1] * x + coeff[2] * x * x;

	}

	public static Extrapolation getInstance() {
		return instance;
	}

	private static double get_Similarity(long product_id1, long product_id2) {
		ArrayList<Long> commonid = new ArrayList<Long>();
		if (product_id1 == product_id2)
			return 1;
		if (!(Data.productdb.containsKey(product_id1) && Data.productdb.containsKey(product_id2)))
			return 0;

		Product pro1 = Data.productdb.get(product_id1);
		commonid.add(product_id1);
		int depth1 = 2;
		int founddepth = -1;
		if (pro1.getParent() != 0) {
			do {
				commonid.add(pro1.getParent());
				pro1 = Data.productdb.get(pro1.getParent());
				depth1++;

			} while (pro1.getParent() != 0);
		}
		Product pro2 = Data.productdb.get(product_id2);
		int depth2 = 2;
		if (commonid.contains(product_id2))
			founddepth = 0;
		if (pro2.getParent() != 0) {
			do {
				if (founddepth == -1) {
					if (commonid.contains(pro2.getParent()))
						founddepth = depth2 - 1;
				}
				pro2 = Data.productdb.get(pro2.getParent());
				depth2++;
			} while (pro2.getParent() != 0);
		}
		double result = ((double) 2 * (founddepth == -1 ? 1 : depth2 - founddepth)) / ((double) (depth1 + depth2));
		return result;
	}

	public static HashMap<Long, Long> get_Similarity_Threshold(String productsId, double threshold) {
		if (productsId.isEmpty())
			return null;

		HashMap<Long, Long> pssweights = new HashMap<Long, Long>();
		String[] products = productsId.split(";");
		ArrayList<Long> simproducts = new ArrayList<Long>();
		for (String p : products) {
			try {
				simproducts.addAll(get_Similarity_Threshold(Long.parseLong(p), threshold));
			} catch (NumberFormatException e1) {
				LOGGER.log(Level.SEVERE, "Parsing String to Long error String = " + p);
				return null;
			}
		}
			for (Long id : simproducts) {
				for (PSS pss : Data.pssdb.values()) {
					if (pss.get_products().contains(id)) {
						if (pssweights.containsKey(pss.getID())) {
							pssweights.put(pss.getID(), pssweights.get(pss.getID()) + 1);
						} else {
							pssweights.put(pss.getID(), (long) 1);
						}
					}
				}
			}

		return pssweights;

	}

	private static ArrayList<Long> get_Similarity_Threshold(long product_id, double threshold) {
		ArrayList<Long> id_list = new ArrayList<Long>();
		while (threshold > 1)
			threshold = threshold / ((double) 100);

		for (Product pro : Data.productdb.values()) {
			if (pro.get_Id() == product_id)
				continue;

			if (get_Similarity(product_id, pro.get_Id()) >= threshold)
				id_list.add(pro.get_Id());
		}

		return id_list;

	}

}
