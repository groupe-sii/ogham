package fr.sii.ogham.runtime.checker;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

public interface SendGridCheckerProvider {
	SendGridChecker get(WireMock server);
}
