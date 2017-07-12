package endpoints;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONException;

@Path("/getLeanRules")
public class GetLeanRules {

//	private static final String ENDPOINT = "https://www.khira.it/LeanDesignRules/LDRServices/designProject_leanRule";
//	private static final String DP_PARAMETER = "design_project_id";
//	private static final String VALIDATED_PARAMETER = "validated";

	@DefaultValue("")
	@QueryParam("design_project_id") // design projects
	String dp;
	@Context
	UriInfo ui;
	
	/**
	 * Builds a JSON with all rules used in a design project, or from all design
	 * projects if -1 is passed as the design_project_id parameter. The output
	 * will contain the rule's score, projects where it is used, text and ID
	 * for each of the rules in the result.
	 * 
	 * @return a JSON array with lean rules
	 * @throws JSONException
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response welcome() throws JSONException {
		if ("".equals(dp))
			return Response.status(Response.Status.BAD_REQUEST).build();

		LeanRules lr = new LeanRules(dp);

		JSONArray json = lr.getResult();
		json.remove(0); // remove the "Op" key used in the backend to display the lean rules table in the graphical interface

		return Response.status(Response.Status.OK).entity(json.toString()).build();
	}
}
