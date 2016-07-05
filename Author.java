package resources;

//Author Individual Object
public class Author {

	private int id; // Keys
	private String name; // 
	private int influence; // calculada
	private int age;
	private String gender;
	private String location;
	
	
	public Author(int _id, String _name, int _age, String _gender, String _location){
		this.id = _id;
		this.age = _age;
		this.name = _name;
		this.gender = _gender;
		this.location = _location;
		this.influence=0;
		
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public int getInfluence() {
		return influence;
	}
	
	public String getGender(){
		return gender;
	}
	
	public int getAge(){
		return age;
	}
	
	public int getID(){
		return id;
	}
	
	public void calcInfluence() {
		// To DO calcInfluence
	}
	
	
}
