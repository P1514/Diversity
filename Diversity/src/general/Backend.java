package general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import security.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.json.*;

import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;

import endpoints.LeanRules;
import endpoints.LeanRules.LeanRule;
import extraction.Collaboration;
import extraction.Extrapolation;
import extraction.GetComments;
import extraction.GetPosts;
import extraction.GetProducts;
import extraction.GetReach;
import extraction.Globalsentiment;
import extraction.Prediction;
import extraction.GetMediawiki;
import extraction.Snapshot;
import extraction.Tagcloud;
import modeling.GetModels;

// TODO: Auto-generated Javadoc
/**
 * The Class Backend.
 */
public class Backend {
	private static final Logger LOGGER = new Logging().create(Backend.class.getName());
	private int op = 0;
	private JSONObject msg, obj;
	private JSONArray result;

	/**
	 * Instantiates a new backend.
	 *
	 * @param _op
	 *            the op
	 * @param _msg
	 *            the msg
	 */
	public Backend(int _op, JSONObject _msg) {
		op = _op;
		msg = _msg;

	}

	public void setMessage(int _op, JSONObject _msg) throws JSONException {
		op = _op;
		_msg.put("Key", msg.get("Key"));
		msg = _msg;
	}

	/**
	 * Resolves the query asked by the front-end.
	 *
	 * @return the string
	 */
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
		Extrapolation extra = Extrapolation.getInstance();
		Prediction pre = new Prediction();
		Collaboration col = new Collaboration();

		Snapshot snapshot = new Snapshot(this);
		GetReach gr = new GetReach();
		GetMediawiki wiki = new GetMediawiki();
		long id = 0;
		try {

			if (!msg.has("Key")) {

				LOGGER.log(Level.INFO, "Unauthorized Access Atempt JSON = " + msg.toString());
				return error_message("You're not allowed to be here. What were you expecting to find?").toString();

			}

			if (msg.has("Role")) {
				Data.newuser(msg.getString("Key"), msg.getString("Role"));
			}

			if (!Data.usercheck(msg.getString("Key"), op)) {
				LOGGER.log(Level.INFO, "Unauthorized Access Atempt JSON = " + msg.toString());
				return error_message("You're not allowed to be here. What were you expecting to find?").toString();
			}
			if (msg.has("Id")) {
				try {
					id = msg.getLong("Id");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			param = (msg.has("Param")) ? msg.getString("Param") : null;
			values = (msg.has("Values")) ? msg.getString("Values") : null;
			filtering = (msg.has("Filter")) ? msg.getString("Filter") : null;
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
				case "Product":
					filter = Data.getmodel(msg.getLong("Id")).getProducts().split(",");
				}

			}

			switch (op) {
			case 99:
				Prediction ps = new Prediction();
				LOGGER.log(Level.INFO, "Hashmapp" + ps.predict(1, "14;15", "14;15").toString());
				break;
			case 35:
				result = new JSONArray();
				obj = new JSONObject();
				obj.put("Op", "Roles");
				result.put(obj);
				String roles = Data.getRolesFromCR();
				LOGGER.log(Level.INFO, roles);
				obj = new JSONObject();
				obj.put("Roles", roles);
				result.put(obj);
				return result.toString();
			case 34:
				// SELECT * FROM sentimentanalysis.posts where id in (select
				// post_id from post_source where post_source = 'wiki');
				result = new JSONArray();
				obj = new JSONObject();
				obj.put("Op", "OE_Redone");
				result.put(obj);

				for (int i = 0; i < filter.length; i++) {

					result = convert(result,
							gs.getWikiPolarityDistribution(id, param + "," + filtering,
									values + ","
											+ (filtering.equals("Product")
													? Data.getProduct(Long.valueOf(filter[i])).get_Name() : filter[i]),
									(filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i])),
							"Graph", "Top_Middle");

				}

				result = convert(result, gs.getWikiCurSentiment(param, values, id, Data.getmodel(id).getFrequency()),
						"Graph", "Top_Right");
				// result = convert(result, , "Graph", "Bottom_Left");

				// for (int i = 0; i < filter.length; i++)
				// result = convert(result,
				// gr.globalreach(param + "," + filtering,
				// values + ","
				// + (filtering.equals("Product")
				// ? Data.getProduct(Long.valueOf(filter[i])).get_Name() :
				// filter[i]),
				// (filtering.equals("Product") ?
				// Data.getProduct(Long.valueOf(filter[i])).get_Name()
				// : filter[i]),
				// id, Data.getmodel(id).getFrequency()),
				// "Graph", "Bottom_Middle");

				for (int i = 0; i < filter.length; i++) {
					result = convert(result,
							gs.wikiGlobalSentiment(param + "," + filtering,
									values + ","
											+ (filtering.equals("Product")
													? Data.getProduct(Long.valueOf(filter[i])).get_Name() : filter[i]),
									(filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i]),
									id, Data.getmodel(id).getFrequency()),
							"Graph", "Bottom_Right");
				}
				if (msg.has("Extrapolate")) {
					LOGGER.log(Level.INFO, "EXTRAPOLATING...");
					for (int i = 0; i < filter.length; i++)
						result = convert(result,
								extra.extrapolate(param + "," + filtering,
										values + "," + (filtering.equals("Product")
												? Data.getProduct(Long.valueOf(filter[i])).get_Name() : filter[i]),
										(filtering.equals("Product")
												? Data.getProduct(Long.valueOf(filter[i])).get_Name() : filter[i]),
										id, Data.getmodel(id).getFrequency()),
								"Graph", "Bottom_Right_Ex");
				}

