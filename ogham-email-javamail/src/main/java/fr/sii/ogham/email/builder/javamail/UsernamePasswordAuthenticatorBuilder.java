package fr.sii.ogham.email.builder.javamail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Authenticator;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.email.sender.impl.javamail.UpdatableUsernamePasswordAuthenticator;
import fr.sii.ogham.email.sender.impl.javamail.UsernamePasswordAuthenticator;

public class UsernamePasswordAuthenticatorBuilder extends AbstractParent<JavaMailBuilder> implements Builder<Authenticator> {
	private List<String> usernames;
	private List<String> passwords;
	private boolean updatable;
	
	public UsernamePasswordAuthenticatorBuilder(JavaMailBuilder parent) {
		super(parent);
		usernames = new ArrayList<>();
		passwords = new ArrayList<>();
	}

	public UsernamePasswordAuthenticatorBuilder username(String... username) {
		for(String u : username) {
			if(u!=null && !u.isEmpty()) {
				usernames.add(u);
			}
		}
		return this;
	}
	
	public UsernamePasswordAuthenticatorBuilder password(String... password) {
		for(String p : password) {
			if(p!=null && !p.isEmpty()) {
				passwords.add(p);
			}
		}
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
			if(!usernames.isEmpty() && !passwords.isEmpty()) {
				return new UpdatableUsernamePasswordAuthenticator(propertyResolver, usernames, passwords);
			}
			return null;
		}
		String username = BuilderUtils.evaluate(this.usernames, propertyResolver, String.class);
		String password = BuilderUtils.evaluate(this.passwords, propertyResolver, String.class);
		if(username!=null && password!=null) {
			return new UsernamePasswordAuthenticator(username, password);
		}
		return null;
	}
}
