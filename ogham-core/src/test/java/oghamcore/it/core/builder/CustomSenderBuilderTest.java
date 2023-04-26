package oghamcore.it.core.builder;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.email.sender.EmailSender;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import mock.builder.FluentChainingBuilderWithEnv;
import mock.builder.MockBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
@MockitoSettings
public class CustomSenderBuilderTest {

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

		BuildException e = assertThrows(BuildException.class, () -> {
			builder.email().sender(InvisibleBuilder.class);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(IllegalAccessException.class));
	}

	@Test
	public void asDeveloperICantRegisterABuilderWithWrongConstructor() {
		MessagingBuilder builder = MessagingBuilder.empty();
		
		BuildException e = assertThrows(BuildException.class, () -> {
			builder.email().sender(InvalidBuilder.class);
		});
		assertThat("should indicate cause", e, hasMessage(containsString("No matching constructor found")));
	}
	
	public static class CustomChainingBuilder extends FluentChainingBuilderWithEnv<EmailBuilder, EmailSender> {
		public CustomChainingBuilder(EmailBuilder parent, BuildContext buildContext) {
			super(parent, buildContext);
		}
	}
	
	private static class InvisibleBuilder extends FluentChainingBuilderWithEnv<EmailBuilder, EmailSender> {
		public InvisibleBuilder(EmailBuilder parent, BuildContext buildContext) {
			super(parent, buildContext);
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
