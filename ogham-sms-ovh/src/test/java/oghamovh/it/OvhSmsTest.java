package oghamovh.it;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.util.PhoneNumberException;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.sms.builder.ovh.OvhSmsBuilder;
import fr.sii.ogham.sms.message.Sender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.OvhSmsSender;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@LogTestInformation
@WireMockTest
public class OvhSmsTest {

	OvhSmsBuilder builder;
	
	@BeforeEach
	public void setUp(WireMockRuntimeInfo wireMock) throws IOException {
		builder = new OvhSmsBuilder();
		builder
			.url("http://localhost:"+wireMock.getHttpPort()+"/cgi-bin/sms/http2sms.cgi")
			.account("sms-nic-foobar42")
			.login("login")
			.password("password")
			.options()
				.noStop(true);
	}

	@Test
	public void simple() throws MessagingException, IOException, InterruptedException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		
		OvhSmsSender sender = builder.build();
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
		
		OvhSmsSender sender = builder.build();
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

	@Test
	public void badRequest() throws MessagingException, IOException, InterruptedException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(400)
						.withHeader("Content-Type", "application/json")));
		
		OvhSmsSender sender = builder.build();
		assertThrows(MessagingException.class, () -> {
			sender.send(new Sms()
					.content("sms content")
					.from(new Sender("0033203040506"))
					.to("0033605040302"));
		});
	}

	@Test
	public void errorInResponse() throws MessagingException, IOException, InterruptedException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ko.json")))));
		
		OvhSmsSender sender = builder.build();
		assertThrows(MessagingException.class, () -> {
			sender.send(new Sms()
					.content("sms content")
					.from(new Sender("0033203040506"))
					.to("0033605040302"));
		});
	}

	@Test
	@Disabled("Not yet implemented")
	public void longMessage() throws MessagingException, IOException {
		// TODO: implement test
		fail("Not yet implemented");
	}

	@Test
	public void severalRecipients() throws MessagingException, IOException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		
		OvhSmsSender sender = builder.build();
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
		
		OvhSmsSender sender = builder.build();
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

	@Test
	public void nationalNumber() throws MessagingException, IOException {
		OvhSmsSender sender = builder.build();
		
		MessageException e = assertThrows(MessageException.class, () -> {
			sender.send(new Sms()
					.content("sms content")
					.from(new Sender("02 03 04 05 06"))
					.to("06 05 04 03 02"));
		}, "should throw");
		assertThat("should indicate cause", e.getCause(), instanceOf(PhoneNumberException.class));
	}

	@Test
	public void customTag() throws MessagingException, IOException {
		stubFor(get(urlMatching(".*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(IOUtils.toString(getClass().getResourceAsStream("/ovh/response/ok.json")))));
		
		builder.options().tag("my-tag");
		OvhSmsSender sender = builder.build();
		sender.send(new Sms()
						.content("sms content")
						.from(new Sender("0033203040506"))
						.to("0033605040302"));
		System.out.println("pouet");

		verify(getRequestedFor(urlPathEqualTo("/cgi-bin/sms/http2sms.cgi"))
					.withQueryParam("account", equalTo("sms-nic-foobar42"))
					.withQueryParam("login", equalTo("login"))
					.withQueryParam("password", equalTo("password"))
					.withQueryParam("noStop", equalTo("1"))
					.withQueryParam("contentType", equalTo("application/json"))
					.withQueryParam("from", equalTo("0033203040506"))
					.withQueryParam("to", equalTo("0033605040302"))
					.withQueryParam("message", equalTo("sms content"))
					.withQueryParam("tag", equalTo("my-tag")));
	}

}
