package fr.sii.ogham.ut.email.sendgrid;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;

import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.SendGridException;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.DelegateSendGridClient;

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
	public void send() throws SendGridException, IOException {
		final Response response = new Response(200, "OK", null);
		final Mail exp = new Mail();

		when(delegate.api(any())).thenReturn(response);

		instance.send(exp);

		verify(delegate).api(any());
	}

	@Test(expected = SendGridException.class)
	public void send_errorResponse() throws SendGridException, IOException {
		final Response response = new Response(403, "FORBIDDEN", null);
		final Mail exp = new Mail();

		when(delegate.api(any())).thenReturn(response);

		instance.send(exp);
	}

}
