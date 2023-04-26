package oghamthymeleafv2.it;

import fr.sii.ogham.core.exception.template.EngineDetectionException;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.template.thymeleaf.v2.buider.ThymeleafV2SmsBuilder;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@LogTestInformation
public class ThymeleafDetectorTest {
	TemplateEngineDetector detector;
	
	@BeforeEach
	public void setUp() {
		detector = new ThymeleafV2SmsBuilder()
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
		assertTrue(canParse, "template found and is thymeleaf template");
	}
	
	@Test
	public void notFound() throws EngineDetectionException {
		boolean canParse = detector.canParse(new UnresolvedPath("classpath:unexisting.html"), null);
		assertFalse(canParse, "template doesn't exist");
	}
	
	@Test
	public void foundButNotThymeleaf() throws EngineDetectionException {
		boolean canParse = detector.canParse(new UnresolvedPath("classpath:/template/other/not-thymeleaf-template.html"), null);
		assertFalse(canParse, "template found but not thymeleaf template");
	}
	
	@Test
	public void foundButEmpty() throws EngineDetectionException {
		boolean canParse = detector.canParse(new UnresolvedPath("classpath:/template/other/empty.html"), null);
		assertTrue(canParse, "template found but empty");
	}
	
}
