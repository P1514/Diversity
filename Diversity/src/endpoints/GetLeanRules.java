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
import general.LeanRules;

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

		LeanRules lr = new LeanRules(dp);

		JSONArray json = lr.getResult();

		return Response.status(Response.Status.OK).entity(json.toString()).build();
	}
}