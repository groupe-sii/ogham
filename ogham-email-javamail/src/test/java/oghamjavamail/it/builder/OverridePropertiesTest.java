package oghamjavamail.it.builder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.context.EnvBuilderBasedContext;
import fr.sii.ogham.core.builder.env.SimpleEnvironmentBuilder;
import fr.sii.ogham.core.builder.registry.Registry;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.env.JavaPropertiesResolver;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.email.builder.javamail.OverrideJavaMailResolver;

public class OverridePropertiesTest {
	@Rule public final MockitoRule mokito = MockitoJUnit.rule();
	
	@Mock Registry<Object> registry;
	
	PropertyResolver defaultResolver;
	PropertyResolver emptyResolver;
	Converter converter;
	ConfigurationValueBuilderHelper<?, String> hosts;
	ConfigurationValueBuilderHelper<?, Integer> ports;
	SimpleEnvironmentBuilder<Object> environmentBuilder;
	Properties defaultProps;
	Properties emptyProps;

	@Before
	public void setup() {
		converter = new DefaultConverter();
		defaultProps = new Properties();
		defaultProps.setProperty("another.prop", "another-prop");
		defaultProps.setProperty("mail.smtp.host", "smtp-default");
		defaultProps.setProperty("mail.smtp.port", "1");
		defaultProps.setProperty("mail.host", "default");
		defaultProps.setProperty("mail.port", "2");
		defaultProps.setProperty("ogham.email.javamail.host", "override");
		defaultProps.setProperty("ogham.email.javamail.port", "3");
		defaultResolver = new JavaPropertiesResolver(defaultProps, converter);
		emptyProps = new Properties();
		emptyResolver = new JavaPropertiesResolver(emptyProps, converter);
		environmentBuilder = new SimpleEnvironmentBuilder<>(null);
		BuildContext ctx = new EnvBuilderBasedContext(environmentBuilder, registry);
		hosts = new ConfigurationValueBuilderHelper<>(null, String.class, ctx);
		ports = new ConfigurationValueBuilderHelper<>(null, Integer.class, ctx);
	}

	@Test
	public void values() {
		hosts.setValue("localhost");
		ports.setValue(1000);
		environmentBuilder.properties(defaultProps);
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
		environmentBuilder.properties(emptyProps);
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
		environmentBuilder.properties(defaultProps);
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
		environmentBuilder.properties(emptyProps);
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
		environmentBuilder.properties(defaultProps);
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
		environmentBuilder.properties(emptyProps);
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
