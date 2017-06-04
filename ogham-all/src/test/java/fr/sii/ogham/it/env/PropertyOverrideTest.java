package fr.sii.ogham.it.env;

import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import org.jsmpp.bean.SubmitSm;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.helper.sms.rule.SmppServerRule;
import fr.sii.ogham.sms.message.Sms;

public class PropertyOverrideTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	// @formatter:off
	/**
	 * Properties are defined at three places:
	 * <ul>
	 * <li>System properties (external)</li>
	 * <li>In a property file (internal configuration file)</li>
	 * <li>Explicitly in code</li>
	 * </ul>
	 * 
	 * <table>
	 * <thead>
	 * <tr><td>property</td><td>value in system properties</td><td>value in code</td><td>value in conf file</td></tr>
	 * </thead>
	 * <tbody>
	 * <tr><td>ogham.sms.smpp.host</td><td></td><td></td><td>127.0.0.1</td></tr>
	 * <tr><td>ogham.sms.smpp.port</td><td></td><td>smppServer.getPort()</td><td>port-from-properties</td></tr>
	 * <tr><td>ogham.sms.from</td><td>0706050403</td><td>sender-from-code</td><td>sender-from-properties</td></tr>
	 * </tbody>
	 * </table>
	 * 
	 * 
	 * The expected result:
	 * <ul>
	 * <li>ogham.sms.smpp.host=127.0.0.1</li>
	 * <li>ogham.sms.smpp.port=smppServer.getPort()</li>
	 * <li>ogham.sms.from=0706050403</li>
	 * </ul>
	 * 
	 * @throws MessagingException
	 *             if host or port properties are not correctly overridden
	 */
	// @formatter:on
	@Test
	public void externalThenPropertiesInCodeThenFile() throws MessagingException {
		// @formatter:off
		System.getProperties().setProperty("ogham.sms.from", "0706050403");
		MessagingService service = MessagingBuilder.standard()
				.environment()
					.properties("props/sms.properties")
					.properties()
						.set("ogham.sms.smpp.port", String.valueOf(smppServer.getPort()))
						.set("ogham.sms.from", "sender-from-code")
						.and()
					.and()
				.build();
		service.send(new Sms()
						.content("sms content")
						.to("0605040302"));
		assertThat(smppServer).receivedMessages()
			.count(is(1))
			.message(0)
				.content(is("sms content"))
				.from()
					.number(is("0706050403"));
		// @formatter:on
	}
	
	@After
	public void clearProperties() {
		System.getProperties().remove("ogham.sms.from");
	}
}
