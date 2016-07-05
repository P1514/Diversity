package resources;

import java.util.ArrayList;

//Opinion Individual Object
public class Opinion {

	private Post main;
	private int author_id;
	private ArrayList<Post> comments = new ArrayList<Post>(); 
	private int reach;
	private int polarity;
	
	
	public Opinion(Post main){
		this.author_id=main.getUID();
		
	}

	public int getUID() {
		return main.getUID();
	}
	
	public void evalReach(int avgcom) {
		//To DO;
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
	
	public int getReach(){
		return reach;
	}
	
	public int getID(){
		return author_id;
	}
	
}
