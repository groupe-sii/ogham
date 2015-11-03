package fr.sii.ogham.core.builder.mimetype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.activation.MimeTypeParseException;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.mimetype.FallbackMimeTypeProvider;
import fr.sii.ogham.core.mimetype.FixedMimeTypeProvider;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.util.BuilderUtils;

public class SimpleMimetypeDetectionBuilder<P> extends AbstractParent<P> implements MimetypeDetectionBuilder<P> {
	private TikaBuilder<MimetypeDetectionBuilder<P>> tikaBuilder;
	private List<String> defaultMimetypes;
	private final EnvironmentBuilder<?> environmentBuilder;
	
	public SimpleMimetypeDetectionBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		defaultMimetypes = new ArrayList<>();
	}

	public TikaBuilder<MimetypeDetectionBuilder<P>> tika() {
		if(tikaBuilder==null) {
			tikaBuilder = new SimpleTikaBuilder<MimetypeDetectionBuilder<P>>(this);
		}
		return tikaBuilder;
	}
	
	public SimpleMimetypeDetectionBuilder<P> defaultMimetype(String... mimetypes) {
		defaultMimetypes.addAll(Arrays.asList(mimetypes));
		return this;
	}

	@Override
	public MimeTypeProvider build() throws BuildException {
		try {
			List<MimeTypeProvider> providers = new ArrayList<>();
			buildTika(providers);
			buildDefault(providers);
			if(providers.isEmpty()) {
				throw new BuildException("No mimetype detector configured");
			}
			if(providers.size()==1) {
				return providers.get(0);
			}
			return new FallbackMimeTypeProvider(providers);
		} catch(MimeTypeParseException e) {
			throw new BuildException("Failed to build mimetype provider", e);
		}
	}

	private void buildTika(List<MimeTypeProvider> providers) {
		if(tikaBuilder!=null) {
			providers.add(tikaBuilder.build());
		}
	}

	private void buildDefault(List<MimeTypeProvider> providers) throws MimeTypeParseException {
		if(defaultMimetypes==null || defaultMimetypes.isEmpty()) {
			return;
		}
		PropertyResolver propertyResolver = environmentBuilder.build();
		String mimetype = BuilderUtils.evaluate(defaultMimetypes, propertyResolver, String.class);
		if(mimetype!=null) {
			providers.add(new FixedMimeTypeProvider(mimetype));
		}
	}
	
}
