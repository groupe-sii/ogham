package oghamall.it.retry;

import com.cloudhopper.smpp.type.SmppChannelConnectException;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.InvalidMessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.handler.ImageInliningException;
import fr.sii.ogham.core.exception.handler.NoContentException;
import fr.sii.ogham.core.exception.handler.TemplateNotFoundException;
import fr.sii.ogham.core.exception.handler.TemplateParsingFailedException;
import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.exception.retry.UnrecoverableException;
import fr.sii.ogham.core.exception.template.NoEngineDetectionException;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.ConnectionFailedException;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import freemarker.core.InvalidReferenceException;
import mock.context.SimpleBean;
import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import ogham.testing.com.icegreen.greenmail.util.ServerSetup;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.eclipse.angus.mail.util.MailConnectException;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.util.Collection;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
public class AutoRetryTest {
	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension(64000, ServerSetup.PROTOCOL_SMTP);
	@RegisterExtension
	public final SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension(new ServerConfig().randomPort(64000));

	MessagingBuilder builder;
	
	@BeforeEach
	public void setup() {
		builder = MessagingBuilder.standard();
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("ogham.email.send-retry.max-attempts", 5)
					.set("ogham.email.send-retry.delay-between-attempts", 100)
					.set("ogham.sms.send-retry.max-attempts", 5)
					.set("ogham.sms.send-retry.delay-between-attempts", 100)
					.set("ogham.sms.cloudhopper.session.connect-retry.max-attempts", 0);
		// @formatter:on
	}
	
