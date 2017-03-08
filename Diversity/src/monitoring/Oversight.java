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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import com.mysql.jdbc.Connection;

import extraction.GetReach;
import extraction.Globalsentiment;
import general.Data;
import general.Logging;
import general.Server;
import general.Settings;

/**
 * The Class Oversight.
 */
public class Oversight extends TimerTask {

	private Connection cnlocal;
	private ArrayList<String> sourcelist = new ArrayList<String>();
	private Data dat = new Data();
	private HashMap<String,update> updatelist = new HashMap<String,update>();
	private String uri = "http://diversity.euprojects.net/";
	private HashMap<String, url> requesturl = new HashMap<String, url>();
	private Calendar now = Calendar.getInstance();
	private boolean local = Settings.JSON_use;
	private static final Logger LOGGER = new Logging().create(Oversight.class.getName());


	/**
	 * Instantiates a new oversight.
	 */
	public Oversight() {
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
	
	/**
	 * Instantiates a new oversight one run only.
	 *
	 * @param a the a
	 */
	public Oversight(boolean a) {
		local = false;
		Calendar date = Calendar.getInstance();
		date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		// timer.scheduleAtFixedRate(this, date.getTime(),
		// 24*60*60*1000);//h/d*m/d*s/m*ms/s = ms/d (runs once a day)
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		
		// TODO Introduce here loading PSS's and Products and save them on static hashmaps on Data Class
		sourcelist = new ArrayList<String>();
		updatelist = new HashMap<String,update>();
		requesturl = new HashMap<String, url>();
		now = Calendar.getInstance();

		// TODO This is the method that run at 00:00 each 24h to update
		// everything
		String getsources = "Select "+Settings.lmtable_uri+","+Settings.lmtable_pss+" from " + Settings.lmtable + " where "+Settings.lmtable_udate+"<=? AND "+Settings.lmtable_uri+" LIKE ?";
		String getpss = "Select distinct( " + Settings.lutable_source + "),"+Settings.lutable_lastupdate+" from " + Settings.lutable;

		if(local==true){
		dbconnect();
		try {
			PreparedStatement query = cnlocal.prepareStatement(getpss);
			ResultSet rs;
			rs = query.executeQuery();
			while (rs.next()) {
				sourcelist.add(rs.getString(Settings.lutable_source)+";;;"+rs.getString(Settings.lutable_lastupdate));
			}
			for (String a : sourcelist) {
				updatelist = new HashMap<String,update>();
				requesturl = new HashMap<String, url>();

				query = cnlocal.prepareStatement(getsources);
				query.setLong(1, now.getTimeInMillis());
				String source = a.split(";;;")[0];
				String date = a.split(";;;")[1];
				query.setString(2, "%"+source+",%");
				rs = query.executeQuery();

				Calendar c = Calendar.getInstance();
				while (rs.next()) {
					c.setTimeInMillis(Long.valueOf(date));
					if (now.after(c)) {
						String[] uri= rs.getString(Settings.lmtable_uri).split(";");
						for(String b : uri){
							if(updatelist.containsKey(b.split(",")[1])){
								update tmp = updatelist.get(b.split(",")[1]);
								if (tmp.date>Long.valueOf(date))tmp.date=Long.valueOf(date);
							}else{
								update tmp = new update();
								tmp.account=b.split(",")[1];
								tmp.date=Long.valueOf(date);
								tmp.pss=Long.valueOf(rs.getString(Settings.lmtable_pss));
								updatelist.put(tmp.account,tmp);
							}
						}
					}
				}

				for(update d : updatelist.values()) {
					url local = requesturl.containsKey(d.pss.toString()) ? requesturl.get(d.pss.toString()) : new url();
					local.accounts += "&accounts[]=\"" + d.account + "\"";
					local.epochs += "&epochFrom[]=" + d.date + "&epochTo[]=" + now.getTimeInMillis();
					requesturl.put(d.pss.toString(), local);
				};
				requesturl.forEach((k, v) -> {
					//String request = uri + a.split(";;;")[0] + "/getPosts/" + v.epochs.replaceFirst("&", "?") + v.accounts + "&pssId=\"" + k + "\"";
					String request = Settings.JSON_uri;
					//System.out.println(request+"/n");
					try {
						//System.out.println(readUrl(request));					
						dat.load(new JSONArray(readUrl(request)));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LOGGER.log(Level.SEVERE, "ERROR ON JSON OVERWATCH");
					}
					String update = "Update " + Settings.lutable + " SET " + Settings.lutable_lastupdate + "=? where (" + Settings.lutable_pss + "=? AND "
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
						query1.setString(2, k);
						query1.setString(3, a.split(";;;")[0]);
						int i=4;
						for (String acc : account) {
							query1.setString(i++, acc.split("=")[1]);
						}
						//System.out.println(query1);
						query1.execute();

						/*
						 * } }
						 */
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "ERROR ON JSON OVERWATCH",e);

						e.printStackTrace();
					}

				});
			}
			// TODO missing uodate DB
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR ON JSON OVERWATCH",e);
		}
		}else{
			try {
				dat.load(null);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Globalsentiment gs = new Globalsentiment();
		GetReach gr = new GetReach();
		try {
			gs.globalsentiment(null, null, gr.getTOPReach(5));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class url {
		public String accounts = "";
		public String epochs = "";
	}
	
	private class update {
		public String account;
		public Long date;
		public Long pss;
	}

	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			//System.out.println("URL:"+url.toString());
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
