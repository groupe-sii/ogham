package fr.sii.notification.email;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.BuildException;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;

public class ManualEmailTest {

	private NotificationService notificationService;
	
	@Before
	public void setUp() throws BuildException, IOException {
		Properties props = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/application.properties"));
		notificationService = new NotificationBuilder().withAllDefaults(props).build();
	}
	
	@Test
	public void simple() throws NotificationException {
		notificationService.send(new Email("Test", "body", "abaudet@sii.fr"));
	}

	@Test
	public void template() {
//		notificationService.send(new Email("aurelien.baudet@gmail.com", "abaudet@sii.fr", "Test", new Template("classpath:/email/test.html", new Context()));
//		notificationService.send(new Email("aurelien.baudet@gmail.com", "abaudet@sii.fr", "Test", new FallbackContent(new Template("classpath:/email/test.html", new Context(), new Template("classpath:/email/test.txt", new Context())));
	}

	@Test
	public void attachment() {
//		notificationService.send(new Email("aurelien.baudet@gmail.com", "abaudet@sii.fr", "Test", "body", new FileAttachment(new File("toto.pdf"))));
//		notificationService.send(new Email("aurelien.baudet@gmail.com", "abaudet@sii.fr", "Test", "body", new FileAttachment("classpath:/toto.pdf")));
//		notificationService.send(new Email("aurelien.baudet@gmail.com", "abaudet@sii.fr", "Test", "body", new FileAttachment("toto.pdf", getClass().getResourceAsStream(""))));
	}
}
