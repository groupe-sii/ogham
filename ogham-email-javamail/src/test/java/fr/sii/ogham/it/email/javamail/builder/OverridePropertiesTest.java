package fr.sii.ogham.it.email.javamail.builder;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.env.JavaPropertiesResolver;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.email.builder.javamail.OverrideJavaMailResolver;

public class OverridePropertiesTest {
	PropertyResolver defaultResolver;
	PropertyResolver emptyResolver;
	Converter converter;
	ConfigurationValueBuilderHelper<?, String> hosts;
	ConfigurationValueBuilderHelper<?, Integer> ports;

	@Before
	public void setup() {
		Properties defaultProps = new Properties();
		defaultProps.setProperty("another.prop", "another-prop");
		defaultProps.setProperty("mail.smtp.host", "smtp-default");
		defaultProps.setProperty("mail.smtp.port", "1");
		defaultProps.setProperty("mail.host", "default");
		defaultProps.setProperty("mail.port", "2");
		defaultProps.setProperty("ogham.email.javamail.host", "override");
		defaultProps.setProperty("ogham.email.javamail.port", "3");
		converter = new DefaultConverter();
		defaultResolver = new JavaPropertiesResolver(defaultProps, converter);
		emptyResolver = new JavaPropertiesResolver(new Properties(), converter);
		hosts = new ConfigurationValueBuilderHelper<>(null, String.class);
		ports = new ConfigurationValueBuilderHelper<>(null, Integer.class);
	}

	@Test
	public void values() {
		hosts.setValue("localhost");
		ports.setValue(1000);
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(defaultResolver, converter, hosts, ports);
		assertThat(props.containsProperty("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("localhost"));
		assertThat(props.containsProperty("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("1000"));
		assertThat(props.containsProperty("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("localhost"));
		assertThat(props.containsProperty("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("1000"));
		assertThat(props.containsProperty("another.prop"), is(true));
		assertThat(props.getProperty("another.prop"), is("another-prop"));
	}

	@Test
	public void valuesWithEmptyProps() {
		hosts.setValue("localhost");
		ports.setValue(1000);
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(emptyResolver, converter, hosts, ports);
		assertThat(props.containsProperty("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("localhost"));
		assertThat(props.containsProperty("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("1000"));
		assertThat(props.containsProperty("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("localhost"));
		assertThat(props.containsProperty("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("1000"));
		assertThat(props.containsProperty("another.prop"), is(false));
		assertThat(props.getProperty("another.prop"), nullValue());
	}

	@Test
	public void overrideProperties() {
		hosts.properties("${ogham.email.javamail.host}", "${mail.smtp.host}", "${mail.host}");
		ports.properties("${ogham.email.javamail.port}", "${mail.smtp.port}", "${mail.port}");
		ports.setValue(null);
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(defaultResolver, converter, hosts, ports);
		assertThat(props.containsProperty("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("override"));
		assertThat(props.containsProperty("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("3"));
		assertThat(props.containsProperty("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("override"));
		assertThat(props.containsProperty("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("3"));
		assertThat(props.containsProperty("another.prop"), is(true));
		assertThat(props.getProperty("another.prop"), is("another-prop"));
	}

	@Test
	public void overridePropertiesWithEmptyProps() {
		hosts.properties("${ogham.email.javamail.host}", "${mail.smtp.host}", "${mail.host}");
		ports.properties("${ogham.email.javamail.port}", "${mail.smtp.port}", "${mail.port}");
		ports.setValue(null);
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(emptyResolver, converter, hosts, ports);
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
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(defaultResolver, converter, hosts, ports);
		assertThat(props.containsProperty("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("smtp-default"));
		assertThat(props.containsProperty("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("1"));
		assertThat(props.containsProperty("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("default"));
		assertThat(props.containsProperty("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("2"));
		assertThat(props.containsProperty("another.prop"), is(true));
		assertThat(props.getProperty("another.prop"), is("another-prop"));
	}

	@Test
	public void noOverrideWithEmptyProps() {
		OverrideJavaMailResolver props = new OverrideJavaMailResolver(emptyResolver, converter, hosts, ports);
		assertThat(props.containsProperty("mail.smtp.host"), is(false));
		assertThat(props.getProperty("mail.smtp.host"), nullValue());
		assertThat(props.containsProperty("mail.smtp.port"), is(false));
		assertThat(props.getProperty("mail.smtp.port"), nullValue());
		assertThat(props.containsProperty("mail.host"), is(false));
		assertThat(props.getProperty("mail.host"), nullValue());
		assertThat(props.containsProperty("mail.port"), is(false));
		assertThat(props.getProperty("mail.port"), nullValue());
		assertThat(props.containsProperty("another.prop"), is(false));
		assertThat(props.getProperty("another.prop"), nullValue());
	}
}
