package fr.sii.notification.core.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import fr.sii.notification.core.exception.builder.BuildException;
import fr.sii.notification.core.filler.EveryFillerDecorator;
import fr.sii.notification.core.filler.MessageFiller;
import fr.sii.notification.core.filler.PropertiesFiller;
import fr.sii.notification.core.filler.SubjectFiller;
import fr.sii.notification.core.subject.provider.FirstSupportingSubjectProvider;
import fr.sii.notification.core.subject.provider.HtmlTitleSubjectProvider;
import fr.sii.notification.core.subject.provider.MultiContentSubjectProvider;
import fr.sii.notification.core.subject.provider.SubjectProvider;
import fr.sii.notification.core.subject.provider.TextPrefixSubjectProvider;
import fr.sii.notification.core.util.BuilderUtils;

/**
 * Builder that help construct the message fillers. The aim of a message filler
 * is to generate some values to put into the message object.
 * 
 * @author Aurélien Baudet
 *
 */
public class MessageFillerBuilder implements Builder<MessageFiller> {
	/**
	 * The fillers to use in chain
	 */
	private List<MessageFiller> fillers;

	public MessageFillerBuilder() {
		super();
		fillers = new ArrayList<>();
	}
	
	@Override
	public MessageFiller build() throws BuildException {
		return new EveryFillerDecorator(fillers);
	}

	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Fill messages with system properties</li>
	 * <li>Generate subject from HTML title or first textual line starting with
	 * <code>"Subject:"</code></li>
	 * </ul>
	 * <p>
	 * Configuration values come from system properties.
	 * </p>
	 * 
	 * @param baseKey
	 *            the prefix for the keys used for filling the message
	 * @return this instance for fluent use
	 */
	public MessageFillerBuilder useDefaults(String baseKey) {
		return useDefaults(BuilderUtils.getDefaultProperties(), baseKey);
	}

	/**
	 * Tells the builder to use all default behaviors and values:
	 * <ul>
	 * <li>Fill messages with provided properties</li>
	 * <li>Generate subject from HTML title or first textual line starting with
	 * <code>"Subject:"</code></li>
	 * </ul>
	 * <p>
	 * Configuration values come from provided properties.
	 * </p>
	 * 
	 * @param properties
	 *            the properties to use instead of default ones
	 * @param baseKey
	 *            the prefix for the keys used for filling the message
	 * @return this instance for fluent use
	 */
	public MessageFillerBuilder useDefaults(Properties properties, String baseKey) {
		withConfigurationFiller(properties, baseKey);
		withSubjectFiller();
		return this;
	}

	/**
	 * Enables filling of messages with values that comes from provided
	 * configuration properties.
	 * <p>
	 * Automatically called by {@link #useDefaults(String)} and
	 * {@link #useDefaults(Properties, String)}
	 * </p>
	 * 
	 * @param props
	 *            the properties that contains the values to set on the message
	 * @param baseKey
	 *            the prefix for the keys used for filling the message
	 * @return this instance for fluent use
	 */
	public MessageFillerBuilder withConfigurationFiller(Properties props, String baseKey) {
		fillers.add(new PropertiesFiller(props, baseKey));
		return this;
	}

	/**
	 * Enables filling of messages with values that comes from system
	 * configuration properties.
	 * <p>
	 * Automatically called by {@link #useDefaults(String)} and
	 * {@link #useDefaults(Properties, String)}
	 * </p>
	 * 
	 * @param baseKey
	 *            the prefix for the keys used for filling the message
	 * @return this instance for fluent use
	 */
	public MessageFillerBuilder withConfigurationFiller(String baseKey) {
		return withConfigurationFiller(BuilderUtils.getDefaultProperties(), baseKey);
	}

	/**
	 * Enable the generation of subject of the message. The subject can
	 * automatically be extracted from the content:
	 * <ul>
	 * <li>If content of the message is HTML, then the title is used as subject</li>
	 * <li>If content of the message is text and the first line starts with
	 * <code>"Subject:"</code>, then it is used as subject</li>
	 * </ul>
	 * <p>
	 * Automatically called by {@link #useDefaults(String)} and
	 * {@link #useDefaults(Properties, String)}
	 * </p>
	 * 
	 * @return this instance for fluent use
	 */
	public MessageFillerBuilder withSubjectFiller() {
		// TODO: builder for subject provider too ?
		FirstSupportingSubjectProvider provider = new FirstSupportingSubjectProvider(new TextPrefixSubjectProvider(), new HtmlTitleSubjectProvider());
		SubjectProvider multiContentProvider = new MultiContentSubjectProvider(provider);
		provider.addProvider(multiContentProvider);
		fillers.add(new SubjectFiller(provider));
		return this;
	}

}
