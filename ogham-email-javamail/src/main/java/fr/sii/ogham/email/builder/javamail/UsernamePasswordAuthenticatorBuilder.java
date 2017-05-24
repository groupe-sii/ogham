package fr.sii.ogham.email.builder.javamail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Authenticator;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
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
 * If {@link #updatable(boolean)} is set to true, it means that properties are
 * not evaluated when calling {@link #build()}. Instead, the property keys are
 * kept for later evaluation. The evaluation will then be done each time an
 * authentication to the mail server is started.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class UsernamePasswordAuthenticatorBuilder extends AbstractParent<JavaMailBuilder> implements Builder<Authenticator> {
	private List<String> usernames;
	private List<String> passwords;
	private boolean updatable;

	/**
	 * Initializes the parent instance for fluent chaining (when method
	 * {@link #and()} is called).
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public UsernamePasswordAuthenticatorBuilder(JavaMailBuilder parent) {
		super(parent);
		usernames = new ArrayList<>();
		passwords = new ArrayList<>();
	}

	/**
	 * Set the username to use for the authentication.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .username("foo");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .username("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param username
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public UsernamePasswordAuthenticatorBuilder username(String... username) {
		for (String u : username) {
			if (u != null && !u.isEmpty()) {
				usernames.add(u);
			}
		}
		return this;
	}

	/**
	 * Set the password to use for the authentication.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .password("foo");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .password("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param password
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public UsernamePasswordAuthenticatorBuilder password(String... password) {
		for (String p : password) {
			if (p != null && !p.isEmpty()) {
				passwords.add(p);
			}
		}
		return this;
	}

	/**
	 * If set to true, it means that properties are not evaluated when calling
	 * {@link #build()}. Instead, the property keys are kept for later
	 * evaluation. The evaluation will then be done each time an authentication
	 * to the mail server is started.
	 * 
	 * @param updatable
	 *            true to evaluate properties each time an authentication is
	 *            started
	 * @return this instance for fluent chaining
	 */
	public UsernamePasswordAuthenticatorBuilder updatable(boolean updatable) {
		this.updatable = updatable;
		return this;
	}

	@Override
	public Authenticator build() throws BuildException {
		PropertyResolver propertyResolver = parent.environment().build();
		if (updatable) {
			if (!usernames.isEmpty() && !passwords.isEmpty()) {
				return new UpdatableUsernamePasswordAuthenticator(propertyResolver, usernames, passwords);
			}
			return null;
		}
		String username = BuilderUtils.evaluate(this.usernames, propertyResolver, String.class);
		String password = BuilderUtils.evaluate(this.passwords, propertyResolver, String.class);
		if (username != null && password != null) {
			return new UsernamePasswordAuthenticator(username, password);
		}
		return null;
	}
}
