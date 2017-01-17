package extraction;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.math3.fitting.*;

public class Extrapolation {
	private Globalsentiment gs;
	
	public Extrapolation(Globalsentiment _gs) {
		gs=_gs;
	}
	
	public JSONArray extrapolate(int timespan /* years */, String param, String values, String output, long id)
			throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

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
		final WeightedObservedPoints obs = new WeightedObservedPoints();

		for (month = data.get(Calendar.MONTH); month < timespan * 12 + data.get(Calendar.MONTH); month++) {
			obs.add(month % 12,gs.globalsentimentby(month % 12, data.get(Calendar.YEAR) + month / 12, param, values, id));
		}
		// Instantiate a Second-degree polynomial fitter.
		final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
		// Retrieve fitted parameters (coefficients of the polynomial function).
		final double[] coeff = fitter.fit(obs.toList());
		
		for (; month < timespan * 12 + data.get(Calendar.MONTH)+Math.floor((timespan * 12)/3); month++) {
			try {
				obj = new JSONObject();
				obj.put("Month", time[month % 12]);
				obj.put("Value",getFutureValue(coeff,month % 12));
				result.put(obj);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}
	
	private double getFutureValue(double [] coeff,int x){
		
		return coeff[0]+coeff[1]*x+coeff[2]*coeff[2]*x;
		
	}

}