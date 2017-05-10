package endpoints;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.media.jfxmedia.logging.Logger;

import general.Settings;

@Path("/getLeanRules")
public class GetLeanRules {

	private static final String ENDPOINT = "https://www.khira.it/LeanDesignRules/LDRServices/designProject_leanRule";
	private static final String DP_PARAMETER = "design_project_id";
	private static final String VALIDATED_PARAMETER = "validated";

	private Map<Integer, ArrayList<Integer>> matrix;
	private Connection cnlocal;
	private int numDp;
	private int numRules;

	@DefaultValue("")
	@QueryParam("design_project_id") // design projects
	String dp;
	@Context
	UriInfo ui;

	/**
	 * Builds a matrix where the rows are all the rules in the selected design
	 * project and the columns are the design projects with those rules. Still
	 * need to include the polarity value of each rule.
	 * 
	 * @return - a JSON string with the lean rule matrix
	 * @throws JSONException
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response welcome() throws JSONException {
		if ("".equals(dp))
			return Response.status(Response.Status.BAD_REQUEST).build();

		// get design project rules
		List<Integer> rules = getRules();

		matrix = new HashMap<Integer, ArrayList<Integer>>();
		// for each rule get the design projects where it is active
		for (int r : rules) {
			List<Integer> designProjects = getDesignProjects(r);

			for (int p : designProjects) {
				List<Integer> tmp;
				if (!matrix.containsKey(r)) {
					tmp = new ArrayList<Integer>();
					tmp.add(p);
					matrix.put(r, (ArrayList<Integer>) tmp);
				} else {
					tmp = matrix.get(r);
					tmp.add(p);
					matrix.replace(r, (ArrayList<Integer>) tmp);
				}
			}
		}
		
		JSONObject obj = new JSONObject(matrix);
		return Response.status(Response.Status.OK).entity(obj.toString()).build();
	}

	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<Integer> getRules() {
		List<Integer> rules = new ArrayList<Integer>();

		String select = "SELECT lean_rule_id FROM diversity_common_repository.design_project_rule WHERE design_project_id =?";

		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cnlocal.prepareStatement(select);
			query1.setInt(1, Integer.parseInt(dp));
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					rules.add(rs.getInt(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			cnlocal.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return (ArrayList<Integer>) rules;
	}

	private ArrayList<Integer> getDesignProjects(int ruleId) {
		List<Integer> designProjects = new ArrayList<Integer>();

		String select = "SELECT design_project_id FROM diversity_common_repository.design_project_rule WHERE lean_rule_id =? AND checked = 1";

		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cnlocal.prepareStatement(select);
			query1.setInt(1, ruleId);
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					designProjects.add(rs.getInt(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			cnlocal.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return (ArrayList<Integer>) designProjects;
	}

	/**
	 * @deprecated
	 */
	private List<Integer> getAllDesignProjects() {
		List<Integer> tmp = new ArrayList<Integer>();
		tmp.add(17);
		tmp.add(18);
		tmp.add(19);
		return tmp;
	}

	/**
	 * @deprecated
	 */
	private String getRules(int id, boolean validated) {
		String jsonString = "";
		try {
			URL url = new URL(ENDPOINT + "?" + DP_PARAMETER + "=" + id + "&" + VALIDATED_PARAMETER + "=" + validated);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("HTTP error " + conn.getResponseCode());
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			String tmp;
			while ((tmp = in.readLine()) != null) {
				jsonString += tmp;
			}

			in.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonString;
	}

}
