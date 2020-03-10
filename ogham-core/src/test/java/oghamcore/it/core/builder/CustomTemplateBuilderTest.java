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

import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import mock.builder.FluentChainingBuilderWithEnv;
import mock.builder.MockBuilder;

public class CustomTemplateBuilderTest {
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	
	@Mock TemplateParser configuredParser;
	
	@Test
	public void asDeveloperIRegisterACustomBuilderWithFluentChaining() {
		MessagingBuilder builder = MessagingBuilder.empty();
		CustomChainingBuilder customBuilder = builder.email().template(CustomChainingBuilder.class);
		customBuilder.someValue(configuredParser);
		assertThat("instantiated", customBuilder, notNullValue());
		assertThat("parent", customBuilder.and(), is(builder.email()));
		assertThat("build", customBuilder.build(), is(configuredParser));
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void asDeveloperIRegisterACustomBuilderWithoutFluentChaining() {
		MessagingBuilder builder = MessagingBuilder.empty();
		MockBuilder customBuilder = builder.email().template(MockBuilder.class);
		customBuilder.someValue(configuredParser);
		assertThat("instantiated", customBuilder, notNullValue());
		assertThat("build", customBuilder.build(), is(configuredParser));
	}
	
	@Test
	public void asDeveloperICantRegisterANonVisible() {
		MessagingBuilder builder = MessagingBuilder.empty();
		
		BuildException e = assertThrows("should throw", BuildException.class, () -> {
			builder.email().template(InvisibleBuilder.class);
		});
		assertThat("should indicate cause", e.getCause(), instanceOf(IllegalAccessException.class));
	}
	
	@Test
	public void asDeveloperICantRegisterABuilderWithWrongConstructor() {
		MessagingBuilder builder = MessagingBuilder.empty();

		BuildException e = assertThrows("should throw", BuildException.class, () -> {
			builder.email().template(InvalidBuilder.class);
		});
		assertThat("should indicate cause", e, hasMessage(containsString("No matching constructor found")));
	}
	
	public static class CustomChainingBuilder extends FluentChainingBuilderWithEnv<EmailBuilder, TemplateParser> {
		public CustomChainingBuilder(EmailBuilder parent, BuildContext buildContext) {
			super(parent, buildContext);
		}
	}
	
	private static class InvisibleBuilder extends FluentChainingBuilderWithEnv<EmailBuilder, TemplateParser> {
		public InvisibleBuilder(EmailBuilder parent, BuildContext buildContext) {
			super(parent, buildContext);
		}
	}
	
	public static class InvalidBuilder implements Builder<TemplateParser> {
		public InvalidBuilder(String foo) {
		}
		@Override
		public TemplateParser build() {
			return null;
		}
	}
}
