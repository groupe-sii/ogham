package fr.sii.notification.core.builder;

import fr.sii.notification.core.exception.builder.BuildException;

public interface Builder<T> {

	public T build() throws BuildException;

}