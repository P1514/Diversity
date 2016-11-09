package monitoring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import com.mysql.jdbc.Connection;

import extraction.GetReach;
import extraction.Globalsentiment;
import general.Data;
import general.Settings;

public class Overwatch extends TimerTask {

	private Connection cnlocal;
	private ArrayList<String> sourcelist = new ArrayList<String>();
	private Data dat = new Data();
	private ArrayList<String> updatelist = new ArrayList<String>();
	private String uri = "http://diversity.euprojects.net/";
	private HashMap<String, url> requesturl = new HashMap<String, url>();
	private Calendar now = Calendar.getInstance();
	private boolean local = Settings.JSON_use;

	public Overwatch() {
		Timer timer = new Timer();
		Calendar date = Calendar.getInstance();
		date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		// timer.scheduleAtFixedRate(this, date.getTime(),
		// 24*60*60*1000);//h/d*m/d*s/m*ms/s = ms/d (runs once a day)
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 0/*1*/);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 1/*0*/);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.AM_PM, Calendar.AM);
		
		timer.scheduleAtFixedRate(this, c.getTime(), 24*60*60*1000);
	}
	public Overwatch(boolean a) {
		Calendar date = Calendar.getInstance();
		date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		// timer.scheduleAtFixedRate(this, date.getTime(),
		// 24*60*60*1000);//h/d*m/d*s/m*ms/s = ms/d (runs once a day)
	}

	@Override
	public void run() {
		
		// TODO Introduce here loading PSS's and Products and save them on static hashmaps on Data Class
		sourcelist = new ArrayList<String>();
		updatelist = new ArrayList<String>();
		requesturl = new HashMap<String, url>();
		now = Calendar.getInstance();

		// TODO This is the method that run at 00:00 each 24h to update
		// everything
		String getsources = "Select * from " + Settings.lutable + " where "+Settings.lutable_source+"=? AND "+Settings.lutable_nextupdate+"<?";
		String getpss = "Select distinct( " + Settings.lutable_source + ") from " + Settings.lutable;

		if(local==true){
		dbconnect();
		try {
			PreparedStatement query = cnlocal.prepareStatement(getpss);
			ResultSet rs;
			rs = query.executeQuery();
			while (rs.next()) {
				sourcelist.add(rs.getString(Settings.lutable_source));
			}
			for (String a : sourcelist) {
				updatelist = new ArrayList<String>();
				requesturl = new HashMap<String, url>();

				query = cnlocal.prepareStatement(getsources);
				query.setString(1, a);
				query.setLong(2, now.getTimeInMillis());
				rs = query.executeQuery();

				Calendar c = Calendar.getInstance();
				while (rs.next()) {
					c.setTimeInMillis(Long.valueOf(rs.getString(Settings.lutable_nextupdate)));
					if (now.after(c)) {
						updatelist.add(rs.getString(Settings.lutable_account) + ";."
								+ rs.getString(Settings.lutable_lastupdate) + ";."
								+ rs.getString(Settings.lutable_pss));
					}
				}

				for (String k : updatelist) {
					String[] split = k.split(";.");

					url local = requesturl.containsKey(split[2]) ? requesturl.get(split[2]) : new url();
					local.accounts += "&accounts[]=\"" + split[0] + "\"";
					local.epochs += "&epochFrom[]=" + split[1] + "&epochTo[]=" + now.getTimeInMillis();
					requesturl.put(split[2], local);
				}
				;
				requesturl.forEach((k, v) -> {
					String request = uri + a + "/getPosts/" + v.epochs.replaceFirst("&", "?") + v.accounts + "&pssId=\""
							+ k + "\"";
					System.out.println(request);
					try {
						// dat.load(new JSONArray(readUrl(request)));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("ERROR ON JSON OVERWATCH");
					}
					String update = "Update " + Settings.lutable + " SET " + Settings.lutable_lastupdate + "=?,"
							+ Settings.lutable_nextupdate + "=? where (" + Settings.lutable_pss + "=? AND "
							+ Settings.lutable_source + "=?) AND (";
					try {
						/*
						 * TODO Missing check standard update time update time, currently doing
						 * update each day Calendar cal=now; for (Model m :
						 * Data.modeldb.values()) { if (m.getPSS().equals(k)) {
						 * cal.add(Calendar.DAY_OF_MONTH, m.getFrequency());
						 */
						String[] account = v.accounts.replaceAll("\"", "").replaceFirst("&", "").split("&");
						for (int i=0; i<account.length; i++) {
							update+=" "+Settings.lutable_account+"=? OR";
						}
						update=update.substring(0, update.length()-3);
						update+=")";
						Calendar cc = (Calendar) now.clone();
						cc.add(Calendar.DAY_OF_MONTH, 1);
						PreparedStatement query1 = cnlocal.prepareStatement(update);
						query1.setLong(1, now.getTimeInMillis());
						query1.setLong(2, cc.getTimeInMillis());
						query1.setString(3, k);
						query1.setString(4, a);
						int i=5;
						for (String acc : account) {
							query1.setString(i++, acc.split("=")[1]);
						}
						System.out.println(query1);
						query1.execute();

						/*
						 * } }
						 */
					} catch (Exception e) {
						System.out.println("ERROR ON SQL OVERWATCH");
						e.printStackTrace();
					}

				});
			}
			// TODO missing uodate DB
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERROR ON OVERWATCH");
		}
		}else{
			try {
				dat.load();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Globalsentiment gs = new Globalsentiment();
		GetReach gr = new GetReach();
		try {
			gs.calc_TOPreachglobalsentiment(1, null, null, gr.getTOPReach(5));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class url {
		public String accounts = "";
		public String epochs = "";
	}

	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
