package resources;

import java.util.HashMap;


public class Adjectives {

	static HashMap <String, Integer> adjectives;
	
	public Adjectives(){
		adjectives = new HashMap<String,Integer>();
		adjectives.put("phenomenal", 96);
		adjectives.put("world-Class", 92); 
		adjectives.put("incredible", 88);
		adjectives.put("amazing", 84);
		adjectives.put("exceptional", 80);
		adjectives.put("excellent", 76);
		adjectives.put("superior",	72);
		adjectives.put("great", 68);
		adjectives.put("good",	64);
		adjectives.put("fine", 60);
		adjectives.put("decent", 56);
		adjectives.put("fair",	52);
		adjectives.put("average", 48);
		adjectives.put("mediocre",	44);
		adjectives.put("limited", 	40);
		adjectives.put("weak",	36);
		adjectives.put("deficient", 32);
		adjectives.put("inferior",	28);
		adjectives.put("poor",	24);
		adjectives.put("bad", 	20);
		adjectives.put("awful", 16);
		adjectives.put("terrible",	12);
		adjectives.put("dreadful",	8);
		adjectives.put("horrendous", 4);
	}
	
	public boolean matches(String adj){
		return adjectives.containsKey(adj);
	}
	
	public int getSentiment(String adj){
		return adjectives.get(adj);
	}
}
