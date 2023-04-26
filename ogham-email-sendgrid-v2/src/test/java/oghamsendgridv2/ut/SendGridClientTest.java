package oghamsendgridv2.ut;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.DelegateSendGridClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public final class SendGridClientTest {

	private SendGrid delegate;
	private DelegateSendGridClient instance;

	public SendGridClientTest() {
		super();
	}

	@BeforeEach
	public void setUp() {
		delegate = mock(SendGrid.class, RETURNS_SMART_NULLS);
		instance = new DelegateSendGridClient(delegate);
	}

	@Test
	public void sendEmailParamCannotBeNull() throws SendGridException {
		assertThrows(IllegalArgumentException.class, () -> {
			instance.send(null);
		});
	}

	@Test
	public void constructorDelegateParamCannotBeNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new DelegateSendGridClient((SendGrid) null);
		});
	}

	@Test
	public void send() throws SendGridException {
		final SendGrid.Response response = new SendGrid.Response(200, "OK");
		final SendGrid.Email exp = new SendGrid.Email();

		when(delegate.send(exp)).thenReturn(response);

		instance.send(exp);

		verify(delegate).send(exp);
	}

	@Test
	public void send_errorResponse() throws SendGridException {
		final SendGrid.Response response = new SendGrid.Response(403, "FORBIDDEN");
		final SendGrid.Email exp = new SendGrid.Email();

		when(delegate.send(exp)).thenReturn(response);

		assertThrows(SendGridException.class, () -> {
			instance.send(exp);
		});
	}

}
