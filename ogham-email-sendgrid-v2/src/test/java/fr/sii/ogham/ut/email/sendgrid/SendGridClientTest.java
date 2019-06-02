package fr.sii.ogham.ut.email.sendgrid;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.DelegateSendGridClient;

public final class SendGridClientTest {

	private SendGrid delegate;
	private DelegateSendGridClient instance;

	public SendGridClientTest() {
		super();
	}

	@Before
	public void setUp() {
		delegate = mock(SendGrid.class, RETURNS_SMART_NULLS);
		instance = new DelegateSendGridClient(delegate);
	}

	@Test(expected = IllegalArgumentException.class)
	public void sendEmailParamCannotBeNull() throws SendGridException {
		instance.send(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorDelegateParamCannotBeNull() {
		new DelegateSendGridClient((SendGrid) null);
	}

	@Test
	public void send() throws SendGridException {
		final SendGrid.Response response = new SendGrid.Response(200, "OK");
		final SendGrid.Email exp = new SendGrid.Email();

		when(delegate.send(exp)).thenReturn(response);

		instance.send(exp);

		verify(delegate).send(exp);
	}

	@Test(expected = SendGridException.class)
	public void send_errorResponse() throws SendGridException {
		final SendGrid.Response response = new SendGrid.Response(403, "FORBIDDEN");
		final SendGrid.Email exp = new SendGrid.Email();

		when(delegate.send(exp)).thenReturn(response);

		instance.send(exp);
	}

}
