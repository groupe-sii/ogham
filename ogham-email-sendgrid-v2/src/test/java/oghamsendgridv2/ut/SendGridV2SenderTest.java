package oghamsendgridv2.ut;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.SendGridV2Sender;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.testing.extension.common.LogTestInformation;

@LogTestInformation
@ExtendWith(MockitoExtension.class)
public class SendGridV2SenderTest {
	@Mock SendGridClient service;
	@Mock SendGridContentHandler handler;
	
	@Test
	public void constructionAssertions() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new SendGridV2Sender(null, handler), "service cannot be null");
		assertThat("message", e.getMessage(), is("[service] cannot be null"));
		IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () -> new SendGridV2Sender(service, null), "handler cannot be null");
		assertThat("message", e2.getMessage(), is("[handler] cannot be null"));
	}
	
	@Test
	public void reportViolations() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
			SendGridV2Sender sender = new SendGridV2Sender(service, handler);
			sender.send(null);
		}, "null email");
		assertThat("message", e.getMessage(), is("[message] cannot be null"));
		MessageException e2 = assertThrows(MessageException.class, () -> {
			SendGridV2Sender sender = new SendGridV2Sender(service, handler);
			sender.send(new Email());
		}, "empty email");
		assertThat("message", e2.getMessage(), is("The provided email is invalid. (Violations: [Missing recipients, Missing sender email address, Missing subject, Missing content])"));
	}
}
