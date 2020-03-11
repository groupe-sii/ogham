package fr.sii.ogham.core.builder.mimetype;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderDelegate;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
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
 * {@link #failIfOctetStream(Boolean)} is set to true, then if Tika returns an
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
	private final BuildContext buildContext;
	private Tika tika;
	private final ConfigurationValueBuilderHelper<SimpleTikaBuilder<P>, Boolean> failIfOctetStreamValueBuilder;

	/**
	 * Initializes the builder with the parent instance (used by the
	 * {@link #and()} method) and the {@link EnvironmentBuilder}. The
	 * {@link EnvironmentBuilder} is used to evaluate property values when
	 * {@link #build()} is called.
	 * 
	 * @param parent
	 *            the parent instance
	 * @param buildContext
	 *            used to evaluate property values
	 */
	public SimpleTikaBuilder(P parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
		failIfOctetStreamValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class, buildContext);
	}

	@Override
	public TikaBuilder<P> instance(Tika tika) {
		this.tika = tika;
		return this;
	}

	@Override
	public TikaBuilder<P> failIfOctetStream(Boolean fail) {
		failIfOctetStreamValueBuilder.setValue(fail);
		return this;
	}

	@Override
	public ConfigurationValueBuilder<TikaBuilder<P>, Boolean> failIfOctetStream() {
		return new ConfigurationValueBuilderDelegate<>(this, failIfOctetStreamValueBuilder);
	}

	@Override
	public MimeTypeProvider build() {
		Tika tikaInstance = this.tika == null ? new Tika() : this.tika;
		return buildContext.register(new TikaProvider(tikaInstance, failIfOctetStreamValueBuilder.getValue(false)));
	}
}
