package endpoints;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

import general.Settings;

@Path("/getNumSources")
public class GetNumSources {

	@DefaultValue("")
	@QueryParam("pss")
	String pss;

	@Context
	UriInfo ui;

	private Connection cnlocal;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response welcome() {
		if ("".equals(pss)) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		int id;
		try {
			id = Integer.parseInt(pss);
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		return Response.status(Response.Status.OK).entity(getNumPosts() + "").build();
	}

	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getNumPosts() {
		int numPosts = 0;
		String select = "select count(*) from " + Settings.lutable + " where pss=?";

		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cnlocal.prepareStatement(select);
			query1.setInt(1, Integer.parseInt(pss));
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					numPosts = rs.getInt(1);
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
		System.out.println(numPosts);
		return numPosts;
	}
}
