package importDB;

import java.util.HashMap;

//Post individual Object
public class PSS {
	private HashMap<String,Integer> tags = new HashMap<String,Integer>();

	public PSS() {
	}

	public boolean tagexists(String word){
		return tags.containsKey(word);
	}
	
	public int getTag(String word){
		return tags.get(word);
	}
}
