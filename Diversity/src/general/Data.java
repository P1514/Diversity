package general;

import java.sql.*;
import security.SessionClean;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Class Data.
 */
public class Data {
	public static final Logger LOGGER = new Logging().create(Data.class.getName());
	/** The modeldb. */
	protected static final ConcurrentHashMap<Long, Model> modeldb = new ConcurrentHashMap<>();

	/** The pssdb. */
	protected static final ConcurrentHashMap<Long, PSS> pssdb = new ConcurrentHashMap<>();

	/** The productdb. */
	protected static final ConcurrentHashMap<Long, Product> productdb = new ConcurrentHashMap<>();

	/** The designProjectdb. */
	protected static final ConcurrentHashMap<Long, DesignProject> designProjectdb = new ConcurrentHashMap<>();
	
	/** The userdb. */
	protected static final ConcurrentHashMap<Long, User> userdb = new ConcurrentHashMap<>();
	
	/** The servicedb. */
	@SuppressWarnings("unused")
	protected static final ConcurrentHashMap<Long, Product> servicedb = new ConcurrentHashMap<>();

	/** The companydb. */
	protected static final ConcurrentHashMap<Long, Company> companydb = new ConcurrentHashMap<>();

	protected static final ConcurrentHashMap<String, Role> roledb = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, Timer> security = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, String> security_users = new ConcurrentHashMap<>();

	protected static void addproduct(ResultSet rs) throws SQLException {

		productdb.put(rs.getLong(Settings.crproducttable_id),
				new Product(rs.getLong(Settings.crproducttable_id), rs.getString(Settings.crproducttable_name),
						rs.getBoolean(Settings.crproducttable_isfinal), rs.getLong(Settings.crproducttable_supplied_by),
						rs.getLong(Settings.crproducttable_parent)));
		if (rs.getLong(Settings.crproducttable_parent) != 0) {
			Product parent = productdb.get(rs.getLong(Settings.crproducttable_parent));
			parent.setParent(rs.getLong(Settings.crproducttable_id));
		}

	}

	protected static void addservice(ResultSet rs) throws SQLException {

		servicedb.put(rs.getLong(Settings.crservicetable_id),
				new Product(rs.getLong(Settings.crservicetable_id), rs.getString(Settings.crservicetable_name), false,
						rs.getLong(Settings.crservicetable_supplied_by), rs.getLong(Settings.crservicetable_parent)));
		if (rs.getLong(Settings.crservicetable_parent) != 0) {
			Product parent = servicedb.get(rs.getLong(Settings.crservicetable_parent));
			parent.setParent(rs.getLong(Settings.crservicetable_id));
		}

	}
	

	public static boolean usercheck(String id, int op) {
		if (!security_users.containsKey(id))
			return false;
		return verifypermission(security_users.get(id), op);
	}

	public static void deleteSession(String id) {
		security.remove(id);
		security_users.remove(id);
	}

	public static void newuser(String id, String role) {
		Timer tmp;
		// System.out.println(id + role);
		if (security_users.containsKey(id)) {
			if (getRole(role).permissionAmount() < getRole(security_users.get(id)).permissionAmount())
				security_users.put(id, role);
			tmp = security.get(id);
			tmp.cancel();
		} else {
			security_users.put(id, role);
		}
		tmp = new Timer();
		tmp.schedule(new SessionClean(id), Settings.session_timeout * 60 * 1000);
		security.put(id, tmp);
	}

	public static boolean verifypermission(String role, int op) {
		Role tmp = roledb.get(role);
		return tmp.getPermission(Operations.return_main_permission(op));
	}

	public static Role getRole(String role) {
		if (roledb.containsKey(role)) {
			return roledb.get(role);
		}
		return new Role();
	}

	public static Model getmodel(long id) {
		if (modeldb.containsKey(id))
			return modeldb.get(id);
		LOGGER.log(Level.INFO, "INJECTION ATTEMPT on get model");
		return null;
	}

	public static void addmodel(long id, Model model) {
		modeldb.put(id, model);
	}

	public static void delmodel(long id) {
		modeldb.remove(id);
	}

	public static Collection<Model> dbmodelall() {
		return modeldb.values();
	}

	public static Collection<PSS> dbpssall() {
		return pssdb.values();
	}
	
	public static Collection<DesignProject> dbdpall() {
		return designProjectdb.values();
	}

	public static PSS getpss(long id) {
		if (pssdb.containsKey(id))
			return pssdb.get(id);
		LOGGER.log(Level.INFO, "INJECTION ATTEMPT on get pss");
		return null;
	}
	
	public static DesignProject getDp(long id) {
		if (designProjectdb.containsKey(id))
			return designProjectdb.get(id);
		LOGGER.log(Level.INFO, "INJECTION ATTEMPT on get Designproject");
		return null;
	}
	
	public static User getUser(long id) {
		if (userdb.containsKey(id))
			return userdb.get(id);
		LOGGER.log(Level.INFO, "INJECTION ATTEMPT on get user");
		return null;
	}
	
	public static Company getCompany(long id) {
		if (companydb.containsKey(id))
			return companydb.get(id);
		LOGGER.log(Level.INFO, "INJECTION ATTEMPT on get Company");
		return null;
	}

	public static boolean dbhasservice(long id) {
		return servicedb.containsKey(id);
	}

	public static Product getService(long id) {
		if (dbhasservice(id))
			return servicedb.get(id);
		LOGGER.log(Level.INFO, "INJECTION ATTEMPT on get service");
		return null;
	}

	public static Collection<Product> dbserviceall() {
		return servicedb.values();
	}

	public static boolean dbhasproduct(long id) {
		return productdb.containsKey(id);
	}

	public static Product getProduct(long id) {
		if (dbhasproduct(id))
			return productdb.get(id);
		LOGGER.log(Level.INFO, "INJECTION ATTEMPT on get product");
		return null;
	}

	public static Collection<Product> dbproductall() {
		return productdb.values();
	}

	/**
	 * Identify PSS byproduct.
	 *
	 * @param product
	 *            the product id
	 * @return the long id
	 */
	public static long identifyPSSbyproduct(long product) {
		if (product == 0)
			return 0;
		long id = productdb.get(product).get_Id();
		for (PSS pss : pssdb.values()) {
			if (pss.get_products().contains(id))
				return pss.getID();
		}
		return 0;

	}

	/**
	 * Identify PSS byname.
	 *
	 * @param name
	 *            the name of the pss
	 * @return the long id
	 */
	public static long identifyPSSbyname(String name) {

		for (PSS a : pssdb.values()) {
			if (a.getName().equals(name))
				return a.getID();
		}
		return 0;

	}

	/**
	 * Identify product by message.
	 *
	 * @param message
	 *            the message
	 * @return the long id
	 */
	public static long identifyProduct(String message) {

		for (Product a : productdb.values()) {
			if (a.checkMessage(message))
				return a.get_Id();
		}

		return 0;
	}

}
