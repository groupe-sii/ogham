package fr.sii.ogham.sample.test;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;

public class LongSmsTestSample {
	private MessagingService oghamService;
	
	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();
	
	@Before
	public void setUp() throws IOException {
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties()
						.set("ogham.sms.from", "+33603040506")
						.set("ogham.sms.smpp.host", "localhost")
						.set("ogham.sms.smpp.port", String.valueOf(smppServer.getPort()))
						.and()
					.and()
				.build();
	}
	
	@Test
	public void longMessageUsingGsm8bit() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Sms()
							.content("sms content with a very very very loooooooooo"
									+ "oooooooooonnnnnnnnnnnnnnnnng message that is"
									+ " over 140 characters in order to test the be"
									+ "havior of the sender when message has to be split")
							.to("0601020304"));
		assertThat(smppServer).receivedMessages()
			.count(is(2))																	// <1>
			.message(0)																		// <2>
				.content(is("sms content with a very very very looooooooooooooooooo"		// <3>
						+ "onnnnnnnnnnnnnnnnng message that is over 140 characters "
						+ "in order to test the beh")).and()
			.message(1)																		// <4>
				.content(is("avior of the sender when message has to be split")).and()		// <5>
			.every()																		// <6>
				.from()
					.number(is("+33603040506"))												// <7>
					.and()
				.to()
					.number(is("0601020304"));												// <8>
		// @formatter:on
	}
}
