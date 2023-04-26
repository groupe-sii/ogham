package oghamsendgridv4.it;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.id.generator.IdGenerator;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.context.SimpleContext;
import fr.sii.ogham.core.util.Base64Utils;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sendgrid.sender.exception.SendGridException;
import fr.sii.ogham.email.sendgrid.v4.builder.sendgrid.SendGridV4Builder;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import mock.context.SimpleBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.File;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static fr.sii.ogham.email.attachment.ContentDisposition.INLINE;
import static fr.sii.ogham.testing.assertion.wiremock.WireMockMatchers.similarHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

@LogTestInformation
@MockitoSettings(strictness = LENIENT)
class SendGridHttpTest {
	private static final String SUBJECT = "Example email";
	private static final String CONTENT_TEXT = "This is a default content.";
	private static final String NAME = "you";
	private static final String CONTENT_TEXT_TEMPLATE = "Hello [(${name})]";
	private static final String CONTENT_HTML_TEMPLATE = "<html xmlns:th=\"http://www.thymeleaf.org\" th:inline=\"text\"><body><p>Hello [[${name}]]</p></body></html>";
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
					.sender(SendGridV4Builder.class)
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
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse().withStatus(202)));
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
		server.verify(postRequestedFor(urlEqualTo("/v3/mail/send"))
			.withRequestBody(equalToJson(resourceAsString("/expected/requests/simpleEmail.json"), true, true)));
		// @formatter:on
	}
	
	@Test
	void recipients() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse().withStatus(202)));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(CONTENT_TEXT)
			.from(FROM_ADDRESS)
			.to("to.1@yopmail.com", "to.2@yopmail.com")
			.cc("cc.1@yopmail.com", "cc.2@yopmail.com")
			.bcc("bcc.1@yopmail.com", "bcc.2@yopmail.com");
		// @formatter:on
		
		messagingService.send(email);
		
		// @formatter:off
		server.verify(postRequestedFor(urlEqualTo("/v3/mail/send"))
			.withRequestBody(equalToJson(resourceAsString("/expected/requests/recipients.json"), true, true)));
		// @formatter:on
	}

	@Test
	void templatedEmail() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse().withStatus(202)));
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
		server.verify(postRequestedFor(urlEqualTo("/v3/mail/send"))
			.withRequestBody(equalToJson(resourceAsString("/expected/requests/templatedEmail.json"), true, true)));
		// @formatter:on
	}
	
	
	@Test
	void emailWithAttachments() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse().withStatus(202)));
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
		server.verify(postRequestedFor(urlEqualTo("/v3/mail/send"))
			.withRequestBody(equalToJson(resourceAsString("/expected/requests/withAttachments.json"), true, true)));
		// @formatter:on
	}
	
	@Test
	void authenticationFailed() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse()
					.withStatus(401)
					.withBody(resourceAsString("/stubs/responses/authenticationFailed.json"))));
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
		}, "root exception");
		
		assertThat("cause", e.getCause(), allOf(notNullValue(), instanceOf(SendGridException.class)));
		assertThat("sub cause", e.getCause().getCause(), allOf(notNullValue(), instanceOf(IOException.class)));
		assertThat("sub cause message", e.getCause().getCause().getMessage().replaceAll("\\r", ""), equalTo("Sending to SendGrid failed: (401) {\n" + 
				"  \"errors\": [\n" + 
				"    {\n" + 
				"      \"message\": \"The provided authorization grant is invalid, expired, or revoked\",\n" + 
				"      \"field\": null,\n" + 
				"      \"help\": null\n" + 
				"    }\n" + 
				"  ]\n" + 
				"}"));
	}
	
	@Test
	void invalidRequest() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse()
					.withStatus(400)
					.withBody(resourceAsString("/stubs/responses/attachmentDispositionError.json"))));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(CONTENT_TEXT)
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1)
			.attach(new Attachment(new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile()), "", "INVALID_DISPOSITION"));
		// @formatter:on
		
		MessagingException e = assertThrows(MessagingException.class, () -> {
			messagingService.send(email);
		}, "root exception");
		
		// @formatter:off
		server.verify(postRequestedFor(urlEqualTo("/v3/mail/send"))
				.withRequestBody(equalToJson(resourceAsString("/expected/requests/invalidRequest.json"), true, true)));
		// @formatter:on
		
		assertThat("cause", e.getCause(), allOf(notNullValue(), instanceOf(SendGridException.class)));
		assertThat("sub cause", e.getCause().getCause(), allOf(notNullValue(), instanceOf(IOException.class)));
		assertThat("sub cause message", e.getCause().getCause().getMessage().replaceAll("\\r", ""), equalTo("Sending to SendGrid failed: (400) {\n" + 
				"  \"errors\": [\n" + 
				"    {\n" + 
				"      \"message\": \"The disposition of your attachment can be either 'inline' or 'attachment'.\",\n" + 
				"      \"field\": \"attachments.0.disposition\",\n" + 
				"      \"help\": \"http://sendgrid.com/docs/API_Reference/Web_API_v3/Mail/errors.html#message.attachments.disposition\"\n" + 
				"    }\n" + 
				"  ]\n" + 
				"}"));
	}
	
	@Test
	void tooManyRequests() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse()
					.withStatus(429)
					.withBody(resourceAsString("/stubs/responses/tooManyRequests.json"))));
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
		}, "root exception");
		assertThat("cause", e.getCause(), allOf(notNullValue(), instanceOf(SendGridException.class)));
		assertThat("sub cause", e.getCause().getCause(), allOf(notNullValue(), instanceOf(IOException.class)));
		assertThat("sub cause message", e.getCause().getCause().getMessage().replaceAll("\\r", ""), equalTo("Sending to SendGrid failed: (429) {\n" + 
				"  \"errors\": [\n" + 
				"    {\n" + 
				"      \"field\": null,\n" + 
				"       \"message\": \"too many requests\"\n" + 
				"    },\n" + 
				"  ]\n" + 
				"}"));
	}
	
	@Test
	void internalServerError() throws MessagingException {
		// @formatter:off
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse().withStatus(500)));
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
		}, "message exception");
		assertThat("cause", e.getCause(), allOf(notNullValue(), instanceOf(SendGridException.class)));
	}
	
	@Test
	void templatedEmailWithInlinedImages() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse().withStatus(202)));
		// @formatter:on
		// @formatter:off
		Email email = new Email()
			.subject(SUBJECT)
			.content(new MultiTemplateContent("/template/freemarker/source/relative_resources", new SimpleBean("foo", 42)))
			.from(FROM_ADDRESS)
			.to(TO_ADDRESS_1)
			.cc(TO_ADDRESS_2);
		// @formatter:on
		
		messagingService.send(email);
		
		// @formatter:off
		server.verify(postRequestedFor(urlEqualTo("/v3/mail/send"))
			.withRequestBody(matchingJsonPath("from.name", WireMock.equalTo("SENDER")))
			.withRequestBody(matchingJsonPath("from.email", WireMock.equalTo("from@example.com")))
			.withRequestBody(matchingJsonPath("subject", WireMock.equalTo("Example email")))
			.withRequestBody(matchingJsonPath("personalizations[0].to[0].email", WireMock.equalTo("to.1@yopmail.com")))
			.withRequestBody(matchingJsonPath("personalizations[0].cc[0].email", WireMock.equalTo("to.2@yopmail.com")))
			.withRequestBody(matchingJsonPath("content[0].type", WireMock.equalTo("text/plain")))
			.withRequestBody(matchingJsonPath("content[0].value", WireMock.equalTo("foo 42")))
			.withRequestBody(matchingJsonPath("content[1].type", WireMock.equalTo("text/html")))
			.withRequestBody(matchingJsonPath("content[1].value", similarHtml(resourceAsString("/template/freemarker/expected/resources_foo_42.html"))))
			.withRequestBody(matchingJsonPath("attachments[0].content", WireMock.equalTo(Base64Utils.encodeToString(resource("/template/freemarker/source/images/h1.gif")))))
			.withRequestBody(matchingJsonPath("attachments[0].type", WireMock.equalTo("image/gif")))
			.withRequestBody(matchingJsonPath("attachments[0].filename", WireMock.equalTo("h1.gif")))
			.withRequestBody(matchingJsonPath("attachments[0].disposition", WireMock.equalTo(INLINE)))
			.withRequestBody(matchingJsonPath("attachments[0].content_id", WireMock.equalTo("h1.gif")))
			.withRequestBody(matchingJsonPath("attachments[1].content", WireMock.equalTo(Base64Utils.encodeToString(resource("/template/freemarker/source/images/left.gif")))))
			.withRequestBody(matchingJsonPath("attachments[1].type", WireMock.equalTo("image/gif")))
			.withRequestBody(matchingJsonPath("attachments[1].filename", WireMock.equalTo("left.gif")))
			.withRequestBody(matchingJsonPath("attachments[1].disposition", WireMock.equalTo(INLINE)))
			.withRequestBody(matchingJsonPath("attachments[1].content_id", WireMock.equalTo("left.gif")))
			.withRequestBody(matchingJsonPath("attachments[2].content", WireMock.equalTo(Base64Utils.encodeToString(resource("/template/freemarker/source/images/right1.gif")))))
			.withRequestBody(matchingJsonPath("attachments[2].type", WireMock.equalTo("image/gif")))
			.withRequestBody(matchingJsonPath("attachments[2].filename", WireMock.equalTo("right1.gif")))
			.withRequestBody(matchingJsonPath("attachments[2].disposition", WireMock.equalTo(INLINE)))
			.withRequestBody(matchingJsonPath("attachments[2].content_id", WireMock.equalTo("right1.gif")))
			.withRequestBody(matchingJsonPath("attachments[3].content", WireMock.equalTo(Base64Utils.encodeToString(resource("/template/freemarker/source/images/tw.gif")))))
			.withRequestBody(matchingJsonPath("attachments[3].type", WireMock.equalTo("image/gif")))
			.withRequestBody(matchingJsonPath("attachments[3].filename", WireMock.equalTo("tw.gif")))
			.withRequestBody(matchingJsonPath("attachments[3].disposition", WireMock.equalTo(INLINE)))
			.withRequestBody(matchingJsonPath("attachments[3].content_id", WireMock.equalTo("tw.gif")))
			.withRequestBody(matchingJsonPath("attachments[4].content", WireMock.equalTo(Base64Utils.encodeToString(resource("/template/freemarker/source/images/fb.gif")))))
			.withRequestBody(matchingJsonPath("attachments[4].type", WireMock.equalTo("image/gif")))
			.withRequestBody(matchingJsonPath("attachments[4].filename", WireMock.equalTo("fb.gif")))
			.withRequestBody(matchingJsonPath("attachments[4].disposition", WireMock.equalTo(INLINE)))
			.withRequestBody(matchingJsonPath("attachments[4].content_id", WireMock.equalTo("fb.gif"))));
		// @formatter:on
	}
	
}
