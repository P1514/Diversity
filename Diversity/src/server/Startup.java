package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.json.JSONException;
import org.json.JSONObject;

import backend.GetModels;
import importDB.CleanDB;
import importDB.Data;
import importDB.Model;

@WebListener
public class Startup implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		CleanDB clean = new CleanDB();
		Data start = new Data();
		System.out.println("Starting up!");
		try {
			clean.clean();
			start.load();
			/*JSONObject json = readJsonFromUrl("http://diversity.euprojects.net/socialfeedbackextraction/twitter/PrimeministerGR");
		    System.out.println(json.toString());*/
		    //System.out.println(json.get("id"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}/* catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		GetModels model = new GetModels();

		JSONObject obj = new JSONObject();

		try {
			obj.put("Op", "create_model");
			obj.put("URI", "http://www.facebook.com");
			obj.put("Update", 1);
			obj.put("PSS", "D522-1 PSS");
			obj.put("Age", "5,99");
			obj.put("Gender", "Female");
			obj.put("Final_Product", true);
			obj.put("Archive", false);
			obj.put("Name", "Female");
			obj.put("User", 1);
			System.out.println(model.create_model(obj).toString());
			obj.put("Name", "Male");
			obj.put("Gender", "Male");
			System.out.println(model.create_model(obj).toString());
			obj.put("Name", "Old");
			obj.put("Gender", "All");
			obj.put("Age", "50,99");
			System.out.println(model.create_model(obj).toString());
			obj.put("Name", "Young");
			obj.put("Age", "5,49");
			System.out.println(model.create_model(obj).toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		System.out.println("Shutting down!");
	}
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("Unicode")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
}