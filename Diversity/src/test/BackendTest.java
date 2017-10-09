package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import general.Backend;
import general.Settings;
import monitoring.Oversight;

public class BackendTest extends Thread {

	JSONObject obj, obj1;
	Backend tester;
	String result;
	Oversight o = new Oversight(true);

	public BackendTest() throws ClassNotFoundException, SQLException {
		o.run();
		Settings.connlocal().close();

	}



	public void run(){
		System.out.println("Thread");
		try {
			double rand = Math.round(Math.random()*13);
			System.out.println(rand+"\n");
	        switch ((int)rand) {
	            case 1:  this.resolveRole();
	                     break;
	            case 2:  this.resolveGetTree();
	                     break;
	            case 3:  this.resolveGetTreePss();
	                     break;
	            case 4:  this.resolveTopreachglobalsentiment();
	                     break;
	            case 5:  this.resolveLoad();
	                     break;
	            case 6:  this.resolveGetposts();
	                     break;
	            case 7:  this.resolveGetmodels();
	                     break;
	            case 8:  this.resolveGetconfig();
	                     break;
	            case 9:  this.resolveGetModel();
	                     break;
	            case 10: this.resolveGetPSS();
	                     break;
	            case 11: this.resolveOpinionExtraction();
	                     break;
	            case 12: this.resolveOeRefresh();
	                     break;
	            default: this.resolveOeRefreshExtrapolate();
	                     break;
	        }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// before running test, import test.db from the test directory
	@Test
	public void resolveRole() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Role", "DESIGNER");
		obj.put("Key", "10");
		tester = new Backend(22, obj);
		System.out.println("Role Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveGetTree() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "gettree");
		obj.put("Key", "10");
		tester = new Backend(21, obj);
		System.out.println("Get Tree Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveGetTreePss() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();

		obj = new JSONObject();
		obj.put("Op", "gettree");
		obj.put("Pss", "D231-2 PSS");
		obj.put("Key", "10");
		tester = new Backend(21, obj);
		System.out.println("Get Tree Pss Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveTopreachglobalsentiment() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "Top5Reach");
		obj.put("Key", "10");
		tester = new Backend(20, obj);
		System.out.println("Top Reach Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveLoad() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Key", "10");
		tester = new Backend(2, obj);
		System.out.println("Load Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveGetposts() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Key", "10");
		obj.put("Op", "getposts");
		obj.put("Id", "860");
		obj.put("Param", "860");
		obj.put("Day", "5");
		obj.put("Month", "NOV");
		obj.put("Year", "2012");
		obj.put("Product", "Morris Ground 1");
		tester = new Backend(4, obj);
		System.out.println("Get Posts Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveGetmodels() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "getmodels");
		obj.put("Key", "10");

		tester = new Backend(5, obj);
		System.out.println("Get Models Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveGetconfig() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "getconfig");
		obj.put("Id", "10");
		obj.put("Key", "10");
		tester = new Backend(12, obj);
		System.out.println("Get Config Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveGetModel() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Key", "10");
		obj.put("Op", "get_model");
		obj.put("Id", "10");
		tester = new Backend(15, obj);
		System.out.println("Get Model Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveGetPSS() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "getpss");
		obj.put("Key", "10");
		tester = new Backend(17, obj);
		System.out.println("Get Pss Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveOpinionExtraction() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "opinion_extraction");
		obj.put("Id", "10");
		obj.put("Key", "10");
		tester = new Backend(18, obj);
		System.out.println("Opinio Extraction Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveOeRefresh() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		// obj.put("Param", "Global");
		// obj.put("Values", "");
		obj.put("Filter", "");
		obj.put("Id", "10");
		obj.put("Key", "10");
		tester = new Backend(19, obj);
		System.out.println("Oe Refresh Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveCreateModel() throws JSONException {

		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "create_model");
		obj.put("Key", "10");
		obj.put("PSS", "D522-1 PSS");
		obj.put("Products", "Morris Ground 1;");
		obj.put("Archive", "false");
		obj.put("User", "1");
		obj.put("Final_Products", "Morris Ground 1;");
		obj.put("Update", "1");
		obj.put("URI", "Facebook,abcd;");
		obj.put("Start_date", "1970-01-04");
		obj.put("Name", "guffiss");
		obj.put("mediawiki", "true");
		obj.put("design_project", "1221");
		obj.put("Key", "10");
		tester = new Backend(14, obj);
		System.out.println("Create Model Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveUpdateModel() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "update_model");
		obj.put("PSS", "D522-1 PSS");
		obj.put("Products", "Morris Ground 1;");
		obj.put("Archive", "false");
		obj.put("User", "1");
		obj.put("Final_Products", "Morris Ground 1;");
		obj.put("Update", "10");
		obj.put("URI", "Facebook,adidas;");
		obj.put("Start_date", "1970-01-02");
		obj.put("Name", "D522-1 PSS");
		obj.put("Id", "10");
		obj.put("mediawiki", "true");
		obj.put("Key", "10");
		tester = new Backend(16, obj);
		System.out.println("Update Model Test Output: " + tester.resolve().toString());

	}

	/*
	 * @Test public void resolveClean() throws JSONException {
	 * 
	 * obj = new JSONObject(); tester = new Backend(7, obj); result =
	 * "[{\"Op\":\"Error\",\"Message\":\"Cleaned Successfully\"}]";
	 * assertEquals("Should be equal to the string", result, tester.resolve());
	 * }
	 */

	@Test
	public void resolveOeRefreshExtrapolate() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "oe_refresh");
		obj.put("Param", "Age,Gender,Location,");
		obj.put("Values", "All,All,All,");
		obj.put("Filter", "Product");
		obj.put("Id", "10");
		obj.put("Extrapolate", 1);
		obj.put("Key", "10");
		tester = new Backend(19, obj);
		System.out.println("Extrapolate Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolvePrediction() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "prediction");
		obj.put("Products", "71;74");
		obj.put("Services", "69;66");
		obj.put("Key", "10");
		tester = new Backend(23, obj);
		System.out.println("Prediction Test Output: " + tester.resolve().toString());

	}
	
	@Test
	public void resolvePredictionLifeCycle() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "prediction");
		obj.put("type", "lifeCycle");
		obj.put("Products", "71;74");
		obj.put("Services", "69;66");
		obj.put("Key", "10");
		tester = new Backend(23, obj);
		System.out.println("Prediction Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveTopreachglobalsentiment5pss() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "Top5Reach");
		obj.put("PSS", "D522-1 PSS;D231-1 PSS;D522-1 PSS;D522-1 PSS;D522-1 PSS;D522-1 PSS");
		obj.put("Key", "10");
		tester = new Backend(20, obj);
		System.out.println("5pss Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveSnapshotPrediction() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "Snapshot");
		obj.put("Products", "13;14;15");
		obj.put("name", "aapredfd");
		obj.put("creation_date", "2017-01-28T16:37:01.466Z");
		obj.put("timespan", "6");
		obj.put("type", "Prediction");
		obj.put("user", "testee3e");
		obj.put("Key", "10");
		tester = new Backend(24, obj);
		System.out.println("Snapshot Prediction Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveSnapshotLoadNames() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "load_snapshot");
		obj.put("Type", "Prediction");
		obj.put("Key", "10");
		tester = new Backend(25, obj);
		System.out.println("Snapshot Load Names Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveSnapshotLoad() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "load_snapshot");
		obj.put("Name", "testing");
		obj.put("Key", "10");
		tester = new Backend(25, obj);
		System.out.println("Snapshot Load Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveSnapshotExtraction() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "Snapshot");
		obj.put("name", "test101");
		obj.put("creation_date", "2017-01-28T16:37:01.466Z");
		obj.put("timespan", "6");
		obj.put("Id", "10");
		obj.put("type", "Extraction");
		obj.put("user", "test12345");
		obj.put("Key", "10");
		tester = new Backend(24, obj);
		System.out.println("Snapshot Extraction Test Output: " + tester.resolve().toString());
	}

	@Test
	public void resolveSnapshotLoadExtraction() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "load_snapshot");
		obj.put("Name", "test101");
		obj.put("Type", "all");
		obj.put("Key", "10");
		tester = new Backend(25, obj);
		System.out.println("Snapshot Extraction Load Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveGetpostsWord() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "getposts");
		obj.put("Id", "10");
		obj.put("Product", "Morris Ground 1");
		obj.put("word", "average");
		obj.put("Key", "10");
		tester = new Backend(4, obj);
		System.out.println("Get Posts Word Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveWiki() throws JSONException {

		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "get_mediawiki");
		obj.put("PSS", "pss1");
		obj.put("Key", "10");
		tester = new Backend(28, obj);
		System.out.println("Wiki Test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveIgnoreWord() throws JSONException {

		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "set_ignore_word");
		obj.put("id", "10");
		obj.put("Word", "phenomenal");
		obj.put("Key", "10");
		tester = new Backend(27, obj);
		System.out.println("Ignore Word test Output: " + tester.resolve().toString());

	}

	@Test
	public void resolveTagcloud() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		// System.out.println(obj1.toString());

		obj = new JSONObject();
		obj.put("Op", "tagcloud");
		obj.put("User", "4");
		obj.put("Id", "10");
		obj.put("Key", "10");

		tester = new Backend(26, obj);
		System.out.println("Tag Cloud Test Output: " + tester.resolve().toString());

	}
	
	
	@Test
	public void resolveCollaboration() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "collaboration");
		//obj.put("Products", "71,74");
		//obj.put("Services", "69,66");
		//obj.put("Company", "DESMA");
		obj.put("Key", "10");
		System.out.println(obj.toString());
		tester = new Backend(33, obj);
		System.out.println("Collaboration Test Output: " + tester.resolve().toString());

		
	}
	
	@Test
	public void resolveCollaborationJustCompany() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "collaboration");
		obj.put("Company", "DESMA");
		obj.put("Key", "10");
		System.out.println(obj.toString());
		tester = new Backend(33, obj);
		System.out.println("Collaboration Test Output: " + tester.resolve().toString());

		
	}
	@Test
	public void resolveGet_user_roles() throws JSONException {
		obj1 = new JSONObject("{\"Role\":\"DEVELOPER\",\"Op\":\"getrestrictions\",\"Key\":\"10\"}");
		new Backend(22, obj1).resolve();
		obj = new JSONObject();
		obj.put("Op", "get_user_roles");
		obj.put("Key", "10");
		JSONArray arr = new JSONArray();
		JSONObject obj2 = new JSONObject();
		obj2.put("User_ID", 1);
		obj2.put("Role_ID", 2);
		arr.put(obj2);
		obj2 = new JSONObject();
		obj2.put("User_ID", 2);
		obj2.put("Role_ID", 4);
		arr.put(obj2);
		obj.put("IDs", arr);
		System.out.println(obj.toString());
		tester = new Backend(37, obj);
		System.out.println("Get_user_roles Test Output: " + tester.resolve().toString());

		
	}
	


	@Test
	public void multipletests() throws JSONException {
		for (int i = 0; i < 1000; i++)
			System.out.println("TEST: "+i+"\n");
			(new Thread(this)).start();
	}
	

}
