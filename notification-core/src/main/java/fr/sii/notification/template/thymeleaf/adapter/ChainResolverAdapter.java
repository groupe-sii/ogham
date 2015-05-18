package fr.sii.notification.template.thymeleaf.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.notification.core.template.resolver.TemplateResolver;
import fr.sii.notification.template.exception.NoResolverAdapter;

public class ChainResolverAdapter implements ThymeleafResolverAdapter {

	private List<ThymeleafResolverAdapter> adapters;
	
	public ChainResolverAdapter(ThymeleafResolverAdapter... adapters) {
		this(new ArrayList<>(Arrays.asList(adapters)));
	}
	
	public ChainResolverAdapter(List<ThymeleafResolverAdapter> adapters) {
		super();
		this.adapters = adapters;
	}

	@Override
	public boolean supports(TemplateResolver resolver) {
		return true;
	}

	@Override
	public ITemplateResolver adapt(TemplateResolver resolver) throws NoResolverAdapter {
		for(ThymeleafResolverAdapter adapter : adapters) {
			if(adapter.supports(resolver)) {
				return adapter.adapt(resolver);
			}
		}
		throw new NoResolverAdapter("No resolver adapter found for the provided resolver: "+resolver.getClass().getSimpleName(), resolver);
	}

	public void addAdapter(ThymeleafResolverAdapter adapter) {
		adapters.add(adapter);
	}
}
