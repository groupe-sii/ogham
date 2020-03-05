package oghamcore.it.core.builder;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.sender.EmailSender;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import mock.builder.FluentChainingBuilderWithEnv;
import mock.builder.MockBuilder;

public class CustomSenderBuilderTest {
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	
	@Mock EmailSender configuredSender;
	
	@Test
	public void asDeveloperIRegisterACustomBuilderWithFluentChaining() {
		MessagingBuilder builder = MessagingBuilder.empty();
		CustomChainingBuilder customBuilder = builder.email().sender(CustomChainingBuilder.class);
		customBuilder.someValue(configuredSender);
		assertThat("instantiated", customBuilder, notNullValue());
		assertThat("parent", customBuilder.and(), is(builder.email()));
		assertThat("build", customBuilder.build(), is(configuredSender));
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void asDeveloperIRegisterACustomBuilderWithoutFluentChaining() {
		MessagingBuilder builder = MessagingBuilder.empty();
		MockBuilder customBuilder = builder.email().sender(MockBuilder.class);
		customBuilder.someValue(configuredSender);
		assertThat("instantiated", customBuilder, notNullValue());
		assertThat("build", customBuilder.build(), is(configuredSender));
	}
	
	@Test
	public void asDeveloperICantRegisterANonVisible() {
		MessagingBuilder builder = MessagingBuilder.empty();

		BuildException e = assertThrows("should throw", BuildException.class, () -> {
			builder.email().sender(InvisibleBuilder.class);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(IllegalAccessException.class));
	}

	@Test
	public void asDeveloperICantRegisterABuilderWithWrongConstructor() {
		MessagingBuilder builder = MessagingBuilder.empty();
		
		BuildException e = assertThrows("should throw", BuildException.class, () -> {
			builder.email().sender(InvalidBuilder.class);
		});
		assertThat("should indicate cause", e, hasMessage(containsString("No matching constructor found")));
	}
	
	public static class CustomChainingBuilder extends FluentChainingBuilderWithEnv<EmailBuilder, EmailSender> {
		public CustomChainingBuilder(EmailBuilder parent, EnvironmentBuilder<?> env) {
			super(parent, env);
		}
	}
	
	private static class InvisibleBuilder extends FluentChainingBuilderWithEnv<EmailBuilder, EmailSender> {
		public InvisibleBuilder(EmailBuilder parent, EnvironmentBuilder<?> env) {
			super(parent, env);
		}
	}
	
	public static class InvalidBuilder implements Builder<EmailSender> {
		public InvalidBuilder(String foo) {
		}
		@Override
		public EmailSender build() {
			return null;
		}
	}
}