	@Test
	public void resendEmail() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", "127.0.0.1")
					.set("mail.smtp.port", 65000);
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Email()
					.from("sender@yopmail.com")
					.to("recipient@yopmail.com")
					.body().template("/template/thymeleaf/source/simple.html", new SimpleBean("foo", 42)));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(MaximumAttemptsReachedException.class));
		assertThat("should indicate original exceptions", e, executionFailures(MaximumAttemptsReachedException.class, hasSize(5)));
		assertThat("should indicate original exceptions", e, executionFailures(MaximumAttemptsReachedException.class, hasItem(hasAnyCause(instanceOf(MailConnectException.class)))));
	}
	
	@Test
	public void doNotResendEmailIfInvalidTemplate() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
					.set("mail.smtp.port", greenMail.getSmtp().getPort());
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Email()
					.from("sender@yopmail.com")
					.to("recipient@yopmail.com")
					.body().template("/template/thymeleaf/source/invalid.html", new SimpleBean("foo", 42)));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasSize(1)));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(TemplateParsingFailedException.class)))));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(ParseException.class)))));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(TemplateProcessingException.class)))));
	}
	
	@Test
	public void doNotResendEmailIfParsingFailed() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
					.set("mail.smtp.port", greenMail.getSmtp().getPort());
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Email()
					.from("sender@yopmail.com")
					.to("recipient@yopmail.com")
					.body().template("/template/freemarker/source/simple.html.ftl", null));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasSize(1)));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(TemplateParsingFailedException.class)))));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(ParseException.class)))));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(InvalidReferenceException.class)))));
	}
	
	@Test
	public void doNotResendEmailIfTemplateNotFound() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
					.set("mail.smtp.port", greenMail.getSmtp().getPort());
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Email()
					.from("sender@yopmail.com")
					.to("recipient@yopmail.com")
					.body().template("/not-found.html.ftl", null));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasSize(1)));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(NoContentException.class)))));
		assertThat("should indicate original exceptions", e, missingContentFailures(UnrecoverableException.class, hasItems(instanceOf(TemplateNotFoundException.class), instanceOf(TemplateNotFoundException.class))));
	}
	
	@Test
	public void doNotResendEmailIfResourceUnresolved() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
					.set("mail.smtp.port", greenMail.getSmtp().getPort());
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Email()
					.from("sender@yopmail.com")
					.to("recipient@yopmail.com")
					.body().template("/template/freemarker/source/invalid-resources.html.ftl", new SimpleBean("foo", 42)));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasSize(1)));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(ImageInliningException.class)))));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(ResourceResolutionException.class)))));
	}
	
	@Test
	public void doNotResendEmailIfMimetypeDetectionFailed() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
					.set("mail.smtp.port", greenMail.getSmtp().getPort());
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Email()
					.from("sender@yopmail.com")
					.to("recipient@yopmail.com")
					.body().template("/template/freemarker/source/invalid-image-mimetype.html.ftl", new SimpleBean("foo", 42)));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasSize(1)));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(ImageInliningException.class)))));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(MimeTypeDetectionException.class)))));
	}
	
	@Test
	public void doNotResendEmailIfInvalid() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("ogham.email.sendgrid.api-key", "foo");
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Email().body().template("/template/freemarker/source/simple.html.ftl", new SimpleBean("foo", 42)));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasSize(1)));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(InvalidMessageException.class)))));
	}
	
	
	@Test
	public void resendSms() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "127.0.0.1")
					.set("ogham.sms.smpp.port", 65000);
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Sms()
					.from("0102030405")
					.to("0203040506")
					.message().template("/template/thymeleaf/source/simple.txt", new SimpleBean("foo", 42)));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(MaximumAttemptsReachedException.class));
		assertThat("should indicate original exceptions", e, executionFailures(MaximumAttemptsReachedException.class, hasSize(5)));
		assertThat("should indicate original exceptions", e, executionFailures(MaximumAttemptsReachedException.class, hasItem(hasAnyCause(instanceOf(ConnectionFailedException.class)))));
		assertThat("should indicate original exceptions", e, executionFailures(MaximumAttemptsReachedException.class, hasItem(hasAnyCause(instanceOf(SmppChannelConnectException.class)))));
	}
	
	@Test
	public void doNotResendSmsIfInvalidTemplate() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "127.0.0.1")
					.set("ogham.sms.smpp.port", smppServer.getPort());
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Sms()
					.from("0102030405")
					.to("0203040506")
					.message().template("/template/thymeleaf/source/invalid.txt", new SimpleBean("foo", 42)));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasSize(1)));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(TemplateParsingFailedException.class)))));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(ParseException.class)))));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(TemplateProcessingException.class)))));
	}
	
	@Test
	public void doNotResendSmsIfParsingFailed() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "127.0.0.1")
					.set("ogham.sms.smpp.port", smppServer.getPort());
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Sms()
					.from("0102030405")
					.to("0203040506")
					.message().template("/template/freemarker/source/simple.html.ftl", null));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasSize(1)));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(TemplateParsingFailedException.class)))));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(ParseException.class)))));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(InvalidReferenceException.class)))));
	}
	
	@Test
	public void doNotResendSmsIfTemplateNotFound() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "127.0.0.1")
					.set("ogham.sms.smpp.port", smppServer.getPort());
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Sms()
					.from("0102030405")
					.to("0203040506")
					.message().template("/not-found.txt.ftl", null));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasSize(1)));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(NoEngineDetectionException.class)))));
	}
	
	@Test
	public void doNotResendSmsIfInvalid() throws MessagingException {
		// @formatter:off
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "127.0.0.1")
					.set("ogham.sms.smpp.port", smppServer.getPort());
		// @formatter:on
		MessagingService messagingService = builder.build();
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			messagingService.send(new Sms()
					.to("0102030405")
					.message().template("/template/freemarker/source/simple.txt.ftl", new SimpleBean("foo", 42)));
		}, "should throw");
		// @formatter:on
		assertThat("should indicate that this is due to unrecoverable error", e, hasAnyCause(UnrecoverableException.class));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasSize(1)));
		assertThat("should indicate original exceptions", e, executionFailures(UnrecoverableException.class, hasItem(hasAnyCause(instanceOf(InvalidMessageException.class)))));
	}

	private static <E extends Exception, T extends Throwable> Matcher<T> executionFailures(Class<E> exception, Matcher<? super Collection<T>> matcher) {
		return hasAnyCause(exception, hasProperty("executionFailures", matcher));
	}
	
	private static <E extends Exception, T extends Throwable> Matcher<T> missingContentFailures(Class<E> exception, Matcher<? super Collection<T>> matcher) {
		return executionFailures(exception, hasItem(hasAnyCause(NoContentException.class, hasProperty("errors", matcher))));
	}
}
