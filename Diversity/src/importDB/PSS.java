package importDB;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Post individual Object
public class PSS {
	private HashMap<String, Integer> tags = new HashMap<String, Integer>();
	private HashMap<Integer, String> psss = new HashMap<Integer, String>();
	private int tag_id = 0;
	JSONArray result = new JSONArray();
	JSONObject obj = new JSONObject();

	public PSS() {
		tags.put("Nike Air Force 1", 1);
		tags.put("Adidas Stan Smith", 2);
		tags.put("Adidas Copa Mundial", 3);
		psss.put(1,"D522-1 PSS");
		psss.put(2, "D522-2 PSS");
		psss.put(3,"D522-2 PSS");
	}

	public boolean tagexists(String word) {
		return tags.containsKey(word);
	}

	public int getTag(String word) {

		tags.forEach((k, v) -> {
			if (word.contains(k))
				tag_id = v;
		});
		return tag_id;
	}

	public HashMap<String, Integer> importPSS() throws IOException {
		return tags;

	}
	
	public int getID(String key){
		return tags.get(key);
	}

	public String getProducts() {
		try {
			obj.put("Op", "products");
			result.put(obj);

			tags.forEach((k, v) -> {
				try {
					obj = new JSONObject();

					obj.put("Name", k);
					obj.put("Pss", psss.get(v));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result.put(obj);

			});
			System.out.println(result.toString());

			return result.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "[{\"Op\":\"Error\"},{\"Message\":\"ERROR getting products\"}]";
		}
	}
}
