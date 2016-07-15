package resources;

import java.util.ArrayList;
import java.util.HashMap;

//Post individual Object
public class PSS {
	private int id;
	private String tag;
	private double GlobalSentiment;

	public PSS() {
	}

	public void evalGlobal(HashMap<Integer, Opinion> opiniondb){
		
	}

	public double getGlobal(){
		return GlobalSentiment;
	}
}
