package fr.sii.ogham.core.builder;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeTypeParseException;
import javax.activation.MimetypesFileTypeMap;

import org.apache.tika.Tika;

import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.mimetype.FallbackMimeTypeProvider;
import fr.sii.ogham.core.mimetype.FixedMimeTypeProvider;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.mimetype.TikaProvider;

/**
 * <p>
 * General builder used to build a MimeType provider.
 * </p>
 * 
 * There exists several implementations to provide the mimetype:
 * <ul>
 * <li>Using Java {@link MimetypesFileTypeMap}</li>
 * <li>Using Java 7 {@link Files#probeContentType(java.nio.file.Path)}</li>
 * <li>Using <a href="http://tika.apache.org/">Apache Tika</a></li>
 * <li>Using <a href="https://github.com/arimus/jmimemagic">JMimeMagic</a></li>
 * </ul>
 * 
 * <p>
 * This builder allows to use several providers. It will chain them until one
 * can find a valid mimetype. If none is found, you can explicitly provide the
 * default one.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class MimetypeProviderBuilder implements Builder<MimeTypeProvider> {
	private List<ChildBuilder> builders = new ArrayList<>();
	private String defaultMimetype;

	public MimetypeProviderBuilder useDefaults() {
		// @formatter:off
		tika()
			.and()
		.defaultMimetype();
		// TODO: auto-detect
//		autodetect();
		// @formatter:on
		return this;
	}

	public TikaBuilder tika() {
		return add(new TikaBuilder(this));
	}

	public MimetypeProviderBuilder register(MimeTypeProvider provider) {
		add(new NoOpBuilder(this, provider));
		return this;
	}

	public MimetypeProviderBuilder defaultMimetype(String defaultMimetype) {
		this.defaultMimetype = defaultMimetype;
		return this;
	}

	public MimetypeProviderBuilder defaultMimetype() {
		return defaultMimetype("application/octet-stream");
	}

//	public MimetypeProviderBuilder autodetect() {
//		return add(new AutoDetectBuilder(this));
//	}

	protected <T extends ChildBuilder> T add(T child) {
		builders.add(child);
		return child;
	}

	@Override
	public MimeTypeProvider build() throws BuildException {
		FallbackMimeTypeProvider provider = new FallbackMimeTypeProvider();
		for (ChildBuilder builder : builders) {
			provider.addProvider(builder.build());
		}
		if (defaultMimetype != null) {
			try {
				provider.addProvider(new FixedMimeTypeProvider(defaultMimetype));
			} catch (MimeTypeParseException e) {
				throw new BuildException("Failed to build MimeTypeProvider due to invalid default mimetype", e);
			}
		}
		return provider;
	}

	private abstract static class ChildBuilder implements Builder<MimeTypeProvider> {
		private final MimetypeProviderBuilder parent;

		public ChildBuilder(MimetypeProviderBuilder parent) {
			super();
			this.parent = parent;
		}

		public MimetypeProviderBuilder and() {
			return parent;
		}
	}

	private static class TikaBuilder extends ChildBuilder {
		/**
		 * The Tika instance to use
		 */
		private Tika tika;

		/**
		 * Whether to fail if the default mimetype is return (this may indicate
		 * that detection hasn't work).
		 */
		private boolean failIfOctetStream;

		public TikaBuilder(MimetypeProviderBuilder parent) {
			super(parent);
			this.tika = new Tika();
			this.failIfOctetStream = true;
		}

		public TikaBuilder tika(Tika instance) {
			this.tika = instance;
			return this;
		}

		public TikaBuilder failIfOctetStream(boolean failIfOctetStream) {
			this.failIfOctetStream = failIfOctetStream;
			return this;
		}

		@Override
		public TikaProvider build() {
			return new TikaProvider(tika, failIfOctetStream);
		}
	}

	private static class NoOpBuilder extends ChildBuilder {
		private final MimeTypeProvider provider;

		public NoOpBuilder(MimetypeProviderBuilder parent, MimeTypeProvider provider) {
			super(parent);
			this.provider = provider;
		}

		@Override
		public MimeTypeProvider build() throws BuildException {
			return provider;
		}
	}

	private static class AutoDetectBuilder extends ChildBuilder {

		public AutoDetectBuilder(MimetypeProviderBuilder parent, Class<?> type) {
			super(parent);
		}

		@Override
		public MimeTypeProvider build() throws BuildException {
			return null;
		}
	}
}
