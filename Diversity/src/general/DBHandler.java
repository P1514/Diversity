package general;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import general.Settings;

/**
 * Handler to log output directly to the database.
 *
 */
public class DBHandler extends Handler {

	private Connection cnlocal;
	private int user_id;
	private Timestamp timestamp;

	public DBHandler(int user_id, long timestamp) {
		this.user_id = user_id;
		this.timestamp = new Timestamp(timestamp);
	}

	private void dbconnect() {
		try {
			cnlocal = Settings.connlocal();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		try (Connection cnlocal = Settings.connlocal()) {

			String sql = "INSERT INTO " + Settings.ltable + "(" + Settings.ltable_user + "," + Settings.ltable_timestamp
					+ "," + Settings.ltable_log + ") VALUES (?,?,?)";

			PreparedStatement insert = null;

			try {
				insert = cnlocal.prepareStatement(sql);
				insert.setInt(1, user_id);
				insert.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				insert.setString(3, record.getLevel() + " " + record.getMessage());
				insert.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		return;
	}

	/**
	 * Closes the connection.
	 */
	@Override
	public void close() throws SecurityException {
		try {
			cnlocal.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
