package fr.sii.ogham.runtime.checker;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;

import java.io.IOException;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.*;

public class JavaMailChecker {
	private final GreenMailExtension greenMail;
	
	public JavaMailChecker(GreenMailExtension greenMail) {
		super();
		this.greenMail = greenMail;
	}

	public void assertEmailWithoutTemplate() {
		assertThat(greenMail).receivedMessages()
			.count(is(1))
				.message(0)
					.subject(is("Simple"))
					.from()
						.address(hasItems("sender@sii.fr"))
						.personal(hasItems("Sender Name")).and()
					.to()
						.address(hasItems("recipient@sii.fr"))
						.personal(hasItems("Recipient Name")).and()
					.body()
						.contentAsString(is("string body"))
						.contentType(startsWith("text/plain")).and()
					.alternative(nullValue())
					.attachments(emptyIterable());
	}
	
	public void assertEmailWithThymeleaf() throws IOException {
		assertEmailWithTemplates("Thymeleaf", "thymeleaf");
	}

	public void assertEmailWithFreemarker() throws IOException {
		assertEmailWithTemplates("Freemarker", "freemarker");
	}

	public void assertEmailWithThymeleafAndFreemarker() throws IOException {
		assertEmailWithTemplates("Thymeleaf+Freemarker", "mixed");
	}
	
	private void assertEmailWithTemplates(String subject, String templateEngine) throws IOException {
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is(subject))
				.from()
					.address(hasItems("sender@sii.fr"))
					.personal(hasItems("Sender Name")).and()
				.to()
					.address(hasItems("recipient@sii.fr"))
					.personal(hasItems("Recipient Name")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/email/"+templateEngine+"/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(is(resourceAsString("/email/"+templateEngine+"/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
	}
}
