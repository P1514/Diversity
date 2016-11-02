package general;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import modeling.GetModels;

@WebListener
public class Startup implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		CleanDB clean = new CleanDB();
		Data start = new Data();
		System.out.println("Starting up!");
		try {

			//System.out.println(clean.clean());
			if (Settings.JSON_use == false) {
				//start.load();
			} else {
				JSONArray json = new JSONArray(readUrl(
				"http://diversity.euprojects.net/socialfeedbackextraction/getPosts/?epochsFrom[]=111&epochsFrom[]=111&epochsTo[]=333333333&epochsTo[]=333333333&pssId=3&accounts[]=Spyros&accounts[]=JohnSmith"));
				//System.out.println(json);
				//start.load(json);
			}
			/*
			 * System.out.println("\n0:"+json.getJSONObject(0).toString()+"\n");
			 * System.out.println("\n1:"+json.getJSONObject(1).toString()+"\n");
			 * System.out.println("\n2:"+json.getJSONObject(2).toString()+"\n");
			 */
			// System.out.println(json.get("id"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*GetModels model = new GetModels();

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
		}*/

	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		System.out.println("Shutting down!");
	}

	private static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}
}