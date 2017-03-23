package extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.*;
import extraction.Snapshot;

public class GetMediawiki {
	private Connection cnlocal;
	private static final Logger LOGGER = new Logging().create(Snapshot.class.getName());
	String error = "error";

	public JSONArray getNames(String pss) throws JSONException {

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
		String insert = new String("SELECT " + Settings.lmwtable_name + " FROM " + Settings.lmwtable + " where "
				+ Settings.lmwtable_pss + "=?;");
		try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {

			query1.setString(1, pss);

			// System.out.println("****Names:" + query1.toString());
			rs = query1.executeQuery();
			// rs.next();//verify
			while (rs.next()) {
				obj = new JSONObject();
				obj.put("Name", rs.getString("name"));
				aux.put(obj);
			}
			result.put("Media_Wiki");
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

	private void dbconnect() throws ClassNotFoundException, SQLException {
		cnlocal = Settings.connlocal();
	}

}
