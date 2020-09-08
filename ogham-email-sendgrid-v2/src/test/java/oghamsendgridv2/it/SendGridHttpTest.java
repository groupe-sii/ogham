package oghamsendgridv2.it;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static fr.sii.ogham.testing.assertion.wiremock.WireMockMatchers.similarHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

import java.io.File;
import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.sendgrid.SendGridException;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.id.generator.IdGenerator;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.context.SimpleContext;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sendgrid.v2.builder.sendgrid.SendGridV2Builder;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import mock.context.SimpleBean;

@LogTestInformation
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class SendGridHttpTest {
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
	void simpleEmail() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.withMultipartRequestBody(aMultipart("from"))
			.willReturn(okForJson("{\"message\": \"success\"}")));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(CONTENT_TEXT)
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
	void templatedEmail() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.withMultipartRequestBody(aMultipart("from"))
			.willReturn(okForJson("{\"message\": \"success\"}")));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(new MultiContent(
					new TemplateContent("string:"+CONTENT_TEXT_TEMPLATE, new SimpleContext("name", NAME)),
					new TemplateContent("string:"+CONTENT_HTML_TEMPLATE, new SimpleContext("name", NAME))))
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
	void emailWithAttachments() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.withMultipartRequestBody(aMultipart("from"))
			.willReturn(okForJson("{\"message\": \"success\"}")));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(CONTENT_TEXT)
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1)
			.attach(new Attachment(new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile())),
					new Attachment(new File(getClass().getResource("/attachment/ogham-grey-900x900.png").getFile())));
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
			.withAnyRequestBodyPart(aMultipart("files[ogham-grey-900x900.png]").withBody(equalTo(resourceAsString("/attachment/ogham-grey-900x900.png")))));
		// @formatter:on
	}
	
	@Test
	void authenticationFailed() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.willReturn(aResponse()
					.withStatus(400)
					.withBody(loadJson("/stubs/responses/authenticationFailed.json"))));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(CONTENT_TEXT)
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1);
		// @formatter:on
		
		MessagingException e = assertThrows(MessagingException.class, () -> {
			messagingService.send(email);
		}, "throws message exception");
		assertThat("sendgrid exception", e.getCause(), allOf(notNullValue(), instanceOf(SendGridException.class)));
		assertThat("root cause", e.getCause().getCause(), allOf(notNullValue(), instanceOf(IOException.class)));
		assertThat("sendgrid message", e.getCause().getCause().getMessage(), Matchers.equalTo("Sending to SendGrid failed: (400) {\n" + 
				"	\"errors\": [\"The provided authorization grant is invalid, expired, or revoked\"],\n" + 
				"	\"message\": \"error\"\n" + 
				"}"));
	}
	
	@Test
	void templatedEmailWithInlinedImages() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/api/mail.send.json")
			.withMultipartRequestBody(aMultipart("from"))
			.willReturn(okForJson("{\"message\": \"success\"}")));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(new MultiTemplateContent("/template/freemarker/source/relative_resources", new SimpleBean("foo", 42)))
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
			.withAnyRequestBodyPart(aMultipart("text").withBody(equalTo(resourceAsString("/template/freemarker/expected/resources_foo_42.txt"))))
			.withAnyRequestBodyPart(aMultipart("html").withBody(similarHtml(resourceAsString("/template/freemarker/expected/resources_foo_42.html"))))
			.withAnyRequestBodyPart(aMultipart("files[fb.gif]").withBody(equalTo(resourceAsString("/template/freemarker/source/images/fb.gif"))))
			.withAnyRequestBodyPart(aMultipart("files[h1.gif]").withBody(equalTo(resourceAsString("/template/freemarker/source/images/h1.gif"))))
			.withAnyRequestBodyPart(aMultipart("files[left.gif]").withBody(equalTo(resourceAsString("/template/freemarker/source/images/left.gif"))))
			.withAnyRequestBodyPart(aMultipart("files[right1.gif]").withBody(equalTo(resourceAsString("/template/freemarker/source/images/right1.gif"))))
			.withAnyRequestBodyPart(aMultipart("files[tw.gif]").withBody(equalTo(resourceAsString("/template/freemarker/source/images/tw.gif"))))
			.withAnyRequestBodyPart(aMultipart("content[fb.gif]").withBody(equalTo("fb.gif")))
			.withAnyRequestBodyPart(aMultipart("content[h1.gif]").withBody(equalTo("h1.gif")))
			.withAnyRequestBodyPart(aMultipart("content[left.gif]").withBody(equalTo("left.gif")))
			.withAnyRequestBodyPart(aMultipart("content[right1.gif]").withBody(equalTo("right1.gif")))
			.withAnyRequestBodyPart(aMultipart("content[tw.gif]").withBody(equalTo("tw.gif"))));
		// @formatter:on
	}
	
	
	private String loadJson(String path) throws IOException, JsonParseException, JsonMappingException {
		return IOUtils.toString(getClass().getResourceAsStream(path));
	}
	
}
