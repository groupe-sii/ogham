package fr.sii.ogham.runtime.checker;

import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

public class SendGridV4CheckerProvider implements SendGridCheckerProvider {

	@Override
	public SendGridChecker get(WireMock server) {
		server.register(post("/v3/mail/send")
				.willReturn(aResponse().withStatus(202)));
		return new SendGridV4Checker(server);
	}

}
