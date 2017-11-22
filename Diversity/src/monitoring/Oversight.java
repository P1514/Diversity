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
public class Oversight extends TimerTask {

	private ArrayList<String> sourcelist = new ArrayList<String>();
	private Data dat = new Data();
	private HashMap<String, update> updatelist = new HashMap<String, update>();
	private String uri = "http://diversity.euprojects.net/";
	private HashMap<String, String> requestAccount = new HashMap<String, String>();
	private HashMap<String, url> urlAccount = new HashMap<String, url>();
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
	public Oversight(boolean a) {
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
		requestAccount = new HashMap<String, String>();
		urlAccount = new HashMap<String, url>();
		now = Calendar.getInstance();

		// TODO This is the method that run at 00:00 each 24h to update
		// everything
		String getsources = "Select " + Settings.lmtable_uri + "," + Settings.lmtable_pss + ","
				+ Settings.lmtable_add_mediawiki + " from " + Settings.lmtable + " where " + Settings.lmtable_udate
				+ "<=? AND " + Settings.lmtable_uri + " LIKE ?";
		String getpss = "Select distinct( " + Settings.lutable_source + ")," + Settings.lutable_lastupdate + " from "
				+ Settings.lutable;

		if (local == true) {
			try {
				try (Connection cnlocal = Settings.connlocal();
						PreparedStatement query = cnlocal.prepareStatement(getpss)) {

					// System.out.println("QUERY: " + query.toString());
					try (ResultSet rs = query.executeQuery()) {

						while (rs.next()) {
							sourcelist.add(rs.getString(Settings.lutable_source) + ";;;"
									+ rs.getString(Settings.lutable_lastupdate));
						}
					}
				} catch (ClassNotFoundException e2) {
					LOGGER.log(Level.WARNING,"Class:Oversight Error 1");
				}
				for (String a : sourcelist) {

					// System.out.println("Source: " + a);

					updatelist = new HashMap<String, update>();
					try (Connection cnlocal = Settings.connlocal();
							PreparedStatement query = cnlocal.prepareStatement(getsources)) {
						query.setLong(1, now.getTimeInMillis());
						String source = a.split(";;;")[0];
						String date = a.split(";;;")[1];
						query.setString(2, "%" + source + ",%");

						try (ResultSet rs = query.executeQuery()) {

							// System.out.println("query: " + query.toString());

							Calendar c = Calendar.getInstance();
							while (rs.next()) {
								c.setTimeInMillis(Long.valueOf(date));
								if (now.after(c)) {
									String[] uri = rs.getString(Settings.lmtable_uri).split(";");
									for (String b : uri) {
										if (updatelist.containsKey(b.split(",")[1])) {
											update tmp = updatelist.get(b.split(",")[1]);
											if (tmp.date > Long.valueOf(date))
												tmp.date = Long.valueOf(date);
										} else {
											update tmp = new update();
											tmp.account = b.split(",")[1];
											tmp.date = Long.valueOf(date);
											tmp.pss = Long.valueOf(rs.getString(Settings.lmtable_pss));
											updatelist.put(tmp.account, tmp);
										}
									}
								}
								/*
								 * if (rs.getBoolean(Settings.lmtable_add_mediawiki) == true) { update tmp = new
								 * update(); tmp.account = "mediawiki"; tmp.date = Long.valueOf(date); tmp.pss =
								 * Long.valueOf(rs.getString(Settings.lmtable_pss)); updatelist.put(tmp.account,
								 * tmp); }
								 */
							}
						}
					} catch (ClassNotFoundException e1) {
						LOGGER.log(Level.WARNING,"Class:Oversight Error 2");
					}

					for (update d : updatelist.values()) {
						url local = new url(d.account, d.date);
						requestAccount.put(d.pss.toString(), d.account);
						urlAccount.put(d.account, local);
						// break;// TO TEST
					}

					requestAccount.forEach((k, v) -> {
						Settings.currentPss = Long.parseLong(k);
						// FIX Media Wiki
//						v.accounts += "&accounts[]=mediawiki";
//						v.epochs += "&epochsFrom[]=1&epochsTo[]=1507376292000";
						url currentUrl = urlAccount.get(v);
						ArrayList<Long> products = Data.getpss(Settings.currentPss).get_products();
						for (Long prodid : products) {
							// String request = uri +
							// a.split(";;;")[0] +
							// "/intelligent-search/getFeedback"
							// + v.epochs.replaceFirst("&", "?") + v.accounts +
							// "&pssId=\"" + k + "\"";
							if(!Data.getProduct(prodid).getFinal())
								continue;
							String request = "";
						
							try {
								request = Settings.JSON_uri + currentUrl.genEpochs().replaceFirst("&", "?") + currentUrl.genAccounts()
										+ "&pssId=" + URLEncoder.encode("" +k,"UTF-8") + "&pssName=" + URLEncoder.encode(Data.getpss(Long.parseLong(k)).getName(),"UTF-8")
										+ (Data.getProduct(prodid).getFinal() ? "&finalProductId=" + URLEncoder.encode(""+prodid,"UTF-8")
												+ "&finalProductName=" + URLEncoder.encode(Data.getProduct(prodid).get_Name(),"UTF-8") : "");
							} catch (NumberFormatException | UnsupportedEncodingException e1) {
								LOGGER.log(Level.SEVERE, "Unsupported encoding exception");
							} 
							// request = Settings.JSON_uri;
							// System.out.println("REQUEST:" + request);
						
							try {
								// System.out.println("TESTE: " +
								// readUrl(request));
								// request = request.replaceAll(" ", "%20");
								Settings.currentProduct = prodid;
								LOGGER.log(Level.INFO, "URL TO REQUEST" + request);
								(new Loader()).load(new JSONArray(readUrl(request)));
								currentUrl.setDate(Calendar.getInstance().getTimeInMillis());
								urlAccount.put(v, currentUrl);
							} catch (Exception e) {
								LOGGER.log(Level.SEVERE, "ERROR ON JSON OVERWATCH");
								continue;
							}
							String update = "Update " + Settings.lutable + " SET " + Settings.lutable_lastupdate
									+ "=? where (" + Settings.lutable_pss + "=? AND " + Settings.lutable_source
									+ "=?) AND (";
							try {
								/*
								 * TODO Missing check standard update time update time, currently doing update
								 * each day Calendar cal=now; for (Model m : Data.modeldb.values()) { if
								 * (m.getPSS().equals(k)) { cal.add(Calendar.DAY_OF_MONTH, m.getFrequency());
								 */
								String[] account = currentUrl.genAccounts().replaceAll("\"", "").replaceFirst("&", "").split("&");
								for (int i = 0; i < account.length; i++) {
									update += " " + Settings.lutable_account + "=? OR";
								}
								update = update.substring(0, update.length() - 3);
								update += ")";
								Calendar cc = (Calendar) now.clone();
								cc.add(Calendar.DAY_OF_MONTH, 1);
								try (Connection cnlocal = Settings.connlocal();
										PreparedStatement query1 = cnlocal.prepareStatement(update)) {
									query1.setLong(1, now.getTimeInMillis());
									query1.setString(2, k);
									query1.setString(3, a.split(";;;")[0]);
									int i = 4;
									for (String acc : account) {
										query1.setString(i++, acc.split("=")[1]);
									}
									// System.out.println(query1);
									query1.execute();

								}
								/*
								 * } }
								 */
							} catch (Exception e) {
								LOGGER.log(Level.SEVERE, "ERROR ON JSON OVERWATCH", e);
							}
						}
					});

					// break;// TO TEST
				}
				// TODO missing uodate DB
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "ERROR ON JSON OVERWATCH", e);
			}

		} else {
			try {
				(new Loader()).loadinit();
			} catch (JSONException e) {
				LOGGER.log(Level.WARNING,"Class:Oversight Error 3");
			}
		}

		Globalsentiment gs = new Globalsentiment();
		GetReach gr = new GetReach();
		try {
			gs.globalsentiment(null, null, gr.getTOPReach(5));
		} catch (JSONException e) {
			LOGGER.log(Level.WARNING,"Class:Oversight Error 4");
		}

		Server.isloading = false;
	}

	private class url {
		public String account = "";
		public Long date;
		
		public url(String _account, Long _date) {
			account= _account;
			date=_date;
		}
		
		public String getAccount() {
			return account;
		}
		
		public void setDate(Long date) {
			this.date = date;
		}
		
		public String genAccounts() {
			try {
				return "&accounts[]="
						+ URLEncoder.encode(account, "UTF-8");
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
