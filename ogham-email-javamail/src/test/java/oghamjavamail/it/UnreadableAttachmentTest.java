package oghamjavamail.it;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

import com.icegreen.greenmail.junit4.GreenMailRule;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.handler.AttachmentResourceHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailRule;

public class UnreadableAttachmentTest {
	GreenMailRule greenMail = new RandomPortGreenMailRule();
	TemporaryFolder temp = new TemporaryFolder();

	@Rule public final RuleChain chain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(temp)
			.around(greenMail);
	
	MessagingService service;
	File unreadable;

	
	@Before
	public void setup() throws IOException {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
					.set("mail.smtp.port", greenMail.getSmtp().getPort());
		service = builder.build();
		unreadable = temp.newFile("UNREADABLE");
		unreadable.setReadable(false);
	}

	@Test
	public void attachmentDoesntExist() throws MessagingException {
		MessageException e = assertThrows("should throw", MessageException.class, () -> {
			service.send(new Email()
					.attach(new Attachment(new File("INVALID_FILE")))
					.subject("Subject")
					.content("Body")
					.from("sender@gmail.com")
					.to("recipient@gmail.com"));
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(AttachmentResourceHandlerException.class));
		assertThat("should indicate which file", e, hasAnyCause(FileNotFoundException.class, hasMessage(containsString("INVALID_FILE"))));
	}

	@Test
	public void attachmentUnreadable() throws MessagingException {
		Assume.assumeFalse("File.setReadable has no effect on Windows", isWindows());
		MessageException e = assertThrows("should throw", MessageException.class, () -> {
			service.send(new Email()
					.attach(new Attachment(unreadable))
					.subject("Subject")
					.content("Body")
					.from("sender@gmail.com")
					.to("recipient@gmail.com"));
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(AttachmentResourceHandlerException.class));
		assertThat("should indicate which file", e, hasAnyCause(FileNotFoundException.class, hasMessage(containsString(unreadable.getName()))));
	}
	
	private static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}
}
