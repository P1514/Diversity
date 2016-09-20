package importDB;

import java.io.IOException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Post individual Object
public class PSS {
	private HashMap<String, Integer> products = new HashMap<String, Integer>();
	private HashMap<String, String> tags = new HashMap<String, String>();
	private HashMap<Integer, String> psss = new HashMap<Integer, String>();
	private String tag_id = null;
	private Integer id =0;
	JSONArray result = new JSONArray();
	JSONObject obj = new JSONObject();
	String out;

	public PSS() {
		tags.put("Morris Ground 1", "D522-1 PSS");
		tags.put("Austin Basket", "D522-2 PSS");
		tags.put("Austin Soccer", "D522-2 PSS");
		psss.put(1,"D522-1 PSS");
		psss.put(2, "D522-2 PSS");
		products.put("Morris Ground 1", 1);
		products.put("Austin Basket", 2);
		products.put("Austin Soccer", 3);
	}

	public boolean tagexists(String word) {
		return tags.containsKey(word);
	}

	public String getTag(String word) {

		tags.forEach((k, v) -> {
			if (word.contains(k))
				tag_id = v;
		});
		return tag_id;
	}
	/*
	 * 
	 * 
	 */
	public int getProduct(String word){
		products.forEach((k, v) -> {
			if (word.contains(k))
				id = v;
		});
		return id;
	}

	public HashMap<String, String> importPSS() throws IOException {
		return tags;

	}
	
	public String getID(String key){
		return tags.get(key);
	}
	
	public String getKeysByValue(String value) {
		tags.forEach((k, v) -> {
			if(v==value){
				out=new String();
				out=k;
			}
		});
		return out;
	}
	public String getPss() throws JSONException{
		obj.put("Op", "pss");
		result.put(obj);
		psss.forEach((k,v)-> {
			obj= new JSONObject();
			try {
				obj.put("Pss", v);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result.put(obj);
		});
		return result.toString();
	}

	/*Not used anymore 
	 * public String getProducts() {
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

			});
			System.out.println(result.toString());

			return result.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "[{\"Op\":\"Error\"},{\"Message\":\"ERROR getting products\"}]";
		}
	}*/
}
