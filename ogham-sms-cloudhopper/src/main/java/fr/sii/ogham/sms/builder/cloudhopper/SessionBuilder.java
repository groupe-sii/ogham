package fr.sii.ogham.sms.builder.cloudhopper;

import fr.sii.ogham.core.builder.BuildContext;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.retry.RetryBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.retry.FixedDelayRetry;

/**
 * Configures Cloudhopper session management (timeouts, retry, session name...).
 * 
 * @author Aurélien Baudet
 *
 */
public class SessionBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<CloudhopperSessionOptions> {
	private final BuildContext buildContext;
	private final ConfigurationValueBuilderHelper<SessionBuilder, Long> bindValueBuilder;
	private final ConfigurationValueBuilderHelper<SessionBuilder, Long> connectValueBuilder;
	private final ConfigurationValueBuilderHelper<SessionBuilder, Long> requestExpiryValueBuilder;
	private final ConfigurationValueBuilderHelper<SessionBuilder, Long> windowMonitorInvervalValueBuilder;
	private final ConfigurationValueBuilderHelper<SessionBuilder, Long> windowWaitValueBuilder;
	private final ConfigurationValueBuilderHelper<SessionBuilder, Integer> windowSizeValueBuilder;
	private final ConfigurationValueBuilderHelper<SessionBuilder, Long> writeValueBuilder;
	private final ConfigurationValueBuilderHelper<SessionBuilder, Long> responseValueBuilder;
	private final ConfigurationValueBuilderHelper<SessionBuilder, Long> unbindValueBuilder;
	private final ConfigurationValueBuilderHelper<SessionBuilder, String> sessionNameValueBuilder;
	private final ConfigurationValueBuilderHelper<SessionBuilder, Boolean> keepSessionValueBuilder;
	private RetryBuilder<SessionBuilder> connectRetryBuilder;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for property resolution and evaluation
	 */
	public SessionBuilder(CloudhopperBuilder parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
		bindValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class, buildContext);
		connectValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class, buildContext);
		requestExpiryValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class, buildContext);
		windowMonitorInvervalValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class, buildContext);
		windowWaitValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class, buildContext);
		windowSizeValueBuilder = new ConfigurationValueBuilderHelper<>(this, Integer.class, buildContext);
		writeValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class, buildContext);
		responseValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class, buildContext);
		unbindValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class, buildContext);
		sessionNameValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class, buildContext);
		keepSessionValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
	}

	/**
	 * A name for the session (used to name threads).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #sessionName()}.
	 * 
	 * <pre>
	 * .sessionName("my-name")
	 * .sessionName()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-name")
	 * </pre>
	 * 
	 * <pre>
	 * .sessionName("my-name")
	 * .sessionName()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-name")
	 * </pre>
	 * 
	 * In both cases, {@code sessionName("my-name")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param name
	 *            the name for the session
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder sessionName(String name) {
		sessionNameValueBuilder.setValue(name);
		return this;
	}

	/**
	 * A name for the session (used to name threads).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .sessionName()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-name")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #sessionName(String)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .sessionName("my-name")
	 * .sessionName()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-name")
	 * </pre>
	 * 
	 * The value {@code "my-name"} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SessionBuilder, String> sessionName() {
		return sessionNameValueBuilder;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for the success
	 * of a bind attempt to the SMSC. Defaults to 5000.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #bindTimeout()}.
	 * 
	 * <pre>
	 * .bindTimeout(1000L)
	 * .bindTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <pre>
	 * .bindTimeout(1000L)
	 * .bindTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * In both cases, {@code bindTimeout(1000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param timeout
	 *            the timeout value in milliseconds
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder bindTimeout(Long timeout) {
		bindValueBuilder.setValue(timeout);
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for the success
	 * of a bind attempt to the SMSC. Defaults to 5000.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .bindTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #bindTimeout(Long)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .bindTimeout(1000L)
	 * .bindTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * The value {@code 1000L} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SessionBuilder, Long> bindTimeout() {
		return bindValueBuilder;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for a
	 * establishing the connection. Defaults to 10000.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #connectTimeout()}.
	 * 
	 * <pre>
	 * .connectTimeout(1000L)
	 * .connectTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <pre>
	 * .connectTimeout(1000L)
	 * .connectTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * In both cases, {@code connectTimeout(1000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param timeout
	 *            the timeout in milliseconds
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder connectTimeout(Long timeout) {
		connectValueBuilder.setValue(timeout);
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for a
	 * establishing the connection. Defaults to 10000.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .connectTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #connectTimeout(Long)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .connectTimeout(1000L)
	 * .connectTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * The value {@code 1000L} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SessionBuilder, Long> connectTimeout() {
		return connectValueBuilder;
	}

	/**
	 * Configures how to handle a connection that fails. You can configure if
	 * retry should be done when a connection to the server couldn't be
	 * established. The builder lets you define the retry behavior.
	 * 
	 * For now, only a {@link FixedDelayRetry} is handled. The
	 * {@link FixedDelayRetry} needs a delay between two tries and a maximum
	 * attempts. In the future, we could handle different strategies if needed
	 * like retrying with an exponential delay for example.
	 * 
	 * If you don't configure it, no retry is applied at all.
	 * 
	 * @return the builder to configure the retry handling
	 */
	public RetryBuilder<SessionBuilder> connectRetry() {
		if (connectRetryBuilder == null) {
			connectRetryBuilder = new RetryBuilder<>(this, buildContext);
		}
		return connectRetryBuilder;
	}

	/**
	 * Set the amount of time (milliseconds) to wait for an endpoint to respond
	 * to a request before it expires. Defaults to disabled (-1).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #requestExpiryTimeout()}.
	 * 
	 * <pre>
	 * .requestExpiryTimeout(1000L)
	 * .requestExpiryTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(-1L)
	 * </pre>
	 * 
	 * <pre>
	 * .requestExpiryTimeout(1000L)
	 * .requestExpiryTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(-1L)
	 * </pre>
	 * 
	 * In both cases, {@code requestExpiryTimeout(1000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param timeout
	 *            the timeout in milliseconds
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder requestExpiryTimeout(Long timeout) {
		requestExpiryValueBuilder.setValue(timeout);
		return this;
	}

	/**
	 * Set the amount of time (milliseconds) to wait for an endpoint to respond
	 * to a request before it expires. Defaults to disabled (-1).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .requestExpiryTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(-1L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #requestExpiryTimeout(Long)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .requestExpiryTimeout(1000L)
	 * .requestExpiryTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(-1L)
	 * </pre>
	 * 
	 * The value {@code 1000L} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SessionBuilder, Long> requestExpiryTimeout() {
		return requestExpiryValueBuilder;
	}

	/**
	 * Sets the amount of time (milliseconds) between executions of monitoring
	 * the window for requests that expire. It's recommended that this generally
	 * either matches or is half the value of requestExpiryTimeout. Therefore,
	 * at worst a request would could take up 1.5X the requestExpiryTimeout to
	 * clear out. Defaults to -1 (disabled).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #windowMonitorInterval()}.
	 * 
	 * <pre>
	 * .windowMonitorInterval(1000L)
	 * .windowMonitorInterval()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(-1L)
	 * </pre>
	 * 
	 * <pre>
	 * .windowMonitorInterval(1000L)
	 * .windowMonitorInterval()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(-1L)
	 * </pre>
	 * 
	 * In both cases, {@code windowMonitorInterval(1000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param timeout
	 *            the tiemout in milliseconds
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder windowMonitorInterval(Long timeout) {
		windowMonitorInvervalValueBuilder.setValue(timeout);
		return this;
	}

	/**
	 * Sets the amount of time (milliseconds) between executions of monitoring
	 * the window for requests that expire. It's recommended that this generally
	 * either matches or is half the value of requestExpiryTimeout. Therefore,
	 * at worst a request would could take up 1.5X the requestExpiryTimeout to
	 * clear out. Defaults to -1 (disabled).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .windowMonitorInterval()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(-1L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #windowMonitorInterval(Long)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .windowMonitorInterval(1000L)
	 * .windowMonitorInterval()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(-1L)
	 * </pre>
	 * 
	 * The value {@code 1000L} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SessionBuilder, Long> windowMonitorInterval() {
		return windowMonitorInvervalValueBuilder;
	}

	/**
	 * Sets the maximum number of requests permitted to be outstanding
	 * (unacknowledged) at a given time. Must be &gt; 0. Defaults to 1.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #windowSize()}.
	 * 
	 * <pre>
	 * .windowSize(5)
	 * .windowSize()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(1)
	 * </pre>
	 * 
	 * <pre>
	 * .windowSize(5)
	 * .windowSize()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(1)
	 * </pre>
	 * 
	 * In both cases, {@code windowSize(5)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param size
	 *            the window size
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder windowSize(Integer size) {
		windowSizeValueBuilder.setValue(size);
		return this;
	}

	/**
	 * Sets the maximum number of requests permitted to be outstanding
	 * (unacknowledged) at a given time. Must be &gt; 0. Defaults to 1.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .windowSize()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(1)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #windowSize(Integer)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .windowSize(5)
	 * .windowSize()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(1)
	 * </pre>
	 * 
	 * The value {@code 5} is used regardless of the value of the properties and
	 * default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SessionBuilder, Integer> windowSize() {
		return windowSizeValueBuilder;
	}

	/**
	 * Set the amount of time (milliseconds) to wait until a slot opens up in
	 * the sendWindow. Defaults to 60000.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #windowWait()}.
	 * 
	 * <pre>
	 * .windowWait(10000L)
	 * .windowWait()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(60000L)
	 * </pre>
	 * 
	 * <pre>
	 * .windowWait(10000L)
	 * .windowWait()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(60000L)
	 * </pre>
	 * 
	 * In both cases, {@code windowWait(10000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param duration
	 *            the amount of time in milliseconds to wait until a slot opens
	 *            up
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder windowWait(Long duration) {
		windowWaitValueBuilder.setValue(duration);
		return this;
	}

	/**
	 * Set the amount of time (milliseconds) to wait until a slot opens up in
	 * the sendWindow. Defaults to 60000.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .windowWait()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(60000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #windowWait(Long)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .windowWait(10000L)
	 * .windowWait()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(60000L)
	 * </pre>
	 * 
	 * The value {@code 10000L} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SessionBuilder, Long> windowWait() {
		return windowWaitValueBuilder;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for bytes to be
	 * written when creating a new SMPP session. Defaults to 0 (no timeout, for
	 * backwards compatibility).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #writeTimeout()}.
	 * 
	 * <pre>
	 * .writeTimeout(10000L)
	 * .writeTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <pre>
	 * .writeTimeout(10000L)
	 * .writeTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * In both cases, {@code writeTimeout(10000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param timeout
	 *            the timeout in milliseconds
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder writeTimeout(Long timeout) {
		writeValueBuilder.setValue(timeout);
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for bytes to be
	 * written when creating a new SMPP session. Defaults to 0 (no timeout, for
	 * backwards compatibility).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .writeTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #writeTimeout(Long)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .writeTimeout(10000L)
	 * .writeTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * The value {@code 10000L} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SessionBuilder, Long> writeTimeout() {
		return writeValueBuilder;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait until a valid
	 * response is received when a "submit" request is synchronously sends to
	 * the remote endpoint. The timeout value includes both waiting for a
	 * "window" slot, the time it takes to transmit the actual bytes on the
	 * socket, and for the remote endpoint to send a response back. Defaults to
	 * 5000.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #responseTimeout()}.
	 * 
	 * <pre>
	 * .responseTimeout(1000L)
	 * .responseTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <pre>
	 * .responseTimeout(1000L)
	 * .responseTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * In both cases, {@code responseTimeout(1000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param timeout
	 *            the timeout in milliseconds
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder responseTimeout(Long timeout) {
		responseValueBuilder.setValue(timeout);
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait until a valid
	 * response is received when a "submit" request is synchronously sends to
	 * the remote endpoint. The timeout value includes both waiting for a
	 * "window" slot, the time it takes to transmit the actual bytes on the
	 * socket, and for the remote endpoint to send a response back. Defaults to
	 * 5000.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .responseTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #responseTimeout(Long)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .responseTimeout(1000L)
	 * .responseTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * The value {@code 1000L} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SessionBuilder, Long> responseTimeout() {
		return responseValueBuilder;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait until the
	 * session is unbounded, waiting up to a specified period of milliseconds
	 * for an unbind response from the remote endpoint. Regardless of whether a
	 * proper unbind response was received, the socket/channel is closed.
	 * Defaults to 5000.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #unbindTimeout()}.
	 * 
	 * <pre>
	 * .unbindTimeout(10000L)
	 * .unbindTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <pre>
	 * .unbindTimeout(10000L)
	 * .unbindTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * In both cases, {@code unbindTimeout(10000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param timeout
	 *            the timeout in milliseconds
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder unbindTimeout(Long timeout) {
		unbindValueBuilder.setValue(timeout);
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait until the
	 * session is unbounded, waiting up to a specified period of milliseconds
	 * for an unbind response from the remote endpoint. Regardless of whether a
	 * proper unbind response was received, the socket/channel is closed.
	 * Defaults to 5000.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .unbindTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #unbindTimeout(Long)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .unbindTimeout(10000L)
	 * .unbindTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * The value {@code 10000L} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SessionBuilder, Long> unbindTimeout() {
		return unbindValueBuilder;
	}

	/**
	 * Keep the previous session open instead of closing and reopening one.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #keepSession()}.
	 * 
	 * <pre>
	 * .keepSession(true)
	 * .keepSession()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <pre>
	 * .keepSession(true)
	 * .keepSession()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * In both cases, {@code keepSession(true)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param keepOpened
	 *            true to reuse same session for sending messages
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder keepSession(Boolean keepOpened) {
		keepSessionValueBuilder.setValue(keepOpened);
		return this;
	}

	/**
	 * Keep the previous session open instead of closing and reopening one.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .keepSession()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #keepSession(Boolean)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .keepSession(true)
	 * .keepSession()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * The value {@code true} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<SessionBuilder, Boolean> keepSession() {
		return keepSessionValueBuilder;
	}

	@Override
	public CloudhopperSessionOptions build() {
		CloudhopperSessionOptions sessionOpts = new CloudhopperSessionOptions();
		sessionOpts.setBindTimeout(bindValueBuilder.getValue());
		sessionOpts.setConnectTimeout(connectValueBuilder.getValue());
		sessionOpts.setRequestExpiryTimeout(requestExpiryValueBuilder.getValue());
		sessionOpts.setWindowMonitorInterval(windowMonitorInvervalValueBuilder.getValue());
		sessionOpts.setWindowSize(windowSizeValueBuilder.getValue());
		sessionOpts.setWindowWaitTimeout(windowWaitValueBuilder.getValue());
		sessionOpts.setWriteTimeout(writeValueBuilder.getValue());
		sessionOpts.setResponseTimeout(responseValueBuilder.getValue());
		sessionOpts.setUnbindTimeout(unbindValueBuilder.getValue());
		sessionOpts.setKeepSession(keepSessionValueBuilder.getValue(false));
		if (connectRetryBuilder != null) {
			sessionOpts.setConnectRetry(connectRetryBuilder.build());
		}
		return sessionOpts;
	}

}
