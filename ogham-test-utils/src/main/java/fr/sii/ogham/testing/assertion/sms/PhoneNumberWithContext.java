package fr.sii.ogham.testing.assertion.sms;

import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.quote;

import fr.sii.ogham.testing.assertion.context.Context;

/**
 * Context dedicated to a phone number of a particular message.
 * 
 * @author Aurélien Baudet
 *
 */
public class PhoneNumberWithContext implements Context {
	private final PhoneNumberInfo number;
	private final String name;
	private final Context parent;

	/**
	 * @param number
	 *            the phone number
	 * @param name
	 *            a name to identify it in the context (sender or receiver)
	 * @param parent
	 *            the parent context
	 */
	public PhoneNumberWithContext(PhoneNumberInfo number, String name, Context parent) {
		super();
		this.number = number;
		this.name = name;
		this.parent = parent;
	}

	@Override
	public String evaluate(String template) {
		String result = template.replaceAll(quote("${numberName}"), quoteReplacement(name));
		return parent.evaluate(result);
	}

	/**
	 * @return the phone number
	 */
	public PhoneNumberInfo getNumber() {
		return number;
	}
}
