package general;

import java.sql.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class Post.
 */
// Post individual Object
public class Post {
	private long id;
	private long userid;
	private String userid2 = null;
	private long time = 0;
	private String message = new String();
	private long likes = 0;
	private long views = 0;
	private double polarity = 50;
	private String source = "N/A";

	/**
	 * Instantiates a new post.
	 *
	 * @param _id
	 *            the id of the post
	 * @param _userid
	 *            the user id
	 * @param _time
	 *            the time of the post
	 * @param _likes
	 *            the amount of likes
	 * @param _views
	 *            the amount of views
	 * @param _message
	 *            the message
	 */
	public Post(long _id, long _userid, long _time, long _likes, long _views, String _message) {
		this.id = _id;
		this.userid = _userid;
		this.time = _time;
		this.views = _views;
		this.likes = _likes;
		this.message = _message;

		// To replace with API
		if (Settings.LocalPolarity) {
			String[] words = message.split("[^\\w'-]+");

			Adjectives adjs = new Adjectives();
			double sentiment = 50;
			for (int i = 0; i < words.length; i++) {

				String currentWord = words[i];

				if (adjs.matches(currentWord)) {
					sentiment = adjs.getSentiment(currentWord);
				}
			}
			// End of To Replace

			this.polarity = sentiment;
		}else{
			this.polarity= 0;
		}
	}

	/**
	 * Instantiates a new post.
	 *
	 * @param _id
	 *            the id of the post
	 * @param _source
	 *            the source the source and account list
	 * @param _userid
	 *            the user id
	 * @param _time
	 *            the time of the post
	 * @param _likes
	 *            the amount of likes
	 * @param _views
	 *            the amount of views
	 * @param _message
	 *            the message
	 */
	public Post(long _id, String _source, String _userid, long _time, long _likes, long _views, String _message) {
		this.id = _id;
		this.userid2 = _userid;
		this.time = _time;
		this.views = _views;
		this.likes = _likes;
		this.message = _message;
		this.source = _source;

		// To replace with API
		if (Settings.LocalPolarity) {
			String[] words = message.split("[^\\w'-]+");

			Adjectives adjs = new Adjectives();
			double sentiment = 50;
			for (int i = 0; i < words.length; i++) {

				String currentWord = words[i];

				if (adjs.matches(currentWord)) {
					sentiment = adjs.getSentiment(currentWord);
				}
			}
			// End of To Replace

			this.polarity = sentiment;
		}else{
			this.polarity= 0;
		}
	}

	/**
	 * Gets the uid.
	 *
	 * @return the uid
	 */
	public long getUID() {
		return userid;
	}

	/**
	 * Gets the uid.
	 *
	 * @param a
	 *            the a
	 * @return the uid
	 */
	public String getUID(boolean a) {
		if (a)
			return userid2;
		return userid2 + "," + source;
	}

	/**
	 * Gets the comment.
	 *
	 * @return the comment
	 */
	public String getComment() {
		return message;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getID() {
		return id;
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Gets the amount of likes.
	 *
	 * @return the likes
	 */
	public long getLikes() {
		return likes;
	}

	/**
	 * Gets the amount views.
	 *
	 * @return the views
	 */
	public long getViews() {
		return views;
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
	 * Gets the source.
	 *
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

}
