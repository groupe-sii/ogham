package fr.sii.ogham.sample.springboot.email;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.icegreen.greenmail.junit5.GreenMailExtension;

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.spring.GreenMailInitializer;
import mock.MockApplication;

@SpringBootTest(classes = MockApplication.class, properties = {
	"mail.smtp.host=127.0.0.1",
	"mail.smtp.port=${greenmail.smtp.port}"                              // <1>
})
@ContextConfiguration(initializers = GreenMailInitializer.class)         // <2>
public class RandomSmtpPortJUnit5TestSample {
	@RegisterExtension @Autowired public GreenMailExtension greenMail;   // <3>
	@Autowired MessagingService messagingService;
	
	@Test
	public void foo() throws Exception {
		// some code to test your application here 
		// that sends email through SMTP
		messagingService.send(new Email()
				.subject("Random port")
				.from("foo@yopmail.com")
				.to("bar@yopmail.com")
				.body().string("Random port sample"));
		// make assertions
		assertThat(greenMail).receivedMessages()                         // <4>
			.count(is(1));
	}
}