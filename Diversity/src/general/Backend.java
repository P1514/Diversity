package general;

import java.util.ArrayList;

import org.json.*;

import extraction.GetAuthors;
import extraction.GetComments;
import extraction.GetLastPost;
import extraction.GetPosts;
import extraction.GetReach;
import extraction.Globalsentiment;
import extraction.SentimentChart;
import modeling.GetModels;

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
		String filtering;
		String tmp;
		String[] filter = { "Global" };
		Settings conf;
		GetModels model;
		GetPosts gp = new GetPosts();
		Globalsentiment gs = new Globalsentiment();
		GetReach gr = new GetReach();
		long id = 0;
		try {
			if (msg.has("Id")) {

				id = msg.getLong("Id");
			}

			param = (msg.has("Param")) ? msg.getString("Param") : null;
			values = (msg.has("Values")) ? msg.getString("Values") : null;
			filtering = (msg.has("Filter")) ? msg.getString("Filter") : null;
			System.out.println(msg);
			if (filtering != null) {
				switch (filtering) {
				case "Age":
					filter = Settings.ages.split(",,");
					break;
				case "Gender":
					filter = Settings.genders.split(",,");
					break;
				case "Location":
					filter = Settings.locations.split(",,");
					break;
				}

			}

			switch (op) {
			case 99:
				result = new JSONArray();
				result.put(new JSONObject().put("Op", "Tree"));
				JSONArray sub_products = new JSONArray();
				JSONArray sub_products2 = new JSONArray();
				JSONObject obj2 = new JSONObject();
				obj= new JSONObject();
				obj.put("PSS", "PSS D522-1");
				obj2 = new JSONObject();
				obj2.put("Name", "Paiting Machine");
				sub_products2.put(new JSONObject().put("Name", "Red Brush"));
				sub_products2.put(new JSONObject().put("Name", "Blue Brush"));
				obj2.put("Products", sub_products2);
				sub_products.put(obj2);
				obj2 = new JSONObject();
				obj2.put("Name", "Glueing Machine");
				sub_products2.put(new JSONObject().put("Name", "Super-Glue +9000"));
				sub_products2.put(new JSONObject().put("Name", "Cheap-Glue 100"));
				obj2.put("Products", sub_products2);
				sub_products.put(obj2);
				obj.put("Products", sub_products);
				result.put(obj);
				return result.toString();
				

			case 20:
				result = new JSONArray();

				result.put(new JSONObject().put("Op", "Graph"));

				System.out.println(gs.Topreachglobalsentiment());
				try{
				result.put(new JSONArray(gs.Topreachglobalsentiment()));
				} catch (JSONException e){
					result.put(new JSONObject().put("Graph", "ERROR"));
				}

				return result.toString();

			case 19:
				result = new JSONArray();
				obj = new JSONObject();
				obj.put("Op", "OE_Redone");
				result.put(obj);
				for (int i = 0; i < filter.length; i++)
					result = convert(result, gs.getPolarityDistribution(id, param + "," + filtering,
							values + "," + filter[i], filter[i]), "Graph", "Top_Middle");

				result = convert(result, gs.getAvgSentiment(1, param, values, id), "Graph", "Top_Right");
				result = convert(result, gr.getReach(1, param, values, id), "Graph", "Bottom_Left");
				for (int i = 0; i < filter.length; i++)
					result = convert(result,
							gs.globalreach(1, param + "," + filtering, values + "," + filter[i], filter[i], id),
							"Graph", "Bottom_Middle");
				for (int i = 0; i < filter.length; i++)
					result = convert(result,
							gs.globalsentiment(1, param + "," + filtering, values + "," + filter[i], filter[i], id),
							"Graph", "Bottom_Right");
				return result.toString();

			case 18:
				result = new JSONArray();
				obj = new JSONObject();
				obj.put("Op", "OE_Redone");
				result.put(obj);
				result = convert(result, gp.getAmmount(param, values, "Global", id), "Graph", "Top_Left");
				result = convert(result, gs.getPolarityDistribution(id, param, values, "Global"), "Graph",
						"Top_Middle");
				result = convert(result, gs.getAvgSentiment(1, param, values, id), "Graph", "Top_Right");
				result = convert(result, gr.getReach(1, param, values, id), "Graph", "Bottom_Left");
				result = convert(result, gs.globalreach(1, param, values, "Global", id), "Graph", "Bottom_Middle");
				result = convert(result, gs.globalsentiment(1, param, values, "Global", id), "Graph", "Bottom_Right");
				return result.toString();
			case 1:
				SentimentChart sc = new SentimentChart();
				result = new JSONArray();
				result = convert(result, sc.chartrequest(param, values, id), "Graph", "Bottom_Right");
				System.out.println("YELLO");
				return result.toString();
			case 2:
				Data dat = new Data();
				return dat.load();
			/*
			 * case 3: gs = new Globalsentiment(); tmp = gs.globalsentiment(1,
			 * param, values, id).toString(); return tmp;
			 */
			case 4:
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
			/*
			 * case 10: GetInfGraph gig = new GetInfGraph(); tmp =
			 * gig.getAll(msg.getString("Author")).toString(); return tmp; case
			 * 11: GetPopulation gpo = new GetPopulation(); tmp =
			 * gpo.getAll(param, id).toString(); return tmp;
			 */
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
			case 17:
				result = new JSONArray();
				result.put(new JSONObject().put("Op", "pss"));
				
				for(PSS a : Data.pssdb.values()){
					result.put(new JSONObject().put("Pss", a.getName()));
				}
				
				
				
				return result.toString();
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

	private JSONArray convert(JSONArray result2, JSONArray obj2, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private JSONArray convert(JSONArray result, JSONArray to_add, String param, String graph) throws JSONException {
		for (int i = 0; i < to_add.length(); i++) {
			obj = new JSONObject();
			JSONObject helper = to_add.getJSONObject(i);
			obj.put(param, graph);
			for (String key : JSONObject.getNames(helper)) {
				obj.put(key, helper.get(key));
			}
			result.put(obj);

		}
		return result;
	}

}