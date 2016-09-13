package backend;

import org.json.*;

import importDB.CleanDB;
import importDB.Data;
import importDB.PSS;

public class Backend {
	private int op = 0;
	private JSONObject msg;
	private JSONArray result;

	public Backend(int _op, JSONObject _msg) {
		op = _op;
		msg = _msg;

	}

	public String resolve() {
		String param;
		String values;
		String tmp;
		Settings conf;
		Models model;
		int pss = 0;
		;
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
			case 6:
				GetComments gc = new GetComments();
				tmp = gc.getAll(param, values).toString();
				return tmp;
			case 7:
				CleanDB cdb = new CleanDB();
				tmp = cdb.clean();
				return tmp;
			case 8:
				GetAuthors ga = new GetAuthors();
				tmp = ga.getAll().toString();
				return tmp;
			case 9:
				GetLastPost glp = new GetLastPost();
				tmp = glp.get(msg.getString("Author")).toString();
				return tmp;
			case 10:
				GetInfGraph gig = new GetInfGraph();
				tmp = gig.getAll(msg.getString("Author")).toString();
				return tmp;
			case 11:
				GetPopulation gpo = new GetPopulation();
				tmp = gpo.getAll(param, pss).toString();
				return tmp;
			case 12:
				conf = new Settings();
				tmp = conf.getConf().toString();
				return tmp;
			case 13:
				conf = new Settings();
				tmp = conf.setConf(msg).toString();
				return tmp;
			case 14:
				model = new Models();
				tmp = model.create_model(msg).toString();
				return tmp;
			case 15: 
				model = new Models();
				tmp = model.get_model(msg).toString();
				return tmp;
			default:
				msg = new JSONObject();
				msg.put("Op", "Error");
				msg.put("Message", "NOT A VALID OPERATION");
				result = new JSONArray();
				result.put(msg);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result.toString();
	}

}