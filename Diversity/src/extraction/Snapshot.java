package extraction;

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

public class Snapshot {
	private Connection cnlocal;
	private static final Logger LOGGER = new Logging().create(Snapshot.class.getName());


	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	Date dateaux = null;
	String error="error";

	public boolean create(String name, long date, int timespan, String user, String type, String result) {
		ResultSet rs;
		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, error, e);
			return false;
		}
		String insert = new String("SELECT * FROM sentimentanalysis.snapshots where name=? && type=?;");
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			query1.setString(1, name);
			query1.setString(2, type);
			rs = query1.executeQuery();
			if (rs.next())
				return false;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, error, e);
		}

		insert = new String("Insert into " + "snapshots(name,creation_date,creation_user,result,type,timespan)"
				+ " values (?,?,?,?,?,?)");
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			query1.setString(1, name);
			query1.setLong(2, date);
			query1.setString(3, user);
			query1.setString(4, result);
			query1.setString(5, type);
			query1.setInt(6, timespan);
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

		Backend b = new Backend(23, obj);
		result = b.resolve();

		return create(name, cdate, timespan, user, "prediction", result) == true ? "success" : "name_in_use";

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
			Backend b = new Backend(19, obj);
			result = b.resolve();
			create(name, cdate, timespan, user, "all", result);

			obj = new JSONObject();
			obj.put("Id", id);
			obj.put("Filter", "Location");
			b = new Backend(19, obj);
			result = b.resolve();
			create(name, cdate, timespan, user, "location", result);

			obj = new JSONObject();
			obj.put("Id", id);
			obj.put("Filter", "Gender");
			b = new Backend(19, obj);
			result = b.resolve();
			create(name, cdate, timespan, user, "gender", result);

			obj = new JSONObject();
			obj.put("Id", id);
			obj.put("Filter", "Age");
			b = new Backend(19, obj);
			result = b.resolve();
			create(name, cdate, timespan, user, "age", result);

			obj = new JSONObject();
			obj.put("Id", id);
			obj.put("Filter", "Product");
			b = new Backend(19, obj);
			result = b.resolve();
			return create(name, cdate, timespan, user, "product", result) == true ? "success" : "name_in_use";

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	private void dbconnect() throws ClassNotFoundException, SQLException {
		cnlocal = Settings.connlocal();
	}

	public JSONArray loadNames(String type) {
		JSONArray result = new JSONArray();
		JSONArray aux = new JSONArray();
		JSONObject obj = new JSONObject();
		ResultSet rs;
		try {
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, error, e);
			return null;
		}
		String insert = new String("SELECT name FROM sentimentanalysis.snapshots where type=?;");
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			if (type.equals("Prediction"))
				query1.setString(1, "prediction");
			else
				query1.setString(1, "all");

			// System.out.println("****Names:" + query1.toString());
			rs = query1.executeQuery();
			// rs.next();//verify
			while(rs.next()) {
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
			dbconnect();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, error, e);
			return null;
		}
		String insert = new String("SELECT result FROM sentimentanalysis.snapshots where name=? && type=?;");
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
			query1.setString(1, name);
			if (type.equals(""))
				query1.setString(2, "prediction");
			else
				query1.setString(2, type);
			try (ResultSet rs = query1.executeQuery()) {
				rs.next();
				return rs.getString("result");
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

}
