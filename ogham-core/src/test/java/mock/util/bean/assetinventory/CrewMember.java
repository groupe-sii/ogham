package mock.util.bean.assetinventory;

public class CrewMember {
	private String firstName;
	private String lastName;
	protected Integer age;
	
	
	public CrewMember() {
		super();
	}
	
	public CrewMember(String firstName, String lastName) {
		this(firstName, lastName, null);
	}
	
	public CrewMember(String firstName, String lastName, Integer age) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
}