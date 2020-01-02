package oghamall.it.configuration;

import static com.icegreen.greenmail.util.ServerSetupTest.SMTP;
import static fr.sii.ogham.core.builder.configurer.ConfigurationPhase.AFTER_INIT;
import static fr.sii.ogham.core.builder.configurer.ConfigurationPhase.BEFORE_BUILD;
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.exception.handler.ImageInliningException;
import fr.sii.ogham.core.exception.handler.TemplateParsingFailedException;
import fr.sii.ogham.core.exception.resource.NoResolverException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.builder.javamail.DefaultJavaMailConfigurer;
import fr.sii.ogham.email.builder.javamail.JavaMailBuilder;
import fr.sii.ogham.email.exception.javamail.NoAttachmentResourceHandlerException;
import fr.sii.ogham.email.exception.javamail.NoContentHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.template.exception.NoResolverAdapterException;
import fr.sii.ogham.template.exception.ResolverAdapterNotFoundException;
import fr.sii.ogham.template.exception.UnknownVariantException;
import fr.sii.ogham.template.freemarker.builder.FreemarkerEmailBuilder;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3EmailBuilder;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.testing.helper.sms.rule.SmppServerRule;
import mock.context.SimpleBean;

public class EmptyBuilderTest {
	ExpectedException thrown = ExpectedException.none();
	@Rule public final RuleChain ruleChain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(thrown);
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
		
		thrown.expect(MessageNotSentException.class);
		thrown.expectMessage(is("No sender available to send the message"));
		thrown.expect(hasProperty("oghamMessage", is(message)));
		
		service.send(message);
	}

	@Test
	public void unconfiguredServiceCantSendSms() throws MessagingException {
		MessagingService service = builder.build();
		
		Sms message = new Sms().from("010203040506").to("060504030201").content("sms");
		
		thrown.expect(MessageNotSentException.class);
		thrown.expectMessage(is("No sender available to send the message"));
		thrown.expect(hasProperty("oghamMessage", is(message)));
		
		service.send(message);
	}


	@Test
	public void noMimetypeConfigurationCantSendEmail() throws MessagingException {
		builder.email().sender(JavaMailBuilder.class);
		
		thrown.expect(BuildException.class);
		thrown.expectMessage("No mimetype detector configured");

		builder.build();
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
		builder.register(new DefaultJavaMailConfigurer.EnvironmentPropagator(), 1, AFTER_INIT);
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content("body")
				.attach(new Attachment("attachment/04-Java-OOP-Basics.pdf"));
		
		thrown.expect(MessageException.class);
		thrown.expectCause(instanceOf(NoAttachmentResourceHandlerException.class));
		
		service.send(message);
	}

	@Test
	public void emailSenderManuallyRegisteredAndImageInliningEnabledButUnconfiguredResourceResolutionCantInlineImages() throws MessagingException {
		builder.register(new DefaultJavaMailConfigurer.EnvironmentPropagator(), 1, AFTER_INIT);
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.email().images().inline()
			.attach().cid().sequential().and().and()
			.mimetype().defaultMimetype("application/octet-stream");
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content("<html><head></head><body><img src='template/freemarker/source/images/h1.gif' /></body></html>");
		
		thrown.expect(MessageNotSentException.class);
		thrown.expectCause(instanceOf(ImageInliningException.class));
		thrown.expectCause(hasMessage(containsString("Failed to inline image file 'template/freemarker/source/images/h1.gif'")));
		thrown.expect(hasAnyCause(NoResolverException.class, hasMessage(containsString("No resource resolver available to find resource template/freemarker/source/images/h1.gif"))));
		
		service.send(message);
	}


	@Test
	public void emailSenderManuallyRegisteredButUnconfiguredTemplateParsersCantHandleTemplateContent() throws MessagingException {
		builder.register(new DefaultJavaMailConfigurer.EnvironmentPropagator(), 1, AFTER_INIT);
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content(new TemplateContent("simple.txt.ftl", new SimpleBean("foo", 42)));
		
		thrown.expect(MessageException.class);
		thrown.expectCause(instanceOf(NoContentHandlerException.class));
		
		service.send(message);
	}

	
	@Test
	public void emailSenderManuallyRegisteredAndFreemarkerOnlyRegisteredButResourceResolutionNotConfiguredCantHandleTemplateContent() throws MessagingException {
		builder.register(new DefaultJavaMailConfigurer.EnvironmentPropagator(), 1, AFTER_INIT);
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.email().template(FreemarkerEmailBuilder.class);
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content(new TemplateContent("simple.txt.ftl", new SimpleBean("foo", 42)));
		
		thrown.expect(ResolverAdapterNotFoundException.class);
		thrown.expectCause(instanceOf(NoResolverAdapterException.class));
		
		service.send(message);
	}
	
	@Test
	public void emailSenderManuallyRegisteredButUnconfiguredTemplateParsersCantHandleMultiTemplateContent() throws MessagingException {
		builder.register(new DefaultJavaMailConfigurer.EnvironmentPropagator(), 1, AFTER_INIT);
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content(new MultiTemplateContent("simple.txt.ftl", new SimpleBean("foo", 42)));
		
		thrown.expect(MessageException.class);
		thrown.expectCause(instanceOf(NoContentHandlerException.class));
		
		service.send(message);
	}
	
	@Test
	public void emailSenderManuallyRegisteredAndTemplateParsersOnlyRegisteredCantHandleTemplateContentDueToResourceResolutionNotConfigured() throws MessagingException {
		builder.register(new DefaultJavaMailConfigurer.EnvironmentPropagator(), 1, AFTER_INIT);
		builder.register(new DefaultJavaMailConfigurer.JavaMailConfigurer(), 1);
		builder.email().template(FreemarkerEmailBuilder.class);
		builder.email().template(ThymeleafV3EmailBuilder.class);
		builder.configure(AFTER_INIT);
		builder.configure(BEFORE_BUILD);
		MessagingService service = builder.build();
		
		Email message = new Email().from("sender@yopmail.com").to("recipient@yopmail.com").subject("subject").content(new MultiTemplateContent("simple.txt.ftl", new SimpleBean("foo", 42)));
		
		thrown.expect(MessageNotSentException.class);
		thrown.expectCause(instanceOf(TemplateParsingFailedException.class));
		thrown.expect(hasAnyCause(UnknownVariantException.class, hasMessage(containsString("unknown variant/extension"))));
		
		service.send(message);
	}
}
