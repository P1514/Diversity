package importDB;

import backend.Settings;

//Author Individual Object
public class Author {

	private int id; // Keys
	private String name; //
	private double influence; // Calculated
	private int age;
	private String gender;
	private String location;
	private int comments;
	private int posts;
	private int likes;
	private int views;
	private Settings dbc;

	public Author(int _id, String _name, int _age, String _gender, String _location) {
		this.id = _id;
		this.age = _age;
		this.name = _name;
		this.gender = _gender;
		this.location = _location;
		this.influence = 0;
		this.comments = 0;
		this.likes = 0;
		this.views = 0;
		dbc = new Settings();

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

	public String getGender() {
		return gender;
	}

	public int getAge() {
		return age;
	}

	public int getID() {
		return id;
	}

	public void addComments(int number) {
		this.comments += number;
	}

	public void addViews(int number) {
		this.views += number;
	}

	public void addLikes(int number) {
		this.likes += number;
	}

	public void addPosts(){
		this.posts += 1;
	}
	public int getComments() {
		return this.comments;
	}

	public int getLikes() {
		return this.likes;
	}

	public int getViews() {
		return this.views;
	}
	
	public void calcInfluence(double avgcom, double avglike, double avgview) {
		this.influence = dbc.aWcomments * ((this.comments/this.posts) / avgcom) + dbc.aWlikes * ((this.likes/this.posts) / avglike)
				+ dbc.aWviews * ((this.views/this.posts) / avgview);
		System.out.println(avgcom + " / " + avglike + " / " + avgview);
	}

}
