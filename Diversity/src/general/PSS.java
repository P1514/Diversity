package general;

import java.io.IOException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Post individual Object
public class PSS {
	private long id;
	private long company;
	private String name;
	private long user_id;
	private String type;
	public PSS(long _id, long _company, String _name, long _user_id, String _type) {
		this.id=_id;
		this.company=_company;
		this.name=_name;
		this.user_id=_user_id;
		this.type=_type;
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

	public long getID(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
	
	public long getCompany(){
		return company;
	}
	
	public long getAuthor(){
		return this.user_id;
	}
}
