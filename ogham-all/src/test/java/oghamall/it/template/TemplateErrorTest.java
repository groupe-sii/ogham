package oghamall.it.template;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.Matchers.containsString;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.handler.NoContentException;
import fr.sii.ogham.core.exception.template.NoEngineDetectionException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class TemplateErrorTest {
	ExpectedException thrown = ExpectedException.none();
	
	@Rule public final RuleChain chain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(thrown);
	@Rule public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

	
	MessagingService service;

	@Before
	public void setup() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder.environment()
			.properties()
				.set("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress())
				.set("mail.smtp.port", ServerSetupTest.SMTP.getPort());
		service = builder.build();
	}
	
	@Test
	public void singleTemplateNotFound() throws MessagingException {
		thrown.expect(MessageNotSentException.class);
		thrown.expect(hasAnyCause(NoEngineDetectionException.class, hasMessage(containsString("INVALID_PATH"))));
		
		service.send(new Email()
				.from("sender@yopmail.com")
				.to("recipient@yopmail.com")
				.content(new TemplateContent("INVALID_PATH", null)));
	}
	
	@Test
	public void multiTemplateNotFound() throws MessagingException {
		thrown.expect(MessageNotSentException.class);
		thrown.expect(hasAnyCause(NoContentException.class, hasMessage(containsString("Template not found for INVALID_PATH after trying to load from [INVALID_PATH.txt, INVALID_PATH.txt.ftl, INVALID_PATH.txt.ftlh]"))));
		thrown.expect(hasAnyCause(NoContentException.class, hasMessage(containsString("Template not found for INVALID_PATH after trying to load from [INVALID_PATH.html, INVALID_PATH.xhtml, INVALID_PATH.html.ftl, INVALID_PATH.html.ftlh]"))));

		service.send(new Email()
				.from("sender@yopmail.com")
				.to("recipient@yopmail.com")
				.content(new MultiTemplateContent("INVALID_PATH", null)));
	}
}
