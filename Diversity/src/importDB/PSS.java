package importDB;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

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
	String out;

	public PSS() {
		tags.put("Morris Ground 1", 1);
		tags.put("Austin Basket", 2);
		tags.put("Austin Soccer", 3);
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
	
	public String getKeysByValue(Integer value) {
		tags.forEach((k, v) -> {
			System.out.println("HELLO" + v + " " + value);
			if(v==value){
				System.out.println("HELLO" + v + " " + value);
				out=new String();
				out=k;
			}
		});
		return out;
	}

	public String getProducts() {
		try {
			obj.put("Op", "products");
			result.put(obj);
			obj = new JSONObject();
			obj.put("Name", getKeysByValue(1));
			obj.put("Pss", psss.get(1));
			result.put(obj);
			obj = new JSONObject();
			obj.put("Name", getKeysByValue(2));
			obj.put("Pss", psss.get(2));
			result.put(obj);
			obj = new JSONObject();
			obj.put("Name", getKeysByValue(3));
			obj.put("Pss", psss.get(3));
			result.put(obj);

			/*tags.forEach((k, v) -> {
				try {
					obj = new JSONObject();

					obj.put("Name", k);
					obj.put("Pss", psss.get(v));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result.put(obj);

			});*/
			System.out.println(result.toString());

			return result.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "[{\"Op\":\"Error\"},{\"Message\":\"ERROR getting products\"}]";
		}
	}
}
