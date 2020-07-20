package fr.sii.ogham.sample.springboot.sms;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import org.jsmpp.bean.SubmitSm;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import fr.sii.ogham.testing.extension.spring.JsmppServerRandomPortInitializer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest({
	"ogham.sms.smpp.host=127.0.0.1",
	"ogham.sms.smpp.port=${jsmpp.server.port}"                                   // <1>
})
@ContextConfiguration(initializers = JsmppServerRandomPortInitializer.class)     // <2>
public class RandomSmppPortTestSample {
	@Rule @Autowired public SmppServerRule<SubmitSm> smppServer;                 // <3>
	
	@Test
	public void foo() throws Exception {
		// some code to test your application here 
		// that sends SMS through SMPP
		assertThat(smppServer).receivedMessages()                                // <4>
			.count(is(1));
	}
}