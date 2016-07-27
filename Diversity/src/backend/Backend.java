package backend;

import org.json.*;

import importDB.Data;
import importDB.PSS;

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
		int pss=0;;
		PSS ps = new PSS();
		try {
			if (msg.has("Pss")) {

				pss = ps.getID(msg.getString("Pss"));
			}

			param = (msg.has("Param")) ? msg.getString("Param") : null;
			values = (msg.has("Values")) ? msg.getString("Values") : null;
			System.out.println(msg);

			switch (op) {
			case 1:
				SentimentChart sc = new SentimentChart();
				return sc.chartrequest(param, values, pss).toString();
			case 2:
				Data dat = new Data();
				return dat.load();
			case 3:
				Globalsentiment gs = new Globalsentiment();
				tmp = gs.globalsentiment(1, param, values, pss).toString();
				return tmp;
			case 4:
				GetPosts gp = new GetPosts();
				tmp = gp.getTop(param, values, pss).toString();
				return tmp;
			case 5:
				return ps.getProducts();
			default:

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Mistake";
	}

}