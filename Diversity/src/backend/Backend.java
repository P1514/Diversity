package backend;

import org.json.*;

import importDB.Data;

public class Backend {
	private int op = 0;
	private JSONObject msg;

	public Backend(int _op, JSONObject _msg) {
		op = _op;
		msg = _msg;

	}

	public String resolve() {
		String param;
		String values;
		String tmp;
		try {
			param = (msg.has("Param")) ? msg.getString("Param") : null;
			values = (msg.has("Values")) ? msg.getString("Values") : null;
			
			switch (op) {
			case 1:
				SentimentChart sc = new SentimentChart();
				return sc.chartrequest(param,values).toString();
			case 2:
				Data dat = new Data();
				return dat.load();
			case 3:
				Globalsentiment gs = new Globalsentiment();
				tmp = gs.globalsentiment(1, 5, param, values).toString();
				System.out.println(tmp);
				return tmp;
			case 4:
				GetPosts gp = new GetPosts();
				tmp = gp.getTop(param, values).toString();
				return tmp;
			default:

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Mistake";
	}

}