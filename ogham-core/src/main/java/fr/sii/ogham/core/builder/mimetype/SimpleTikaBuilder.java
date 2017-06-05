package fr.sii.ogham.core.builder.mimetype;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.mimetype.TikaProvider;

/**
 * Instantites and configures the {@link TikaProvider}:
 * <ul>
 * <li>If a custom {@link Tika} instance has been provided, then use it
 * directly</li>
 * <li>If no custom {@link Tika} instance has been provided, then use the
 * default one: {@code new Tika()} (see
 * {@link TikaConfig#getDefaultConfig()})</li>
 * <li>Tika may be in some conditions not enough accurate. In this case, it will
 * return application/octet-stream mimetype. If
 * {@link #failIfOctetStream(boolean)} is set to true, then if Tika returns an
 * application/octet-stream, it will throw an exception. The purpose is to let
 * another {@link MimeTypeProvider} implementation take over and try to make a
 * better detection.</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class SimpleTikaBuilder<P> extends AbstractParent<P> implements TikaBuilder<P> {
	private Tika tika;
	private boolean failIfOctetStream = true;

	/**
	 * The parent builder (it is used when calling {@link #and()} method).
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public SimpleTikaBuilder(P parent) {
		super(parent);
	}

	@Override
	public TikaBuilder<P> instance(Tika tika) {
		this.tika = tika;
		return this;
	}

	@Override
	public TikaBuilder<P> failIfOctetStream(boolean fail) {
		failIfOctetStream = fail;
		return this;
	}

	@Override
	public MimeTypeProvider build() {
		Tika tikaInstance = this.tika == null ? new Tika() : this.tika;
		return new TikaProvider(tikaInstance, failIfOctetStream);
	}
}
