package oghamthymeleafv2.it.resolver;

import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.resource.path.ResolvedString;
import fr.sii.ogham.core.template.context.BeanContext;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.thymeleaf.v2.buider.ThymeleafV2SmsBuilder;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import mock.context.SimpleBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static fr.sii.ogham.testing.assertion.template.AssertTemplate.assertEquals;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@LogTestInformation
public class StringResourceResolverTest {

	TemplateParser parser;

	@BeforeEach
	public void setup() {
		parser = new ThymeleafV2SmsBuilder()
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
		assertNotNull(content, "content should not be null");
		assertTrue(content instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/simple_foo_42.txt", content);
	}
	
	@Test
	public void html() throws ParseException, IOException {
		Content content = parser.parse(new ResolvedString(resourceAsString("/template/thymeleaf/source/simple.html"), "s:"), new BeanContext(new SimpleBean("foo", 42)));
		assertNotNull(content, "content should not be null");
		assertTrue(content instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/simple_foo_42.html", content);
	}
}
