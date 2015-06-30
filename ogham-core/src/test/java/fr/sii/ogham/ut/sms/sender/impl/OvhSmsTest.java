package fr.sii.ogham.ut.sms.sender.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.helper.sms.AssertSms;
import fr.sii.ogham.helper.sms.ExpectedAddressedPhoneNumber;
import fr.sii.ogham.helper.sms.ExpectedSms;
import fr.sii.ogham.helper.sms.SplitSms;
import fr.sii.ogham.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.helper.sms.rule.SmppServerRule;
import fr.sii.ogham.sms.builder.CloudhopperSMPPBuilder;
import fr.sii.ogham.sms.builder.OvhSmsBuilder;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.message.addressing.NumberingPlanIndicator;
import fr.sii.ogham.sms.message.addressing.TypeOfNumber;
import fr.sii.ogham.sms.sender.impl.OvhSmsSender;
import fr.sii.ogham.sms.sender.impl.ovh.OvhAuthParams;
import fr.sii.ogham.sms.sender.impl.ovh.OvhOptions;

public class OvhSmsTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public WireMockRule serverRule = new WireMockRule(8079);
	
	private OvhSmsSender sender;
	
	@Before
	public void setUp() throws IOException {
		sender = new OvhSmsBuilder()
						.withOvhUrl(new URL("http://localhost:"+serverRule.port()+"/cgi-bin/sms/http2sms.cgi"))
						.withAuthParams(new OvhAuthParams("sms-nic-foobar42", "login", "password"))
						.withOptions(new OvhOptions())
						.build();
	}

	@Test
	public void simple() throws MessagingException, IOException, InterruptedException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		sender.send(new Sms("sms content", new Sender("0033203040506"), "0033605040302"));
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
		sender.send(new Sms("sms content\nwith new lines\r\nof all\rtypes", new Sender("0033203040506"), "0033605040302"));
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
		sender.send(new Sms("sms content", new Sender("0033203040506"), "0033605040302"));
	}

	@Test(expected=MessagingException.class)
	public void errorInResponse() throws MessagingException, IOException, InterruptedException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ko.json")))));
		sender.send(new Sms("sms content", new Sender("0033203040506"), "0033605040302"));
	}

	@Test
	public void longMessage() throws MessagingException, IOException {

	}

	@Test
	public void severalRecipients() throws MessagingException, IOException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		sender.send(new Sms("sms content", new Sender("0033203040506"), "0033605040302", "0033605040303", "0033605040304"));
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
		sender.send(new Sms("sms content", new Sender("0203040506"), "0605040302", "446681800"));
		verify(getRequestedFor(urlPathEqualTo("/cgi-bin/sms/http2sms.cgi"))
					.withQueryParam("account", equalTo("sms-nic-foobar42"))
					.withQueryParam("login", equalTo("login"))
					.withQueryParam("password", equalTo("password"))
					.withQueryParam("noStop", equalTo("1"))
					.withQueryParam("contentType", equalTo("application/json"))
					.withQueryParam("from", equalTo("0033203040506"))
					.withQueryParam("to", equalTo("0033605040302,0033605040303,0041446681800"))
					.withQueryParam("message", equalTo("sms content")));
	}


}
