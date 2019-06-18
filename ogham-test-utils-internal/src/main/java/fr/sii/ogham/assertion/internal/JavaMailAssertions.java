package fr.sii.ogham.assertion.internal;

import static fr.sii.ogham.assertion.AssertionHelper.assertThat;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;

import java.util.Properties;

import org.hamcrest.Matcher;

import fr.sii.ogham.assertion.HasParent;
import fr.sii.ogham.email.sender.impl.JavaMailSender;

/**
 * Helper to mke assertions on {@link JavaMailSender} instance created by Ogham.
 * 
 * @author Aurélien Baudet
 *
 */
public class JavaMailAssertions extends HasParent<MessagingServiceAssertions> {
	private final JavaMailSender javaMailSender;

	public JavaMailAssertions(MessagingServiceAssertions parent, JavaMailSender javaMailSender) {
		super(parent);
		this.javaMailSender = javaMailSender;
	}

	/**
	 * Ensure that host value is correctly configured as expected.
	 * 
	 * @param matcher
	 *            the matcher used to check that host is configured as expected.
	 * @return this instance for fluent chaining
	 */
	public JavaMailAssertions host(Matcher<String> matcher) {
		assertThat(getHost(javaMailSender), matcher);
		return this;
	}

	private String getHost(JavaMailSender javaMailSender) {
		Properties properties = getProperties(javaMailSender);
		return properties.getProperty("mail.smtp.host", properties.getProperty("mail.host"));
	}

	private Properties getProperties(JavaMailSender javaMailSender) {
		try {
			return (Properties) readField(javaMailSender, "properties", true);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to get 'properties' of JavaMailSender", e);
		}
	}
}
