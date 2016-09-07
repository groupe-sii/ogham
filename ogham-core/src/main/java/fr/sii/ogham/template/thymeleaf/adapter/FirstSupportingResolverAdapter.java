package fr.sii.ogham.template.thymeleaf.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.NoResolverAdapterException;

/**
 * Decorator that will ask each resolver adapter if it is able to handle the
 * template resolver. If the resolver adapter supports it, then this
 * implementation asks the resolver adapter to provide the Thymeleaf template
 * resolver.
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

	public FirstSupportingResolverAdapter() {
		this(new ArrayList<ThymeleafResolverAdapter>());
	}

	@Override
	public boolean supports(ResourceResolver resolver) {
		for (ThymeleafResolverAdapter adapter : adapters) {
			if (adapter.supports(resolver)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ITemplateResolver adapt(ResourceResolver resolver) throws NoResolverAdapterException {
		for (ThymeleafResolverAdapter adapter : adapters) {
			if (adapter.supports(resolver)) {
				return adapter.adapt(resolver);
			}
		}
		throw new NoResolverAdapterException("No resolver adapter found for the provided resolver: " + resolver.getClass().getSimpleName(), resolver);
	}

	/**
	 * Register a new adapter. The adapter is added at the end.
	 * 
	 * @param adapter
	 *            the adapter to register
	 */
	public void addAdapter(ThymeleafResolverAdapter adapter) {
		adapters.add(adapter);
	}

	public List<ThymeleafResolverAdapter> getAdapters() {
		return adapters;
	}

	@Override
	public void setOptions(ThymeleafResolverOptions options) {
		for (ThymeleafResolverAdapter adapter : adapters) {
			adapter.setOptions(options);
		}
	}
}
