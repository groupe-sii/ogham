package oghamspringbootautoconfigure.it;

import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.email.sendgrid.v4.builder.sendgrid.SendGridV4Builder;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import mock.MockApplication;
import oghamspringbootautoconfigure.it.PropertiesConsistencyTest.DecorateBuildContext;
import utils.properties.ConfigurationPropertiesMetadata;
import utils.properties.PropertiesAndValue;
import utils.properties.TrackConfigurationValueBuilder;

/**
 * Test that check if properties defined by Ogham are well-formed and consistent
 * with Spring properties.
 * 
 * To do that, there is a script that auto-generates all properties defined by
 * Ogham
 * 
 * See .tools/properties-consistency
 * 
 * 
 * TODO: should automate properties generation before running tests or let developer handle that manually ?
 * 
 * @author Aurélien Baudet
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MockApplication.class, webEnvironment = NONE)
@ActiveProfiles("consistency-check")
@Import(DecorateBuildContext.class)
public class PropertiesConsistencyTest {
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesConsistencyTest.class);
	
	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Autowired MessagingBuilder builder;
	@Autowired ApplicationContext context;
	
	private static final List<String> ignoredProps = asList(
		"ogham.email.implementation-priority.javamail",
		"ogham.email.implementation-priority.sendgrid",
		"ogham.sms.implementation-priority.cloudhopper",
		"ogham.sms.implementation-priority.ovh-http2sms",
		"ogham.sms.implementation-priority.smsglobal-rest",
		"ogham.template.implementation-priority.freemarker",
		"ogham.template.implementation-priority.thymeleaf",
		"ogham.sms.smsglobal.service-provider.auto-conf.skip",
		"mail.from",
		"mail.host",
		"mail.port",
		"mail.smtp.from",
		"mail.smtp.host",
		"mail.smtp.port",
		"greenmail.smtp.port",
		"jsmpp.server.port",
		"wiremock.server.port"
	);
	
	private static final List<String> ignorePropertiesDefinedInOghamWithoutSpringEquivalentValue = asList(
		"ogham.freemarker.static-method-access.enable",				// used as condition
		"ogham.freemarker.static-method-access.variable-name",		// used directly FreeMarker configuration
		"ogham.email.sendgrid.unit-testing"							// uses directly the Spring SendGrid client instance
	);
		
	private static final List<String> ignoreValuesDefinedInSpringWihtoutOghamPropertyEquivalentForBuilderClass = asList(
		SendGridV4Builder.class.getSimpleName()						// username and password don't have properties in Ogham for SendGrid v4
	);
		
	@Test
	public void contextLoads() {
		assertNotNull("context can't load if one property name is malformed or if value is of wrong type", builder);
	}

	@Test
	public void oghamPropertiesShouldBeDefinedInSpringUsingConfigurationPropertiesToBenefitFromRelaxedBinding() throws Exception {
		List<String> missingProperties = new ArrayList<>();
		for (String key : getAllKeysDefinedInOghamCode()) {
			if (!ignored(key) && !existsInSpringConfigurationProperties(key)) {
				missingProperties.add(key);
			}
		}
		missingProperties.stream().sorted().forEach(p -> LOG.warn("property '{}' not defined in Spring", p));
		assertThat("all Ogham properties should be defined in Spring using @ConfigurationProperties in order to benefit from relaxed binding", missingProperties, empty());
	}
	
	@Test
	public void ensureThatAllPropertiesConfiguredInOghamHaveTheirEquivalentValueConfiguredInSpring() {
		for (PropertiesAndValue pv : getPropertiesAndAssociatedValue()) {
			assertThat("properties '" + pv.getProperties()+ "' defined in Ogham should have their equivalent in Spring", pv.isConsistent(), is(true));
		}
	}

	
	
	
	
	
	
	
	
	@TestConfiguration
	public static class DecorateBuildContext {
		@Bean
		public Supplier<MessagingBuilder> messagingBuilderFactory() {
			return () -> new CustomMessagingBuilder(false);
		}
	}

	
	
	private List<PropertiesAndValue> getPropertiesAndAssociatedValue() {
		return ((CustomMessagingBuilder) builder).getConfigured()
				.stream()
				.filter(this::skipPropertiesWithoutSpringEquivalent)
				.filter(this::skipValuesWithoutProperties)
				.collect(toList());
	}
	
	
	private boolean skipPropertiesWithoutSpringEquivalent(PropertiesAndValue pv) {
		return ignorePropertiesDefinedInOghamWithoutSpringEquivalentValue.stream().noneMatch(pv::containsPropertyKey);
	}
	
	private boolean skipValuesWithoutProperties(PropertiesAndValue pv) {
		if (pv.isOnlyDefinedInSpring()) {
			return ignoreValuesDefinedInSpringWihtoutOghamPropertyEquivalentForBuilderClass.stream().noneMatch(pv::isForBuilder);
		}
		return true;
	}
	
	private static class CustomMessagingBuilder extends MessagingBuilder {
		private List<PropertiesAndValue> configured;
		
		public CustomMessagingBuilder(boolean autoconfigure) {
			super(autoconfigure);
		}

		@Override
		protected BuildContext createBuildContext() {
			return new TrackConfigurationValueBuilder(super.createBuildContext(), getConfigured());
		}

		public List<PropertiesAndValue> getConfigured() {
			if (configured == null) {
				configured = new ArrayList<>();
			}
			return configured;
		}
		
	}
	
	private static boolean ignored(String key) {
		return ignoredProps.contains(key);
	}

	private boolean existsInSpringConfigurationProperties(String key) {
		for (ConfigurationPropertiesMetadata springProps : getAllSpringConfigurationProperties()) {
			if (springProps.exists(key)) {
				return true;
			}
		}
		return false;
	}
	
	private List<ConfigurationPropertiesMetadata> getAllSpringConfigurationProperties() {
		return context.getBeansWithAnnotation(ConfigurationProperties.class)
				.entrySet()
				.stream()
				.map(this::toConfigurationPropertiesBean)
				.filter(Objects::nonNull)
				.collect(toList());
	}
	
	private static Set<String> getAllKeysDefinedInOghamCode() throws IOException {
		return Arrays.stream(resourceAsString("config/application-consistency-check.properties").split("\r?\n"))
				.filter(PropertiesConsistencyTest::skipComments)
				.filter(PropertiesConsistencyTest::skipEmptyLines)
				.map(PropertiesConsistencyTest::extractKey)
				.collect(toSet());
	}
	
	private static boolean skipComments(String line) {
		return !line.startsWith("#");
	}

	private static boolean skipEmptyLines(String line) {
		return !line.trim().isEmpty();
	}
	
	private static String extractKey(String line) {
		return line.replaceFirst("=.+", "");
	}
	
	private ConfigurationPropertiesMetadata toConfigurationPropertiesBean(Entry<String, Object> entry) {
		ConfigurationProperties annotation = entry.getValue().getClass().getAnnotation(ConfigurationProperties.class);
		if (annotation == null) {
			return null;
		}
		String prefix = annotation.prefix();
		if (prefix == null || prefix.isEmpty()) {
			prefix = annotation.value();
		}
		if (prefix == null) {
			return null;
		}
		return new ConfigurationPropertiesMetadata(entry.getKey(), entry.getValue(), prefix);
	}
}
