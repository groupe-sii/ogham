package fr.sii.ogham.email.builder;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.filler.AbstractAutofillDefaultValueBuilder;
import fr.sii.ogham.core.filler.MessageFiller;
import fr.sii.ogham.core.filler.SubjectFiller;
import fr.sii.ogham.core.subject.provider.FirstSupportingSubjectProvider;
import fr.sii.ogham.core.subject.provider.HtmlTitleSubjectProvider;
import fr.sii.ogham.core.subject.provider.MultiContentSubjectProvider;
import fr.sii.ogham.core.subject.provider.SubjectProvider;
import fr.sii.ogham.core.subject.provider.TextPrefixSubjectProvider;
import fr.sii.ogham.email.message.Email;

/**
 * Configures how to handle missing {@link Email} subject: if no subject is
 * explicitly defined on the {@link Email}, Ogham will use the result of this
 * builder configuration to provide a default subject.
 * 
 * Default subject can be provided by:
 * <ul>
 * <li>Using HTML {@code <title>} header tag as subject</li>
 * <li>Using first line text if prefixed</li>
 * <li>Using a property value</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class AutofillSubjectBuilder extends AbstractAutofillDefaultValueBuilder<AutofillSubjectBuilder, AutofillEmailBuilder, String> implements Builder<MessageFiller> {
	private final ConfigurationValueBuilderHelper<AutofillSubjectBuilder, Boolean> enableHtmlTitleValueBuilder;
	private final ConfigurationValueBuilderHelper<AutofillSubjectBuilder, String> firstLinePrefixValueBuilder;
	private SubjectProvider customProvider;

	/**
	 * Initializes with the parent builder and the {@link EnvironmentBuilder}.
	 * The parent builder is used when calling the {@link #and()} method. The
	 * {@link EnvironmentBuilder} is used by {@link #build()} method to evaluate
	 * property values.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for property resolution
	 */
	public AutofillSubjectBuilder(AutofillEmailBuilder parent, BuildContext buildContext) {
		super(AutofillSubjectBuilder.class, parent, String.class, buildContext);
		enableHtmlTitleValueBuilder = buildContext.newConfigurationValueBuilder(myself, Boolean.class);
		firstLinePrefixValueBuilder = buildContext.newConfigurationValueBuilder(myself, String.class);
	}
	
	/**
	 * Enable/disable using HTML {@code <title>} tag to provide a subject on the
	 * {@link Email} if none was explicitly defined.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #htmlTitle()}.
	 * 
	 * <pre>
	 * .htmlTitle(false)
	 * .htmlTitle()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <pre>
	 * .htmlTitle(false)
	 * .htmlTitle()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * In both cases, {@code htmlTitle(false)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            enable (true) or disable (false) extraction of HTML title to be used as subject of the email
	 * @return this instance for fluent chaining
	 */
	public AutofillSubjectBuilder htmlTitle(Boolean enable) {
		enableHtmlTitleValueBuilder.setValue(enable);
		return this;
	}

	
	/**
	 * Enable/disable using HTML {@code <title>} tag to provide a subject on the
	 * {@link Email} if none was explicitly defined.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .htmlTitle()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #htmlTitle(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .htmlTitle(false)
	 * .htmlTitle()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(true)
	 * </pre>
	 * 
	 * The value {@code false} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<AutofillSubjectBuilder, Boolean> htmlTitle() {
		return enableHtmlTitleValueBuilder;
	}
	
	/**
	 * Uses first line of text template to define the email subject (only if a
	 * prefix is defined).
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #text()}.
	 * 
	 * <pre>
	 * .text("MyPrefix:")
	 * .text()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("Subject:")
	 * </pre>
	 * 
	 * <pre>
	 * .text("MyPrefix:")
	 * .text()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("Subject:")
	 * </pre>
	 * 
	 * In both cases, {@code text("MyPrefix:")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param firstLinePrefix
	 *            the prefix used to indicate that subject should be extracted
	 * @return this instance for fluent chaining
	 */
	public AutofillSubjectBuilder text(String firstLinePrefix) {
		firstLinePrefixValueBuilder.setValue(firstLinePrefix);
		return this;
	}

	
	/**
	 * Uses first line of text template to define the email subject (only if a
	 * prefix is defined).
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .text()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("Subject:")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #text(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .text("MyPrefix:")
	 * .text()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("Subject:")
	 * </pre>
	 * 
	 * The value {@code "MyPrefix:"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<AutofillSubjectBuilder, String> text() {
		return firstLinePrefixValueBuilder;
	}

	/**
	 * Extension point to provide any additional subject provider. See
	 * {@link SubjectProvider} for more information.
	 * 
	 * If defined, any previously defined configuration is not used.
	 * 
	 * Only one provider can be defined. So you may need to register a
	 * {@link FirstSupportingSubjectProvider} with the list of your custom
	 * providers.
	 * 
	 * @param provider
	 *            the provider to use
	 * @return this instance for fluent chaining
	 */
	public AutofillSubjectBuilder provider(SubjectProvider provider) {
		customProvider = provider;
		return myself;
	}

	@Override
	public MessageFiller build() {
		return buildContext.register(new SubjectFiller(buildProvider()));
	}

	private SubjectProvider buildProvider() {
		if (customProvider != null) {
			return customProvider;
		}
		FirstSupportingSubjectProvider provider = buildContext.register(new FirstSupportingSubjectProvider());
		String prefix = firstLinePrefixValueBuilder.getValue();
		if (prefix != null) {
			provider.addProvider(buildContext.register(new TextPrefixSubjectProvider(prefix)));
		}
		boolean htmlTitle = enableHtmlTitleValueBuilder.getValue(false);
		if (htmlTitle) {
			provider.addProvider(buildContext.register(new HtmlTitleSubjectProvider()));
		}
		SubjectProvider multiContentProvider = buildContext.register(new MultiContentSubjectProvider(provider));
		provider.addProvider(multiContentProvider);
		return provider;
	}
}
