package extraction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Data;
import general.PSS;
import general.Product;

// TODO: Auto-generated Javadoc
/**
 * The Class GetProducts.
 *
 * @author Uninova - IControl
 */
public class GetProducts {

	/**
	 * Returns a JSONArray with information about all products present in the
	 * database, and their respective relation to other products or pss's.
	 * 
	 * @return JSONArray Example PSS:"D522", Products:[Name:"Brush",Name:"Iron"]
	 * @throws JSONException
	 *             in case creating a JSON fails to occur
	 */
	public static final JSONArray getTree(String pss_in) throws JSONException {

		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		result.put(new JSONObject().put("Op", "Tree"));
		for (PSS pss : Data.dbpssall()) {
			if (pss_in != null && !pss.getName().equals(pss_in))
				continue;
			obj = new JSONObject();
			obj.put("PSS", pss.getName());
			JSONArray sub_products = new JSONArray();
			for (Long id : pss.get_products()) {
				JSONArray sub_products2 = new JSONArray();
				Product product = Data.getProduct(id);
				if (!product.getFinal())
					continue;
				//if (product.getParent() == 0) {
					sub_products.put(new JSONObject().put("Name", product.get_Name()));
					for (Product product2 : Data.dbproductall()) {
						if (product2.getParent() == product.get_Id()) {
							sub_products2.put(new JSONObject().put("Name", product2.get_Name()));
						}

						if (sub_products2.length() != 0)
							sub_products.put(new JSONObject().put("Products", sub_products2));
						// TODO Replace this with recursive mode that can be
						// done
						// until
						// the ammount of products
						// reaches the end of infinity, and that also doesn't
						// need
						// to
						// iterate over everything
					}
				//}
			}
			obj.put("Products", sub_products);
			result.put(obj);
		}
		return result;

	}

	public static final JSONArray getTree(JSONObject msg) throws JSONException {
		if (!msg.has("All")) {
			if (msg.has("Pss")) {
				return getTree(msg.getString("Pss"));
			} else {
				
				return getTree((String) null);
			}
		} else {
			return getPSTree();
		}
	}

	public static final JSONArray getPSTree() throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("Op", "Tree");
		result.put(obj);
		
		for (Product pro : Data.dbproductall()) {
			if (pro.getParent() != 0)
				continue;
			obj = new JSONObject();
			obj.put("Name", pro.get_Name());
			obj.put("Id", pro.get_Id());
			obj.put("Type", "Product");
			if (!pro.getsubproducts().isEmpty()) {
				JSONArray array = new JSONArray();
				for (long id : pro.getsubproducts())
					array.put(subproducts(id));
				obj.put("Products", array);
			}
			result.put(obj);

		}
		for (Product ser : Data.dbserviceall()) {
			if (ser.getParent() != 0)
				continue;
			obj = new JSONObject();
			obj.put("Name", ser.get_Name());
			obj.put("Id", ser.get_Id());
			obj.put("Type", "Service");
			if (!ser.getsubproducts().isEmpty()) {
				JSONArray array = new JSONArray();
				for (long id : ser.getsubproducts())
					array.put(subservices(id));
				obj.put("Products", array);
			}
			result.put(obj);

		}
		
		return result;
	}

	private static final JSONObject subproducts(long id) throws JSONException {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		Product pro = Data.getProduct(id);
		result.put("Name", pro.get_Name());
		result.put("Id", pro.get_Id());
		if (!pro.getsubproducts().isEmpty()) {
			for (long id2 : pro.getsubproducts())
				array.put(subproducts(id2));
			result.put("Products", array);
		}

		return result;
	}
	
	private static final JSONObject subservices(long id) throws JSONException {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		Product pro = Data.getService(id);
		result.put("Name", pro.get_Name());
		result.put("Id", pro.get_Id());
		if (!pro.getsubproducts().isEmpty()) {
			for (long id2 : pro.getsubproducts())
				array.put(subservices(id2));
			result.put("Products", array);
		}

		return result;
	}
}