package fr.sii.ogham.template.freemarker.adapter;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import fr.sii.ogham.core.resource.resolver.DelegateResourceResolver;
import fr.sii.ogham.core.resource.resolver.FileResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.template.exception.ResolverAdapterConfigurationException;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * Adapter that converts general {@link FileResolver} into FreeMarker specific
 * {@link FileTemplateLoader}.
 * 
 * @author Cyril Dejonghe
 *
 */
public class FileResolverAdapter extends AbstractFreeMarkerTemplateLoaderOptionsAdapter {
	private final File baseDir;
	
	public FileResolverAdapter() {
		this(new File("/"));
	}

	public FileResolverAdapter(File baseDir) {
		super();
		this.baseDir = baseDir;
	}

	@Override
	public boolean supports(ResourceResolver resolver) {
		ResourceResolver actualResolver = resolver instanceof DelegateResourceResolver ? ((DelegateResourceResolver) resolver).getActualResourceResolver() : resolver;
		return actualResolver instanceof FileResolver;
	}

	@Override
	public TemplateLoader adapt(ResourceResolver resolver) throws ResolverAdapterConfigurationException {
		try {
			return new FileTemplateLoaderAllowingAbsolutePaths(baseDir);
		} catch (IOException e) {
			throw new ResolverAdapterConfigurationException("Invalid configuration for " + FileTemplateLoader.class.getSimpleName(), resolver, e);
		}
	}

	private static class FileTemplateLoaderAllowingAbsolutePaths extends FileTemplateLoader {
		private static final boolean SEP_IS_SLASH = File.separatorChar == '/';
		
		public FileTemplateLoaderAllowingAbsolutePaths(File baseDir) throws IOException {
			super(baseDir, true);
		}

		@Override
		public Object findTemplateSource(String name) throws IOException {
			// TODO: add security option to enable/disable absolute paths outside of baseDir
			try {
				return AccessController.doPrivileged(new PrivilegedExceptionAction<File>() {
					@Override
					public File run() throws IOException {
						File source = new File(name);
						if (source.isAbsolute() && source.isFile()) {
							return source;
						}
						source = new File(baseDir, SEP_IS_SLASH ? name : name.replace('/', File.separatorChar));
						if (!source.isFile()) {
							return null;
						}
						return source;
					}
				});
			} catch (PrivilegedActionException e) {
				throw (IOException) e.getException();
			}
		}
	}
}
