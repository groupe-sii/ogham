package oghamsendgridv4.ut;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.testing.extension.common.LogTestInformation;

@LogTestInformation
@ExtendWith(MockitoExtension.class)
public class SendGridV4SenderTest {
	@Mock SendGridClient service;
	@Mock SendGridContentHandler handler;
	@Mock MimeTypeProvider mimetypeProvider;
	
	@Test
	public void constructionAssertions() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new SendGridV4Sender(null, handler, mimetypeProvider), "service cannot be null");
		assertThat("message", e.getMessage(), is("[service] cannot be null"));
		IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () -> new SendGridV4Sender(service, null, mimetypeProvider), "handler cannot be null");
		assertThat("message", e2.getMessage(), is("[handler] cannot be null"));
		IllegalArgumentException e3 = assertThrows(IllegalArgumentException.class, () -> new SendGridV4Sender(service, handler, null), "mimetypeProvider cannot be null");
		assertThat("message", e3.getMessage(), is("[mimetypeProvider] cannot be null"));
	}
	
	@Test
	public void reportViolations() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
			SendGridV4Sender sender = new SendGridV4Sender(service, handler, mimetypeProvider);
			sender.send(null);
		}, "null email");
		assertThat("message", e.getMessage(), is("[message] cannot be null"));
		MessageException e2 = assertThrows(MessageException.class, () -> {
			SendGridV4Sender sender = new SendGridV4Sender(service, handler, mimetypeProvider);
			sender.send(new Email());
		}, "empty email");
		assertThat("message", e2.getMessage(), is("The provided email is invalid. (Violations: [Missing recipients, Missing sender email address, Missing subject, Missing content])"));
	}
}
