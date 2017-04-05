package general;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.json.JSONArray;
import org.json.JSONException;

import com.mysql.jdbc.Connection;
import monitoring.Oversight;

/**
 * The Class Startup runs every time the server boots up.
 */
@WebListener
public class Startup implements ServletContextListener {
	
	private static final Logger LOGGER = new Logging().create(Startup.class.getName());

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		/*CleanDB clean = new CleanDB();
			try {
				clean.clean();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
		
		LOGGER.log(Level.INFO,"Starting up!");
		
		Oversight o = new Oversight(true);
		o.run();
		//new Oversight();
		/*Connection cnlocal = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			cnlocal = Settings.connlocal();
			String select = "Select * from "+Settings.gentable+" WHERE id=1";
			stmt=cnlocal.createStatement();
			rs = stmt.executeQuery(select);
			rs.next();
			if (rs.getInt("Version") != Settings.dbversion)
				rs.getLong("asdasasd");
			//System.out.println(clean.clean());
			if (Settings.JSON_use == false) {
				new Loader().load(null);
			} else {
				JSONArray json = new JSONArray(readUrl(Settings.JSON_uri));
				LOGGER.log(Level.INFO,json.toString());
				new Loader().load(json);

			}
			/*
			 * System.out.println("\n0:"+json.getJSONObject(0).toString()+"\n");
			 * System.out.println("\n1:"+json.getJSONObject(1).toString()+"\n");
			 * System.out.println("\n2:"+json.getJSONObject(2).toString()+"\n");
			 
			// System.out.println(json.get("id"));
		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE,"ERROR Database Outdated",e);
			
			//e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (cnlocal != null)
					cnlocal.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}/*

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

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		LOGGER.log(Level.INFO,"Shutting down!");
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