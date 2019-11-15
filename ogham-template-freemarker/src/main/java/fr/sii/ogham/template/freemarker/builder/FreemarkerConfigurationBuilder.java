package fr.sii.ogham.template.freemarker.builder;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
import freemarker.core.Configurable;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
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
	private Configuration base;
	private Version version;
	private List<String> defaultEncodings;
	private TemplateExceptionHandler templateExceptionHandler;
	private EnvironmentBuilder<?> environmentBuilder;
	private TemplateHashModelEx variablesHash;
	private Map<String, Object> sharedVariables;
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
		sharedVariables = new HashMap<>();
		this.environmentBuilder = environmentBuilder;
	}

	/**
	 * Sets the base configuration that will be configured.
	 * 
	 * If none is provided, a new configuration instance is created.
	 * 
	 * @param base
	 *            the configuration to use and to configure
	 * @return this instance for fluent chaining
	 */
	public FreemarkerConfigurationBuilder<P> base(Configuration base) {
		this.base = base;
		return this;
	}

	/**
	 * Sets which of the non-backward-compatible bugfixes/improvements should be
	 * enabled.
	 * 
	 * See {@link Configuration#Configuration(Version)} for more information
	 * about version and incompatible improvements.
	 * 
	 * @see Configuration#Configuration(Version)
	 * @param version
	 *            the non-backward-compatible bugfixes/improvements should be
	 *            enabled
	 * @return this instance for fluent chaining
	 */
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
	 * with {@code <#ftl encoding="...">}
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

	/**
	 * Adds a shared variable to the configuration. Shared sharedVariables are
	 * sharedVariables that are visible as top-level sharedVariables for all
	 * templates which use this configuration, if the data model does not
	 * contain a variable with the same name.
	 *
	 * <p>
	 * Never use <code>TemplateModel</code> implementation that is not
	 * thread-safe for shared sharedVariables, if the configuration is used by
	 * multiple threads! It is the typical situation for Servlet based Web
	 * sites.
	 * 
	 * <p>
	 * This method is <b>not</b> thread safe; use it with the same restrictions
	 * as those that modify setting values.
	 *
	 * @param name
	 *            the name used to access the data object from your template. If
	 *            a shared variable with this name already exists, it will
	 *            replace that.
	 * @param tm
	 *            the data object value directly available as a
	 *            {@link TemplateModel}
	 * 
	 * @see #addSharedVariables
	 * @see #addSharedVariable(String,Object)
	 * @return this instance for fluent chaining
	 */
	public FreemarkerConfigurationBuilder<P> addSharedVariable(String name, TemplateModel tm) {
		this.sharedVariables.put(name, tm);
		return this;
	}

	/**
	 * Adds shared variable to the configuration; It uses
	 * {@link Configurable#getObjectWrapper()} to wrap the {@code value}, so
	 * it's important that the object wrapper is set before this.
	 * 
	 * <p>
	 * This method is <b>not</b> thread safe; use it with the same restrictions
	 * as those that modify setting values.
	 * 
	 * <p>
	 * The added value should be thread safe, if you are running templates from
	 * multiple threads with this configuration.
	 *
	 * @param name
	 *            the name used to access the data object from your template. If
	 *            a shared variable with this name already exists, it will
	 *            replace that.
	 * @param value
	 *            the data object value
	 * @throws TemplateModelException
	 *             If some of the variables couldn't be wrapped via
	 *             {@link Configuration#getObjectWrapper()}.
	 *
	 * @see #addSharedVariable(String,TemplateModel)
	 * @see #addSharedVariables(TemplateHashModelEx)
	 * @return this instance for fluent chaining
	 */
	public FreemarkerConfigurationBuilder<P> addSharedVariable(String name, Object value) throws TemplateModelException {
		this.sharedVariables.put(name, value);
		return this;
	}

	/**
	 * Adds all object in the hash as shared variable to the configuration; it's
	 * like doing several {@link #addSharedVariable(String, Object)} calls, one
	 * for each hash entry. It doesn't remove the already added shared variable
	 * before doing this.
	 *
	 * <p>
	 * Never use <code>TemplateModel</code> implementation that is not
	 * thread-safe for shared shared variable values, if the configuration is
	 * used by multiple threads! It is the typical situation for Servlet based
	 * Web sites.
	 *
	 * <p>
	 * This method is <b>not</b> thread safe; use it with the same restrictions
	 * as those that modify setting values.
	 *
	 * @param variables
	 *            a hash model whose objects will be copied to the configuration
	 *            with same names as they are given in the hash. If a shared
	 *            variable with these names already exist, it will be replaced
	 *            with those from the map.
	 * @return this instance for fluent chaining
	 */
	public FreemarkerConfigurationBuilder<P> addSharedVariables(TemplateHashModelEx variables) {
		this.variablesHash = variables;
		return this;
	}

	@Override
	public Configuration build() {
		Configuration configuration = getConfiguration();
		PropertyResolver propertyResolver = environmentBuilder.build();
		String defaultEncoding = BuilderUtils.evaluate(defaultEncodings, propertyResolver, String.class);
		if (defaultEncoding != null) {
			configuration.setDefaultEncoding(defaultEncoding);
		}
		if (templateExceptionHandler != null) {
			configuration.setTemplateExceptionHandler(templateExceptionHandler);
		}
		buildSharedVariables(configuration);
		return configuration;
	}

	private Configuration getConfiguration() {
		if (base != null) {
			if (version != null) {
				base.setIncompatibleImprovements(version);
			}
			return base;
		}
		return version == null ? new Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS) : new Configuration(version);
	}

	private void buildSharedVariables(Configuration configuration) {
		try {
			if (variablesHash != null) {
				configuration.setAllSharedVariables(variablesHash);
			}
			for (Entry<String, Object> entry : sharedVariables.entrySet()) {
				configuration.setSharedVariable(entry.getKey(), entry.getValue());
			}
		} catch (TemplateModelException e) {
			throw new BuildException("Failed to configure FreeMarker shared variables", e);
		}
	}
}
