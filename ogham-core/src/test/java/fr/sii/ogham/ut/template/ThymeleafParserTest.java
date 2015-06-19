package fr.sii.ogham.ut.template;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.builder.TemplateBuilder;
import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.template.context.BeanContext;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.helper.template.AssertTemplate;
import fr.sii.ogham.mock.context.NestedBean;
import fr.sii.ogham.mock.context.SimpleBean;
import fr.sii.ogham.template.thymeleaf.builder.ThymeleafBuilder;

public class ThymeleafParserTest {
	private TemplateParser parser;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Before
	public void setUp() {
		parser = new TemplateBuilder()
					.registerTemplateParser(new ThymeleafBuilder())
					.useDefaultResolvers()
					.withPrefix("/template/thymeleaf/source/")
					.build();
	}
	
	@Test
	public void html() throws ParseException, IOException {
		Content content = parser.parse("classpath:simple.html", new BeanContext(new SimpleBean("foo", 42)));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertSimilar("/template/thymeleaf/expected/simple_foo_42.html", content);
	}
	
	@Test
	public void text() throws ParseException, IOException {
		Content content = parser.parse("classpath:simple.txt", new BeanContext(new SimpleBean("foo", 42)));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertSimilar("/template/thymeleaf/expected/simple_foo_42.txt", content);
	}
	
	@Test
	public void nested() throws ParseException, IOException {
		Content content = parser.parse("classpath:nested.html", new BeanContext(new NestedBean(new SimpleBean("foo", 42))));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertSimilar("/template/thymeleaf/expected/nested_foo_42.html", content);
	}
	
	@Test
	public void layout() throws ParseException, IOException {
		Content content = parser.parse("classpath:layout.html", new BeanContext(new NestedBean(new SimpleBean("foo", 42))));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertSimilar("/template/thymeleaf/expected/layout_foo_42.html", content);
	}
	
	@Test(expected=ParseException.class)
	public void invalid() throws ParseException, IOException {
		parser.parse("classpath:invalid.html", new BeanContext(new NestedBean(new SimpleBean("foo", 42))));
	}
}
