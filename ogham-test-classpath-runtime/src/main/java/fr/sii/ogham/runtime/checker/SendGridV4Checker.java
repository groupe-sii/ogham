package fr.sii.ogham.runtime.checker;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;

import java.io.IOException;

import com.github.tomakehurst.wiremock.WireMockServer;

public class SendGridV4Checker implements SendGridChecker {
	private final WireMockServer server;
	
	public SendGridV4Checker(WireMockServer server) {
		super();
		this.server = server;
	}

	@Override
	public void assertEmailWithoutTemplate() throws IOException {
		server.verify(postRequestedFor(urlEqualTo("/v3/mail/send"))
				.withRequestBody(equalToJson(resourceAsString("/email/sendgrid/expected/no-template.json"), false, true)));
	}
	
	@Override
	public void assertEmailWithThymeleaf() throws IOException {
		server.verify(postRequestedFor(urlEqualTo("/v3/mail/send"))
				.withRequestBody(equalToJson(resourceAsString("/email/sendgrid/expected/thymeleaf.json"), false, true)));
	}

	@Override
	public void assertEmailWithFreemarker() throws IOException {
		server.verify(postRequestedFor(urlEqualTo("/v3/mail/send"))
				.withRequestBody(equalToJson(resourceAsString("/email/sendgrid/expected/freemarker.json"), false, true)));
	}

	@Override
	public void assertEmailWithThymeleafAndFreemarker() throws IOException {
		server.verify(postRequestedFor(urlEqualTo("/v3/mail/send"))
				.withRequestBody(equalToJson(resourceAsString("/email/sendgrid/expected/thymeleaf-and-freemarker.json"), false, true)));
	}
}