				LOGGER.log(Level.INFO, result.toString());
				//System.out.println(result.toString());
				return result.toString();
			case 33:
				obj = new JSONObject();
				result = new JSONArray();
				obj.put("Op", "collaboration");
				result.put(obj);

				result.put(col.teamRating(msg.has("Products") ? msg.getString("Products") : "",
						msg.has("Services") ? msg.getString("Services") : "",
						msg.has("Company") ? msg.getString("Company") : ""));
				if (result.isNull(1)) {
					obj = new JSONObject();
					result = new JSONArray();
					obj.put("Op", "Error");
					result.put(obj);
					result.put("Company does not exist");


				}
				return result.toString();
			case 32:
				obj = new JSONObject();
				result = new JSONArray();
				obj.put("Op", "design_projects");

				JSONArray dpList = new JSONArray();
				for (int i : LeanRules.getDesignProjects(-1)) {
					dpList.put(i);
				}

				obj.put("List", dpList);
				result.put(obj);

				return result.toString();
			case 31:
				obj = new JSONObject();
				result = new JSONArray();
				obj.put("Op", "rules");

				LeanRules lr = new LeanRules("-1");
				result = lr.getResult();

				return result.toString();
			case 30:
				obj = new JSONObject();
				result = new JSONArray();
				try {
					obj.put("Logs", Logging.getAllLogs());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				result.put(obj);

				return result.toString();

			case 28:
				return wiki.getNames(msg.getString("PSS")).toString();

			case 27:
				obj = new JSONObject();
				result = new JSONArray();
				Tagcloud tag;
				if (msg.has("Type")) {
					switch (msg.getString("Type")) {
					case "Positive":
						tag = new Tagcloud(
								gp.getTopWithPolarity(param, values, id,
										(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", 50, -1),
								id, msg.has("User") ? msg.getLong("User") : 0);
						break;

					case "Negative":
						tag = new Tagcloud(
								gp.getTopWithPolarity(param, values, id,
										(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", -1, 50),
								id, msg.has("User") ? msg.getLong("User") : 0);
						break;
					default:
						tag = new Tagcloud(
								gp.getTop(param, values, id,
										(msg.has("Product") ? msg.getString("Product") : "noproduct"), ""),
								id, msg.has("User") ? msg.getLong("User") : 0);
						break;
					}
				} else {
					tag = new Tagcloud(
							gp.getTop(param, values, id,
									(msg.has("Product") ? msg.getString("Product") : "noproduct"), ""),
							id, msg.has("User") ? msg.getLong("User") : 0);
				}

				if (msg.has("Word")) {
					tag.addIgnoreWord(msg.getString("Word"));
				}
				obj.put("Op", "words");
				obj.put("Words", tag.calculateWeights());
				result.put(obj);
				LOGGER.log(Level.INFO, result.toString());
				return result.toString();
			case 26:
				obj = new JSONObject();
				result = new JSONArray();
				if (msg.has("Type")) {
					//System.out.println("type " + msg.getString("Type"));
					switch (msg.getString("Type")) {
					case "Positive":
						//System.out.println("POSITIVE");
						tag = new Tagcloud(
								gp.getTopWithPolarity(param, values, id,
										(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", 50, -1),
								id, msg.has("User") ? msg.getLong("User") : 0);
						break;

					case "Negative":
						//System.out.println("NEGATIVE");
						tag = new Tagcloud(
								gp.getTopWithPolarity(param, values, id,
										(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", -1, 50),
								id, msg.has("User") ? msg.getLong("User") : 0);
						break;
					default:
						tag = new Tagcloud(
								gp.getTopWithPolarity(param, values, id,
										(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", -1, -1),
								id, msg.has("User") ? msg.getLong("User") : 0);
					}
				} else {
					tag = new Tagcloud(
							gp.getTopWithPolarity(param, values, id,
									(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", -1, -1),
							id, msg.has("User") ? msg.getLong("User") : 0);
				}
				obj.put("Op", "words");
				obj.put("Words", tag.calculateWeights());
				result.put(obj);
				LOGGER.log(Level.INFO, result.toString());
				return result.toString();
			case 25:
				obj = new JSONObject();
				result = new JSONArray();
				String resul;
				if (msg.has("Name")) {
					return snapshot.load(msg.getString("Name"), msg.has("Type") ? msg.getString("Type") : "");
				} else {
					result = snapshot.loadNames(msg.getString("Type"));
					resul = result.toString();
				}

				return resul;

			case 24:

				String res = "";

				if (msg.getString("type").equals("Prediction")) {
					res = snapshot.savePrediction(msg.getString("name"), msg.getString("creation_date"),
							msg.getInt("timespan"), msg.getString("user"),
							msg.has("Products") ? msg.getString("Products") : "",
							msg.has("Services") ? msg.getString("Services") : "");
				} else {
					res = snapshot.saveExtraction(msg.getString("name"), msg.getString("creation_date"),
							msg.getInt("timespan"), msg.getString("user"), msg.has("Id") ? msg.getInt("Id") : 0);

				}

				if (res.equals("name_in_use")) {
					obj = new JSONObject();
					obj.put("Message", "Name Already in Use");
					obj.put("Op", "Error");

				}
				if (res.equals("success")) {
					/*
					 * obj = new JSONObject(); obj.put("Message",
					 * "Snapshot Saved Successfully"); obj.put("Op", "Error");
					 */
					return error_message("Snapshot Saved Successfully").toString();

				}

				return obj.toString();

			case 23:
				result = new JSONArray();
				obj = new JSONObject();
				if (msg.has("Products") || msg.has("Services")) {
					obj.put("Op", "Prediction");
					result.put(obj);
					if(!msg.has("type"))
					result.put(pre.predictSeassonal(1, msg.has("Products") ? msg.getString("Products") : "",
							msg.has("Services") ? msg.getString("Services") : ""));
					else
					result.put(pre.predictLifeCycle(1, msg.has("Products") ? msg.getString("Products") : "",
							msg.has("Services") ? msg.getString("Services") : ""));
					// result = convert(result, pre.predict(1,
					// msg.getString("Products"), msg.getString("Services")),
					// "Graph", "1");
					// result = convert(result, gs.getPolarityDistribution(id,
					// param, values, "Global"), "Average","1");
					if (result.getJSONArray(1).getJSONObject(0).has("Op")) {
						result = result.getJSONArray(1);
					}

				} else {
					obj.put("Message", "No products or services selected");
					obj.put("Op", "Error");
					result.put(obj);
				}
				if (op == 23)
					return result.toString();

			case 22:
				return Roles.getRestrictions(msg.getString("Role")).toString();

			case 21:

				return GetProducts.getTree(msg).toString();

			case 20:
				result = new JSONArray();
				result.put(new JSONObject().put("Op", "Graph"));
				ArrayList<Long> pss = new ArrayList<Long>();

				if (msg.has("PSS")) {
					// LOGGER.log(Level.INFO, "PSSNAME:" +
					// msg.getString("PSS").split(";")[0]);
					// LOGGER.log(Level.INFO, "PSSNAME:" +
					// msg.getString("PSS").split(";")[1]);
					// LOGGER.log(Level.INFO, "PSSNAME:" +
					// msg.getString("PSS").split(";")[2]);
					// LOGGER.log(Level.INFO, "PSSNAME:" +
					// msg.getString("PSS").split(";")[3]);
					// LOGGER.log(Level.INFO, "PSSNAME:" +
					// msg.getString("PSS").split(";")[4]);
					for (int i = 0; i < msg.getString("PSS").split(";").length; i++)
						pss.add(Data.identifyPSSbyname(msg.getString("PSS").split(";")[i]));
					LOGGER.log(Level.INFO, "PSSID's:" + pss.toString());
					gs.globalsentiment(null, null, pss);

				} else
					gs.globalsentiment(null, null, gr.getTOPReach(5));// computes
																		// the
																		// value
																		// and
																		// puts
																		// it in
																		// the
																		// DB
																		// TODO:
																		// change
																		// it to
																		// compute
																		// only
																		// if
																		// not
																		// computed
																		// before

				LOGGER.log(Level.INFO, gs.globalsentiment());

				try {
					result.put(new JSONArray(gs.globalsentiment()));
				} catch (JSONException e) {
					result.put(new JSONObject().put("Graph", "ERROR"));
				}
				return result.toString();

			case 19:
				result = new JSONArray();
				obj = new JSONObject();
				obj.put("Op", "OE_Redone");
				result.put(obj);
				for (int i = 0; i < filter.length; i++)
					result = convert(result,
							gs.getPolarityDistribution(id, param + "," + filtering,
									values + ","
											+ (filtering.equals("Product")
													? Data.getProduct(Long.valueOf(filter[i])).get_Name() : filter[i]),
									(filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i])),
							"Graph", "Top_Middle");

				result = convert(result, gs.getCurSentiment(param, values, id, Data.getmodel(id).getFrequency()),
						"Graph", "Top_Right");
				result = convert(result, gr.getReach(param, values, id), "Graph", "Bottom_Left");
				for (int i = 0; i < filter.length; i++)
					result = convert(result,
							gr.globalreach(param + "," + filtering,
									values + ","
											+ (filtering.equals("Product")
													? Data.getProduct(Long.valueOf(filter[i])).get_Name() : filter[i]),
									(filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i]),
									id, Data.getmodel(id).getFrequency()),
							"Graph", "Bottom_Middle");
				for (int i = 0; i < filter.length; i++)
					result = convert(result,
							gs.globalsentiment(param + "," + filtering,
									values + ","
											+ (filtering.equals("Product")
													? Data.getProduct(Long.valueOf(filter[i])).get_Name() : filter[i]),
									(filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i]),
									id, Data.getmodel(id).getFrequency()),
							"Graph", "Bottom_Right");
				if (msg.has("Extrapolate")) {
					LOGGER.log(Level.INFO, "EXTRAPOLATING...");
					for (int i = 0; i < filter.length; i++)
						result = convert(result,
								extra.extrapolate(param + "," + filtering,
										values + "," + (filtering.equals("Product")
												? Data.getProduct(Long.valueOf(filter[i])).get_Name() : filter[i]),
										(filtering.equals("Product")
												? Data.getProduct(Long.valueOf(filter[i])).get_Name() : filter[i]),
										id, Data.getmodel(id).getFrequency()),
								"Graph", "Bottom_Right_Ex");
				}

				LOGGER.log(Level.INFO, result.toString());

				return result.toString();

			case 18:
				result = new JSONArray();
				obj = new JSONObject();
				obj.put("Op", "OE_Redone");
				result.put(obj);
				// System.out.println("TEST:"+gp.getAmmount(param, values,
				// "Global", id).getJSONObject(1).getInt("Value"));
				if (gp.getAmmount(param, values, "Global", id).getJSONObject(1).getInt("Value") != 0) {
					result = convert(result, gp.getAmmount(param, values, "Global", id), "Graph", "Top_Left");
					result = convert(result, gs.getPolarityDistribution(id, param, values, "Global"), "Graph",
							"Top_Middle");
					result = convert(result, gs.getCurSentiment(param, values, id, Data.getmodel(id).getFrequency()),
							"Graph", "Top_Right");
					result = convert(result, gr.getReach(param, values, id), "Graph", "Bottom_Left");
					result = convert(result,
							gr.globalreach(param, values, "Global", id, Data.getmodel(id).getFrequency()), "Graph",
							"Bottom_Middle");
					result = convert(result,
							gs.globalsentiment(param, values, "Global", id, Data.getmodel(id).getFrequency()), "Graph",
							"Bottom_Right");
				} else {
					obj = new JSONObject();
					obj.put("Error", "No_data");
					result.put(obj);

				}
				return result.toString();
			/*
			 * case 1: SentimentChart sc = new SentimentChart(); result = new
			 * JSONArray(); result = convert(result, sc.chartrequest(param,
			 * values, id), "Graph", "Bottom_Right");
			 * System.out.println("YELLO"); return result.toString();
			 */
			case 2:
				Startup.running.run();
				return error_message("Update Ran Successfully").toString();
				/*
			 * case 3: gs = new Globalsentiment(); tmp = gs.globalsentiment(1,
			 * param, values, id).toString(); return tmp;
			 */
			case 4:
				if (msg.has("Product")) {
					if (msg.has("word"))
						tmp = gp.getTop(param, values, id, msg.getString("Product"), msg.getString("word")).toString();
					else
						tmp = gp.getTop(param, values, id, msg.getString("Product"), null).toString();
				} else {
					if (msg.has("word"))
						tmp = gp.getTop(param, values, id, "noproduct", msg.getString("word")).toString();
					else
						tmp = gp.getTop(param, values, id, "noproduct", null).toString();
				}
				return tmp;
			case 5:
				model = new GetModels();
				if (msg.has("Project")) {
					return model.get_models(msg.getLong("Project")).toString();
				}
				return model.get_models().toString();
			case 6:
				GetComments gc = new GetComments();
				tmp = gc.getAll(msg).toString();
				return tmp;
			case 7:
				CleanDB cdb = new CleanDB();
				tmp = cdb.clean();
				return tmp;
			/*
			 * case 8: GetAuthors ga = new GetAuthors(); tmp =
			 * ga.getAll().toString(); return tmp;
			 */
			/*
			 * case 9: GetLastPost glp = new GetLastPost(); tmp =
			 * glp.get(msg.getString("Author")).toString(); return tmp;
			 */
			/*
			 * case 10: GetInfGraph gig = new GetInfGraph(); tmp =
			 * gig.getAll(msg.getString("Author")).toString(); return tmp; case
			 * 11: GetPopulation gpo = new GetPopulation(); tmp =
			 * gpo.getAll(param, id).toString(); return tmp;
			 */
			case 12:
				conf = new Settings();
				tmp = "";
				if (msg.has("Id")) {
					tmp = conf.getConf(msg.getLong("Id")).toString();
				} else {
					tmp = conf.getConf().toString();
				}
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

				for (PSS a : Data.dbpssall()) {
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

	public static JSONArray error_message(String message) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("Op", "Error");
		obj.put("Message", message);
		return new JSONArray().put(obj);
	}

	private JSONArray convert(JSONArray result2, JSONArray obj2, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private static JSONArray convert(JSONArray result, JSONArray to_add, String param, String graph)
			throws JSONException {
		for (int i = 0; i < to_add.length(); i++) {
			JSONObject obj = new JSONObject();
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