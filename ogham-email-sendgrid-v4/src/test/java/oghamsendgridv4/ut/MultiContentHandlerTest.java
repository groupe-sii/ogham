package oghamsendgridv4.ut;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.sendgrid.helpers.mail.Mail;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.email.exception.handler.ContentHandlerException;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.MultiContentHandler;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.StringContentHandler;

/**
 * Test campaign for the {@link MultiContentHandler} class.
 */
public final class MultiContentHandlerTest {

	private SendGridContentHandler delegate;
	private MultiContentHandler instance;


	@Before
	public void setUp() {
		delegate = mock(SendGridContentHandler.class, RETURNS_SMART_NULLS);
		this.instance = new MultiContentHandler(delegate);
	}

	@Test(expected = IllegalArgumentException.class)
	public void emailParamCannotBeNull() throws ContentHandlerException {
		instance.setContent(null, null, new StringContent(""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void contentParamCannotBeNull() throws ContentHandlerException {
		instance.setContent(null, new Mail(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void providerParamCannotBeNull() {
		new StringContentHandler(null);
	}


	@Test(expected = IllegalArgumentException.class)
	public void constructor_delegateParamCannotBeNull() {
		new MultiContentHandler(null);
	}

	@Test
	public void setContent_single() throws ContentHandlerException {
		final Content exp = new StringContent("Insignificant");
		final Content content = new MultiContent(exp);
		final Mail email = new Mail();

		instance.setContent(null, email, content);

		verify(delegate).setContent(any(), any(Mail.class), eq(exp));
	}

	@Test
	public void setContent_multiple() throws ContentHandlerException {
		final Content exp1 = new StringContent("Insignificant 1");
		final Content exp2 = new StringContent("Insignificant 2");
		final Content content = new MultiContent(exp1, exp2);
		final Mail email = new Mail();

		instance.setContent(null, email, content);

		verify(delegate).setContent(any(), any(Mail.class), eq(exp1));
		verify(delegate).setContent(any(), any(Mail.class), eq(exp2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void setContent_notMultiContent() throws ContentHandlerException {
		final Content content = new StringContent("Insignificant");
		final Mail email = new Mail();

		instance.setContent(null, email, content);
	}

	@Test(expected = ContentHandlerException.class)
	public void setContent_delegateFailure() throws ContentHandlerException {
		final Content exp = new StringContent("Insignificant");
		final Content content = new MultiContent(exp);
		final Mail email = new Mail();

		final ContentHandlerException e = new ContentHandlerException("Thrown by mock", exp);
		doThrow(e).when(delegate).setContent(null, email, exp);
		instance.setContent(null, email, content);
	}

}
