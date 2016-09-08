package fr.sii.ogham.template.freemarker.adapter;

import fr.sii.ogham.template.freemarker.TemplateLoaderOptions;
import freemarker.cache.TemplateLoader;

/**
 * Abstract class to handle options configuration for the adapted
 * {@link TemplateLoader}.
 * 
 * @author Cyril Dejonghe
 *
 */
public abstract class AbstractFreemarkerTemplateLoaderOptionsAdapter implements TemplateLoaderAdapter {
	private TemplateLoaderOptions options;

	public TemplateLoaderOptions getOptions() {
		return options;
	}

	@Override
	public void setOptions(TemplateLoaderOptions options) {
		this.options = options;
	}
}
