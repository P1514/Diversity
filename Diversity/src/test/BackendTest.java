package test;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import general.Backend;
import monitoring.Oversight;

public class BackendTest {

	JSONObject obj;
	Backend tester;
	String result;
	Oversight o = new Oversight(true);
	public Connection cnlocal;

	@Test
	public void resolveRole() throws JSONException {

		obj = new JSONObject();
		obj.put("Role", "DESIGNER");
		tester = new Backend(22, obj);
		result = "[{\"Op\":\"Rights\",\"view_use_opinion_prediction\":t"
				+ "rue,\"create_edit_delete_model\":true,\"view_opinion_re"
				+ "sults\":true,\"view_OM\":true,\"save_delete_snapshots\":true}]";
		assertEquals("Should be equal to the string", result, tester.resolve());

	}

	@Test
	public void resolveGetTree() throws JSONException {
		o.run();
		obj = new JSONObject();
		obj.put("Op", "gettree");
		tester = new Backend(21, obj);
		result = "[{\"Op\":\"Tree\"},{\"PSS\":\"D522-1 PSS\",\"Products\":[{\"Name\":\"Morr"
				+ "is Ground 1\"}]},{\"PSS\":\"D522-2 PSS\",\"Products\":"
				+ "[{\"Name\":\"Austin Basket\"},{\"Name\":\"Austin Soccer\"}]},{\"PSS\":\"D3"
				+ "41-1 PSS\",\"Products\":[{\"Name\":\"Morris Sea 1000\"},"
				+ "{\"Name\":\"Morris Sea 2099\"},{\"Name\":\"Morris Wind\"}]},{\"PSS\":\"D231-"
				+ "1 PSS\",\"Products\":[{\"Name\":\"Austin Polo\"},"
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
		result ="[{\"Op\":\"Graph\"},[{\"Filter\":\"D522-1 PSS\"},{\"Month\":\"DEC\",\"Val"
				+ "ue\":84.91},{\"Month\":\"JAN\",\"Value\":88.52},{\"Month\":\"FEB\",\"Val"
				+ "ue\":91.97},{\"Month\":\"MAR\",\"Value\":86.78},{\"Month\":\"APR\",\"Va"
				+ "lue\":85.7},{\"Month\":\"MAY\",\"Value\":85.01},{\"Month\":\"JUN\",\"Va"
				+ "lue\":82.67},{\"Month\":\"JUL\",\"Value\":74.79},{\"Month\":\"AUG\",\"Va"
				+ "lue\":72.76},{\"Month\":\"SEP\",\"Value\":70.39},{\"Month\":\"OCT\",\"Va"
				+ "lue\":70.98},{\"Month\":\"NOV\",\"Value\":84.5},{\"Filter\":\"D522-2 PSS"
				+ "\"},{\"Month\":\"DEC\",\"Value\":39.12},{\"Month\":\"JAN\",\"Value\":33."
				+ "87},{\"Month\":\"FEB\",\"Value\":34.19},{\"Month\":\"MAR\",\"Value\":37.5"
				+ "5},{\"Month\":\"APR\",\"Value\":36.87},{\"Month\":\"MAY\",\"Value\":36.58"
				+ "},{\"Month\":\"JUN\",\"Value\":37.07},{\"Month\":\"JUL\",\"Value\":40.04},"
				+ "{\"Month\":\"AUG\",\"Value\":40.79},{\"Month\":\"SEP\",\"Value\":43.15},"
				+ "{\"Month\":\"OCT\",\"Value\":44.79},{\"Month\":\"NOV\",\"Value\":47.41},{"
				+ "\"Filter\":\"D231-1 PSS\"},{\"Month\":\"DEC\",\"Value\":60.99},{\"Month\""
				+ ":\"JAN\",\"Value\":67.53},{\"Month\":\"FEB\",\"Value\":65.01},{\"Month\":"
				+ "\"MAR\",\"Value\":65.15},{\"Month\":\"APR\",\"Value\":55.77},{\"Month\":"
				+ "\"MAY\",\"Value\":48.76},{\"Month\":\"JUN\",\"Value\":40.06},{\"Month\":"
				+ "\"JUL\",\"Value\":38.7},{\"Month\":\"AUG\",\"Value\":40.45},{\"Month\":"
				+ "\"SEP\",\"Value\":39.61},{\"Month\":\"OCT\",\"Value\":49.81},{\"Month\""
				+ ":\"NOV\",\"Value\":68.12},{\"Filter\":\"D341-1 PSS\"},{\"Month\":\"DEC\""
				+ ",\"Value\":35.92},{\"Month\":\"JAN\",\"Value\":38.44},{\"Month\":\"FEB\","
				+ "\"Value\":41.27},{\"Month\":\"MAR\",\"Value\":43.51},{\"Month\":\"APR\",\"Val"
				+ "ue\":42.88},{\"Month\":\"MAY\",\"Value\":41.91},{\"Month\":\"JUN\",\"Value\":"
				+ "43.81},{\"Month\":\"JUL\",\"Value\":49.87},{\"Month\":\"AUG\",\"Value\":49},{"
				+ "\"Month\":\"SEP\",\"Value\":56.18},{\"Month\":\"OCT\",\"Value\":63.26},{\"Mon"
				+ "th\":\"NOV\",\"Value\":65.5}]]";
		assertEquals("Should be equal to the string", result, tester.resolve());

	}
	
