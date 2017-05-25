package general;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import extraction.Globalsentiment;

public class LeanRules {
	private Map<Integer, ArrayList<Integer>> matrix;
	private Connection cnlocal;
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
	
	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buildMatrix() {
		// get design project rules
		List<Integer> rules = getRules();
		List<Integer> designProjects = null;
		matrix = new HashMap<Integer, ArrayList<Integer>>();
		// for each rule get the design projects where it is active
		for (int r : rules) {
			designProjects = getDesignProjects(r);

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
	}

	private JSONArray buildResult() throws JSONException {
		JSONArray json = new JSONArray();

		Set<Integer> rulesSet = matrix.keySet();

		for (int r : rulesSet) {
			JSONObject obj = new JSONObject();
			obj.put("Rule", r);
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
			}

			avg = avg / dpSentiment.size();

			int res = -1;

			if (avg > 50) {
				res = 1;
			} else if (avg == 50) {
				res = 0;
			} else {
				res = -1;
			}
			obj.put("Polarity", res);
			json.put(obj);
		}

		return json;
	}

	private ArrayList<Integer> getRules() {
		List<Integer> rules = new ArrayList<Integer>();

		String select = "SELECT lean_rule_id FROM diversity_common_repository.design_project_rule WHERE design_project_id =?";

		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cnlocal.prepareStatement(select);
			query1.setInt(1, Integer.parseInt(dp));
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					rules.add(rs.getInt(1));
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

		return (ArrayList<Integer>) rules;
	}

	private ArrayList<Integer> getDesignProjects(int ruleId) {
		List<Integer> designProjects = new ArrayList<Integer>();

		String select = "SELECT design_project_id FROM diversity_common_repository.design_project_rule WHERE lean_rule_id =? AND checked = 1";

		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cnlocal.prepareStatement(select);
			query1.setInt(1, ruleId);
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					designProjects.add(rs.getInt(1));
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

		return (ArrayList<Integer>) designProjects;
	}

	private double getDesignProjectSentiment(int dpId) throws JSONException {
		List<Integer> models = new ArrayList<Integer>();
		List<Integer> frequencies = new ArrayList<Integer>();
		List<Double> sentiments = new ArrayList<Double>();
		String select = "SELECT " + Settings.lmtable_id + "," + Settings.lmtable_update + " FROM " + Settings.lmtable
				+ "  WHERE " + Settings.lmtable_designproject + "=?";

		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			query1 = cnlocal.prepareStatement(select);
			query1.setInt(1, dpId);
			try (ResultSet rs = query1.executeQuery()) {
				while (rs.next()) {
					models.add(rs.getInt(1));
					frequencies.add(rs.getInt(2));
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

		for (int i = 0; i < models.size(); i++) {
			Globalsentiment gs = new Globalsentiment();
			JSONArray sentiment = models.get(i) != -1 ? gs.getAvgSentiment((String) null, (String) null, models.get(i))
					: null;
			sentiments.add(sentiment != null ? sentiment.getJSONObject(0).getDouble("Value") : -1);
		}
		double avg = 0;
		sentiments.removeIf(i -> i == -1);

		for (double s : sentiments) {
			avg += s;
		}

		return avg / sentiments.size();
	}
}
