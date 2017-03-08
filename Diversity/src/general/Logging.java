package general;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging{
	
	public Logger create(String classname){
		Logger LOGGER = Logger.getLogger(classname);
		FileHandler fh;
		try {  
			 File homeLoggingDir = new File (System.getProperty("user.home")+"/SentimentAnalysisLogs/");
		        if (!homeLoggingDir.exists() ) {
		            homeLoggingDir.mkdirs();
		            LOGGER.info("Creating missing logging directory: " + homeLoggingDir);
		        }
	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler(System.getProperty("user.home")+"/SentimentAnalysisLogs/"+classname+".log", true);  
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
}
