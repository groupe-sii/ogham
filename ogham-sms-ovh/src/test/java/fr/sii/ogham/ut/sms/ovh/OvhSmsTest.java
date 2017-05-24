package fr.sii.ogham.ut.sms.ovh;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.sms.builder.ovh.OvhSmsBuilder;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.OvhSmsSender;

public class OvhSmsTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public WireMockRule serverRule = new WireMockRule(8079);
	
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
	public void simple() throws MessagingException, IOException, InterruptedException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		sender.send(new Sms()
						.content("sms content")
						.from(new Sender("0033203040506"))
						.to("0033605040302"));
		verify(getRequestedFor(urlPathEqualTo("/cgi-bin/sms/http2sms.cgi"))
					.withQueryParam("account", equalTo("sms-nic-foobar42"))
					.withQueryParam("login", equalTo("login"))
					.withQueryParam("password", equalTo("password"))
					.withQueryParam("noStop", equalTo("1"))
					.withQueryParam("contentType", equalTo("application/json"))
					.withQueryParam("from", equalTo("0033203040506"))
					.withQueryParam("to", equalTo("0033605040302"))
					.withQueryParam("message", equalTo("sms content")));
	}

	@Test
	public void newLines() throws MessagingException, IOException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		sender.send(new Sms()
						.content("sms content\nwith new lines\r\nof all\rtypes")
						.from(new Sender("0033203040506"))
						.to("0033605040302"));
		verify(getRequestedFor(urlPathEqualTo("/cgi-bin/sms/http2sms.cgi"))
					.withQueryParam("account", equalTo("sms-nic-foobar42"))
					.withQueryParam("login", equalTo("login"))
					.withQueryParam("password", equalTo("password"))
					.withQueryParam("noStop", equalTo("1"))
					.withQueryParam("contentType", equalTo("application/json"))
					.withQueryParam("from", equalTo("0033203040506"))
					.withQueryParam("to", equalTo("0033605040302"))
					.withQueryParam("message", equalTo("sms content\rwith new lines\rof all\rtypes")));
	}

	@Test(expected=MessagingException.class)
	public void badRequest() throws MessagingException, IOException, InterruptedException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(400)
						.withHeader("Content-Type", "application/json")));
		sender.send(new Sms()
						.content("sms content")
						.from(new Sender("0033203040506"))
						.to("0033605040302"));
	}

	@Test(expected=MessagingException.class)
	public void errorInResponse() throws MessagingException, IOException, InterruptedException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ko.json")))));
		sender.send(new Sms()
						.content("sms content")
						.from(new Sender("0033203040506"))
						.to("0033605040302"));
	}

	@Test
	@Ignore("Not yet implemented")
	public void longMessage() throws MessagingException, IOException {
		// TODO: implement test
		Assert.fail("Not yet implemented");
	}

	@Test
	public void severalRecipients() throws MessagingException, IOException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		sender.send(new Sms()
						.content("sms content")
						.from(new Sender("0033203040506"))
						.to("0033605040302")
						.to("0033605040303")
						.to("0033605040304"));
		verify(getRequestedFor(urlPathEqualTo("/cgi-bin/sms/http2sms.cgi"))
					.withQueryParam("account", equalTo("sms-nic-foobar42"))
					.withQueryParam("login", equalTo("login"))
					.withQueryParam("password", equalTo("password"))
					.withQueryParam("noStop", equalTo("1"))
					.withQueryParam("contentType", equalTo("application/json"))
					.withQueryParam("from", equalTo("0033203040506"))
					.withQueryParam("to", equalTo("0033605040302,0033605040303,0033605040304"))
					.withQueryParam("message", equalTo("sms content")));
	}

	@Test
	public void phoneNumberConversion() throws MessagingException, IOException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		sender.send(new Sms()
						.content("sms content")
						.from(new Sender("+332 03 04 05 06"))
						.to("+33 6 05 04 03 02")
						.to("+41 44 668 18 00"));
		verify(getRequestedFor(urlPathEqualTo("/cgi-bin/sms/http2sms.cgi"))
					.withQueryParam("account", equalTo("sms-nic-foobar42"))
					.withQueryParam("login", equalTo("login"))
					.withQueryParam("password", equalTo("password"))
					.withQueryParam("noStop", equalTo("1"))
					.withQueryParam("contentType", equalTo("application/json"))
					.withQueryParam("from", equalTo("0033203040506"))
					.withQueryParam("to", equalTo("0033605040302,0041446681800"))
					.withQueryParam("message", equalTo("sms content")));
	}

	@Test(expected=IllegalArgumentException.class)
	public void nationalNumber() throws MessagingException, IOException {
		sender.send(new Sms()
						.content("sms content")
						.from(new Sender("02 03 04 05 06"))
						.to("06 05 04 03 02"));
	}


}
