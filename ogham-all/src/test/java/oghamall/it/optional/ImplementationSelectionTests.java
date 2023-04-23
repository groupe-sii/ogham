package oghamall.it.optional;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.core.util.classpath.SimpleClasspathHelper;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.mock.classloader.FilterableClassLoader;

@RunWith(MockitoJUnitRunner.class)
public class ImplementationSelectionTests {

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Before
	public void setUp() throws IOException {
		SimpleClasspathHelper helper = new SimpleClasspathHelper();
		helper.setClassLoader(new FilterableClassLoader(getClass().getClassLoader(), c -> !c.equals("jakarta.mail.Transport")));
		ClasspathUtils.setHelper(helper);
		Properties additionalProps = new Properties();
		additionalProps.setProperty("mail.smtp.host", "localhost");
		additionalProps.setProperty("mail.smtp.port", "25");
		MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties(additionalProps)
					.and()
				.build();
	}
	
	@Test
	public void javaMailAvailable() {
		assertFalse(ClasspathUtils.exists("jakarta.mail.Transport"));
	}
	
	@After
	public void reset() {
		ClasspathUtils.reset();
	}
}
