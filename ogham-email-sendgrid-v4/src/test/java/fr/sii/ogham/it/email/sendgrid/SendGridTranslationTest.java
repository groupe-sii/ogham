package fr.sii.ogham.it.email.sendgrid;

import static fr.sii.ogham.SendGridTestUtils.getFromAddress;
import static fr.sii.ogham.SendGridTestUtils.getFromName;
import static fr.sii.ogham.SendGridTestUtils.getHtml;
import static fr.sii.ogham.SendGridTestUtils.getText;
import static fr.sii.ogham.SendGridTestUtils.getToNames;
import static fr.sii.ogham.SendGridTestUtils.getTos;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.sendgrid.helpers.mail.Mail;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.template.context.SimpleContext;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.sendgrid.sender.exception.SendGridException;
import fr.sii.ogham.email.sendgrid.v4.builder.sendgrid.SendGridV4Builder;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.junit.LoggingTestRule;

/**
 * Tests regarding the integration between {@code ogham} and the
 * {@link SendGridV4Sender} class. The emails are captured at the boundary between
 * the {@code ogham}-aware code and the {@code SendGrid-java}
 * -aware code, i.e. the {@link SendGridClient} interface.
 */
public final class SendGridTranslationTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	private static final String SUBJECT = "Example email";
	private static final String CONTENT_TEXT = "This is a default content.";
	private static final String CONTENT_TEXT_ACCENTED = "I like soufflé.";
	private static final String CONTENT_HTML = "<html><body><p>This is a default content.</p></body></html>";
	private static final String NAME = "you";
	private static final String CONTENT_TEXT_TEMPLATE = "Hello [(${name})]";
	private static final String CONTENT_TEXT_RESULT = "Hello " + NAME;
	private static final String CONTENT_HTML_TEMPLATE = "<html xmlns:th=\"http://www.thymeleaf.org\" th:inline=\"text\"><body><p>Hello [[${name}]]</p></body></html>";
	private static final String CONTENT_HTML_RESULT = "<html><body><p>Hello " + NAME + "</p></body></html>";
	private static final String FROM = "SENDER";
	private static final String FROM_ADDRESS = "from@example.com";
	private static final String TO = "RECIPIENT";
	private static final String TO_ADDRESS_1 = "to.1@example.com";
	private static final String TO_ADDRESS_2 = "to.2@example.com";

	private SendGridClient sendGridClient;
	private MessagingService messagingService;

	@Before
	public void setUp() {
		sendGridClient = mock(SendGridClient.class);
		messagingService = MessagingBuilder.standard()
				.email()
					.sender(SendGridV4Builder.class)
						.client(sendGridClient)
						.and()
					.and()
				.build();
	}

	@Test
	public void forBasicTextEmail() throws MessagingException, SendGridException {
		// @formatter:off
		final Email email = new Email()
									.subject(SUBJECT)
									.content(CONTENT_TEXT)
									.from(new EmailAddress(FROM_ADDRESS, FROM))
									.to(new EmailAddress(TO_ADDRESS_1, TO));
		// @formatter:on

		messagingService.send(email);

		final ArgumentCaptor<Mail> argument = ArgumentCaptor.forClass(Mail.class);
		verify(sendGridClient).send(argument.capture());
		final Mail val = argument.getValue();

		assertEquals(SUBJECT, val.getSubject());
		assertEquals(FROM, getFromName(val));
		assertEquals(FROM_ADDRESS, getFromAddress(val));
		assertEquals(TO, getToNames(val)[0]);
		assertEquals(TO_ADDRESS_1, getTos(val)[0]);
		assertEquals(CONTENT_TEXT, getText(val));
	}

	// JMimeMagicProvider is only able to detect text/plain when using ASCII
	// characters. This test ensures our
	// workaround (use of a secondary provider that always returns text/plain)
	// works.
	@Test
	public void forAccentedTextEmail() throws MessagingException, SendGridException {
		// @formatter:off
		final Email email = new Email()
									.subject(SUBJECT)
									.content(new StringContent(CONTENT_TEXT_ACCENTED))
									.from(new EmailAddress(FROM_ADDRESS, FROM))
									.to(new EmailAddress(TO_ADDRESS_1, TO));
		// @formatter:on

		messagingService.send(email);

		final ArgumentCaptor<Mail> argument = ArgumentCaptor.forClass(Mail.class);
		verify(sendGridClient).send(argument.capture());
		final Mail val = argument.getValue();

		assertEquals(SUBJECT, val.getSubject());
		assertEquals(FROM, getFromName(val));
		assertEquals(FROM_ADDRESS, getFromAddress(val));
		assertEquals(TO, getToNames(val)[0]);
		assertEquals(TO_ADDRESS_1, getTos(val)[0]);
		assertEquals(CONTENT_TEXT_ACCENTED, getText(val));
	}

	@Test
	public void forBasicHtmlEmail() throws MessagingException, SendGridException {
		// @formatter:off
		final Email email = new Email()
									.subject(SUBJECT)
									.content(new StringContent(CONTENT_HTML))
									.from(new EmailAddress(FROM_ADDRESS, FROM))
									.to(new EmailAddress(TO_ADDRESS_1, TO));
		// @formatter:on

		messagingService.send(email);

		final ArgumentCaptor<Mail> argument = ArgumentCaptor.forClass(Mail.class);
		verify(sendGridClient).send(argument.capture());
		final Mail val = argument.getValue();

		assertEquals(SUBJECT, val.getSubject());
		assertEquals(FROM, getFromName(val));
		assertEquals(FROM_ADDRESS, getFromAddress(val));
		assertEquals(TO, getToNames(val)[0]);
		assertEquals(TO_ADDRESS_1, getTos(val)[0]);
		assertEquals(CONTENT_HTML, getHtml(val));
	}

	@Test
	public void forTemplatedTextEmail() throws MessagingException, SendGridException {
		// @formatter:off
		final Email email = new Email()
									.subject(SUBJECT)
									.content(new TemplateContent("string:" + CONTENT_TEXT_TEMPLATE, new SimpleContext("name", NAME)))
									.from(new EmailAddress(FROM_ADDRESS, FROM))
									.to(new EmailAddress(TO_ADDRESS_1, TO), new EmailAddress(TO_ADDRESS_2, TO));
		// @formatter:on

		messagingService.send(email);

		final ArgumentCaptor<Mail> argument = ArgumentCaptor.forClass(Mail.class);
		verify(sendGridClient).send(argument.capture());
		final Mail val = argument.getValue();

		assertEquals(SUBJECT, val.getSubject());
		assertEquals(FROM, getFromName(val));
		assertEquals(FROM_ADDRESS, getFromAddress(val));
		assertArrayEquals(new String[] { TO, TO }, getToNames(val));
		assertArrayEquals(new String[] { TO_ADDRESS_1, TO_ADDRESS_2 }, getTos(val));
		assertEquals(CONTENT_TEXT_RESULT, getText(val));
	}

	@Test
	public void forTemplatedHtmlEmail() throws MessagingException, SendGridException {
		// @formatter:off
		final Email email = new Email()
									.subject(SUBJECT)
									.content(new TemplateContent("string:" + CONTENT_HTML_TEMPLATE, new SimpleContext("name", NAME)))
									.from(new EmailAddress(FROM_ADDRESS, FROM))
									.to(new EmailAddress(TO_ADDRESS_1, TO), new EmailAddress(TO_ADDRESS_2, TO));
		// @formatter:on

		messagingService.send(email);

		final ArgumentCaptor<Mail> argument = ArgumentCaptor.forClass(Mail.class);
		verify(sendGridClient).send(argument.capture());
		final Mail val = argument.getValue();

		assertEquals(SUBJECT, val.getSubject());
		assertEquals(FROM, getFromName(val));
		assertEquals(FROM_ADDRESS, getFromAddress(val));
		assertArrayEquals(new String[] { TO, TO }, getToNames(val));
		assertArrayEquals(new String[] { TO_ADDRESS_1, TO_ADDRESS_2 }, getTos(val));
		assertEquals(CONTENT_HTML_RESULT, getHtml(val));
	}

}
