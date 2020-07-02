package fr.sii.ogham.runtime.checker;

import com.github.tomakehurst.wiremock.WireMockServer;

public interface SendGridCheckerProvider {
	SendGridChecker get(WireMockServer server);
}
