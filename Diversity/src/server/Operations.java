package server;

import java.util.HashMap;

public class Operations {
	private HashMap<String,Integer> op;
	
	
	public Operations(){
		op = new HashMap<String,Integer>();
		op.put("chartrequest", 1);
		op.put("load", 2);
		op.put("globalsentiment", 3);
		op.put("getposts", 4);
		op.put("getproducts",5);
		op.put("getcomments", 6);
		op.put("clean", 7);
		op.put("getauthors", 8);
		op.put("getlastpost", 9);
		op.put("getinfgraph", 10);
		
		
	}

	public int getOP(String msg){
		if(op.containsKey(msg))
		return op.get(msg);
		return 0;
	}
}
