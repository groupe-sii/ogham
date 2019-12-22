package fr.sii.ogham.it.sms.ovh;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.sms.builder.ovh.OvhSmsBuilder;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.OvhSmsSender;

public class SmsCodingTest {
	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public WireMockRule serverRule = new WireMockRule(wireMockConfig().dynamicPort());
	
	private OvhSmsSender sender;
	
	@Before
	public void setUp() throws IOException {
		sender = new OvhSmsBuilder()
						.url("http://localhost:"+serverRule.port()+"/cgi-bin/sms/http2sms.cgi")
						.account("sms-nic-foobar42")
						.login("login")
						.password("password")
						.options()
							.noStop(true)
							.and()
						.build();
	}

	@Test
	public void gsm7BasicCharacters() throws MessagingException, IOException, InterruptedException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		sender.send(new Sms()
						.content("abcdefghijklmnopqrstuvwxyz0123456789 @£$¥\nØø\rΔ_ΦΓΛΩΠΨΣΘΞÆæß!\"#¤%&'()*+,-./:;<=>?¡¿§ èéùìòÇÅåÉÄÖÑÜäöñüà")
						.from(new Sender("0033203040506"))
						.to("0033605040302"));
		verify(getRequestedFor(urlPathEqualTo("/cgi-bin/sms/http2sms.cgi"))
					.withQueryParam("account", equalTo("sms-nic-foobar42"))
					.withQueryParam("login", equalTo("login"))
					.withQueryParam("password", equalTo("password"))
					.withQueryParam("noStop", equalTo("1"))
					.withQueryParam("contentType", equalTo("application/json"))
					.withQueryParam("smsCoding", equalTo("1"))
					.withQueryParam("from", equalTo("0033203040506"))
					.withQueryParam("to", equalTo("0033605040302"))
					// /!\ OVH only accepts \r (\n character is replaced by \r)
					.withQueryParam("message", equalTo("abcdefghijklmnopqrstuvwxyz0123456789 @£$¥\rØø\rΔ_ΦΓΛΩΠΨΣΘΞÆæß!\"#¤%&'()*+,-./:;<=>?¡¿§ èéùìòÇÅåÉÄÖÑÜäöñüà")));
	}

	@Test
	public void gsm7ExtensionCharacters() throws MessagingException, IOException, InterruptedException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		sender.send(new Sms()
						.content("|^€{}[~]\\")
						.from(new Sender("0033203040506"))
						.to("0033605040302"));
		verify(getRequestedFor(urlPathEqualTo("/cgi-bin/sms/http2sms.cgi"))
					.withQueryParam("account", equalTo("sms-nic-foobar42"))
					.withQueryParam("login", equalTo("login"))
					.withQueryParam("password", equalTo("password"))
					.withQueryParam("noStop", equalTo("1"))
					.withQueryParam("contentType", equalTo("application/json"))
					.withQueryParam("smsCoding", equalTo("1"))
					.withQueryParam("from", equalTo("0033203040506"))
					.withQueryParam("to", equalTo("0033605040302"))
					.withQueryParam("message", equalTo("|^€{}[~]\\")));
	}


	@Test
	public void unicode() throws MessagingException, IOException, InterruptedException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		sender.send(new Sms()
						.content("hôpital")
						.from(new Sender("0033203040506"))
						.to("0033605040302"));
		verify(getRequestedFor(urlPathEqualTo("/cgi-bin/sms/http2sms.cgi"))
					.withQueryParam("account", equalTo("sms-nic-foobar42"))
					.withQueryParam("login", equalTo("login"))
					.withQueryParam("password", equalTo("password"))
					.withQueryParam("noStop", equalTo("1"))
					.withQueryParam("contentType", equalTo("application/json"))
					.withQueryParam("smsCoding", equalTo("2"))
					.withQueryParam("from", equalTo("0033203040506"))
					.withQueryParam("to", equalTo("0033605040302"))
					.withQueryParam("message", equalTo("hôpital")));
	}

}
