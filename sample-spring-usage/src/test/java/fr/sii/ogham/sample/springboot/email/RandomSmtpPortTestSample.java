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

import com.icegreen.greenmail.junit.GreenMailRule;

import fr.sii.ogham.testing.extension.spring.GreenMailRandomSmtpPortInitializer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest({
	"mail.smtp.host=127.0.0.1",
	"mail.smtp.port=${greenmail.smtp.port}"                                      // <1>
})
@ContextConfiguration(initializers = GreenMailRandomSmtpPortInitializer.class)   // <2>
public class RandomSmtpPortTestSample {
	@Rule @Autowired public GreenMailRule greenMail;                             // <3>
	
	@Test
	public void foo() throws Exception {
		// some code to test your application here 
		// that sends email through SMTP
		assertThat(greenMail).receivedMessages()                                 // <4>
			.count(is(1));
	}
}