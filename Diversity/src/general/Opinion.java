package general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class Opinion.
 */
//Opinion Individual Object
public class Opinion {

	private Post main;
	private long id;
	private long author_id; // String
	private String author_id2;
	private HashMap<Long,Post> comments = new HashMap<>();
	private String URI = "";
	private double reach = 0;
	private double polarity = 0;
	private double total_inf = 0;
	private long timestamp;
	private long pss;
	private long product;

	/**
	 * Instantiates a new opinion.
	 *
	 * @param _main the main post
	 * @param _pss the pss id
	 * @param _product the product id
	 */
	public Opinion(Post _main, long _pss, long _product) {
		this.main = _main;
		this.author_id = main.getUID();
		timestamp = main.getTime();
		pss = _pss;
		product = _product;
		this.id=this.main.getID();

	}
	
	/**
	 * Instantiates a new opinion.
	 *
	 * @param _main the main post
	 * @param _pss the pss id
	 * @param _product the product id
	 * @param _URI the source and account list Example:"facebook,shoes;twitter,run;"
	 */
	public Opinion(Post _main, long _pss, long _product, String _URI) {
		this.main = _main;
		this.author_id2 = main.getUID(false);
		timestamp = main.getTime();
		pss = _pss;
		product = _product;
		URI=_URI;
		this.id=this.main.getID();

	}

	/**
	 * Calculate reach.
	 *
	 * @param avgPost the avg post
	 * @param avgLikes the avg likes
	 * @param avgViews the avg views
	 */
	public void evalReach(double avgPost, double avgLikes, double avgViews) {
		this.reach = Settings.pWcomments * (ncomments() / avgPost) + Settings.pWlikes * (nlikes() / (avgLikes != 0 ? avgLikes : 1))
				+ Settings.pWviews * (nviews() / (avgViews != 0 ? avgViews : 1));
	}

	/**
	 * Calculate polarity.
	 *
	 * @param authordb the authordb
	 */
	public void evalPolarity(ConcurrentHashMap<Long, Author> authordb) {
		total_inf = authordb.get(author_id).getInfluence();
		polarity = total_inf * main.getPolarity();

		comments.forEach((k,v) -> {
			total_inf += authordb.get(v.getUID()).getInfluence();
			polarity += v.getPolarity() * authordb.get(v.getUID()).getInfluence();

		});

		polarity = polarity / total_inf;
	}
	
	
	/**
	 * Calculate polarity 2.
	 *
	 * @param authordb the authordb
	 */
	public void evalPolarity2(ConcurrentHashMap<String, Author> authordb) {
		total_inf = authordb.get(author_id2).getInfluence();
		polarity = total_inf * main.getPolarity();

		comments.forEach((k,v) -> {
			total_inf += authordb.get(v.getUID(false)).getInfluence();
			polarity += v.getPolarity() * authordb.get(v.getUID(false)).getInfluence();

		});

		polarity = polarity / total_inf;
	}

	/**
	 * Add comment.
	 *
	 * @param _comment the comment
	 */
	public void addcomment(Post _comment) {
		comments.put(_comment.getID(),_comment);
	}

	/**
	 * Gets the polarity.
	 *
	 * @return the polarity
	 */
	public double getPolarity() {
		return polarity;
	}

	/**
	 * Gets the reach.
	 *
	 * @return the reach
	 */
	public double getReach() {
		return reach;
	}

	/**
	 * Gets the uid.
	 *
	 * @return the uid long
	 */
	public long getUID() {
		return author_id;
	}
	
	/**
	 * Gets the uid.
	 *
	 * @param a the a
	 * @return the uid string
	 */
	public String getUID(boolean a) {
		return author_id2;
	}

	/**
	 * Gets the total influence.
	 *
	 * @return the total influence
	 */
	public double getTotalInf() {
		return total_inf;
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public Long getTime() {
		return timestamp;
	}

	/**
	 * Gets the pss id.
	 *
	 * @return the pss id
	 */
	public long getPSS() {
		return pss;
	}
	
	/**
	 * Gets the product id.
	 *
	 * @return the product id
	 */
	public long getProduct(){
		return product;
	}
	
	public long getID(){
		return this.id;
	}

	/**
	 * Number of comments.
	 *
	 * @return the long
	 */
	public long ncomments() {

		return (comments.isEmpty() ? 0 : comments.size());
	}

	/**
	 * New comments.
	 *
	 * @return the long the ammount of new comments from current update
	 */
	public long newcomments() {
		int newcomm = 0;

		for (Post i : comments.values()) {
			if (i.getTime() != 0)
				newcomm++;
		}

		return newcomm;

	}

	/**
	 * Number of likes.
	 *
	 * @return the long
	 */
	public long nlikes() {
		int num = 0;
		for (Post i : comments.values()) {
			num += i.getLikes();
		}
		return num;
	}

	/**
	 * New likes.
	 *
	 * @return the long the ammount of new likes from current update
	 */
	public long newlikes() {

		int newlike = 0;

		for (Post i : comments.values()) {
			if (i.getTime() != 0)
				newlike+=i.getLikes();
		}

		return newlike;

	}

	/**
	 * Number of views.
	 *
	 * @return the long
	 */
	public long nviews() {
		int num = 0;
		for (Post i : comments.values()) {
			num += i.getViews();
		}
		return num;
	}

	/**
	 * New views.
	 *
	 * @return the long the ammount of new views from current update
	 */
	public long newviews() {

		int newview = 0;

		for (Post i : comments.values()) {
			if (i.getTime() != 0)
				newview+=i.getViews();
		}

		return newview;
	}

	/**
	 * Gets the posts.
	 *
	 * @return the posts
	 */
	public HashMap<Long,Post> getPosts() {
		if (!comments.containsKey(this.id))
			comments.put(this.id,this.main);
		return comments;
	}
}
