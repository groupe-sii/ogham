package oghamcore.ut.core.translator.content;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.handler.NoContentException;
import fr.sii.ogham.core.exception.handler.TemplateNotFoundException;
import fr.sii.ogham.core.exception.handler.TemplateParsingFailedException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.ParsedContent;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.translator.content.MultiContentTranslator;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

@LogTestInformation
@MockitoSettings(strictness = LENIENT)
public class MultiContentTranslatorTest {

	@Mock ContentTranslator templateParser;
	MultiContentTranslator translator;
	
	@Mock Content textTemplate;
	@Mock Content htmlTemplate;
	@Mock ParsedContent text;
	@Mock ParsedContent html;
	MultiContent content;
	
	@BeforeEach
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
		
		NoContentException e = assertThrows(NoContentException.class, () -> {
			translator.translate(content);
		});
		assertThat("should indicate why", e.getMessage(), is("The message is empty"));
	}
	
	@Test
	public void bothTemplatesNotFoundShouldThrowNoContentExceptionWithDetails() throws ContentTranslatorException {
		when(templateParser.translate(textTemplate)).thenThrow(new TemplateNotFoundException("text template not found"));
		when(templateParser.translate(htmlTemplate)).thenThrow(new TemplateNotFoundException("html template not found"));
		
		NoContentException e = assertThrows(NoContentException.class, () -> {
			translator.translate(content);
		});
		assertThat("should indicate why", e.getMessage(), is("The message is empty maybe due to some errors:\ntext template not found\nhtml template not found"));
	}
	
	
	@Test
	public void parsingTextTemplateFailsShouldThrowAnError() throws ContentTranslatorException {
		when(templateParser.translate(textTemplate)).thenThrow(new TemplateParsingFailedException("failed to parse text template"));
		when(templateParser.translate(htmlTemplate)).thenReturn(html);

		TemplateParsingFailedException e = assertThrows(TemplateParsingFailedException.class, () -> {
			translator.translate(content);
		});
		assertThat("should indicate why", e.getMessage(), is("failed to parse text template"));
	}
}
