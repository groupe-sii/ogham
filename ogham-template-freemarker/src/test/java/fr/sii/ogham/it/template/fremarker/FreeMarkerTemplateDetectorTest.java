package fr.sii.ogham.it.template.fremarker;

import static org.mockito.ArgumentMatchers.eq;
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
import fr.sii.ogham.core.resource.path.ResolvedResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
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
	public void setUp() throws ResourceResolutionException {
		when(resolver.resolve(eq(new UnresolvedPath("template.txt.ftl")))).thenReturn(new ResolvedResourcePath(new UnresolvedPath("template.txt.ftl"), "", "template.txt.ftl"));
		when(resolver.getResource(eq(new ResolvedResourcePath(new UnresolvedPath("template.txt.ftl"), "", "template.txt.ftl")))).thenReturn(resource);
		when(resolver.resolve(eq(new UnresolvedPath("template.html.ftl")))).thenReturn(new ResolvedResourcePath(new UnresolvedPath("template.html.ftl"), "", "template.html.ftl"));
		when(resolver.getResource(eq(new ResolvedResourcePath(new UnresolvedPath("template.html.ftl"), "", "template.html.ftl")))).thenReturn(resource);
		when(resolver.resolve(eq(new UnresolvedPath("template.txtl")))).thenReturn(new ResolvedResourcePath(new UnresolvedPath("template.txtl"), "", "template.txtl"));
		when(resolver.getResource(eq(new ResolvedResourcePath(new UnresolvedPath("template.txtl"), "", "template.txtl")))).thenReturn(resource);
		when(resolver.resolve(eq(new UnresolvedPath("template.ftl")))).thenReturn(new ResolvedResourcePath(new UnresolvedPath("template.ftl"), "", "template.ftl"));
		when(resolver.getResource(eq(new ResolvedResourcePath(new UnresolvedPath("template.ftl"), "", "template.ftl")))).thenThrow(ResourceResolutionException.class);
		detector = new FreeMarkerTemplateDetector(resolver);
	}

	@Test
	public void text() throws EngineDetectionException {
		Assert.assertTrue("should parse any filename ending with .ftl", detector.canParse(new UnresolvedPath("template.txt.ftl"), null));
	}

	@Test
	public void html() throws EngineDetectionException {
		Assert.assertTrue("should parse any filename ending with .ftl", detector.canParse(new UnresolvedPath("template.html.ftl"), null));
	}

	@Test
	public void notFTL() throws EngineDetectionException {
		Assert.assertFalse("should not parse any filename not ending with .ftl", detector.canParse(new UnresolvedPath("template.txtl"), null));
	}


	@Test
	public void hasFtlExtensionButFileDoesntExistShouldNotBeAbleToParse() throws EngineDetectionException {
		Assert.assertFalse("should not parse any filename ending with .ftl that doesn't exist", detector.canParse(new UnresolvedPath("template.ftl"), null));
	}
}
