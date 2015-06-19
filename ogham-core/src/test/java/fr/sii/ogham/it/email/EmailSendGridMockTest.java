package fr.sii.ogham.it.email;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.context.SimpleContext;
import fr.sii.ogham.email.builder.SendGridBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.sender.impl.SendGridSender;
import fr.sii.ogham.email.sender.impl.sendgrid.client.SendGridClient;

/**
 * Tests regarding the integration between {@code notification-module} and the
 * {@link SendGridSender} class. The emails are captured at the boundary between
 * the {@code notification-module}-aware code and the {@code SendGrid-java}
 * -aware code, i.e. the {@link SendGridClient} interface.
 */
public final class EmailSendGridMockTest {

	private static final String SUBJECT = "Example email";
	private static final String CONTENT_TEXT = "This is a default content.";
	private static final String CONTENT_TEXT_ACCENTED = "I like soufflé.";
	private static final String CONTENT_HTML = "<html><body><p>This is a default content.</p></body></html>";
	private static final String NAME = "you";
	private static final String CONTENT_TEXT_TEMPLATE = "<html xmlns:th=\"http://www.thymeleaf.org\" th:inline=\"text\" th:remove=\"tag\">Hello [[${name}]]</html>";
	private static final String CONTENT_TEXT_RESULT = "Hello " + NAME;
	private static final String CONTENT_HTML_TEMPLATE = "<html xmlns:th=\"http://www.thymeleaf.org\" th:inline=\"text\"><body><p>Hello [[${name}]]</p></body></html>";
	private static final String CONTENT_HTML_RESULT = "<html><body><p>Hello " + NAME + "</p></body></html>";
	private static final String FROM = "SENDER";
	private static final String FROM_ADDRESS = "from@example.com";
	private static final String TO = "RECIPIENT";
	private static final String TO_ADDRESS_1 = "to.1@example.com";
	private static final String TO_ADDRESS_2 = "to.2@example.com";

	private SendGridClient sendGridClient;
	private MessagingService notificationService;

	@Before
	public void setUp() {
		sendGridClient = mock(SendGridClient.class);
		MessagingBuilder builder = new MessagingBuilder().useAllDefaults();
		// FixedCondition(true) => always used for Email messages
		builder.getEmailBuilder().registerImplementation(new FixedCondition<Message>(true), new SendGridBuilder().useDefaults().withClient(sendGridClient));
		notificationService = builder.build();
	}

	@Test
	public void forBasicTextEmail() throws MessagingException, SendGridException {
		final Email email = new Email(SUBJECT, new StringContent(CONTENT_TEXT), new EmailAddress(FROM_ADDRESS, FROM), new EmailAddress(TO_ADDRESS_1, TO));

		notificationService.send(email);

		final ArgumentCaptor<SendGrid.Email> argument = ArgumentCaptor.forClass(SendGrid.Email.class);
		verify(sendGridClient).send(argument.capture());
		final SendGrid.Email val = argument.getValue();

		assertEquals(SUBJECT, val.getSubject());
		assertEquals(FROM, val.getFromName());
		assertEquals(FROM_ADDRESS, val.getFrom());
		assertEquals(TO, val.getToNames()[0]);
		assertEquals(TO_ADDRESS_1, val.getTos()[0]);
		assertEquals(CONTENT_TEXT, val.getText());
	}

