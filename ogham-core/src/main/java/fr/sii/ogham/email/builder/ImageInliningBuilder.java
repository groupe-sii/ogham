package fr.sii.ogham.email.builder;

import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilderDelegate;
import fr.sii.ogham.core.builder.mimetype.SimpleMimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.resolution.ClassPathResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.FileResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilderHelper;
import fr.sii.ogham.core.builder.resolution.StringResolutionBuilder;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.html.inliner.EveryImageInliner;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.translator.InlineImageTranslator;

public class ImageInliningBuilder extends AbstractParent<ImageHandlingBuilder> implements ResourceResolutionBuilder<ImageInliningBuilder>, Builder<ContentTranslator> {
	private ResourceResolutionBuilderHelper<ImageInliningBuilder> resourceResolutionBuilderHelper;
	private AttachImageBuilder attachBuilder;
	private Base64InliningBuilder base64Builder;
	private MimetypeDetectionBuilder<ImageInliningBuilder> mimetypeBuilder;
	private EnvironmentBuilder<?> environmentBuilder;

	public ImageInliningBuilder(ImageHandlingBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(this, environmentBuilder);
	}

	public AttachImageBuilder attach() {
		if(attachBuilder==null) {
			attachBuilder = new AttachImageBuilder(this);
		}
		return attachBuilder;
	}
	
	public Base64InliningBuilder base64() {
		if(base64Builder==null) {
			base64Builder = new Base64InliningBuilder(this);
		}
		return base64Builder;
	}
	
	public MimetypeDetectionBuilder<ImageInliningBuilder> mimetype() {
		if(mimetypeBuilder==null) {
			mimetypeBuilder = new SimpleMimetypeDetectionBuilder<>(this, environmentBuilder);
		}
		return mimetypeBuilder;
	}
	
	public ImageInliningBuilder mimetype(MimetypeDetectionBuilder<?> builder) {
		mimetypeBuilder = new MimetypeDetectionBuilderDelegate<>(this, builder);
		return this;
	}

	@Override
	public ClassPathResolutionBuilder<ImageInliningBuilder> classpath() {
		return resourceResolutionBuilderHelper.classpath();
	}

	@Override
	public FileResolutionBuilder<ImageInliningBuilder> file() {
		return resourceResolutionBuilderHelper.file();
	}

	@Override
	public StringResolutionBuilder<ImageInliningBuilder> string() {
		return resourceResolutionBuilderHelper.string();
	}

	@Override
	public ImageInliningBuilder resolver(ResourceResolver resolver) {
		return resourceResolutionBuilderHelper.resolver(resolver);
	}

	@Override
	public ContentTranslator build() throws BuildException {
		ResourceResolver resourceResolver = buildResolver();
		ImageInliner imageInliner = buildInliner();
		MimeTypeProvider mimetypeProvider = buildMimetypeProvider();
		if(mimetypeProvider==null) {
			// TODO: log to indicate why no translator
			return null;
		}
		return new InlineImageTranslator(imageInliner, resourceResolver, mimetypeProvider);
	}

	private MimeTypeProvider buildMimetypeProvider() {
		if(mimetypeBuilder==null) {
			return null;
		}
		return mimetypeBuilder.build();
	}

	private ImageInliner buildInliner() {
		EveryImageInliner inliner = new EveryImageInliner();
		if(attachBuilder!=null) {
			inliner.addInliner(attachBuilder.build());
		}
		if(base64Builder!=null) {
			inliner.addInliner(base64Builder.build());
		}
		return inliner;
	}

	private ResourceResolver buildResolver() {
		List<ResourceResolver> resolvers = resourceResolutionBuilderHelper.buildResolvers();
		return new FirstSupportingResourceResolver(resolvers);
	}

}
