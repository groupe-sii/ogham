package oghamjavaxmail.it;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.handler.AttachmentResourceHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
public class UnreadableAttachmentTest {
	@RegisterExtension GreenMailExtension greenMail = new RandomPortGreenMailExtension();

	@TempDir File temp;

	MessagingService service;
	File unreadable;

	
	@BeforeEach
	public void setup() throws IOException {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
					.set("mail.smtp.port", greenMail.getSmtp().getPort());
		service = builder.build();
		unreadable = new File(temp, "UNREADABLE");
		unreadable.setReadable(false);
	}

	@Test
	public void attachmentDoesntExist() throws MessagingException {
		MessageException e = assertThrows(MessageException.class, () -> {
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
		Assumptions.assumeFalse(isWindows(), "File.setReadable has no effect on Windows");
		MessageException e = assertThrows(MessageException.class, () -> {
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
