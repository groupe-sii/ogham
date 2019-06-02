package fr.sii.ogham.ut.email.sendgrid;

import static fr.sii.ogham.SendGridTestUtils.getHtml;
import static fr.sii.ogham.SendGridTestUtils.getText;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sendgrid.helpers.mail.Mail;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.StringContentHandler;

/**
 * Test campaign for the {@link StringContentHandler} class.
 */
@RunWith(MockitoJUnitRunner.class)
public final class StringContentHandlerTest {

	private static final String CONTENT_TEXT = "This is a simple text content.";
	private static final String CONTENT_HTML = "<!DOCTYPE html><html><body><p>This is a simple text content.</p></body></html>";

	/**
	 * Content type that StringContentHandler is not compatible with.
	 */
	@Mock
	private Content testContent;

	private final StringContent content = new StringContent(CONTENT_TEXT);
	private MimeTypeProvider provider;
	private SendGridContentHandler instance;


	@Before
	public void setUp() {
		provider = mock(MimeTypeProvider.class, RETURNS_SMART_NULLS);
		instance = new StringContentHandler(provider);
	}

	@Test(expected = IllegalArgumentException.class)
	public void emailParamCannotBeNull() throws ContentHandlerException {
		instance.setContent(null, content);
	}

	@Test(expected = IllegalArgumentException.class)
	public void contentParamCannotBeNull() throws ContentHandlerException {
		instance.setContent(new Mail(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void providerParamCannotBeNull() {
		new StringContentHandler(null);
	}

	@Test
	public void setContent_text() throws ContentHandlerException, MimeTypeDetectionException, MimeTypeParseException {
		final Mail email = new Mail();

		final MimeType mime = new MimeType("text/plain");
		when(provider.detect(CONTENT_TEXT)).thenReturn(mime);

		instance.setContent(email, content);

		assertEquals("The email was not correctly updated", CONTENT_TEXT, getText(email));
	}

	@Test
	public void setContent_html() throws ContentHandlerException, MimeTypeDetectionException, MimeTypeParseException {
		final Mail email = new Mail();
		final StringContent content = new StringContent(CONTENT_HTML);

		final MimeType mime = new MimeType("text/html");
		when(provider.detect(CONTENT_HTML)).thenReturn(mime);

		instance.setContent(email, content);

		assertEquals("The email was not correctly updated", CONTENT_HTML, getHtml(email));
	}


	@Test(expected = IllegalArgumentException.class)
	public void setContent_badContentType() throws ContentHandlerException {
		final Mail email = new Mail();

		instance.setContent(email, testContent);
	}

	@Test(expected = ContentHandlerException.class)
	public void setContent_providerFailure() throws ContentHandlerException, MimeTypeDetectionException {
		final Mail email = new Mail();
		final Content content = new StringContent(CONTENT_TEXT);

		final MimeTypeDetectionException exception = new MimeTypeDetectionException("Sent by mock");
		when(provider.detect(CONTENT_TEXT)).thenThrow(exception);

		instance.setContent(email, content);
	}

}
