package oghamsendgridv2.ut;

import com.sendgrid.SendGrid;
import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeType;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.mimetype.RawMimeType;
import fr.sii.ogham.email.exception.handler.ContentHandlerException;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.StringContentHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Test campaign for the {@link StringContentHandler} class.
 */
@MockitoSettings
public final class StringContentHandlerTest {

	private static final String CONTENT_TEXT = "This is a simple text content.";
	private static final String CONTENT_HTML = "<!DOCTYPE html><html><body><p>This is a simple text content.</p></body></html>";
	private static final String CONTENT_JSON = "{ format: \"unsupported\" }";

	/**
	 * Content type that StringContentHandler is not compatible with.
	 */
	@Mock
	private Content testContent;

	private final StringContent content = new StringContent(CONTENT_TEXT);
	private MimeTypeProvider provider;
	private SendGridContentHandler instance;


	@BeforeEach
	public void setUp() {
		provider = mock(MimeTypeProvider.class, RETURNS_SMART_NULLS);
		instance = new StringContentHandler(provider);
	}

	@Test
	public void emailParamCannotBeNull() throws ContentHandlerException {
		assertThrows(IllegalArgumentException.class, () -> {
			instance.setContent(null, null, content);
		});
	}

	@Test
	public void contentParamCannotBeNull() throws ContentHandlerException {
		assertThrows(IllegalArgumentException.class, () -> {
			instance.setContent(null, new SendGrid.Email(), null);
		});
	}

	@Test
	public void providerParamCannotBeNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new StringContentHandler(null);
		});
	}

	@Test
	public void setContent_text() throws ContentHandlerException, MimeTypeDetectionException {
		final SendGrid.Email email = new SendGrid.Email();

		final MimeType mime = new RawMimeType("text/plain");
		when(provider.detect(CONTENT_TEXT)).thenReturn(mime);

		instance.setContent(null, email, content);

		assertEquals(CONTENT_TEXT, email.getText(), "The email was not correctly updated");
	}

	@Test
	public void setContent_html() throws ContentHandlerException, MimeTypeDetectionException {
		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent(CONTENT_HTML);

		final MimeType mime = new RawMimeType("text/html");
		when(provider.detect(CONTENT_HTML)).thenReturn(mime);

		instance.setContent(null, email, content);

		assertEquals(CONTENT_HTML, email.getHtml(), "The email was not correctly updated");
	}

	@Test
	public void setContent_unknown() throws ContentHandlerException, MimeTypeDetectionException {
		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent(CONTENT_JSON);

		final MimeType mime = new RawMimeType("application/json");
		when(provider.detect(CONTENT_JSON)).thenReturn(mime);

		assertThrows(ContentHandlerException.class, () -> {
			instance.setContent(null, email, content);
		});
	}

	@Test
	public void setContent_badContentType() throws ContentHandlerException {
		final SendGrid.Email email = new SendGrid.Email();

		assertThrows(IllegalArgumentException.class, () -> {
			instance.setContent(null, email, testContent);
		});
	}

	@Test
	public void setContent_providerFailure() throws ContentHandlerException, MimeTypeDetectionException {
		final SendGrid.Email email = new SendGrid.Email();
		final Content content = new StringContent(CONTENT_TEXT);

		final MimeTypeDetectionException exception = new MimeTypeDetectionException("Sent by mock");
		when(provider.detect(CONTENT_TEXT)).thenThrow(exception);

		assertThrows(ContentHandlerException.class, () -> {
			instance.setContent(null, email, content);
		});
	}

}
