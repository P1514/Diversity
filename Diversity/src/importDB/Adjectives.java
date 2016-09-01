package importDB;

import java.util.concurrent.ConcurrentHashMap;

public class Adjectives {

	static ConcurrentHashMap<String, Double> adjectives;

	public Adjectives() {
		adjectives = new ConcurrentHashMap<String, Double>();
		adjectives.put("phenomenal", 97.0);
		adjectives.put("world-class", 95.0);
		adjectives.put("incredible", 92.0);
		adjectives.put("exceptional", 90.0);
		adjectives.put("tremendous", 88.0);
		adjectives.put("amazing", 87.0);
		adjectives.put("terrific", 85.0);
		adjectives.put("astonishing", 83.0);
		adjectives.put("wonderful", 81.0);
		adjectives.put("superior", 80.0);
		adjectives.put("fantastic", 79.0);
		adjectives.put("excellent", 78.0);
		adjectives.put("great", 77.0);
		adjectives.put("superb", 76.0);
		adjectives.put("fabulous", 75.0);
		adjectives.put("splendid", 74.0);
		adjectives.put("astounding", 73.0);
		adjectives.put("magnificent", 72.0);
		adjectives.put("outstanding", 71.0);
		adjectives.put("sensational", 70.0);
		adjectives.put("idyllic", 69.0);
		adjectives.put("desirable", 68.0);
		adjectives.put("good", 67.0);
		adjectives.put("decent", 66.0);
		adjectives.put("popular", 65.0);
		adjectives.put("sweet", 63.0);
		adjectives.put("fine", 61.0);
		adjectives.put("cool", 59.0);
		adjectives.put("okay", 57.0);
		adjectives.put("fair", 55.0);
		adjectives.put("average", 50.0);
		adjectives.put("mediocre", 46.0);
		adjectives.put("lame", 40.0);
		adjectives.put("substandard", 39.0);
		adjectives.put("limited", 38.0);
		adjectives.put("rotten", 37.0);
		adjectives.put("weak", 36.0);
		adjectives.put("inferior", 35.0);
		adjectives.put("crappy", 34.0);
		adjectives.put("deficient", 33.0);
		adjectives.put("miserable", 32.0);
		adjectives.put("nasty", 31.0);
		adjectives.put("bad", 30.0);
		adjectives.put("poor", 29.0);
		adjectives.put("pitiful", 28.0);
		adjectives.put("pathetic", 27.0);
		adjectives.put("appalling", 26.0);
		adjectives.put("horrible", 24.0);
		adjectives.put("dreadful", 22.0);
		adjectives.put("terrible", 20.0);
		adjectives.put("awful", 15.0);
		adjectives.put("abysmal", 13.0);
	}

	public boolean matches(String adj) {
		return adjectives.containsKey(adj);
	}

	public double getSentiment(String adj) {
		//System.out.println(adj);
		return adjectives.get(adj) == null ? 50:adjectives.get(adj);
	}
}
