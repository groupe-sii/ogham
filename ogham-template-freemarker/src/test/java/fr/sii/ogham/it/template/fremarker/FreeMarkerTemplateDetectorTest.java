package fr.sii.ogham.it.template.fremarker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.template.freemarker.FreeMarkerTemplateDetector;

public class FreeMarkerTemplateDetectorTest {
	private TemplateEngineDetector detector;

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Before
	public void setUp() {
		detector = new FreeMarkerTemplateDetector();
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
}
