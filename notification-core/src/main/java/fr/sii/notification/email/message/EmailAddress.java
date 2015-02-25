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
		builder.append("EmailAddress [address=").append(address).append(", personal=").append(personal).append("]");
		return builder.toString();
	}
}
