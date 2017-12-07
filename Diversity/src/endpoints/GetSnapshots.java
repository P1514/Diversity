package endpoints;
import general.Logging;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.util.logging.Level;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.*;

import java.util.logging.Logger;

@Path("/getSnapshots") // Path of the Endpoint
public class GetSnapshots {
	private static final Logger LOGGER = new Logging().create(GetSnapshots.class.getName());
	@DefaultValue("") // Sets pss default value as ""
	@QueryParam("pss") // Sets pss value as the pss form get if it exists
	String pss;
	@DefaultValue("")
	@QueryParam("type")
	String type;
	@Context
	UriInfo ui; // Get the URL from the requesting Website

	@GET // Indicates that this method answers a get request
	@Produces(MediaType.TEXT_HTML) // Indicates this method answers a HTML
									// request
	public Response welcome() {
		if ("".equals(pss))
			return Response.status(Response.Status.BAD_REQUEST).build();
		if ("".equals(type))
			return Response.status(Response.Status.BAD_REQUEST).build();
		int id;
		try {
			id = Integer.parseInt(pss);
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		return Response.status(Response.Status.OK).entity(getAll(id, type)).build();
	}

	/**
	 * Returns all snapshots of a given PSS, or all snapshots of all PSSs if -1
	 * is passed
	 * 
	 * @param pssId
	 *            - id of the pss from which to retrieve the snapshots, or -1 to
	 *            retrieve all snapshots
	 * @return - a JSON string with the list of snapshots
	 */
	private String getAll(int pssId, String type) {
		String url = ui.getBaseUri().toString();
		String urlExt = url.split("Diversity/")[0] + "Diversity/pages/opinion_extraction_page.html?snapshot=";
		String urlPred = url.split("Diversity/")[0] + "Diversity/pages/prediction_settings.html?snapshot=";
		JSONArray response;
		try {
			response = extraction.Snapshot.getAll(pssId, type);
			JSONObject obj;
			JSONArray snapshots = response.getJSONArray(1);
			StringBuilder bld = new StringBuilder();
			for (int i = 0; i < snapshots.length(); i++) {
				obj = snapshots.getJSONObject(i);
				bld.delete(0, bld.length());
				if (obj.has("Type") && obj.getString("Type").equals("prediction")) {
					bld.append(urlPred);
				} else {
					bld.append(urlExt);
				}				
					try {
						bld.append(URLEncoder.encode(obj.getString("Name"), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						LOGGER.log(Level.SEVERE, "Error While creating URL for snapshots => " + bld);
					}
				
				obj.put("URL", bld.toString());
				obj.remove("Id");
			}

		} catch (JSONException e) {

			LOGGER.log(Level.SEVERE, "Error", e);
			return "";
		}

		return response.toString();
	}

}