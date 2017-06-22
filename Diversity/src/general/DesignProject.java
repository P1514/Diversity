package general;


// TODO: Auto-generated Javadoc
/**
 * The Class DesignProject.
 */
//Post individual Object
public class DesignProject {
	private long id;
	private long produces_pss_id;
	private String name;
	private long user_id;
	private long time_created;
	private long wiki_id;

	

	public DesignProject(long _id, long _produces_pss_id, String _name, long _user_id, long _time_created, long _wiki_id) {
		this.id=_id;
		this.produces_pss_id=_produces_pss_id;
		this.name=_name;
		this.user_id=_user_id;
		this.time_created=_time_created;
		this.wiki_id=_wiki_id;

	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getID(){
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
	 * Gets the time_created.
	 *
	 * @return the time_created
	 */
	public long getTimeCreated(){
		return time_created;
	}
	
	/**
	 * Gets the produces_pss_id.
	 *
	 * @return the produces_pss_id
	 */
	public long getProducesPssId(){
		return produces_pss_id;
	}
	
	/**
	 * Gets the wiki_id.
	 *
	 * @return the wiki_id
	 */
	public long getwikiId(){
		return wiki_id;
	}
	
	/**
	 * Gets the author.
	 *
	 * @return the author
	 */
	public long getAuthor(){
		return this.user_id;
	}
	
	
}
