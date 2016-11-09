package general;

public class Company {
	private long id;
	private String name;
	private String type;
	private long company_id;
	
	public Company(long _id, String _name, String _type, long _company_id){
		this.id=_id;
		this.name=_name;
		this.type=_type;
		this.company_id=_company_id;
	}
	
	public long getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
	
	public long getCompany(){
		return company_id;
	}
}
