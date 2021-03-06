package monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import extraction.Globalsentiment;

import java.sql.Connection;

import general.Data;
import general.Product;
import general.PSS;
import general.Logging;
import general.Settings;

/**
 * The Class Monitor.
 */
public class Monitor {
	private static final Logger LOGGER = new Logging().create(Monitor.class.getName());
	String error = "error";

	/**
	 * Update.
	 *
	 * @param uri
	 *            the uri from the remote API to get posts
	 */
	public static void update(String uri, long pss) {
		String[] urilists = uri.split(";");
		String account = "", source = "", url="", finalProductId = "", finalProductName = "";
		PSS pssInstance = Data.getpss(pss);
		String pssName = "&pssName=" + pssInstance.getName();
		ArrayList<Long> products = pssInstance.get_products();
		Product productInstance = null;
		for (int i = 0; i < products.size(); i++) {
			productInstance = Data.getProduct((long) products.get(i));
			if (productInstance.getFinal()) {
				finalProductId = "&finalProductId=" + String.valueOf(productInstance.get_Id());
				finalProductName = "&finalProductName=" + productInstance.get_Name();

			}
		}
	
		for (int i = 0; i < urilists.length; i++) {
			url = Settings.register_uri + "?accounts[]=";
			source = urilists[i].split(",")[0];
			account = urilists[i].split(",")[1];

			try {
				url += URLEncoder.encode(account, "UTF-8") + "&type[]=" + URLEncoder.encode(source, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				LOGGER.log(Level.WARNING, "Unsupported encoding exception");
			}
		}

		// url = url.substring(0, url.length() - 1);
		// url += pssName + finalProductId + finalProductName;
		// System.out.println(url);
		try {
			//System.out.println(url);
			Oversight.readUrl(url);
		} catch (Exception e1) {
			LOGGER.log(Level.WARNING,"Class:Monitor Error 1");
		}
		
		PreparedStatement stmt = null;
		Connection cnlocal = null;
		
		try {
			for (int i = 0; i < urilists.length; i++) {
				source = urilists[i].split(",")[0];
				account = urilists[i].split(",")[1];
			cnlocal = Settings.connlocal();
			String query = "INSERT INTO " + Settings.lutable + " (" + Settings.lutable_source + ","
					+ Settings.lutable_account + "," + Settings.lutable_pss + "," + Settings.lutable_lastupdate
					+ ") VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.lutable_source + "=?";
			stmt = cnlocal.prepareStatement(query);
			stmt.setString(1, source);
			stmt.setString(2, account);
			stmt.setLong(3, pss);
			stmt.setLong(4, (long) 9466848 * 100000);
			stmt.setString(5, source);
			// System.out.println(query);

			stmt.execute();
			}
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.WARNING,"Class:Monitor Error 2");
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING,"Class:Monitor Error 3");
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING,"Class:Monitor Error 4");
			}
			try {
				if (cnlocal != null)
					cnlocal.close();
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING,"Class:Monitor Error 5");
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
		 * } catch (MalformedURLException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 */

	}
	
	public static boolean updateSource(String source, String account, long timestamp, long pss) {
		String query = "UPDATE " + Settings.lutable + " SET " + Settings.lutable_lastupdate + "=? WHERE "
				+ Settings.lutable_source + "=? AND " + Settings.lutable_pss + "=? AND " + Settings.lutable_account
				+ "=?";
		try(Connection cnlocal = Settings.connlocal();
				PreparedStatement stmt = cnlocal.prepareStatement(query)) {
			stmt.setLong(1, timestamp);
			stmt.setString(2, source);
			stmt.setLong(3, pss);
			stmt.setString(4, account);
			stmt.execute();
			return true;
		} catch (ClassNotFoundException | SQLException s) {
			Logger.getLogger(Monitor.class.getName()).log(Level.WARNING, "Could not Update Sources Table", s);
			return false;
		}

		
	}
	

	public static void load(String uri, long pss) {

		// Objetos co info sources

		// String uri = Settings.JSON_uri;

	}

	/**
	 * Delete.
	 *
	 * @param uri
	 *            the uri
	 */
	public static void delete(String uri) {
		String[] urilist = uri.split("(?<=;)");
		int count = 1;

		for (int i = 0; i < urilist.length; i++) {

			String insert = new String("SELECT COUNT(id) FROM models WHERE archived=0 and uri=?;");
			try (Connection cnlocal = Settings.connlocal();
					PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
				query1.setString(1, urilist[i]);
				// System.out.println(query1.toString());
				try(ResultSet rs = query1.executeQuery()){
					rs.next();
				count = rs.getInt(1);
				}

			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "error", e);
			}

			// System.out.println(count);
			if (count > 0) {
				LOGGER.log(Level.INFO, "Source not deleted");
			} else {
				LOGGER.log(Level.INFO, "Source deleted");
				String delete = new String("delete FROM sentimentanalysis.sources where source=? and account=?;");
				try (Connection cnlocal = Settings.connlocal();
						PreparedStatement query1 = cnlocal.prepareStatement(delete)) {
					query1.setString(1, urilist[i].split(",")[0]);
					query1.setString(1, urilist[i].split(",")[1]);
					System.out.println(query1.toString());
					query1.execute();
						
					

				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "error", e);
				}	
			}
		}

	}

}
