package general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class PSS.
 */
//Post individual Object
public class PSS {
	private long id;
	private long company;
	private String name;
	private long user_id;
	private String type;
	private ArrayList<Long> list_services;
	private ArrayList<Long> list_products;
	
	/**
	 * Instantiates a new pss.
	 *
	 * @param _id the id
	 * @param _company the company
	 * @param _name the name
	 * @param _user_id the user id
	 * @param _type the type
	 */
	public PSS(long _id, long _company, String _name, long _user_id, String _type) {
		this.id=_id;
		this.company=_company;
		this.name=_name;
		this.user_id=_user_id;
		this.type=_type;
		this.list_products = new ArrayList<Long>();
		this.list_services = new ArrayList<Long>();

		/*
		tags.put("Morris Ground 1", "D522-1 PSS");// TODO Change this so that it all comes from DB
		tags.put("Austin Basket", "D522-2 PSS");
		tags.put("Austin Soccer", "D522-2 PSS");
		psss.put(1,"D522-1 PSS");
		psss.put(2, "D522-2 PSS");
		products.put("Morris Ground 1", 1);
		products.put("Austin Basket", 2);
		products.put("Austin Soccer", 3);*/
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getID(){
		return id;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Gets the company.
	 *
	 * @return the company
	 */
	public long getCompany(){
		return company;
	}
	
	/**
	 * Gets the author.
	 *
	 * @return the author
	 */
	public long getAuthor(){
		return this.user_id;
	}
	
	public void add_product(Long id){
		this.list_products.add(id);
	}
	
	public ArrayList<Long> get_products(){
		return new ArrayList<>(this.list_products);
	}
	
	public void add_service(Long id){
		this.list_services.add(id);
	}
	
	public ArrayList<Long> get_services(){
		return new ArrayList<>(this.list_services);
	}
}