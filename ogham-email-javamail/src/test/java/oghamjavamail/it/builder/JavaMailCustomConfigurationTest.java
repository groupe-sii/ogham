package oghamjavamail.it.builder;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.builder.javamail.JavaMailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class JavaMailCustomConfigurationTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	@Test(expected=MessageNotSentException.class)
	public void noHostDefinedShouldFail() throws MessagingException {
		MessagingService service = MessagingBuilder.empty()
				.email()
					.sender(JavaMailBuilder.class)
					.environment()
						.and()
					.mimetype()
						.tika()
							.failIfOctetStream(false)
							.and()
						.and()
					.and()
				.and()
				.build();
		// send the message
		service.send(new Email()
				.from("noreply@foo.bar")
				.to("recipient@foo.bar")
				.content("test")
				.subject("subject"));
	}

	@Test
	public void oghamPropertyShouldOverride() throws MessagingException {
		Properties additionalProps = new Properties();
		additionalProps.setProperty("ogham.email.javamail.host", ServerSetupTest.SMTP.getBindAddress());
		additionalProps.setProperty("ogham.email.javamail.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		additionalProps.setProperty("mail.smtp.host", "value of mail.smtp.host");
		additionalProps.setProperty("mail.smtp.port", "value of mail.smtp.port");
		additionalProps.setProperty("mail.host", "value of mail.host");
		additionalProps.setProperty("mail.port", "value of mail.port");
		MessagingService service = MessagingBuilder.empty()
				.email()
					.sender(JavaMailBuilder.class)
					.environment()
						.properties(additionalProps)
						.and()
					.mimetype()
						.tika()
							.failIfOctetStream(false)
							.and()
						.and()
					.host().properties("${ogham.email.javamail.host}", "${mail.smtp.host}", "${mail.host}").and()
					.port().properties("${ogham.email.javamail.port}", "${mail.smtp.port}", "${mail.port}").and()
					.and()
				.and()
				.build();
		// send the message
		service.send(new Email()
				.from("noreply@foo.bar")
				.to("recipient@foo.bar")
				.content("test")
				.subject("subject"));
		assertThat(greenMail).receivedMessages().count(is(1));
	}
	
	@Test
	public void directValueShouldOverride() throws MessagingException {
		Properties additionalProps = new Properties();
		additionalProps.setProperty("ogham.email.javamail.host", "value of ogham.email.javamail.host");
		additionalProps.setProperty("ogham.email.javamail.port", "value of ogham.email.javamail.port");
		additionalProps.setProperty("mail.smtp.host", "value of mail.smtp.host");
		additionalProps.setProperty("mail.smtp.port", "value of mail.smtp.port");
		additionalProps.setProperty("mail.host", "value of mail.host");
		additionalProps.setProperty("mail.port", "value of mail.port");
		MessagingService service = MessagingBuilder.empty()
				.email()
					.sender(JavaMailBuilder.class)
					.environment()
						.properties(additionalProps)
						.and()
					.mimetype()
						.tika()
							.failIfOctetStream(false)
							.and()
						.and()
					// simulate automatic configuration
					.host().properties("${ogham.email.javamail.host}", "${mail.smtp.host}", "${mail.host}").defaultValue("default-host").and()
					.port().properties("${ogham.email.javamail.port}", "${mail.smtp.port}", "${mail.port}").defaultValue(1).and()
					// developer sets values explicitly
					.host(ServerSetupTest.SMTP.getBindAddress())
					.port(ServerSetupTest.SMTP.getPort())
					.and()
				.and()
				.build();
		// send the message
		service.send(new Email()
				.from("noreply@foo.bar")
				.to("recipient@foo.bar")
				.content("test")
				.subject("subject"));
		assertThat(greenMail).receivedMessages().count(is(1));
//		
//		// 1) instantiate builder (use empty() here to show complete lifecycle)
//		MessagingBuilder builder = MessagingBuilder.empty();
//		// 2) find all configurers for "standard" configuration (see MessagingBuilder.standard())
//		MessagingBuilder.findAndRegister(builder, "standard");
//		// 3) trigger ConfigurationPhase.AFTER_INIT configuration
//		builder.configure(ConfigurationPhase.AFTER_INIT);
//		// => some early configuration may be applied
//		builder
//			.wrapUncaught(property("${configurer.after-init.high-priority}"), property("${configurer.after-init.low-priority}"), defaultValue("after-init default value"));
//		// 4) developer can configure Ogham for its needs
//		// 4a) developer can set property values from everywhere
//		builder
//			.environment()
//				// system properties
//				.systemProperties()
//				// from external configuration file
//				.properties("file:/path/to/external-file.properties")
//				// from an Properties instance
//				.properties(new Properties())
//				// set property values directly from code
//				.properties()
//					.set("property.key", "value from propertie().set()")
//					.and()
//				// use configuration file present in the classpath
//				.properties("classpath:/internal.properties");
//		// 4b) developer can customize parts of Ogham
//		builder
//			.wrapUncaught("${custom.property.key}", "default value set by developer");
//		// 5) developer has finished configuring so he calls .build() method to get instance of MessagingBuilder
//		// 5a) trigger ConfigurationPhase.BEFORE_BUILD configuration
//		builder.configure(ConfigurationPhase.BEFORE_BUILD);
//		// => some configuration may be applied (service providers for example)
//		builder
//			.wrapUncaught("${configurer.before-build.service-provider.high-priority}", "${configurer.before-build.service-provider.low-priority}", "before-build service provider default value");
//		// => some configuration may be applied (default configuration)
//		builder
//			.wrapUncaught("${configurer.before-build.default.high-priority}", "${configurer.before-build.default.low-priority}", "before-build default value");
//		// 5b) MessagingService is created with merged configuration
//		MessagingService service = builder.build();
	}
}
