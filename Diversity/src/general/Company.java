package general;

import java.util.ArrayList;

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
	
	private ArrayList<Long> design_projects;
	
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
		this.design_projects= new ArrayList<Long>();
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
	

	public void add_design_project(Long id){
		this.design_projects.add(id);
	}
	
	public ArrayList<Long> get_design_projects(){
		return this.design_projects;
	}
}
