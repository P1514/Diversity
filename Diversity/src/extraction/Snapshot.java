package extraction;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Backend;
import general.Data;
import general.Logging;
import general.Settings;
import general.User;

public class Snapshot {
	
	private static final String ALL_SNAPSHOTS = "all";
	private static final String EXTRACTION_SNAPSHOTS = "extraction";
	private static final String PREDICTION_SNAPSHOTS = "prediction";
	
	private static final Logger LOGGER = new Logging().create(Snapshot.class.getName());
	private final Backend b;

	public Snapshot(Backend b) {
		this.b = b;
	}

	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	Date dateaux = null;
	String error = "error";

	public boolean create(String name, long date, int timespan, String user, String type, String result, int id) {
		ResultSet rs;
		Connection cnlocal;
		try {
			cnlocal=Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, error, e);
			return false;
		}
		String insert = new String("SELECT * FROM " + Settings.lsstable + " where " + Settings.lsstable_name + "=? && "
				+ Settings.lsstable_type + "=?;");
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			query1.setString(1, name);
			query1.setString(2, type);
			rs = query1.executeQuery();
			if (rs.next())
				return false;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, error, e);
		}

		insert = new String("Insert into " + Settings.lsstable + "(" + Settings.lsstable_name + ","
				+ Settings.lsstable_creation_date + "," + Settings.lsstable_creation_user + ","
				+ Settings.lsstable_result + "," + Settings.lsstable_type + "," + Settings.lsstable_timespan + ","
				+ Settings.lsstable_model_id + ")" + " values (?,?,?,?,?,?,?)");
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			query1.setString(1, name);
			query1.setLong(2, date);
			query1.setString(3, user);
			query1.setString(4, result);
			query1.setString(5, type);
			query1.setInt(6, timespan);
			query1.setInt(7, id);

			query1.execute();

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, error, e);
		}
		try {
			if (cnlocal != null)
				cnlocal.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public String savePrediction(String name, String date, int timespan, String user, String products,
			String services) {
		String result;
		JSONObject obj = new JSONObject();
		long cdate;
		try {
			obj.put("Op", "Prediction");
			if (products != "")
				obj.put("Products", products);
			if (services != "")
				obj.put("Services", services);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			dateaux = df.parse(date);
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, "ERROR Parsing Date from Browser", e);
		}
		cdate = dateaux.getTime();
		if (cdate < 0) {
			LOGGER.log(Level.SEVERE, "ERROR BAD DATE");
			return "bad date";
		}

		try {
			b.setMessage(23, obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = b.resolve();
		//System.out.println("TEST" + result);
		return create(name, cdate, timespan, user, "prediction", result, -10) == true ? "success" : "name_in_use";

	}

	public String saveExtraction(String name, String date, int timespan, String user, int id) {
		String result;
		JSONObject obj = new JSONObject();
		long cdate;

		try {
			dateaux = df.parse(date);
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, "ERROR Parsing Date from Browser", e);
		}
		cdate = dateaux.getTime();
		if (cdate < 0) {
			LOGGER.log(Level.SEVERE, "ERROR BAD DATE");
			return "bad date";
		}
		try {
			obj.put("Id", id);
			obj.put("Filter", "");
			b.setMessage(19, obj);
			result = b.resolve();
			create(name, cdate, timespan, user, "all", result, id);

			obj = new JSONObject();
			obj.put("Id", id);
			obj.put("Filter", "Location");
			b.setMessage(19, obj);
			result = b.resolve();
			create(name, cdate, timespan, user, "location", result, id);

			obj = new JSONObject();
			obj.put("Id", id);
			obj.put("Filter", "Gender");
			b.setMessage(19, obj);
			result = b.resolve();
			create(name, cdate, timespan, user, "gender", result, id);

			obj = new JSONObject();
			obj.put("Id", id);
			obj.put("Filter", "Age");
			b.setMessage(19, obj);
			result = b.resolve();
			create(name, cdate, timespan, user, "age", result, id);

			obj = new JSONObject();
			obj.put("Id", id);
			obj.put("Filter", "Product");
			b.setMessage(19, obj);
			result = b.resolve();

			return create(name, cdate, timespan, user, "product", result, id) == true ? "success" : "name_in_use";

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	private String extraction(JSONObject msg) {

		return "";
	}

	private static void dbconnect() throws ClassNotFoundException, SQLException {
		Connection cnlocal = Settings.connlocal();
	}

	public JSONArray loadNames(String type) {
		JSONArray result = new JSONArray();
		JSONArray aux = new JSONArray();
		JSONObject obj = new JSONObject();
		ResultSet rs;
		Connection cnlocal;
		try {
			cnlocal=Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, error, e);
			return null;
		}
		String insert = new String("SELECT " + Settings.lsstable_name + " FROM " + Settings.lsstable + " where "
				+ Settings.lsstable_type + "=?;");
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			if (type.equals("Prediction"))
				query1.setString(1, "prediction");
			else
				query1.setString(1, "all");

			// System.out.println("****Names:" + query1.toString());
			rs = query1.executeQuery();
			// rs.next();//verify
			while (rs.next()) {
				obj = new JSONObject();
				obj.put("Name", rs.getString("name"));
				aux.put(obj);
			}
			result.put("Snapshots");
			result.put(aux);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, error, e);
		}
		try {
			if (cnlocal != null)
				cnlocal.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	public String load(String name, String type) throws JSONException {
		try {
			name = URLDecoder.decode(name, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			LOGGER.log(Level.SEVERE, "Error Decoding Snapshot Name => " + name);
			return null;
		}
		Connection cnlocal;
		try {
			cnlocal=Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, error, e);
			return null;
		}
		String insert = new String("SELECT " + Settings.lsstable_result + "," + Settings.lsstable_creation_user + "," + Settings.lsstable_creation_date + "," + Settings.lsstable_model_id + " FROM " + Settings.lsstable + " where "
				+ Settings.lsstable_name + "=? && " + Settings.lsstable_type + "=?;");
		long model;
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			query1.setString(1, name);
			if (type.equals(""))
				query1.setString(2, "prediction");
			else
				query1.setString(2, type);
			try (ResultSet rs = query1.executeQuery()) {
				if(!rs.next()) return "";
				JSONArray json = new JSONArray(rs.getString("result"));
				JSONObject obj = new JSONObject();
				obj.put("User", rs.getString("creation_user"));
				obj.put("Date", new Date(rs.getLong("creation_date")));
				model = rs.getLong("model_id");
				if (Data.getmodel(model) != null) {
					obj.put("PSS", Data.getmodel(model).getPSS());
				}
				json.put(obj);
				return json.toString();
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, error, e);
			return Backend.error_message(error).toString();
		} finally {
			try {
				cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.INFO, error, e);
			}
		}
		
		
	}

	// pss = -1 returns all snapshots from all PSS
	public static JSONArray getAll(int pss, String type) throws JSONException {

		JSONArray result = new JSONArray();
		JSONArray aux = new JSONArray();
		JSONObject obj = new JSONObject();
		ResultSet rs;
		Connection cnlocal;
		try {
			cnlocal=Settings.connlocal();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error", e);
			return null;
		}
		String insert;
		
		if (pss == -1) {
			insert = new String("Select * from " + Settings.lsstable + " where " + Settings.lsstable_type + " like ?");
		} else {
			insert = new String("Select * from " + Settings.lsstable + " where " + Settings.lsstable_model_id
					+ " in (SELECT " + Settings.lmtable_id + " FROM " + Settings.lmtable + " where " + Settings.lmtable_pss
					+ "=?) AND "+Settings.lsstable_type+" like '%all%'");
		}
		
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			int i = 1;
			if (pss != -1) {
				query1.setInt(i++, pss);
			} else {
				if (type.equals(ALL_SNAPSHOTS)) {
					query1.setString(i++, "%%");
				} else if (type.equals(EXTRACTION_SNAPSHOTS)) {
					query1.setString(i++, "%all%");
				} else if (type.endsWith(PREDICTION_SNAPSHOTS)) {
					query1.setString(i++, PREDICTION_SNAPSHOTS);
				}
			}

			// System.out.println("****Names:" + query1.toString());
			rs = query1.executeQuery();
			// rs.next();//verify
			while (rs.next()) {
				obj = new JSONObject();
				obj.put("Name", rs.getString("name"));
				String _userS = rs.getString("creation_user");
				User _user;
				try{
				_user = Data.getUser(Long.parseLong(_userS));
				}catch(NumberFormatException e) {
					_user=null;
				}
				String user_name = _user != null ? _user.getUserName() : _userS;
				obj.put("Id", rs.getString("id") );
				obj.put("User", user_name);
				obj.put("Type", rs.getString("type"));
				aux.put(obj);
			}
			result.put(new JSONObject().put("Op", "Snapshots"));
			result.put(aux);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error", e);
		}
		try {
			if (cnlocal != null)
				cnlocal.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
