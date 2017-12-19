package general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import endpoints.LeanRules;
import extraction.Collaboration;
import extraction.Extrapolation;
import extraction.GetComments;
import extraction.GetMediawiki;
import extraction.GetPosts;
import extraction.GetProducts;
import extraction.GetReach;
import extraction.Globalsentiment;
import extraction.Prediction;
import extraction.Snapshot;
import extraction.Tagcloud;
import modeling.GetModels;
import security.Roles;

import javax.websocket.Session;

// TODO: Auto-generated Javadoc
/**
 * The Class Backend.
 */
public class Backend {
	private static final Logger LOGGER = new Logging().create(Backend.class.getName());
	private int op = 0;
	private JSONObject msg, obj;
	private JSONArray result;
	private Session session=null;

	/**
	 * Instantiates a new backend.
	 *
	 * @param _op
	 *            the op
	 * @param _msg
	 *            the msg
	 */
	public Backend(int _op, JSONObject _msg, Session _session) {
		op = _op;
		msg = _msg;
		session=_session;

	}

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
				String role = msg.getString("Role");
				try {
					msg.put("Role", URLDecoder.decode(role, "UTF-8"));
				} catch (UnsupportedEncodingException e1) {
					LOGGER.log(Level.SEVERE, "Error Decoding User Role URL => " + msg.getString("Role"));
				}
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
					LOGGER.log(Level.WARNING,"Class:Backend, ERROR 1");
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
					break;
				default:
					filter[0] = "Global";
				}

			}

			switch (op) {
			case 99:
				Prediction ps = new Prediction();
				LOGGER.log(Level.INFO, "Hashmapp" + ps.predict(1, "14;15", "14;15").toString());
				break;
			case 38:
				obj = new JSONObject();
				result = new JSONArray();
				obj.put("Op", "collaboration");
				result.put(obj);

				Data.userdb.clear();
				Loader.loadUsers();
				
				result.put(col.teamRating(msg.has("Products") ? msg.getString("Products") : "",
						msg.has("Services") ? msg.getString("Services") : "",
						msg.has("Company") ? msg.getString("Company") : "", session));
				if (result.isNull(1)) {
					obj = new JSONObject();
					result = new JSONArray();
					obj.put("Op", "Error");
					result.put(obj);
					result.put("Company does not exist");

				}
				return result.toString();
			case 39:
				Data.productdb.clear();
				
				Loader.loadPSS();
				
				return GetProducts.getTree(msg).toString();
			case 37:
				obj = new JSONObject();
				result = new JSONArray();
				JSONArray ids = new JSONArray();
				obj.put("Op", "names");
				result.put(obj);
				User user1;
				Company company1;
				ids = msg.getJSONArray("IDs");
				for (int i = 0; i < ids.length(); i++) {
					obj = new JSONObject();
					JSONObject obj1 = ids.getJSONObject(i);
					if (!obj1.has("User_ID") || !obj1.has("Role_ID")) {
						if (!obj1.has("User_ID")) {
							obj.put("Op", "Error");
							result = new JSONArray();
							result.put(obj);
							result.put("User_ID was not sent");
							return result.toString();
						}
						if (!obj1.has("Role_ID")) {
							obj.put("Op", "Error");
							result = new JSONArray();
							result.put(obj);
							result.put("Role_ID was not sent");
							return result.toString();
						}
					} else {
						user1 = Data.getUser(obj1.getLong("User_ID"));
						company1 = Data.getCompany(Data.getUser(obj1.getLong("User_ID")).getcompany_id());
						obj.put("First_name", user1.getfirst_name());
						obj.put("Last_name", user1.getlast_name());
						obj.put("Company", company1.getName());
						obj.put("Role", Data.getRolenameFromCR(obj1.getLong("Role_ID")));
					}
					result.put(obj);
				}
				return result.toString();
			case 36:
				HttpClient httpClient = HttpClientBuilder.create().build(); // Use this instead

				try {
					HttpPost request = new HttpPost(Settings.collaboration_uri);
					StringEntity params = new StringEntity(msg.getString("Message"));
					request.addHeader("content-type", "application/json");
					request.setEntity(params);
					HttpResponse response = httpClient.execute(request);
				} catch (Exception ex) {
					LOGGER.log(Level.WARNING,"Class:Backend, ERROR 2");
				}
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
				result = convert(result, gp.getAmmount(true, param, values, "Global", id), "Graph", "Top_Left");
				for (int i = 0; i < filter.length; i++) {

					result = convert(result,
							gs.getWikiPolarityDistribution(id, param + "," + filtering, values + ","
									+ (filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i]),
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
							gs.wikiGlobalSentiment(param + "," + filtering, values + ","
									+ (filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i]),
									(filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i]),
									id, Data.getmodel(id).getFrequency()),
							"Graph", "Bottom_Right");
				}
				if (msg.has("Extrapolate")) {
					LOGGER.log(Level.INFO, "EXTRAPOLATING...");
					for (int i = 0; i < filter.length; i++)
						result = convert(result, extra.extrapolate(param + "," + filtering, values + ","
								+ (filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
										: filter[i]),
								(filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
										: filter[i]),
								id, Data.getmodel(id).getFrequency()), "Graph", "Bottom_Right_Ex");
				}
				JSONObject mw = new JSONObject();
				mw.put("has_wiki", Data.getmodel(id).getMediawiki());
				result.put(mw);
				
				JSONObject sn = new JSONObject();
				sn.put("has_social", !Data.getmodel(id).getAccounts(false).isEmpty());
				result.put(sn);
				LOGGER.log(Level.INFO, !Data.getmodel(id).getAccounts(false).isEmpty()+"");
				LOGGER.log(Level.INFO, result.toString());
				// System.out.println(result.toString());
				return result.toString();
			case 33:
				obj = new JSONObject();
				result = new JSONArray();
				obj.put("Op", "collaboration");
				result.put(obj);

				result.put(col.teamRating(msg.has("Products") ? msg.getString("Products") : "",
						msg.has("Services") ? msg.getString("Services") : "",
						msg.has("Company") ? msg.getString("Company") : "", session));
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
			case 28:
				return wiki.getNames(msg.getString("PSS")).toString();

			case 27:
				obj = new JSONObject();
				result = new JSONArray();
				Tagcloud tag;
				if (msg.has("Type")) {
					switch (msg.getString("Type")) {
					case "Positive":
						// System.out.println("POSITIVE");
						tag = new Tagcloud(gp.getTopWithPolarity(msg.getBoolean("Wiki"),param, values, id,
								(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", 50, 100,
								msg.has("Day") ? msg.getInt("Day") : 1, msg.has("Year") ? msg.getInt("Year") : 2017),
								id, msg.has("User") ? msg.getLong("User") : 0);
						break;

					case "Negative":
						// System.out.println("NEGATIVE");
						tag = new Tagcloud(gp.getTopWithPolarity(msg.getBoolean("Wiki"),param, values, id,
								(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", 0, 50,
								msg.has("Day") ? msg.getInt("Day") : 1, msg.has("Year") ? msg.getInt("Year") : 2017),
								id, msg.has("User") ? msg.getLong("User") : 0);
						break;
					default:
						tag = new Tagcloud(gp.getTopWithPolarity(msg.getBoolean("Wiki"),param, values, id,
								(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", 0, 100,
								msg.has("Day") ? msg.getInt("Day") : 1, msg.has("Year") ? msg.getInt("Year") : 2017),
								id, msg.has("User") ? msg.getLong("User") : 0);
					}
				} else {
					tag = new Tagcloud(gp.getTopWithPolarity(msg.getBoolean("Wiki"),param, values, id,
							(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", 0, 100,
							msg.has("Day") ? msg.getInt("Day") : 1, msg.has("Year") ? msg.getInt("Year") : 2017), id,
							msg.has("User") ? msg.getLong("User") : 0);
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
					// System.out.println("type " + msg.getString("Type"));
					switch (msg.getString("Type")) {
					case "Positive":
						// System.out.println("POSITIVE");
						tag = new Tagcloud(gp.getTopWithPolarity(msg.getBoolean("Wiki"),param, values, id,
								(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", 49, 100,
								msg.has("Day") ? msg.getInt("Day") : 1, msg.has("Year") ? msg.getInt("Year") : 2017),
								id, msg.has("User") ? msg.getLong("User") : 0);
						break;

					case "Negative":
						// System.out.println("NEGATIVE");
						tag = new Tagcloud(gp.getTopWithPolarity(msg.getBoolean("Wiki"),param, values, id,
								(msg.has("Product") ? msg.getString("Product") : "noproduct"), "", 0, 50,
								msg.has("Day") ? msg.getInt("Day") : 1, msg.has("Year") ? msg.getInt("Year") : 2017),
								id, msg.has("User") ? msg.getLong("User") : 0);
						break;
					default:
						tag = new Tagcloud(gp.getTop(msg.getBoolean("Wiki"),param, values, id,
								(msg.has("Product") ? msg.getString("Product") : "noproduct"), "",
								msg.has("Day") ? msg.getInt("Day") : 1, msg.has("Year") ? msg.getInt("Year") : 2017),
								id, msg.has("User") ? msg.getLong("User") : 0);
					}
				} else {
					tag = new Tagcloud(gp.getTop(msg.getBoolean("Wiki"),param, values, id,
							(msg.has("Product") ? msg.getString("Product") : "noproduct"), "",
							msg.has("Day") ? msg.getInt("Day") : 1, msg.has("Year") ? msg.getInt("Year") : 2017), id,
							msg.has("User") ? msg.getLong("User") : 0);
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
					res = snapshot.saveExtraction((msg.has("Wiki") ? msg.getBoolean("Wiki") : false),msg.getString("name"), msg.getString("creation_date"),
							msg.getInt("timespan"), msg.has("user") ? msg.getString("user"): "0", msg.has("Id") ? msg.getInt("Id") : 0);

				}

				if (res.equals("name_in_use")) {
					obj = new JSONObject();
					obj.put("Message", "Name Already in Use");
					obj.put("Op", "Error");

				}
				if (res.equals("success")) {
					/*
					 * obj = new JSONObject(); obj.put("Message", "Snapshot Saved Successfully");
					 * obj.put("Op", "Error");
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
					if (!msg.has("type"))
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
							gs.getPolarityDistribution(id, param + "," + filtering, values + ","
									+ (filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i]),
									(filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i])),
							"Graph", "Top_Middle");

				result = convert(result, gs.getCurSentiment(param, values, id, Data.getmodel(id).getFrequency()),
						"Graph", "Top_Right");
				result = convert(result, gr.getReach(param, values, id), "Graph", "Bottom_Left");
				for (int i = 0; i < filter.length; i++)
					result = convert(result,
							gr.globalreach(param + "," + filtering, values + ","
									+ (filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i]),
									(filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i]),
									id, Data.getmodel(id).getFrequency()),
							"Graph", "Bottom_Middle");
				for (int i = 0; i < filter.length; i++)
					result = convert(result,
							gs.globalsentiment(param + "," + filtering, values + ","
									+ (filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i]),
									(filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
											: filter[i]),
									id, Data.getmodel(id).getFrequency()),
							"Graph", "Bottom_Right");
				if (msg.has("Extrapolate")) {
					LOGGER.log(Level.INFO, "EXTRAPOLATING...");
					for (int i = 0; i < filter.length; i++)
						result = convert(result, extra.extrapolate(param + "," + filtering, values + ","
								+ (filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
										: filter[i]),
								(filtering.equals("Product") ? Data.getProduct(Long.valueOf(filter[i])).get_Name()
										: filter[i]),
								id, Data.getmodel(id).getFrequency()), "Graph", "Bottom_Right_Ex");
				}
				mw = new JSONObject();
				mw.put("has_wiki", Data.getmodel(id).getMediawiki());
				result.put(mw);
				
				sn = new JSONObject();
				sn.put("has_social", !Data.getmodel(id).getAccounts(false).isEmpty() );
				LOGGER.log(Level.INFO, !Data.getmodel(id).getAccounts(false).isEmpty()+"");
				result.put(sn);
				LOGGER.log(Level.INFO, result.toString());

				return result.toString();

			case 18:
				result = new JSONArray();
				obj = new JSONObject();
				obj.put("Op", "OE_Redone");
				result.put(obj);
				// System.out.println("TEST:"+gp.getAmmount(param, values,
				// "Global", id).getJSONObject(1).getInt("Value"));
				if (gp.getAmmount(false, param, values, "Global", id).getJSONObject(1).getInt("Value") != 0) {
					result = convert(result, gp.getAmmount(false, param, values, "Global", id), "Graph", "Top_Left");
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
				mw = new JSONObject();
				mw.put("has_wiki", Data.getmodel(id).getMediawiki());
				result.put(mw);
				
				sn = new JSONObject();
				sn.put("has_social", !Data.getmodel(id).getAccounts(false).isEmpty());
				result.put(sn);
				LOGGER.log(Level.INFO, !Data.getmodel(id).getAccounts(false).isEmpty() + "");
				LOGGER.log(Level.INFO, result.toString());
				return result.toString();
			/*
			 * case 1: SentimentChart sc = new SentimentChart(); result = new JSONArray();
			 * result = convert(result, sc.chartrequest(param, values, id), "Graph",
			 * "Bottom_Right"); System.out.println("YELLO"); return result.toString();
			 */
			case 2:
				Startup.running.run();
				return error_message("Update Ran Successfully").toString();
			/*
			 * case 3: gs = new Globalsentiment(); tmp = gs.globalsentiment(1, param,
			 * values, id).toString(); return tmp;
			 */
			case 4:
				if (msg.has("Product")) {
					if (msg.has("word"))
						tmp = gp.getTop(msg.getBoolean("Wiki"),msg.has("Day") ? " " : param, msg.has("Month") ? msg.getString("Month") : "JAN",
								id, msg.getString("Product"), msg.getString("word"),
								msg.has("Day") ? msg.getInt("Day") : 1, msg.has("Year") ? msg.getInt("Year") : 2017)
								.toString();
					else
						tmp = gp.getTop(msg.getBoolean("Wiki"),msg.has("Day") ? " " : param, msg.has("Month") ? msg.getString("Month") : "JAN",
								id, msg.getString("Product"), null, msg.has("Day") ? msg.getInt("Day") : 1,
								msg.has("Year") ? msg.getInt("Year") : 2017).toString();
				} else {
					if (msg.has("word"))
						tmp = gp.getTop(msg.getBoolean("Wiki"),msg.has("Day") ? " " : param, msg.has("Month") ? msg.getString("Month") : "JAN",
								id, "noproduct", msg.getString("word"), msg.has("Day") ? msg.getInt("Day") : 1,
								msg.has("Year") ? msg.getInt("Year") : 2017).toString();
					else
						tmp = gp.getTop(msg.getBoolean("Wiki"),msg.has("Day") ? " " : param, msg.has("Month") ? msg.getString("Month") : "JAN",
								id, "noproduct", null, msg.has("Day") ? msg.getInt("Day") : 1,
								msg.has("Year") ? msg.getInt("Year") : 2017).toString();
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
			 * case 8: GetAuthors ga = new GetAuthors(); tmp = ga.getAll().toString();
			 * return tmp;
			 */
			/*
			 * case 9: GetLastPost glp = new GetLastPost(); tmp =
			 * glp.get(msg.getString("Author")).toString(); return tmp;
			 */
			/*
			 * case 10: GetInfGraph gig = new GetInfGraph(); tmp =
			 * gig.getAll(msg.getString("Author")).toString(); return tmp; case 11:
			 * GetPopulation gpo = new GetPopulation(); tmp = gpo.getAll(param,
			 * id).toString(); return tmp;
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
				JSONArray tmp2 = model.create_model(msg);
				boolean update = false;
				for (int i = 0; i < tmp2.length(); i++) {
					if (tmp2.getJSONObject(i).has("Update")) {
						update = true;
					}
				}

				if (update) {

					URL url = new URL(Settings.geoip_uri);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");

					BufferedReader in = null;
					try {
						in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					} catch (IOException e) {
						LOGGER.log(Level.WARNING,"Class:Backend, ERROR 4");
					}
					String output;
					StringBuffer response = new StringBuffer();

					try {
						while ((output = in.readLine()) != null) {
							response.append(output);
						}
					} catch (IOException e) {
						LOGGER.log(Level.WARNING,"Class:Backend, ERROR 5");
					}
					in.close();
					obj = new JSONObject(response.toString());

					int user = msg.has("User")?msg.getInt("User"):0;
					int dp = msg.has("design_project")?msg.getInt("design_project"):0;
					String lat = obj.has("latitude")?obj.getString("latitude"):"";
					String lon = obj.has("longitude")?obj.getString("longitude"):"";

					String link = Settings.has_steps_uri;

					link = link.replace("REPLACE_USER", user + "");
					link = link.replace("REPLACE_DP", dp + "");
					link = link.replace("REPLACE_LATITUDE", lat);
					link = link.replace("REPLACE_LONGITUDE", lon);

					httpClient = HttpClientBuilder.create().build();

					try {
						HttpGet request = new HttpGet(link);
						// HttpPost request = new HttpPost(link);
						request.addHeader("content-type", "application/json");
						HttpResponse response2 = httpClient.execute(request);
						LOGGER.log(Level.INFO, response2.toString());
					} catch (Exception ex) {
						LOGGER.log(Level.WARNING,"Class:Backend, ERROR 6");
					}
				}

				return tmp2.toString();
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
				Loader.requestPSS();
				result.put(new JSONObject().put("Op", "pss"));

				for (PSS a : Data.dbpssall()) {
					result.put(new JSONObject().put("Pss",a.getName()));
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
			LOGGER.log(Level.WARNING,"Class:Backend, ERROR 7");
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING,"Class:Backend, ERROR 8");
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