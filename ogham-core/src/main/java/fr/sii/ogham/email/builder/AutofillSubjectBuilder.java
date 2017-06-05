package fr.sii.ogham.email.builder;

import static fr.sii.ogham.core.util.BuilderUtils.evaluate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractAutofillDefaultValueBuilder;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
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
public class AutofillSubjectBuilder extends AbstractAutofillDefaultValueBuilder<AutofillSubjectBuilder, AutofillEmailBuilder> implements Builder<MessageFiller> {
	private boolean enableHtmlTitle;
	private List<String> firstLinePrefixes;
	private SubjectProvider customProvider;
	private EnvironmentBuilder<?> environmentBuilder;

	/**
	 * Initializes with the parent builder and the {@link EnvironmentBuilder}.
	 * The parent builder is used when calling the {@link #and()} method. The
	 * {@link EnvironmentBuilder} is used by {@link #build()} method to evaluate
	 * property values.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            configuration about property resolution
	 */
	public AutofillSubjectBuilder(AutofillEmailBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(AutofillSubjectBuilder.class, parent);
		this.environmentBuilder = environmentBuilder;
		firstLinePrefixes = new ArrayList<>();
	}

	/**
	 * Enable/disable using HTML {@code <title>} tag to provide a subject on the
	 * {@link Email} if none was explicitly defined.
	 * 
	 * @param enable
	 *            true to enable, false to disable
	 * @return this instance for fluent chaining
	 */
	public AutofillSubjectBuilder htmlTitle(boolean enable) {
		enableHtmlTitle = enable;
		return myself;
	}

	/**
	 * Uses first line of text template to define the email subject (only if a
	 * prefix is defined).
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .text("Subject:");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .text("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param firstLinePrefixes
	 *            one value, or one or serveral property keys
	 * @return this instance for fluent chaining
	 */
	public AutofillSubjectBuilder text(String... firstLinePrefixes) {
		this.firstLinePrefixes.addAll(Arrays.asList(firstLinePrefixes));
		return myself;
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
		return new SubjectFiller(buildProvider());
	}

	private SubjectProvider buildProvider() {
		if (customProvider != null) {
			return customProvider;
		}
		FirstSupportingSubjectProvider provider = new FirstSupportingSubjectProvider();
		if (!firstLinePrefixes.isEmpty()) {
			PropertyResolver propertyResolver = environmentBuilder.build();
			String prefix = evaluate(firstLinePrefixes, propertyResolver, String.class);
			if (prefix != null) {
				provider.addProvider(new TextPrefixSubjectProvider(prefix));
			}
		}
		if (enableHtmlTitle) {
			provider.addProvider(new HtmlTitleSubjectProvider());
		}
		SubjectProvider multiContentProvider = new MultiContentSubjectProvider(provider);
		provider.addProvider(multiContentProvider);
		return provider;
	}
}
