package fr.sii.ogham.ut.email.sender.impl.sendgrid;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.junit.Before;
import org.junit.Test;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.StringContentHandler;

/**
 * Test campaign for the {@link StringContentHandler} class.
 */
public final class StringContentHandlerTest {

	private static final String CONTENT_TEXT = "This is a simple text content.";
	private static final String CONTENT_HTML = "<!DOCTYPE html><html><body><p>This is a simple text content.</p></body></html>";
	private static final String CONTENT_JSON = "{ format: \"unsupported\" }";

	/**
	 * Content type that StringContentHandler is not compatible with.
	 */
	private class TestContent implements Content {
	}

	private final StringContent content = new StringContent(CONTENT_TEXT);
	private MimeTypeProvider provider;
	private SendGridContentHandler instance;


	@Before
	public void setUp() {
		provider = mock(MimeTypeProvider.class, RETURNS_SMART_NULLS);
		instance = new StringContentHandler(provider);
	}

	@Test(expected = NullPointerException.class)
	public void emailParamCannotBeNull() throws ContentHandlerException {
		instance.setContent(null, content);
	}

	@Test(expected = NullPointerException.class)
	public void contentParamCannotBeNull() throws ContentHandlerException {
		instance.setContent(new SendGrid.Email(), null);
	}

	@Test(expected = NullPointerException.class)
	public void providerParamCannotBeNull() {
		new StringContentHandler(null);
	}

	@Test
	public void setContent_text() throws ContentHandlerException, MimeTypeDetectionException, MimeTypeParseException {
		final SendGrid.Email email = new SendGrid.Email();

		final MimeType mime = new MimeType("text/plain");
		when(provider.detect(CONTENT_TEXT)).thenReturn(mime);

		instance.setContent(email, content);

		assertEquals("The email was not correctly updated", CONTENT_TEXT, email.getText());
	}

	@Test
	public void setContent_html() throws ContentHandlerException, MimeTypeDetectionException, MimeTypeParseException {
		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent(CONTENT_HTML);

		final MimeType mime = new MimeType("text/html");
		when(provider.detect(CONTENT_HTML)).thenReturn(mime);

		instance.setContent(email, content);

		assertEquals("The email was not correctly updated", CONTENT_HTML, email.getHtml());
	}

	@Test(expected = ContentHandlerException.class)
	public void setContent_unknown() throws ContentHandlerException, MimeTypeDetectionException, MimeTypeParseException {
		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent(CONTENT_JSON);

		final MimeType mime = new MimeType("application/json");
		when(provider.detect(CONTENT_JSON)).thenReturn(mime);

		instance.setContent(email, content);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setContent_badContentType() throws ContentHandlerException {
		final SendGrid.Email email = new SendGrid.Email();
		final Content content = new TestContent();

		instance.setContent(email, content);
	}

	@Test(expected = ContentHandlerException.class)
	public void setContent_providerFailure() throws ContentHandlerException, MimeTypeDetectionException {
		final SendGrid.Email email = new SendGrid.Email();
		final Content content = new StringContent(CONTENT_TEXT);

		final MimeTypeDetectionException exception = new MimeTypeDetectionException("Sent by mock");
		when(provider.detect(CONTENT_TEXT)).thenThrow(exception);

		instance.setContent(email, content);
	}

}
