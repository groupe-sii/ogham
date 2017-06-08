package fr.sii.ogham.template.freemarker.adapter;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;
import fr.sii.ogham.template.freemarker.SkipLocaleForStringContentTemplateLookupStrategy;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * Ogham special implementation to handle template that is directly provided as
 * string. This is different from {@link StringTemplateLoader} provided by
 * Freemarker. The Freemarker implementations expects that template content is
 * previously registered and referenced by a name. Then when requesting the
 * template, only the name is used. This is an alias.
 * 
 * <p>
 * This implementation directly handles the template content as string.
 * 
 * <p>
 * To work as expected, the {@link Locale} resolution must be skipped. Take a
 * look at {@link SkipLocaleForStringContentTemplateLookupStrategy}.
 * 
 * @author Aurélien Baudet
 *
 */
public class StringContentTemplateLoader implements TemplateLoader {

	@Override
	public Object findTemplateSource(String name) throws IOException {
		return new StringTemplateSource(name);
	}

	@Override
	public long getLastModified(Object templateSource) {
		return System.currentTimeMillis();
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		return new StringReader(((StringTemplateSource) templateSource).source);
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		// nothing to do
	}

	private static class StringTemplateSource {
		private final String source;

		public StringTemplateSource(String source) {
			super();
			this.source = source;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(source).hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return new EqualsBuilder(this, obj).appendFields("source").isEqual();
		}
		
		
	}
}