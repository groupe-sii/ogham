package fr.sii.ogham.spring.ut.autoconfigure;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.thymeleaf.spring4.SpringTemplateEngine;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.spring.autoconfigure.OghamAutoConfiguration;
import fr.sii.ogham.spring.config.PropertiesBridge;
import freemarker.template.Configuration;

@RunWith(MockitoJUnitRunner.class)
public class OghamAutoConfigurationTest {
	private OghamAutoConfiguration autoConfiguration;

	@Mock
	private ApplicationContext appContextMock;

	@Mock
	private PropertiesBridge propertiesBridgeMock;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Before
	public void setUp() {
		autoConfiguration = new OghamAutoConfiguration();
		autoConfiguration.setApplicationContext(appContextMock);
	}

	@Test
	public void byDefault() {
		// Given
		Mockito.when(propertiesBridgeMock.convert(Mockito.any(Environment.class))).thenReturn(new Properties());

		Mockito.when(appContextMock.getBean(SpringTemplateEngine.class)).thenThrow(new NoSuchBeanDefinitionException(""));
		Mockito.when(appContextMock.getBean(Configuration.class)).thenThrow(new NoSuchBeanDefinitionException(""));

		// When
		MessagingBuilder builder = autoConfiguration.messagingServiceBuilder(propertiesBridgeMock);

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
		Mockito.when(propertiesBridgeMock.convert(Mockito.any(Environment.class))).thenReturn(new Properties());

		SpringTemplateEngine givenSpringTemplateEngine = Mockito.mock(SpringTemplateEngine.class);
		Mockito.when(appContextMock.getBean(SpringTemplateEngine.class)).thenReturn(givenSpringTemplateEngine);
		Mockito.when(appContextMock.getBean(Configuration.class)).thenThrow(new NoSuchBeanDefinitionException(""));

		// When
		MessagingBuilder builder = autoConfiguration.messagingServiceBuilder(propertiesBridgeMock);

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
		Mockito.when(propertiesBridgeMock.convert(Mockito.any(Environment.class))).thenReturn(new Properties());

		Mockito.when(appContextMock.getBean(SpringTemplateEngine.class)).thenThrow(new NoSuchBeanDefinitionException(""));
		Configuration givenFreeMarkerConfiguration = Mockito.mock(Configuration.class);
		Mockito.when(appContextMock.getBean(Configuration.class)).thenReturn(givenFreeMarkerConfiguration);

		// When
		MessagingBuilder builder = autoConfiguration.messagingServiceBuilder(propertiesBridgeMock);

		// Then
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertEquals(builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser().getConfiguration(), givenFreeMarkerConfiguration);
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertFalse(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);

		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertEquals(builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser().getConfiguration(), givenFreeMarkerConfiguration);
		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertFalse(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);
	}

	@Test
	public void springThymeLeafFreeMarker() {
		// Given
		Mockito.when(propertiesBridgeMock.convert(Mockito.any(Environment.class))).thenReturn(new Properties());

		SpringTemplateEngine givenSpringTemplateEngine = Mockito.mock(SpringTemplateEngine.class);
		Mockito.when(appContextMock.getBean(SpringTemplateEngine.class)).thenReturn(givenSpringTemplateEngine);
		Configuration givenFreeMarkerConfiguration = Mockito.mock(Configuration.class);
		Mockito.when(appContextMock.getBean(Configuration.class)).thenReturn(givenFreeMarkerConfiguration);

		// When
		MessagingBuilder builder = autoConfiguration.messagingServiceBuilder(propertiesBridgeMock);

		// Then
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertEquals(builder.getEmailBuilder().getTemplateBuilder().getFreeMarkerParser().getConfiguration(), givenFreeMarkerConfiguration);
		Assert.assertNotNull(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertTrue(builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);

		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser());
		Assert.assertEquals(builder.getSmsBuilder().getTemplateBuilder().getFreeMarkerParser().getConfiguration(), givenFreeMarkerConfiguration);
		Assert.assertNotNull(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser());
		Assert.assertTrue(builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser().getEngine() instanceof SpringTemplateEngine);
	}
}
