package general;

import java.util.HashMap;

public class Operations {
	private static HashMap<Integer, Integer> permission;
	static{
		permission = new HashMap<>();
		//0- View Model
		//1- Edit Model
		//2- View Results
		//3- Snapshots
		//4- prediction
		//99- ADMIN
		permission.put(2, 99);
		permission.put(4, 2);
		permission.put(5, -1);
		permission.put(7, 99);
		permission.put(12, 2);
		permission.put(14, 1);
		permission.put(15, 0);
		permission.put(16, 1);
		permission.put(17, 1);
		permission.put(18, 2);
		permission.put(19, 2);
		permission.put(20, -1);
		permission.put(21, 0);
		permission.put(22, -1);
		permission.put(23, 4);
		permission.put(24, 4);
		permission.put(25, 3);
		permission.put(26, 2);
		permission.put(27, 3);
		permission.put(28, 1);
		permission.put(29, 3);
		permission.put(99, 99);
	}
	private static HashMap<String, Integer> op;
	static {
		op = new HashMap<>();
		op.put("chartrequest", 1); // not used
		op.put("load", 2); // ONLY ADMIN
		op.put("globalsentiment", 3); // not used
		op.put("getposts", 4); // View Results
		op.put("getmodels", 5); // No particular
		op.put("getcomments", 6); // not used
		op.put("clean", 7); // ONLY ADMIN
		op.put("getauthors", 8); // not used
		op.put("getlastpost", 9); // not used
		op.put("getinfgraph", 10); // not used
		op.put("getpopulation", 11); // not used
		op.put("getconfig", 12); // View Results
		op.put("setconfig", 13); // not used
		op.put("create_model", 14); // Create model
		op.put("get_model", 15); // View model
		op.put("update_model", 16); // Create Model
		op.put("getpss", 17); // Create Model
		op.put("opinion_extraction", 18); // View Results
		op.put("oe_refresh", 19); // View Results
		op.put("Top5Reach", 20); // No particular
		op.put("gettree", 21); // View Model
		op.put("getrestrictions", 22); // No particular
		op.put("prediction", 23); // Use prediction
		op.put("Snapshot", 24);	// Save_delete snapshot	
		op.put("load_snapshot", 25); // Save delete snapshot
		op.put("tagcloud", 26); // View Results
		op.put("set_ignore_word", 27); // set user ignored words
		op.put("get_mediawiki", 28); // Create model
		op.put("get_log", 29); // get all logs
		op.put("testing", 99); // Admin only
	}

	public int getOP(String msg) {
		if (op.containsKey(msg))
			return op.get(msg);
		return 0;
	}
	
	public static int return_main_permission(int id){
		
		return permission.containsKey(id) ? permission.get(id) : 99;
	}
}
