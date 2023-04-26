package oghamsendgridv2.ut;

import com.sendgrid.SendGrid;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.email.exception.handler.ContentHandlerException;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.PriorizedContentHandler;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.StringContentHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Test campaign for the {@link PriorizedContentHandler} class.
 */
public final class PriorizedContentHandlerTest {

	private SendGridContentHandler handler;
	private PriorizedContentHandler instance;


	@BeforeEach
	public void setUp() {
		handler = mock(SendGridContentHandler.class, RETURNS_SMART_NULLS);
		instance = new PriorizedContentHandler();
	}

	@Test
	public void emailParamCannotBeNull() throws ContentHandlerException {
		assertThrows(IllegalArgumentException.class, () -> {
			instance.setContent(null, null, new StringContent(""));
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
	public void register_handlerParamCannotBeNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			instance.register(StringContent.class, null);
		});
	}

	@Test
	public void register_clazzParamCannotBeNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			instance.register((Class<? extends Content>) null, handler);
		});
	}

	@Test
	public void setContent_noMatchingHandler() throws ContentHandlerException {
		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent("Insignificant");

		assertThrows(ContentHandlerException.class, () -> {
			instance.setContent(null, email, content);
		});
	}

	@Test
	public void setContent() throws ContentHandlerException {
		instance.register(StringContent.class, handler);

		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent("Insignificant");

		instance.setContent(null, email, content);

		verify(handler).setContent(null, email, content);
	}

	@Test
	public void setContent_handlerFailure() throws ContentHandlerException {
		instance.register(StringContent.class, handler);

		final SendGrid.Email email = new SendGrid.Email();
		final StringContent content = new StringContent("Insignificant");

		final ContentHandlerException e = new ContentHandlerException("Thrown by mock", mock(Content.class));
		doThrow(e).when(handler).setContent(null, email, content);

		assertThrows(ContentHandlerException.class, () -> {
			instance.setContent(null, email, content);
		});
	}

}
