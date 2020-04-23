package fr.sii.ogham.sms.builder.cloudhopper;

import com.cloudhopper.smpp.pdu.EnquireLink;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.sms.sender.impl.cloudhopper.ReuseSessionOptions;

/**
 * Builder to configure how reuse session management should behave.
 * 
 * <p>
 * When sending the first message, a new session is created. Later, when sending
 * the next message, if the session is still alive, this session is reused. As
 * the connection is not actively maintained, the session may be killed by the
 * server. Therefore to check if the session is still alive, an
 * {@link EnquireLink} request is sent. If a response is received from the
 * server, then the session is still alive and the message can be sent using the
 * same session. If a failure response or no response is received after some
 * time from the server, then a new session must be created.
 * 
 * <p>
 * To check if the session is still alive, the {@link EnquireLink} request is
 * sent just before sending the real message. In order to prevent sending an
 * {@link EnquireLink} request before <strong>every</strong> message, the date
 * of the last sent message or {@link EnquireLink} is kept. This date is
 * compared to a delay to ensure that no {@link EnquireLink} is sent during this
 * delay.
 * 
 * <p>
 * This builder let you configure:
 * <ul>
 * <li>Enable/disable reuse session management</li>
 * <li>The maximum time to wait for a response from the server for
 * {@link EnquireLink} request</li>
 * <li>The time to wait before sending a new {@link EnquireLink} request
 * again</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class ReuseSessionBuilder extends AbstractParent<SessionBuilder> implements Builder<ReuseSessionOptions> {
	private final ConfigurationValueBuilderHelper<ReuseSessionBuilder, Boolean> enableValueBuilder;
	private final ConfigurationValueBuilderHelper<ReuseSessionBuilder, Long> lastInteractionExpirationDelayValueBuilder;
	private final ConfigurationValueBuilderHelper<ReuseSessionBuilder, Long> enquireLinkTimeoutValueBuilder;

	public ReuseSessionBuilder(SessionBuilder parent, BuildContext buildContext) {
		super(parent);
		this.enableValueBuilder = buildContext.newConfigurationValueBuilder(this, Boolean.class);
		this.lastInteractionExpirationDelayValueBuilder = buildContext.newConfigurationValueBuilder(this, Long.class);
		this.enquireLinkTimeoutValueBuilder = buildContext.newConfigurationValueBuilder(this, Long.class);
	}

	/**
	 * Enable or disable the reuse the same session (if possible) for sending
	 * messages.
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
	 *            enable reuse of same session
	 * @return this instance for fluent chaining
	 */
	public ReuseSessionBuilder enable(Boolean enable) {
		enableValueBuilder.setValue(enable);
		return this;
	}

	/**
	 * Enable or disable the reuse the same session (if possible) for sending
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
	public ConfigurationValueBuilder<ReuseSessionBuilder, Boolean> enable() {
		return enableValueBuilder;
	}

	/**
	 * To check if the session is still alive, an {@link EnquireLink} request is
	 * sent. This request may fail since the session may be killed by the
	 * server. The timeout ensures that the client doesn't wait too long for a
	 * response that may never come.
	 * 
	 * The maximum amount of time (in milliseconds) to wait for receiving a
	 * response from the server to an {@link EnquireLink} request.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #responseTimeout()}.
	 * 
	 * <pre>
	 * .responseTimeout(10000L)
	 * .responseTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <pre>
	 * .responseTimeout(10000L)
	 * .responseTimeout()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * In both cases, {@code responseTimeout(10000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param timeout
	 *            the maximum amount of time (in milliseconds) to wait for the
	 *            response
	 * @return this instance for fluent chaining
	 */
	public ReuseSessionBuilder responseTimeout(Long timeout) {
		enquireLinkTimeoutValueBuilder.setValue(timeout);
		return this;
	}

	/**
	 * To check if the session is still alive, an {@link EnquireLink} request is
	 * sent. This request may fail since the session may be killed by the
	 * server. The timeout ensures that the client doesn't wait too long for a
	 * response that may never come.
	 * 
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
	 *   .defaultValue(5000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #responseTimeout(Long)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .responseTimeout(10000L)
	 * .responseTimeout()
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
	public ConfigurationValueBuilder<ReuseSessionBuilder, Long> responseTimeout() {
		return enquireLinkTimeoutValueBuilder;
	}

	/**
	 * To check if the session is still alive, an {@link EnquireLink} request is
	 * sent. The request is sent just before sending the message.
	 * 
	 * This is the time (in milliseconds) to wait before considering last
	 * {@link EnquireLink} response as expired (need to send a new
	 * {@link EnquireLink} request to check if session is still alive).
	 * 
	 * <p>
	 * This is needed to prevent sending {@link EnquireLink} request every time
	 * a message has to be sent. Instead it considers that the time elapsed
	 * between now and the last {@link EnquireLink} response (or the last sent
	 * message) is not enough so a new {@link EnquireLink} is not necessary to
	 * check if session is still alive.
	 *
	 * <p>
	 * Set to 0 to always check session before sending message.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #lastInteractionExpiration()}.
	 * 
	 * <pre>
	 * .delay(60000L)
	 * .delay()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(60000L)
	 * </pre>
	 * 
	 * <pre>
	 * .delay(60000L)
	 * .delay()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(60000L)
	 * </pre>
	 * 
	 * In both cases, {@code delay(60000L)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param delay
	 *            The time to wait (in milliseconds) to wait before sending a
	 *            new {@link EnquireLink} request to check if session is still
	 *            alive.
	 * @return this instance for fluent chaining
	 */
	public ReuseSessionBuilder lastInteractionExpiration(Long delay) {
		lastInteractionExpirationDelayValueBuilder.setValue(delay);
		return this;
	}

	/**
	 * To check if the session is still alive, an {@link EnquireLink} request is
	 * sent. The request is sent just before sending the message.
	 * 
	 * This is the time (in milliseconds) to wait before considering last
	 * {@link EnquireLink} response as expired (need to send a new
	 * {@link EnquireLink} request to check if session is still alive).
	 * 
	 * <p>
	 * This is needed to prevent sending {@link EnquireLink} request every time
	 * a message has to be sent. Instead it considers that the time elapsed
	 * between now and the last {@link EnquireLink} response (or the last sent
	 * message) is not enough so a new {@link EnquireLink} is not necessary to
	 * check if session is still alive.
	 *
	 * <p>
	 * Set to 0 to always check session before sending message.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .delay()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(60000L)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #lastInteractionExpiration(Long)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .delay(60000L)
	 * .delay()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(60000L)
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
	public ConfigurationValueBuilder<ReuseSessionBuilder, Long> lastInteractionExpiration() {
		return lastInteractionExpirationDelayValueBuilder;
	}

	@Override
	public ReuseSessionOptions build() {
		ReuseSessionOptions reuseSessionOptions = new ReuseSessionOptions();
		reuseSessionOptions.setEnable(enableValueBuilder.getValue(false));
		reuseSessionOptions.setLastInteractionExpirationDelay(lastInteractionExpirationDelayValueBuilder.getValue(0L));
		reuseSessionOptions.setEnquireLinkTimeout(enquireLinkTimeoutValueBuilder.getValue(0L));
		return reuseSessionOptions;
	}
}
