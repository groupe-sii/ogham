package fr.sii.ogham.runtime.checker;

import com.github.tomakehurst.wiremock.WireMockServer;

import fr.sii.ogham.core.util.ClasspathUtils;

public class AutoSendGridCheckerProvider implements SendGridCheckerProvider {

	@Override
	public SendGridChecker get(WireMockServer server) {
		if (ClasspathUtils.exists("com.sendgrid.SendGrid") && ClasspathUtils.exists("com.sendgrid.SendGridAPI")) {
			return new SendGridV4CheckerProvider().get(server);
		}
		if (ClasspathUtils.exists("com.sendgrid.SendGrid") && ClasspathUtils.exists("com.sendgrid.SendGrid$Email")) {
			return new SendGridV2CheckerProvider().get(server);
		}
		throw new IllegalStateException("unknown SendGrid version => can't provide SendGridChecker");
	}

}
