package general;

/**
 * The Class Author.
 *
 * @author Uninova - IControl
 */
public final class Author {

	// private long id; // Keys
	private String id2; // Keys String
	private String source;
	private String name; //
	private double influence; // Calculated
	private long age;
	private String gender;
	private String location;
	private long comments;
	private long posts = 1;
	private long likes;
	private long views;

	/**
	 * Instantiates a new author.
	 *
	 * @param _id
	 *            the id
	 * @param _name
	 *            the name
	 * @param _age
	 *            the age
	 * @param _gender
	 *            the gender
	 * @param _location
	 *            the location
	 */
	/*public Author(String _id, String _name, long _age, String _gender, String _location) {
		this.id2 = _id;
		this.age = _age;
		this.name = _name;
		this.gender = _gender;
		this.location = _location;
		this.influence = 0;
		this.comments = 0;
		this.likes = 0;
		this.views = 0;
		this.source = "";

	}*/

	/**
	 * Instantiates a new author.
	 *
	 * @param _id
	 *            the id
	 * @param _source
	 *            the source
	 * @param _name
	 *            the name
	 * @param _age
	 *            the age
	 * @param _gender
	 *            the gender
	 * @param _location
	 *            the location
	 */
	public Author(String _id, String _source, String _name, long _age, String _gender, String _location) {
		this.id2 = _id;
		this.name=_name;
		this.source = _source;
		this.age = _age;
		this.gender = _gender;
		this.location = _location;
		this.influence = 0;
		this.comments = 0;
		this.likes = 0;
		this.views = 0;

	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Gets the influence.
	 *
	 * @return the influence
	 */
	public double getInfluence() {
		return influence;
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * Gets the gender.
	 *
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Gets the age.
	 *
	 * @return the age
	 */
	public long getAge() {
		return age;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	// public long getID() {
	// return id;
	// }

	/**
	 * Gets the uid.
	 *
	 * @return the uid
	 */
	public String getID() {
		return id2;
	}

	/**
	 * Gets the ammount of posts.
	 *
	 * @return the posts
	 */
	public long getPosts() {
		return posts;
	}

	/**
	 * Adds the ammount of comments.
	 *
	 * @param number
	 *            the number
	 */
	public void addComments(long number) {
		this.comments += number;
	}

	/**
	 * Adds the ammount of views.
	 *
	 * @param number
	 *            the number
	 */
	public void addViews(long number) {
		this.views += number;
	}

	/**
	 * Adds the likes.
	 *
	 * @param number
	 *            the number
	 */
	public void addLikes(long number) {
		this.likes += number;
	}

	/**
	 * Adds the posts.
	 */
	public void addPosts() {
		this.posts += 1;
	}

	/**
	 * Sets the comments.
	 *
	 * @param number
	 *            the new comments
	 */
	public void setComments(long number) {
		this.comments = number;
	}

	/**
	 * Sets the views.
	 *
	 * @param number
	 *            the new views
	 */
	public void setViews(long number) {
		this.views = number;
	}
	
	

	/**
	 * Sets the likes.
	 *
	 * @param number
	 *            the new likes
	 */
	public void setLikes(long number) {
		this.likes = number;
	}

	/**
	 * Sets the posts.
	 *
	 * @param number
	 *            the new posts
	 */
	public void setPosts(long number) {
		this.posts = number;
	}

	/**
	 * Gets the comments.
	 *
	 * @return the comments
	 */
	public long getComments() {
		return this.comments;
	}

	/**
	 * Gets the likes.
	 *
	 * @return the likes
	 */
	public long getLikes() {
		return this.likes;
	}

	/**
	 * Gets the views.
	 *
	 * @return the views
	 */
	public long getViews() {
		return this.views;
	}

	/**
	 * Calculates influence.
	 *
	 * @param avgcom
	 *            the avgcom
	 * @param avglike
	 *            the avglike
	 * @param avgview
	 *            the avgview
	 */
	public void calcInfluence(double avgcom, double avglike, double avgview) {
		// System.out.println(avgcom + " / " + avglike + " / " +
		// avgview+"/"+this.posts);
		this.influence = Settings.aWcomments
				* ((this.comments / (double)this.posts) / (double) (avgcom != (double) 0 ? avgcom : (double) 1))
				+ Settings.aWlikes * ((this.likes / (double)this.posts) / (avglike != (double) 0 ? avglike : (double) 1))
				+ Settings.aWviews * ((this.views / (double)this.posts) / (avgview != (double) 0 ? avgview : (double) 1));
		//System.out.println(avgcom + " / " + avglike + " / " + avgview);
	}

}
