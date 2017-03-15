package fr.sii.ogham.spring.ut.autoconfigure;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.thymeleaf.spring4.SpringTemplateEngine;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.spring.autoconfigure.OghamAutoConfiguration;
import fr.sii.ogham.spring.config.FreeMarkerConfigurer;
import fr.sii.ogham.spring.config.MessagingBuilderConfigurer;
import fr.sii.ogham.spring.config.PropertiesBridge;
import fr.sii.ogham.spring.config.ThymeLeafConfigurer;
import freemarker.template.Configuration;

@RunWith(MockitoJUnitRunner.class)
public class OghamAutoConfigurationTest {
	private OghamAutoConfiguration autoConfiguration;

	@Mock PropertiesBridge propertiesBridgeMock;
	@Mock Properties properties;
	@Mock SpringTemplateEngine springTemplateEngineMock;
	@Mock Configuration freemarkerConfiguration;
	
	@InjectMocks ThymeLeafConfigurer thymeleafConfigurer;
	@InjectMocks FreeMarkerConfigurer freeMarkerConfigurer;
	

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Before
	public void setUp() {
		autoConfiguration = new OghamAutoConfiguration();
		when(propertiesBridgeMock.convert(any(Environment.class))).thenReturn(properties);
	}

	@Test
	public void byDefault() {
		// Given
		List<MessagingBuilderConfigurer> configurers = Collections.emptyList();
		
		// When
		MessagingBuilder builder = autoConfiguration.defaultMessagingBuilder(propertiesBridgeMock, configurers);

		// Then
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertFalse(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);

		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertFalse(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);
	}

	@Test
	public void springThymeLeaf() {
		// Given
		List<MessagingBuilderConfigurer> configurers = Arrays.<MessagingBuilderConfigurer>asList(thymeleafConfigurer);

		// When
		MessagingBuilder builder = autoConfiguration.defaultMessagingBuilder(propertiesBridgeMock, configurers);

		// Then
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertTrue(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);

		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertTrue(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);
	}

	@Test
	public void springFreeMarker() {
		// Given
		List<MessagingBuilderConfigurer> configurers = Arrays.<MessagingBuilderConfigurer>asList(freeMarkerConfigurer);

		// When
		MessagingBuilder builder = autoConfiguration.defaultMessagingBuilder(propertiesBridgeMock, configurers);

		// Then
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertEquals(builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser().getConfiguration(), freemarkerConfiguration);
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertFalse(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);

		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertEquals(builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser().getConfiguration(), freemarkerConfiguration);
		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertFalse(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);
	}

	@Test
	public void springThymeLeafFreeMarker() {
		// Given
		List<MessagingBuilderConfigurer> configurers = Arrays.<MessagingBuilderConfigurer>asList(thymeleafConfigurer, freeMarkerConfigurer);

		// When
		MessagingBuilder builder = autoConfiguration.defaultMessagingBuilder(propertiesBridgeMock, configurers);

		// Then
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertEquals(builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser().getConfiguration(), freemarkerConfiguration);
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertTrue(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);

		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertEquals(builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser().getConfiguration(), freemarkerConfiguration);
		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertTrue(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);
	}
}
