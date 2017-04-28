package endpoints;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

@Path("/getLeanRules")
public class GetLeanRules {

	private static final String ENDPOINT = "https://www.khira.it/LeanDesignRules/LDRServices/designProject_leanRule";
	private static final String DP_PARAMETER = "design_project_id";
	private static final String VALIDATED_PARAMETER = "validated";
	
	@DefaultValue("")
	@QueryParam("design_project_id") // design projects
	String dp;
	@Context
	UriInfo ui;

	/**
	 * NOT DONE, NEED ENDPOINT TO RETURN ALL DESIGN PROJECTS
	 * Builds a matrix where the rows are all the rules in the selected design project and 
	 * the columns are the design projects with those rules. Matrix contains the sentiment
	 * value for each of those (rule,project) pairs.
	 * @return
	 * @throws JSONException
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response welcome() throws JSONException {
		if ("".equals(dp))
			return Response.status(Response.Status.BAD_REQUEST).build();
		int id;
		try {
			id = Integer.parseInt(dp);
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
	    return Response.status(Response.Status.OK).entity(getRules(id, true)).build();
	}
	
	/**
	 * Request all design projects (no endpoint yet, so the data is simulated)
	 * @return - a list of all design projects
	 */
	private List<Integer> getAllDesignProjects() {
		List<Integer> tmp = new ArrayList<Integer>();
		tmp.add(17);
		tmp.add(18);
		tmp.add(19);
		return tmp;
	}

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
			while((tmp = in.readLine()) != null) {
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
