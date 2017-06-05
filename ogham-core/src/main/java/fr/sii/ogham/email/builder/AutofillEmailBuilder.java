package fr.sii.ogham.email.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.filler.EveryFillerDecorator;
import fr.sii.ogham.core.filler.MessageFiller;
import fr.sii.ogham.email.filler.EmailFiller;
import fr.sii.ogham.email.message.Email;

/**
 * Configures how Ogham will add default values to the {@link Email} if some
 * information is missing.
 * 
 * If sender address is missing, a default one can be defined in configuration
 * properties.
 * 
 * If recipient address is missing, a default one can be defined in
 * configuration properties.
 * 
 * If subject is missing, a default one can be defined either:
 * <ul>
 * <li>In HTML title</li>
 * <li>In first line of text template</li>
 * <li>Using a default value defined in configuration properties</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 * @see EmailFiller
 *
 */
public class AutofillEmailBuilder extends AbstractParent<EmailBuilder> implements Builder<MessageFiller> {
	private AutofillSubjectBuilder subjectBuilder;
	private AutofillDefaultEmailAddressBuilder fromBuilder;
	private AutofillDefaultEmailAddressBuilder toBuilder;
	private AutofillDefaultEmailAddressBuilder ccBuilder;
	private AutofillDefaultEmailAddressBuilder bccBuilder;
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
	public AutofillEmailBuilder(EmailBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
	}

	/**
	 * Configures how to handle missing {@link Email} subject: if no subject is
	 * explicitly defined on the {@link Email}, Ogham will use the result of
	 * this builder configuration to provide a default subject.
	 * 
	 * Default subject can be provided by:
	 * <ul>
	 * <li>Using HTML {@code <title>} header tag as subject</li>
	 * <li>Using first line text if prefixed</li>
	 * <li>Using a property value</li>
	 * </ul>
	 * 
	 * 
	 * @return the builder to configure default subject handling
	 */
	public AutofillSubjectBuilder subject() {
		if (subjectBuilder == null) {
			subjectBuilder = new AutofillSubjectBuilder(this, environmentBuilder);
		}
		return subjectBuilder;
	}

	/**
	 * Configures how to handle missing {@link Email} sender address: if no
	 * sender address is explicitly defined on the {@link Email}, Ogham will use
	 * the result of this builder configuration to provide a default sender
	 * address.
	 * 
	 * @return the builder to configure default sender address handling
	 */
	public AutofillDefaultEmailAddressBuilder from() {
		if (fromBuilder == null) {
			fromBuilder = new AutofillDefaultEmailAddressBuilder(this);
		}
		return fromBuilder;
	}

	/**
	 * Configures how to handle missing {@link Email} recipient address: if no
	 * "to" address is explicitly defined on the {@link Email}, Ogham will use
	 * the result of this builder configuration to provide a default "to"
	 * address.
	 * 
	 * @return the builder to configure default address handling
	 */
	public AutofillDefaultEmailAddressBuilder to() {
		if (toBuilder == null) {
			toBuilder = new AutofillDefaultEmailAddressBuilder(this);
		}
		return toBuilder;
	}

	/**
	 * Configures how to handle missing {@link Email} recipient address: if no
	 * "cc" address is explicitly defined on the {@link Email}, Ogham will use
	 * the result of this builder configuration to provide a default "cc"
	 * address.
	 * 
	 * @return the builder to configure default address handling
	 */
	public AutofillDefaultEmailAddressBuilder cc() {
		if (ccBuilder == null) {
			ccBuilder = new AutofillDefaultEmailAddressBuilder(this);
		}
		return ccBuilder;
	}

	/**
	 * Configures how to handle missing {@link Email} recipient address: if no
	 * "bcc" address is explicitly defined on the {@link Email}, Ogham will use
	 * the result of this builder configuration to provide a default "bcc"
	 * address.
	 * 
	 * @return the builder to configure default address handling
	 */
	public AutofillDefaultEmailAddressBuilder bcc() {
		if (bccBuilder == null) {
			bccBuilder = new AutofillDefaultEmailAddressBuilder(this);
		}
		return bccBuilder;
	}

	@Override
	public MessageFiller build() {
		EveryFillerDecorator filler = new EveryFillerDecorator();
		if (subjectBuilder != null) {
			filler.addFiller(subjectBuilder.build());
		}
		PropertyResolver propertyResolver = environmentBuilder.build();
		filler.addFiller(new EmailFiller(propertyResolver, buildDefaultValueProps()));
		return filler;
	}

	private Map<String, List<String>> buildDefaultValueProps() {
		Map<String, List<String>> props = new HashMap<>();
		if (subjectBuilder != null) {
			props.put("subject", subjectBuilder.getDefaultValueProperties());
		}
		if (fromBuilder != null) {
			props.put("from", fromBuilder.getDefaultValueProperties());
		}
		if (toBuilder != null) {
			props.put("to", toBuilder.getDefaultValueProperties());
		}
		if (ccBuilder != null) {
			props.put("cc", ccBuilder.getDefaultValueProperties());
		}
		if (bccBuilder != null) {
			props.put("bcc", bccBuilder.getDefaultValueProperties());
		}
		return props;
	}
}
