package endpoints;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import extraction.Prediction;
import extraction.Snapshot;
import general.Backend;

@Path("/getPrediction")
public class GetPrediction {

	// private static final String ENDPOINT =
	// "https://www.khira.it/LeanDesignRules/LDRServices/designProject_leanRule";
	// private static final String DP_PARAMETER = "design_project_id";
	// private static final String VALIDATED_PARAMETER = "validated";

	@DefaultValue("")
	@QueryParam("products")
	String products;
	@DefaultValue("")
	@QueryParam("services")
	String services;
	@Context
	UriInfo ui;

	/**
	 * Creates a prediction snapshot and stores it in the database. Returns a
	 * JSON array with the predicted average sentiment and URL to the snapshot
	 * 
	 * @return - JSON array with URL to snapshot and predicted average sentiment
	 * @throws JSONException
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response welcome() throws JSONException {
		if ("".equals(products) && "".equals(services))
			return Response.status(Response.Status.BAD_REQUEST).build();

		Prediction p = new Prediction();
		JSONObject tmp = new JSONObject();
		tmp.put("Key", "");
		Backend b = new Backend(23, tmp);
		b.setMessage(22, new JSONObject("{\"Op\":\"getrestrictions\",\"Role\":\"DEVELOPER\",\"Key\":\"3gwnd3m3ipc0000\"}"));
		b.resolve();
		Snapshot s = new Snapshot(b);
		String name = "prediction" + System.currentTimeMillis();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		s.savePrediction("Self-generated snapshot", df.format(new Date()).split(" ")[0], 1, "-1", products.replace(',', ';'), services.replace(',', ';'));

		JSONArray json = p.predict(1, products.replace(',', ';'), services.replace(',', ';'));
		double sum = 0;
		int count = 0;
		for (int i = 0; i < json.length(); i++) {
			if (json.getJSONObject(i).has("Value")) {
				double val = json.getJSONObject(i).getDouble("Value");
				sum += val != -1 ? val : 0;
				count += val != -1 ? 1 : 0;
			}
		}

		double avg = sum / (count != 0 ? count : 12);

		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		String url = ui.getBaseUri().toString();
		String urlPred = url.split("Diversity/")[0] + "Diversity/pages/prediction_settings.html?snapshot=";
		obj.put("URL", urlPred + name);
		obj.put("Average", avg);
		result.put(obj);
		return Response.status(Response.Status.OK).entity(result.toString()).build();
	}
}
