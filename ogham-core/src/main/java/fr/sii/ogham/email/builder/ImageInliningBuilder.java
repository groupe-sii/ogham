package fr.sii.ogham.email.builder;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilderDelegate;
import fr.sii.ogham.core.builder.mimetype.SimpleMimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.resolution.ClassPathResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.FileResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilderHelper;
import fr.sii.ogham.core.builder.resolution.StringResolutionBuilder;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.resource.path.LookupAwareRelativePathResolver;
import fr.sii.ogham.core.resource.path.RelativePathResolver;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.html.inliner.EveryImageInliner;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.translator.InlineImageTranslator;

/**
 * Configures how images declared in the HTML content are automatically
 * transformed to make it work with email.
 * 
 * Images can be either:
 * <ul>
 * <li>Attached to the email</li>
 * <li>Encoded to a base64 string</li>
 * <li>Not inlined at all</li>
 * </ul>
 * 
 * This builder is used to enable the inlining modes (and to configure them).
 * Several modes can be enabled.
 * 
 * <p>
 * If {@link #attach()} is called, it enables image attachment.
 * 
 * Image defined in a html must be referenced by a
 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID (or
 * CID)</a> if the image is attached to the email.
 * 
 * For example, if your template contains the following HTML code:
 * 
 * <pre>
 * {@code
 *    <img src="classpath:/foo.png" data-inline-image="attach" />
 * }
 * </pre>
 * 
 * Then the image will be loaded from the classpath and attached to the email.
 * The src attribute will be replaced by the Content-ID.
 * 
 * 
 * <p>
 * If {@link #base64()} is called, it enables inlining by converting image
 * content into base64 string and using the base64 string as image source.
 * 
 * For example, if your template contains the following HTML code:
 * 
 * <pre>
 * {@code
 *    <img src="classpath:/foo.png" data-inline-image="base64" />
 * }
 * </pre>
 * 
 * Then the image will be loaded from the classpath and encoded into a base64
 * string. This base64 string is used in the src attribute of the image.
 * 
 * <p>
 * If you don't want to inline a particular image, you can set the
 * "data-inline-image" attribute to "skip":
 * 
 * <pre>
 * {@code
 *    <img src="classpath:/foo.png" data-inline-image="skip" />
 * }
 * </pre>
 * 
 * Then the image won't be inlined at all.
 * 
 * <p>
 * If no inline mode is explicitly defined on the {@code <img>}:
 * 
 * <pre>
 * {@code
 *    <img src="classpath:/foo.png" />
 * }
 * </pre>
 * 
 * The behavior depends on what you have configured:
 * <ul>
 * <li>If {@link #attach()} is enabled (has been called), then image will be
 * loaded from the classpath and attached to the email. The src attribute will
 * be replaced by the Content-ID.</li>
 * <li>If {@link #attach()} is not enabled (never called) and {@link #base64()}
 * is enabled (has been called), then the image will be loaded from the
 * classpath and encoded into a base64 string. This base64 string is used in the
 * src attribute of the image.</li>
 * <li>If neither {@link #attach()} nor {@link #base64()} are enabled (never
 * called), then images won't be inlined at all</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class ImageInliningBuilder extends AbstractParent<ImageHandlingBuilder> implements ResourceResolutionBuilder<ImageInliningBuilder>, Builder<ContentTranslator> {
	private static final Logger LOG = LoggerFactory.getLogger(ImageInliningBuilder.class);

	private final BuildContext buildContext;
	private final ResourceResolutionBuilderHelper<ImageInliningBuilder> resourceResolutionBuilderHelper;
	private AttachImageBuilder attachBuilder;
	private Base64InliningBuilder base64Builder;
	private MimetypeDetectionBuilder<ImageInliningBuilder> mimetypeBuilder;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public ImageInliningBuilder(ImageHandlingBuilder parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
		resourceResolutionBuilderHelper = new ResourceResolutionBuilderHelper<>(this, buildContext);
	}

	/**
	 * Configures how attachment of images is handled.
	 * 
	 * <p>
	 * If this method is called, it enables image attachment.
	 * 
	 * Image defined in a html must be referenced by a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a> if the image is attached to the email.
	 * 
	 * For example, if your template contains the following HTML code:
	 * 
	 * <pre>
	 * {@code
	 *    <img src="classpath:/foo.png" data-inline-image="attach" />
	 * }
	 * </pre>
	 * 
	 * Then the image will be loaded from the classpath and attached to the
	 * email. The src attribute will be replaced by the Content-ID.
	 * 
	 * 
	 * 
	 * <p>
	 * In the same way, if your template contains the following code:
	 * 
	 * <pre>
	 * <code>
	 *  &lt;style&gt;
	 *     .some-class {
	 *       background: url('classpath:/foo.png');
	 *       --inline-image: attach;
	 *     }
	 *  &lt;/style&gt;
	 * </code>
	 * </pre>
	 * 
	 * Or directly on {@code style} attribute:
	 * 
	 * <pre>
	 * {@code
	 * 	<div style=
	"background: url('classpath:/foo.png'); --inline-image: attach;"></div>
	 * }
	 * </pre>
	 * 
	 * Then the image will be loaded from the classpath and attached to the
	 * email. The url will be replaced by the Content-ID.
	 * 
	 * <p>
	 * If no inline mode is defined, image attachment is used by default:
	 * 
	 * <pre>
	 * {@code
	 *    <img src="classpath:/foo.png" />
	 * }
	 * </pre>
	 * 
	 * Or:
	 * 
	 * <pre>
	 * <code>
	 *  &lt;style&gt;
	 *     .some-class {
	 *       background: url('classpath:/foo.png');
	 *     }
	 *  &lt;/style&gt;
	 * </code>
	 * </pre>
	 * 
	 * Or:
	 * 
	 * <pre>
	 * {@code
	 * 	<div style="background: url('classpath:/foo.png');"></div>
	 * }
	 * </pre>
	 * 
	 * The examples above have the same result as the example that explicitly
	 * defines the inline mode.
	 * 
	 * 
	 * @return the builder to configure how images are automatically attached
	 */
	public AttachImageBuilder attach() {
		if (attachBuilder == null) {
			attachBuilder = new AttachImageBuilder(this, buildContext);
		}
		return attachBuilder;
	}

	/**
	 * Configures how attachment of images is handled.
	 * 
	 * <p>
	 * If this method is called, it enables inlining by converting image content
	 * into base64 string and using the base64 string as image source.
	 * 
	 * For example, if your template contains the following HTML code:
	 * 
	 * <pre>
	 * {@code
	 *    <img src="classpath:/foo.png" data-inline-image="base64" />
	 * }
	 * </pre>
	 * 
	 * Then the image will be loaded from the classpath and encoded into a
	 * base64 string. This base64 string is used in the src attribute of the
	 * {@code <img>}.
	 * 
	 * 
	 * <p>
	 * In the same way, if your template contains the following code:
	 * 
	 * <pre>
	 * <code>
	 *  &lt;style&gt;
	 *     .some-class {
	 *       background: url('classpath:/foo.png');
	 *       --inline-image: base64;
	 *     }
	 *  &lt;/style&gt;
	 * </code>
	 * </pre>
	 * 
	 * Or directly on {@code style} attribute:
	 * 
	 * <pre>
	 * {@code
	 * 	<div style="background: url('classpath:/foo.png'); --inline-image: base64;"></div>
	 * }
	 * </pre>
	 * 
	 * Then the image will be loaded from the classpath and encoded into a
	 * base64 string. The url is updated with the base64 string.
	 * 
	 * <p>
	 * If no inline mode is defined <strong>and</strong> image attachment is not
	 * enable ({@link #attach()} is not called), this mode is used by default:
	 * 
	 * <pre>
	 * {@code
	 *    <img src="classpath:/foo.png" />
	 * }
	 * </pre>
	 * 
	 * In this case, the example above as the same result as the example that
	 * explicitly defines the inline mode.
	 * 
	 * 
	 * @return the builder to configure how images are automatically converted
	 *         to base64
	 */
	public Base64InliningBuilder base64() {
		if (base64Builder == null) {
			base64Builder = new Base64InliningBuilder(this, buildContext);
		}
		return base64Builder;
	}

	/**
	 * Builder that configures mimetype detection.
	 * 
	 * There exists several implementations to provide the mimetype:
	 * <ul>
	 * <li>Using Java {@link MimetypesFileTypeMap}</li>
	 * <li>Using Java 7 {@link Files#probeContentType(java.nio.file.Path)}</li>
	 * <li>Using <a href="http://tika.apache.org/">Apache Tika</a></li>
	 * <li>Using
	 * <a href="https://github.com/arimus/jmimemagic">JMimeMagic</a></li>
	 * </ul>
	 * 
	 * <p>
	 * Both implementations provided by Java are based on file extensions. This
	 * can't be used in most cases as we often handle {@link InputStream}s.
	 * </p>
	 * 
	 * <p>
	 * In previous version of Ogham, JMimeMagic was used and was working quite
	 * well. Unfortunately, the library is no more maintained.
	 * </p>
	 * 
	 * <p>
	 * You can configure how Tika will detect mimetype:
	 * 
	 * <pre>
	 * .mimetype()
	 *    .tika()
	 *       ...
	 * </pre>
	 * 
	 * <p>
	 * This builder allows to use several providers. It will chain them until
	 * one can find a valid mimetype. If none is found, you can explicitly
	 * provide the default one:
	 * 
	 * <pre>
	 * .mimetype()
	 *    .defaultMimetype("text/html")
	 * </pre>
	 * 
	 * <p>
	 * If no mimetype detector was previously defined, it creates a new one.
	 * Then each time you call {@link #mimetype()}, the same instance is used.
	 * </p>
	 * 
	 * @return the builder to configure mimetype detection
	 */
	public MimetypeDetectionBuilder<ImageInliningBuilder> mimetype() {
		if (mimetypeBuilder == null) {
			mimetypeBuilder = new SimpleMimetypeDetectionBuilder<>(this, buildContext);
		}
		return mimetypeBuilder;
	}

	/**
	 * NOTE: this is mostly for advance usage (when creating a custom module).
	 * 
	 * Inherits mimetype configuration from another builder. This is useful for
	 * configuring independently different parts of Ogham but keeping a whole
	 * coherence.
	 * 
	 * The same instance is shared meaning that all changes done here will also
	 * impact the other builder.
	 * 
	 * <p>
	 * If a previous builder was defined (by calling {@link #mimetype()} for
	 * example), the new builder will override it.
	 * 
	 * @param builder
	 *            the builder to inherit
	 * @return this instance for fluent chaining
	 */
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
	public ContentTranslator build() {
		MimeTypeProvider mimetypeProvider = buildMimetypeProvider();
		if (mimetypeProvider == null) {
			LOG.info("Images won't be inlined because no mimetype detector is configured");
			return null;
		}
		LOG.info("Images will be inlined");
		return buildContext.register(new InlineImageTranslator(buildInliner(), buildResolver(), mimetypeProvider, buildRelativePathProvider()));
	}

	private MimeTypeProvider buildMimetypeProvider() {
		if (mimetypeBuilder == null) {
			return null;
		}
		return mimetypeBuilder.build();
	}

	private ImageInliner buildInliner() {
		EveryImageInliner inliner = buildContext.register(new EveryImageInliner());
		if (attachBuilder != null) {
			inliner.addInliner(attachBuilder.build());
		}
		if (base64Builder != null) {
			inliner.addInliner(base64Builder.build());
		}
		return inliner;
	}

	private ResourceResolver buildResolver() {
		List<ResourceResolver> resolvers = resourceResolutionBuilderHelper.buildResolvers();
		return buildContext.register(new FirstSupportingResourceResolver(resolvers));
	}

	private RelativePathResolver buildRelativePathProvider() {
		return buildContext.register(new LookupAwareRelativePathResolver(resourceResolutionBuilderHelper.getAllLookups()));
	}
}
