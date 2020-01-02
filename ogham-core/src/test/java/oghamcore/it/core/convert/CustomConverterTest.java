package oghamcore.it.core.convert;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.convert.SupportingConverter;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.convert.ConversionException;

public class CustomConverterTest {
	@Test
	public void asDeveloperIUseMyCustomConverter() {
		MessagingBuilder builder = MessagingBuilder.standard();
		EnvironmentBuilder<?> env = builder.environment();
			env
				.properties()
					.set("custom-file", "/path/to/file")
					.and()
				.converter()
					.override(new CustomConverter());
		builder.build();
		PropertyResolver resolver = env.build();
		File file = resolver.getProperty("custom-file", File.class);
		assertThat("converted to file", file, notNullValue());
		assertThat("file path", file.getPath(), is("/path/to/file"));
	}

	@Test
	public void asDeveloperIUseARegisterCustomConverter() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("custom-file", "/path/to/file")
					.and()
				.converter()
					.register(new CustomConverter());
		builder.build();
		PropertyResolver resolver = builder.environment().build();
		File file = resolver.getProperty("custom-file", File.class);
		assertThat("converted to file", file, notNullValue());
		assertThat("file path", file.getPath(), is("/path/to/file"));
	}
	
	private static class CustomConverter implements SupportingConverter {
		@SuppressWarnings("unchecked")
		@Override
		public <T> T convert(Object source, Class<T> targetType) throws ConversionException {
			return (T) new File((String) source);
		}

		@Override
		public boolean supports(Class<?> sourceType, Class<?> targetType) {
			return String.class.isAssignableFrom(sourceType) && File.class.isAssignableFrom(targetType);
		}
	}
}
