package fr.sii.ogham.core.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.sender.ConditionalSender;
import fr.sii.ogham.core.service.CatchAllMessagingService;
import fr.sii.ogham.core.service.EverySupportingMessagingService;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;

/**
 * Basic implementation of the builder that help to construct the messaging
 * service. It relies on the specialized builders for:
 * <ul>
 * <li>{@link EmailBuilder} for helping to construct email sender</li>
 * <li>{@link SmsBuilder} for helping construct sms sender</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class MessagingBuilder implements MessagingServiceBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(MessagingBuilder.class);

	/**
	 * The list of general builders used to construct senders. The constructed
	 * senders will be used by the messaging service.
	 */
	private List<MessagingSenderBuilder<ConditionalSender>> builders;

	/**
	 * The specialized builder of sender for SMS messages
	 */
	private SmsBuilder smsBuilder;

	/**
	 * The specialized builder of sender for email messages
	 */
	private EmailBuilder emailBuilder;

	public MessagingBuilder() {
		super();
		builders = new ArrayList<MessagingSenderBuilder<ConditionalSender>>();
	}

	/**
	 * Build the messaging service. The messaging service relies on the
	 * generated senders. Each sender is able to manage one or multiple
	 * messages. The default implementation of the messaging service is to ask
	 * each sender if it is able to handle the message and if it the case, then
	 * use this sender to really send the message. This implementation doesn't
	 * stop when the message is handled by a sender to possibly let another send
	 * the message through another channel.
	 * 
	 * @return the messaging service instance
	 * @throws BuildException
	 *             when one of the sender couldn't be built
	 */
	public MessagingService build() throws BuildException {
		List<ConditionalSender> senders = new ArrayList<ConditionalSender>();
		for (MessagingSenderBuilder<ConditionalSender> builder : builders) {
			senders.add(builder.build());
		}
		LOG.info("Using service that calls all registered senders");
		LOG.debug("Registered senders: {}", senders);
		return new CatchAllMessagingService(new EverySupportingMessagingService(senders));
	}

	/**
	 * Tells the builder to use all default behavior and values. The
	 * configuration values will be read from the system properties. The builder
	 * will construct the messaging service with the following senders:
	 * <ul>
	 * <li>An email sender that is able to construct email content with or
	 * without template</li>
	 * <li>A SMS sender that is able to construct email content with or without
	 * template</li>
	 * </ul>
	 * 
	 * @return this builder instance for fluent use
	 * @see EmailBuilder#useDefaults() More information about created email
	 *      sender
	 * @see SmsBuilder#useDefaults() More information about created SMS sender
	 */
	public MessagingBuilder useAllDefaults() {
		return useAllDefaults(BuilderUtils.getDefaultProperties());
	}

	/**
	 * Tells the builder to use all default behavior and values. The
	 * configuration values will be read from the provided properties. The
	 * builder will construct the messaging service with the following senders:
	 * <ul>
	 * <li>An email sender that is able to construct email content with or
	 * without template</li>
	 * <li>A SMS sender that is able to construct email content with or without
	 * template</li>
	 * </ul>
	 * 
	 * @param properties
	 *            indicate which properties to use instead of using the system
	 *            ones
	 * @return this builder instance for fluent use
	 * @see EmailBuilder#useDefaults() More information about created email
	 *      sender
	 * @see SmsBuilder#useDefaults() More information about created SMS sender
	 */
	public MessagingBuilder useAllDefaults(Properties properties) {
		useEmailDefaults(properties);
		useSmsDefaults(properties);
		return this;
	}

	/**
	 * Tells the builder to use default behaviors and values for email sender.
	 * The configuration values will be read from the provided properties.
	 * 
	 * This method is automatically called when using
	 * {@link #useAllDefaults(Properties)}.
	 * 
	 * 
	 * @return this builder instance for fluent use
	 * @see EmailBuilder#useDefaults() More information about created email
	 *      sender
	 */
	public MessagingBuilder useEmailDefaults() {
		return useEmailDefaults(BuilderUtils.getDefaultProperties());
	}

	/**
	 * Tells the builder to use the default behaviors and values for email
	 * sender. The configuration values will be read from the provided
	 * properties.
	 * 
	 * This method is automatically called when using
	 * {@link #useAllDefaults(Properties)}.
	 * 
	 * @param properties
	 *            indicate which properties to use instead of using the system
	 *            ones
	 * @return this builder instance for fluent use
	 * @see EmailBuilder#useDefaults() More information about created email
	 *      sender
	 */
	public MessagingBuilder useEmailDefaults(Properties properties) {
		withEmail();
		emailBuilder.useDefaults(properties);
		return this;
	}

	/**
	 * Tells the builder to use default behaviors and values for SMS sender. The
	 * configuration values will be read from the provided properties.
	 * 
	 * This method is automatically called when using
	 * {@link #useAllDefaults(Properties)}.
	 * 
	 * @return this builder instance for fluent use
	 * @see SmsBuilder#useDefaults() More information about created SMS sender
	 */
	public MessagingBuilder useSmsDefaults() {
		return useSmsDefaults(BuilderUtils.getDefaultProperties());
	}

	/**
	 * Tells the builder to use the default behaviors and values for SMS sender.
	 * The configuration values will be read from the provided properties.
	 * 
	 * This method is automatically called when using
	 * {@link #useAllDefaults(Properties)}.
	 * 
	 * @param properties
	 *            indicate which properties to use instead of using the system
	 *            ones
	 * @return this builder instance for fluent use
	 * @see SmsBuilder#useDefaults() More information about created SMS sender
	 */
	public MessagingBuilder useSmsDefaults(Properties properties) {
		withSms();
		smsBuilder.useDefaults(properties);
		return this;
	}

	/**
	 * Tells the builder to activate the email sender.
	 * 
	 * This method is automatically called when using {@link #useAllDefaults()},
	 * {@link #useAllDefaults(Properties)}, {@link #useEmailDefaults()} or
	 * {@link #useEmailDefaults(Properties)}.
	 * 
	 * 
	 * @return this builder instance for fluent use
	 */
	public MessagingBuilder withEmail() {
		emailBuilder = new EmailBuilder();
		builders.add(emailBuilder);
		return this;
	}

	/**
	 * Tells the builder to activate the sms sender.
	 * 
	 * This method is automatically called when using {@link #useAllDefaults()},
	 * {@link #useAllDefaults(Properties)}, {@link #useEmailDefaults()} or
	 * {@link #useEmailDefaults(Properties)}.
	 * 
	 * @return this builder instance for fluent use
	 */
	public MessagingBuilder withSms() {
		smsBuilder = new SmsBuilder();
		builders.add(smsBuilder);
		return this;
	}

	/**
	 * Get access to the SMS specialized builder. The aim is to be able to fine
	 * tune the SMS sender.
	 * 
	 * @return The specialized builder for SMS sender.
	 */
	public SmsBuilder getSmsBuilder() {
		return smsBuilder;
	}

	/**
	 * Get access to the email specialized builder. The aim is to be able to
	 * fine tune the email sender.
	 * 
	 * @return The specialized builder for email sender.
	 */
	public EmailBuilder getEmailBuilder() {
		return emailBuilder;
	}
}
