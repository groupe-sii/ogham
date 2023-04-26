package oghamall.it.optional;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.core.util.classpath.SimpleClasspathHelper;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.mock.classloader.FilterableClassLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;

@LogTestInformation
@MockitoSettings
public class ImplementationSelectionTests {

	@BeforeEach
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
	
	@AfterEach
	public void reset() {
		ClasspathUtils.reset();
	}
}
