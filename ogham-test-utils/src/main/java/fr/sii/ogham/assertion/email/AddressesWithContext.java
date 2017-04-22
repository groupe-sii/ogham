package fr.sii.ogham.assertion.email;

import java.util.List;
import java.util.regex.Pattern;

import javax.mail.internet.InternetAddress;

import fr.sii.ogham.assertion.context.Context;

public class AddressesWithContext implements Context {
	private final List<InternetAddress> addresses;
	private final String field;
	private final Context parent;
	
	public AddressesWithContext(List<InternetAddress> addresses, String field, Context parent) {
		super();
		this.addresses = addresses;
		this.field = field;
		this.parent = parent;
	}
	
	public String evaluate(String template) {
		String result = template.replaceAll(Pattern.quote("${fieldName}"), field);
		return parent.evaluate(result);
	}
	
	public List<InternetAddress> getAddresses() {
		return addresses;
	}
	
}