package fr.sii.ogham.testing.assertion.email;

import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.quote;

import java.util.List;

import javax.mail.internet.InternetAddress;

import fr.sii.ogham.testing.assertion.context.Context;

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
		String result = template.replaceAll(quote("${fieldName}"), quoteReplacement(field));
		return parent.evaluate(result);
	}
	
	public List<InternetAddress> getAddresses() {
		return addresses;
	}
	
}