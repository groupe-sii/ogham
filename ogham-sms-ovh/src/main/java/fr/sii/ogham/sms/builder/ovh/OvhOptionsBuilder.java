package fr.sii.ogham.sms.builder.ovh;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.sms.sender.impl.ovh.OvhOptions;
import fr.sii.ogham.sms.sender.impl.ovh.SmsCoding;

/**
 * Configures OVH SMS options:
 * <ul>
 * <li>Enable/disable the "STOP" indication at the end of the message (useful to
 * disable for non-commercial SMS)</li>
 * <li>Define the SMS encoding (see {@link SmsCoding}): 1 for 7bit encoding, 2
 * for 16bit encoding (UTF-16). If you use UTF-16, your SMS will have a maximum
 * size of 70 characters instead of 160. If {@code null}, automatic detection is
 * used. Set the value to force a particular coding</li>
 * <li>Define a tag to mark sent messages (a 20 maximum character string)</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class OvhOptionsBuilder extends AbstractParent<OvhSmsBuilder> implements Builder<OvhOptions> {
	private final EnvironmentBuilder<?> environmentBuilder;
	private final ConfigurationValueBuilderHelper<OvhOptionsBuilder, Boolean> noStopValueBuilder;
	private final ConfigurationValueBuilderHelper<OvhOptionsBuilder, String> tagValueBuilder;
	private final ConfigurationValueBuilderHelper<OvhOptionsBuilder, SmsCoding> smsCodingValueBuilder;

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
	public OvhOptionsBuilder(OvhSmsBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		noStopValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class);
		tagValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		smsCodingValueBuilder = new ConfigurationValueBuilderHelper<>(this, SmsCoding.class);
	}

	/**
	 * Enable/disable "STOP" indication at the end of the message (useful to
	 * disable for non-commercial SMS).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #noStop()}.
	 * 
	 * <pre>
	 * .noStop(true)
	 * .noStop()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <pre>
	 * .noStop(true)
	 * .noStop()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * In both cases, {@code noStop(true)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param noStop
	 *            true to disable STOP message
	 * @return this instance for fluent chaining
	 */
	public OvhOptionsBuilder noStop(Boolean noStop) {
		noStopValueBuilder.setValue(noStop);
		return this;
	}

	/**
	 * Enable/disable "STOP" indication at the end of the message (useful to
	 * disable for non-commercial SMS).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .noStop()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #noStop(Boolean)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .noStop(true)
	 * .noStop()
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
	public ConfigurationValueBuilder<OvhOptionsBuilder, Boolean> noStop() {
		return noStopValueBuilder;
	}

	/**
	 * Set a tag to mark sent messages (20 maximum character string).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #tag()}.
	 * 
	 * <pre>
	 * .tag("my-tag")
	 * .tag()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-tag")
	 * </pre>
	 * 
	 * <pre>
	 * .tag("my-tag")
	 * .tag()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-tag")
	 * </pre>
	 * 
	 * In both cases, {@code tag("my-tag")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param tag
	 *            tag name to use
	 * @return this instance for fluent chaining
	 */
	public OvhOptionsBuilder tag(String tag) {
		tagValueBuilder.setValue(tag);
		return this;
	}

	/**
	 * Set a tag to mark sent messages (20 maximum character string).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .tag()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-tag")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #tag(String)} takes precedence over
	 * property values and default value.
	 * 
	 * <pre>
	 * .tag("my-tag")
	 * .tag()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-tag")
	 * </pre>
	 * 
	 * The value {@code "my-tag"} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<OvhOptionsBuilder, String> tag() {
		return tagValueBuilder;
	}

	/**
	 * Set the message encoding:
	 * <ul>
	 * <li>"1" or "GSM7" for 7bit encoding</li>
	 * <li>"2" or "UNICODE" for 16bit encoding</li>
	 * </ul>
	 * If you use Unicode, your SMS will have a maximum size of 70 characters
	 * instead of 160.
	 * 
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #smsCoding()}.
	 * 
	 * <pre>
	 * .smsCoding(SmsCoding.UNICODE)
	 * .smsCoding()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(SmsCoding.GSM7)
	 * </pre>
	 * 
	 * <pre>
	 * .smsCoding(SmsCoding.UNICODE)
	 * .smsCoding()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(SmsCoding.GSM7)
	 * </pre>
	 * 
	 * In both cases, {@code smsCoding(SmsCoding.UNICODE)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param smsCoding
	 *            the coding for messages
	 * @return this instance for fluent chaining
	 */
	public OvhOptionsBuilder smsCoding(SmsCoding smsCoding) {
		smsCodingValueBuilder.setValue(smsCoding);
		return this;
	}

	/**
	 * Set the message encoding:
	 * <ul>
	 * <li>"1" or "GSM7" for 7bit encoding</li>
	 * <li>"2" or "UNICODE" for 16bit encoding</li>
	 * </ul>
	 * If you use Unicode, your SMS will have a maximum size of 70 characters
	 * instead of 160.
	 * 
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .smsCoding()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(SmsCoding.GSM7)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #smsCoding(SmsCoding)} takes precedence
	 * over property values and default value.
	 * 
	 * <pre>
	 * .smsCoding(SmsCoding.UNICODE)
	 * .smsCoding()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(SmsCoding.GSM7)
	 * </pre>
	 * 
	 * The value {@code SmsCoding.UNICODE} is used regardless of the value of
	 * the properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<OvhOptionsBuilder, SmsCoding> smsCoding() {
		return smsCodingValueBuilder;
	}

	@Override
	public OvhOptions build() {
		PropertyResolver propertyResolver = environmentBuilder.build();
		boolean builtNoStop = buildNoStop(propertyResolver);
		String builtTag = buildTag(propertyResolver);
		SmsCoding builtSmsCoding = buildSmsCoding(propertyResolver);
		return new OvhOptions(builtNoStop, builtTag, builtSmsCoding);
	}

	private boolean buildNoStop(PropertyResolver propertyResolver) {
		return noStopValueBuilder.getValue(propertyResolver, false);
	}

	private String buildTag(PropertyResolver propertyResolver) {
		return tagValueBuilder.getValue(propertyResolver);
	}

	private SmsCoding buildSmsCoding(PropertyResolver propertyResolver) {
		return smsCodingValueBuilder.getValue(propertyResolver);
	}
}
