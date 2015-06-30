package fr.sii.ogham.ut.email.sender.impl.sendgrid;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.SendGridContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.MultiContentHandler;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.StringContentHandler;

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
	public void constructor_delegateParamCannotBeNull() {
		new MultiContentHandler(null);
	}

	@Test
	public void setContent_single() throws ContentHandlerException {
		final Content exp = new StringContent("Insignificant");
		final Content content = new MultiContent(exp);
		final SendGrid.Email email = new SendGrid.Email();

		instance.setContent(email, content);

		verify(delegate).setContent(any(SendGrid.Email.class), eq(exp));
	}

	@Test
	public void setContent_multiple() throws ContentHandlerException {
		final Content exp1 = new StringContent("Insignificant 1");
		final Content exp2 = new StringContent("Insignificant 2");
		final Content content = new MultiContent(exp1, exp2);
		final SendGrid.Email email = new SendGrid.Email();

		instance.setContent(email, content);

		verify(delegate).setContent(any(SendGrid.Email.class), eq(exp1));
		verify(delegate).setContent(any(SendGrid.Email.class), eq(exp2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void setContent_notMultiContent() throws ContentHandlerException {
		final Content content = new StringContent("Insignificant");
		final SendGrid.Email email = new SendGrid.Email();

		instance.setContent(email, content);
	}

	@Test(expected = ContentHandlerException.class)
	public void setContent_delegateFailure() throws ContentHandlerException {
		final Content exp = new StringContent("Insignificant");
		final Content content = new MultiContent(exp);
		final SendGrid.Email email = new SendGrid.Email();

		final ContentHandlerException e = new ContentHandlerException("Thrown by mock");
		doThrow(e).when(delegate).setContent(email, exp);
		instance.setContent(email, content);
	}

}
