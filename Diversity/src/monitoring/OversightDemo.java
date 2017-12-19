package monitoring;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
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

import java.sql.Connection;

import extraction.GetReach;
import extraction.Globalsentiment;
import general.Data;
import general.Loader;
import general.Logging;
import general.Server;
import general.Settings;

/**
 * The Class Oversight.
 */
public class OversightDemo extends TimerTask {

	private ArrayList<String> sourcelist = new ArrayList<String>();
	private Data dat = new Data();
	private HashMap<String, update> updatelist = new HashMap<String, update>();
	private String uri = "http://diversity.euprojects.net/";
	private HashMap<String, ArrayList<String>> requestAccount = new HashMap<String, ArrayList<String>>();
	private HashMap<String, url> urlAccount = new HashMap<String, url>();
	private Calendar now = Calendar.getInstance();
	private boolean local = Settings.JSON_use;
	private static final Logger LOGGER = new Logging().create(Oversight.class.getName());

	/**
	 * Instantiates a new oversight.
	 */
	public OversightDemo() {
		Timer timer = new Timer();
		Calendar date = Calendar.getInstance();
		date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		// timer.scheduleAtFixedRate(this, date.getTime(),
		// 24*60*60*1000);//h/d*m/d*s/m*ms/s = ms/d (runs once a day)
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 0/* 1 */);
		/*
		 * c.set(Calendar.HOUR, 0); c.set(Calendar.MINUTE, 1); c.set(Calendar.SECOND,
		 * 0); c.set(Calendar.AM_PM, Calendar.AM);
		 */
		c.add(Calendar.SECOND, 15);

		timer.scheduleAtFixedRate(this, c.getTime(), 24 * 60 * 60 * (long) 1000);
	}

	/**
	 * Instantiates a new oversight one run only.
	 *
	 * @param a
	 *            the a
	 */
	public OversightDemo(boolean a) {
		local = false;
		Calendar date = Calendar.getInstance();
		date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		// timer.scheduleAtFixedRate(this, date.getTime(),
		// 24*60*60*1000);//h/d*m/d*s/m*ms/s = ms/d (runs once a day)
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		Server.isloading = true;
		// TODO Introduce here loading PSS's and Products and save them on
		// static hashmaps on Data Class
		sourcelist = new ArrayList<String>();
		updatelist = new HashMap<String, update>();
		requestAccount = new HashMap<String, ArrayList<String>>();
		urlAccount = new HashMap<String, url>();
		now = Calendar.getInstance();

		// TODO This is the method that run at 00:00 each 24h to update
		// everything
		String getsources = "SELECT * FROM sentimentanalysis.sources;";

		try {
			try (Connection cnlocal = Settings.connlocal();
					PreparedStatement query = cnlocal.prepareStatement(getsources)) {

				// System.out.println("QUERY: " + query.toString());
				try (ResultSet rs = query.executeQuery()) {

					while (rs.next()) {
						url local = new url(rs.getString("account"), rs.getLong("last_updated"), rs.getString("source"),
								rs.getLong("pss"));
						urlAccount.put(rs.getString("account"), local);

					}
				}
			} catch (ClassNotFoundException e2) {
				LOGGER.log(Level.WARNING, "Class:Oversight Error 1");
			}

			urlAccount.forEach((k, v) -> {
				Settings.currentPss = v.getPSS();
					String request = "";

					try {
						request = Settings.JSON_uri;
						boolean firsttime = true;
							url currentUrl = urlAccount.get(k);
							if (firsttime) {
								request += currentUrl.genEpochs().replaceFirst("&", "?") + currentUrl.genAccounts()
										+ "&pssId=" + URLEncoder.encode("" + k, "UTF-8") + "&pssName="
										+ URLEncoder.encode(Data.getpss(Long.parseLong(k)).getName(), "UTF-8");
								firsttime = false;
							} else {
								request += currentUrl.genEpochs() + currentUrl.genAccounts() + "&pssId="
										+ URLEncoder.encode("" + k, "UTF-8") + "&pssName="
										+ URLEncoder.encode(Data.getpss(Long.parseLong(k)).getName(), "UTF-8");
							}
						

					} catch (NumberFormatException | UnsupportedEncodingException e1) {
						LOGGER.log(Level.SEVERE, "Unsupported encoding exception");
					}

					try {

						Calendar updateTime = Calendar.getInstance();

						LOGGER.log(Level.INFO, "URL TO REQUEST" + request);
						(new Loader()).load(new JSONArray(readUrl(request)));
							v.setDate(updateTime.getTimeInMillis());
							urlAccount.put(k, v);
							Monitor.updateSource(v.getSource(), v.account,
									updateTime.getTimeInMillis(), Settings.currentPss);
						
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "ERROR ON JSON OVERWATCH");
						e.printStackTrace();
					}

			});
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "ERROR ON JSON OVERWATCH", e);
		}

		Globalsentiment gs = new Globalsentiment();
		GetReach gr = new GetReach();
		try {
			gs.globalsentiment(null, null, gr.getTOPReach(5),"default");
		} catch (JSONException e) {
			LOGGER.log(Level.WARNING, "Class:Oversight Error 4");
		}

		Server.isloading = false;
	}

	private class url {
		private String account;
		private Long date;
		private String source;
		private Long pss;

		public url(String _account, Long _date, String _source, Long _pss) {
			account = _account;
			date = _date;
			source = _source;
			pss = _pss;
		}
		
		public Long getPSS() {
			return pss;
		}

		public String getSource() {
			return source;
		}

		public void setDate(Long date) {
			this.date = date;
		}

		public String genAccounts() {
			try {
				return "&accounts[]=" + URLEncoder.encode(account, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.log(Level.INFO, "ERROR ENCONDING URL - Trying Unencoded");
				return "&accounts[]=" + account.replace(" ", "%20");
			}
		}

		public String genEpochs() {
			try {
				return "&epochsFrom[]=" + URLEncoder.encode(date + "", "UTF-8") + "&epochsTo[]="
						+ URLEncoder.encode(now.getTimeInMillis() + "", "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.log(Level.INFO, "ERROR ENCONDING URL - Trying Unencoded");
				return "&epochsFrom[]=" + date + "&epochsTo[]=" + now.getTimeInMillis();
			}
		}
	}

	private class update {
		public String account;
		public Long date;
		public Long pss;
		public String source;
	}

	public static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			// System.out.println("URL:" + url.toString());
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
