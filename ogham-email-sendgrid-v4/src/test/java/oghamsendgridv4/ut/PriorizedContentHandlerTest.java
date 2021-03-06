package oghamsendgridv4.ut;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.sendgrid.helpers.mail.Mail;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.email.exception.handler.ContentHandlerException;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat.CorrectPackageNameMailCompat;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat.MailCompat;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.PriorizedContentHandler;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.StringContentHandler;

/**
 * Test campaign for the {@link PriorizedContentHandler} class.
 */
public final class PriorizedContentHandlerTest {

	private SendGridContentHandler handler;
	private PriorizedContentHandler instance;


	@Before
	public void setUp() {
		handler = mock(SendGridContentHandler.class, RETURNS_SMART_NULLS);
		instance = new PriorizedContentHandler();
	}

	@Test(expected = IllegalArgumentException.class)
	public void emailParamCannotBeNull() throws ContentHandlerException {
		instance.setContent(null, null, new StringContent(""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void contentParamCannotBeNull() throws ContentHandlerException {
		instance.setContent(null, new CorrectPackageNameMailCompat(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void providerParamCannotBeNull() {
		new StringContentHandler(null);
	}


	@Test(expected = IllegalArgumentException.class)
	public void register_handlerParamCannotBeNull() {
		instance.register(StringContent.class, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void register_clazzParamCannotBeNull() {
		instance.register((Class<? extends Content>) null, handler);
	}

	@Test(expected = ContentHandlerException.class)
	public void setContent_noMatchingHandler() throws ContentHandlerException {
		final Mail email = new Mail();
		final StringContent content = new StringContent("Insignificant");
		final MailCompat compat = new CorrectPackageNameMailCompat(email);

		instance.setContent(null, compat, content);
	}

	@Test
	public void setContent() throws ContentHandlerException {
		instance.register(StringContent.class, handler);

		final Mail email = new Mail();
		final StringContent content = new StringContent("Insignificant");
		final MailCompat compat = new CorrectPackageNameMailCompat(email);

		instance.setContent(null, compat, content);

		verify(handler).setContent(null, compat, content);
	}

	@Test(expected = ContentHandlerException.class)
	public void setContent_handlerFailure() throws ContentHandlerException {
		instance.register(StringContent.class, handler);

		final Mail email = new Mail();
		final StringContent content = new StringContent("Insignificant");
		final MailCompat compat = new CorrectPackageNameMailCompat(email);

		final ContentHandlerException e = new ContentHandlerException("Thrown by mock", mock(Content.class));
		doThrow(e).when(handler).setContent(null, compat, content);

		instance.setContent(null, compat, content);
	}

}
