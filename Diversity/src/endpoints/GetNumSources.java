package endpoints;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


import extraction.Collaboration;
import general.Logging;
import general.Settings;



@Path("/getNumSources")
public class GetNumSources {
	private static final Logger LOGGER = new Logging().create(Collaboration.class.getName());
	private final String select = "select count(*) from " + Settings.lutable + " where pss=?";


	@DefaultValue("")
	@QueryParam("pss")
	String pss;

	@Context
	UriInfo ui;


	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response welcome() {
		if ("".equals(pss)) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		return Response.status(Response.Status.OK).entity(Integer.toString(getNumPosts())).build();
	}



	private int getNumPosts() {
		int numPosts = 0;
		try(Connection cnlocal=Settings.connlocal();
				PreparedStatement query1 = cnlocal.prepareStatement(select);) {
			query1.setInt(1, Integer.parseInt(pss));
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					numPosts = rs.getInt(1);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error", e);
		}

		return numPosts;
	}
}
