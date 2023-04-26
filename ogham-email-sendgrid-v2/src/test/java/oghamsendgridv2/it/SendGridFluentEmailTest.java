package oghamsendgridv2.it;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.id.generator.IdGenerator;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.context.SimpleContext;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sendgrid.v2.builder.sendgrid.SendGridV2Builder;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.File;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

@LogTestInformation
@MockitoSettings(strictness = LENIENT)
class SendGridFluentEmailTest {
	private static final String SUBJECT = "Example email";
	private static final String CONTENT_TEXT = "This is a default content.";
	private static final String NAME = "you";
	private static final String CONTENT_TEXT_TEMPLATE = "Hello [(${name})]";
	private static final String CONTENT_TEXT_RESULT = "Hello " + NAME;
	private static final String CONTENT_HTML_TEMPLATE = "<html xmlns:th=\"http://www.thymeleaf.org\" th:inline=\"text\"><body><p>Hello [[${name}]]</p></body></html>";
	private static final String CONTENT_HTML_RESULT = "<html><body><p>Hello " + NAME + "</p></body></html>";
	private static final String FROM_ADDRESS = "SENDER <from@example.com>";
	private static final String TO_ADDRESS_1 = "to.1@yopmail.com";
	private static final String TO_ADDRESS_2 = "to.2@yopmail.com";

	@Mock IdGenerator generator;
	
	private MessagingService messagingService;
	private WireMockServer server;

	@BeforeEach
	void setup() {
		when(generator.generate(anyString())).then(AdditionalAnswers.returnsArgAt(0));
		server = new WireMockServer(options().dynamicPort());
		server.start();
		messagingService = MessagingBuilder.standard()
				.email()
					.images()
						.inline()
							.attach()
								.cid()
									.generator(generator)
									.and()
								.and()
							.and()
						.and()
					.sender(SendGridV2Builder.class)
						.url("http://localhost:"+server.port())
						.apiKey("foobar")
						.and()
					.and()
				.build();
	}
	
	@AfterEach
	void clean() {
		server.stop();
	}
	
