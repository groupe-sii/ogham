package fr.sii.ogham.sms.builder.cloudhopper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.retry.RetryBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.retry.FixedDelayRetry;
import fr.sii.ogham.core.util.BuilderUtils;

/**
 * Configures Cloudhopper session management (timeouts, retry, session name...).
 * 
 * @author Aurélien Baudet
 *
 */
public class SessionBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<CloudhopperSessionOptions> {
	private EnvironmentBuilder<?> environmentBuilder;
	private ValueOrProperties<Long> bind;
	private ValueOrProperties<Long> connect;
	private ValueOrProperties<Long> requestExpiry;
	private ValueOrProperties<Long> windowMonitorInverval;
	private ValueOrProperties<Long> windowWait;
	private ValueOrProperties<Integer> windowSize;
	private ValueOrProperties<Long> write;
	private ValueOrProperties<Long> response;
	private ValueOrProperties<Long> unbind;
	private RetryBuilder<SessionBuilder> connectRetryBuilder;
	private List<String> sessionNames;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 */
	public SessionBuilder(CloudhopperBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		bind = new ValueOrProperties<>();
		connect = new ValueOrProperties<>();
		requestExpiry = new ValueOrProperties<>();
		windowMonitorInverval = new ValueOrProperties<>();
		windowWait = new ValueOrProperties<>();
		windowSize = new ValueOrProperties<>();
		write = new ValueOrProperties<>();
		response = new ValueOrProperties<>();
		unbind = new ValueOrProperties<>();
		sessionNames = new ArrayList<>();
	}

