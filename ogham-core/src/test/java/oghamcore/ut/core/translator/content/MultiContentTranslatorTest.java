package oghamcore.ut.core.translator.content;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	@Rule public final LoggingTestRule logging = new LoggingTestRule();
			
	
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
		
		NoContentException e = assertThrows("should throw", NoContentException.class, () -> {
			translator.translate(content);
		});
		assertThat("should indicate why", e.getMessage(), is("The message is empty"));
	}
	
	@Test
	public void bothTemplatesNotFoundShouldThrowNoContentExceptionWithDetails() throws ContentTranslatorException {
		when(templateParser.translate(textTemplate)).thenThrow(new TemplateNotFoundException("text template not found"));
		when(templateParser.translate(htmlTemplate)).thenThrow(new TemplateNotFoundException("html template not found"));
		
		NoContentException e = assertThrows("should throw", NoContentException.class, () -> {
			translator.translate(content);
		});
		assertThat("should indicate why", e.getMessage(), is("The message is empty maybe due to some errors:\ntext template not found\nhtml template not found"));
	}
	
	
	@Test
	public void parsingTextTemplateFailsShouldThrowAnError() throws ContentTranslatorException {
		when(templateParser.translate(textTemplate)).thenThrow(new TemplateParsingFailedException("failed to parse text template"));
		when(templateParser.translate(htmlTemplate)).thenReturn(html);

		TemplateParsingFailedException e = assertThrows("should throw", TemplateParsingFailedException.class, () -> {
			translator.translate(content);
		});
		assertThat("should indicate why", e.getMessage(), is("failed to parse text template"));
	}
}
