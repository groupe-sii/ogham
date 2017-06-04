package fr.sii.ogham.it.template.fremarker;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.ResourcePath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.template.freemarker.FreeMarkerTemplateDetector;

public class FreeMarkerTemplateDetectorTest {
	private TemplateEngineDetector detector;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final MockitoRule mockito = MockitoJUnit.rule();
	
	@Mock ResourceResolver resolver;
	@Mock Resource resource;
	
	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws ResourceResolutionException {
		when(resolver.getResourcePath(eq("template.txt.ftl"))).thenReturn(new ResourcePath("template.txt.ftl", "", "template.txt.ftl"));
		when(resolver.getResource(eq("template.txt.ftl"))).thenReturn(resource);
		when(resolver.getResourcePath(eq("template.html.ftl"))).thenReturn(new ResourcePath("template.html.ftl", "", "template.html.ftl"));
		when(resolver.getResource(eq("template.html.ftl"))).thenReturn(resource);
		when(resolver.getResourcePath(eq("template.txtl"))).thenReturn(new ResourcePath("template.txtl", "", "template.txtl"));
		when(resolver.getResource(eq("template.txtl"))).thenReturn(resource);
		when(resolver.getResourcePath(eq("template.ftl"))).thenReturn(new ResourcePath("template.ftl", "", "template.ftl"));
		when(resolver.getResource(eq("template.ftl"))).thenThrow(ResourceResolutionException.class);
		detector = new FreeMarkerTemplateDetector(resolver);
	}

	@Test
	public void text() throws EngineDetectionException {
		Assert.assertTrue("should parse any filename ending with .ftl", detector.canParse("template.txt.ftl", null));
	}

	@Test
	public void html() throws EngineDetectionException {
		Assert.assertTrue("should parse any filename ending with .ftl", detector.canParse("template.html.ftl", null));
	}

	@Test
	public void notFTL() throws EngineDetectionException {
		Assert.assertFalse("should not parse any filename not ending with .ftl", detector.canParse("template.txtl", null));
	}


	@Test
	public void hasFtlExtensionButFileDoesntExistShouldNotBeAbleToParse() throws EngineDetectionException {
		Assert.assertFalse("should not parse any filename ending with .ftl that doesn't exist", detector.canParse("template.ftl", null));
	}
}
