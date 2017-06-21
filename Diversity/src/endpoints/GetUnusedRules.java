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

@Path("/getUnusedRules")
public class GetUnusedRules {

	private static final String ENDPOINT = "https://www.khira.it/LeanDesignRules/LDRServices/designProject_leanRule";
	private static final String DP_PARAMETER = "design_project_id";
	private static final String VALIDATED_PARAMETER = "validated";

	@Context
	UriInfo ui;


	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response welcome() throws JSONException {

		LeanRules lr = new LeanRules("-1");

		JSONArray json = lr.getUnusedRules();
		return Response.status(Response.Status.OK).entity(json.toString()).build();
	}
}