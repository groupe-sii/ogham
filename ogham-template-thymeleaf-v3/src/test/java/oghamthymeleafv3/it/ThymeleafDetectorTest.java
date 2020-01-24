package oghamthymeleafv3.it;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3SmsBuilder;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class ThymeleafDetectorTest {
	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();

	TemplateEngineDetector detector;
	
	@Before
	public void setUp() {
		detector = new ThymeleafV3SmsBuilder()
					.environment()
						.systemProperties()
						.and()
					.classpath()
						.lookup("classpath:", "")
						.pathPrefix("/template/thymeleaf/source/")
						.and()
					.file()
						.lookup("file:")
						.pathPrefix("/template/thymeleaf/source/")
						.and()
					.string()
						.lookup("s:", "string:")
						.and()
					.buildDetector();
	}
	
	@Test
	public void found() throws EngineDetectionException {
		boolean canParse = detector.canParse(new UnresolvedPath("classpath:simple.html"), null);
		assertTrue("template found and is thymeleaf template", canParse);
	}
	
	@Test
	public void foundWithoutNamespace() throws EngineDetectionException {
		boolean canParse = detector.canParse(new UnresolvedPath("classpath:simple.txt"), null);
		assertTrue("template found and is thymeleaf template", canParse);
	}
	
	@Test
	public void notFound() throws EngineDetectionException {
		boolean canParse = detector.canParse(new UnresolvedPath("classpath:unexisting.html"), null);
		assertFalse("template doesn't exist", canParse);
	}
	
	@Test
	public void foundButNotThymeleaf() throws EngineDetectionException {
		boolean canParse = detector.canParse(new UnresolvedPath("classpath:/template/other/not-thymeleaf-template.html"), null);
		assertFalse("template found but not thymeleaf template", canParse);
	}
	
	@Test
	public void foundButEmpty() throws EngineDetectionException {
		boolean canParse = detector.canParse(new UnresolvedPath("classpath:/template/other/empty.html"), null);
		assertTrue("template found but empty", canParse);
	}
	
}
