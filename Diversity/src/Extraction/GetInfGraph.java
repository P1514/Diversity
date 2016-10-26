package Extraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import General.Settings;

public class GetInfGraph {
// So far unused since no more influence graphs will be shown
	private Settings dbc = new Settings();
	private Connection cnlocal;

	public GetInfGraph() {
	}

	public JSONArray getAll(String name) throws JSONException {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		String insert;
		obj.put("Op", "graph");
		result.put(obj);
		insert = "Select timestamp,value from influences where authors_id in (Select id from authors where name=?) ORDER BY timestamp ASC";
		PreparedStatement query = null;
		ResultSet rs=null;
		dbconnect();
		try {
			query=cnlocal.prepareStatement(insert);
			query.setString(1, name);
			rs=query.executeQuery();
			
			for(;rs.next();){
				obj=new JSONObject();
				obj.put("Date", rs.getDate("timestamp"));
				obj.put("Value", rs.getDouble("value"));
				result.put(obj);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
			if (query!=null)
			if (rs!=null) rs.close();
			if (cnlocal!=null) cnlocal.close();
			query.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		return result;
	}

	private void dbconnect(){
		try {
			cnlocal = Settings.connlocal();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
