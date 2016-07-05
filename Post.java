package resources;

import java.sql.Date;

//Post individual Object
public class Post {
	private int id;
	private int userid;
	private Date time;
	private String message = new String();
	private int likes;
	private int views;
	private int polarity;
	
	public Post(int _id, int _userid, Date _time, int _likes, int _views, String _message){
		this.id = _id;
		this.userid = _userid;
		this.time = _time;
		this.views=_views;
		this.likes=_likes;
		this.message=_message;
		
		//To replace with API
		
    	String[] words = message.split("[^\\w'-]+");
    	
    	Adjectives adjs = new Adjectives();
    	int sentiment=50;
    	for(int i = 0; i<words.length; i++){
    		
    		String currentWord  = words[i];
    		
    		if(adjs.matches(currentWord)){
    			sentiment = adjs.getSentiment(currentWord);
    		}
    	}
		// End of To Replace
		
		this.polarity=sentiment;
	}
	
	public int getUID() {
		return userid;
	}
	
	public String getComment(){
		return message;
	}
	
	public int getID(){
		return id;
	}
	
	public Date getTime(){
		return time;
	}
	
	public int getLikes(){
		return likes;
	}
	
	public int getViews(){
		return views;
	}
	
	public int getPolarity(){
		return polarity;
	}
	
}
