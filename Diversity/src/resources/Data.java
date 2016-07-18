package resources;

import resources.Settings;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Data {
	private HashMap<Integer, Author> authordb = new HashMap<Integer, Author>();
	private HashMap<Integer, Opinion> opiniondb = new HashMap<Integer, Opinion>();
	private int totalposts;
	private int totalviews;
	private int totalcomments;
	private int totallikes;

	Settings dbc = new Settings();
	Connection cndata = null;
	Connection cnlocal = null;

	public Data() {
		this.totalcomments = 0;
		this.totallikes = 0;
		this.totalposts = 0;
		this.totalviews = 0;
	}

	public void load() throws SQLException {

		try {
			cndata = dbc.conndata();
			cnlocal = dbc.connlocal();
		} catch (ClassNotFoundException | SQLException e4) {
			e4.printStackTrace();
		}

		ResultSet rs = null;
		//Load PSS
		
		
		
		
		//Load Posts
		String query = ("Select * from " + dbc.posttn + " Where " + dbc.ptime + " < \'" + dbc.LastUpdated + "\'");
		System.out.println(query);
		dbc.setLastUpdated();
		Statement stmt = cndata.createStatement();
		rs = stmt.executeQuery(query);

		List<Integer> users = new ArrayList<Integer>();
		while (rs.next()) {

			int post_id = rs.getInt(dbc.rpost_id);
			int id = rs.getInt(dbc.post_id);
			int user_id = rs.getInt(dbc.puser_id);
			Date time = rs.getDate(dbc.pdate);
			int likes = rs.getInt(dbc.plikes);
			int views = rs.getInt(dbc.pviews);
			String message = rs.getString(dbc.pmessage);
			Post _post = new Post(id, user_id, time, likes, views, message);
			if (!(users.contains(user_id))) {
				users.add(user_id);
			}
			if (post_id == 0) {
				
				String[] words = message.split("[^\\w'-]+");

				PSS pss = new PSS();
				int tag = 0;
				for (int i = 0; i < words.length; i++) {

					String currentWord = words[i];

					if (pss.tagexists(currentWord)) {
						tag = pss.getTag(currentWord);
					}
				}
				
				opiniondb.put(id, new Opinion(_post, tag));
			} else {
				Opinion _opin = opiniondb.get(post_id);
				_opin.addcomment(_post);
				opiniondb.put(post_id, _opin);
			}
		}
		rs = null;
		String querycond = users.toString();
		querycond = querycond.replaceAll("\\[", "(").replaceAll("\\]", "\\)");
		//Load users
		query = ("Select * from " + dbc.usertn + " where " + dbc.user_id + " in " + querycond);
		// System.out.println(query);
		stmt = cndata.createStatement();
		rs = stmt.executeQuery(query);
		while (rs.next()){
			if (authordb.containsKey(rs.getInt(dbc.user_id))) {
			} else {
				authordb.put(rs.getInt(dbc.user_id), new Author(rs.getInt(dbc.user_id), rs.getString(dbc.uname),
						rs.getInt(dbc.uage), rs.getString(dbc.ugender), rs.getString(dbc.uloc)));
			}
		} ;

		opiniondb.forEach((k, v) -> {
			ArrayList<Integer> uniqueauthors = new ArrayList<Integer>();
			ArrayList<Post> temp_post = v.getPosts();
			temp_post.forEach((v2) -> {
				if (!uniqueauthors.contains(v2.getUID()))
					uniqueauthors.add(v2.getUID());
			});
			uniqueauthors.forEach((v3) -> {
				Author temp_author = authordb.get(v3);
				temp_author.addComments(v.ncomments());
				temp_author.addLikes(v.nlikes());
				temp_author.addViews(v.nviews());
				temp_author.addPosts();
				authordb.put(temp_author.getID(), temp_author);
			});

			totalcomments += v.ncomments();
			totallikes += v.nlikes();
			totalviews += v.nviews();
		});

		this.totalposts = opiniondb.size();
		authordb.forEach((k, v) -> {
			v.calcInfluence((totalcomments / ((double) opiniondb.size())), totallikes / ((double) opiniondb.size()),
					totalviews / ((double) opiniondb.size()));
		});
		opiniondb.forEach((k, v) -> {
			v.evalReach(totalcomments / ((double) opiniondb.size()), totallikes / ((double) opiniondb.size()),
					totalviews / ((double) opiniondb.size()));
			v.evalPolarity(authordb);
		});

		opiniondb.forEach((k, v) -> {
			System.out.println("AMEN ID : " + " " + v.getPolarity() + "/" + v.getReach());
		});

		authordb.forEach((k, author) -> {
			String insert = "INSERT INTO authors " + "Values (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY "
					+ "UPDATE age=?, influence=?, comments=?, likes=?, views=?";
			// System.out.println(query);
			PreparedStatement query1;
			try {
				query1 = cnlocal.prepareStatement(insert);
				query1.setInt(1, author.getID());
				query1.setInt(2, author.getAge());
				query1.setString(3, author.getName());
				query1.setString(4, author.getGender());
				query1.setString(5, author.getLocation());
				query1.setDouble(6, author.getInfluence());
				query1.setInt(7, author.getComments());
				query1.setInt(8, author.getLikes());
				query1.setInt(9, author.getViews());
				query1.setInt(10, author.getAge());
				query1.setDouble(11, author.getInfluence());
				query1.setInt(12, author.getComments());
				query1.setInt(13, author.getLikes());
				query1.setInt(14, author.getViews());

				System.out.println(query1);
				query1.executeUpdate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		opiniondb.forEach((k, opinion) -> {
			String update = "INSERT INTO opinions " + "Values (?,?,?,?,?,?,?) ON DUPLICATE KEY"
					+ " UPDATE reach=?, polarity=?, total_inf=?";
			// System.out.println(query);
			PreparedStatement query1;
			try {
				query1 = cnlocal.prepareStatement(update);
				query1.setInt(1, k);
				query1.setDouble(2, opinion.getReach());
				query1.setDouble(3, opinion.getPolarity());
				query1.setDouble(4, opinion.getTotalInf());
				query1.setInt(5, opinion.getUID());
				java.sql.Date sqlDate = new java.sql.Date(opinion.getTime().getTime());
				query1.setDate(6, sqlDate);
				query1.setInt(7, opinion.getTag());
				query1.setDouble(8, opinion.getReach());
				query1.setDouble(9, opinion.getPolarity());
				query1.setDouble(10, opinion.getTotalInf());

				System.out.println(query1);
				query1.executeUpdate();

				opinion.getPosts().forEach((post) -> {
					try {
						String update1 = "REPLACE INTO posts " + "Values (?,?,?,?,?)";
						PreparedStatement query2 = cnlocal.prepareStatement(update1);
						query2.setInt(1, post.getID());
						query2.setDouble(2, post.getPolarity());
						query2.setString(3, post.getComment());
						query2.setInt(4, k);
						query2.setInt(5, post.getUID());

						System.out.println(query2);
						query2.executeUpdate();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		    try { if (cnlocal != null) cnlocal.close(); } catch (Exception e) {};
	}

	/**
	 * Returns an Iterator of all Opinions
	 * 
	 * @return Iterator<Opinion>
	 */
	public Iterator<Opinion> getOpinion() {
		return opiniondb.values().iterator();
	}

	/**
	 * Returns an Author Object based on id
	 * 
	 * @param id
	 *            User id to search for
	 * @return Object
	 */
	public Author getAuthor(int id) {
		return authordb.get(id);
	}

	/**
	 * Returns an Author Object based on Opinion
	 * 
	 * @param op
	 *            Opinion Object
	 * @return Object
	 */
	public Author getAuthor(Opinion op) {
		return authordb.get(op.getUID());
	}

	/**
	 * Returns an Author Object based on Post
	 * 
	 * @param comment
	 *            Post Object
	 * @return Object
	 */
	public Author getAuthor(Post comment) {
		return authordb.get(comment.getUID());
	}

	public int getTotalComments() {
		return this.totalcomments;
	}

	public int getTotalViews() {
		return this.totalviews;
	}

	public int getTotalLikes() {
		return this.totallikes;
	}

	public int getTotalPosts() {
		return this.totalposts;
	}

}
