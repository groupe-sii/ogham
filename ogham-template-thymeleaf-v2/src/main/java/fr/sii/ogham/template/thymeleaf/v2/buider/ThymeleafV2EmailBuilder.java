package fr.sii.ogham.template.thymeleaf.v2.buider;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.context.DefaultBuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.template.thymeleaf.common.adapter.FileResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.TemplateResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.buider.AbstractThymeleafMultiContentBuilder;
import fr.sii.ogham.template.thymeleaf.v2.ThymeLeafV2FirstSupportingTemplateResolver;
import fr.sii.ogham.template.thymeleaf.v2.ThymeleafV2TemplateDetector;
import fr.sii.ogham.template.thymeleaf.v2.adapter.FixClassPathResolverAdapter;
import fr.sii.ogham.template.thymeleaf.v2.adapter.StringResolverAdapter;
import fr.sii.ogham.template.thymeleaf.v2.adapter.ThymeleafV2TemplateOptionsApplier;

/**
 * Configures parsing of templates using Thymeleaf.
 * 
 * Specific resource resolution can be configured to use template prefix/suffix
 * paths:
 * 
 * <pre>
 * <code>
 * .classpath()
 *   .pathPrefix("email/")
 *   .pathSuffix(".html")
 *   .and()
 * .file()
 *   .pathPrefix("/data/myapplication/templates/email")
 *   .pathSuffix(".html")
 * </code>
 * </pre>
 * 
 * You can customize default Thymeleaf {@link TemplateEngine}:
 * 
 * <pre>
 * <code>
 * .engine()
 *   .addDialect("foo", myDialect)
 *   .addMessageResolver(myMessageResolver)
 * </code>
 * </pre>
 * 
 * Or you can use a particular Thymeleaf {@link TemplateEngine}:
 * 
 * <pre>
 * <code>
 * .engine(new MyTemplateEngine())
 * </code>
 * </pre>
 * 
 * 
 * Email protocol supports several contents (main and alternative). The main
 * content is often an HTML email to display a beautiful email to users. The
 * alternative content is often a textual fallback (when email client can't
 * display HTML version like mobile phones that tries to display a summary of
 * the email). You can configure which file extensions are supported by
 * Thymeleaf to automatically load variants (HTML: main, TEXT: alternative):
 * 
 * <pre>
 * <code>
 * .variant(EmailVariant.HTML, "html")
 * .variant(EmailVariant.TEXT, "txt")
 * </code>
 * </pre>
 * 
 * Thanks to that configuration, you can send an email without specifying the
 * extension:
 * 
 * <pre>
 * <code>
 * service.send(new Email()
 *   .content(new MultiTemplateContent("email/sample", new SampleBean("foo", 42)))
 *   .to("foo.bar@sii.fr"))
 * </code>
 * </pre>
 * 
 * Ogham will then be able to detect which files exist and choose the right
 * behavior:
 * <ul>
 * <li>If you provide an ".html" file (either in classpath or on filesytem), the
 * HTML template is used as main content</li>
 * <li>If you provide an ".txt" file (either in classpath or on filesytem), the
 * text template is used as main content</li>
 * <li>If you provide both files, the HTML template is used as main content and
 * text template as alternative</li>
 * </ul>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafV2EmailBuilder extends AbstractThymeleafMultiContentBuilder<ThymeleafV2EmailBuilder, EmailBuilder, ThymeleafV2EngineConfigBuilder<ThymeleafV2EmailBuilder>> {
	/**
	 * Default constructor when using Thymeleaf without all Ogham work.
	 * 
	 * <strong>WARNING: use is only if you know what you are doing !</strong>
	 */
	public ThymeleafV2EmailBuilder() {
		super(ThymeleafV2EmailBuilder.class, null, new DefaultBuildContext());
	}

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
	public ThymeleafV2EmailBuilder(EmailBuilder parent, BuildContext buildContext) {
		super(ThymeleafV2EmailBuilder.class, parent, buildContext);
	}

	@Override
	protected ITemplateResolver buildTemplateResolver(TemplateEngine builtEngine) {
		return buildContext.register(new ThymeLeafV2FirstSupportingTemplateResolver(buildResolver(), buildAdapters()));
	}

	@Override
	protected TemplateEngineDetector createTemplateDetector() {
		return buildContext.register(new ThymeleafV2TemplateDetector(buildResolver()));
	}

	@Override
	protected ThymeleafV2EngineConfigBuilder<ThymeleafV2EmailBuilder> getThymeleafEngineConfigBuilder() {
		return buildContext.register(new ThymeleafV2EngineConfigBuilder<>(myself, buildContext));
	}

	@Override
	protected FirstSupportingResolverAdapter buildAdapters() {
		FirstSupportingResolverAdapter adapter = buildContext.register(new FirstSupportingResolverAdapter());
		for (TemplateResolverAdapter custom : customAdapters) {
			adapter.addAdapter(custom);
		}
		ThymeleafV2TemplateOptionsApplier applier = buildContext.register(new ThymeleafV2TemplateOptionsApplier());
		adapter.addAdapter(buildContext.register(new FixClassPathResolverAdapter(applier)));
		adapter.addAdapter(buildContext.register(new FileResolverAdapter(applier)));
		adapter.addAdapter(buildContext.register(new StringResolverAdapter(applier)));
		adapter.setOptions(buildTemplateResolverOptions());
		return adapter;
	}
}