	@Test
	public void resolveLoad() throws JSONException {
		o.run();
		obj = new JSONObject();
		tester = new Backend(2, obj);
		result = "[{\"Op\":\"Error\",\"Message\":\"Loaded Successfully\"}]";
		assertEquals("Should be equal to the string", result, tester.resolve());

	}
	
	@Test
	public void resolveGetposts() throws JSONException {
		o.run();
		obj = new JSONObject();
		obj.put("Op", "getposts");
		obj.put("Id", "806");
		tester = new Backend(4, obj);
		result = "[{\"Op\":\"table\"},{\"Reach\":\"1.93\",\"Mes"
				+ "sage\":\"Check the new Morris Ground 1! I say phen"
				+ "omenal! No?\",\"Comments\":\"26\",\"Influence\":\"1.66\",\"Po"
				+ "larity\":\"87.42\",\"Id\":\"487\",\"Gender\":\"Female\",\"Ag"
				+ "e\":\"15\",\"Date\":\"2016-03-22\",\"Name\":\"Sandra Goodr"
				+ "ich\",\"Location\":\"Europe\"},{\"Reach\":\"1.93\",\"Messag"
				+ "e\":\"Have you tested the Morris Ground 1? Hum, phenomenal!\",\"Com"
				+ "ments\":\"26\",\"Influence\":\"1.66\",\"Polarity\":\"75.45\",\"Id\":\"9"
				+ "08\",\"Gender\":\"Female\",\"Age\":\"15\",\"Date\":\"2016-07-08\",\"Na"
				+ "me\":\"Sandra Goodrich\",\"Location\":\"Europe\"},{\"Reach\":\"1.86\",\"Mess"
				+ "age\":\"Tell me what you think of the new Morris Ground 1! These sneakers a"
				+ "re phenomenal!\",\"Comments\":\"25\",\"Influence\":\"1.66\",\"Polarity\":\"73"
				+ ".8\",\"Id\":\"989\",\"Gender\":\"Female\",\"Age\":\"15\",\"Date\":\"2016-07-3"
				+ "0\",\"Name\":\"Sandra Goodrich\",\"Location\":\"Europe\"},{\"Reach\":\"1.86\",\"M"
				+ "essage\":\"Tell me what you think of the new Morris Ground 1! phenomenal!\",\"Com"
				+ "ments\":\"25\",\"Influence\":\"1.66\",\"Polarity\":\"81.14\",\"Id\":\"822\",\"Gend"
				+ "er\":\"Female\",\"Age\":\"15\",\"Date\":\"2016-06-16\",\"Name\":\"Sandra Goodric"
				+ "h\",\"Location\":\"Europe\"},{\"Reach\":\"1.86\",\"Message\":\"They launched the n"
				+ "ew Morris Ground 1! I say phenomenal! No?\",\"Comments\":\"25\",\"Influence\":\"1."
				+ "66\",\"Polarity\":\"87.56\",\"Id\":\"576\",\"Gender\":\"Female\",\"Age\":\"15\",\"D"
				+ "ate\":\"2016-04-13\",\"Name\":\"Sandra Goodrich\",\"Location\":\"Europe\"}]";
		assertEquals("Should be equal to the string", result, tester.resolve());

	}
	
