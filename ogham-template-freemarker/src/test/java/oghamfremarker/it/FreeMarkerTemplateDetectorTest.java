package oghamfremarker.it;

import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.resource.path.ResolvedResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.template.freemarker.FreeMarkerTemplateDetector;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

@LogTestInformation
@MockitoSettings(strictness = LENIENT)
public class FreeMarkerTemplateDetectorTest {
	private TemplateEngineDetector detector;

	@Mock ResourceResolver resolver;
	@Mock Resource resource;
	
	@BeforeEach
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
		assertTrue(detector.canParse(new UnresolvedPath("template.txt.ftl"), null), "should parse any filename ending with .ftl");
	}

	@Test
	public void html() throws EngineDetectionException {
		assertTrue(detector.canParse(new UnresolvedPath("template.html.ftl"), null), "should parse any filename ending with .ftl");
	}

	@Test
	public void notFTL() throws EngineDetectionException {
		assertFalse(detector.canParse(new UnresolvedPath("template.txtl"), null), "should not parse any filename not ending with .ftl");
	}


	@Test
	public void hasFtlExtensionButFileDoesntExistShouldNotBeAbleToParse() throws EngineDetectionException {
		assertFalse(detector.canParse(new UnresolvedPath("template.ftl"), null), "should not parse any filename ending with .ftl that doesn't exist");
	}
}
