package oghamjavamail.it;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.javamail.AttachmentResourceHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class UnreadableAttachmentTest {
	GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	ExpectedException thrown = ExpectedException.none();
	TemporaryFolder temp = new TemporaryFolder();

	@Rule public final RuleChain chain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(temp)
			.around(greenMail)
			.around(thrown);
	
	MessagingService service;
	File unreadable;

	
	@Before
	public void setup() throws IOException {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress())
					.set("mail.smtp.port", ServerSetupTest.SMTP.getPort());
		service = builder.build();
		unreadable = temp.newFile("UNREADABLE");
		unreadable.setReadable(false);
	}

	@Test
	public void attachmentDoesntExist() throws MessagingException {
		thrown.expect(MessageException.class);
		thrown.expectCause(instanceOf(AttachmentResourceHandlerException.class));
		thrown.expect(hasAnyCause(FileNotFoundException.class, hasMessage(containsString("INVALID_FILE"))));
		service.send(new Email()
				.attach(new Attachment(new File("INVALID_FILE")))
				.subject("Subject")
				.content("Body")
				.from("sender@gmail.com")
				.to("recipient@gmail.com"));
	}

	@Test
	public void attachmentUnreadable() throws MessagingException {
		thrown.expect(MessageException.class);
		thrown.expectCause(instanceOf(AttachmentResourceHandlerException.class));
		thrown.expect(hasAnyCause(FileNotFoundException.class, hasMessage(containsString(unreadable.getName()))));
		service.send(new Email()
				.attach(new Attachment(unreadable))
				.subject("Subject")
				.content("Body")
				.from("sender@gmail.com")
				.to("recipient@gmail.com"));
	}
}
