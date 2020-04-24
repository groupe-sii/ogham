package oghamall.it.configuration;

import static com.icegreen.greenmail.util.ServerSetupTest.SMTP;
import static fr.sii.ogham.core.builder.configurer.ConfigurationPhase.AFTER_INIT;
import static fr.sii.ogham.core.builder.configurer.ConfigurationPhase.BEFORE_BUILD;
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.exception.handler.ImageInliningException;
import fr.sii.ogham.core.exception.handler.NoContentException;
import fr.sii.ogham.core.exception.resource.NoResolverException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.builder.javamail.DefaultJavaMailConfigurer;
import fr.sii.ogham.email.builder.javamail.JavaMailBuilder;
import fr.sii.ogham.email.exception.handler.NoContentHandlerException;
import fr.sii.ogham.email.exception.handler.UnresolvableAttachmentResourceHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.template.exception.NoResolverAdapterException;
import fr.sii.ogham.template.exception.ResolverAdapterNotFoundException;
import fr.sii.ogham.template.freemarker.builder.FreemarkerEmailBuilder;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3EmailBuilder;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import mock.context.SimpleBean;

public class EmptyBuilderTest {
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	@Rule public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();
	
	
	MessagingBuilder builder;
	
	@Before
	public void setup() {
		builder = MessagingBuilder.empty();
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", SMTP.getBindAddress())
					.set("mail.smtp.port", SMTP.getPort())
					.set("ogham.email.sendgrid.api-key", "foobar")
					.set("ogham.email.sendgrid.username", "foo")
					.set("ogham.email.sendgrid.password", "bar")
					.set("ogham.sms.smpp.host", "127.0.0.1")
					.set("ogham.sms.smpp.port", smppServer.getPort())
					.set("ogham.template.path-suffix", "/template/mixed/source/");
	}

	@Test
	public void unconfiguredServiceCantSendEmail() throws MessagingException {
		MessagingService service = builder.build();

		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content("body");
		
		MessageNotSentException e = assertThrows("should throw", MessageNotSentException.class, () -> {
			service.send(message);
		});
		assertThat("should indicate that there is no sender", e.getMessage(), is("No sender available to send the message"));
		assertThat("should provide original message", e.getOghamMessage(), is(message));
	}

	@Test
	public void unconfiguredServiceCantSendSms() throws MessagingException {
		MessagingService service = builder.build();
		
		Sms message = new Sms().from("010203040506").to("060504030201").content("sms");
		
		MessageNotSentException e = assertThrows("should throw", MessageNotSentException.class, () -> {
			service.send(message);
		});
		assertThat("should indicate that there is no sender", e.getMessage(), is("No sender available to send the message"));
		assertThat("should provide original message", e.getOghamMessage(), is(message));
	}


	@Test
	public void noMimetypeConfigurationCantSendEmail() throws MessagingException {
		builder.email().sender(JavaMailBuilder.class);
		
		BuildException e = assertThrows("should throw", BuildException.class, () -> {
			builder.build();
		});
		assertThat("should indicate that there is no mimetype detector", e.getMessage(), is("No mimetype detector configured"));
	}
	

	@Test
	public void manualJavaMailConfigurationCanSendEmail() throws MessagingException {
		builder.email().sender(JavaMailBuilder.class)
			.mimetype()
				.defaultMimetype("text/plain");
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content("body");
		service.send(message);
		
		assertThat(greenMail)
			.receivedMessages(hasSize(1))
			.message(0)
				.from().address(contains("sender@yopmail.com")).and()
				.to().address(contains("recipient@yopmail.com")).and()
				.subject(is("subject"))
				.body().contentAsString(is("body"));
	}
	
	@Test
	public void emailSenderManuallyRegisteredButUnconfiguredResourceResolutionCantAttachFilesFromPath() throws MessagingException {
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content("body")
				.attach(new Attachment("attachment/04-Java-OOP-Basics.pdf"));
		
		MessageException e = assertThrows("should throw", MessageException.class, () -> {
			service.send(message);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(UnresolvableAttachmentResourceHandlerException.class));
	}

	@Test
	public void emailSenderManuallyRegisteredAndImageInliningEnabledButUnconfiguredResourceResolutionCantInlineImages() throws MessagingException {
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.email().images().inline()
			.attach().cid().sequential().and().and()
			.mimetype().defaultMimetype("application/octet-stream");
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content("<html><head></head><body><img src='template/freemarker/source/images/h1.gif' /></body></html>");
		
		MessageNotSentException e = assertThrows("should throw", MessageNotSentException.class, () -> {
			service.send(message);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(ImageInliningException.class));
		assertThat("should indicate failing path", e.getCause(), hasMessage(containsString("Failed to inline image file 'template/freemarker/source/images/h1.gif'")));
		assertThat("should indicate no resolver configured", e, hasAnyCause(NoResolverException.class, hasMessage(containsString("No resource resolver available to find resource template/freemarker/source/images/h1.gif"))));
	}


	@Test
	public void emailSenderManuallyRegisteredButUnconfiguredTemplateParsersCantHandleTemplateContent() throws MessagingException {
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content(new TemplateContent("simple.txt.ftl", new SimpleBean("foo", 42)));
		
		MessageException e = assertThrows("should throw", MessageException.class, () -> {
			service.send(message);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(NoContentHandlerException.class));
	}

	
	@Test
	public void emailSenderManuallyRegisteredAndFreemarkerOnlyRegisteredButResourceResolutionNotConfiguredCantHandleTemplateContent() throws MessagingException {
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.email().template(FreemarkerEmailBuilder.class);
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content(new TemplateContent("simple.txt.ftl", new SimpleBean("foo", 42)));
		
		ResolverAdapterNotFoundException e = assertThrows("should throw", ResolverAdapterNotFoundException.class, () -> {
			service.send(message);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(NoResolverAdapterException.class));
	}
	
	@Test
	public void emailSenderManuallyRegisteredButUnconfiguredTemplateParsersCantHandleMultiTemplateContent() throws MessagingException {
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content(new MultiTemplateContent("simple.txt.ftl", new SimpleBean("foo", 42)));
		
		MessageException e = assertThrows("should throw", MessageException.class, () -> {
			service.send(message);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(NoContentHandlerException.class));
	}
	
	@Test
	public void emailSenderManuallyRegisteredAndTemplateParsersOnlyRegisteredCantHandleTemplateContentDueToResourceResolutionNotConfigured() throws MessagingException {
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.email().template(FreemarkerEmailBuilder.class);
		builder.email().template(ThymeleafV3EmailBuilder.class);
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content(new MultiTemplateContent("simple.txt.ftl", new SimpleBean("foo", 42)));
		
		MessageNotSentException e = assertThrows("should throw", MessageNotSentException.class, () -> {
			service.send(message);
		});
		assertThat("should indicate cause", e.getCause(), allOf(instanceOf(NoContentException.class), hasMessage(containsString("Template not found for simple.txt.ftl"))));
	}
}
