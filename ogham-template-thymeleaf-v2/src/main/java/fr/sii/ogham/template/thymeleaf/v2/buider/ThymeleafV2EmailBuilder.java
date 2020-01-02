package fr.sii.ogham.template.thymeleaf.v2.buider;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.template.detector.TemplateEngineDetector;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.template.thymeleaf.common.TemplateResolverOptions;
import fr.sii.ogham.template.thymeleaf.common.adapter.FileResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.FirstSupportingResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.adapter.TemplateResolverAdapter;
import fr.sii.ogham.template.thymeleaf.common.buider.AbstractThymeleafMultiContentBuilder;
import fr.sii.ogham.template.thymeleaf.v2.ThymeLeafV2FirstSupportingTemplateResolver;
import fr.sii.ogham.template.thymeleaf.v2.ThymeleafV2TemplateDetector;
import fr.sii.ogham.template.thymeleaf.v2.adapter.FixClassPathResolverAdapter;
import fr.sii.ogham.template.thymeleaf.v2.adapter.StringResolverAdapter;

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
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 */
	public ThymeleafV2EmailBuilder(EmailBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(ThymeleafV2EmailBuilder.class, parent, environmentBuilder);
	}


	@Override
	protected ITemplateResolver buildTemplateResolver(TemplateEngine builtEngine) {
		return new ThymeLeafV2FirstSupportingTemplateResolver(buildResolver(), buildAdapters());
	}

	@Override
	protected TemplateEngineDetector createTemplateDetector() {
		return new ThymeleafV2TemplateDetector(buildResolver());
	}


	@Override
	protected ThymeleafV2EngineConfigBuilder<ThymeleafV2EmailBuilder> getThymeleafEngineConfigBuilder() {
		return new ThymeleafV2EngineConfigBuilder<>(myself);
	}

	@Override
	protected FirstSupportingResolverAdapter buildAdapters() {
		FirstSupportingResolverAdapter adapter = new FirstSupportingResolverAdapter();
		for (TemplateResolverAdapter custom : customAdapters) {
			adapter.addAdapter(custom);
		}
		adapter.addAdapter(new FixClassPathResolverAdapter());
		adapter.addAdapter(new FileResolverAdapter());
		adapter.addAdapter(new StringResolverAdapter());
		adapter.setOptions(new TemplateResolverOptions());
		return adapter;
	}
}
