package fr.sii.ogham.testing.extension.junit.sms.config;

import static fr.sii.ogham.testing.extension.junit.sms.config.SlowConfig.noWait;
import static fr.sii.ogham.testing.extension.junit.sms.config.SlowConfig.waitFor;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.testing.sms.simulator.config.Awaiter;
import fr.sii.ogham.testing.sms.simulator.config.Credentials;
import fr.sii.ogham.testing.sms.simulator.config.ServerDelays;
import fr.sii.ogham.testing.sms.simulator.config.SimulatorConfiguration;

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
	private boolean keepMessages;

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
	 * If the server is restarted, it indicates if received messages in the
	 * previous session should be kept (true) or dropped (false).
	 * 
	 * @param keep
	 *            indicate if messages should be kept or not between sessions
	 * @return this instance for fluent chaining
	 */
	public ServerConfig keepMessages(boolean keep) {
		keepMessages = keep;
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
		config.setKeepMessages(keepMessages);
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
		delays.setSendAlertNotificationWaiting(toAwaiter(slow.sendAlertNotificationDelay()));
		delays.setSendBindWaiting(toAwaiter(slow.sendBindDelay()));
		delays.setSendBindRespWaiting(toAwaiter(slow.sendBindRespDelay()));
		delays.setSendCancelSmWaiting(toAwaiter(slow.sendCancelSmDelay()));
		delays.setSendCancelSmRespWaiting(toAwaiter(slow.sendCancelSmRespDelay()));
		delays.setSendDataSmWaiting(toAwaiter(slow.sendDataSmDelay()));
		delays.setSendDataSmRespWaiting(toAwaiter(slow.sendDataSmRespDelay()));
		delays.setSendDeliverSmWaiting(toAwaiter(slow.sendDeliverSmDelay()));
		delays.setSendDeliverSmRespWaiting(toAwaiter(slow.sendDeliverSmRespDelay()));
		delays.setSendEnquireLinkWaiting(toAwaiter(slow.sendEnquireLinkDelay()));
		delays.setSendEnquireLinkRespWaiting(toAwaiter(slow.sendEnquireLinkRespDelay()));
		delays.setSendGenericNackWaiting(toAwaiter(slow.sendGenericNackDelay()));
		delays.setSendHeaderWaiting(toAwaiter(slow.sendHeaderDelay()));
		delays.setSendOutbindWaiting(toAwaiter(slow.sendOutbindDelay()));
		delays.setSendQuerySmWaiting(toAwaiter(slow.sendQuerySmDelay()));
		delays.setSendQuerySmRespWaiting(toAwaiter(slow.sendQuerySmRespDelay()));
		delays.setSendReplaceSmWaiting(toAwaiter(slow.sendReplaceSmDelay()));
		delays.setSendReplaceSmRespWaiting(toAwaiter(slow.sendReplaceSmRespDelay()));
		delays.setSendSubmiMultiWaiting(toAwaiter(slow.sendSubmiMultiDelay()));
		delays.setSendSubmitMultiRespWaiting(toAwaiter(slow.sendSubmitMultiRespDelay()));
		delays.setSendSubmitSmWaiting(toAwaiter(slow.sendSubmitSmDelay()));
		delays.setSendSubmitSmRespWaiting(toAwaiter(slow.sendSubmitSmRespDelay()));
		delays.setSendUnbindWaiting(toAwaiter(slow.sendUnbindDelay()));
		delays.setSendUnbindRespWaiting(toAwaiter(slow.sendUnbindRespDelay()));
		return delays;
	}
	
	private static Awaiter toAwaiter(long delayMs) {
		if (delayMs == 0) {
			return noWait();
		}
		return waitFor(delayMs);
	}
}
