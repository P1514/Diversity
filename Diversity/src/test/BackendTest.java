package test;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import general.Backend;
import monitoring.Overwatch;

public class BackendTest {

	JSONObject obj;
	Backend tester;
	String result;
	Overwatch o = new Overwatch(true);

	@Test
	public void resolveRole() throws JSONException {

		obj = new JSONObject();
		obj.put("Role", "DESIGNER");
		tester = new Backend(22, obj);
		result = "[{\"Op\":\"Rights\",\"view_use_opinion_prediction\":true,\"create_edit_delete_model\":true,\"view_opinion_results\":true,\"view_OM\":true,\"save_delete_snapshots\":true}]";
		assertEquals("Should be equal to the string", result, tester.resolve());

	}

	@Test
	public void resolveGetTree() throws JSONException {
		o.run();
		obj = new JSONObject();
		obj.put("Op", "gettree");
		tester = new Backend(21, obj);
		result = "[{\"Op\":\"Tree\"},{\"PSS\":\"D522-1 PSS\",\"Products\":[{\"Name\":\"Morris Ground 1\"}]},{\"PSS\":\"D522-2 PSS\",\"Products\":"
				+ "[{\"Name\":\"Austin Basket\"},{\"Name\":\"Austin Soccer\"}]},{\"PSS\":\"D341-1 PSS\",\"Products\":[{\"Name\":\"Morris Sea 1000\"},"
				+ "{\"Name\":\"Morris Sea 2099\"},{\"Name\":\"Morris Wind\"}]},{\"PSS\":\"D231-1 PSS\",\"Products\":[{\"Name\":\"Austin Polo\"},"
				+ "{\"Name\":\"Austin Cricket\"}]},"
				+ "{\"PSS\":\"D231-2 PSS\",\"Products\":[{\"Name\":\"Austin XC\"},{\"Name\":\"Austin Base\"}]}]";
		assertEquals("Should be equal to the string", result, tester.resolve());

	}

	@Test
	public void resolveTopreachglobalsentiment() throws JSONException {
		o.run();
		obj = new JSONObject();
		obj.put("Op", "gettree");
		tester = new Backend(20, obj);
		result = "[{\"Op\":\"Graph\"},[{\"Filter\":\"D522-1 PSS\"},{\"Month\":\"DEC\",\"Value\":84.91},{"
				+ "\"Month\":\"JAN\",\"Value\":88.52},{\"Month\":\"FEB\",\"Value\":91"
				+ ".97},{\"Month\":\"MAR\",\"Value\":86.78},{\"Month\":\"APR\",\"Value"
				+ "\":85.7},{\"Month\":\"MAY\",\"Value\":85.01},{\"Month\":\"JUN\",\"V"
				+ "alue\":82.67},{\"Month\":\"JUL\",\"Value\":74.79},{\"Month\":\"AUG\""
				+ ",\"Value\":72.76},{\"Month\":\"SEP\",\"Value\":70.39},{\"Month\":\"OCT"
				+ "\",\"Value\":70.98},{\"Month\":\"NOV\",\"Value\":84.5},{\"Filter\":\"D522"
				+ "-2 PSS\"},{\"Month\":\"DEC\",\"Value\":39.12},{\"Month\":\"JAN\",\"Value\":"
				+ "33.87},{\"Month\":\"FEB\",\"Value\":34.19},{\"Month\":\"MAR\",\"Value\":37.55},"
				+ "{\"Month\":\"APR\",\"Value\":36.87},{\"Month\":\"MAY\",\"Value\":36.58},{\"Mont"
				+ "h\":\"JUN\",\"Value\":37.07},{\"Month\":\"JUL\",\"Value\":40.04},{\"Month\":\"A"
				+ "UG\",\"Value\":40.79},{\"Month\":\"SEP\",\"Value\":43.15},{\"Month\":\"OCT\",\"Va"
				+ "lue\":44.79},{\"Month\":\"NOV\",\"Value\":47.41},{\"Filter\":\"D231-1 PSS\"},{\"Mon"
				+ "th\":\"DEC\",\"Value\":60.99},{\"Month\":\"JAN\",\"Value\":67.53},{\"Month\":\"FEB\",\"Val"
				+ "ue\":65.01},{\"Month\":\"MAR\",\"Value\":65.15},{\"Month\":\"APR\",\"Value\":55.77},{\"Mon"
				+ "th\":\"MAY\",\"Value\":48.76},{\"Month\":\"JUN\",\"Value\":40.06},{\"Month\":\"JUL\",\"Val"
				+ "ue\":38.7},{\"Month\":\"AUG\",\"Value\":40.45},{\"Month\":\"SEP\",\"Value\":39.61},{\"Month\":\"OC"
				+ "T\",\"Value\":49.81},{\"Month\":\"NOV\",\"Value\":68.12},{\"Filter\":\"D341-1 PSS\"},{\"Month\":\"D"
				+ "EC\",\"Value\":35.92},{\"Month\":\"JAN\",\"Value\":38.44},{\"Month\":\"FEB\",\"Value\":41.27},{\"Mon"
				+ "th\":\"MAR\",\"Value\":43.51},{\"Month\":\"APR\",\"Value\":42.88},{\"Month\":\"MAY\",\"Value\":41.91},{\"Mo"
				+ "nth\":\"JUN\",\"Value\":43.81},{\"Month\":\"JUL\",\"Value\":49.87},{\"Month\":\"AUG\",\"Value\":49},{\"Mo"
				+ "nth\":\"SEP\",\"Value\":56.18},{\"Month\":\"OCT\",\"Value\":63.26},{\"Month\":\"NOV\",\"Value\":65.5}]]";
		assertEquals("Should be equal to the string", result, tester.resolve());

	}

}
