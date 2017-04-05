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

import org.json.*;

import extraction.Snapshot;

@Path("/hello") // Path of the Endpoint
public class GetSnapshots {
	@DefaultValue("")
	@QueryParam("pss")
	String pss;// Puts get param into pss otherwise ""
	@Context UriInfo ui;

	@GET
  @Produces(MediaType.TEXT_HTML)
  public Response welcome() {
	  if("".equals(pss)) return Response.status(Response.Status.BAD_REQUEST).build();
	  int id;
	  try{
		  id = Integer.parseInt(pss);
	}catch(NumberFormatException e){
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
	  
	  
	  
    return Response.status(Response.Status.OK).entity(getAll(id)).build();
  }

	private String getAll(int pss_id) {
		String url = ui.getBaseUri().toString();
		url=url.split("Diversity/")[0]+"Diversity/pages/opinion_extraction_page.html?snapshot=";
		JSONArray response;
		try {
			response = extraction.Snapshot.getAll(pss_id);
			JSONObject obj;
			JSONArray snapshots=response.getJSONArray(1);
			for(int i=0; i<snapshots.length();i++){
				obj=snapshots.getJSONObject(i);
				obj.put("URL", "http://localhost:8080/Diversity/pages/opinion_extraction_page.html?snapshot="+obj.getInt("Id"));
				obj.remove("Id");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
		
		return "Got IN BABY"+response.toString();
	}

}