package extraction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.json.JSONArray;

import extraction.Extrapolation;
import general.Data;
import general.Model;
import general.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Prediction extends Globalsentiment {
	
	double totalSentiment, totalWeight, totalGsweight, variance, numbOfProd, maxValue, mean,tempvalue, stDeviation;
	int month,i;

	
	public Prediction(){
		Extrapolation ex = new Extrapolation();
		
	}
	
	
	public JSONArray predict(int timespan /* years */, String productsId, String servicesId)
		throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		HashMap<Long, Long> pssweights = Extrapolation.get_Similarity_Threshold(productsId, 75);
		if (pssweights.isEmpty())
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

		for (month = data.get(Calendar.MONTH); month < timespan * 12 + data.get(Calendar.MONTH); month++) {
			totalWeight=0;
			totalGsweight=0;
			variance=0;
			numbOfProd=0;
			maxValue=0;
			pssweights.forEach((k,v)->{
				Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1));
				tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12, data.get(Calendar.YEAR) + month / 12, "Global", "", (long)-1);
				totalGsweight += (tempvalue == -1 ? 0 : v*tempvalue);
				Data.delmodel((long) -1);
				totalWeight+=(tempvalue == -1 ? 0 : v);
				numbOfProd++;
				maxValue=(tempvalue>=Math.abs(maxValue)?Math.abs(tempvalue):Math.abs(maxValue));
				});
			
			mean=(totalGsweight)/(totalWeight==0?1:totalWeight);
			variance=0;
			pssweights.forEach((k,v)->{
			Data.addmodel((long) -1, new Model(-1, 0, 0, "", "", k, "0,150", "All", "-1", false, 0, 0, -1));
			tempvalue = globalsentimentby(data.get(Calendar.DAY_OF_MONTH), month % 12, data.get(Calendar.YEAR) + month / 12, "Global", "", (long)-1);
			variance+=Math.pow(tempvalue-mean, 2);
			});
			variance=variance/totalGsweight;
			stDeviation=Math.sqrt(variance);
				
			try {
				obj = new JSONObject();
				obj.put("Month", time[month % 12]);
				obj.put("Value",mean);
				obj.put("Variance",(1.96*stDeviation)/Math.sqrt(numbOfProd));// 95% confidence interval
				result.put(obj);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}
}
