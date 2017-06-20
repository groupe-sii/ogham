package fr.sii.ogham.template.freemarker.adapter;

import java.net.URL;

import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.DelegateResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * Adapter that converts general {@link ClassPathResolver} into FreeMarker
 * specific {@link ClassTemplateLoader}.
 * 
 * @author Cyril Dejonghe
 *
 */
public class ClassPathResolverAdapter extends AbstractFreeMarkerTemplateLoaderOptionsAdapter implements TemplateLoaderAdapter {
	private final ClassLoader classLoader;
	
	public ClassPathResolverAdapter(ClassLoader classLoader) {
		super();
		this.classLoader = classLoader;
	}

	@Override
	public boolean supports(ResourceResolver resolver) {
		ResourceResolver actualResolver = resolver instanceof DelegateResourceResolver ? ((DelegateResourceResolver) resolver).getActualResourceResolver() : resolver;
		return actualResolver instanceof ClassPathResolver;
	}

	@Override
	public TemplateLoader adapt(ResourceResolver resolver) {
		return new FixClassTemplateLoader(getClassLoader(), "");
	}

	private ClassLoader getClassLoader() {
		if(classLoader!=null) {
			return classLoader;
		}
		return Thread.currentThread().getContextClassLoader();
	}

	private static class FixClassTemplateLoader extends ClassTemplateLoader {
		public FixClassTemplateLoader(ClassLoader classLoader, String basePackagePath) {
			super(classLoader, basePackagePath);
		}

		@Override
		protected URL getURL(String name) {
			String path = name.startsWith("/") ? name.substring(1) : name;
			return super.getURL(path);
		}
	}
	
}
