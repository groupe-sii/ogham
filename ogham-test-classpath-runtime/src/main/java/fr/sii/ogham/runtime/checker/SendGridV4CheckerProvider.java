package fr.sii.ogham.runtime.checker;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

import com.github.tomakehurst.wiremock.WireMockServer;

public class SendGridV4CheckerProvider implements SendGridCheckerProvider {

	@Override
	public SendGridChecker get(WireMockServer server) {
		server.stubFor(post("/v3/mail/send")
				.willReturn(aResponse().withStatus(202)));
		return new SendGridV4Checker(server);
	}

}
