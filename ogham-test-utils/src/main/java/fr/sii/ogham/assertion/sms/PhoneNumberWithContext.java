package fr.sii.ogham.assertion.sms;

import java.util.regex.Pattern;

import fr.sii.ogham.assertion.context.Context;

public class PhoneNumberWithContext implements Context {
	private final PhoneNumberInfo number;
	private final String name;
	private final Context parent;
	
	public PhoneNumberWithContext(PhoneNumberInfo number, String name, Context parent) {
		super();
		this.number = number;
		this.name = name;
		this.parent = parent;
	}


	@Override
	public String evaluate(String template) {
		String result = template.replaceAll(Pattern.quote("${numberName}"), name);
		return parent.evaluate(result);
	}


	public PhoneNumberInfo getNumber() {
		return number;
	}
}
