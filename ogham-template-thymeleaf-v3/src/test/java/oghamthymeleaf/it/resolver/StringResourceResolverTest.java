package oghamthymeleaf.it.resolver;

import static fr.sii.ogham.testing.assertion.OghamAssertions.resourceAsString;
import static fr.sii.ogham.testing.helper.template.AssertTemplate.assertSimilar;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.resource.path.ResolvedString;
import fr.sii.ogham.core.template.context.BeanContext;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3SmsBuilder;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import mock.context.SimpleBean;

public class StringResourceResolverTest {
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
	
	TemplateParser parser;

	@Before
	public void setup() {
		parser = new ThymeleafV3SmsBuilder()
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
				.build();
	}
	
	@Test
	public void text() throws ParseException, IOException {
		Content content = parser.parse(new ResolvedString(resourceAsString("/template/thymeleaf/source/simple.txt"), "s:"), new BeanContext(new SimpleBean("foo", 42)));
		assertNotNull("content should not be null", content);
		assertTrue("content should be MayHaveStringContent", content instanceof MayHaveStringContent);
		assertSimilar("/template/thymeleaf/expected/simple_foo_42.txt", content);
	}
	
	@Test
	public void html() throws ParseException, IOException {
		Content content = parser.parse(new ResolvedString(resourceAsString("/template/thymeleaf/source/simple.html"), "s:"), new BeanContext(new SimpleBean("foo", 42)));
		assertNotNull("content should not be null", content);
		assertTrue("content should be MayHaveStringContent", content instanceof MayHaveStringContent);
		assertSimilar("/template/thymeleaf/expected/simple_foo_42.html", content);
	}
}