	@Test
	void bodyString() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.withMultipartRequestBody(aMultipart("from"))
			.willReturn(okForJson("{\"message\": \"success\"}")));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.body().string(CONTENT_TEXT)
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1);
		// @formatter:on
		
		messagingService.send(email);
		
		// @formatter:off
		server.verify(postRequestedFor(urlEqualTo("/api/mail.send.json"))
			.withAnyRequestBodyPart(aMultipart("from").withBody(equalTo("from@example.com")))
			.withAnyRequestBodyPart(aMultipart("fromname").withBody(equalTo("SENDER")))
			.withAnyRequestBodyPart(aMultipart("to[]").withBody(equalTo(TO_ADDRESS_1)))
			.withAnyRequestBodyPart(aMultipart("toname[]").withBody(equalTo("")))
			.withAnyRequestBodyPart(aMultipart("subject").withBody(equalTo(SUBJECT)))
			.withAnyRequestBodyPart(aMultipart("text").withBody(equalTo(CONTENT_TEXT))));
		// @formatter:on
	}
	
	@Test
	void htmlAndTextTemplates() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.withMultipartRequestBody(aMultipart("from"))
			.willReturn(okForJson("{\"message\": \"success\"}")));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.html().templateString(CONTENT_TEXT_TEMPLATE, new SimpleContext("name", NAME))
			.text().templateString(CONTENT_HTML_TEMPLATE, new SimpleContext("name", NAME))
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1, TO_ADDRESS_2);
		// @formatter:on
		
		messagingService.send(email);
		
		// @formatter:off
		server.verify(postRequestedFor(urlEqualTo("/api/mail.send.json"))
			.withAnyRequestBodyPart(aMultipart("from").withBody(equalTo("from@example.com")))
			.withAnyRequestBodyPart(aMultipart("fromname").withBody(equalTo("SENDER")))
			.withAnyRequestBodyPart(aMultipart("to[]").withBody(equalTo(TO_ADDRESS_1)))
			.withAnyRequestBodyPart(aMultipart("to[]").withBody(equalTo(TO_ADDRESS_2)))
			.withAnyRequestBodyPart(aMultipart("toname[]").withBody(equalTo("")))
			.withAnyRequestBodyPart(aMultipart("subject").withBody(equalTo(SUBJECT)))
			.withAnyRequestBodyPart(aMultipart("text").withBody(equalTo(CONTENT_TEXT_RESULT)))
			.withAnyRequestBodyPart(aMultipart("html").withBody(equalTo(CONTENT_HTML_RESULT))));
		// @formatter:on
	}
	
	
	@Test
	void attachResource() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.withMultipartRequestBody(aMultipart("from"))
			.willReturn(okForJson("{\"message\": \"success\"}")));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.body().string(CONTENT_TEXT)
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1)
			.attach().resource("/attachment/04-Java-OOP-Basics.pdf")
			.attach().resource("ogham-grey.png", "/attachment/ogham-grey-900x900.png")
			.attach().resource("ogham-grey.gif", "/attachment/ogham-grey-900x900.png", "image/gif");
		// @formatter:on
		
		messagingService.send(email);
		
		// @formatter:off
		server.verify(postRequestedFor(urlEqualTo("/api/mail.send.json"))
			.withAnyRequestBodyPart(aMultipart("from").withBody(equalTo("from@example.com")))
			.withAnyRequestBodyPart(aMultipart("fromname").withBody(equalTo("SENDER")))
			.withAnyRequestBodyPart(aMultipart("to[]").withBody(equalTo(TO_ADDRESS_1)))
			.withAnyRequestBodyPart(aMultipart("toname[]").withBody(equalTo("")))
			.withAnyRequestBodyPart(aMultipart("subject").withBody(equalTo(SUBJECT)))
			.withAnyRequestBodyPart(aMultipart("text").withBody(equalTo(CONTENT_TEXT)))
			.withAnyRequestBodyPart(aMultipart("files[04-Java-OOP-Basics.pdf]").withBody(equalTo(resourceAsString("/attachment/04-Java-OOP-Basics.pdf"))))
			.withAnyRequestBodyPart(aMultipart("files[ogham-grey.png]").withBody(equalTo(resourceAsString("/attachment/ogham-grey-900x900.png"))))
			.withAnyRequestBodyPart(aMultipart("files[ogham-grey.gif]").withBody(equalTo(resourceAsString("/attachment/ogham-grey-900x900.png")))));
			// TODO: how to set content-type using SendGrid v2 ?
		// @formatter:on
	}
	
	@Test
	void attachFile() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.withMultipartRequestBody(aMultipart("from"))
			.willReturn(okForJson("{\"message\": \"success\"}")));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.body().string(CONTENT_TEXT)
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1)
			.attach().file(new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile()))
			.attach().file("ogham-grey.png", new File(getClass().getResource("/attachment/ogham-grey-900x900.png").getFile()))
			.attach().file("ogham-grey.gif", new File(getClass().getResource("/attachment/ogham-grey-900x900.png").getFile()), "image/gif");
		// @formatter:on
		
		messagingService.send(email);
		
		// @formatter:off
		server.verify(postRequestedFor(urlEqualTo("/api/mail.send.json"))
			.withAnyRequestBodyPart(aMultipart("from").withBody(equalTo("from@example.com")))
			.withAnyRequestBodyPart(aMultipart("fromname").withBody(equalTo("SENDER")))
			.withAnyRequestBodyPart(aMultipart("to[]").withBody(equalTo(TO_ADDRESS_1)))
			.withAnyRequestBodyPart(aMultipart("toname[]").withBody(equalTo("")))
			.withAnyRequestBodyPart(aMultipart("subject").withBody(equalTo(SUBJECT)))
			.withAnyRequestBodyPart(aMultipart("text").withBody(equalTo(CONTENT_TEXT)))
			.withAnyRequestBodyPart(aMultipart("files[04-Java-OOP-Basics.pdf]").withBody(equalTo(resourceAsString("/attachment/04-Java-OOP-Basics.pdf"))))
			.withAnyRequestBodyPart(aMultipart("files[ogham-grey.png]").withBody(equalTo(resourceAsString("/attachment/ogham-grey-900x900.png"))))
			.withAnyRequestBodyPart(aMultipart("files[ogham-grey.gif]").withBody(equalTo(resourceAsString("/attachment/ogham-grey-900x900.png")))));
			// TODO: how to set content-type using SendGrid v2 ?
		// @formatter:on
	}
	
}
