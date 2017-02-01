package deprecated;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import general.Settings;
// TODO: Auto-generated Javadoc

/**
 * The Class GetInfGraph.
 *
 * @author Uninova - IControl
 */
@Deprecated
public class GetInfGraph {
	// So far unused since no more influence graphs will be shown
	private Connection cnlocal;

	/**
	 * Instantiates a new gets the inf graph.
	 */
	@Deprecated
	/**
	 * Function that evalutes author's influence over time
	 */
	public GetInfGraph() {
	}

	/**
	 * This Method return the requested author named 'name' influence
	 * information over time.
	 *
	 * @param name - Author's name to evaluate
	 * @return JSONArray with all the information present about that specific author
	 * @throws JSONException if an error occured creating a JSON
	 */
	@Deprecated
	public JSONArray getAll(String name) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		String insert;
		obj.put("Op", "graph");
		result.put(obj);
		insert = "Select timestamp,value from influences where authors_id in (Select id from authors where name=?) ORDER BY timestamp ASC";
		PreparedStatement query = null;
		ResultSet rs = null;
		dbconnect();
		try {
			query = cnlocal.prepareStatement(insert);
			query.setString(1, name);
			rs = query.executeQuery();

			for (; rs.next();) {
				obj = new JSONObject();
				obj.put("Date", rs.getDate("timestamp"));
				obj.put("Value", rs.getDouble("value"));
				result.put(obj);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(rs != null)
					rs.close();
				if (query != null)
					if (rs != null)
						rs.close();
				if (cnlocal != null)
					cnlocal.close();
				query.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}
	@Deprecated

	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
