package fr.sii.notification.email.message;


public class EmailAddress {
	private String address;
	
	private String personal;

	public EmailAddress(String address) {
		this(address, null);
	}

	public EmailAddress(String address, String personal) {
		super();
		this.address = address;
		this.personal = personal;
	}

	public String getAddress() {
		return address;
	}

	public String getPersonal() {
		return personal;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if(personal!=null && !personal.isEmpty()) {
			builder.append(personal).append(" ");
		}
		builder.append("<").append(address).append(">");
		return builder.toString();
	}
}
