package backend;

import org.json.*;

import importDB.CleanDB;
import importDB.Data;
import importDB.PSS;

public class Backend {
	private int op = 0;
	private JSONObject msg, obj;
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
		GetModels model;
		PSS pss = new PSS();
		Globalsentiment gs = new Globalsentiment();
		long id=0;
		try {
			if (msg.has("Id")) {

				id = msg.getLong("Id");
			}

			param = (msg.has("Param")) ? msg.getString("Param") : null;
			values = (msg.has("Values")) ? msg.getString("Values") : null;
			System.out.println(msg);

			switch (op) {
			case 99:
				result=new JSONArray();
				obj = new JSONObject();
				obj.put("Op", "OE_Redone");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Top_Left");
				obj.put("Param", "Global");
				obj.put("Value", "88");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Top_Middle");
				obj.put("Param", "--");
				obj.put("Value", "6");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Top_Middle");
				obj.put("Param", "-");
				obj.put("Value", "13");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Top_Middle");
				obj.put("Param", "0");
				obj.put("Value", "9");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Top_Middle");
				obj.put("Param", "+");
				obj.put("Value", "23");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Top_Middle");
				obj.put("Param", "++");
				obj.put("Value", "40");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Top_Right");
				obj.put("Value", "0.4");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Bottom_Left");
				obj.put("Param","Global");
				obj.put("Value", "1.3");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Bottom_Middle");
				obj.put("Month", "MAR");
				obj.put("Value", "0.6");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Bottom_Middle");
				obj.put("Month", "APR");
				obj.put("Value", "1.1");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Bottom_Middle");
				obj.put("Month", "MAY");
				obj.put("Value", "1.8");
				result.put(obj);
				obj = new JSONObject();
				obj.put("Graph", "Bottom_Middle");
				obj.put("Month", "AUG");
				obj.put("Value", "0.9");
				result.put(obj);
				
				JSONArray convert = gs.globalsentiment(1, param, values, id);
				
				for(int i=0; i<convert.length(); i++){
					obj=new JSONObject();
					JSONObject helper = convert.getJSONObject(i);
					obj.put("Graph", "Bottom_Right");
					for(String key : JSONObject.getNames(helper))
					{
					  obj.put(key, helper.get(key));
					}
					result.put(obj);
				}
				
				
	
				return result.toString();
			case 1:
				SentimentChart sc = new SentimentChart();
				return sc.chartrequest(param, values, id).toString();
			case 2:
				Data dat = new Data();
				return dat.load();
			case 3:
				gs = new Globalsentiment();
				tmp = gs.globalsentiment(1, param, values, id).toString();
				return tmp;
			case 4:
				GetPosts gp = new GetPosts();
				tmp = gp.getTop(param, values, id).toString();
				return tmp;
			case 5:
				model = new GetModels();
				return model.get_models().toString();
			case 6:
				GetComments gc = new GetComments();
				tmp = gc.getAll(msg).toString();
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
				tmp = gpo.getAll(param, id).toString();
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
				model = new GetModels();
				tmp = model.create_model(msg).toString();
				return tmp;
			case 15: 
				model = new GetModels();
				tmp = model.get_model(msg).toString();
				return tmp;
			case 16:
				model = new GetModels();
				tmp = model.update_model(msg).toString();
				return tmp;
			case 17: return pss.getPss();
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