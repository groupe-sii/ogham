package oghamsendgridv4.it;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sendgrid.sender.exception.AttachmentReadException;
import fr.sii.ogham.testing.extension.common.LogTestInformation;

@LogTestInformation
public class UnreadableAttachmentTest {
	MessagingService service;
	
	@BeforeEach
	public void setup() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.email.sendgrid.api-key", "foobar");
		service = builder.build();
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
		}, "invalid file");
		assertThat("cause", e.getCause(), instanceOf(AttachmentReadException.class));
		assertThat("sub cause", e.getCause().getCause(), instanceOf(FileNotFoundException.class));
	}
}
