package oghamall.it.env;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;

public class PropertyOverrideTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();

	// @formatter:off
	/**
	 * Properties are defined at several places:
	 * <ul>
	 * <li>System properties (external)</li>
	 * <li>In a property file (external configuration file)</li>
	 * <li>Explicitly in code</li>
	 * <li>In a property file (internal configuration file)</li>
	 * </ul>
	 * 
	 * <table>
	 * <caption>Property values for the test</caption>
	 * <thead>
	 * <tr><td>property</td><td>value in system properties</td><td>value in conf file outside app</td><td>value in code</td><td>value in conf file inside app</td></tr>
	 * </thead>
	 * <tbody>
	 * <tr><td>ogham.sms.smpp.host</td><td></td><td></td><td></td><td><strong>127.0.0.1</strong></td></tr>
	 * <tr><td>ogham.sms.smpp.port</td><td></td><td></td><td><strong>smppServer.getPort()</strong></td><td>port-from-properties</td></tr>
	 * <tr><td>ogham.sms.to</td><td></td><td><strong>0605040302</strong></td><td>recipient-from-code</td><td>recipient-from-properties</td></tr>
	 * <tr><td>ogham.sms.from</td><td><strong>0706050403</strong></td><td>sender-from-ext-file</td><td>sender-from-code</td><td>sender-from-properties</td></tr>
	 * </tbody>
	 * </table>
	 * 
	 * 
	 * The expected result:
	 * <ul>
	 * <li>ogham.sms.smpp.host=127.0.0.1</li>
	 * <li>ogham.sms.smpp.port=smppServer.getPort()</li>
	 * <li>ogham.sms.from=0706050403</li>
	 * <li>ogham.sms.to=0605040302</li>
	 * </ul>
	 * 
	 * @throws MessagingException
	 *             if host or port properties are not correctly overridden
	 * @throws IOException 
	 *             if external properties file couldn't be created
	 */
	// @formatter:on
	@Test
	public void externalThenPropertiesInCodeThenFile() throws MessagingException, IOException {
		File extProps = temp.newFile("ext.properties");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(extProps))) {
			writer.write("ogham.sms.from=sender-from-ext-file\n");
			writer.write("ogham.sms.to=0605040302");
		}
		// @formatter:off
		System.getProperties().setProperty("ogham.sms.from", "0706050403");
		MessagingService service = MessagingBuilder.standard()
				.environment()
					.properties()
						.set("ogham.sms.smpp.port", String.valueOf(smppServer.getPort()))
						.set("ogham.sms.from", "sender-from-code")
						.set("ogham.sms.to", "recipient-from-code")
						.and()
					.properties("props/sms.properties")
					.properties("file:"+extProps.getAbsolutePath())
					.and()
				.build();
		service.send(new Sms()
						.content("sms content"));
		assertThat(smppServer).receivedMessages()
			.count(is(1))
			.message(0)
				.content(is("sms content"))
				.from()
					.number(is("0706050403"))
					.and()
				.to()
					.number(is("0605040302"));
		// @formatter:on
	}

	@After
	public void clearProperties() {
		System.getProperties().remove("ogham.sms.from");
	}

}
