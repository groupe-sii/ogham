package oghamsendgridv4.ut;

import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import fr.sii.ogham.email.sendgrid.sender.exception.SendGridException;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.DelegateSendGridClient;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat.CorrectPackageNameMailCompat;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.compat.MailCompat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
	public void send() throws SendGridException, IOException {
		final Response response = new Response(200, "OK", null);
		final Mail email = new Mail();
		final MailCompat exp = new CorrectPackageNameMailCompat(email);

		when(delegate.api(any())).thenReturn(response);

		instance.send(exp);

		verify(delegate).api(any());
	}

	@Test
	public void send_errorResponse() throws SendGridException, IOException {
		final Response response = new Response(403, "FORBIDDEN", null);
		final Mail email = new Mail();
		final MailCompat exp = new CorrectPackageNameMailCompat(email);

		when(delegate.api(any())).thenReturn(response);

		assertThrows(SendGridException.class, () -> {
			instance.send(exp);
		});
	}

}
