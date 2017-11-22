package fr.sii.ogham.it.template.thymeleaf;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.template.context.BeanContext;
import fr.sii.ogham.core.template.context.LocaleContext;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.helper.template.AssertTemplate;
import fr.sii.ogham.mock.context.NestedBean;
import fr.sii.ogham.mock.context.SimpleBean;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3SmsBuilder;

public class ThymeleafParserTest {
	private TemplateParser parser;
	private Date date;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	
	@Before
	public void setUp() {
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
		Calendar cal = Calendar.getInstance();
		cal.set(2015, 6, 1, 14, 28, 42);
		date = cal.getTime();
	}
	
	@Test
	public void html() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:simple.html"), new BeanContext(new SimpleBean("foo", 42)));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertSimilar("/template/thymeleaf/expected/simple_foo_42.html", content);
	}
	
	@Test
	public void text() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:simple.txt"), new BeanContext(new SimpleBean("foo", 42)));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertSimilar("/template/thymeleaf/expected/simple_foo_42.txt", content);
	}
	
	@Test
	public void nested() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:nested.html"), new BeanContext(new NestedBean(new SimpleBean("foo", 42))));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertSimilar("/template/thymeleaf/expected/nested_foo_42.html", content);
	}
	
	@Test
	public void layout() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:layout.html"), new BeanContext(new NestedBean(new SimpleBean("foo", 42))));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertSimilar("/template/thymeleaf/expected/layout_foo_42.html", content);
	}
	
	@Test
	public void french() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:locale.txt"), new LocaleContext(new SimpleBean("foo", 42, date), Locale.FRENCH));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertSimilar("/template/thymeleaf/expected/locale_foo_42_fr.txt", content);
	}
	
	@Test
	public void english() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:locale.txt"), new LocaleContext(new SimpleBean("foo", 42, date), Locale.ENGLISH));
		Assert.assertNotNull("content should not be null", content);
		Assert.assertTrue("content should be StringContent", content instanceof StringContent);
		AssertTemplate.assertSimilar("/template/thymeleaf/expected/locale_foo_42_en.txt", content);
	}
	
	@Test(expected=ParseException.class)
	public void invalid() throws ParseException, IOException {
		parser.parse(new UnresolvedPath("classpath:invalid.html"), new BeanContext(new NestedBean(new SimpleBean("foo", 42))));
	}
}
