package extraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tagcloud extends GetPosts {

	private Map<String, Integer> wordWeights;
	private List<String> ignoreWords;
	private JSONArray posts;
	private JSONArray comments;

	public Tagcloud(JSONArray posts) {
		wordWeights = new HashMap<String, Integer>();
		ignoreWords = new ArrayList<String>();
		setIgnoreWords();
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

		for (int i = 0; i < posts.length(); i++) {
			if (posts.getJSONObject(i).has("Id")) {
				JSONArray postComments = getComments(posts.getJSONObject(i).getLong("Id"));

				for (int j = 0; j < postComments.length(); j++) {
					String message = postComments.getString(i).toLowerCase();
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
//			if (entry.getValue() > 0) {
				JSONObject weight = new JSONObject();
				weight.put("word", entry.getKey());
				weight.put("frequency", entry.getValue());
				result.put(weight);
//			}
		}
		
		return result;
	}

	private JSONArray joinArray(JSONArray a1, JSONArray a2) throws JSONException {
		System.out.println(a1.length());
		JSONArray result = new JSONArray();

		for (int i = 0; i < a1.length(); i++) {
			result.put(a1.get(i));
		}

		for (int i = 0; i < a2.length(); i++) {
			result.put(a2.get(i));
		}
		return result;
	}

	private void setIgnoreWords() {
		ignoreWords.add("and");
		ignoreWords.add("or");
		ignoreWords.add("so");
		ignoreWords.add("of");
		ignoreWords.add("the");
		ignoreWords.add("me");
		ignoreWords.add("i");
		ignoreWords.add("to");
		ignoreWords.add("get");
		ignoreWords.add("a");
		ignoreWords.add("you");
		ignoreWords.add("us");
		ignoreWords.add("we");
		ignoreWords.add("they");
		ignoreWords.add("he");
		ignoreWords.add("she");
		ignoreWords.add("check");
		ignoreWords.add("also");
		ignoreWords.add("too");
		ignoreWords.add("tell");
		ignoreWords.add("these");
		ignoreWords.add("no");
		ignoreWords.add("yes");
		ignoreWords.add("hum");
		ignoreWords.add("are");
		ignoreWords.add("say");
		ignoreWords.add("in");
		ignoreWords.add("what");
		ignoreWords.add("theyre");
		ignoreWords.add("have");
	}
}
