package fr.sii.ogham.ut.email.javamail.builder;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.env.JavaPropertiesResolver;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.email.builder.javamail.OverrideJavaMailResolver;

public class OverridePropertiesTest {
	PropertyResolver defaultResolver;
	PropertyResolver emptyResolver;
	Converter converter;
	
	@Before
	public void setup() {
		Properties defaultProps = new Properties();
		defaultProps.setProperty("mail.smtp.host", "smtp-default");
		defaultProps.setProperty("mail.smtp.port", "smtp-default");
		defaultProps.setProperty("mail.host", "default");
		defaultProps.setProperty("mail.port", "default");
		defaultProps.setProperty("ogham.email.javamail.host", "override");
		defaultProps.setProperty("ogham.email.javamail.port", "override");
		defaultResolver = new JavaPropertiesResolver(defaultProps, converter);
		emptyResolver = new JavaPropertiesResolver(new Properties(), converter);
	}
	
	@Test
	public void values() {
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(defaultResolver, converter, asList("localhost"), Collections.<String>emptyList(), 1000);
		assertThat(props.containsProperty("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("localhost"));
		assertThat(props.containsProperty("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("1000"));
		assertThat(props.containsProperty("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("localhost"));
		assertThat(props.containsProperty("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("1000"));
	}
	
	@Test
	public void valuesWithEmptyProps() {
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(emptyResolver, converter, asList("localhost"), Collections.<String>emptyList(), 1000);
		assertThat(props.containsProperty("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("localhost"));
		assertThat(props.containsProperty("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("1000"));
		assertThat(props.containsProperty("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("localhost"));
		assertThat(props.containsProperty("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("1000"));
	}
	
	@Test
	public void overrideProperties() {
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(defaultResolver, converter, asList("${ogham.email.javamail.host}", "${mail.smtp.host}", "${mail.host}"), asList("${ogham.email.javamail.port}", "${mail.smtp.port}", "${mail.port}"), null);
		assertThat(props.containsProperty("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("override"));
		assertThat(props.containsProperty("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("override"));
		assertThat(props.containsProperty("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("override"));
		assertThat(props.containsProperty("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("override"));
	}
	
	@Test
	public void overridePropertiesWithEmptyProps() {
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(emptyResolver, converter, asList("${ogham.email.javamail.host}", "${mail.smtp.host}", "${mail.host}"), asList("${ogham.email.javamail.port}", "${mail.smtp.port}", "${mail.port}"), null);
		assertThat(props.containsProperty("mail.smtp.host"), is(false));
		assertThat(props.getProperty("mail.smtp.host"), nullValue());
		assertThat(props.containsProperty("mail.smtp.port"), is(false));
		assertThat(props.getProperty("mail.smtp.port"), nullValue());
		assertThat(props.containsProperty("mail.host"), is(false));
		assertThat(props.getProperty("mail.host"), nullValue());
		assertThat(props.containsProperty("mail.port"), is(false));
		assertThat(props.getProperty("mail.port"), nullValue());
	}

	@Test
	public void noOverride() {
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(defaultResolver, converter, Collections.<String>emptyList(), Collections.<String>emptyList(), null);
		assertThat(props.containsProperty("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("smtp-default"));
		assertThat(props.containsProperty("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("smtp-default"));
		assertThat(props.containsProperty("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("default"));
		assertThat(props.containsProperty("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("default"));
	}

	@Test
	public void noOverrideWithEmptyProps() {
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(emptyResolver, converter, Collections.<String>emptyList(), Collections.<String>emptyList(), null);
		assertThat(props.containsProperty("mail.smtp.host"), is(false));
		assertThat(props.getProperty("mail.smtp.host"), nullValue());
		assertThat(props.containsProperty("mail.smtp.port"), is(false));
		assertThat(props.getProperty("mail.smtp.port"), nullValue());
		assertThat(props.containsProperty("mail.host"), is(false));
		assertThat(props.getProperty("mail.host"), nullValue());
		assertThat(props.containsProperty("mail.port"), is(false));
		assertThat(props.getProperty("mail.port"), nullValue());
	}
}
