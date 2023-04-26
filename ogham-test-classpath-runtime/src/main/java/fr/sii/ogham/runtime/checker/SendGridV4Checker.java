package fr.sii.ogham.runtime.checker;

import com.github.tomakehurst.wiremock.client.WireMock;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;

public class SendGridV4Checker implements SendGridChecker {
	private final WireMock server;
	
	public SendGridV4Checker(WireMock server) {
		super();
		this.server = server;
	}

	@Override
	public void assertEmailWithoutTemplate() throws IOException {
		server.verifyThat(postRequestedFor(urlEqualTo("/v3/mail/send"))
				.withRequestBody(equalToJson(resourceAsString("/email/sendgrid/expected/no-template.json"), false, true)));
	}
	
	@Override
	public void assertEmailWithThymeleaf() throws IOException {
		server.verifyThat(postRequestedFor(urlEqualTo("/v3/mail/send"))
				.withRequestBody(equalToJson(resourceAsString("/email/sendgrid/expected/thymeleaf.json"), false, true)));
	}

	@Override
	public void assertEmailWithFreemarker() throws IOException {
		server.verifyThat(postRequestedFor(urlEqualTo("/v3/mail/send"))
				.withRequestBody(equalToJson(resourceAsString("/email/sendgrid/expected/freemarker.json"), false, true)));
	}

	@Override
	public void assertEmailWithThymeleafAndFreemarker() throws IOException {
		server.verifyThat(postRequestedFor(urlEqualTo("/v3/mail/send"))
				.withRequestBody(equalToJson(resourceAsString("/email/sendgrid/expected/thymeleaf-and-freemarker.json"), false, true)));
	}
}
