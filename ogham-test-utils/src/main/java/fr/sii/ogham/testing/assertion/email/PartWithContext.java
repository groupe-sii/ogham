package fr.sii.ogham.testing.assertion.email;

import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.quote;

import jakarta.mail.Part;

import fr.sii.ogham.testing.assertion.context.Context;

public class PartWithContext implements Context {
	private final Part part;
	private final String partName;
	private final Context parent;
	public PartWithContext(Part part, String partName, Context parent) {
		super();
		this.part = part;
		this.partName = partName;
		this.parent = parent;
	}
	
	public String evaluate(String template) {
		String result = template.replaceAll(quote("${partName}"), quoteReplacement(partName));
		return parent.evaluate(result);
	}
	
	public Part getPart() {
		return part;
	}
}