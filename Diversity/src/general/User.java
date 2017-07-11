package general;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class DesignProject.
 */
// Post individual Object
public class User {
	private long id;
	private String username;
	private String password;
	private String email;
	private String first_name;
	private String last_name;
	private String role;
	private long company_id;

	public User(long _id, String _username, String _password, String _email, String _first_name, String _last_name,
			String _role, long _company_id) {
		this.id = _id;
		this.username = _username;
		this.email = _email;
		this.password = _password;
		this.first_name = _first_name;
		this.last_name = _last_name;
		this.role = _role;
		this.company_id = _company_id;

	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getID() {
		return id;
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUserName() {
		return username;
	}


}
