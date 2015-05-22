package fr.sii.notification.template.thymeleaf.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.template.resolver.TemplateResolver;
import fr.sii.notification.template.exception.NoResolverAdapterException;

/**
 * Decorator that will ask each resolver adapter if it is able to handle the
 * template resolver. If the resolver adapter can, then this implementation asks
 * the resolver adapter to provide the Thymeleaf template resolver.
 * 
 * Only the first resolver adapter that can handle the template resolver is
 * used.
 * 
 * @author Aurélien Baudet
 */
public class FirstSupportingResolverAdapter implements ThymeleafResolverAdapter {

	/**
	 * The list of adapters used to convert the general resolvers into Thymeleaf
	 * specific resolvers
	 */
	private List<ThymeleafResolverAdapter> adapters;

	/**
	 * Initialize the decorator with none, one or several resolver adapter
	 * implementations. The registration order may be important.
	 * 
	 * @param adapters
	 *            the adapters to register
	 */
	public FirstSupportingResolverAdapter(ThymeleafResolverAdapter... adapters) {
		this(new ArrayList<>(Arrays.asList(adapters)));
	}

	/**
	 * Initialize the decorator with the provided resolver adapter
	 * implementations. The registration order may be important.
	 * 
	 * @param adapters
	 *            the adapters to register
	 */
	public FirstSupportingResolverAdapter(List<ThymeleafResolverAdapter> adapters) {
		super();
		this.adapters = adapters;
	}

	@Override
	public boolean supports(TemplateResolver resolver) {
		return true;
	}

	@Override
	public ITemplateResolver adapt(TemplateResolver resolver) throws NoResolverAdapterException {
		for (ThymeleafResolverAdapter adapter : adapters) {
			if (adapter.supports(resolver)) {
				return adapter.adapt(resolver);
			}
		}
		throw new NoResolverAdapterException("No resolver adapter found for the provided resolver: " + resolver.getClass().getSimpleName(), resolver);
	}

	/**
	 * Register a new adapter. The adatper is added at the end.
	 * 
	 * @param adapter
	 *            the adapter to register
	 */
	public void addAdapter(ThymeleafResolverAdapter adapter) {
		adapters.add(adapter);
	}
}
