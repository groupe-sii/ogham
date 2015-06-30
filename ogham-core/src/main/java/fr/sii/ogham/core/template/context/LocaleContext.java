package fr.sii.ogham.core.template.context;

import java.util.Locale;
import java.util.Map;

import fr.sii.ogham.core.exception.template.ContextException;

/**
 * A context that also provides {@link Locale} information for
 * internationalization. It allows to explicitly specify the language.
 * 
 * @author Aurélien Baudet
 *
 */
public class LocaleContext implements Context {
	/**
	 * The context that contains the variables
	 */
	private final Context delegate;
	
	/**
	 * The locale to use
	 */
	private final Locale locale;

	public LocaleContext(Context delegate, Locale locale) {
		super();
		this.delegate = delegate;
		this.locale = locale;
	}

	public LocaleContext(Object bean, Locale locale) {
		this(new BeanContext(bean), locale);
	}

	@Override
	public Map<String, Object> getVariables() throws ContextException {
		return delegate.getVariables();
	}

	public Locale getLocale() {
		return locale;
	}

	public Context getDelegate() {
		return delegate;
	}
}
