package general;

public class Product {
	
	private long id;
	private String name;
	private long pss_id;
	private boolean is_final_product;
	private long supplied_by;
	private long parent_product_id;
	
	public Product(long _id, String _name, long _pss_id, boolean _is_final_product, long _supplied_by, long _parent_product_id){
		this.id=_id;
		this.name=_name;
		this.pss_id=_pss_id;
		this.is_final_product=_is_final_product;
		this.supplied_by=_supplied_by;
		this.parent_product_id=_parent_product_id;
		
	}
	
	public String get_Name(){
		return name;
	}
	
	public long get_PSS(){
		return pss_id;
	}
	
	public long get_Id(){
		return id;
	}
	
	public boolean getFinal(){
		return is_final_product;
	}
	
	public long getSupplier(){
		return supplied_by;
	}
	
	public long getParent(){
		return parent_product_id;
	}
	
	public boolean checkMessage(String message){
		
		if(message.toLowerCase().contains(this.name.toLowerCase()))
			return true;
		return false;
	}

}
