package fr.sii.ogham.core.builder;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.util.BuilderUtils;

/**
 * Base class to configure a property key that will be used to automatically
 * fill a message. It registers the property keys only (no direct value).
 * 
 * @author Aurélien Baudet
 *
 * @param <MYSELF>
 *            The type of this instance. This is needed to have the right return
 *            type for fluent chaining with inheritance
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public abstract class AbstractAutofillDefaultValueBuilder<MYSELF, P> extends AbstractParent<P> {
	protected MYSELF myself;
	protected List<String> defaultValueProperties;

	/**
	 * Initializes the builder with the explicit type of this instance for
	 * chaining. This is mandatory in order to have a fluent chaining that
	 * doesn't loose sub-types. If we were using directly {@code this}, chaining
	 * would only give methods statically defined by
	 * {@link AbstractAutofillDefaultValueBuilder}. All methods defined by any
	 * specialized implementation that would add other methods won't be
	 * accessible directly.
	 * 
	 * The parent is used by the {@link #and()} method for chaining.
	 * 
	 * @param selfType
	 *            the real implementation class that helps compiler to chain
	 *            calls
	 * @param parent
	 *            the parent builder
	 */
	@SuppressWarnings("unchecked")
	public AbstractAutofillDefaultValueBuilder(Class<?> selfType, P parent) {
		super(parent);
		myself = (MYSELF) selfType.cast(this);
		defaultValueProperties = new ArrayList<>();
	}

	/**
	 * Registers a property key. The key may be either of the form
	 * <code>"${custom.foo.bar}"</code> or <code>"custom.foo.bar"</code>.
	 * 
	 * @param properties
	 *            the property keys to register
	 * @return this instance for fluent chaining
	 */
	public MYSELF defaultValueProperty(String... properties) {
		for (String prop : properties) {
			String propertyKey = BuilderUtils.isExpression(prop) ? BuilderUtils.getPropertyKey(prop) : prop;
			this.defaultValueProperties.add(propertyKey);
		}
		return myself;
	}

	/**
	 * Get registered properties (key will always be of the form
	 * <code>"custom.foo.bar"</code>
	 * 
	 * @return the list of previsouly registered property keys
	 */
	public List<String> getDefaultValueProperties() {
		return defaultValueProperties;
	}
}