	// JMimeMagicProvider is only able to detect text/plain when using ASCII
	// characters. This test ensures our
	// workaround (use of a secondary provider that always returns text/plain)
	// works.
	@Test
	public void forAccentedTextEmail() throws MessagingException, SendGridException {
		final Email email = new Email(SUBJECT, new StringContent(CONTENT_TEXT_ACCENTED), new EmailAddress(FROM_ADDRESS, FROM), new EmailAddress(TO_ADDRESS_1, TO));

		notificationService.send(email);

		final ArgumentCaptor<SendGrid.Email> argument = ArgumentCaptor.forClass(SendGrid.Email.class);
		verify(sendGridClient).send(argument.capture());
		final SendGrid.Email val = argument.getValue();

		assertEquals(SUBJECT, val.getSubject());
		assertEquals(FROM, val.getFromName());
		assertEquals(FROM_ADDRESS, val.getFrom());
		assertEquals(TO, val.getToNames()[0]);
		assertEquals(TO_ADDRESS_1, val.getTos()[0]);
		assertEquals(CONTENT_TEXT_ACCENTED, val.getText());
	}

	@Test
	public void forBasicHtmlEmail() throws MessagingException, SendGridException {
		final Email email = new Email(SUBJECT, new StringContent(CONTENT_HTML), new EmailAddress(FROM_ADDRESS, FROM), new EmailAddress(TO_ADDRESS_1, TO));

		notificationService.send(email);

		final ArgumentCaptor<SendGrid.Email> argument = ArgumentCaptor.forClass(SendGrid.Email.class);
		verify(sendGridClient).send(argument.capture());
		final SendGrid.Email val = argument.getValue();

		assertEquals(SUBJECT, val.getSubject());
		assertEquals(FROM, val.getFromName());
		assertEquals(FROM_ADDRESS, val.getFrom());
		assertEquals(TO, val.getToNames()[0]);
		assertEquals(TO_ADDRESS_1, val.getTos()[0]);
		assertEquals(CONTENT_HTML, val.getHtml());
	}

	@Test
	public void forTemplatedTextEmail() throws MessagingException, SendGridException {
		final SimpleContext context = new SimpleContext("name", NAME);
		final TemplateContent content = new TemplateContent("string:" + CONTENT_TEXT_TEMPLATE, context);
		final Email email = new Email(SUBJECT, content, new EmailAddress(FROM_ADDRESS, FROM), new EmailAddress(TO_ADDRESS_1, TO), new EmailAddress(TO_ADDRESS_2, TO));

		notificationService.send(email);

		final ArgumentCaptor<SendGrid.Email> argument = ArgumentCaptor.forClass(SendGrid.Email.class);
		verify(sendGridClient).send(argument.capture());
		final SendGrid.Email val = argument.getValue();

		assertEquals(SUBJECT, val.getSubject());
		assertEquals(FROM, val.getFromName());
		assertEquals(FROM_ADDRESS, val.getFrom());
		assertArrayEquals(new String[] { TO, TO }, val.getToNames());
		assertArrayEquals(new String[] { TO_ADDRESS_1, TO_ADDRESS_2 }, val.getTos());
		assertEquals(CONTENT_TEXT_RESULT, val.getText());
	}

	@Test
	public void forTemplatedHtmlEmail() throws MessagingException, SendGridException {
		final SimpleContext context = new SimpleContext("name", NAME);
		final TemplateContent content = new TemplateContent("string:" + CONTENT_HTML_TEMPLATE, context);
		final Email email = new Email(SUBJECT, content, new EmailAddress(FROM_ADDRESS, FROM), new EmailAddress(TO_ADDRESS_1, TO), new EmailAddress(TO_ADDRESS_2, TO));

		notificationService.send(email);

		final ArgumentCaptor<SendGrid.Email> argument = ArgumentCaptor.forClass(SendGrid.Email.class);
		verify(sendGridClient).send(argument.capture());
		final SendGrid.Email val = argument.getValue();

		assertEquals(SUBJECT, val.getSubject());
		assertEquals(FROM, val.getFromName());
		assertEquals(FROM_ADDRESS, val.getFrom());
		assertArrayEquals(new String[] { TO, TO }, val.getToNames());
		assertArrayEquals(new String[] { TO_ADDRESS_1, TO_ADDRESS_2 }, val.getTos());
		assertEquals(CONTENT_HTML_RESULT, val.getHtml());
	}

}
