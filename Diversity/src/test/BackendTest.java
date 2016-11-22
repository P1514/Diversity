package test;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import general.Backend;



public class BackendTest {

	JSONObject obj;
	Backend tester;
	String result;
	
	
	@Test
	public void resolveRole() throws JSONException {
		obj = new JSONObject();
		obj.put("Role","DESIGNER");
		tester = new Backend(22,obj);
		result = "[{\"Op\":\"Rights\",\"view_use_opinion_prediction\":true,\"create_edit_delete_model\":true,\"view_opinion_results\":true,\"view_OM\":true,\"save_delete_snapshots\":true}]";
		assertEquals("Should be equal to the string",result , tester.resolve());

	}
	
	@Test
	public void resolveGetTree() throws JSONException {
		obj = new JSONObject();
		obj.put("Op","gettree");
		tester = new Backend(21,obj);
		result = "[{\"Op\":\"Tree\"}]";
		assertEquals("Should be equal to the string",result , tester.resolve());

	}
	

}
