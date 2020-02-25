package fr.sii.ogham.email.builder.javamail;

import javax.mail.Authenticator;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configurer.Configurer;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.email.sender.impl.javamail.UpdatableUsernamePasswordAuthenticator;
import fr.sii.ogham.email.sender.impl.javamail.UsernamePasswordAuthenticator;

/**
 * Configures authentication mechanism based on username/password.
 * 
 * <p>
 * You can define direct values for username and password:
 * 
 * <pre>
 * .username("foo")
 * .password("bar")
 * </pre>
 * 
 * Or you can specify one or several property keys:
 * 
 * <pre>
 * .username("${ogham.email.javamail.authenticator.username}")
 * .password("${ogham.email.javamail.authenticator.password}")
 * </pre>
 * 
 * The evaluation of the properties will be evaluated when {@link #build()} is
 * called (by default).
 * 
 * <p>
 * If {@link #updatable(Boolean)} is set to true, it means that properties are
 * not evaluated when calling {@link #build()}. Instead, the property keys are
 * kept for later evaluation. The evaluation will then be done each time an
 * authentication to the mail server is started.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class UsernamePasswordAuthenticatorBuilder extends AbstractParent<JavaMailBuilder> implements Builder<Authenticator> {
	private final ConfigurationValueBuilderHelper<UsernamePasswordAuthenticatorBuilder, String> usernameValueBuilder;
	private final ConfigurationValueBuilderHelper<UsernamePasswordAuthenticatorBuilder, String> passwordValueBuilder;
	private final ConfigurationValueBuilderHelper<UsernamePasswordAuthenticatorBuilder, Boolean> updatableValueBuilder;

	/**
	 * Initializes the parent instance for fluent chaining (when method
	 * {@link #and()} is called).
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public UsernamePasswordAuthenticatorBuilder(JavaMailBuilder parent) {
		super(parent);
		usernameValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		passwordValueBuilder = new ConfigurationValueBuilderHelper<>(this, String.class);
		updatableValueBuilder = new ConfigurationValueBuilderHelper<>(this, Boolean.class);
	}
	
	
	/**
	 * Set the username to use for the authentication.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #username()}.
	 * 
	 * <pre>
	 * .username("my-username")
	 * .username()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-username")
	 * </pre>
	 * 
	 * <pre>
	 * .username("my-username")
	 * .username()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-username")
	 * </pre>
	 * 
	 * In both cases, {@code username("my-username")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param username
	 *            the username
	 * @return this instance for fluent chaining
	 */
	public UsernamePasswordAuthenticatorBuilder username(String username) {
		usernameValueBuilder.setValue(username);
		return this;
	}

	/**
	 * Set the username to use for the authentication.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .username()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-username")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #username(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .username("my-username")
	 * .username()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-username")
	 * </pre>
	 * 
	 * The value {@code "my-username"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<UsernamePasswordAuthenticatorBuilder, String> username() {
		return usernameValueBuilder;
	}

	
	/**
	 * Set the password to use for the authentication.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #password()}.
	 * 
	 * <pre>
	 * .password("my-password")
	 * .password()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-password")
	 * </pre>
	 * 
	 * <pre>
	 * .password("my-password")
	 * .password()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-password")
	 * </pre>
	 * 
	 * In both cases, {@code password("my-password")} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param password
	 *            the passowrd
	 * @return this instance for fluent chaining
	 */
	public UsernamePasswordAuthenticatorBuilder password(String password) {
		passwordValueBuilder.setValue(password);
		return this;
	}

	/**
	 * Set the password to use for the authentication.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .password()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-password")
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #password(String)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .password("my-password")
	 * .password()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue("default-password")
	 * </pre>
	 * 
	 * The value {@code "my-password"} is used regardless of the value of the properties
	 * and default value.
	 * 
	 * <p>
	 * See {@link ConfigurationValueBuilder} for more information.
	 * 
	 * 
	 * @return the builder to configure property keys/default value
	 */
	public ConfigurationValueBuilder<UsernamePasswordAuthenticatorBuilder, String> password() {
		return passwordValueBuilder;
	}
	
	
	/**
	 * If set to true, it means that properties are not evaluated when calling
	 * {@link #build()}. Instead, the property keys are kept for later
	 * evaluation. The evaluation will then be done each time an authentication
	 * to the mail server is started.
	 * 
	 * <p>
	 * The value set using this method takes precedence over any property and
	 * default value configured using {@link #updatable()}.
	 * 
	 * <pre>
	 * .updatable(true)
	 * .updatable()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <pre>
	 * .updatable(true)
	 * .updatable()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * In both cases, {@code updatable(true)} is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If {@code null} value is set, it is like not setting a value at all. The
	 * property/default value configuration is applied.
	 * 
	 * @param updatable
	 *            true to evaluate expression when connecting to server
	 * @return this instance for fluent chaining
	 */
	public UsernamePasswordAuthenticatorBuilder updatable(Boolean updatable) {
		updatableValueBuilder.setValue(updatable);
		return this;
	}

	/**
	 * If set to true, it means that properties are not evaluated when calling
	 * {@link #build()}. Instead, the property keys are kept for later
	 * evaluation. The evaluation will then be done each time an authentication
	 * to the mail server is started.
	 * 
	 * <p>
	 * This method is mainly used by {@link Configurer}s to register some property keys and/or a default value.
	 * The aim is to let developer be able to externalize its configuration (using system properties, configuration file or anything else).
	 * If the developer doesn't configure any value for the registered properties, the default value is used (if set).
	 * 
	 * <pre>
	 * .updatable()
	 *   .properties("${custom.property.high-priority}", "${custom.property.low-priority}")
	 *   .defaultValue(false)
	 * </pre>
	 * 
	 * <p>
	 * Non-null value set using {@link #updatable(Boolean)} takes
	 * precedence over property values and default value.
	 * 
	 * <pre>
	 * .updatable(true)
	 * .updatable()
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
	public ConfigurationValueBuilder<UsernamePasswordAuthenticatorBuilder, Boolean> updatable() {
		return updatableValueBuilder;
	}

	@Override
	public Authenticator build() {
		PropertyResolver propertyResolver = parent.environment().build();
		boolean isUpdatable = updatableValueBuilder.getValue(propertyResolver, false);
		if (isUpdatable) {
			if (usernameValueBuilder.hasValueOrProperties() && passwordValueBuilder.hasValueOrProperties()) {
				return new UpdatableUsernamePasswordAuthenticator(propertyResolver, usernameValueBuilder, passwordValueBuilder);
			}
			return null;
		}
		String u = usernameValueBuilder.getValue(propertyResolver);
		String p = passwordValueBuilder.getValue(propertyResolver);
		if (u != null && p != null) {
			return new UsernamePasswordAuthenticator(u, p);
		}
		return null;
	}
}
