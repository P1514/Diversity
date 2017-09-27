package general;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoadThreads {
	private static final Logger LOGGER = new Logging().create(LoadThreads.class.getName());

	class Tinsert implements Runnable {
		private Author a;
		private Opinion opinion;

		/**
		 * Instantiates a new tauthors.
		 *
		 * @param _a
		 *            the a
		 */
		public Tinsert(Opinion _op) {
			this.opinion = _op;
		}

		public void run() {
			Connection cnlocal = null;
			try {
				cnlocal = Settings.connlocal();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
			}
			String update = "INSERT INTO " + Settings.lotable + " "
					+ "Values (?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.lotable_reach + "=?,"
					+ Settings.lotable_influence + "=?," + Settings.lotable_comments
					+ "=?";

			try (PreparedStatement query1 = cnlocal.prepareStatement(update)) {
				query1.setLong(1, opinion.getID());
				query1.setDouble(2, opinion.getReach());
				query1.setDouble(3, opinion.getPolarity());
				query1.setDouble(4, opinion.getTotalInf());
				if (!Settings.JSON_use)
					query1.setString(5, opinion.getUID(false));
				else
					query1.setString(5, opinion.getUID(true));

				query1.setLong(6, opinion.getTime());
				query1.setLong(7, opinion.getPSS());
				query1.setLong(8, opinion.ncomments());
				query1.setLong(9, opinion.getProduct());				
				query1.setString(10, opinion.getSource());
				query1.setDouble(11, opinion.getReach());
				query1.setDouble(12, opinion.getTotalInf());
				query1.setLong(13, opinion.ncomments());
				try {
//					if (opinion.getID() == 8480)
//						System.out.println("INSERT OPINION: " + query1.toString());
					query1.executeUpdate();
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, Settings.err_unknown + "Retried", e);
					Thread.sleep((long) (Math.random() * 1000));
				}

				if (!Loader.first_load) {
					cnlocal.close();
					return;
				}
				for (Post post : opinion.getPosts().values()) {
					PreparedStatement query2 = null;

					try {
						String update1 = "INSERT INTO " + Settings.lptable + " " + "Values (?,?,?,?,?,?,?) "
					+"ON DUPLICATE KEY UPDATE "+Settings.lptable_views+"=?,"+Settings.lptable_likes+"=?";
						query2 = cnlocal.prepareStatement(update1);
						query2.setLong(1, post.getID());
						if (post.getPolarity() != -1) {
							query2.setDouble(2, post.getPolarity());
						} else {
							query2.setNull(2, java.sql.Types.DOUBLE);
						}
						query2.setString(3, post.getComment());
						query2.setLong(4, post.getLikes());
						query2.setLong(5, post.getViews());
						query2.setLong(6, opinion.getID());
						if (!Settings.JSON_use)
							query2.setString(7, post.getUID());
						else
							query2.setString(7, post.getUID());
						query2.setLong(8, post.getLikes());
						query2.setLong(9, post.getViews());
						while (true) {
							try {
								query2.executeUpdate();
							} catch (Exception e) {
								LOGGER.log(Level.SEVERE, Settings.err_unknown + "Retried", e);
								Thread.sleep((long) (Math.random() * 1000));
								continue;
							}
							break;
						}
						if (query2 != null)
							query2.close();
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
						continue;
					}
					try {
						if (query2 != null)
							query2.close();
					} catch (Exception e) {
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				cnlocal.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * The Class Tauthors.
	 */
	class Tauthors implements Runnable {
		private Author a;
		private int counter = 0;

		/**
		 * Instantiates a new tauthors.
		 *
		 * @param _a
		 *            the a
		 */
		public Tauthors(Author _a) {
			a = _a;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */

		public void run() {
			Connection cnlocal = null;
			LOGGER.log(Level.SEVERE, "started thread n�" + counter);
			try {
				cnlocal = Settings.connlocal();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
				return;
			}

			String insert = "INSERT INTO " + Settings.latable + " "
					+ "Values (?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + Settings.latable_influence + "=?,"
					+ Settings.latable_comments + "=?," + Settings.latable_likes + "=?," + Settings.latable_views
					+ "=?," + Settings.latable_posts + "=?";
			try (PreparedStatement query1 = cnlocal.prepareStatement(insert)) {
				query1.setString(1, a.getID());
				query1.setLong(2, a.getAge());
				query1.setString(3, a.getName());
				query1.setString(4, a.getGender());
				query1.setString(5, a.getLocation());
				query1.setDouble(6, a.getInfluence());
				query1.setLong(7, a.getComments());
				query1.setLong(8, a.getLikes());
				query1.setLong(9, a.getViews());
				query1.setLong(10, a.getPosts());
				query1.setDouble(11, a.getInfluence());
				query1.setLong(12, a.getComments());
				query1.setLong(13, a.getLikes());
				query1.setLong(14, a.getViews());
				query1.setLong(15, a.getPosts());
				query1.executeUpdate();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
				return;
			} finally {
				try {
					cnlocal.close();
				} catch (Exception e) {
					LOGGER.log(Level.INFO, "Nothing can be done here", e);
				}
			}

		}
	}

	/**
	 * The Class Topinions.
	 */
	class Topinions implements Runnable {
		private long id;
		private JSONObject obj = null;
		private long counter;

		/**
		 * Instantiates a new topinions referenced to local data.
		 *
		 * @param _id
		 *            the id of the opinion
		 */
		public Topinions(long _id) {
			id = _id;

		}

		/**
		 * Instantiates a new topinions referenced to remote.
		 *
		 * @param _obj
		 *            the json object from remote
		 */
		public Topinions(JSONObject _obj) {
			obj = _obj;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */

		private void load(ResultSet rs, boolean remote) throws SQLException {//fazer join com post table
			// //system.out.println(id);
			long postid = remote ? rs.getLong(Settings.rptable_postid) : rs.getLong(Settings.lptable_opinion);
			// //system.out.println(id);
			String user_id = remote ? rs.getString(Settings.rptable_userid) : rs.getString(Settings.lptable_authorid);
			long time = 0;
			double polarity = -1;
			if (remote) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date date = null;
				try {
					date = df.parse(rs.getString(Settings.rptable_date));
				} catch (ParseException e) {
					// system.out.print("Error Parsing Date from Local DB");
				}
				time = date.getTime();

			} else {
				polarity = rs.getDouble(Settings.lptable_polarity);
			}
			long likes = remote ? rs.getLong(Settings.rptable_likes) : rs.getLong(Settings.lptable_likes);
			long views = remote ? rs.getLong(Settings.rptable_views) : rs.getLong(Settings.lptable_views);

			String message = remote ? rs.getString(Settings.rptable_message) : rs.getString(Settings.lptable_message);
			String source = remote ? rs.getString(Settings.latable_source) : rs.getString(Settings.lotable_source);

			long product = Settings.JSON_use ? Settings.currentProduct : Data.identifyProduct(message);
			if (product == 0) {
				return;
			}
			Post _post = remote ? new Post(postid, source, user_id, time, likes, views, message)
					: new Post(postid, user_id, (long) 0, likes, views, message, polarity, "");
			if (!(Loader.users.contains(user_id))) {
				Loader.users.add(user_id);
			}

			Loader.opiniondb.put(postid, new Opinion(_post, Data.identifyPSSbyproduct(product), product, source));

		}

		public void run() {
			Connection cndata = null;
			Connection cnlocal;
			if (obj == null) {

				/// Change load order

				try {
					cnlocal = Settings.connlocal();
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
					return;
				}
				String query = ("SELECT sentimentanalysis.posts.*, sentimentanalysis.opinions.source FROM sentimentanalysis.posts left join sentimentanalysis.opinions on sentimentanalysis.posts.opinions_id=sentimentanalysis.opinions.id" + Settings.sqlwhere + "posts."+Settings.lptable_id
						+ " = " + id);
				try (Statement stmt = cnlocal.createStatement()) {
					try (ResultSet rs = stmt.executeQuery(query)) {
						if (rs.next()) {
							if (Loader.first_load)
								Loader.totalposts--;
							load(rs, false);
						}
					}
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
					try {
						cnlocal.close();
					} catch (Exception e1) {
						LOGGER.log(Level.SEVERE, Settings.err_unknown, e1);
					}
					return;
				}
				try {
					cnlocal.close();
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
					return;
				}

				// FInished load order

				try {
					cndata = Settings.conndata();
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
					return;
				}
				boolean remote = true;
				query = (Settings.sqlselectall + Settings.rptable + Settings.sqlwhere + Settings.rptable + "."
						+ Settings.rptable_postid + " = " + id);
				// system.out.println(query);
				try (Statement stmt = cndata.createStatement()) {
					try (ResultSet rs = stmt.executeQuery(query)) {
						if (!rs.next()) {
							remote = false;
						} else {
							load(rs, remote);
							cndata.close();
						}
					}
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, Settings.err_unknown, e);
					try {
						cndata.close();
					} catch (SQLException e1) {
						LOGGER.log(Level.INFO, Settings.err_unknown, e1);
					}
					return;
				}
			} else

			{
				try {
					cnlocal = Settings.connlocal();
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
					return;
				}
				Statement stmt = null;
				ResultSet rs = null;
				try {// TODO AQUI
					boolean remote = false;
					String query = (Settings.sqlselectall + Settings.lptable + Settings.sqlwhere + Settings.lptable_id
							+ " = " + id);
					stmt = cnlocal.createStatement();
					rs = stmt.executeQuery(query);
					if (!rs.next()) {
						remote = true;
						rs.close();
						stmt.close();
						cnlocal.close();
					} else {
						Loader.totalposts--;
					}
					Date date = new Date(Long.valueOf(obj.getString("postEpoch")));
					// Date date = new Date(0);
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					format.setTimeZone(TimeZone.getTimeZone("GMT"));
					String formatted = format.format(date);
					java.util.Date parsed = format.parse(formatted);

					// //system.out.println(formatted);
					long postid;
					if (obj.has("postId")) {
						postid = obj.getLong("postId");
					} else {
						postid = (long) (Math.random() * 200);
						obj.put("postId", postid);
					}
					// //system.out.println(id);
					String source = obj.getString(Settings.JSON_source);
					String user_id = obj.getString(Settings.JSON_userid);
					long time = parsed.getTime();
					if (!remote) {
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						date = null;
						try {
							date = (Date) df.parse(rs.getString(Settings.lptable_timestamp));
						} catch (ParseException e) {
							// system.out.print("Error Parsing Date from Local
							// DB");
						}
						time = date.getTime();

					}
					// //system.out.println("IM HERE");
					long likes = obj.has("mediaSpecificInfo") ? obj.has("likes") ? obj.getLong("likes") : 0 : 0;
					long views = obj.has("mediaSpecificInfo") ? obj.has("views") ? obj.getLong("views") : 0 : 0;
					String name = obj.has(Settings.JSON_fname) ? obj.getString(Settings.JSON_fname) + " " : "";
					name += obj.has(Settings.JSON_lname) ? obj.getString(Settings.JSON_lname) : "";
					long age = obj.has(Settings.JSON_age) && obj.getString(Settings.JSON_age) != "null"
							? obj.getLong(Settings.JSON_age) : 0;
					String gender = obj.has(Settings.JSON_gender) ? obj.getString(Settings.JSON_gender) : "";
					String location = obj.has(Settings.JSON_location) ? obj.getString(Settings.JSON_location) : "";
					String message = obj.getString("post");
					long product = Settings.JSON_use ? Settings.currentProduct : Data.identifyProduct(message);
					/*
					 * if (product == 0) { rs.close(); stmt.close();
					 * conlocal.close(); condata.close(); return; }
					 */
					Post _post = new Post(postid, source, user_id, time, likes, views, message);// TODO
																								// create
																								// constructor
																								// for
																								// the
																								// different
																								// type
																								// of
																								// media
																								// specific
																								// info

					Author author = new Author(user_id, source, name, age, gender, location);
					if (!(Loader.users2.contains(author))) {
						Loader.users2.add(author);
					}
					Loader.opiniondb.put(postid,
							new Opinion(_post, Data.identifyPSSbyproduct(product), product, source));
					// find
					// url
					// to
					// attack
					// here
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (cnlocal != null)
						cnlocal.close();
				} catch (SQLException | JSONException e) {
					// system.out.println("ERROR loading Opinions");
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (rs != null)
							rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (stmt != null)
							stmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (cnlocal != null)
							cnlocal.close();

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
			try {
				if (cndata != null)
					cndata.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * The Class Tposts.
	 */
	class Tposts implements Runnable {
		private long id;
		private Opinion _opin;
		private JSONObject obj = null;

		/**
		 * Instantiates a new tposts referenced to local data.
		 *
		 * @param _id
		 *            the id of the opinion
		 */
		public Tposts(long _id) {
			id = _id;
			_opin = Loader.opiniondb.containsKey(id) ? Loader.opiniondb.get(id) : null;

		}

		/**
		 * Instantiates a new tposts referenced to remote data.
		 *
		 * @param _obj
		 *            the json object from remote
		 * @throws JSONException
		 *             the JSON exception
		 */
		public Tposts(JSONObject _obj) throws JSONException {
			obj = _obj;
			_opin = Loader.opiniondb.containsKey(_obj.getLong(Settings.JSON_postid))
					? Loader.opiniondb.get(_obj.getLong(Settings.JSON_postid)) : null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (_opin == null)
				return;
			Statement stmt = null;
			ResultSet rs = null;
			Connection cndata = null;
			Connection cnlocal = null;
			if (obj == null) {
				try {
					// //system.out.println("HELLO1");
					cndata = Settings.conndata();
					String query = (Settings.sqlselectall + Settings.rptable + Settings.sqlwhere
							+ Settings.rptable_rpostid + " = " + id);
					stmt = cndata.createStatement();
					// //system.out.println(query);
					rs = stmt.executeQuery(query);
					if (rs.next()) {
						do {
							// //system.out.println("HELLO2");
							long postid = rs.getLong(Settings.rptable_postid);
							String user_id = rs.getString(Settings.rptable_userid);
							long time = 0;

							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							java.util.Date date = null;
							try {
								date = df.parse(rs.getString(Settings.rptable_date));
							} catch (ParseException e) {
								// system.out.print("Error Parsing Date from
								// Local DB");
							}
							time = date.getTime();

							long likes = rs.getLong(Settings.rptable_likes);
							long views = rs.getLong(Settings.rptable_views);
							String message = rs.getString(Settings.rptable_message);
							message=message.trim();
							if(message.length()<=1)continue;
							Post _post = new Post(postid, "", user_id, time, likes, views, message);
							if (!(Loader.users.contains(user_id))) {
								Loader.users.add(user_id);
							}
							_opin.addcomment(_post);
						} while (rs.next());
					}
					rs.close();
					stmt.close();
					cndata.close();
					cnlocal = Settings.connlocal();
					query = (Settings.sqlselectall + Settings.lptable
							+ " left JOIN  sentimentanalysis.authors ON authors.id=sentimentanalysis.posts.authors_id "
							+ Settings.sqlwhere + Settings.lptable_opinion + " = " + id);
					// system.out.println(query);
					stmt = cnlocal.createStatement();
					rs = stmt.executeQuery(query);
					if (rs.next()) {
						do {
							// //system.out.println("HELLO3");
							long postid = rs.getLong(Settings.lptable_id);
							String user_id = rs.getString(Settings.lptable_authorid);
							long likes = rs.getLong(Settings.lptable_likes);
							long views = rs.getLong(Settings.lptable_views);
							String message = rs.getString(Settings.lptable_message);
							double polarity = rs.getDouble(Settings.lptable_polarity);
							String source = rs.getString(Settings.latable_source);
							Post _post = new Post(postid, user_id, 0, likes, views, message, polarity, source);
							if (!(Loader.users.contains(user_id)))
								Loader.users.add(user_id);
							_opin.addcomment(_post);
						} while (rs.next());
					}
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (cndata != null)
						cndata.close();
					if (cnlocal != null)
						cnlocal.close();
					Loader.opiniondb.put(id, _opin);
				} catch (ClassNotFoundException e) {
					// system.out.println("ERROR loading Posts");
					e.printStackTrace();
				} catch (SQLException e) {
					//
					e.printStackTrace();
				} finally {
					try {
						if (rs != null)
							rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (stmt != null)
							stmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (cndata != null)
							cndata.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (cnlocal != null)
							cnlocal.close();

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			} else {
				try {

					if (obj.has(Settings.JSON_replies)) {

						// //system.out.println("HELLO2");
						JSONArray replies = obj.getJSONArray(Settings.JSON_replies);
						for (int index = 0; index < replies.length(); index++) {

							JSONObject reply;

							reply = replies.getJSONObject(index);

							Date date;
							if (reply.has(Settings.JSON_epoch)) {
								date = new Date(Long.valueOf(reply.getString(Settings.JSON_epoch)) * 1000L);
							} else {
								date = new Date(Long.valueOf(11111 * 1000L));
							}
							DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							format.setTimeZone(TimeZone.getTimeZone("GMT"));
							String formatted = format.format(date);
							java.util.Date parsed = format.parse(formatted);

							long postid = reply.getLong(Settings.JSON_postid);
							String user_id = reply.getString(Settings.JSON_userid);
							long time = parsed.getTime();
							long likes = reply.has("mediaSpecificInfo")
									? reply.has("likes") ? reply.getLong("likes") : 0 : 0;
							long views = reply.has("mediaSpecificInfo")
									? reply.has("views") ? reply.getLong("views") : 0 : 0;
							String message = reply.getString(Settings.JSON_message);
							
							String source = obj.getString(Settings.JSON_source);
							Post _post = new Post(postid, source, user_id, time, likes, views, message);
							String name = obj.has(Settings.JSON_fname) ? obj.getString(Settings.JSON_fname) + " " : "";
							name += obj.has(Settings.JSON_lname) ? obj.getString(Settings.JSON_lname) : "";
							long age = obj.has(Settings.JSON_age) && obj.getString(Settings.JSON_age) != "null"
									? obj.getLong(Settings.JSON_age) : 0;
							String gender = obj.has(Settings.JSON_gender) ? obj.getString(Settings.JSON_gender) : "";
							String location = obj.has(Settings.JSON_location) ? obj.getString(Settings.JSON_location)
									: "";
							Author author = new Author(user_id, source, name, age, gender, location);
							// if(Loader.) //se n�o existir na authors db2 tem
							// de ser criado
							if (!(Loader.users2.contains(author))) {
								Loader.users2.add(author);

							}
							_opin.addcomment(_post);
						}

					}

					// //system.out.println("HELLO");
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (rs != null)
							rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (stmt != null)
							stmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (cndata != null)
							cndata.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (cnlocal != null)
							cnlocal.close();

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		}

		/**
		 * Tmodels Multithreading for model
		 */
		/*
		 * class Tmodels implements Runnable { private Model model; private
		 * Connection conlocal;
		 * 
		 * public Tmodels() { }
		 */
		public void Tmodels() {
			// //system.out.println("HELLO1");
			Statement stmt = null;
			ResultSet rs = null;
			Connection cnlocal = null;
			try {
				cnlocal = Settings.connlocal();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			try {
				String query = (Settings.sqlselectall + Settings.lmtable);
				stmt = cnlocal.createStatement();
				// //system.out.println(query);
				rs = stmt.executeQuery(query);
				if (rs.next()) {
					do {
						Model model = new Model(rs.getLong(Settings.latable_id), rs.getLong(Settings.lmtable_update),
								rs.getLong(Settings.lmtable_creator), rs.getString(Settings.lmtable_name),
								rs.getString(Settings.lmtable_uri), rs.getLong(Settings.lmtable_pss),
								rs.getString(Settings.lmtable_age), rs.getString(Settings.lmtable_gender),
								rs.getString(Settings.lmtable_monitorfinal), rs.getBoolean(Settings.lmtable_archived),
								rs.getLong(Settings.lmtable_cdate), rs.getLong(Settings.lmtable_udate),
								rs.getLong(Settings.lmtable_designproject),
								rs.getBoolean(Settings.lmtable_add_mediawiki));
						Data.modeldb.put(model.getId(), model);
					} while (rs.next());
				}
				rs.close();
				stmt.close();
				cnlocal.close();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, Settings.err_dbconnect, e);
				return;
			} finally {
				try {
					if (rs != null)
						rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (stmt != null)
						stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if (cnlocal != null)
						cnlocal.close();

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

}
