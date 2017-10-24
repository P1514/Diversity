package general;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.sun.media.jfxmedia.logging.Logger;

import general.Settings;

/**
 * Handler to log output directly to the database.
 *
 */
public class DBHandler extends Handler {

	private int user_id;
	private Timestamp timestamp;

	public DBHandler(int user_id, long timestamp) {
		this.user_id = user_id;
		this.timestamp = new Timestamp(timestamp);
	}

	/**
	 * Stores a record in the database
	 */
	@Override
	public void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		String msg;
		try {
			msg = getFormatter().format(record);
		} catch (Exception ex) {
			reportError(null, ex, ErrorManager.FORMAT_FAILURE);
			return;
		}


			String sql = "INSERT INTO " + Settings.ltable + "(" + Settings.ltable_user + "," + Settings.ltable_timestamp
					+ "," + Settings.ltable_log + ") VALUES (?,?,?)";

			

			try (Connection cnlocal = Settings.connlocal();
				PreparedStatement insert = cnlocal.prepareStatement(sql)){
				insert.setInt(1, user_id);
				insert.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				insert.setString(3, record.getLevel() + " " + record.getMessage());
				insert.executeUpdate();
		} catch (ClassNotFoundException e1) {
			Logger.logMsg(Level.SEVERE.intValue(), "ERROR ON DBHANDLER");
		} catch (SQLException e1) {
			Logger.logMsg(Level.SEVERE.intValue(), "ERROR ON DBHANDLER");
		}

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public void close() throws SecurityException {
		// TODO Auto-generated method stub
		
	}

}
