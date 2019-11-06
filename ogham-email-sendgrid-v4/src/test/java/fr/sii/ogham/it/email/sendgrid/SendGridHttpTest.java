package fr.sii.ogham.it.email.sendgrid;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tomakehurst.wiremock.WireMockServer;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.context.SimpleContext;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sendgrid.sender.exception.SendGridException;
import fr.sii.ogham.email.sendgrid.v4.builder.sendgrid.SendGridV4Builder;
import fr.sii.ogham.junit.LoggingTestExtension;

@Extensions(@ExtendWith(LoggingTestExtension.class))
public class SendGridHttpTest {
	private static final String SUBJECT = "Example email";
	private static final String CONTENT_TEXT = "This is a default content.";
	private static final String NAME = "you";
	private static final String CONTENT_TEXT_TEMPLATE = "Hello [(${name})]";
	private static final String CONTENT_HTML_TEMPLATE = "<html xmlns:th=\"http://www.thymeleaf.org\" th:inline=\"text\"><body><p>Hello [[${name}]]</p></body></html>";
	private static final String FROM_ADDRESS = "SENDER <from@example.com>";
	private static final String TO_ADDRESS_1 = "to.1@yopmail.com";
	private static final String TO_ADDRESS_2 = "to.2@yopmail.com";

	private MessagingService messagingService;
	private WireMockServer server;

	@BeforeEach
	public void setup() {
		server = new WireMockServer(options().dynamicPort());
		server.start();
		messagingService = MessagingBuilder.standard()
				.email()
					.sender(SendGridV4Builder.class)
						.url("http://localhost:"+server.port())
						.apiKey("foobar")
						.and()
					.and()
				.build();
	}
	
	@AfterEach
	public void clean() {
		server.stop();
	}
	
	@Test
	public void simpleEmail() throws MessagingException, JsonParseException, JsonMappingException, IOException {
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
			.withRequestBody(equalToJson(loadJson("/expected/requests/simpleEmail.json"), true, true)));
		// @formatter:on
	}
	
	@Test
	public void templatedEmail() throws MessagingException, JsonParseException, JsonMappingException, IOException {
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
			.withRequestBody(equalToJson(loadJson("/expected/requests/templatedEmail.json"), true, true)));
		// @formatter:on
	}
	
	
	@Test
	public void emailWithAttachments() throws MessagingException, JsonParseException, JsonMappingException, IOException {
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
			.withRequestBody(equalToJson(loadJson("/expected/requests/withAttachments.json"), true, true)));
		// @formatter:on
	}
	
	@Test
	public void authenticationFailed() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse()
					.withStatus(401)
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
		});
		
		assertThat(e.getCause(), allOf(notNullValue(), instanceOf(SendGridException.class)));
		assertThat(e.getCause().getCause(), allOf(notNullValue(), instanceOf(IOException.class)));
		assertThat(e.getCause().getCause().getMessage(), equalTo("Request returned status Code 401Body:{\n" + 
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
	public void invalidRequest() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse()
					.withStatus(400)
					.withBody(loadJson("/stubs/responses/attachmentDispositionError.json"))));
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
		});
		
		// @formatter:off
		server.verify(postRequestedFor(urlEqualTo("/v3/mail/send"))
				.withRequestBody(equalToJson(loadJson("/expected/requests/invalidRequest.json"), true, true)));
		// @formatter:on
		
		assertThat(e.getCause(), allOf(notNullValue(), instanceOf(SendGridException.class)));
		assertThat(e.getCause().getCause(), allOf(notNullValue(), instanceOf(IOException.class)));
		assertThat(e.getCause().getCause().getMessage(), equalTo("Request returned status Code 400Body:{\n" + 
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
	public void tooManyRequests() throws MessagingException, JsonParseException, JsonMappingException, IOException {
		// @formatter:off
		server.stubFor(post("/v3/mail/send")
			.willReturn(aResponse()
					.withStatus(429)
					.withBody(loadJson("/stubs/responses/tooManyRequests.json"))));
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
		});
		assertThat(e.getCause(), allOf(notNullValue(), instanceOf(SendGridException.class)));
		assertThat(e.getCause().getCause(), allOf(notNullValue(), instanceOf(IOException.class)));
		assertThat(e.getCause().getCause().getMessage(), equalTo("Request returned status Code 429Body:{\n" + 
				"  \"errors\": [\n" + 
				"    {\n" + 
				"      \"field\": null,\n" + 
				"       \"message\": \"too many requests\"\n" + 
				"    },\n" + 
				"  ]\n" + 
				"}"));
	}
	
	@Test
	public void internalServerError() throws MessagingException {
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
		});
		assertThat(e.getCause(), allOf(notNullValue(), instanceOf(SendGridException.class)));
	}
	
	private String loadJson(String path) throws IOException, JsonParseException, JsonMappingException {
		return IOUtils.toString(getClass().getResourceAsStream(path));
	}
}
