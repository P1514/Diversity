package extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Logging;
import general.Settings;

public class Tagcloud extends GetPosts {

	private static final Logger LOGGER = new Logging().create(Tagcloud.class.getName());
	private Map<String, Integer> wordWeights;
	private List<String> ignoreWords;
	private JSONArray posts;
	private JSONArray comments;
	private long model_id;
	private long user_id;

	public Tagcloud(JSONArray posts, long model_id, long user_id) {
		wordWeights = new HashMap<String, Integer>();
		ignoreWords = new ArrayList<String>();
		this.model_id = model_id;
		this.user_id = user_id;
		addUserIfNotExists();
		this.posts = posts;
	}

	public JSONArray calculateWeights() throws JSONException {

		// for (int i = 0; i < posts.length(); i++) {
		// System.out.println(i);
		// if (posts.getJSONObject(i).has("Id")) {
		// comments = joinArray(comments,
		// getComments(posts.getJSONObject(i).getLong("Id")));
		// }
		// }

		fillMaps();

		for (int i = 0; i < posts.length(); i++) {
			if (posts.getJSONObject(i).has("Id")) {
				JSONArray postComments = getComments(posts.getJSONObject(i).getLong("Id"));

				for (int j = 0; j < postComments.length(); j++) {
					String message = postComments.getString(j).toLowerCase();
					message = message.replaceAll("[^A-Za-z0-9 ]", "");
					String[] wordArray = message.split(" ");
					for (String word : wordArray) {
						if (!ignoreWords.contains(word)) {
							wordWeights.put(word, wordWeights.get(word) != null ? wordWeights.get(word) + 1 : 1);
						}
					}
				}

				String message = posts.getJSONObject(i).getString("Message").toLowerCase();

				message = message.replaceAll("[^A-Za-z0-9 ]", " ");
				String[] wordArray = message.split(" ");
				for (String word : wordArray) {
					if (!ignoreWords.contains(word) && word != "") {
						wordWeights.put(word, wordWeights.get(word) != null ? wordWeights.get(word) + 1 : 1);
					}
				}

			}
		}

		JSONArray result = new JSONArray();
		for (Entry<String, Integer> entry : wordWeights.entrySet()) {
			JSONObject weight = new JSONObject();
			weight.put("word", entry.getKey());
			weight.put("frequency", entry.getValue());
			result.put(weight);
		}

		return result;
	}

	private void addUserIfNotExists() {

		int userModelPairExists = 0;
		String select = "SELECT COUNT(*) FROM " + Settings.tctable + " WHERE " + Settings.tctable_user + " =? AND "
				+ Settings.tctable_model + " = ?";

		try (Connection cnlocal = Settings.connlocal();
				PreparedStatement query1 = cnlocal.prepareStatement(select, PreparedStatement.RETURN_GENERATED_KEYS)) {
			query1.setLong(1, user_id);
			query1.setLong(2, model_id);
			try (ResultSet rs = query1.executeQuery()) {
				rs.next();
				userModelPairExists = rs.getInt(1);
			}
		} catch (

		Exception e) {
			LOGGER.log(Level.WARNING,"Class:Tagcloud, ERROR 1");
		}

		if (userModelPairExists < 1) {
			setIgnoreWords();
		}

		// fillMaps();
	}

	private void fillMaps() {

		ignoreWords = new ArrayList<String>();
		String select = "SELECT " + Settings.tctable_ignored_words + " FROM " + Settings.tctable + " WHERE "
				+ Settings.tctable_user + " =? AND " + Settings.tctable_model + " = ?";

		try (Connection cnlocal = Settings.connlocal();
				PreparedStatement query1 = cnlocal.prepareStatement(select, PreparedStatement.RETURN_GENERATED_KEYS)) {
			query1.setLong(1, user_id);
			query1.setLong(2, model_id);
			try (ResultSet rs = query1.executeQuery()) {
				rs.next();
				if (rs.getString(1) != null) {
					String[] words = rs.getString(1).split(",");

					for (String word : words) {
						ignoreWords.add(word);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING,"Class:Tagcloud, ERROR 2");
		}
	}

	private void setIgnoreWords() {

		String ignore_words = "and,or,so,of,the,me,i,to,get,a,you,us,we,they,he,she,"
				+ "check,also,too,tell,these,no,yes,hum,are,say,in,what,theyre,re,have,";

		String insert = "INSERT INTO " + Settings.tctable + "(" + Settings.tctable_user + "," + Settings.tctable_model
				+ "," + Settings.tctable_ignored_words + ") VALUES (?,?,?)";

		try (Connection cnlocal = Settings.connlocal();
				PreparedStatement query1 = cnlocal.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS)) {
			query1.setLong(1, user_id);
			query1.setLong(2, model_id);
			query1.setString(3, ignore_words);
			query1.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			LOGGER.log(Level.WARNING,"Class:Tagcloud, ERROR 3");
		}
	}

	public void addIgnoreWord(String word) {

		String update = "UPDATE " + Settings.tctable + " SET " + Settings.tctable_ignored_words + " = CONCAT(IFNULL("
				+ Settings.tctable_ignored_words + ",''), '" + word + ",') WHERE " + Settings.tctable_user + " = ? AND "
				+ Settings.tctable_model + " = ?";

		try (Connection cnlocal = Settings.connlocal();
				PreparedStatement query1 = cnlocal.prepareStatement(update, PreparedStatement.RETURN_GENERATED_KEYS)) {
			query1.setLong(1, user_id);
			query1.setLong(2, model_id);
			query1.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			LOGGER.log(Level.WARNING,"Class:Tagcloud, ERROR 4");
		}

		fillMaps();
	}
}
