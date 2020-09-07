package fr.sii.ogham.sample.springboot.email;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.icegreen.greenmail.junit4.GreenMailRule;

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.spring.GreenMailInitializer;
import mock.MockApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MockApplication.class, properties = {
	"mail.smtp.host=127.0.0.1",
	"mail.smtp.port=${greenmail.smtp.port}"                              // <1>
})
@ContextConfiguration(initializers = GreenMailInitializer.class)         // <2>
public class RandomSmtpPortJUnit4TestSample {
	@Rule @Autowired public GreenMailRule greenMail;                     // <3>
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