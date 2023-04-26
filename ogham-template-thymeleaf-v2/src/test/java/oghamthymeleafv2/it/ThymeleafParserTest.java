package oghamthymeleafv2.it;

import fr.sii.ogham.core.exception.template.ParseException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.template.context.BeanContext;
import fr.sii.ogham.core.template.context.LocaleContext;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.template.thymeleaf.v2.buider.ThymeleafV2SmsBuilder;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import mock.context.NestedBean;
import mock.context.SimpleBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static fr.sii.ogham.testing.assertion.template.AssertTemplate.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@LogTestInformation
public class ThymeleafParserTest {
	private TemplateParser parser;
	private Date date;
	

	@BeforeEach
	public void setUp() {
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
		Calendar cal = Calendar.getInstance();
		cal.set(2015, 6, 1, 14, 28, 42);
		date = cal.getTime();
	}
	
	@Test
	public void html() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:simple.html"), new BeanContext(new SimpleBean("foo", 42)));
		assertNotNull(content, "content should not be null");
		assertTrue(content instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/simple_foo_42.html", content);
	}
	
	@Test
	public void text() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:simple.txt"), new BeanContext(new SimpleBean("foo", 42)));
		assertNotNull(content, "content should not be null");
		assertTrue(content instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/simple_foo_42.txt", content);
	}
	
	@Test
	public void nested() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:nested.html"), new BeanContext(new NestedBean(new SimpleBean("foo", 42))));
		assertNotNull(content, "content should not be null");
		assertTrue(content instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/nested_foo_42.html", content);
	}
	
	@Test
	public void layout() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:layout.html"), new BeanContext(new NestedBean(new SimpleBean("foo", 42))));
		assertNotNull(content, "content should not be null");
		assertTrue(content instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/layout_foo_42.html", content);
	}
	
	@Test
	public void french() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:locale.txt"), new LocaleContext(new SimpleBean("foo", 42, date), Locale.FRENCH));
		assertNotNull(content, "content should not be null");
		assertTrue(content instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/locale_foo_42_fr.txt", content);
	}
	
	@Test
	public void english() throws ParseException, IOException {
		Content content = parser.parse(new UnresolvedPath("classpath:locale.txt"), new LocaleContext(new SimpleBean("foo", 42, date), Locale.ENGLISH));
		assertNotNull(content, "content should not be null");
		assertTrue(content instanceof MayHaveStringContent, "content should be MayHaveStringContent");
		assertEquals("/template/thymeleaf/expected/locale_foo_42_en.txt", content);
	}
	
	@Test
	public void invalid() throws ParseException, IOException {
		assertThrows(ParseException.class, () -> {
			parser.parse(new UnresolvedPath("classpath:invalid.html"), new BeanContext(new NestedBean(new SimpleBean("foo", 42))));
		});
	}
}
