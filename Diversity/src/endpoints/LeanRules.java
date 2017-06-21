package endpoints;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import extraction.Globalsentiment;
import general.Settings;

public class LeanRules {
	
	public static class LeanRule {
		
		private int id;
		private String rule;
		
		public LeanRule(int id, String rule) {
			this.id = id;
			this.rule = rule;
		}

		public int getId() {
			return id;
		}
		
		public String getRule() {
			return rule;
		}
	}
	
	private Map<LeanRule, LinkedList<Integer>> matrix;
	private static Connection cncr;
	private static Connection cnlocal;
	private JSONArray result;
	private String dp;

	public LeanRules(String dp_id) throws JSONException {
		this.dp = dp_id;
		buildMatrix();
		result = buildResult();
	}

	public JSONArray getResult() {
		return result;
	}

	private static void dbconnect() {
		try {
			cncr = Settings.conncr();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void localDBconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void buildMatrix() {
		// get design project rules
		List<LeanRule> rules = getRules(Integer.parseInt(dp));
		List<Integer> designProjects = null;
		matrix = new HashMap<LeanRule, LinkedList<Integer>>();
		// for each rule get the design projects where it is active
		for (LeanRule r : rules) {
			designProjects = getDesignProjects(r.getId());

			for (int p : designProjects) {
				List<Integer> tmp;
				if (!matrix.containsKey(r)) {
					tmp = new LinkedList<Integer>();
					tmp.add(p);
					matrix.put(r, (LinkedList<Integer>) tmp);
				} else {
					tmp = matrix.get(r);
					tmp.add(p);
					matrix.replace(r, (LinkedList<Integer>) tmp);
				}
			}
		}
	}

	private JSONArray buildResult() throws JSONException {
		JSONArray json = new JSONArray();
		JSONObject obj = new JSONObject();
		Set<LeanRule> rulesSet = matrix.keySet();

		obj.put("Op", "rules");
		json.put(obj);
		
		for (LeanRule r : rulesSet) {
			obj = new JSONObject();
			obj.put("Rule", r.getRule());
			obj.put("ID", r.getId());
			obj.put("Projects", matrix.get(r));

			List<Double> dpSentiment = new LinkedList<Double>();

			for (int dp : matrix.get(r)) {
				if (dp != -1) {
					dpSentiment.add(getDesignProjectSentiment(dp));
				}
			}
			double avg = 0;
			for (double s : dpSentiment) {
				avg += s;
			}

			avg = avg / dpSentiment.size();

			int res = -1;

			if (avg > 55) {
				res = 1;
			} else if (avg < 45) {
				res = -1;
			} else {
				res = 0;
			}
			
			obj.put("Score", (int) avg);
//			obj.put("Score", res);
			json.put(obj);
		}

		return json;
	}

	public static LinkedList<LeanRule> getRules(int dp) {
		List<LeanRule> rules = new LinkedList<LeanRule>();

		String select = "";
		if (dp == -1) { // if dp is -1 it gets rules from all projects 
			select = "SELECT DISTINCT lean_rule_id, rule FROM diversity_common_repository.design_project_lean_rule,diversity_common_repository.lean_rule WHERE lean_rule_id = diversity_common_repository.lean_rule.id;";
		} else {
			select = "SELECT DISTINCT lean_rule_id, rule FROM diversity_common_repository.design_project_lean_rule,diversity_common_repository.lean_rule WHERE design_project_id =? AND lean_rule_id = diversity_common_repository.lean_rule.id;";
		}

		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cncr.prepareStatement(select);
			if (dp != -1) {
				query1.setInt(1, dp);
			}
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					LeanRule r = new LeanRule(rs.getInt(1), rs.getString(2));
					rules.add(r);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			cncr.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return (LinkedList<LeanRule>) rules;
	}

	public static LinkedList<Integer> getDesignProjects(int ruleId) {
		List<Integer> designProjects = new LinkedList<Integer>();

		String select;

		if (ruleId == -1) {
			select = "SELECT DISTINCT design_project_id FROM diversity_common_repository.design_project_lean_rule WHERE checked = 1";
		} else {
			select = "SELECT design_project_id FROM diversity_common_repository.design_project_lean_rule WHERE lean_rule_id =? AND checked = 1";
		}

		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cncr.prepareStatement(select);
			if (ruleId != -1) {
				query1.setInt(1, ruleId);
			}
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					designProjects.add(rs.getInt(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			cncr.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return (LinkedList<Integer>) designProjects;
	}

	private double getDesignProjectSentiment(int dpId) throws JSONException {
		int pss = -1;
		int avg = -1;
		List<Integer> frequencies = new LinkedList<Integer>();
		List<Double> sentiments = new LinkedList<Double>();
		String select1 = "SELECT DISTINCT produces_pss_id FROM diversity_common_repository.design_project WHERE id = ? ";
		String select2 = "SELECT (SELECT SUM(polarity*reach) FROM opinions WHERE pss = ?) / (SELECT SUM(reach) FROM opinions WHERE pss= ?);";

		//String select = "SELECT produces_pss_id FROM diversity_common_repository.design_project WHERE id = ?;";
		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cncr.prepareStatement(select1);
			query1.setInt(1, dpId);
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					pss = rs.getInt(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			cncr.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		query1 = null;
		try {
			localDBconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cnlocal.prepareStatement(select2);
			query1.setInt(1, pss);
			query1.setInt(2, pss);
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					avg = rs.getInt(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			cnlocal.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return avg;
	}
	
	
	public JSONArray getUnusedRules() throws JSONException {
		List<Integer> rules = new LinkedList<Integer>();
		String select = "SELECT DISTINCT lean_rule_id FROM diversity_common_repository.design_project_lean_rule WHERE lean_rule_id NOT IN (SELECT lean_rule_id FROM diversity_common_repository.design_project_lean_rule WHERE checked = 1);";
	
		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cncr.prepareStatement(select);
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					rules.add(rs.getInt(1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			cncr.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		JSONArray json = new JSONArray();
		JSONObject obj;
		for (int r : rules) {
			obj = new JSONObject();
			obj.put("ID", r);
			json.put(obj);
		}
		
		return json;
	}
}
