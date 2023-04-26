package fr.sii.ogham.runtime.checker;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

public class SendGridV2CheckerProvider implements SendGridCheckerProvider {

	@Override
	public SendGridChecker get(WireMock server) {
		server.register(post("/api/mail.send.json")
			.withMultipartRequestBody(aMultipart("from"))
			.willReturn(okForJson("{\"message\": \"success\"}")));
		return new SendGridV2Checker(server);
	}

}
