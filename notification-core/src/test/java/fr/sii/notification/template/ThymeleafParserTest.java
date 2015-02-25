package fr.sii.notification.template;

import org.junit.Before;
import org.junit.Test;

import fr.sii.notification.core.exception.BuildException;
import fr.sii.notification.core.exception.ParseException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.template.context.BeanContext;
import fr.sii.notification.core.template.parser.TemplateParser;
import fr.sii.notification.template.thymeleaf.builder.ThymeleafBuilder;

public class ThymeleafParserTest {
	private TemplateParser parser;
	
	@Before
	public void setUp() throws BuildException {
		parser = new ThymeleafBuilder().withDefaultLookupMappings().withPrefix("thymeleaf/").withSuffix(".html").build();
	}
	
	@Test
	public void simple() throws ParseException {
		Content content = parser.parse("classpath:simple", new BeanContext(new BeanSample("foo", "bar")));
		System.out.println(content.toString());
	}
}
