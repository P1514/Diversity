package general;

//Author Individual Object
public class Author {

	private long id; // Keys
	private String id2; // Keys String
	private String source;
	private String name; //
	private double influence; // Calculated
	private long age;
	private String gender;
	private String location;
	private long comments;
	private long posts;
	private long likes;
	private long views;

	public Author(long _id, String _name, long _age, String _gender, String _location) {
		this.id = _id;
		this.age = _age;
		this.name = _name;
		this.gender = _gender;
		this.location = _location;
		this.influence = 0;
		this.comments = 0;
		this.likes = 0;
		this.views = 0;

	}

	public Author(String _id, String _source, String _name, long _age, String _gender, String _location) {
		this.id2 = _id;
		this.source = _source;
		this.age = _age;
		this.gender = _gender;
		this.location = _location;
		this.influence = 0;
		this.comments = 0;
		this.likes = 0;
		this.views = 0;

	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public double getInfluence() {
		return influence;
	}

	public String getSource() {
		return this.source;
	}

	public String getGender() {
		return gender;
	}

	public long getAge() {
		return age;
	}

	public long getID() {
		return id;
	}

	public String getUID() {
		return id2;
	}

	public long getPosts() {
		return posts;
	}

	public void addComments(long number) {
		this.comments += number;
	}

	public void addViews(long number) {
		this.views += number;
	}

	public void addLikes(long number) {
		this.likes += number;
	}

	public void addPosts() {
		this.posts += 1;
	}

	public void setComments(long number) {
		this.comments = number;
	}

	public void setViews(long number) {
		this.views = number;
	}

	public void setLikes(long number) {
		this.likes = number;
	}

	public void setPosts(long number) {
		this.posts = number;
	}

	public long getComments() {
		return this.comments;
	}

	public long getLikes() {
		return this.likes;
	}

	public long getViews() {
		return this.views;
	}

	public void calcInfluence(double avgcom, double avglike, double avgview) {
		this.influence = Settings.aWcomments
				* ((this.comments / this.posts) / (double) (avgcom != 0 ? avgcom : (double) 1))
				+ Settings.aWlikes * ((this.likes / this.posts) / (avglike != (double) 0 ? avglike : (double) 1))
				+ Settings.aWviews * ((this.views / this.posts) / (avgview != (double) 0 ? avgview : (double) 1));
		// System.out.println(avgcom + " / " + avglike + " / " + avgview);
	}

}
