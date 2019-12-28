package fr.sii.ogham.template.freemarker.builder;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import freemarker.core.Configurable;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
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
	private final EnvironmentBuilder<?> environmentBuilder;
	private final ConfigurationValueBuilderHelper<FreemarkerConfigurationBuilder<P>, String> defaultEncodingValueBuilder;
	private final ConfigurationValueBuilderHelper<FreemarkerConfigurationBuilder<P>, Boolean> enableStaticMethodAccessValueBuilder;
	private final ConfigurationValueBuilderHelper<FreemarkerConfigurationBuilder<P>, String> staticMethodAccessVariableNameValueBuilder;
	private final Map<String, Object> sharedVariables;
	private Configuration base;
	private Version version;
	private TemplateExceptionHandler templateExceptionHandler;
	private TemplateHashModelEx variablesHash;
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
		this.environmentBuilder = environmentBuilder;
		defaultEncodingValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		enableStaticMethodAccessValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class);
		staticMethodAccessVariableNameValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		sharedVariables = new HashMap<>();
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
	 * * Sets the charset used for decoding byte sequences to character
	 * sequences when reading template files in a locale for which no explicit
	 * encoding was specified via
	 * {@link Configuration#setEncoding(Locale, String)}. Note that by default
	 * there is no locale specified for any locale, so the default encoding is
	 * always in effect.
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
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #defaultEncoding()}.
	 * 
	 * <pre>
	 * .defaultEncoding("UTF-16")
	 * .defaultEncoding()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("UTF-8")
	 * </pre>
	 * 
	 * <pre>
	 * .defaultEncoding("UTF-16")
	 * .defaultEncoding()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("UTF-8")
	 * </pre>
	 * 
	 * In both cases, {@code defaultEncoding("UTF-16")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param charsetName
	 *            the name of the charset
	 * @return this instance for fluent chaining
	 */
	public FreemarkerConfigurationBuilder<P> defaultEncoding(String charsetName) {
		defaultEncodingValueBuilder.setValue(charsetName);
		return this;
	}

	/**
	 * * Sets the charset used for decoding byte sequences to character
	 * sequences when reading template files in a locale for which no explicit
	 * encoding was specified via
	 * {@link Configuration#setEncoding(Locale, String)}. Note that by default
	 * there is no locale specified for any locale, so the default encoding is
	 * always in effect.
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
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .defaultEncoding()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("UTF-8")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #defaultEncoding(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .defaultEncoding("UTF-16")
	 * .defaultEncoding()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("UTF-8")
	 * </pre>
	 * 
	 * The value {@code "UTF-16"} is used regardless of the value of the
	 * properties and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<FreemarkerConfigurationBuilder<P>, String> defaultEncoding() {
		return defaultEncodingValueBuilder;
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
	 *
	 * @see #addSharedVariable(String,TemplateModel)
	 * @see #addSharedVariables(TemplateHashModelEx)
	 * @return this instance for fluent chaining
	 */
	public FreemarkerConfigurationBuilder<P> addSharedVariable(String name, Object value) {
		this.sharedVariables.put(name, value);
		return this;
	}

	/**
	 * Enable calls to static methods from templates:
	 * 
	 * <pre>
	 * ${statics['foo.bar.StringUtils'].capitalize(name)}
	 * </pre>
	 * 
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #enableStaticMethodAccess()}.
	 * 
	 * <pre>
	 * .enableStaticMethodAccess(true)
	 * .enableStaticMethodAccess()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <pre>
	 * .enableStaticMethodAccess(true)
	 * .enableStaticMethodAccess()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * In both cases, {@code enableStaticMethodAccess(true)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param enable
	 *            enable or disable static method access
	 * @return this instance for fluent chaining
	 */
	public FreemarkerConfigurationBuilder<P> enableStaticMethodAccess(Boolean enable) {
		enableStaticMethodAccessValueBuilder.setValue(enable);
		return this;
	}

	/**
	 * Enable calls to static methods from templates:
	 * 
	 * <pre>
	 * ${statics['foo.bar.StringUtils'].capitalize(name)}
	 * </pre>
	 * 
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some
	 * property keys and/or a default value. The aim is to let developer be able
	 * to externalize its configuration (using system properties, configuration
	 * file or anything else). If the developer doesn't configure any value for
	 * the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .enableStaticMethodAccess()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #enableStaticMethodAccess(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .enableStaticMethodAccess(true)
	 * .enableStaticMethodAccess()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * The value {@code true} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<FreemarkerConfigurationBuilder<P>, Boolean> enableStaticMethodAccess() {
		return enableStaticMethodAccessValueBuilder;
	}

	/**
	 * Change the name of the variable used to access static methods.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #staticMethodAccessVariableName()}.
	 * 
	 * <pre>
	 * .staticMethodAccessVariableName("myStatics")
	 * .staticMethodAccessVariableName()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("statics")
	 * </pre>
	 * 
	 * <pre>
	 * .staticMethodAccessVariableName("myStatics")
	 * .staticMethodAccessVariableName()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("statics")
	 * </pre>
	 * 
	 * In both cases, {@code staticMethodAccessVariableName("myStatics")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param variableName
	 *            the name of the variable used to access static methods from templates
	 * @return this instance for fluent chaining
	 */
	public FreemarkerConfigurationBuilder<P> staticMethodAccessVariableName(String variableName) {
		staticMethodAccessVariableNameValueBuilder.setValue(variableName);
		return this;
	}
	
	/**
	 * Change the name of the variable used to access static methods.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .staticMethodAccessVariableName()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("statics")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #staticMethodAccessVariableName(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .staticMethodAccessVariableName("myStatics")
	 * .staticMethodAccessVariableName()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("statics")
	 * </pre>
	 * 
	 * The value {@code "myStatics"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<FreemarkerConfigurationBuilder<P>, String> staticMethodAccessVariableName() {
		return staticMethodAccessVariableNameValueBuilder;
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
		String defaultEncoding = defaultEncodingValueBuilder.getValue(propertyResolver);
		if (defaultEncoding != null) {
			configuration.setDefaultEncoding(defaultEncoding);
		}
		if (templateExceptionHandler != null) {
			configuration.setTemplateExceptionHandler(templateExceptionHandler);
		}
		buildSharedVariables(configuration);
		buildStaticMethodAccess(configuration, propertyResolver);
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

	private void buildStaticMethodAccess(Configuration configuration, PropertyResolver propertyResolver) {
		boolean enableStaticMethods = enableStaticMethodAccessValueBuilder.getValue(propertyResolver, false);
		if (enableStaticMethods) {
			String staticsVariableName = staticMethodAccessVariableNameValueBuilder.getValue(propertyResolver);
			configuration.setSharedVariable(staticsVariableName, getBeansWrapper(configuration).getStaticModels());
		}
	}

	private static BeansWrapper getBeansWrapper(Configuration configuration) {
		ObjectWrapper objectWrapper = configuration.getObjectWrapper();
		if (objectWrapper instanceof BeansWrapper) {
			return (BeansWrapper) objectWrapper;
		}
		return new BeansWrapperBuilder(configuration.getIncompatibleImprovements()).build();
	}

}
