package general;


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

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getemail() {
		return email;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getpassword() {
		return password;
	}

	/**
	 * Gets the first_name.
	 *
	 * @return the first_name
	 */
	public String getfirst_name() {
		return first_name;
	}

	/**
	 * Gets the last_name.
	 *
	 * @return the last_name
	 */
	public String getlast_name() {
		return last_name;
	}

	/**
	 * Gets the role.
	 *
	 * @return the role
	 */
	public String getrole() {
		return role;
	}
	
	/**
	 * Gets the company_id.
	 *
	 * @return the company_id
	 */
	public long getcompany_id() {
		return company_id;
	}
}