	@Test
	public void resolveGetmodels() throws JSONException {
		o.run();
		obj = new JSONObject();
		obj.put("Op", "getmodels");
		tester = new Backend(5, obj);
		result ="[{\"Op\":\"Models\"},{\"PSS\":\"D522-1 PSS\",\"Id\":806,\"Name\":\"Morris "
				+ "Ground 1\"},{\"PSS\":\"D522-2 PSS\",\"Id\":807,\"Name\":\"D522-2 PSS\"},{\"P"
				+ "SS\":\"D522-2 PSS\",\"Id\":808,\"Name\":\"Austin Basket\"},{\"PSS\":\"D522-"
				+ "2 PSS\",\"Id\":809,\"Name\":\"Austin Soccer\"},{\"PSS\":\"D341-1 PSS\",\"Id\":8"
				+ "10,\"Name\":\"D341-1 PSS\"},{\"PSS\":\"D341-1 PSS\",\"Id\":811,\"Name\":\"Mor"
				+ "ris Sea 1000\"},{\"PSS\":\"D341-1 PSS\",\"Id\":812,\"Name\":\"Morris Sea 20"
				+ "99\"},{\"PSS\":\"D341-1 PSS\",\"Id\":813,\"Name\":\"Morris Wind\"},{\"PSS\":\"D2"
				+ "31-1 PSS\",\"Id\":814,\"Name\":\"D231-1 PSS\"},{\"PSS\":\"D231-1 PSS\",\"Id\":81"
				+ "5,\"Name\":\"Austin Polo\"},{\"PSS\":\"D231-1 PSS\",\"Id\":816,\"Name\":\"Austin"
				+ " Cricket\"},{\"PSS\":\"D231-2 PSS\",\"Id\":817,\"Name\":\"D231-2 PSS\"},{\"PSS\""
				+ ":\"D231-2 PSS\",\"Id\":818,\"Name\":\"Austin XC\"},{\"PSS\":\"D231-2 PSS\",\"Id\""
				+ ":819,\"Name\":\"Austin Base\"}]"; 
		assertEquals("Should be equal to the string", result, tester.resolve());

	}
	
	
	@Test
	public void resolveClean() throws JSONException {o.run();
		obj = new JSONObject();
		tester = new Backend(7, obj);
		result = "[{\"Op\":\"Error\",\"Message\":\"Cleaned Successfully\"}]";
		assertEquals("Should be equal to the string", result, tester.resolve());
	}
	
	@Test
	public void resolveGetconfig() throws JSONException {
		o.run();
		obj = new JSONObject();
		obj.put("Op", "getconfig");
		obj.put("Id", "806");
		tester = new Backend(4, obj);
		result = "[{\"Op\":\"table\"},{\"Reach\":\"1.93\",\"Mes"
				+ "sage\":\"Check the new Morris Ground 1! I say ph"
				+ "enomenal! No?\",\"Comments\":\"26\",\"Influence\""
				+ ":\"1.66\",\"Polarity\":\"87.42\",\"Id\":\"487\",\"G"
				+ "ender\":\"Female\",\"Age\":\"15\",\"Date\":\"2016-0"
				+ "3-22\",\"Name\":\"Sandra Goodrich\",\"Location\":\"E"
				+ "urope\"},{\"Reach\":\"1.93\",\"Message\":\"Have you t"
				+ "ested the Morris Ground 1? Hum, phenomenal!\",\"Comme"
				+ "nts\":\"26\",\"Influence\":\"1.66\",\"Polarity\":\"75"
				+ ".45\",\"Id\":\"908\",\"Gender\":\"Female\",\"Age\":\"1"
				+ "5\",\"Date\":\"2016-07-08\",\"Name\":\"Sandra Goodric"
				+ "h\",\"Location\":\"Europe\"},{\"Reach\":\"1.86\",\"Mes"
				+ "sage\":\"Tell me what you think of the new Morris Groun"
				+ "d 1! These sneakers are phenomenal!\",\"Comments\":\"25\""
				+ ",\"Influence\":\"1.66\",\"Polarity\":\"73.8\",\"Id\":\"98"
				+ "9\",\"Gender\":\"Female\",\"Age\":\"15\",\"Date\":\"2016-"
				+ "07-30\",\"Name\":\"Sandra Goodrich\",\"Location\":\"Europ"
				+ "e\"},{\"Reach\":\"1.86\",\"Message\":\"Tell me what you th"
				+ "ink of the new Morris Ground 1! phenomenal!\",\"Comments\":"
				+ "\"25\",\"Influence\":\"1.66\",\"Polarity\":\"81.14\",\"Id\""
				+ ":\"822\",\"Gender\":\"Female\",\"Age\":\"15\",\"Date\":\"2"
				+ "016-06-16\",\"Name\":\"Sandra Goodrich\",\"Location\":\"Eur"
				+ "ope\"},{\"Reach\":\"1.86\",\"Message\":\"They launched the n"
				+ "ew Morris Ground 1! I say phenomenal! No?\",\"Comments\":\"2"
				+ "5\",\"Influence\":\"1.66\",\"Polarity\":\"87.56\",\"Id\":\"57"
				+ "6\",\"Gender\":\"Female\",\"Age\":\"15\",\"Date\":\"2016-04-"
				+ "13\",\"Name\":\"Sandra Goodrich\",\"Location\":\"Europe\"}]";
		assertEquals("Should be equal to the string", result, tester.resolve());
	}
	

}
