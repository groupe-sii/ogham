package fr.sii.ogham.template.freemarker.builder;

import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.email.builder.EmailBuilder;
import freemarker.template.Configuration;

/**
 * Configures parsing of templates using Freemarker.
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
 * You can customize default Freemarker {@link Configuration}:
 * 
 * <pre>
 * <code>
 * .configuration()
 *   .version(Configuration.VERSION_2_3_25)
 *   .templateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER)
 * </code>
 * </pre>
 * 
 * Or you can use a particular Freemarker {@link Configuration}:
 * 
 * <pre>
 * <code>
 * .configuration(myConfiguration)
 * </code>
 * </pre>
 * 
 * Email protocol supports several contents (main and alternative). The main
 * content is often an HTML email to display a beautiful email to users. The
 * alternative content is often a textual fallback (when email client can't
 * display HTML version like mobile phones that tries to display a summary of
 * the email). You can configure which file extensions are supported by
 * Freemarker to automatically load variants (HTML: main, TEXT: alternative):
 * 
 * <pre>
 * <code>
 * .variant(EmailVariant.HTML, "html.ftl")
 * .variant(EmailVariant.TEXT, "txt.ftl")
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
 */
public class FreemarkerEmailBuilder extends AbstractFreemarkerMultiContentBuilder<FreemarkerEmailBuilder, EmailBuilder> {

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
	public FreemarkerEmailBuilder(EmailBuilder parent, BuildContext buildContext) {
		super(FreemarkerEmailBuilder.class, parent, buildContext);
	}

}
