package server;

import java.util.HashMap;

public class Operations {
	private HashMap<String,Integer> op;
	
	
	public Operations(){
		op = new HashMap<String,Integer>();
		op.put("chartrequest", 1);
		op.put("load", 2);
		op.put("globalsentiment", 3);
		
		
	}

	public int getOP(String msg){
		System.out.println(msg);
		if(op.containsKey(msg))
		return op.get(msg);
		return 0;
	}
}
