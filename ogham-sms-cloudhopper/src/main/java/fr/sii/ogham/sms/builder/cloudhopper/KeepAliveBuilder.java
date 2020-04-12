package fr.sii.ogham.sms.builder.cloudhopper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import com.cloudhopper.smpp.pdu.EnquireLink;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.sms.sender.impl.cloudhopper.KeepAliveOptions;

/**
 * Builder to configure how keep alive session management should behave.
 * 
 * <p>
 * Keep alive actively maintains the session opened by sending regularly
 * {@link EnquireLink} messages.
 * 
 * This builder let you configure:
 * <ul>
 * <li>Enable/disable active keep alive management</li>
 * <li>The time to wait between two {@link EnquireLink} messages</li>
 * <li>The maximum time to wait for a response from the server for
 * {@link EnquireLink} request</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class KeepAliveBuilder extends AbstractParent<SessionBuilder> implements Builder<KeepAliveOptions> {
	private static final AtomicInteger enquireLinkThreadCounter = new AtomicInteger();

	private final ConfigurationValueBuilderHelper<KeepAliveBuilder, Boolean> enableValueBuilder;
	private final ConfigurationValueBuilderHelper<KeepAliveBuilder, Long> enquireLinkIntervalValueBuilder;
	private final ConfigurationValueBuilderHelper<KeepAliveBuilder, Long> enquireLinkTimeoutValueBuilder;
	private final ConfigurationValueBuilderHelper<KeepAliveBuilder, Boolean> connectAtStartupValueBuilder;
	private final ConfigurationValueBuilderHelper<KeepAliveBuilder, Integer> maxConsecutiveTimeoutsValueBuilder;
	private Supplier<ScheduledExecutorService> executorFactory;

	public KeepAliveBuilder(SessionBuilder parent, BuildContext buildContext) {
		super(parent);
		this.enableValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
		this.enquireLinkIntervalValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class, buildContext);
		this.enquireLinkTimeoutValueBuilder = new ConfigurationValueBuilderHelper<>(this, Long.class, buildContext);
		this.connectAtStartupValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
		this.maxConsecutiveTimeoutsValueBuilder = new ConfigurationValueBuilderHelper<>(this, Integer.class, buildContext);
	}

	/**
	 * Enable or disable sending of {@link EnquireLink} messages to keep the
	 * session alive.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #enable()}.
	 * 
	 * <pre>
	 * .enable(true)
	 * .enable()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <pre>
	 * .enable(true)
	 * .enable()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * In both cases, {@code enable(true)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            true to enable sending requests to keep the session alive
	 * @return this instance for fluent chaining
	 */
	public KeepAliveBuilder enable(Boolean enable) {
		enableValueBuilder.setValue(enable);
		return this;
	}

	/**
	 * Enable or disable sending of {@link EnquireLink} messages to keep the
	 * session alive.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .enable()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #enable(Boolean)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .enable(true)
	 * .enable()
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
	public ConfigurationValueBuilder<KeepAliveBuilder, Boolean> enable() {
		return enableValueBuilder;
	}

	/**
	 * The fixed delay (in milliseconds) between two {@link EnquireLink}
	 * messages.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #interval()}.
	 * 
	 * <pre>
	 * .interval(60000L)
	 * .interval()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(30000L)
	 * </pre>
	 * 
	 * <pre>
	 * .interval(60000L)
	 * .interval()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(30000L)
	 * </pre>
	 * 
	 * In both cases, {@code interval(60000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param delay
	 *            the amount of time to wait between two {@link EnquireLink}
	 *            messages
	 * @return this instance for fluent chaining
	 */
	public KeepAliveBuilder interval(Long delay) {
		enquireLinkIntervalValueBuilder.setValue(delay);
		return this;
	}

	/**
	 * The fixed delay (in milliseconds) between two {@link EnquireLink}
	 * messages.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .interval()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(30000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #interval(Long)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .interval(60000L)
	 * .interval()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(30000L)
	 * </pre>
	 * 
	 * The value {@code 60000L} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<KeepAliveBuilder, Long> interval() {
		return enquireLinkIntervalValueBuilder;
	}

	/**
	 * The maximum amount of time (in milliseconds) to wait for receiving a
	 * response from the server to an {@link EnquireLink} request.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #responseTimeout()}.
	 * 
	 * <pre>
	 * .responseTimeout(5000L)
	 * .responseTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * <pre>
	 * .responseTimeout(5000L)
	 * .responseTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * In both cases, {@code responseTimeout(5000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param timeout
	 *            the maximum amount of time to wait for the response
	 * @return this instance for fluent chaining
	 */
	public KeepAliveBuilder responseTimeout(Long timeout) {
		enquireLinkTimeoutValueBuilder.setValue(timeout);
		return this;
	}

	/**
	 * The maximum amount of time (in milliseconds) to wait for receiving a
	 * response from the server to an {@link EnquireLink} request.
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
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #responseTimeout(Long)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .responseTimeout(5000L)
	 * .responseTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(10000L)
	 * </pre>
	 * 
	 * The value {@code 5000L} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<KeepAliveBuilder, Long> responseTimeout() {
		return enquireLinkTimeoutValueBuilder;
	}

	/**
	 * Connect to the server directly when the client is ready (if true).
	 * Otherwise, the connection is done when the first message is sent.
	 * 
	 * This may be useful to avoid a latency for the first message.
	 * 
	 * If connection fails at startup, then a new attempt is done when first
	 * message is sent.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #connectAtStartup()}.
	 * 
	 * <pre>
	 * .connectAtStartup(true)
	 * .connectAtStartup()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <pre>
	 * .connectAtStartup(true)
	 * .connectAtStartup()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * In both cases, {@code connectAtStartup(true)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param connectAtStartup
	 *            try to connect when client is ready
	 * @return this instance for fluent chaining
	 */
	public KeepAliveBuilder connectAtStartup(Boolean connectAtStartup) {
		connectAtStartupValueBuilder.setValue(connectAtStartup);
		return this;
	}

	/**
	 * Connect to the server directly when the client is ready (if true).
	 * Otherwise, the connection is done when the first message is sent.
	 * 
	 * This may be useful to avoid a latency for the first message.
	 * 
	 * If connection fails at startup, then a new attempt is done when first
	 * message is sent.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .connectAtStartup()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #connectAtStartup(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .connectAtStartup(true)
	 * .connectAtStartup()
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
	public ConfigurationValueBuilder<KeepAliveBuilder, Boolean> connectAtStartup() {
		return connectAtStartupValueBuilder;
	}

	/**
	 * Provides a factory that creates an {@link ScheduledExecutorService}. The
	 * created executor is then used to schedule the task that sends
	 * {@link EnquireLink} requests.
	 * 
	 * The factory should use one of:
	 * <ul>
	 * <li>{@link Executors#newSingleThreadScheduledExecutor()}</li>
	 * <li>{@link Executors#newSingleThreadScheduledExecutor(java.util.concurrent.ThreadFactory)}</li>
	 * <li>{@link Executors#newScheduledThreadPool(int)}</li>
	 * <li>{@link Executors#newScheduledThreadPool(int, java.util.concurrent.ThreadFactory)}</li>
	 * </ul>
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param executorFactory
	 *            the factory that creates an executor to use
	 * @return this instance for fluent chaining
	 */
	public KeepAliveBuilder executor(Supplier<ScheduledExecutorService> executorFactory) {
		this.executorFactory = executorFactory;
		return this;
	}

	/**
	 * Provides an {@link ScheduledExecutorService}. The executor is then used
	 * to schedule the task that sends {@link EnquireLink} requests.
	 * 
	 * <p>
	 * This method is a shortcut to {@code executor(() -> executor)}.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param executor
	 *            the executor to use
	 * @return this instance for fluent chaining
	 */
	public KeepAliveBuilder executor(ScheduledExecutorService executor) {
		return executor(() -> executor);
	}

	/**
	 * The maximum number of consecutive timeouts to {@link EnquireLink}
	 * requests to consider that a new session is required.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #maxConsecutiveTimeouts()}.
	 * 
	 * <pre>
	 * .maxConsecutiveTimeouts(5)
	 * .maxConsecutiveTimeouts()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(3)
	 * </pre>
	 * 
	 * <pre>
	 * .maxConsecutiveTimeouts(5)
	 * .maxConsecutiveTimeouts()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(3)
	 * </pre>
	 * 
	 * In both cases, {@code maxConsecutiveTimeouts(5)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param max
	 *            the maximum consecutive timeouts
	 * @return this instance for fluent chaining
	 */
	public KeepAliveBuilder maxConsecutiveTimeouts(Integer max) {
		maxConsecutiveTimeoutsValueBuilder.setValue(max);
		return this;
	}

	/**
	 * The maximum number of consecutive timeouts to {@link EnquireLink}
	 * requests to consider that a new session is required.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .maxConsecutiveTimeouts()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(3)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #maxConsecutiveTimeouts(Integer)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .maxConsecutiveTimeouts(5)
	 * .maxConsecutiveTimeouts()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(3)
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
	public ConfigurationValueBuilder<KeepAliveBuilder, Integer> maxConsecutiveTimeouts() {
		return maxConsecutiveTimeoutsValueBuilder;
	}

	@Override
	public KeepAliveOptions build() {
		KeepAliveOptions keepAliveOptions = new KeepAliveOptions();
		keepAliveOptions.setEnable(enableValueBuilder.getValue());
		keepAliveOptions.setEnquireLinkInterval(enquireLinkIntervalValueBuilder.getValue());
		keepAliveOptions.setEnquireLinkTimeout(enquireLinkTimeoutValueBuilder.getValue());
		keepAliveOptions.setConnectAtStartup(connectAtStartupValueBuilder.getValue());
		keepAliveOptions.setExecutor(executorFactory != null ? executorFactory : defaultEnquireLinkTimerFactory());
		keepAliveOptions.setMaxConsecutiveTimeouts(maxConsecutiveTimeoutsValueBuilder.getValue());
		return keepAliveOptions;
	}

	/**
	 * Default factory that provides a {@link ScheduledExecutorService}
	 * executor. As only one thread is needed (only one task) to regularly send
	 * {@link EnquireLink}s, it uses
	 * {@link Executors#newSingleThreadScheduledExecutor(ThreadFactory)}.
	 * 
	 * <p>
	 * The thread is named {@code EnquireLink-<generated number>}.
	 * 
	 * @return the factory
	 */
	public static Supplier<ScheduledExecutorService> defaultEnquireLinkTimerFactory() {
		return () -> Executors.newSingleThreadScheduledExecutor(KeepAliveBuilder::newThread);
	}

	private static Thread newThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setName("EnquireLink-" + enquireLinkThreadCounter.incrementAndGet());
		return thread;
	}
}
