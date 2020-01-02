package oghamcore.ut.core.translator.content;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.handler.NoContentException;
import fr.sii.ogham.core.exception.handler.TemplateNotFoundException;
import fr.sii.ogham.core.exception.handler.TemplateParsingFailedException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.ParsedContent;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.translator.content.MultiContentTranslator;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;


public class MultiContentTranslatorTest {
	public final ExpectedException thrown = ExpectedException.none();

	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	@Rule public final RuleChain rules = RuleChain
			.outerRule(new LoggingTestRule())
			.around(thrown);
			
	
	@Mock ContentTranslator templateParser;
	MultiContentTranslator translator;
	
	@Mock Content textTemplate;
	@Mock Content htmlTemplate;
	@Mock ParsedContent text;
	@Mock ParsedContent html;
	MultiContent content;
	
	@Before
	public void setup() {
		content = new MultiContent(textTemplate, htmlTemplate);
		translator = new MultiContentTranslator(templateParser);
	}
	
	@Test
	public void bothTemplatesParsedShouldReturnTwoParsedContent() throws ContentTranslatorException {
		when(templateParser.translate(textTemplate)).thenReturn(text);
		when(templateParser.translate(htmlTemplate)).thenReturn(html);
		Content result = translator.translate(content);
		assertThat("is multi-content", result, instanceOf(MultiContent.class));
		assertThat("two parsed contents", ((MultiContent) result).getContents(), contains(text, html));
	}
	
	@Test
	public void nullTextShouldReturnOneParsedContent() throws ContentTranslatorException {
		when(templateParser.translate(textTemplate)).thenReturn(null);
		when(templateParser.translate(htmlTemplate)).thenReturn(html);
		Content result = translator.translate(content);
		assertThat("is multi-content", result, instanceOf(MultiContent.class));
		assertThat("one parsed content", ((MultiContent) result).getContents(), contains(html));
	}
	
	@Test
	public void textTemplateNotFoundShouldSkipTheErrorAndReturnOneParsedContent() throws ContentTranslatorException {
		when(templateParser.translate(textTemplate)).thenThrow(TemplateNotFoundException.class);
		when(templateParser.translate(htmlTemplate)).thenReturn(html);
		Content result = translator.translate(content);
		assertThat("is multi-content", result, instanceOf(MultiContent.class));
		assertThat("one parsed content", ((MultiContent) result).getContents(), contains(html));
	}
	
	@Test
	public void bothNullShouldThrowNoContentException() throws ContentTranslatorException {
		when(templateParser.translate(textTemplate)).thenReturn(null);
		when(templateParser.translate(htmlTemplate)).thenReturn(null);
		thrown.expect(NoContentException.class);
		thrown.expectMessage("The message is empty");
		translator.translate(content);
	}
	
	@Test
	public void bothTemplatesNotFoundShouldThrowNoContentExceptionWithDetails() throws ContentTranslatorException {
		when(templateParser.translate(textTemplate)).thenThrow(new TemplateNotFoundException("text template not found"));
		when(templateParser.translate(htmlTemplate)).thenThrow(new TemplateNotFoundException("html template not found"));
		thrown.expect(NoContentException.class);
		thrown.expectMessage("The message is empty maybe due to some errors:\ntext template not found\nhtml template not found");
		translator.translate(content);
	}
	
	
	@Test
	public void parsingTextTemplateFailsShouldThrowAnError() throws ContentTranslatorException {
		when(templateParser.translate(textTemplate)).thenThrow(new TemplateParsingFailedException("failed to parse text template"));
		when(templateParser.translate(htmlTemplate)).thenReturn(html);
		thrown.expect(TemplateParsingFailedException.class);
		thrown.expectMessage("failed to parse text template");
		translator.translate(content);
	}
}
