package oghamall.it.template;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.handler.NoContentException;
import fr.sii.ogham.core.exception.template.NoEngineDetectionException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
public class TemplateErrorTest {
	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();

	
	MessagingService service;

	@BeforeEach
	public void setup() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder.environment()
			.properties()
				.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
				.set("mail.smtp.port", greenMail.getSmtp().getPort());
		service = builder.build();
	}
	
	@Test
	public void singleTemplateNotFound() throws MessagingException {
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			service.send(new Email()
					.from("sender@yopmail.com")
					.to("recipient@yopmail.com")
					.content(new TemplateContent("INVALID_PATH", null)));
		}, "should throw");
		assertThat("should indicate path", e, hasAnyCause(NoEngineDetectionException.class, hasMessage(containsString("INVALID_PATH"))));
	}
	
	@Test
	public void multiTemplateNotFound() throws MessagingException {
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			service.send(new Email()
					.from("sender@yopmail.com")
					.to("recipient@yopmail.com")
					.content(new MultiTemplateContent("INVALID_PATH", null)));
		}, "should throw");
		assertThat("should indicate path for text", e, hasAnyCause(NoContentException.class, hasMessage(containsString("Template not found for INVALID_PATH after trying to load from [INVALID_PATH.txt, INVALID_PATH.txt.ftl, INVALID_PATH.txt.ftlh]"))));
		assertThat("should indicate path for html", e, hasAnyCause(NoContentException.class, hasMessage(containsString("Template not found for INVALID_PATH after trying to load from [INVALID_PATH.html, INVALID_PATH.xhtml, INVALID_PATH.html.ftl, INVALID_PATH.html.ftlh]"))));

	}
}
