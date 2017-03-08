package monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.resource.cci.ResultSet;

import com.mysql.jdbc.Connection;

import general.Settings;

/**
 * The Class Monitor.
 */
public class Monitor {

	/**
	 * Update.
	 *
	 * @param uri
	 *            the uri from the remote API to get posts
	 */
	public static void update(String uri, long pss) {
		String[] urilists = uri.split(";");
		String account = "", source = "", url;
		url = "http://diversity.euprojects.net/socialfeedbackextraction/registerSFE?accounts[]=\"";
		for (int i = 0; i < urilists.length; i++) {
			source = urilists[i].split(",")[0];
			account = urilists[i].split(",")[1];
			url += account + "\"&type[]=\"" + source + "\"&";
		}
		url = url.substring(0, url.length() - 1);
		PreparedStatement stmt=null;
		Connection cnlocal=null;
		try {
			cnlocal = Settings.connlocal();
			String query = "INSERT INTO " + Settings.lutable + " (" + Settings.lutable_source + ","
					+ Settings.lutable_account + "," + Settings.lutable_pss + ") VALUES(?,?,?) ON DUPLICATE KEY UPDATE "
					+ Settings.lutable_source + "=?";
			stmt = cnlocal.prepareStatement(query);
			stmt.setString(1, source);
			stmt.setString(2, account);
			stmt.setLong(3, pss);
			stmt.setString(4, source);

			stmt.execute();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(cnlocal!=null)cnlocal.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * URL registeraccount; try { registeraccount = new URL(url);
		 * 
		 * BufferedReader in = new BufferedReader(new
		 * InputStreamReader(registeraccount.openStream()));
		 * 
		 * String status; while ((status = in.readLine()) != null)
		 * System.out.println(status); in.close();
		 * 
		 * } catch (MalformedURLException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

	}

	
	public static void load(String uri, long pss) {//TODO
		
	}
	
	/**
	 * Delete.
	 *
	 * @param uri
	 *            the uri
	 */
	public static void delete(String uri) {
		// TODO By Francisco Silva

	}

}