	/**
	 * A name for the session (used to name threads).
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .sessionName("foo");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .sessionName("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param sessionName
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder sessionName(String... sessionName) {
		sessionNames.addAll(Arrays.asList(sessionName));
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for the success
	 * of a bind attempt to the SMSC. Defaults to 5000.
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #bindTimeout(String...)} method.
	 * 
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param timeout
	 *            The maximum amount of time to wait (in ms)
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder bindTimeout(Long timeout) {
		if (timeout != null) {
			bind.setValue(timeout);
		}
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for the success
	 * of a bind attempt to the SMSC. Defaults to 5000.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .bindTimeout("5000");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .bindTimeout("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param timeout
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder bindTimeout(String... timeout) {
		bind.setProperties(timeout);
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for a
	 * establishing the connection. Defaults to 10000.
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #connectTimeout(String...)} method.
	 * 
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param timeout
	 *            The maximum amount of time to wait (in ms)
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder connectTimeout(Long timeout) {
		if (timeout != null) {
			connect.setValue(timeout);
		}
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for a
	 * establishing the connection. Defaults to 10000.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .connectTimeout("10000");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .connectTimeout("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param timeout
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder connectTimeout(String... timeout) {
		connect.setProperties(timeout);
		return this;
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
			connectRetryBuilder = new RetryBuilder<>(this, environmentBuilder);
		}
		return connectRetryBuilder;
	}

	/**
	 * Set the amount of time (milliseconds) to wait for an endpoint to respond
	 * to a request before it expires. Defaults to disabled (-1).
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #requestExpiryTimeout(String...)} method.
	 * 
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param timeout
	 *            The maximum amount of time to wait (in ms) before an
	 *            unacknowledged request expires. -1 disables.
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder requestExpiryTimeout(Long timeout) {
		if (timeout != null) {
			requestExpiry.setValue(timeout);
		}
		return this;
	}

	/**
	 * Set the amount of time (milliseconds) to wait for an endpoint to respond
	 * to a request before it expires. Defaults to disabled (-1).
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .requestExpiryTimeout("-1");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .requestExpiryTimeout("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param timeout
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder requestExpiryTimeout(String... timeout) {
		requestExpiry.setProperties(timeout);
		return this;
	}

	/**
	 * Sets the amount of time (milliseconds) between executions of monitoring
	 * the window for requests that expire. It's recommended that this generally
	 * either matches or is half the value of requestExpiryTimeout. Therefore,
	 * at worst a request would could take up 1.5X the requestExpiryTimeout to
	 * clear out. Defaults to -1 (disabled).
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #windowMonitorInterval(String...)} method.
	 * 
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param interval
	 *            The maximum amount of time to wait (in ms) between executions
	 *            of monitoring the window.
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder windowMonitorInterval(Long interval) {
		if (interval != null) {
			windowMonitorInverval.setValue(interval);
		}
		return this;
	}

	/**
	 * Sets the amount of time (milliseconds) between executions of monitoring
	 * the window for requests that expire. It's recommended that this generally
	 * either matches or is half the value of requestExpiryTimeout. Therefore,
	 * at worst a request would could take up 1.5X the requestExpiryTimeout to
	 * clear out. Defaults to -1 (disabled).
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .windowMonitorInterval("-1");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .windowMonitorInterval("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param interval
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder windowMonitorInterval(String... interval) {
		windowMonitorInverval.setProperties(interval);
		return this;
	}

	/**
	 * Sets the maximum number of requests permitted to be outstanding
	 * (unacknowledged) at a given time. Must be &gt; 0. Defaults to 1.
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #windowSize(String...)} method.
	 * 
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param size
	 *            The maximum number of requests
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder windowSize(Integer size) {
		if (windowSize != null) {
			windowSize.setValue(size);
		}
		return this;
	}

	/**
	 * Sets the maximum number of requests permitted to be outstanding
	 * (unacknowledged) at a given time. Must be &gt; 0. Defaults to 1.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .windowSize("1");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .windowSize("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param size
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder windowSize(String... size) {
		windowSize.setProperties(size);
		return this;
	}

	/**
	 * Set the amount of time (milliseconds) to wait until a slot opens up in
	 * the sendWindow. Defaults to 60000.
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #windowWait(String...)} method.
	 * 
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param timeout
	 *            The maximum amount of time to wait (in ms) until a slot in the
	 *            sendWindow becomes available.
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder windowWait(Long timeout) {
		if (timeout != null) {
			windowWait.setValue(timeout);
		}
		return this;
	}

	/**
	 * Set the amount of time (milliseconds) to wait until a slot opens up in
	 * the sendWindow. Defaults to 60000.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .windowWait("60000");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .windowWait("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param windowWait
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder windowWait(String... windowWait) {
		this.windowWait.setProperties(windowWait);
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for bytes to be
	 * written when creating a new SMPP session. Defaults to 0 (no timeout, for
	 * backwards compatibility).
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #writeTimeout(String...)} method.
	 * 
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param timeout
	 *            The maximum amount of time to wait (in ms)
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder writeTimeout(Long timeout) {
		write.setValue(timeout == null ? 0 : timeout);
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait for bytes to be
	 * written when creating a new SMPP session. Defaults to 0 (no timeout, for
	 * backwards compatibility).
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .writeTimeout("0");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .writeTimeout("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param timeout
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder writeTimeout(String... timeout) {
		write.setProperties(timeout);
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
	 * This value preempts any other value defined by calling
	 * {@link #requestExpiryTimeout(String...)} method.
	 * 
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param timeout
	 *            The maximum amount of time to wait (in ms)
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder responseTimeout(Long timeout) {
		if (timeout != null) {
			response.setValue(timeout);
		}
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
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .responseTimeout("5000");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .responseTimeout("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param timeout
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder responseTimeout(String... timeout) {
		response.setProperties(timeout);
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait until the
	 * session is unbounded, waiting up to a specified period of milliseconds
	 * for an unbind response from the remote endpoint. Regardless of whether a
	 * proper unbind response was received, the socket/channel is closed.
	 * Defaults to 5000.
	 * 
	 * This value preempts any other value defined by calling
	 * {@link #unbindTimeout(String...)} method.
	 * 
	 * If this method is called several times, only the last value is used.
	 * 
	 * @param timeout
	 *            The maximum amount of time to wait (in ms)
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder unbindTimeout(Long timeout) {
		if (timeout != null) {
			unbind.setValue(timeout);
		}
		return this;
	}

	/**
	 * Set the maximum amount of time (in milliseconds) to wait until the
	 * session is unbounded, waiting up to a specified period of milliseconds
	 * for an unbind response from the remote endpoint. Regardless of whether a
	 * proper unbind response was received, the socket/channel is closed.
	 * Defaults to 5000.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .unbindTimeout("5000");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .unbindTimeout("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param timeout
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public SessionBuilder unbindTimeout(String... timeout) {
		unbind.setProperties(timeout);
		return this;
	}

	@Override
	public CloudhopperSessionOptions build() {
		CloudhopperSessionOptions sessionOpts = new CloudhopperSessionOptions();
		PropertyResolver propertyResolver = environmentBuilder.build();
		sessionOpts.setBindTimeout(getValue(propertyResolver, bind));
		sessionOpts.setConnectTimeout(getValue(propertyResolver, connect));
		sessionOpts.setRequestExpiryTimeout(getValue(propertyResolver, requestExpiry));
		sessionOpts.setWindowMonitorInterval(getValue(propertyResolver, windowMonitorInverval));
		sessionOpts.setWindowSize(getValue(propertyResolver, windowSize, Integer.class));
		sessionOpts.setWindowWaitTimeout(getValue(propertyResolver, windowWait));
		sessionOpts.setWriteTimeout(getValue(propertyResolver, write));
		sessionOpts.setResponseTimeout(getValue(propertyResolver, response));
		sessionOpts.setUnbindTimeout(getValue(propertyResolver, unbind));
		if (connectRetryBuilder != null) {
			sessionOpts.setConnectRetry(connectRetryBuilder.build());
		}
		return sessionOpts;
	}

	private Long getValue(PropertyResolver propertyResolver, ValueOrProperties<Long> val) {
		return getValue(propertyResolver, val, Long.class);
	}

	private <T> T getValue(PropertyResolver propertyResolver, ValueOrProperties<T> val, Class<T> targetType) {
		if (val.getValue() != null) {
			return val.getValue();
		}
		if (val.getProperties() == null) {
			return null;
		}
		return BuilderUtils.evaluate(val.getProperties(), propertyResolver, targetType);
	}

	private static class ValueOrProperties<V> {
		private V value;
		private List<String> properties;

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}

		public List<String> getProperties() {
			return properties;
		}

		public void setProperties(List<String> properties) {
			this.properties = properties;
		}

		public void setProperties(String... properties) {
			setProperties(Arrays.asList(properties));
		}
	}
}
