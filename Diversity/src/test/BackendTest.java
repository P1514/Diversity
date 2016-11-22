package test;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import general.Backend;



public class BackendTest {

	JSONObject obj = new JSONObject();
	Backend tester;
	String result;
	
	
	@Test
	public void resolveRole0() throws JSONException {
		obj.put("Role","DESIGNER");
		tester = new Backend(22,obj);
		result = "[{\"Op\":\"Rights\",\"view_use_opinion_prediction\":true,\"create_edit_delete_model\":true,\"view_opinion_results\":true,\"view_OM\":true,\"save_delete_snapshots\":true}]";
		assertEquals("Should equal the string",result , tester.resolve());

	}
	
	@Test
	public void resolveRole1() throws JSONException {
		obj.put("Role","no_role");
		tester = new Backend(22,obj);
		result = "[{\"Op\":\"Rights\",\"view_use_opinion_prediction\":fals,\"create_edit_delete_model\":false,\"view_opinion_results\":false,\"view_OM\":false,\"save_delete_snapshots\":false}]";
		assertEquals("Should equal the string",result , tester.resolve());
	}

}
