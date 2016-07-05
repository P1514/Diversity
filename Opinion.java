package resources;

import java.util.ArrayList;

//Opinion Individual Object
public class Opinion {

	private Post main;
	private int author_id;
	private ArrayList<Post> comments = new ArrayList<Post>(); 
	private double reach;
	private int polarity;
	private Settings dbc;
	
	
	public Opinion(Post main){
		this.author_id=main.getUID();
		
	}

	public int getUID() {
		return main.getUID();
	}
	
	public void evalReach(double avgPost, double avgLikes, double avgViews) {
		this.reach = dbc.Wcomments*(ncomments()/avgPost) + dbc.Wlikes*(nlikes()/avgLikes) + dbc.Wviews*(nviews()/avgViews);
	}
	
	public void evalPolarity() {
		// To Do
	}
	
	public void addcomment(Post _comment){
		comments.add(_comment);
	}
	
	public int getPolarity(){
		return polarity;
	}
	
	public double getReach(){
		return reach;
	}
	
	public int getID(){
		return author_id;
	}
	
	public int ncomments(){
		return comments.size();
	}
	
	public int nlikes(){
		int num = main.getLikes();
		for(Post i : comments){
			num=+i.getLikes();
		}
		return num;
	}
	
	public int nviews(){
		int num = main.getViews();
		for(Post i : comments){
			num=+i.getViews();
		}
		return num;
	}
	
}
