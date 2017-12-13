package general;
/**
 * 
 * @author Uninova - IControl
 *
 */
public class Post {
	private long id;
	//private long userid;
	private String userid2;
	private long time = 0;
	private String message;
	private long likes = 0;
	private long views = 0;
	private double polarity = -1;
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
	public Post(long id, String userid, long time, long likes, long views, String message, double polarity, String _source){
		this.polarity=polarity;
		this.id = id;
		this.userid2 = userid;
		this.time = time;
		this.views = views;
		this.likes = likes;
		this.message = message;
		this.source = _source;	
	}
	
//	public Post(long id, long userid, long time, long likes, long views, String message) {
//		this.id = id;
//		this.userid = userid;
//		this.time = time;
//		this.views = views;
//		this.likes = likes;
//		this.message = message;
//
//		// To replace with API
//		if (Settings.LocalPolarity) {
//			String[] words = message.split("[^\\w'-]+");
//
//			Adjectives adjs = new Adjectives();
//			double sentiment = 50;
//			for (int i = 0; i < words.length; i++) {
//
//				String currentWord = words[i];
//
//				if (adjs.matches(currentWord)) {
//					sentiment = adjs.getSentiment(currentWord);
//				}
//			}
//			// End of To Replace
//
//			this.polarity = sentiment;
//		}else{
//			this.polarity= -1;
//		}
//	}

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
	public Post(long id, String source, String userid, long time, long likes, long views, String message) {
		this.id = id;
		this.userid2 = userid;
		this.time = time;
		this.views = views;
		this.likes = likes;
		this.message = message;
		this.source = source;

		// To replace with API
		if (Settings.LocalPolarity) {
			String[] words = message.split("[^\\w'-]+");
			int count = 0;
			int sum = 0;
			Adjectives adjs = new Adjectives();
			double sentiment = 50;
			for (int i = 0; i < words.length; i++) {

				String currentWord = words[i];

				if (adjs.matches(currentWord)) {
//					sentiment = adjs.getSentiment(currentWord);
					sum += adjs.getSentiment(currentWord);
					count++;
				}
			}
			sentiment = count > 0 ? sum/(double)count : 50;
			// End of To Replace

			this.polarity = sentiment;
		}else{
			this.polarity= -1;
		}
		message=message.trim();
		if(message.length()<=1) {
			this.polarity=50.0;
		}
	}

//	/**
//	 * Gets the uid.
//	 *
//	 * @return the uid
//	 */
//	public long getUID() {
//		return userid;
//	}

	/**
	 * Gets the uid.
	 *
	 * @param a
	 *            the a
	 * @return the uid
	 */
	public String getUID(/*boolean a*/) {
//		if (a)
//			return userid2;
		return userid2;
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
	 * Sets the polarity.
	 *
	 * @return the polarity
	 */
	public void setPolarity(double pol) {
		this.polarity=pol;
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	public void setNewstatus() {
		this.time=1;
		
	}

}
