package fr.sii.ogham.runtime.checker;

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static fr.sii.ogham.testing.assertion.wiremock.WireMockMatchers.similarHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;

import java.io.IOException;

import com.github.tomakehurst.wiremock.WireMockServer;

public class SendGridV2Checker implements SendGridChecker {
	private final WireMockServer server;
	
	public SendGridV2Checker(WireMockServer server) {
		super();
		this.server = server;
	}

	@Override
	public void assertEmailWithoutTemplate() throws IOException {
		server.verify(postRequestedFor(urlEqualTo("/api/mail.send.json"))
				.withAnyRequestBodyPart(aMultipart("from").withBody(equalTo("sender@sii.fr")))
				.withAnyRequestBodyPart(aMultipart("fromname").withBody(equalTo("Sender Name")))
				.withAnyRequestBodyPart(aMultipart("to[]").withBody(equalTo("recipient@sii.fr")))
				.withAnyRequestBodyPart(aMultipart("toname[]").withBody(equalTo("Recipient Name")))
				.withAnyRequestBodyPart(aMultipart("subject").withBody(equalTo("Simple")))
				.withAnyRequestBodyPart(aMultipart("text").withBody(equalTo("string body"))));
	}
	
	@Override
	public void assertEmailWithThymeleaf() throws IOException {
		assertEmailWithTemplates("Thymeleaf", "thymeleaf");
	}

	@Override
	public void assertEmailWithFreemarker() throws IOException {
		assertEmailWithTemplates("Freemarker", "freemarker");
	}

	@Override
	public void assertEmailWithThymeleafAndFreemarker() throws IOException {
		assertEmailWithTemplates("Thymeleaf+Freemarker", "mixed");
	}
	
	private void assertEmailWithTemplates(String subject, String templateEngine) throws IOException {
		server.verify(postRequestedFor(urlEqualTo("/api/mail.send.json"))
				.withAnyRequestBodyPart(aMultipart("from").withBody(equalTo("sender@sii.fr")))
				.withAnyRequestBodyPart(aMultipart("fromname").withBody(equalTo("Sender Name")))
				.withAnyRequestBodyPart(aMultipart("to[]").withBody(equalTo("recipient@sii.fr")))
				.withAnyRequestBodyPart(aMultipart("toname[]").withBody(equalTo("Recipient Name")))
				.withAnyRequestBodyPart(aMultipart("subject").withBody(equalTo(subject)))
				.withAnyRequestBodyPart(aMultipart("text").withBody(equalTo(resourceAsString("/email/"+templateEngine+"/expected/simple_foo_42.txt"))))
				.withAnyRequestBodyPart(aMultipart("html").withBody(similarHtml(resourceAsString("/email/"+templateEngine+"/expected/simple_foo_42.html")))));
	}
}
