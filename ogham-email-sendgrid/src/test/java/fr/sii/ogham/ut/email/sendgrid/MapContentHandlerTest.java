package fr.sii.ogham.ut.email.sendgrid;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.MapContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.StringContentHandler;

/**
 * Test campaign for the {@link MapContentHandler} class.
 */
public final class MapContentHandlerTest {

	private SendGridContentHandler handler;
	private MapContentHandler instance;


	@Before
	public void setUp() {
		handler = mock(SendGridContentHandler.class, RETURNS_SMART_NULLS);
		instance = new MapContentHandler();
	}

	@Test(expected = IllegalArgumentException.class)
	public void emailParamCannotBeNull() throws ContentHandlerException {
		instance.setContent(null, new StringContent(""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void contentParamCannotBeNull() throws ContentHandlerException {
		instance.setContent(new SendGrid.Email(), null);
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
		instance.register(null, handler);
	}

	@Test(expected = ContentHandlerException.class)
	public void setContent_noMatchingHandler() throws ContentHandlerException {
		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent("Insignificant");

		instance.setContent(email, content);
	}

	@Test
	public void setContent() throws ContentHandlerException {
		instance.register(StringContent.class, handler);

		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent("Insignificant");

		instance.setContent(email, content);

		verify(handler).setContent(email, content);
	}

	@Test(expected = ContentHandlerException.class)
	public void setContent_handlerFailure() throws ContentHandlerException {
		instance.register(StringContent.class, handler);

		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent("Insignificant");

		final ContentHandlerException e = new ContentHandlerException("Thrown by mock");
		doThrow(e).when(handler).setContent(email, content);

		instance.setContent(email, content);
	}

}
