package general;

import java.util.HashMap;

public class Operations {
	private HashMap<String,Integer> op;
	
	
	public Operations(){
		op = new HashMap<String,Integer>();
		op.put("chartrequest", 1);		//not used
		op.put("load", 2);        
		op.put("globalsentiment", 3);	//not used
		op.put("getposts", 4);
		op.put("getmodels",5);
		op.put("getcomments", 6);		//not used
		op.put("clean", 7);
		op.put("getauthors", 8);		//not used
		op.put("getlastpost", 9);		//not used
		op.put("getinfgraph", 10);		//not used
		op.put("getpopulation", 11);	//not used
		op.put("getconfig", 12);
		op.put("setconfig", 13);		//not used
		op.put("create_model", 14);		
		op.put("get_model", 15);
		op.put("update_model",16);
		op.put("getpss", 17);
		op.put("opinion_extraction", 18); // TODO correct date
		op.put("oe_refresh", 19);
		op.put("Top5Reach", 20);
		op.put("gettree", 21);
		op.put("getrestrictions", 22);
		
		
	}

	public int getOP(String msg){
		if(op.containsKey(msg))
		return op.get(msg);
		return 0;
	}
}
