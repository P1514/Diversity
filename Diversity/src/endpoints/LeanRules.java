package endpoints;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
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

	private Map<LeanRule, ArrayList<Integer>> matrix;
	private Map<LeanRule, ArrayList<Integer>> guidelineMatrix;

	private static Connection cncr;
	private static Connection cnlocal;
	private JSONArray result, guidelineResult;
	private String dp;

	public LeanRules(String dp_id) throws JSONException {
		this.dp = dp_id;
		buildMatrix();
		result = buildResult();
		guidelineResult = buildGuidelineResult();
	}

	public JSONArray getResult() {
		return result;
	}
	
	public JSONArray getGuidelineResult() {
		return guidelineResult;
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
		matrix = new HashMap<LeanRule, ArrayList<Integer>>();
		// for each rule get the design projects where it is active
		for (LeanRule r : rules) {
			designProjects = getDesignProjects(r.getId());

			for (int p : designProjects) {
				List<Integer> tmp;
				if (!matrix.containsKey(r)) {
					tmp = new ArrayList<Integer>();
					tmp.add(p);
					matrix.put(r, (ArrayList<Integer>) tmp);
				} else {
					tmp = matrix.get(r);
					tmp.add(p);
					matrix.replace(r, (ArrayList<Integer>) tmp);
				}
			}
		}

		List<LeanRule> guidelines = getRules(Integer.parseInt(dp));
		List<Integer> guidelinesDesignProjects = null;
		guidelineMatrix = new HashMap<LeanRule, ArrayList<Integer>>();
		// for each rule get the design projects where it is active
		for (LeanRule g : guidelines) {
			guidelinesDesignProjects = getGuidelineDesignProjects(g.getId());

			for (int p : guidelinesDesignProjects) {
				List<Integer> tmp;
				if (!guidelineMatrix.containsKey(g)) {
					tmp = new ArrayList<Integer>();
					tmp.add(p);
					guidelineMatrix.put(g, (ArrayList<Integer>) tmp);
				} else {
					tmp = guidelineMatrix.get(g);
					tmp.add(p);
					guidelineMatrix.replace(g, (ArrayList<Integer>) tmp);
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

			List<Double> dpSentiment = new ArrayList<Double>();

			for (int dp : matrix.get(r)) {
				if (dp != -1) {
					dpSentiment.add(getDesignProjectSentiment(dp));
				}
			}
			double avg = 0;
			for (double s : dpSentiment) {
				avg += s;
//				System.out.println(s + "  " + avg);
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
			// obj.put("Score", res);
			json.put(obj);
		}

		return json;
	}

	private JSONArray buildGuidelineResult() throws JSONException {
		JSONArray json = new JSONArray();
		JSONObject obj = new JSONObject();
		Set<LeanRule> guidelinesSet = guidelineMatrix.keySet();

		obj.put("Op", "guidelines");
		json.put(obj);

		for (LeanRule g : guidelinesSet) {
			obj = new JSONObject();
			obj.put("Guideline", g.getRule());
			obj.put("ID", g.getId());
			obj.put("Projects", matrix.get(g));

			List<Double> dpSentiment = new ArrayList<Double>();

			for (int dp : guidelineMatrix.get(g)) {
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
			// obj.put("Score", res);
			json.put(obj);
		}

		return json;
	}

	public static ArrayList<LeanRule> getRules(int dp) {
		List<LeanRule> rules = new ArrayList<LeanRule>();

		String select = "";
		if (dp == -1) { // if dp is -1 it gets rules from all projects
			select = "SELECT DISTINCT lean_rule_id, lean_rule.rule FROM diversity_common_repository.design_project_lean_rule,diversity_common_repository.lean_rule WHERE lean_rule_id = diversity_common_repository.lean_rule.id;";
		} else {

			select = "SELECT DISTINCT lean_rule_id, lean_rule.rule FROM diversity_common_repository.design_project_lean_rule,diversity_common_repository.lean_rule WHERE design_project_id =? AND lean_rule_id = diversity_common_repository.lean_rule.id;";

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

		return (ArrayList<LeanRule>) rules;
	}

	public static JSONArray getUnusedRules() {
		JSONArray rules = new JSONArray();

		String select = "SELECT distinct lean_rule_id FROM diversity_common_repository.design_project_lean_rule where lean_rule_id not in (select distinct lean_rule_id from diversity_common_repository.design_project_lean_rule where checked = 1);";

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
					LeanRule r = new LeanRule(rs.getInt(1), "");
					JSONObject obj = new JSONObject();
					obj.put("ID", r.id);
					rules.put(obj);
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

		return rules;
	}

	public static JSONArray getGuidelines(int dp) {
		JSONArray guidelines = new JSONArray();

		String select = "";
		if (dp == -1) { // if dp is -1 it gets rules from all projects
			select = "SELECT DISTINCT lean_guideline_id, lean_guideline.guideline FROM diversity_common_repository.design_project_lean_guideline,diversity_common_repository.lean_guideline WHERE lean_guideline_id = diversity_common_repository.lean_guideline.id;";
		} else {
			select = "SELECT DISTINCT lean_guideline_id, lean_guideline.guideline FROM diversity_common_repository.design_project_lean_guideline,diversity_common_repository.lean_guideline WHERE design_project_id =? AND lean_guideline_id = diversity_common_repository.lean_guideline.id;";
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
					JSONObject obj = new JSONObject();
					obj.put("ID", r.getId());
					obj.put("Guideline", r.getRule());
					guidelines.put(obj);
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

		return guidelines;
	}

	public static JSONArray getUnusedGuidelines() {

		JSONArray guidelines = new JSONArray();

		String select = "SELECT distinct lean_guideline_id FROM diversity_common_repository.design_project_lean_guideline where lean_guideline_id not in (select distinct lean_guideline_id from diversity_common_repository.design_project_lean_guideline where checked = 1);";

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
					LeanRule r = new LeanRule(rs.getInt(1), "");
					JSONObject obj = new JSONObject();
					obj.put("ID", r.id);
					guidelines.put(obj);
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

		return guidelines;
	}

	public static ArrayList<Integer> getDesignProjects(int ruleId) {
		List<Integer> designProjects = new ArrayList<Integer>();

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

		return (ArrayList<Integer>) designProjects;
	}

	public static ArrayList<Integer> getGuidelineDesignProjects(int guidelineId) {
		List<Integer> designProjects = new ArrayList<Integer>();

		String select;

		if (guidelineId == -1) {
			select = "SELECT DISTINCT design_project_id FROM diversity_common_repository.design_project_lean_guideline WHERE checked = 1";
		} else {
			select = "SELECT design_project_id FROM diversity_common_repository.design_project_lean_guideline WHERE lean_guideline_id =? AND checked = 1";
		}

		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cncr.prepareStatement(select);
			if (guidelineId != -1) {
				query1.setInt(1, guidelineId);
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

		return (ArrayList<Integer>) designProjects;
	}

	private double getDesignProjectSentiment(int dpId) throws JSONException {
		int pss = -1;
		int avg = -1;
		List<Integer> frequencies = new ArrayList<Integer>();
		List<Double> sentiments = new ArrayList<Double>();
		String select1 = "SELECT DISTINCT produces_pss_id FROM diversity_common_repository.design_project"
				+ (dpId != -1 ? " WHERE id = ? " : "");
		String select2 = "SELECT (SELECT SUM(polarity*reach) FROM opinions WHERE pss = ?) / (SELECT SUM(reach) FROM opinions WHERE pss= ?);";

		// String select = "SELECT produces_pss_id FROM
		// diversity_common_repository.design_project WHERE id = ?;";
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
					System.out.println(query1.toString());
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

		// List<Integer> models = new ArrayList<Integer>();
		// List<Integer> frequencies = new ArrayList<Integer>();
		// List<Double> sentiments = new ArrayList<Double>();
		// String select = "SELECT DISTINCT " + Settings.lmtable_id + ", " +
		// Settings.lmtable_update + " FROM " + Settings.lmtable
		// + " WHERE " + Settings.lmtable_designproject + "=?";
		//
		// //String select = "SELECT produces_pss_id FROM
		// diversity_common_repository.design_project WHERE id = ?;";
		// PreparedStatement query1 = null;
		// try {
		// localDBconnect();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// try {
		// query1 = cnlocal.prepareStatement(select);
		// query1.setInt(1, dpId);
		// try (ResultSet rs = query1.executeQuery()) {
		// while (rs.next()) {
		// models.add(rs.getInt(1));
		// frequencies.add(rs.getInt(2));
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// try {
		// cnlocal.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// System.out.println("Design project: " + dpId + "-------------");
		// for (int i = 0; i < models.size(); i++) {
		// Globalsentiment gs = new Globalsentiment();
		// //System.out.println("Model " + models.get(i) + ": " +
		// gs.getAvgSentiment("All", "", models.get(i)));
		// JSONArray sentiment = models.get(i) != -1 ? gs.globalsentiment(null,
		// null, "Global", models.get(i), frequencies.get(i)) : null;
		// sentiments.add(sentiment != null ?
		// sentiment.getJSONObject(0).getDouble("Value") : -1);
		// }
		// double avg = 0;
		// sentiments.removeIf(i -> i == -1);
		//
		// for (double s : sentiments) {
		// System.out.println(s);
		// avg += s;
		// }
		//
		// return avg / sentiments.size();
	}
}