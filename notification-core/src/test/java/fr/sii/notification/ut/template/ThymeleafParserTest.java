package fr.sii.notification.ut.template;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.sii.notification.core.exception.template.ParseException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.template.context.BeanContext;
import fr.sii.notification.core.template.parser.TemplateParser;
import fr.sii.notification.helper.AssertTemplate;
import fr.sii.notification.mock.context.SimpleBean;
import fr.sii.notification.template.thymeleaf.builder.ThymeleafBuilder;

public class ThymeleafParserTest {
	private TemplateParser parser;
	
	@Before
	public void setUp() {
		parser = new ThymeleafBuilder().withDefaultLookupMappings().withPrefix("template/thymeleaf/source/").withSuffix(".html").build();
	}
	
	@Test
	public void simple() throws ParseException, IOException {
		Content content = parser.parse("classpath:simple", new BeanContext(new SimpleBean("foo", 42)));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertEquals("/template/thymeleaf/expected/simple_foo_42.html", "text/html", (StringContent) content);
	}
}
