package fr.sii.notification.ut.template;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.sii.notification.core.builder.TemplateBuilder;
import fr.sii.notification.core.exception.template.ParseException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.template.context.BeanContext;
import fr.sii.notification.core.template.parser.TemplateParser;
import fr.sii.notification.helper.AssertTemplate;
import fr.sii.notification.mock.context.NestedBean;
import fr.sii.notification.mock.context.SimpleBean;
import fr.sii.notification.template.thymeleaf.builder.ThymeleafBuilder;

public class ThymeleafParserTest {
	private TemplateParser parser;
	
	@Before
	public void setUp() {
		parser = new TemplateBuilder()
					.registerTemplateParser(new ThymeleafBuilder())
					.useDefaultLookupResolvers()
					.withPrefix("/template/thymeleaf/source/")
					.build();
	}
	
	@Test
	public void html() throws ParseException, IOException {
		Content content = parser.parse("classpath:simple.html", new BeanContext(new SimpleBean("foo", 42)));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertEquals("/template/thymeleaf/expected/simple_foo_42.html", content);
	}
	
	@Test
	public void text() throws ParseException, IOException {
		Content content = parser.parse("classpath:simple.txt", new BeanContext(new SimpleBean("foo", 42)));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertEquals("/template/thymeleaf/expected/simple_foo_42.txt", content);
	}
	
	@Test
	public void nested() throws ParseException, IOException {
		Content content = parser.parse("classpath:nested.html", new BeanContext(new NestedBean(new SimpleBean("foo", 42))));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertEquals("/template/thymeleaf/expected/nested_foo_42.html", content);
	}
}
