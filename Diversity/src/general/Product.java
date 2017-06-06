package general;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class Product.
 */
public class Product {
	
	private long id;
	private String name;
	private boolean is_final_product;
	private long supplied_by;
	private long parent_product_id;
	private ArrayList<Long> sub_products;
	
	/**
	 * Instantiates a new product.
	 *
	 * @param _id the id
	 * @param _name the name
	 * @param _pss_id the pss id
	 * @param _is_final_product true if it's final product, false otherwise
	 * @param _supplied_by the supplied by
	 * @param _parent_product_id the parent product id
	 */
	public Product(long _id, String _name, boolean _is_final_product, long _supplied_by, long _parent_product_id){
		this.id=_id;
		this.name=_name;
		this.is_final_product=_is_final_product;
		this.supplied_by=_supplied_by;
		this.parent_product_id=_parent_product_id;
		this.sub_products = new ArrayList<Long>();
		
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String get_Name(){
		return name;
	}
	
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long get_Id(){
		return id;
	}
	
	/**
	 * Gets boolean if final product.
	 *
	 * @return true if final product, false otherwise
	 */
	public boolean getFinal(){
		return is_final_product;
	}
	
	/**
	 * Gets the supplier.
	 *
	 * @return the supplier
	 */
	public long getSupplier(){
		return supplied_by;
	}
	
	/**
	 * Gets the parent product.
	 *
	 * @return the parent
	 */
	public long getParent(){
		return parent_product_id;
	}
	
	public void setParent(long id){
		sub_products.add(id);
	}
	
	/**
	 * Check message.
	 *
	 * @param message the message
	 * @return true, if successful
	 */
	public boolean checkMessage(String message){
		
		if(this.is_final_product == true && message.toLowerCase().contains(this.name.toLowerCase()))
			return true;
		return false;
	}
	
	public ArrayList<Long> getsubproducts(){
		return new ArrayList<>(sub_products);
	}

}