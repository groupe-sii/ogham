package fr.sii.ogham.email.builder.javamail;

import javax.mail.Authenticator;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.sender.impl.javamail.UpdatableUsernamePasswordAuthenticator;
import fr.sii.ogham.email.sender.impl.javamail.UsernamePasswordAuthenticator;

public class UsernamePasswordAuthenticatorBuilder extends AbstractParent<JavaMailBuilder> implements Builder<Authenticator> {
	private String username;
	private String password;
	private boolean updatable;
	
	public UsernamePasswordAuthenticatorBuilder(JavaMailBuilder parent) {
		super(parent);
	}

	public UsernamePasswordAuthenticatorBuilder username(String username) {
		this.username = username;
		return this;
	}
	
	public UsernamePasswordAuthenticatorBuilder password(String password) {
		this.password = password;
		return this;
	}

	public UsernamePasswordAuthenticatorBuilder updatable(boolean updatable) {
		this.updatable = updatable;
		return this;
	}

	@Override
	public Authenticator build() throws BuildException {
		PropertyResolver propertyResolver = parent.environment().build();
		if(updatable) {
			if(username!=null && password!=null) {
				return new UpdatableUsernamePasswordAuthenticator(propertyResolver, username, password);
			}
			return null;
		}
		String username = BuilderUtils.evaluate(this.username, propertyResolver, String.class);
		String password = BuilderUtils.evaluate(this.password, propertyResolver, String.class);
		if(username!=null && password!=null) {
			return new UsernamePasswordAuthenticator(username, password);
		}
		return null;
	}
}
