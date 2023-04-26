package fr.sii.ogham.testing.sms.simulator.config;

import ogham.testing.io.github.resilience4j.retry.RetryConfig;

import java.util.List;

/**
 * Configuration for local server used to simulate a SMPP server.
 *
 * @author Aurélien Baudet
 */
public class SimulatorConfiguration {
    private ServerPortProvider portProvider;
    private List<Credentials> credentials;
    private ServerDelays serverDelays;
    private boolean keepMessages;
    private RetryConfig startRetryConfig;

    /**
     * Get the configuration that provides a port for the server
     *
     * @return the port configuration
     */
    public ServerPortProvider getPortProvider() {
        return portProvider;
    }

    /**
     * Set the configuration that provides a port for the server
     *
     * @param port the port configuration
     */
    public void setPort(ServerPortProvider port) {
        this.portProvider = port;
    }

    /**
     * Get the allowed credentials
     *
     * @return the allowed credentials
     */
    public List<Credentials> getCredentials() {
        return credentials;
    }

    /**
     * Set the allowed credentials
     *
     * @param credentials the allowed credentials
     */
    public void setCredentials(List<Credentials> credentials) {
        this.credentials = credentials;
    }

    /**
     * Control delays to simulate a slow server.
     *
     * @return the delay configuration
     */
    public ServerDelays getServerDelays() {
        return serverDelays;
    }

    /**
     * Control delays to simulate a slow server.
     *
     * @param serverDelays the delay configuration
     */
    public void setServerDelays(ServerDelays serverDelays) {
        this.serverDelays = serverDelays;
    }

    /**
     * If the server is restarted, it indicates if received messages in the
     * previous session should be kept (true) or dropped (false).
     *
     * @return indicate if messages should be kept or not between sessions
     */
    public boolean isKeepMessages() {
        return keepMessages;
    }

    /**
     * If the server is restarted, it indicates if received messages in the
     * previous session should be kept (true) or dropped (false).
     *
     * @param keepMessages indicate if messages should be kept or not between sessions
     */
    public void setKeepMessages(boolean keepMessages) {
        this.keepMessages = keepMessages;
    }

    /**
     * Server may not start correctly (because port was available but is not
     * available anymore for example).
     * <p>
     * In such case, the server will be restarted until it starts successfully, or
     * it fails due to maximum attempts reached.
     *
     * @return the retry config
     */
    public RetryConfig getStartRetryConfig() {
        return startRetryConfig;
    }

    /**
     * Server may not start correctly (because port was available but is not
     * available anymore for example).
     * <p>
     * In such case, the server will be restarted until it starts successfully, or
     * it fails due to maximum attempts reached.
     *
     * @param retryConfig the retry config
     */
    public void setStartRetryConfig(RetryConfig retryConfig) {
        this.startRetryConfig = retryConfig;
    }

    /**
     * Reset the configuration to be reused by the same server
     */
    public void reset() {
        portProvider.reset();
    }

}
