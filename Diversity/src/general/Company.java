package general;

// TODO: Auto-generated Javadoc
/**
 * The Class Company.
 */
public final class Company {
	
	/** The id. */
	private long id;
	
	/** The name. */
	private String name;
	
	/** The type. */
	private String type;
	
	/** The company id. */
	private long company_id;
	
	/**
	 * Instantiates a new company.
	 *
	 * @param _id the id
	 * @param _name the name
	 * @param _type the type
	 * @param _company_id the company id
	 */
	public Company(long _id, String _name, String _type, long _company_id){
		this.id=_id;
		this.name=_name;
		this.type=_type;
		this.company_id=_company_id;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId(){
		return id;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Gets the company.
	 *
	 * @return the company
	 */
	public long getCompany(){
		return company_id;
	}
}
