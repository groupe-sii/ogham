package fr.sii.ogham.testing.extension.junit.sms.config;

import fr.sii.ogham.testing.sms.simulator.config.Credentials;
import fr.sii.ogham.testing.sms.simulator.config.*;
import fr.sii.ogham.testing.util.RandomPortUtils;
import ogham.testing.io.github.resilience4j.retry.RetryConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fr.sii.ogham.testing.extension.junit.sms.config.SlowConfig.noWait;
import static fr.sii.ogham.testing.extension.junit.sms.config.SlowConfig.waitFor;
import static fr.sii.ogham.testing.util.RandomPortUtils.PORT_RANGE_MAX;
import static fr.sii.ogham.testing.util.RandomPortUtils.PORT_RANGE_MIN;
import static java.time.Duration.ofMillis;
import static java.util.stream.Collectors.toList;
import static ogham.testing.io.github.resilience4j.core.IntervalFunction.ofExponentialBackoff;

/**
 * Builder to generate a {@link SimulatorConfiguration}.
 *
 * <p>
 * Default configuration:
 * <ul>
 * <li>Starts on random port</li>
 * <li>No delay</li>
 * <li>No credentials</li>
 * <li>Do not keep messages between tests</li>
 * </ul>
 *
 * @author Aurélien Baudet
 */
public class ServerConfig {
    private PortConfig portConfig;
    private final List<Credentials> credentials = new ArrayList<>();
    private SmppServerConfig annotationConfig;
    private SlowConfig slowConfig;
    private boolean keepMessages;
    private RetryConfig startRetryConfig;

    /**
     * Start the server with a fixed port.
     *
     * @param port the port value
     * @return this instance for fluent chaining
     */
    public ServerConfig port(int port) {
        this.portConfig = new FixedPortConfig(port);
        return this;
    }

    /**
     * Start the server with a random port.
     * <p>
     * The port is contained in the range
     * [{@link RandomPortUtils#PORT_RANGE_MIN},
     * {@link RandomPortUtils#PORT_RANGE_MAX}].
     *
     * @return this instance for fluent chaining
     */
    public ServerConfig randomPort() {
        return randomPort(PORT_RANGE_MAX);
    }

    /**
     * Start the server with a random port.
     * <p>
     * The port is contained in the range
     * [{@link RandomPortUtils#PORT_RANGE_MIN}, {@code maxPort}].
     *
     * @param maxPort the maximum port value
     * @return this instance for fluent chaining
     */
    public ServerConfig randomPort(int maxPort) {
        return randomPort(PORT_RANGE_MIN, maxPort);
    }

    /**
     * Start the server with a random port.
     * <p>
     * The port is contained in the range [{@code minPort}, {@code maxPort}].
     *
     * @param minPort the minimum port value
     * @param maxPort the maximum port value
     * @return this instance for fluent chaining
     */
    public ServerConfig randomPort(int minPort, int maxPort) {
        this.portConfig = new RandomPortConfig(minPort, maxPort);
        return this;
    }

    /**
     * Register allowed credentials.
     *
     * @param systemId the system_id
     * @param password the password
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
     * <p>
     * The configuration provided by the annotation takes precedence over
     * configuration using builder methods. The aim is to be able to provide
     * common configuration for the test class using JUnit rules and be able to
     * override some configuration for a particular test.
     *
     * @param annotationConfig the annotation configuration
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
     * @param keep indicate if messages should be kept or not between sessions
     * @return this instance for fluent chaining
     */
    public ServerConfig keepMessages(boolean keep) {
        keepMessages = keep;
        return this;
    }

    /**
     * Server may not start correctly (because port was available but is not
     * available anymore for example).
     * <p>
     * In such case, the server will be restarted until it starts successfully, or
     * it fails due to maximum attempts reached.
     *
     * @param retryConfig the retry configuration
     * @return this instance for fluent chaining
     */
    public ServerConfig startRetry(RetryConfig retryConfig) {
        this.startRetryConfig = retryConfig;
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
        config.setPort(buildPort());
        config.setCredentials(buildCredentials());
        config.setServerDelays(buildServerDelays());
        config.setKeepMessages(keepMessages);
        config.setStartRetryConfig(buildStartRetryConfig());
        return config;
    }

    private ServerPortProvider buildPort() {
        if (portConfig == null) {
            return new RandomPortConfig(PORT_RANGE_MIN, PORT_RANGE_MAX).build();
        }
        return portConfig.build();
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

    private RetryConfig buildStartRetryConfig() {
        if (startRetryConfig != null) {
            return startRetryConfig;
        }
        return RetryConfig.custom()
                .maxAttempts(5)
                .intervalFunction(ofExponentialBackoff(ofMillis(100)))
                .failAfterMaxAttempts(true)
                .build();
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
        delays.setSendSubmitMultiWaiting(toAwaiter(slow.sendSubmitMultiDelay()));
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
