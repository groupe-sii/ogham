package fr.sii.ogham.testing.helper.sms.rule.config;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.testing.helper.sms.rule.config.annotation.Slow;
import fr.sii.ogham.testing.helper.sms.rule.config.annotation.SmppServerConfig;

/**
 * Builder to generate a {@link SimulatorConfiguration}.
 * 
 * @author Aurélien Baudet
 *
 */
public class ServerConfig {
	private final List<Credentials> credentials = new ArrayList<>();
	private SmppServerConfig annotationConfig;
	private SlowConfig slowConfig;

	/**
	 * Register allowed credentials.
	 * 
	 * @param systemId
	 *            the system_id
	 * @param password
	 *            the password
	 * @return this instance for fluent chaining
	 */
	public ServerConfig credentials(String systemId, String password) {
		credentials.add(new Credentials(systemId, password));
		return this;
	}

	/**
	 * Configure the simulator to behave like a slow server.
	 * 
	 * @return the builder to control slow delays
	 */
	public SlowConfig slow() {
		if (slowConfig == null) {
			slowConfig = new SlowConfig(this);
		}
		return slowConfig;
	}

	/**
	 * Merge with configuration provided by annotation.
	 * 
	 * The configuration provided by the annotation takes precedence over
	 * configuration using builder methods. The aim is to be able to provide
	 * common configuration for the test class using JUnit rules and be able to
	 * override some configuration for a particular test.
	 * 
	 * @param annotationConfig
	 *            the annotation configuration
	 * @return this instance for fluent chaining
	 */
	public ServerConfig annotationConfig(SmppServerConfig annotationConfig) {
		this.annotationConfig = annotationConfig;
		return this;
	}

	/**
	 * Create the final {@link SimulatorConfiguration} that is used by the SMPP
	 * server.
	 * 
	 * @return the simulator configuration
	 */
	public SimulatorConfiguration build() {
		SimulatorConfiguration config = new SimulatorConfiguration();
		config.setCredentials(buildCredentials());
		config.setServerDelays(buildServerDelays());
		return config;
	}

	private List<Credentials> buildCredentials() {
		if (annotationConfig != null) {
			return Arrays.stream(annotationConfig.credentials()).map(c -> new Credentials(c.systemId(), c.password())).collect(toList());
		}
		return credentials;
	}

	private ServerDelays buildServerDelays() {
		if (annotationConfig != null) {
			return buildServerDelays(annotationConfig.slow());
		}
		if (slowConfig != null) {
			return slowConfig.build();
		}
		return null;
	}

	private static ServerDelays buildServerDelays(Slow slow) {
		ServerDelays delays = new ServerDelays();
		delays.setSendAlertNotificationDelay(slow.sendAlertNotificationDelay());
		delays.setSendBindDelay(slow.sendBindDelay());
		delays.setSendBindRespDelay(slow.sendBindRespDelay());
		delays.setSendCancelSmDelay(slow.sendCancelSmDelay());
		delays.setSendCancelSmRespDelay(slow.sendCancelSmRespDelay());
		delays.setSendDataSmDelay(slow.sendDataSmDelay());
		delays.setSendDataSmRespDelay(slow.sendDataSmRespDelay());
		delays.setSendDeliverSmDelay(slow.sendDeliverSmDelay());
		delays.setSendDeliverSmRespDelay(slow.sendDeliverSmRespDelay());
		delays.setSendEnquireLinkDelay(slow.sendEnquireLinkDelay());
		delays.setSendEnquireLinkRespDelay(slow.sendEnquireLinkRespDelay());
		delays.setSendGenericNackDelay(slow.sendGenericNackDelay());
		delays.setSendHeaderDelay(slow.sendHeaderDelay());
		delays.setSendOutbindDelay(slow.sendOutbindDelay());
		delays.setSendQuerySmDelay(slow.sendQuerySmDelay());
		delays.setSendQuerySmRespDelay(slow.sendQuerySmRespDelay());
		delays.setSendReplaceSmDelay(slow.sendReplaceSmDelay());
		delays.setSendReplaceSmRespDelay(slow.sendReplaceSmRespDelay());
		delays.setSendSubmiMultiDelay(slow.sendSubmiMultiDelay());
		delays.setSendSubmitMultiRespDelay(slow.sendSubmitMultiRespDelay());
		delays.setSendSubmitSmDelay(slow.sendSubmitSmDelay());
		delays.setSendSubmitSmRespDelay(slow.sendSubmitSmRespDelay());
		delays.setSendUnbindDelay(slow.sendUnbindDelay());
		delays.setSendUnbindRespDelay(slow.sendUnbindRespDelay());
		return delays;
	}
}
