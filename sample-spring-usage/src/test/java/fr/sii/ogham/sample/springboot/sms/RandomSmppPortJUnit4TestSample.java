package fr.sii.ogham.sample.springboot.sms;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.spring.JsmppServerInitializer;
import mock.MockApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MockApplication.class, properties = {
	"ogham.sms.smpp.host=127.0.0.1",
	"ogham.sms.smpp.port=${jsmpp.server.port}"                                   // <1>
})
@ContextConfiguration(initializers = JsmppServerInitializer.class)               // <2>
public class RandomSmppPortJUnit4TestSample {
	@Rule @Autowired public SmppServerRule<SubmitSm> smppServer;                 // <3>
	@Autowired MessagingService messagingService;

	@Test
	public void foo() throws Exception {
		// some code to test your application here 
		// that sends SMS through SMPP
		messagingService.send(new Sms()
				.from("0000000000")
				.to("1111111111")
				.message().string("Random port"));
		// make assertions
		assertThat(smppServer).receivedMessages()                                // <4>
			.count(is(1));
	}
}