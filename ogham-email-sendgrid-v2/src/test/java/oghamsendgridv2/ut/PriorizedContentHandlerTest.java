package oghamsendgridv2.ut;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.email.exception.handler.ContentHandlerException;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.PriorizedContentHandler;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.StringContentHandler;

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
		instance.setContent(null, new SendGrid.Email(), null);
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
		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent("Insignificant");

		instance.setContent(null, email, content);
	}

	@Test
	public void setContent() throws ContentHandlerException {
		instance.register(StringContent.class, handler);

		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent("Insignificant");

		instance.setContent(null, email, content);

		verify(handler).setContent(null, email, content);
	}

	@Test(expected = ContentHandlerException.class)
	public void setContent_handlerFailure() throws ContentHandlerException {
		instance.register(StringContent.class, handler);

		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent("Insignificant");

		final ContentHandlerException e = new ContentHandlerException("Thrown by mock", mock(Content.class));
		doThrow(e).when(handler).setContent(null, email, content);

		instance.setContent(null, email, content);
	}

}
