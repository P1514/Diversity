package general;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import monitoring.Monitor;;
public class Model {
	Connection cnlocal;
	private long id = 0;
	private int frequency, user;
	private String name, uri, pss, age, gender;
	private boolean products, archived;

	public Model(long _id, int _frequency, int _user, String _name, String _uri, String _pss, String _age,
			String _gender, Boolean _products, Boolean _archived) {
		this.id=_id;
		this.frequency=_frequency;
		this.user=_user;
		this.name=_name;
		this.uri=_uri;
		this.pss=_pss;
		this.age=_age;
		this.gender=_gender;
		this.products=_products;
		this.archived=_archived;
	}

	public Model() {
	}

	public JSONArray add_model(JSONObject msg) throws JSONException {

		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		name = msg.getString("Name");
		uri = msg.getString("URI");
		pss = msg.getString("PSS");
		frequency = msg.getInt("Update");
		archived = msg.getBoolean("Archive");
		products = msg.getBoolean("Final_Product");
		user = msg.getInt("User");
		//age = msg.getString("Age");
		//gender = msg.getString("Gender");
		dbconnect();

		String insert = "Insert into " + Settings.lmtable + "(" + Settings.lmtable_name + "," + Settings.lmtable_uri
				+ "," + Settings.lmtable_pss + "," + Settings.lmtable_update + "," + Settings.lmtable_archived + ","
				+ Settings.lmtable_monitorfinal + "," + Settings.lmtable_creator /*+ "," + Settings.lmtable_age + ","
				+ Settings.lmtable_gender */+ ") values (?,?,?,?,?,?,?"/*,?,?*/+")";
		PreparedStatement query1 = null;
		try {
			query1 = cnlocal.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS);
			query1.setString(1, name);
			query1.setString(2, uri);
			query1.setString(3, pss);
			query1.setInt(4, frequency);
			query1.setBoolean(5, archived);
			query1.setBoolean(6, products);
			query1.setInt(7, user);
			//query1.setString(8, age);
			//query1.setString(9, gender);
			query1.executeUpdate();
			ResultSet generatedKeys = query1.getGeneratedKeys();
			if (generatedKeys.next())
				id = generatedKeys.getLong(1);
			obj.put("id", id);
		} catch (SQLIntegrityConstraintViolationException e) {
			e.printStackTrace();
			obj.put("Op", "Error");
			obj.put("Message", "Model name Already Exists");
			result.put(obj);
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			obj.put("Op", "Error");
			obj.put("Message", "Error adding model to DB");
			result.put(obj);
			return result;
		} finally {
			try {
				if (query1 != null)
					query1.close();
				if (cnlocal != null)
					cnlocal.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		obj.put("Op", "Error2");
		obj.put("Message", "Successfully added model " + name + " to monitor module");
		result.put(obj);
		
		Monitor.update(msg);
		return result;

	}

	public JSONArray update_model(JSONObject msg) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();

		if (msg.get("Name") != this.name || msg.getString("PSS") != this.pss) {
			obj.put("id", msg.getInt("Id"));
			obj.put("Op", "Error");
			obj.put("Message", "Error updating model " + msg.getString("Name") + "updated attempt not allowed");
			result.put(obj);
		}

		String insert = "Update " + Settings.lmtable + " Set "/* + Settings.lmtable_age + "=?, " + Settings.lmtable_gender
				+ "=?, " */+ Settings.lmtable_archived + "=?, " + Settings.lmtable_monitorfinal + "=?, "
				+ Settings.lmtable_uri + "=?, " + Settings.lmtable_update + "=? Where " + Settings.lmtable_id + "=?";
		PreparedStatement query1 = null;
		try {
			dbconnect();
			query1 = cnlocal.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS);
			query1.setString(3, msg.getString("URI"));
			query1.setInt(4, msg.getInt("Update"));
			query1.setBoolean(2, msg.getBoolean("Archive"));
			query1.setBoolean(1, msg.getBoolean("Final_Product"));
			query1.setInt(5, msg.getInt("Id"));
			//query1.setString(1, msg.getString("Age"));
			//query1.setString(2, msg.getString("Gender"));
			query1.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			obj.put("Op", "Error");
			obj.put("Message", "Error adding model to DB");
			result.put(obj);
			return result;
		}finally{
			try {
			if(query1!=null)query1.close();
			if(cnlocal!=null)
				
					cnlocal.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
		}
		
		this.uri=msg.getString("URI");
		this.frequency=msg.getInt("Update");
		this.archived=msg.getBoolean("Archive");
		this.products=msg.getBoolean("Final_Product");

		obj.put("id", msg.getInt("Id"));
		obj.put("Op", "Error");
		obj.put("Message", "Successfully updated model " + msg.getString("Name"));
		result.put(obj);
		Monitor.update(msg);
		return result;
	}

	public String getName() {
		return this.name;
	}

	public String getPSS() {
		return this.pss;
	}

	public Long getId() {
		return this.id;
	}

	public boolean getArchived() {
		return this.archived;
	}

	public String getAge() {
		return this.age;
	}

	public String getURI() {
		return this.uri;
	}

	public Integer getFrequency() {
		return this.frequency;
	}

	public String getGender() {
		return this.gender;
	}

	public Boolean getProducts() {
		return this.products;
	}

	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
