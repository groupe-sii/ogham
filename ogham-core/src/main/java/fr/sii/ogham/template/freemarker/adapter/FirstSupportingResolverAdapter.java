package fr.sii.ogham.template.freemarker.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.NoResolverAdapterException;
import fr.sii.ogham.template.exception.ResolverAdapterConfigurationException;
import fr.sii.ogham.template.freemarker.TemplateLoaderOptions;
import freemarker.cache.TemplateLoader;

/**
 * Decorator that will ask each resolver adapter if it is able to handle the template resolver. If the resolver adapter supports it, then this implementation
 * asks the resolver adapter to provide the FreeMarker template loader.
 * 
 * Only the first resolver adapter that can handle the template resolver is used.
 * 
 * @author Cyril Dejonghe
 */
public class FirstSupportingResolverAdapter implements TemplateLoaderAdapter {

	/**
	 * The list of adapters used to convert the general resolvers into FreeMarker specific resolvers
	 */
	private List<TemplateLoaderAdapter> adapters;

	/**
	 * Initialize the decorator with none, one or several resolver adapter implementations. The registration order may be important.
	 * 
	 * @param adapters
	 *            the adapters to register
	 */
	public FirstSupportingResolverAdapter(TemplateLoaderAdapter... adapters) {
		this(new ArrayList<>(Arrays.asList(adapters)));
	}

	/**
	 * Initialize the decorator with the provided resolver adapter implementations. The registration order may be important.
	 * 
	 * @param adapters
	 *            the adapters to register
	 */
	public FirstSupportingResolverAdapter(List<TemplateLoaderAdapter> adapters) {
		super();
		this.adapters = adapters;
	}

	public FirstSupportingResolverAdapter() {
		this(new ArrayList<TemplateLoaderAdapter>());
	}

	@Override
	public boolean supports(ResourceResolver resolver) {
		for (TemplateLoaderAdapter adapter : adapters) {
			if (adapter.supports(resolver)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public TemplateLoader adapt(ResourceResolver resolver) throws NoResolverAdapterException, ResolverAdapterConfigurationException {
		for (TemplateLoaderAdapter adapter : adapters) {
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
	public void addAdapter(TemplateLoaderAdapter adapter) {
		adapters.add(adapter);
	}

	public List<TemplateLoaderAdapter> getAdapters() {
		return adapters;
	}

	@Override
	public void setOptions(TemplateLoaderOptions options) {
		for (TemplateLoaderAdapter adapter : adapters) {
			adapter.setOptions(options);
		}
	}
}
