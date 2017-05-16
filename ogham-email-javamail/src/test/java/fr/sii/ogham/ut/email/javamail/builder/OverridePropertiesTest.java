package fr.sii.ogham.ut.email.javamail.builder;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import fr.sii.ogham.email.builder.javamail.OverrideJavaMailProperties;

public class OverridePropertiesTest {
	Properties defaultProps;
	
	@Before
	public void setup() {
		defaultProps = new Properties();
		defaultProps.setProperty("mail.smtp.host", "smtp-default");
		defaultProps.setProperty("mail.smtp.port", "smtp-default");
		defaultProps.setProperty("mail.host", "default");
		defaultProps.setProperty("mail.port", "default");
		defaultProps.setProperty("ogham.email.host", "override");
		defaultProps.setProperty("ogham.email.port", "override");
	}
	
	@Test
	public void values() {
		OverrideJavaMailProperties props = new OverrideJavaMailProperties(defaultProps, asList("localhost"), Collections.<String>emptyList(), 1000);
		assertThat(props.containsKey("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("localhost"));
		assertThat(props.containsKey("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("1000"));
		assertThat(props.containsKey("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("localhost"));
		assertThat(props.containsKey("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("1000"));
	}
	
	@Test
	public void valuesWithEmptyProps() {
		OverrideJavaMailProperties props = new OverrideJavaMailProperties(new Properties(), asList("localhost"), Collections.<String>emptyList(), 1000);
		assertThat(props.containsKey("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("localhost"));
		assertThat(props.containsKey("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("1000"));
		assertThat(props.containsKey("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("localhost"));
		assertThat(props.containsKey("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("1000"));
	}
	
	@Test
	public void overrideProperties() {
		OverrideJavaMailProperties props = new OverrideJavaMailProperties(defaultProps, asList("${ogham.email.host}", "${mail.smtp.host}", "${mail.host}"), asList("${ogham.email.port}", "${mail.smtp.port}", "${mail.port}"), null);
		assertThat(props.containsKey("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("override"));
		assertThat(props.containsKey("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("override"));
		assertThat(props.containsKey("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("override"));
		assertThat(props.containsKey("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("override"));
	}
	
	@Test
	public void overridePropertiesWithEmptyProps() {
		OverrideJavaMailProperties props = new OverrideJavaMailProperties(new Properties(), asList("${ogham.email.host}", "${mail.smtp.host}", "${mail.host}"), asList("${ogham.email.port}", "${mail.smtp.port}", "${mail.port}"), null);
		assertThat(props.containsKey("mail.smtp.host"), is(false));
		assertThat(props.getProperty("mail.smtp.host"), nullValue());
		assertThat(props.containsKey("mail.smtp.port"), is(false));
		assertThat(props.getProperty("mail.smtp.port"), nullValue());
		assertThat(props.containsKey("mail.host"), is(false));
		assertThat(props.getProperty("mail.host"), nullValue());
		assertThat(props.containsKey("mail.port"), is(false));
		assertThat(props.getProperty("mail.port"), nullValue());
	}

	@Test
	public void noOverride() {
		OverrideJavaMailProperties props = new OverrideJavaMailProperties(defaultProps, Collections.<String>emptyList(), Collections.<String>emptyList(), null);
		assertThat(props.containsKey("mail.smtp.host"), is(true));
		assertThat(props.getProperty("mail.smtp.host"), is("smtp-default"));
		assertThat(props.containsKey("mail.smtp.port"), is(true));
		assertThat(props.getProperty("mail.smtp.port"), is("smtp-default"));
		assertThat(props.containsKey("mail.host"), is(true));
		assertThat(props.getProperty("mail.host"), is("default"));
		assertThat(props.containsKey("mail.port"), is(true));
		assertThat(props.getProperty("mail.port"), is("default"));
	}

	@Test
	public void noOverrideWithEmptyProps() {
		OverrideJavaMailProperties props = new OverrideJavaMailProperties(new Properties(), Collections.<String>emptyList(), Collections.<String>emptyList(), null);
		assertThat(props.containsKey("mail.smtp.host"), is(false));
		assertThat(props.getProperty("mail.smtp.host"), nullValue());
		assertThat(props.containsKey("mail.smtp.port"), is(false));
		assertThat(props.getProperty("mail.smtp.port"), nullValue());
		assertThat(props.containsKey("mail.host"), is(false));
		assertThat(props.getProperty("mail.host"), nullValue());
		assertThat(props.containsKey("mail.port"), is(false));
		assertThat(props.getProperty("mail.port"), nullValue());
	}
}
