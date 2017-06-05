package fr.sii.ogham.template.freemarker.builder;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.BuilderUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

/**
 * Fluent builder to configure Freemarker configuration object.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class FreemarkerConfigurationBuilder<P> extends AbstractParent<P> implements Builder<Configuration> {
	private Version version;
	private List<String> defaultEncodings;
	private TemplateExceptionHandler templateExceptionHandler;
	private EnvironmentBuilder<?> environmentBuilder;
	// TODO: handle all other options

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
	public FreemarkerConfigurationBuilder(P parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		defaultEncodings = new ArrayList<>();
		this.environmentBuilder = environmentBuilder;
	}

	public FreemarkerConfigurationBuilder<P> version(Version version) {
		this.version = version;
		return this;
	}

	/**
	 * Sets the charset used for decoding byte sequences to character sequences
	 * when reading template files in a locale for which no explicit encoding
	 * was specified via {@link Configuration#setEncoding(Locale, String)}. Note
	 * that by default there is no locale specified for any locale, so the
	 * default encoding is always in effect.
	 * 
	 * <p>
	 * Defaults to the default system encoding, which can change from one server
	 * to another, so <b>you should always set this setting</b>. If you don't
	 * know what charset your should chose, {@code "UTF-8"} is usually a good
	 * choice.
	 * 
	 * <p>
	 * Note that individual templates may specify their own charset by starting
	 * with <tt>&lt;#ftl encoding="..."&gt;</tt>
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .defaultEncoding("UTF-8");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .defaultEncoding("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param encodings
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public FreemarkerConfigurationBuilder<P> defaultEncoding(String... encodings) {
		this.defaultEncodings.addAll(Arrays.asList(encodings));
		return this;
	}

	/**
	 * Sets the exception handler used to handle exceptions occurring inside
	 * templates. The default is {@link TemplateExceptionHandler#DEBUG_HANDLER}.
	 * The recommended values are:
	 * 
	 * <ul>
	 * <li>In production systems:
	 * {@link TemplateExceptionHandler#RETHROW_HANDLER}
	 * <li>During development of HTML templates:
	 * {@link TemplateExceptionHandler#HTML_DEBUG_HANDLER}
	 * <li>During development of non-HTML templates:
	 * {@link TemplateExceptionHandler#DEBUG_HANDLER}
	 * </ul>
	 * 
	 * <p>
	 * All of these will let the exception propagate further, so that you can
	 * catch it around {@link Template#process(Object, Writer)} for example. The
	 * difference is in what they print on the output before they do that.
	 * 
	 * <p>
	 * Note that the {@link TemplateExceptionHandler} is not meant to be used
	 * for generating HTTP error pages. Neither is it meant to be used to roll
	 * back the printed output. These should be solved outside template
	 * processing when the exception raises from
	 * {@link Template#process(Object, Writer) Template.process}.
	 * {@link TemplateExceptionHandler} meant to be used if you want to include
	 * special content <em>in</em> the template output, or if you want to
	 * suppress certain exceptions.
	 * 
	 * @param exceptionHandler
	 *            the exception handler
	 * @return this instance for fluent chaining
	 */
	public FreemarkerConfigurationBuilder<P> templateExceptionHandler(TemplateExceptionHandler exceptionHandler) {
		this.templateExceptionHandler = exceptionHandler;
		return this;
	}

	@Override
	public Configuration build() {
		Configuration configuration = version == null ? new Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS) : new Configuration(version);
		PropertyResolver propertyResolver = environmentBuilder.build();
		String defaultEncoding = BuilderUtils.evaluate(defaultEncodings, propertyResolver, String.class);
		if (defaultEncoding != null) {
			configuration.setDefaultEncoding(defaultEncoding);
		}
		configuration.setTemplateExceptionHandler(templateExceptionHandler);
		return configuration;
	}
}
