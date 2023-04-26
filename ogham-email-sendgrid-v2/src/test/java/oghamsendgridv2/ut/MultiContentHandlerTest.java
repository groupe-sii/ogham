package oghamsendgridv2.ut;

import com.sendgrid.SendGrid;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.email.exception.handler.ContentHandlerException;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.MultiContentHandler;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.StringContentHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test campaign for the {@link MultiContentHandler} class.
 */
public final class MultiContentHandlerTest {

	private SendGridContentHandler delegate;
	private MultiContentHandler instance;


	@BeforeEach
	public void setUp() {
		delegate = mock(SendGridContentHandler.class, RETURNS_SMART_NULLS);
		this.instance = new MultiContentHandler(delegate);
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
	public void constructor_delegateParamCannotBeNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MultiContentHandler(null);
		});
	}

	@Test
	public void setContent_single() throws ContentHandlerException {
		final Content exp = new StringContent("Insignificant");
		final Content content = new MultiContent(exp);
		final SendGrid.Email email = new SendGrid.Email();

		instance.setContent(null, email, content);

		verify(delegate).setContent(any(), any(SendGrid.Email.class), eq(exp));
	}

	@Test
	public void setContent_multiple() throws ContentHandlerException {
		final Content exp1 = new StringContent("Insignificant 1");
		final Content exp2 = new StringContent("Insignificant 2");
		final Content content = new MultiContent(exp1, exp2);
		final SendGrid.Email email = new SendGrid.Email();

		instance.setContent(null, email, content);

		verify(delegate).setContent(any(), any(SendGrid.Email.class), eq(exp1));
		verify(delegate).setContent(any(), any(SendGrid.Email.class), eq(exp2));
	}

	@Test
	public void setContent_notMultiContent() throws ContentHandlerException {
		final Content content = new StringContent("Insignificant");
		final SendGrid.Email email = new SendGrid.Email();

		assertThrows(IllegalArgumentException.class, () -> {
			instance.setContent(null, email, content);
		});
	}

	@Test
	public void setContent_delegateFailure() throws ContentHandlerException {
		final Content exp = new StringContent("Insignificant");
		final Content content = new MultiContent(exp);
		final SendGrid.Email email = new SendGrid.Email();

		final ContentHandlerException e = new ContentHandlerException("Thrown by mock", exp);
		doThrow(e).when(delegate).setContent(null, email, exp);

		assertThrows(ContentHandlerException.class, () -> {
			instance.setContent(null, email, content);
		});
	}

}
