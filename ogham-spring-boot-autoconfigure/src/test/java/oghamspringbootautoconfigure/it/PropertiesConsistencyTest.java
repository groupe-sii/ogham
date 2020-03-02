package oghamspringbootautoconfigure.it;

import static org.junit.Assert.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import mock.MockApplication;

/**
 * Test that check if properties defined by Ogham are well-formed and 
 * consistent with Spring properties. 
 * 
 * To do that, there is a script that auto-generates all properties defined
 * by OghamSee .tools/properties-consistency
 * 
 * 
 * @author Aurélien Baudet
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MockApplication.class, webEnvironment = NONE)
@ActiveProfiles("consistency-check")
public class PropertiesConsistencyTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Autowired
	MessagingBuilder builder;

	@Test
	public void contextLoads() {
		assertNotNull("Builder should not be null", builder);
	}

}
