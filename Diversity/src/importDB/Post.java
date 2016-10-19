package importDB;

import java.sql.Date;

//Post individual Object
public class Post {
	private int id;
	private int userid;
	private String userid2 = null;
	private Date time;
	private String message = new String();
	private int likes = 0;
	private int views = 0;
	private double polarity = 50;
	private String source = "N/A";

	public Post(int _id, int _userid, Date _time, int _likes, int _views, String _message) {
		this.id = _id;
		this.userid = _userid;
		this.time = _time;
		this.views = _views;
		this.likes = _likes;
		this.message = _message;

		// To replace with API

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
	}

	public Post(int _id, String _source, String _userid, Date _time, int _likes, int _views, String _message) {
		this.id = _id;
		this.userid2 = _userid;
		this.time = _time;
		this.views = _views;
		this.likes = _likes;
		this.message = _message;
		this.source = _source;

		// To replace with API

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
	}

	public int getUID() {
		return userid;
	}
	
	public String getUID(boolean a){
		if(a) return userid2;
		return userid2+","+source;
	}

	public String getComment() {
		return message;
	}

	public int getID() {
		return id;
	}

	public Date getTime() {
		return time;
	}

	public int getLikes() {
		return likes;
	}

	public int getViews() {
		return views;
	}

	public double getPolarity() {
		return polarity;
	}

	public String getSource() {
		return source;
	}

}
