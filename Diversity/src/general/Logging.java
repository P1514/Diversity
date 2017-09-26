package general;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Logging {
	
	private static Connection cnlocal;
	/*
	public Logger create(String classname) {
		Logger LOGGER = Logger.getLogger(classname);
		FileHandler fh;
		try {
			File homeLoggingDir = new File(System.getProperty("user.home") + "/SentimentAnalysisLogs/");
			if (!homeLoggingDir.exists()) {
				homeLoggingDir.mkdirs();
				LOGGER.info("Creating missing logging directory: " + homeLoggingDir);
			}
			// This block configure the logger with handler and formatter
			fh = new FileHandler(System.getProperty("user.home") + "/SentimentAnalysisLogs/" + classname + ".log",
					true);
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			LOGGER.info("Logger initilized\n\n\n");

		} catch (SecurityException e) {
			LOGGER.log(Level.INFO, Settings.err_unknown);
		} catch (IOException e) {
			LOGGER.log(Level.INFO, Settings.err_unknown);
		}
		return LOGGER;
	}
	*/
	
	public Logger create(String classname) {
		Logger LOGGER = Logger.getLogger(classname);
		DBHandler dh;
		
		dh = new DBHandler(1, System.currentTimeMillis());
		LOGGER.addHandler(dh);
		SimpleFormatter formatter = new SimpleFormatter();
		dh.setFormatter(formatter);
		LOGGER.info("Logger initialized\n\n\n");
		
		return LOGGER;
	}
	

	public static JSONArray getAllLogs() throws IOException, JSONException {
		
		String select = "SELECT * FROM " + Settings.ltable;

		PreparedStatement query1 = null;
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray logs = new JSONArray();
		
		try {
			query1 = cnlocal.prepareStatement(select, PreparedStatement.RETURN_GENERATED_KEYS);
			try (ResultSet rs = query1.executeQuery()) {
				try {
					File logDir = new File("WebContent/pages/Logs/");
					if (!logDir.exists()) {
						logDir.mkdir();
					}
					
					Calendar date = Calendar.getInstance();
					File latest = new File("WebContent/pages/Logs/" + date.get(Calendar.DAY_OF_MONTH) + "-" + (date.get(Calendar.MONTH)+1) + "-" + date.get(Calendar.YEAR) + ".txt");
					latest.createNewFile();
					FileWriter writer = new FileWriter(latest, true);
					while(rs.next()) {
						writer.write(rs.getString(4));
						/*
						JSONObject obj = new JSONObject();
						obj.put("User", rs.getInt(2));
						obj.put("Timestamp", rs.getTimestamp(3));
						obj.put("Log", rs.getString(4));
						logs.put(obj);
						*/
					}
					writer.close();
					
					File[] allLogs = logDir.listFiles();
					
					for (File file : allLogs) {
						logs.put(new JSONObject().put("log", file.getName()));
					}
				
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
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
		
		try {
			dbconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String delete = "DELETE FROM " + Settings.ltable;
		try {
			query1 = cnlocal.prepareStatement(delete);
			query1.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		try {
			cnlocal.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return logs;
		/*
		JSONArray logs = new JSONArray();
		File homeLoggingDir = new File(System.getProperty("user.home") + "/SentimentAnalysisLogs/");
		if (!homeLoggingDir.exists()) {
			logs.put("No data.");
			return logs;
		}

		logs.put(getLogs("extraction.Extrapolation.log"));
		logs.put(getLogs("extraction.GetComments.log"));
		logs.put(getLogs("extraction.GetPosts.log"));
		logs.put(getLogs("extraction.GetReach.log"));
		logs.put(getLogs("extraction.GlobalSentiment.log"));
		logs.put(getLogs("extraction.Snapshot.log"));
		logs.put(getLogs("general.Backend.log"));
		logs.put(getLogs("general.Data.log"));
		logs.put(getLogs("general.Loader.log"));
		logs.put(getLogs("general.LoadThreads.log"));
		logs.put(getLogs("general.Model.log"));
		logs.put(getLogs("general.Server.log"));
		logs.put(getLogs("general.Settings.log"));
		logs.put(getLogs("general.Startup.log"));
		logs.put(getLogs("monitoring.Oversight.log"));

		return logs;
		*/
	}
	
	public static JSONObject getLogs(String classname) throws IOException, JSONException {
		List<String> logs = Files.readAllLines(Paths.get(System.getProperty("user.home") + "/SentimentAnalysisLogs/" + classname),	Charset.defaultCharset());
		JSONObject logsJSON = new JSONObject();
		String text = "";
		for(String s : logs) {
			text += s;
			//System.out.println("working...");
		}
		
		logsJSON.put(classname, text);
		//System.out.println(classname);
		return logsJSON;
	}
	
	private static void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
