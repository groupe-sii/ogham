package fr.sii.ogham.testing.assertion.email;

import java.util.regex.Pattern;

import javax.mail.Part;

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
		String result = template.replaceAll(Pattern.quote("${partName}"), partName);
		return parent.evaluate(result);
	}
	
	public Part getPart() {
		return part;
	}
}